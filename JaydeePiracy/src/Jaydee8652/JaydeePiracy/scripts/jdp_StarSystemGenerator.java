package Jaydee8652.JaydeePiracy.scripts;

import java.awt.Color;
import java.awt.desktop.SystemEventListener;
import java.util.*;
import java.util.stream.Collectors;

import com.fs.starfarer.D.M;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.*;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.loading.specs.PlanetSpec;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.AddedEntity;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.EntityLocation;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.LocationType;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain.TileParams;
import com.fs.starfarer.api.impl.campaign.terrain.NebulaTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.PulsarBeamTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.StarCoronaTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.StarCoronaTerrainPlugin.CoronaParams;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import com.fs.starfarer.api.impl.campaign.procgen.themes.*;


public class jdp_StarSystemGenerator extends StarSystemGenerator {
	public static Logger log = Global.getLogger(jdp_retrogen.class);

	public static class CustomConstellationParams implements Cloneable {
		public String name = null;
		public String secondaryName = null;
		public StarAge age = null;
		public int minStars = 0;
		public int maxStars = 0;
		public int numStars = 0;
		public boolean forceNebula = false;
		public List<StarSystemGenerator.StarSystemType> systemTypes = new ArrayList<StarSystemGenerator.StarSystemType>();
		public List<String> starTypes = new ArrayList<String>();
		public Vector2f location = null;

		public CustomConstellationParams(StarAge age) {
			this.age = age;
		}

		@Override
		public StarSystemGenerator.CustomConstellationParams clone() {
			try {
				return (StarSystemGenerator.CustomConstellationParams) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}


	}

	public static final float MIN_STAR_DIST = 2000f;
	public static final float MAX_STAR_DIST = 2000f;

	public static final float TILT_MIN = -45f;
	public static final float TILT_MAX = 45f;
	public static final float PITCH_MIN = -15f;
	public static final float PITCH_MAX = 45f;

	public static final float MAX_ORBIT_RADIUS = 20000;
	public static final float FAR_MAX_ORBIT_RADIUS = 5000;

	public static final float LAGRANGE_OFFSET = 60f;

	public static final float BASE_INCR = 800f;
	public static final float BASE_INCR_MOON = 200f;

	public static final float STARTING_RADIUS_STAR_BASE = 750f;
	public static final float STARTING_RADIUS_STAR_RANGE = 500f;

	public static final float STARTING_RADIUS_MOON_BASE = 300f;
	public static final float STARTING_RADIUS_MOON_RANGE = 100f;

	public static final float MOON_RADIUS_MAX_FRACTION_OF_PARENT = 0.33f;
	public static final float MOON_RADIUS_MIN_FRACTION_OF_NORMAL = 0.2f;
	public static final float MOON_RADIUS_MAX_FRACTION_OF_NORMAL = 0.75f;
	public static final float MIN_MOON_RADIUS = 60f;

	public static final String TAG_FIRST_ORBIT_ONLY = "first_orbit_only";
	public static final String TAG_GIANT_MOON = "around_giant_at_any_offset";
	public static final String TAG_LAGRANGE_ONLY = "lagrange_only";
	public static final String TAG_NOT_IN_NEBULA = "not_in_nebula";
	public static final String TAG_REQUIRES_NEBULA = "requires_nebula";

	public static final String TAG_NOT_NEBULA_UNLESS_MOON = "not_NEBULA_unless_moon";

	public static final String CAT_HAB5 = "cat_hab5";
	public static final String CAT_HAB4 = "cat_hab4";
	public static final String CAT_HAB3 = "cat_hab3";
	public static final String CAT_HAB2 = "cat_hab2";
	public static final String CAT_HAB1 = "cat_hab1";

	public static final String CAT_NOTHING = "cat_nothing";
	public static final String CAT_GIANT = "cat_giant";

	public static final String COL_LAGRANGE = "lagrange";
	public static final String COL_IN_ASTEROIDS = "in_asteroids";
	public static final String COL_IS_MOON = "is_moon";
	public static final String COL_BINARY = "binary";
	public static final String COL_TRINARY = "trinary";

	public static final String NEBULA_DEFAULT = "nebula";
	public static final String NEBULA_AMBER = "nebula_amber";
	public static final String NEBULA_BLUE = "nebula_blue";
	public static final String NEBULA_NONE = "no_nebula";


	public static Map<StarAge, String> nebulaTypes = new LinkedHashMap<StarAge, String>();
	public static Map<String, WeightedRandomPicker<String>> backgroundsByNebulaType = new LinkedHashMap<String, WeightedRandomPicker<String>>();


	public static List<TerrainGenPlugin> terrainPlugins = new ArrayList<TerrainGenPlugin>();

	public static void addTerrainGenPlugin(TerrainGenPlugin plugin) {
		terrainPlugins.add(0, plugin);
	}

	public static void removeTerrainGenPlugin(TerrainGenPlugin plugin) {
		terrainPlugins.remove(plugin);
	}

	public static Random random = new Random();

	static {
		terrainPlugins.add(new RingGenPlugin());
		terrainPlugins.add(new AsteroidBeltGenPlugin());
		terrainPlugins.add(new MagFieldGenPlugin());

		terrainPlugins.add(new NebulaSmallGenPlugin());
		terrainPlugins.add(new AsteroidFieldGenPlugin());

		terrainPlugins.add(new AccretionDiskGenPlugin());

		nebulaTypes.put(StarAge.YOUNG, NEBULA_BLUE);
		nebulaTypes.put(StarAge.AVERAGE, NEBULA_DEFAULT);
		nebulaTypes.put(StarAge.OLD, NEBULA_AMBER);

		nebulaTypes.put(StarAge.ANY, NEBULA_DEFAULT);

		updateBackgroundPickers();
	}

	public static void updateBackgroundPickers() {
		WeightedRandomPicker<String> picker;
		picker = new WeightedRandomPicker<String>(random);
		picker.add("graphics/backgrounds/background2.jpg", 10);
		picker.add("graphics/backgrounds/background4.jpg", 10);
		backgroundsByNebulaType.put(NEBULA_NONE, picker);

		picker = new WeightedRandomPicker<String>(random);
		picker.add("graphics/backgrounds/background5.jpg", 10);
		backgroundsByNebulaType.put(NEBULA_BLUE, picker);

		picker = new WeightedRandomPicker<String>(random);
		picker.add("graphics/backgrounds/background6.jpg", 10);
		backgroundsByNebulaType.put(NEBULA_AMBER, picker);

		picker = new WeightedRandomPicker<String>(random);
		picker.add("graphics/backgrounds/background1.jpg", 10);
		picker.add("graphics/backgrounds/background2.jpg", 10);
		backgroundsByNebulaType.put(NEBULA_DEFAULT, picker);
	}

	public static boolean DEBUG = Global.getSettings().isDevMode();

	public static TerrainGenPlugin pickTerrainGenPlugin(TerrainGenDataSpec terrainData, StarSystemGenerator.GenContext context) {
		for (TerrainGenPlugin plugin : terrainPlugins) {
			if (plugin.wantsToHandle(terrainData, context)) return plugin;
		}
		return null;
	}


	public static class GeneratedPlanet {
		public SectorEntityToken parent;
		public PlanetAPI planet;
		public float orbitDays;
		public float orbitRadius;
		public float orbitAngle;
		public boolean isMoon;

		public GeneratedPlanet(SectorEntityToken parent, PlanetAPI planet, boolean isMoon, float orbitDays, float orbitRadius, float orbitAngle) {
			this.parent = parent;
			this.planet = planet;
			this.isMoon = isMoon;
			this.orbitDays = orbitDays;
			this.orbitRadius = orbitRadius;
			this.orbitAngle = orbitAngle;
		}
	}

	public enum LagrangePointType {
		//		L1,
//		L2,
//		L3,
		L4,
		L5,
	}


	public static class LagrangePoint {
		public StarSystemGenerator.GeneratedPlanet parent;
		public StarSystemGenerator.LagrangePointType type;

		public LagrangePoint(StarSystemGenerator.GeneratedPlanet parent, StarSystemGenerator.LagrangePointType type) {
			this.parent = parent;
			this.type = type;
		}
	}


	public static class GenResult {
		public float orbitalWidth;
		public boolean onlyIncrementByWidth = false;
		public List<SectorEntityToken> entities = new ArrayList<SectorEntityToken>();
		public StarSystemGenerator.GenContext context;

		public GenResult() {

		}
	}

	public static class GenContext {
		public StarSystemGenerator gen;
		public List<StarSystemGenerator.GeneratedPlanet> generatedPlanets = new ArrayList<StarSystemGenerator.GeneratedPlanet>();

		public Set<String> excludeCategories = new LinkedHashSet<String>();

		//public NamePick parentNamePick = null;
		public StarSystemAPI system;
		public SectorEntityToken center;
		public StarGenDataSpec starData;
		public PlanetAPI parent;
		public int orbitIndex = -1;

		public int startingOrbitIndex = 0;
		public String age;

		public float currentRadius;
		public String parentCategory;
		public int parentOrbitIndex;
		public float parentRadiusOverride = -1;

		public StarSystemGenerator.GeneratedPlanet lagrangeParent = null;
		public StarSystemGenerator.LagrangePointType lagrangePointType = null;

		public List<String> multipliers = new ArrayList<String>();
		public float maxOrbitRadius;

		public Map<Object, Object> customData = new LinkedHashMap<Object, Object>();

		public GenContext(StarSystemGenerator gen, StarSystemAPI system, SectorEntityToken center,
						  StarGenDataSpec starData, PlanetAPI parent,
						  //NamePick parentNamePick,
						  int orbitIndex,
						  String age, float currentRadius, float maxOrbitRadius, String parentCategory, int parentOrbitIndex) {
			super();
			//this.parentNamePick = parentNamePick;
			this.maxOrbitRadius = maxOrbitRadius;
			this.gen = gen;
			this.system = system;
			this.center = center;
			this.starData = starData;
			this.parent = parent;
			this.startingOrbitIndex = orbitIndex;
			this.orbitIndex = 0;
			this.age = age;
			this.currentRadius = currentRadius;
			this.parentCategory = parentCategory;
			this.parentOrbitIndex = parentOrbitIndex;
		}

	}

	protected StarAge constellationAge;

	protected StarSystemGenerator.StarSystemType systemType = StarSystemGenerator.StarSystemType.SINGLE;

	protected StarAge starAge;
	protected SectorAPI sector;
	protected StarSystemAPI system;
	protected LocationAPI hyper;

	protected PlanetAPI star;
	protected PlanetAPI secondary;
	protected PlanetAPI tertiary;

	protected SectorEntityToken systemCenter;
	protected float centerRadius = 0f;
	protected AgeGenDataSpec constellationAgeData;
	protected AgeGenDataSpec starAgeData;
	protected StarGenDataSpec starData;
	protected String nebulaType;
	protected String backgroundName;

	protected Map<SectorEntityToken, PlanetAPI> lagrangeParentMap = new LinkedHashMap<SectorEntityToken, PlanetAPI>();
	protected Map<SectorEntityToken, List<SectorEntityToken>> allNameableEntitiesAdded = new LinkedHashMap<SectorEntityToken, List<SectorEntityToken>>();
	protected StarSystemGenerator.CustomConstellationParams params;


	public jdp_StarSystemGenerator(StarSystemGenerator.CustomConstellationParams params) {
		super(params);
		this.params = params;
		this.constellationAge = params.age;

		if (this.constellationAge == StarAge.ANY) {
			WeightedRandomPicker<StarAge> picker = new WeightedRandomPicker<StarAge>(random);
			picker.add(StarAge.AVERAGE);
			picker.add(StarAge.OLD);
			picker.add(StarAge.YOUNG);
			this.constellationAge = picker.pick();
		}

		constellationAgeData = (AgeGenDataSpec) Global.getSettings().getSpec(AgeGenDataSpec.class, constellationAge.name(), true);
	}


	public void pickNebulaAndBackground() {
		boolean hasNebula = constellationAgeData.getProbNebula() > random.nextFloat();
		if (params != null && params.forceNebula) hasNebula = true;

		nebulaType = NEBULA_NONE;
		if (hasNebula) {
			nebulaType = nebulaTypes.get(constellationAge);
		}

		WeightedRandomPicker<String> bgPicker = backgroundsByNebulaType.get(nebulaType);
		backgroundName = bgPicker.pick();
	}

	public StarSystemAPI generateSystem(StarSystemType definedSystemType, String definedStarType) {
		Vector2f loc = new Vector2f(0, 0);

		pickNebulaAndBackground();

		systemType = definedSystemType;

		String id = null;
		String name = null;
		String randomName = null;

		if (definedSystemType == StarSystemType.NEBULA) {
			randomName = ProcgenUsedNames.pickName(NameGenData.TAG_NEBULA, null, null).nameWithRomanSuffixIfAny;
			id = randomName;
			name = randomName;
		} else {
			randomName = ProcgenUsedNames.pickName(NameGenData.TAG_STAR, null, null).nameWithRomanSuffixIfAny;
			id = randomName;
			name = randomName;
		}

		if (!initSystem(name, loc)) {
			cleanup();
			return null;
		}

		star = null;
		secondary = null;
		tertiary = null;
		systemCenter = null;


		if (!addStars(id, definedStarType)) {
			cleanup();
			return null;
		}

		updateAgeAfterPickingStar();

		float binaryPad = 1500f;

		float maxOrbitRadius = MAX_ORBIT_RADIUS;
		if (systemType == StarSystemGenerator.StarSystemType.BINARY_FAR ||
				systemType == StarSystemGenerator.StarSystemType.TRINARY_1CLOSE_1FAR ||
				systemType == StarSystemGenerator.StarSystemType.TRINARY_2FAR) {
			maxOrbitRadius -= FAR_MAX_ORBIT_RADIUS + binaryPad;
		}
		StarSystemGenerator.GenResult result = addPlanetsAndTerrain(MAX_ORBIT_RADIUS);
		float primaryOrbitalRadius = star.getRadius();
		if (result != null) {
			primaryOrbitalRadius = result.orbitalWidth * 0.5f;
		}

		// add far stars, if needed
		float orbitAngle = random.nextFloat() * 360f;
		float baseOrbitRadius = primaryOrbitalRadius + binaryPad;
		float orbitDays = baseOrbitRadius / (3f + random.nextFloat() * 2f);
		if (systemType == StarSystemGenerator.StarSystemType.BINARY_FAR && secondary != null) {
			addFarStar(secondary, orbitAngle, baseOrbitRadius, orbitDays);
		} else if (systemType == StarSystemGenerator.StarSystemType.TRINARY_1CLOSE_1FAR && tertiary != null) {
			addFarStar(tertiary, orbitAngle, baseOrbitRadius, orbitDays);
		} else if (systemType == StarSystemGenerator.StarSystemType.TRINARY_2FAR) {
			addFarStar(secondary, orbitAngle, baseOrbitRadius, orbitDays);
			addFarStar(tertiary, orbitAngle + 60f + 180f * random.nextFloat(), baseOrbitRadius, orbitDays);
		}


		if (systemType == StarSystemGenerator.StarSystemType.NEBULA) {
			star.setSkipForJumpPointAutoGen(true);
		}

		addJumpPoints(result, false);

		if (systemType == StarSystemGenerator.StarSystemType.NEBULA) {
			system.setHasSystemwideNebula(true);
			system.removeEntity(star);
			StarCoronaTerrainPlugin coronaPlugin = Misc.getCoronaFor(star);
			if (coronaPlugin != null) {
				system.removeEntity(coronaPlugin.getEntity());
			}
			system.setStar(null);
			system.initNonStarCenter();
			for (SectorEntityToken entity : system.getAllEntities()) {
				if (entity.getOrbitFocus() == star ||
						entity.getOrbitFocus() == system.getCenter()) {
					entity.setOrbit(null);
				}
			}
			system.getCenter().addTag(Tags.AMBIENT_LS);
		}

		if (systemType == StarSystemGenerator.StarSystemType.NEBULA) {
			system.setStar(star);
		}

		addStableLocations();
		addSystemwideNebula();
		system.addTag("$jdp_postgenSystem");

		if (placeInSector(system)) return system;
		log.warn("JDP_RETROGEN_EVENTS:  	A system failed to be placed");
		return system;
	}


	protected void addFarStar(PlanetAPI farStar, float orbitAngle, float baseOrbitRadius, float orbitPeriod) {
		float min = 0;
		float max = 2;
		int numOrbits = Math.round(getNormalRandom(min, max));
		StarSystemGenerator.GenResult resultFar = null;
		if (numOrbits > 0) {
			float currentRadius = farStar.getRadius() + STARTING_RADIUS_STAR_BASE + STARTING_RADIUS_STAR_RANGE * random.nextFloat();

			StarGenDataSpec farData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, farStar.getSpec().getPlanetType(), false);
			StarAge farAge = farData.getAge();
			if (farAge == StarAge.ANY) {
				farAge = constellationAge;
			}

			//GenContext context = new GenContext(this, system, star, starData,
			StarSystemGenerator.GenContext context = new StarSystemGenerator.GenContext(this, system, farStar, farData,
					null, 0, farAge.name(), currentRadius, FAR_MAX_ORBIT_RADIUS, null, -1);


			resultFar = addOrbitingEntities(context, numOrbits, false, true, false, true);
			resultFar.context = context;
		}

		if (resultFar != null) {
			baseOrbitRadius += resultFar.orbitalWidth * 0.5f;
		}

		SectorEntityToken center = star;
		if (systemType == StarSystemGenerator.StarSystemType.TRINARY_1CLOSE_1FAR) {
			center = systemCenter;
		}
		farStar.setCircularOrbit(center, orbitAngle, baseOrbitRadius, orbitPeriod);

		if (resultFar != null) {
			addJumpPoints(resultFar, true);
		}
	}


	protected StarSystemGenerator.StarSystemType pickSystemType(StarAge constellationAge) {

		if (params != null && !params.systemTypes.isEmpty()) {
			return params.systemTypes.remove(0);
		}


		WeightedRandomPicker<StarSystemGenerator.StarSystemType> picker = new WeightedRandomPicker<StarSystemGenerator.StarSystemType>(random);
		for (StarSystemGenerator.StarSystemType type : EnumSet.allOf(StarSystemGenerator.StarSystemType.class)) {
			if (type == StarSystemGenerator.StarSystemType.DEEP_SPACE || type == StarSystemGenerator.StarSystemType.DEEP_SPACE_GAS_GIANT)
				continue;

			Object test = Global.getSettings().getSpec(LocationGenDataSpec.class, type.name(), true);
			if (test == null) continue;
			LocationGenDataSpec data = (LocationGenDataSpec) test;

			boolean nebulaStatusOk = NEBULA_NONE.equals(nebulaType) || !data.hasTag(TAG_NOT_IN_NEBULA);
			nebulaStatusOk &= !NEBULA_NONE.equals(nebulaType) || !data.hasTag(TAG_REQUIRES_NEBULA);

			if (!nebulaStatusOk) continue;

			float freq = 0f;
			switch (constellationAge) {
				case AVERAGE:
					freq = data.getFreqAVERAGE();
					break;
				case OLD:
					freq = data.getFreqOLD();
					break;
				case YOUNG:
					freq = data.getFreqYOUNG();
					break;
			}
			picker.add(type, freq);
		}

		return picker.pick();

	}


	protected boolean addStars(String id, String definedStarType) {
		if (systemType == StarSystemGenerator.StarSystemType.BINARY_CLOSE ||
				systemType == StarSystemGenerator.StarSystemType.TRINARY_1CLOSE_1FAR ||
				systemType == StarSystemGenerator.StarSystemType.TRINARY_2CLOSE) {
			system.initNonStarCenter();
			systemCenter = system.getCenter();
		}

		PlanetSpecAPI starSpec = pickStar(constellationAge);

		if (definedStarType != null) {
			starSpec = (PlanetSpecAPI) Global.getSettings().getSpec(PlanetSpec.class, definedStarType, true);
		}

		if (starSpec == null) return false;

		starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, starSpec.getPlanetType(), false);
		float radius = getRadius(starData.getMinRadius(), starData.getMaxRadius());

		float corona = radius * (starData.getCoronaMult() + starData.getCoronaVar() * (random.nextFloat() - 0.5f));
		if (corona < starData.getCoronaMin()) corona = starData.getCoronaMin();

		star = system.initStar(id, // unique id for this star
				starSpec.getPlanetType(),  // id in planets.json
				radius,          // radius (in pixels at default zoom)
				corona,  // corona radius, from star edge
				starData.getSolarWind(),
				starData.getMinFlare() + (starData.getMaxFlare() - starData.getMinFlare()) * random.nextFloat(),
				starData.getCrLossMult()
		);

		if (systemType == StarSystemGenerator.StarSystemType.NEBULA) {
			star.addTag(Tags.AMBIENT_LS);
		}

		if (systemCenter == null) {
			systemCenter = star;
			centerRadius = star.getRadius();
		}


		// create and switch around stars so that the primary is always the largest
		if (systemType == StarSystemGenerator.StarSystemType.BINARY_CLOSE) {
			secondary = addRandomStar(id + "_b", system.getBaseName() + " B");
			if (secondary == null) return false;
			switchPrimaryAndSecondaryIfNeeded(true);
		} else if (systemType == StarSystemGenerator.StarSystemType.BINARY_FAR) {
			secondary = addRandomStar(id + "_b", system.getBaseName() + " B");
			if (secondary == null) return false;
			switchPrimaryAndSecondaryIfNeeded(true);

			centerRadius = star.getRadius();

			secondary.setLightColorOverrideIfStar(pickLightColorForStar(secondary));
		} else if (systemType == StarSystemGenerator.StarSystemType.TRINARY_2CLOSE ||
				systemType == StarSystemGenerator.StarSystemType.TRINARY_1CLOSE_1FAR ||
				systemType == StarSystemGenerator.StarSystemType.TRINARY_2FAR) {
			secondary = addRandomStar(id + "_b", system.getBaseName() + " B");
			if (secondary == null) return false;
			switchPrimaryAndSecondaryIfNeeded(true);

			tertiary = addRandomStar(id + "_c", system.getBaseName() + " C");
			if (tertiary == null) return false;
			switchPrimaryAndTertiaryIfNeeded(true);

			if (systemType == StarSystemGenerator.StarSystemType.TRINARY_1CLOSE_1FAR) {
				tertiary.setLightColorOverrideIfStar(pickLightColorForStar(tertiary));
			} else if (systemType == StarSystemGenerator.StarSystemType.TRINARY_2FAR) {
				secondary.setLightColorOverrideIfStar(pickLightColorForStar(secondary));
				tertiary.setLightColorOverrideIfStar(pickLightColorForStar(tertiary));
			}
		}

		// make close stars orbit common center
		if (systemType == StarSystemGenerator.StarSystemType.BINARY_CLOSE || systemType == StarSystemGenerator.StarSystemType.TRINARY_1CLOSE_1FAR) {
			float dist = STARTING_RADIUS_STAR_BASE + STARTING_RADIUS_STAR_RANGE * random.nextFloat();

			float r1 = star.getRadius();
			float r2 = secondary.getRadius();
			if (star.getSpec().getPlanetType().equals("black_hole")) r1 *= 5f;
			if (secondary.getSpec().getPlanetType().equals("black_hole")) r2 *= 5f;

			float totalRadius = r1 + r2;
			dist += totalRadius;

			float orbitPrimary = dist * r2 / totalRadius;
			float orbitSecondary = dist * r1 / totalRadius;

			centerRadius = Math.max(orbitPrimary + star.getRadius(), orbitSecondary + secondary.getRadius());

			float anglePrimary = random.nextFloat() * 360f;
			float orbitDays = dist / (30f + random.nextFloat() * 50f);

			star.setCircularOrbit(system.getCenter(), anglePrimary, orbitPrimary, orbitDays);
			secondary.setCircularOrbit(system.getCenter(), anglePrimary + 180f, orbitSecondary, orbitDays);

		} else if (systemType == StarSystemGenerator.StarSystemType.TRINARY_2CLOSE) {
			float dist = STARTING_RADIUS_STAR_BASE + STARTING_RADIUS_STAR_RANGE * random.nextFloat();
			dist += star.getRadius();

			float anglePrimary = random.nextFloat() * 360f;
			float orbitDays = dist / (20f + random.nextFloat() * 80f);

			// smaller dist for primary/secondary so that their gravity wells get generated first
			// and are closer to the center
			star.setCircularOrbit(system.getCenter(), anglePrimary, dist - 10, orbitDays);
			secondary.setCircularOrbit(system.getCenter(), anglePrimary + 120f, dist - 5, orbitDays);
			tertiary.setCircularOrbit(system.getCenter(), anglePrimary + 240f, dist, orbitDays);

			centerRadius = dist + star.getRadius();
		} else {
			star.getLocation().set(0, 0);
		}


		if (star != null) {
			starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, star.getSpec().getPlanetType(), false);
		}

		setDefaultLightColorBasedOnStars();


		if (star != null) {
			ArrayList<SectorEntityToken> list = new ArrayList<SectorEntityToken>();
			list.add(star);
			allNameableEntitiesAdded.put(star, list);
			system.setStar(star);
		}
		if (secondary != null) {
			ArrayList<SectorEntityToken> list = new ArrayList<SectorEntityToken>();
			list.add(secondary);
			allNameableEntitiesAdded.put(secondary, list);
			system.setSecondary(secondary);
		}
		if (tertiary != null) {
			ArrayList<SectorEntityToken> list = new ArrayList<SectorEntityToken>();
			list.add(tertiary);
			allNameableEntitiesAdded.put(tertiary, list);
			system.setTertiary(tertiary);
		}

		setBlackHoleIfBlackHole(star);
		setBlackHoleIfBlackHole(secondary);
		setBlackHoleIfBlackHole(tertiary);

		setPulsarIfNeutron(star);
		setPulsarIfNeutron(secondary);
		setPulsarIfNeutron(tertiary);

		return true;
	}

	protected void setBlackHoleIfBlackHole(PlanetAPI star) {
		if (star == null) return;

		if (star.getSpec().getPlanetType().equals("black_hole")) {
			StarCoronaTerrainPlugin coronaPlugin = Misc.getCoronaFor(star);
			if (coronaPlugin != null) {
				system.removeEntity(coronaPlugin.getEntity());
			}

			StarGenDataSpec starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, star.getSpec().getPlanetType(), false);
			float corona = star.getRadius() * (starData.getCoronaMult() + starData.getCoronaVar() * (random.nextFloat() - 0.5f));
			if (corona < starData.getCoronaMin()) corona = starData.getCoronaMin();

			SectorEntityToken eventHorizon = system.addTerrain(Terrain.EVENT_HORIZON,
					new CoronaParams(star.getRadius() + corona, (star.getRadius() + corona) / 2f,
							star, starData.getSolarWind(),
							starData.getMinFlare() + (starData.getMaxFlare() - starData.getMinFlare()) * random.nextFloat(),
							starData.getCrLossMult()));
			eventHorizon.setCircularOrbit(star, 0, 0, 100);
		}
	}

	protected void setPulsarIfNeutron(PlanetAPI star) {
		if (star == null) return;

		if (star.getSpec().getPlanetType().equals("star_neutron")) {
			StarCoronaTerrainPlugin coronaPlugin = Misc.getCoronaFor(star);
			if (coronaPlugin != null) {
				system.removeEntity(coronaPlugin.getEntity());
			}

			system.addCorona(star,
					300, // radius
					3, // wind
					0, // flares
					3); // cr loss


			StarGenDataSpec starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, star.getSpec().getPlanetType(), false);
			float corona = star.getRadius() * (starData.getCoronaMult() + starData.getCoronaVar() * (random.nextFloat() - 0.5f));
			if (corona < starData.getCoronaMin()) corona = starData.getCoronaMin();

			SectorEntityToken eventHorizon = system.addTerrain(Terrain.PULSAR_BEAM,
					new CoronaParams(star.getRadius() + corona, (star.getRadius() + corona) / 2f,
							star, starData.getSolarWind(),
							starData.getMinFlare() + (starData.getMaxFlare() - starData.getMinFlare()) * random.nextFloat(),
							starData.getCrLossMult()));
			eventHorizon.setCircularOrbit(star, 0, 0, 100);
		}
	}

	protected void switchPrimaryAndSecondaryIfNeeded(boolean sizeDownSmaller) {
		if (star == null || secondary == null) return;

		if (star.getRadius() < secondary.getRadius()) {
			star = secondary;
			secondary = system.getStar();
			system.setStar(star);

			String temp = star.getName();
			star.setName(secondary.getName());
			secondary.setName(temp);

			temp = star.getId();
			star.setId(secondary.getId());
			secondary.setId(temp);

			if (sizeDownSmaller) {
				secondary.setRadius(Math.min(secondary.getRadius(), star.getRadius() * 0.67f));
			}

			if (secondary == systemCenter) {
				systemCenter = star;
				centerRadius = systemCenter.getRadius();
			}
		}
	}

	protected void switchPrimaryAndTertiaryIfNeeded(boolean sizeDownSmaller) {
		if (star == null || tertiary == null) return;

		if (star.getRadius() < tertiary.getRadius()) {
			star = tertiary;
			tertiary = system.getStar();
			system.setStar(star);

			String temp = star.getName();
			star.setName(tertiary.getName());
			tertiary.setName(temp);

			temp = star.getId();
			star.setId(tertiary.getId());
			tertiary.setId(temp);

			if (sizeDownSmaller) {
				tertiary.setRadius(Math.min(tertiary.getRadius(), star.getRadius() * 0.67f));
			}

			if (tertiary == systemCenter) {
				systemCenter = star;
				centerRadius = systemCenter.getRadius();
			}
		}
	}


	protected void addStableLocations() {
		int min = 1;
		int max = 3;

		int num = random.nextInt(max + 1 - min) + min;

		if (num == min && random.nextFloat() < 0.25f) {
			num = 0;
		}

		addStableLocations(system, num);
	}

	public static void addStableLocations(StarSystemAPI system, int num) {
		for (int i = 0; i < num; i++) {
			LinkedHashMap<LocationType, Float> weights = new LinkedHashMap<LocationType, Float>();
			weights.put(LocationType.STAR_ORBIT, 10f);
			weights.put(LocationType.OUTER_SYSTEM, 10f);
			weights.put(LocationType.L_POINT, 10f);
			weights.put(LocationType.IN_SMALL_NEBULA, 2f);
			WeightedRandomPicker<EntityLocation> locs = BaseThemeGenerator.getLocations(random, system, null, 100f, weights);
			EntityLocation loc = locs.pick();

			AddedEntity added = BaseThemeGenerator.addNonSalvageEntity(system, loc, Entities.STABLE_LOCATION, Factions.NEUTRAL);

			if (added != null) {
				BaseThemeGenerator.convertOrbitPointingDown(added.entity);
			}
		}
	}


	protected void addJumpPoints(StarSystemGenerator.GenResult result, boolean farStarMode) {

		float outerRadius = centerRadius + STARTING_RADIUS_STAR_BASE + STARTING_RADIUS_STAR_RANGE * random.nextFloat();
		if (result != null) {
			outerRadius = result.orbitalWidth / 2f + 500f;
		}

		if (farStarMode) {
			if (result.context.orbitIndex < 0 && random.nextFloat() < 0.5f) {
				return;
			}
			SectorEntityToken farStar = result.context.center;
			String name = "Omega Jump-point";
			if (result.context.center == tertiary) {
				name = "Omicron Jump-point";
			}
			JumpPointAPI point = Global.getFactory().createJumpPoint(null, name);
			point.setStandardWormholeToHyperspaceVisual();
			float orbitDays = outerRadius / (15f + random.nextFloat() * 5f);
			point.setCircularOrbit(farStar, random.nextFloat() * 360f, outerRadius, orbitDays);
			system.addEntity(point);

			return;
		}


		JumpPointAPI point = Global.getFactory().createJumpPoint(null, "Fringe Jump-point");
		point.setStandardWormholeToHyperspaceVisual();
		float orbitDays = outerRadius / (15f + random.nextFloat() * 5f);
		point.setCircularOrbit(systemCenter, random.nextFloat() * 360f, outerRadius, orbitDays);
		system.addEntity(point);


		// to make sure that "is this location clear" calculations below always work
		system.updateAllOrbits();


		if (result != null) {
			float halfway = outerRadius * 0.5f;

			WeightedRandomPicker<StarSystemGenerator.LagrangePoint> inner = new WeightedRandomPicker<StarSystemGenerator.LagrangePoint>(random);
			WeightedRandomPicker<StarSystemGenerator.LagrangePoint> outer = new WeightedRandomPicker<StarSystemGenerator.LagrangePoint>(random);

			int total = 0;
			for (StarSystemGenerator.GeneratedPlanet planet : result.context.generatedPlanets) {
				if (planet.isMoon) continue;
				if (planet.planet.getOrbit() == null || planet.planet.getOrbit().getFocus() == null) continue;
				total++;

				for (StarSystemGenerator.LagrangePointType type : EnumSet.of(StarSystemGenerator.LagrangePointType.L4, StarSystemGenerator.LagrangePointType.L5)) {
					float orbitRadius = planet.orbitRadius;
					float angleOffset = -LAGRANGE_OFFSET * 0.5f;
					if (type == StarSystemGenerator.LagrangePointType.L5) angleOffset = LAGRANGE_OFFSET * 0.5f;
					float angle = planet.orbitAngle + angleOffset;
					Vector2f location = Misc.getUnitVectorAtDegreeAngle(angle + angleOffset);
					location.scale(orbitRadius);
					Vector2f.add(location, planet.planet.getOrbit().getFocus().getLocation(), location);

					boolean clear = true;
					for (PlanetAPI curr : system.getPlanets()) {
						float dist = Misc.getDistance(curr.getLocation(), location);
						if (dist < 500) {
							clear = false;
							break;
						}
					}
					if (clear) {
						if (planet.orbitRadius < halfway || planet.orbitRadius < 5000f) {
							inner.add(new StarSystemGenerator.LagrangePoint(planet, type), 10f);
						} else {
							outer.add(new StarSystemGenerator.LagrangePoint(planet, type), 10f);
						}
					}
				}
			}


			if (outerRadius > 2000f + 5000f * random.nextFloat()) {
				boolean addedOne = false;
				if (!inner.isEmpty()) {
					StarSystemGenerator.LagrangePoint p = inner.pick();
					String name = "Inner System Jump-point";
					if (systemType == StarSystemGenerator.StarSystemType.NEBULA) name = "Inner Jump-point";
					addJumpPoint(p, name);
					addedOne = true;
				}


				if (!outer.isEmpty() && (random.nextFloat() < outer.getItems().size() * 0.2f || !addedOne)) {
					StarSystemGenerator.LagrangePoint p = outer.pick();
					String name = "Outer System Jump-point";
					if (systemType == StarSystemGenerator.StarSystemType.NEBULA) name = "Outer Jump-point";
					addJumpPoint(p, name);
					addedOne = true;
				}
			}
		}
	}

	protected void addJumpPoint(StarSystemGenerator.LagrangePoint p, String name) {
		float orbitRadius = p.parent.orbitRadius;
		float orbitDays = p.parent.orbitDays;
		float angleOffset = -LAGRANGE_OFFSET * 0.5f;
		if (p.type == StarSystemGenerator.LagrangePointType.L5) angleOffset = LAGRANGE_OFFSET * 0.5f;
		float angle = p.parent.orbitAngle + angleOffset;

		SectorEntityToken focus = p.parent.planet.getOrbitFocus();
		if (focus == null) focus = systemCenter;

		JumpPointAPI point = Global.getFactory().createJumpPoint(null, name);
		point.setStandardWormholeToHyperspaceVisual();
		if (!p.parent.planet.isGasGiant()) {
			point.setRelatedPlanet(p.parent.planet);
		}
		point.setCircularOrbit(focus, angle + angleOffset, orbitRadius, orbitDays);
		system.addEntity(point);
	}

	public static float addOrbitingEntities(StarSystemAPI system, SectorEntityToken parentStar, StarAge age,
											int min, int max, float startingRadius,
											int nameOffset, boolean withSpecialNames) {
		return addOrbitingEntities(system, parentStar, age, min, max, startingRadius, nameOffset, withSpecialNames, true);
	}

	public static float addOrbitingEntities(StarSystemAPI system, SectorEntityToken parentStar, StarAge age,
											int min, int max, float startingRadius,
											int nameOffset, boolean withSpecialNames,
											boolean allowHabitable) {
		StarSystemGenerator.CustomConstellationParams p = new StarSystemGenerator.CustomConstellationParams(age);
		p.forceNebula = true; // not sure why this is here; should avoid small nebula at lagrange points though (but is that desired?)

		jdp_StarSystemGenerator gen = new jdp_StarSystemGenerator(p);
		gen.system = system;
		gen.starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, system.getStar().getSpec().getPlanetType(), false);
		gen.starAge = age;
		gen.constellationAge = age;
		gen.starAgeData = (AgeGenDataSpec) Global.getSettings().getSpec(AgeGenDataSpec.class, age.name(), true);
		gen.star = system.getStar();

		gen.pickNebulaAndBackground();
		if (system.getType() != null) gen.systemType = system.getType();


		gen.systemCenter = system.getCenter();

		StarGenDataSpec starData = gen.starData;
		PlanetGenDataSpec planetData = null;
		PlanetAPI parentPlanet = null;
		if (parentStar instanceof PlanetAPI planet) {
			if (planet.isStar()) {
				starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, planet.getSpec().getPlanetType(), false);
			} else {
				planetData = (PlanetGenDataSpec) Global.getSettings().getSpec(PlanetGenDataSpec.class, planet.getSpec().getPlanetType(), false);
				parentPlanet = planet;
			}
		}

		int parentOrbitIndex = -1;
		int startingOrbitIndex = 0;

		boolean addingAroundStar = parentPlanet == null;
		float r = 0;
		if (parentStar != null) {
			r = parentStar.getRadius();
		}

		float approximateExtraRadiusPerOrbit = 400f;
		if (addingAroundStar) {
			parentOrbitIndex = -1;
			startingOrbitIndex = (int) ((startingRadius - r - STARTING_RADIUS_STAR_BASE - STARTING_RADIUS_STAR_RANGE * 0.5f) /
					(BASE_INCR * 1.25f + approximateExtraRadiusPerOrbit));

			if (startingOrbitIndex < 0) startingOrbitIndex = 0;
		} else {
			float dist = 0f;
			if (parentPlanet.getOrbitFocus() != null) {
				dist = Misc.getDistance(parentPlanet.getLocation(), parentPlanet.getOrbitFocus().getLocation());
			}
			parentOrbitIndex = (int) ((dist - r - STARTING_RADIUS_STAR_BASE - STARTING_RADIUS_STAR_RANGE * 0.5f) /
					(BASE_INCR * 1.25f + approximateExtraRadiusPerOrbit));
			startingOrbitIndex = (int) ((startingRadius - STARTING_RADIUS_MOON_BASE - STARTING_RADIUS_MOON_RANGE * 0.5f) /
					(BASE_INCR_MOON * 1.25f));

			if (parentOrbitIndex < 0) parentOrbitIndex = 0;
			if (startingOrbitIndex < 0) startingOrbitIndex = 0;
		}

		int num = Math.round(getNormalRandom(min, max));

		StarSystemGenerator.GenContext context = new StarSystemGenerator.GenContext(gen, system, gen.systemCenter, starData,
				parentPlanet, startingOrbitIndex, age.name(), startingRadius, MAX_ORBIT_RADIUS,
				planetData != null ? planetData.getCategory() : null, parentOrbitIndex);

		if (!allowHabitable) {
			context.excludeCategories.add(CAT_HAB5);
			context.excludeCategories.add(CAT_HAB4);
			context.excludeCategories.add(CAT_HAB3);
			context.excludeCategories.add(CAT_HAB2);
		}

		StarSystemGenerator.GenResult result = gen.addOrbitingEntities(context, num, false, addingAroundStar, false, false);


		Constellation c = new Constellation(Constellation.ConstellationType.NORMAL, age);
		c.getSystems().add(system);
		c.setLagrangeParentMap(gen.lagrangeParentMap);
		c.setAllEntitiesAdded(gen.allNameableEntitiesAdded);
		c.setLeavePickedNameUnused(true);
		NameAssigner namer = new NameAssigner(c);
		if (withSpecialNames) {
			namer.setSpecialNamesProbability(1f);
		} else {
			namer.setSpecialNamesProbability(0f);
		}
		namer.setRenameSystem(false);
		namer.setStructuralNameOffset(nameOffset);
		namer.assignNames(null, null);

		for (SectorEntityToken entity : gen.allNameableEntitiesAdded.keySet()) {
			if (entity instanceof PlanetAPI && entity.getMarket() != null) {
				entity.getMarket().setName(entity.getName());
			}
		}

		return result.orbitalWidth * 0.5f;

	}

	public static void addSystemwideNebula(StarSystemAPI system, StarAge age) {
		jdp_StarSystemGenerator.CustomConstellationParams p = new jdp_StarSystemGenerator.CustomConstellationParams(age);
		p.forceNebula = true;

		jdp_StarSystemGenerator gen = new jdp_StarSystemGenerator(p.clone());
		gen.system = system;
		gen.starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, system.getStar().getSpec().getPlanetType(), false);
		gen.starAge = age;
		gen.constellationAge = age;
		gen.starAgeData = (AgeGenDataSpec) Global.getSettings().getSpec(AgeGenDataSpec.class, age.name(), true);
		gen.pickNebulaAndBackground();
		if (system.getType() != null) gen.systemType = system.getType();

		gen.addSystemwideNebula();

		system.setAge(age);
		system.setHasSystemwideNebula(true);
	}

	protected void addSystemwideNebula() {
		if (nebulaType.equals(NEBULA_NONE)) return;


		int w = 128;
		int h = 128;

		StringBuilder string = new StringBuilder();
		for (int y = h - 1; y >= 0; y--) {
			for (int x = 0; x < w; x++) {
				string.append("x");
			}
		}
		SectorEntityToken nebula = system.addTerrain(Terrain.NEBULA, new TileParams(string.toString(),
				w, h,
				"terrain", nebulaType, 4, 4, null));
		nebula.getLocation().set(0, 0);

		NebulaTerrainPlugin nebulaPlugin = (NebulaTerrainPlugin) ((CampaignTerrainAPI) nebula).getPlugin();
		NebulaEditor editor = new NebulaEditor(nebulaPlugin);

		editor.regenNoise();

		// yes, star age here, despite using constellation age to determine if a nebula to all exists
		// basically: young star in old constellation will have lots of nebula, but of the constellation-age color
		editor.noisePrune(starAgeData.getNebulaDensity());

		editor.regenNoise();

		if (systemType != StarSystemGenerator.StarSystemType.NEBULA) {
			for (PlanetAPI planet : system.getPlanets()) {

				if (planet.getOrbit() != null && planet.getOrbit().getFocus() != null &&
						planet.getOrbit().getFocus().getOrbit() != null) {
					// this planet is orbiting something that's orbiting something
					// its motion will be relative to its parent moving
					// don't clear anything out for this planet
					continue;
				}

				float clearThreshold = 0f; // clear everything by default
				float clearInnerRadius = 0f;
				float clearOuterRadius = 0f;
				Vector2f clearLoc = null;


				if (!planet.isStar() && !planet.isGasGiant()) {
					clearThreshold = 1f - Math.min(0f, planet.getRadius() / 300f);
					if (clearThreshold > 0.5f) clearThreshold = 0.5f;
				}

				Vector2f loc = planet.getLocation();
				if (planet.getOrbit() != null && planet.getOrbit().getFocus() != null) {
					Vector2f focusLoc = planet.getOrbit().getFocus().getLocation();
					float dist = Misc.getDistance(planet.getOrbit().getFocus().getLocation(), loc);
					float width = planet.getRadius() * 4f + 100f;
					if (planet.isStar()) {
						StarCoronaTerrainPlugin corona = Misc.getCoronaFor(planet);
						if (corona != null) {
							width = corona.getParams().bandWidthInEngine * 4f;
						}
						PulsarBeamTerrainPlugin pulsar = Misc.getPulsarFor(planet);
						if (pulsar != null) {
							width = Math.max(width, pulsar.getParams().bandWidthInEngine * 0.5f);
						}
					}
					clearLoc = focusLoc;
					clearInnerRadius = dist - width / 2f;
					clearOuterRadius = dist + width / 2f;
				} else if (planet.getOrbit() == null) {
					float width = planet.getRadius() * 4f + 100f;
					if (planet.isStar()) {
						StarCoronaTerrainPlugin corona = Misc.getCoronaFor(planet);
						if (corona != null) {
							width = corona.getParams().bandWidthInEngine * 4f;
						}
						PulsarBeamTerrainPlugin pulsar = Misc.getPulsarFor(planet);
						if (pulsar != null) {
							width = Math.max(width, pulsar.getParams().bandWidthInEngine * 0.5f);
						}
					}
					clearLoc = loc;
					clearInnerRadius = 0f;
					clearOuterRadius = width;
				}

				if (clearLoc != null) {
					float min = nebulaPlugin.getTileSize() * 2f;
					if (clearOuterRadius - clearInnerRadius < min) {
						clearOuterRadius = clearInnerRadius + min;
					}
					editor.clearArc(clearLoc.x, clearLoc.y, clearInnerRadius, clearOuterRadius, 0, 360f, clearThreshold);
				}
			}
		}

		// add a spiral going from the outside towards the star
		float angleOffset = random.nextFloat() * 360f;
		editor.clearArc(0f, 0f, 30000, 31000 + 1000f * random.nextFloat(),
				angleOffset + 0f, angleOffset + 360f * (2f + random.nextFloat() * 2f), 0.01f, 0f);

		// do some random arcs
		int numArcs = (int) (8f + 6f * random.nextFloat());
		//int numArcs = 11;

		for (int i = 0; i < numArcs; i++) {
			//float dist = 4000f + 10000f * random.nextFloat();
			float dist = 15000f + 15000f * random.nextFloat();
			float angle = random.nextFloat() * 360f;

			Vector2f dir = Misc.getUnitVectorAtDegreeAngle(angle);
			dir.scale(dist - (2000f + 8000f * random.nextFloat()));

			//float tileSize = nebulaPlugin.getTileSize();
			//float width = tileSize * (2f + 4f * random.nextFloat());
			float width = 800f * (1f + 2f * random.nextFloat());

			float clearThreshold = 0f + 0.5f * random.nextFloat();
			//clearThreshold = 0f;

			editor.clearArc(dir.x, dir.y, dist - width / 2f, dist + width / 2f, 0, 360f, clearThreshold);
		}
	}

	protected StarSystemGenerator.GenResult addPlanetsAndTerrain(float maxOrbitRadius) {
		boolean hasOrbits = random.nextFloat() < starData.getProbOrbits();
		if (!hasOrbits) return null;

		float min = starData.getMinOrbits() + starAgeData.getMinExtraOrbits();
		float max = starData.getMaxOrbits() + starAgeData.getMaxExtraOrbits();
		int numOrbits = Math.round(getNormalRandom(min, max));

		if (numOrbits <= 0) return null;


		float currentRadius = centerRadius + STARTING_RADIUS_STAR_BASE + STARTING_RADIUS_STAR_RANGE * random.nextFloat();

		StarSystemGenerator.GenContext context = new StarSystemGenerator.GenContext(this, system, systemCenter, starData,
				null, 0, starAge.name(), currentRadius, maxOrbitRadius, null, -1);

		if (systemType == StarSystemGenerator.StarSystemType.BINARY_CLOSE || systemType == StarSystemGenerator.StarSystemType.TRINARY_1CLOSE_1FAR) {
			context.multipliers.add(COL_BINARY);
		}
		if (systemType == StarSystemGenerator.StarSystemType.TRINARY_2CLOSE) {
			context.multipliers.add(COL_TRINARY);
		}


		StarSystemGenerator.GenResult result = addOrbitingEntities(context, numOrbits, false, true, false, true);
		result.context = context;
		return result;
	}

	protected StarSystemGenerator.GenResult addOrbitingEntities(StarSystemGenerator.GenContext context, int numOrbits, boolean addingMoons, boolean addMoons, boolean parentIsMoon, boolean nothingOk) {

		if (DEBUG && context.starData != null) {
			if (addingMoons && context.parent != null) {
				System.out.println("  Adding " + numOrbits + " moon orbits around " + context.parent.getSpec().getPlanetType());
			} else {
				System.out.println("Adding " + numOrbits + " orbits around " + context.starData.getId());
			}
		}

		float currentRadius = context.currentRadius;
		float lastIncrementExtra = 0f;

		String extraMult = null;
		if (parentIsMoon) extraMult = COL_IS_MOON;

		int extra = 0;
		for (int i = 0; i < numOrbits; i++) {

			context.orbitIndex = i + context.startingOrbitIndex + extra;
			//CategoryGenDataSpec categoryData = pickCategory(orbitIndex, i, starAge.name(), starType, context.parentCategory, null, nothingOk);
			CategoryGenDataSpec categoryData = pickCategory(context, extraMult, nothingOk);
			nothingOk = true; // only applies to first pick, after this, we'll have *something*

//			if (!addingMoons) {
//				categoryData = (CategoryGenDataSpec) Global.getSettings().getSpec(CategoryGenDataSpec.class, "cat_giant", true);
//				//categoryData = (CategoryGenDataSpec) Global.getSettings().getSpec(CategoryGenDataSpec.class, "cat_terrain_rings", true);
//			}
//			if (orbitIndex == 0 && addMoons) {
//				categoryData = (CategoryGenDataSpec) Global.getSettings().getSpec(CategoryGenDataSpec.class, "cat_giant", true);
//				//categoryData = (CategoryGenDataSpec) Global.getSettings().getSpec(CategoryGenDataSpec.class, "cat_hab4", true);
//			}
//			if (orbitIndex == 1 && addMoons) {
//			if (!addingMoons) {
//				categoryData = (CategoryGenDataSpec) Global.getSettings().getSpec(CategoryGenDataSpec.class, "cat_giant", true);
//			}
//			} else {
//				categoryData = (CategoryGenDataSpec) Global.getSettings().getSpec(CategoryGenDataSpec.class, "cat_terrain_rings", true);
//			}
			//categoryData = (CategoryGenDataSpec) Global.getSettings().getSpec(CategoryGenDataSpec.class, "cat_terrain_rings", true);

			StarSystemGenerator.GenResult result = null;
			float incrMult = 1f;

			if (categoryData != null && !CAT_NOTHING.equals(categoryData.getCategory())) {
				//WeightedRandomPicker<EntityGenDataSpec> picker = getPickerForCategory(categoryData, orbitIndex, i, starAge.name(), starType, context.parentCategory, null);
				WeightedRandomPicker<EntityGenDataSpec> picker = getPickerForCategory(categoryData, context, extraMult);
				if (DEBUG) {
					picker.print("  Picking from category " + categoryData.getCategory() +
							", orbit index " + (context.parent != null ? context.parentOrbitIndex : context.orbitIndex));
				}
				EntityGenDataSpec entityData = picker.pick();
				if (DEBUG) {
					if (entityData == null) {
						System.out.println("  Nothing to pick");
						System.out.println();
					} else {
						System.out.println("  Picked: " + entityData.getId());
						System.out.println();
					}
				}

				context.currentRadius = currentRadius;
				//context.orbitIndex = i;

				if (entityData instanceof PlanetGenDataSpec planetData) {
					result = addPlanet(context, planetData, addingMoons, addMoons);

				} else if (entityData instanceof TerrainGenDataSpec terrainData) {
					result = addTerrain(context, terrainData);
				}

				if (result != null) {
					//List<SectorEntityToken> combined = new ArrayList<SectorEntityToken>(result.entities);
					for (SectorEntityToken curr : result.entities) {
						if (context.lagrangeParent != null && !result.entities.isEmpty()) {
							lagrangeParentMap.put(curr, context.lagrangeParent.planet);
						}
						allNameableEntitiesAdded.put(curr, result.entities);
					}
				} else {
					incrMult = 0.5f;
				}
			} else {
				incrMult = 0.5f;
			}

			float baseIncr = BASE_INCR;
			if (addingMoons) {
				baseIncr = BASE_INCR_MOON;
			}
			baseIncr *= incrMult;

			float increment = baseIncr + baseIncr * 0.5f * random.nextFloat();
			if (result != null) {
				if (result.orbitalWidth > 1000) extra++;
				//increment = Math.max(increment, result.orbitalWidth + baseIncr * 0.5f);
				//increment += result.orbitalWidth;
				increment = Math.max(increment + Math.min(result.orbitalWidth, 300f),
						result.orbitalWidth + increment * 0.5f);
				if (result.onlyIncrementByWidth) {
					increment = result.orbitalWidth;
				}
				lastIncrementExtra = Math.max(increment * 0.1f, increment - result.orbitalWidth);
			} else {
				lastIncrementExtra = increment;
			}
			currentRadius += increment;

			if (currentRadius >= context.maxOrbitRadius) {
				break;
			}
		}

		StarSystemGenerator.GenResult result = new StarSystemGenerator.GenResult();
		result.onlyIncrementByWidth = false;
		result.orbitalWidth = (currentRadius - lastIncrementExtra) * 2f;
		return result;
	}

	protected StarSystemGenerator.GenResult addTerrain(StarSystemGenerator.GenContext context, TerrainGenDataSpec terrainData) {
		TerrainGenPlugin plugin = pickTerrainGenPlugin(terrainData, context);
		if (plugin == null) return null;
		StarSystemGenerator.GenResult result = plugin.generate(terrainData, context);


		return result;
	}

	public StarSystemGenerator.GenResult addPlanet(StarSystemGenerator.GenContext context, PlanetGenDataSpec planetData, boolean isMoon, boolean addMoons) {
		float radius = getRadius(planetData.getMinRadius(), planetData.getMaxRadius());
		if (isMoon) {
			float mult = MOON_RADIUS_MIN_FRACTION_OF_NORMAL +
					random.nextFloat() * (MOON_RADIUS_MAX_FRACTION_OF_NORMAL - MOON_RADIUS_MIN_FRACTION_OF_NORMAL);
			radius *= mult;
			if (radius < MIN_MOON_RADIUS) {
				radius = MIN_MOON_RADIUS;
			}
			float parentRadius = 100000f;
			if (context.parent != null || context.lagrangeParent != null) {
				PlanetAPI parent = context.parent;
				if (context.lagrangeParent != null) {
					parent = context.lagrangeParent.planet;
				}
				parentRadius = parent.getRadius();
			}
			if (context.parentRadiusOverride > 0) {
				parentRadius = context.parentRadiusOverride;
			}

			if (parentRadius > MIN_MOON_RADIUS / MOON_RADIUS_MAX_FRACTION_OF_PARENT) {
				float max = parentRadius * MOON_RADIUS_MAX_FRACTION_OF_PARENT;
				if (radius > max) {
					radius = max;
				}
			}
		}

		float orbitRadius = context.currentRadius + radius;
		float orbitDays = orbitRadius / (20f + random.nextFloat() * 5f);

		String planetId = context.center.getId() + ":planet_" + context.orbitIndex;
		String planetName = "Planet " + context.orbitIndex;
		if (context.parent != null) {
			planetId = system.getId() + "_moon_" + context.center.getId() + "_" + context.parent.getId() + "_" + context.orbitIndex;
			planetName = context.parent.getName() + " moon " + context.orbitIndex;
		}
		String planetType = planetData.getId();
		SectorEntityToken parent = context.center;
		if (context.parent != null) parent = context.parent;

		float angle = random.nextFloat() * 360f;

		// if adding a moon to a largange point planet, then parentCategory == null
		// will fail and it'll orbit the parent rather than stay at the point
		if (context.parentCategory == null) {
			if (context.lagrangeParent != null && context.lagrangePointType != null) {
				orbitRadius = context.lagrangeParent.orbitRadius;
				orbitDays = context.lagrangeParent.orbitDays;
				float angleOffset = -LAGRANGE_OFFSET;
				if (context.lagrangePointType == StarSystemGenerator.LagrangePointType.L5)
					angleOffset = LAGRANGE_OFFSET;
				angle = context.lagrangeParent.orbitAngle + angleOffset;
				planetName += " " + context.lagrangePointType.name();
				planetId += "_" + context.lagrangePointType.name();
			}
		}

		PlanetAPI planet = system.addPlanet(planetId, parent, planetName, planetType, angle, radius, orbitRadius, orbitDays);
		if (planet.isGasGiant()) {
			if (systemType == StarSystemGenerator.StarSystemType.NEBULA) {
				planet.setAutogenJumpPointNameInHyper(system.getBaseName() + ", " + planetName + " Gravity Well");
			}
		}

		float radiusWithMoons = planet.getRadius();
		if (addMoons) {
			boolean hasOrbits = random.nextFloat() < planetData.getProbOrbits();
			float min = planetData.getMinOrbits();
			float max = planetData.getMaxOrbits();

			int numOrbits = Math.round(getNormalRandom(min, max));


			if (hasOrbits && numOrbits > 0) {
				float startingRadius = planet.getRadius() + STARTING_RADIUS_MOON_BASE + STARTING_RADIUS_MOON_RANGE * random.nextFloat();
				StarSystemGenerator.GenContext moonContext = new StarSystemGenerator.GenContext(this, context.system, context.center, context.starData, planet, 0, starAge.name(),
						startingRadius, context.maxOrbitRadius, planetData.getCategory(), context.orbitIndex);
				moonContext.excludeCategories.addAll(context.excludeCategories);
				moonContext.multipliers.addAll(context.multipliers);
				// add moons etc
				StarSystemGenerator.GenResult moonResult = addOrbitingEntities(moonContext, numOrbits, true, false, false, false);

				context.generatedPlanets.addAll(moonContext.generatedPlanets);

				// move the parent planet out so that there's room for everythnig that was added
				radius = moonResult.orbitalWidth * 0.5f;
				orbitRadius = context.currentRadius + radius;
				orbitDays = orbitRadius / (20f + random.nextFloat() * 5f);

				radiusWithMoons = radius;

				planet.setOrbit(Global.getFactory().createCircularOrbit(context.center, angle, orbitRadius, orbitDays));
			}
		} else if (isMoon) {
			float startingRadius = planet.getRadius() + STARTING_RADIUS_MOON_BASE + STARTING_RADIUS_MOON_RANGE * random.nextFloat();
			StarSystemGenerator.GenContext moonContext = new StarSystemGenerator.GenContext(this, context.system, context.center, context.starData, planet, 0, starAge.name(),
					startingRadius, context.maxOrbitRadius, planetData.getCategory(), context.orbitIndex);
			moonContext.excludeCategories.addAll(context.excludeCategories);
			moonContext.multipliers.addAll(context.multipliers);
			StarSystemGenerator.GenResult moonResult = addOrbitingEntities(moonContext, 1, true, false, true, true);
			context.generatedPlanets.addAll(moonContext.generatedPlanets);

		}


		Color color = getColor(planetData.getMinColor(), planetData.getMaxColor());
		//System.out.println("Setting color: " + color);
		planet.getSpec().setPlanetColor(color);
		if (planet.getSpec().getAtmosphereThickness() > 0) {
			Color atmosphereColor = Misc.interpolateColor(planet.getSpec().getAtmosphereColor(), color, 0.25f);
			atmosphereColor = Misc.setAlpha(atmosphereColor, planet.getSpec().getAtmosphereColor().getAlpha());
			planet.getSpec().setAtmosphereColor(atmosphereColor);

			if (planet.getSpec().getCloudTexture() != null) {
				Color cloudColor = Misc.interpolateColor(planet.getSpec().getCloudColor(), color, 0.25f);
				cloudColor = Misc.setAlpha(cloudColor, planet.getSpec().getCloudColor().getAlpha());
				planet.getSpec().setAtmosphereColor(atmosphereColor);
			}
		}

		float tilt = planet.getSpec().getTilt();
		float pitch = planet.getSpec().getPitch();

		float sign = Math.signum(random.nextFloat() - 0.5f);
		double r = random.nextFloat();
		//r *= r;
		if (sign > 0) {
			tilt += r * TILT_MAX;
		} else {
			tilt += r * TILT_MIN;
		}

		sign = Math.signum(random.nextFloat() - 0.5f);
		r = random.nextFloat();
		//r *= r;
		if (sign > 0) {
			pitch += r * PITCH_MAX;
		} else {
			tilt += r * PITCH_MIN;
		}
		planet.getSpec().setTilt(tilt);
		planet.getSpec().setPitch(pitch);


		if (context.orbitIndex == 0 && context.parent == null && context.orbitIndex < context.starData.getHabZoneStart() &&
				orbitRadius < 2500f + context.starData.getHabZoneStart() * 200f) {
			//&& radiusWithMoons <= planet.getRadius() + 500f) {
			if (planet.getSpec().getAtmosphereThickness() > 0) {
				WeightedRandomPicker<String> glowPicker = new WeightedRandomPicker<String>(random);
				glowPicker.add("banded", 10f);
				glowPicker.add("aurorae", 10f);

				String glow = glowPicker.pick();

				planet.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", glow));
				//system.getLightColor();
				if (context.center instanceof PlanetAPI) {
					planet.getSpec().setGlowColor(((PlanetAPI) context.center).getSpec().getCoronaColor());
				}
				planet.getSpec().setUseReverseLightForGlow(true);
				planet.getSpec().setAtmosphereThickness(0.5f);
				planet.getSpec().setCloudRotation(planet.getSpec().getCloudRotation() * (-1f - 2f * random.nextFloat()));

				if (planet.isGasGiant()) {// && radiusWithMoons <= planet.getRadius() + 500f) {
					system.addCorona(planet, Terrain.CORONA_AKA_MAINYU,
							300f + 200f * random.nextFloat(), // radius outside planet
							5f, // burn level of "wind"
							0f, // flare probability
							1f // CR loss mult while in it
					);
				}
			}
		}

		planet.applySpecChanges();

		PlanetConditionGenerator.generateConditionsForPlanet(context, planet);


		StarSystemGenerator.GeneratedPlanet generatedPlanetData = new StarSystemGenerator.GeneratedPlanet(parent, planet, isMoon, orbitDays, orbitRadius, angle);
		context.generatedPlanets.add(generatedPlanetData);

		// need to add this here because planet might have been moved after adding moons
		if (!isMoon && context.lagrangeParent == null) {// && planet.isGasGiant()) {
			addStuffAtLagrangePoints(context, generatedPlanetData);
		}

		StarSystemGenerator.GenResult result = new StarSystemGenerator.GenResult();
		result.orbitalWidth = radius * 2f;
		result.onlyIncrementByWidth = false;
		result.entities.add(planet);
		return result;
	}

	protected void addStuffAtLagrangePoints(StarSystemGenerator.GenContext context, StarSystemGenerator.GeneratedPlanet planet) {
		float radius = planet.planet.getRadius();
		float probability = radius / 500f;
		if (radius < 150f) probability = 0f;
		if (planet.planet.isGasGiant()) probability = 1f;
		if (random.nextFloat() > probability) {
			return;
		}

		// still a high chance to end up w/ nothing if cat_nothing is picked

		Set<StarSystemGenerator.LagrangePointType> points = EnumSet.of(StarSystemGenerator.LagrangePointType.L4, StarSystemGenerator.LagrangePointType.L5);
		for (StarSystemGenerator.LagrangePointType point : points) {
			//CategoryGenDataSpec categoryData = pickCategory(orbitIndex, 0, starAge.name(), starType, context.parentCategory, COL_LAGRANGE, true);
			CategoryGenDataSpec categoryData = pickCategory(context, COL_LAGRANGE, true);
			if (categoryData != null && !CAT_NOTHING.equals(categoryData.getCategory())) {
				//WeightedRandomPicker<EntityGenDataSpec> picker = getPickerForCategory(categoryData, orbitIndex, 0, starAge.name(), starType, context.parentCategory, COL_LAGRANGE);
				WeightedRandomPicker<EntityGenDataSpec> picker = getPickerForCategory(categoryData, context, COL_LAGRANGE);
				if (DEBUG) {
					picker.print("  Picking from category " + categoryData.getCategory() +
							", orbit index " + (context.parent != null ? context.parentOrbitIndex : context.orbitIndex + ", for lagrange point"));
				}
				EntityGenDataSpec entityData = picker.pick();
				if (DEBUG) {
					if (entityData == null) {
						System.out.println("  Nothing to pick");
						System.out.println();
					} else {
						System.out.println("  Picked: " + entityData.getId());
						System.out.println();
					}
				}

				context.lagrangeParent = planet;
				context.lagrangePointType = point;

				StarSystemGenerator.GenResult result = null;
				if (entityData instanceof PlanetGenDataSpec planetData) {
					result = addPlanet(context, planetData, true, true);
				} else if (entityData instanceof TerrainGenDataSpec terrainData) {
					result = addTerrain(context, terrainData);
				}

				if (result != null) {
					for (SectorEntityToken curr : result.entities) {
						if (context.lagrangeParent != null && !result.entities.isEmpty()) {
							lagrangeParentMap.put(curr, context.lagrangeParent.planet);
						}
						allNameableEntitiesAdded.put(curr, result.entities);
					}
				}
			}
		}
		context.lagrangeParent = null;
		context.lagrangePointType = null;
	}

	public CategoryGenDataSpec pickCategory(StarSystemGenerator.GenContext context, String extraMult, boolean nothingOk) {
//		int orbitIndex = context.orbitIndex;
//		if (context.parentOrbitIndex >= 0) {
//			orbitIndex = context.parentOrbitIndex;
//		}
//		int fromParentOrbitIndex = context.orbitIndex;
		String age = context.age;
		//String starType = context.star.getTypeId();
		String starType = star != null ? star.getTypeId() : null;
		if (context.center instanceof PlanetAPI star) {
			if (star.isStar()) starType = star.getTypeId();
		}

		String parentCategory = context.parentCategory;

		WeightedRandomPicker<CategoryGenDataSpec> picker = new WeightedRandomPicker<CategoryGenDataSpec>(random);
		Collection<CategoryGenDataSpec> categoryDataSpecs = Global.getSettings().getAllSpecs(CategoryGenDataSpec.class);
		for (CategoryGenDataSpec categoryData : categoryDataSpecs) {
			if (context.excludeCategories.contains(categoryData.getCategory())) continue;

			boolean catNothing = categoryData.getCategory().equals(CAT_NOTHING);
			if (!nothingOk && catNothing) continue;
//			if (categoryData.getCategory().equals("cat_terrain_rings")) {
//				System.out.println("sdfkwefewfe");
//			}
			float weight = categoryData.getFrequency();
			if (age != null) weight *= categoryData.getMultiplier(age);
			if (starType != null) weight *= categoryData.getMultiplier(starType);
			if (parentCategory != null) weight *= categoryData.getMultiplier(parentCategory);
			for (String col : context.multipliers) {
				weight *= categoryData.getMultiplier(col);
			}
			if (extraMult != null) weight *= categoryData.getMultiplier(extraMult);

			//if (weight > 0 && (catNothing || !isCategoryEmpty(categoryData, orbitIndex, fromParentOrbitIndex, age, starType, parentCategory, extraMult))) {
			if (weight > 0 && (catNothing || !isCategoryEmpty(categoryData, context, extraMult, nothingOk))) {
				picker.add(categoryData, weight);
			}
		}

		if (DEBUG) {
			boolean withParent = context.parent != null;
			int orbitIndex = context.orbitIndex;
			String parentType = "";
			if (withParent) {
				parentType = context.parent.getSpec().getPlanetType();
				orbitIndex = context.parentOrbitIndex;
			}

//			float offset = orbitIndex;
//			float minIndex = context.starData.getHabZoneStart() + planetData.getHabOffsetMin() + offset;
//			float maxIndex = context.starData.getHabZoneStart() + planetData.getHabOffsetMax() + offset;
			//boolean inRightRange = orbitIndex >= minIndex && orbitIndex <= maxIndex;
			int habDiff = orbitIndex - (int) context.starData.getHabZoneStart();
			if (withParent) {
				picker.print("  Picking category for moon of " + parentType +
						", orbit from star: " + orbitIndex + " (" + habDiff + ")" + ", extra: " + extraMult);
			} else {
				picker.print("  Picking category for entity orbiting star " + starType +
						", orbit from star: " + orbitIndex + " (" + habDiff + ")" + ", extra: " + extraMult);
			}
		}

		CategoryGenDataSpec pick = picker.pick();
		if (DEBUG) {
			System.out.println("  Picked: " + pick.getCategory());
			System.out.println();
		}

		return pick;
	}

	public boolean isCategoryEmpty(CategoryGenDataSpec categoryData, StarSystemGenerator.GenContext context, String extraMult, boolean nothingOk) {
		return getPickerForCategory(categoryData, context, extraMult, nothingOk).isEmpty();
	}

	protected float getHabOffset(EntityGenDataSpec data) {
		if (starAge == StarAge.YOUNG) {
			return data.getHabOffsetYOUNG();
		}
		if (starAge == StarAge.AVERAGE) {
			return data.getHabOffsetAVERAGE();
		}
		if (starAge == StarAge.OLD) {
			return data.getHabOffsetOLD();
		}
		return 0f;
	}

	protected WeightedRandomPicker<EntityGenDataSpec> getPickerForCategory(CategoryGenDataSpec categoryData,
																		   StarSystemGenerator.GenContext context, String extraMult) {
		return getPickerForCategory(categoryData, context, extraMult, true);
	}

	protected WeightedRandomPicker<EntityGenDataSpec> getPickerForCategory(CategoryGenDataSpec categoryData,
																		   StarSystemGenerator.GenContext context, String extraMult, boolean nothingOk) {
		int orbitIndex = context.orbitIndex;
		if (context.parentOrbitIndex >= 0) {
			orbitIndex = context.parentOrbitIndex;
		}
		int fromParentOrbitIndex = context.orbitIndex;
		String age = context.age;
		//String starType = context.star.getTypeId();
		String starType = star != null ? star.getTypeId() : null;
		if (context.center instanceof PlanetAPI star) {
			if (star.isStar()) starType = star.getTypeId();
		}

		String parentCategory = context.parentCategory;


		WeightedRandomPicker<EntityGenDataSpec> picker = new WeightedRandomPicker<EntityGenDataSpec>(random);

		Collection<PlanetGenDataSpec> planetDataSpecs = Global.getSettings().getAllSpecs(PlanetGenDataSpec.class);
		for (PlanetGenDataSpec planetData : planetDataSpecs) {
			if (!planetData.getCategory().equals(categoryData.getCategory())) continue;

			float offset = getHabOffset(planetData);
			float minIndex = context.starData.getHabZoneStart() + planetData.getHabOffsetMin() + offset;
			float maxIndex = context.starData.getHabZoneStart() + planetData.getHabOffsetMax() + offset;
			boolean inRightRange = orbitIndex >= minIndex && orbitIndex <= maxIndex;
			boolean giantMoonException = CAT_GIANT.equals(parentCategory) &&
					(planetData.hasTag(TAG_GIANT_MOON) && context.parent != null && context.parent.isGasGiant());
			if (!inRightRange && !giantMoonException) continue;

			boolean orbitIndexOk = fromParentOrbitIndex == 0 || !planetData.hasTag(TAG_FIRST_ORBIT_ONLY);
			if (!orbitIndexOk) continue;

			boolean lagrangeStatusOk = COL_LAGRANGE.equals(extraMult) || !planetData.hasTag(TAG_LAGRANGE_ONLY);
			if (!lagrangeStatusOk) continue;

			boolean nebulaStatusOk = NEBULA_NONE.equals(nebulaType) || !planetData.hasTag(TAG_NOT_IN_NEBULA);
			nebulaStatusOk &= !NEBULA_NONE.equals(nebulaType) || !planetData.hasTag(TAG_REQUIRES_NEBULA);
			nebulaStatusOk &= systemType != StarSystemGenerator.StarSystemType.NEBULA || !planetData.hasTag(TAG_NOT_NEBULA_UNLESS_MOON) || context.parent != null;
			if (!nebulaStatusOk) continue;

			float weight = planetData.getFrequency();
			if (age != null) weight *= planetData.getMultiplier(age);
			if (starType != null) weight *= planetData.getMultiplier(starType);
			if (parentCategory != null) weight *= planetData.getMultiplier(parentCategory);
			for (String col : context.multipliers) {
				weight *= planetData.getMultiplier(col);
			}
			if (extraMult != null) weight *= planetData.getMultiplier(extraMult);
			if (weight > 0) picker.add(planetData, weight);
		}

		Collection<TerrainGenDataSpec> terrainDataSpecs = Global.getSettings().getAllSpecs(TerrainGenDataSpec.class);
		for (TerrainGenDataSpec terrainData : terrainDataSpecs) {
			if (!terrainData.getCategory().equals(categoryData.getCategory())) continue;

			if (!nothingOk && terrainData.getId().equals("rings_nothing")) continue;

			float offset = getHabOffset(terrainData);
			float minIndex = context.starData.getHabZoneStart() + terrainData.getHabOffsetMin() + offset;
			float maxIndex = context.starData.getHabZoneStart() + terrainData.getHabOffsetMax() + offset;
			boolean inRightRange = orbitIndex >= minIndex && orbitIndex <= maxIndex;
			boolean giantMoonException = CAT_GIANT.equals(parentCategory) &&
					(terrainData.hasTag(TAG_GIANT_MOON) && context.parent != null && context.parent.isGasGiant());
			if (!inRightRange && !giantMoonException) continue;

			boolean orbitIndexOk = fromParentOrbitIndex == 0 || !terrainData.hasTag(TAG_FIRST_ORBIT_ONLY);
			if (!orbitIndexOk) continue;

			boolean lagrangeStatusOk = COL_LAGRANGE.equals(extraMult) || !terrainData.hasTag(TAG_LAGRANGE_ONLY);
			if (!lagrangeStatusOk) continue;

			boolean nebulaStatusOk = NEBULA_NONE.equals(nebulaType) || !terrainData.hasTag(TAG_NOT_IN_NEBULA);
			nebulaStatusOk &= !NEBULA_NONE.equals(nebulaType) || !terrainData.hasTag(TAG_REQUIRES_NEBULA);
			nebulaStatusOk &= systemType != StarSystemGenerator.StarSystemType.NEBULA || !terrainData.hasTag(TAG_NOT_NEBULA_UNLESS_MOON) || context.parent != null;
			if (!nebulaStatusOk) continue;

			float weight = terrainData.getFrequency();
			if (age != null) weight *= terrainData.getMultiplier(age);
			if (starType != null) weight *= terrainData.getMultiplier(starType);
			if (parentCategory != null) weight *= terrainData.getMultiplier(parentCategory);
			for (String col : context.multipliers) {
				weight *= terrainData.getMultiplier(col);
			}
			if (extraMult != null) weight *= terrainData.getMultiplier(extraMult);
			if (weight > 0) picker.add(terrainData, weight);
		}

		return picker;
	}

	protected void setDefaultLightColorBasedOnStars() {
		Color one = Color.white, two = null, three = null;

		switch (systemType) {
			case BINARY_FAR:
			case TRINARY_2FAR:
			case SINGLE:
			case NEBULA:
				one = pickLightColorForStar(star);
				break;
			case BINARY_CLOSE:
			case TRINARY_1CLOSE_1FAR:
				one = pickLightColorForStar(star);
				two = pickLightColorForStar(secondary);
				break;
			case TRINARY_2CLOSE:
				one = pickLightColorForStar(star);
				two = pickLightColorForStar(secondary);
				three = pickLightColorForStar(tertiary);
				break;
		}

		Color result = one;
		if (two != null && three == null) {
			result = Misc.interpolateColor(one, two, 0.5f);
		} else if (two != null && three != null) {
			result = Misc.interpolateColor(one, two, 0.5f);
			result = Misc.interpolateColor(result, three, 0.5f);
		}
		system.setLightColor(result); // light color in entire system, affects all entities
	}


	protected Color pickLightColorForStar(PlanetAPI star) {
		StarGenDataSpec starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, star.getSpec().getPlanetType(), false);
		Color min = starData.getLightColorMin();
		Color max = starData.getLightColorMax();
		Color lightColor = Misc.interpolateColor(min, max, random.nextFloat());
		return lightColor;
	}

	protected PlanetAPI addRandomStar(String id, String name) {
		PlanetSpecAPI starSpec = pickStar(constellationAge);
		if (starSpec == null) return null;

		StarGenDataSpec starData = (StarGenDataSpec) Global.getSettings().getSpec(StarGenDataSpec.class, starSpec.getPlanetType(), false);
		float radius = getRadius(starData.getMinRadius(), starData.getMaxRadius());

		float corona = radius * (starData.getCoronaMult() + starData.getCoronaVar() * (random.nextFloat() - 0.5f));
		if (corona < starData.getCoronaMin()) corona = starData.getCoronaMin();

		PlanetAPI star = system.addPlanet(id, // unique id for this star
				null,
				name,
				starSpec.getPlanetType(),  // id in planets.json
				0f,  // angle
				radius,          // radius (in pixels at default zoom)
				10000f, // orbit radius
				1000f // orbit days
		);

		system.addCorona(star, corona,  // corona radius, from star edge
				starData.getSolarWind(),
				starData.getMinFlare() + (starData.getMaxFlare() - starData.getMinFlare()) * random.nextFloat(),
				starData.getCrLossMult());

		return star;
	}


	public void init(StarSystemAPI system, StarAge age) {
		sector = Global.getSector();
		hyper = Global.getSector().getHyperspace();
		this.starAge = age;
		this.system = system;

	}

	protected boolean initSystem(String name, Vector2f loc) {
		sector = Global.getSector();
		system = sector.createStarSystem(name);
		system.setProcgen(true);
		system.setType(systemType);
		system.getLocation().set(loc);
		hyper = Global.getSector().getHyperspace();

		//system.setBackgroundTextureFilename("graphics/backgrounds/background2.jpg");
		system.setBackgroundTextureFilename(backgroundName);
		return true;
	}

	protected void updateAgeAfterPickingStar() {
		starAge = starData.getAge();
		if (starAge == StarAge.ANY) {
			starAge = constellationAge;
		}
		starAgeData = (AgeGenDataSpec) Global.getSettings().getSpec(AgeGenDataSpec.class, starAge.name(), true);
	}

	protected void cleanup() {
		if (system != null) {
			Global.getSector().removeStarSystem(system);
			system = null;
		}
	}

	public String getNebulaType() {
		return nebulaType;
	}

	public StarAge getConstellationAge() {
		return constellationAge;
	}

	public StarAge getStarAge() {
		return starAge;
	}


	public PlanetSpecAPI pickStar(StarAge age) {
		if (params != null && !params.starTypes.isEmpty()) {
			String id = params.starTypes.remove(0);
			for (PlanetSpecAPI spec : Global.getSettings().getAllPlanetSpecs()) {
				if (spec.getPlanetType().equals(id)) {
					Object test = Global.getSettings().getSpec(StarGenDataSpec.class, id, true);
					if (test == null) continue;
					StarGenDataSpec data = (StarGenDataSpec) test;
					boolean hasTag = data.hasTag(StarSystemGenerator.StarSystemType.NEBULA.name());
					boolean nebType = systemType == StarSystemGenerator.StarSystemType.NEBULA;
					boolean nebulaStatusOk = hasTag == nebType;
					if (nebulaStatusOk) {
						return spec;
					}
				}
			}
		}

		WeightedRandomPicker<PlanetSpecAPI> picker = new WeightedRandomPicker<PlanetSpecAPI>(random);
		for (PlanetSpecAPI spec : Global.getSettings().getAllPlanetSpecs()) {
			if (!spec.isStar()) continue;
			if (spec.isBlackHole() || spec.isPulsar()) continue;

			String id = spec.getPlanetType();
			Object test = Global.getSettings().getSpec(StarGenDataSpec.class, id, true);
			if (test == null) continue;
			StarGenDataSpec data = (StarGenDataSpec) test;

			boolean hasTag = data.hasTag(StarSystemGenerator.StarSystemType.NEBULA.name());
			boolean nebType = systemType == StarSystemGenerator.StarSystemType.NEBULA;
			boolean nebulaStatusOk = hasTag == nebType;

			if (!nebulaStatusOk) continue;


			float freq = 0f;
			switch (age) {
				case AVERAGE:
					freq = data.getFreqAVERAGE();
					break;
				case OLD:
					freq = data.getFreqOLD();
					break;
				case YOUNG:
					freq = data.getFreqYOUNG();
					break;
			}
			picker.add(spec, freq);
		}

		return picker.pick();
	}


	public static Color getColor(Color min, Color max) {
		Color color = new Color((int) (min.getRed() + (max.getRed() - min.getRed()) * random.nextDouble()),
				(int) (min.getGreen() + (max.getGreen() - min.getGreen()) * random.nextDouble()),
				(int) (min.getBlue() + (max.getBlue() - min.getBlue()) * random.nextDouble()),
				255);

		return color;
	}


	public static float getNormalRandom(float min, float max) {
		return getNormalRandom(random, min, max);
	}

	public static float getNormalRandom(Random random, float min, float max) {
		double r = random.nextGaussian();
		r *= 0.2f;
		r += 0.5f;
		if (r < 0) r = 0;
		if (r > 1) r = 1;

		// 70% chance 0.3 < r < .7
		// 95% chance 0.1 < r < .7
		// 99% chance 0 < r < 1
		return min + (float) r * (max - min);
	}

	public static float getRadius(float min, float max) {
		float radius = min + (max - min) * random.nextFloat();
		return radius;
	}

	public Map<SectorEntityToken, PlanetAPI> getLagrangeParentMap() {
		return lagrangeParentMap;
	}

	public Map<SectorEntityToken, List<SectorEntityToken>> getAllEntitiesAdded() {
		return allNameableEntitiesAdded;
	}

	//Built off of the bones of a script by Wisp for Persean Chronicles Questgiver Library.
	//Repurposed with permission
	/*public static List<Constellation> getConstellations(boolean preferUnvisited, boolean preferUntouched) {
		Vector2f startAtHyperspaceLocation = null;
		List<MarketAPI> markets = Global.getSector().getEconomy().getMarketsCopy();

		if (markets != null && !markets.isEmpty()) {
			MarketAPI firstMarket = markets.get(0);
			if (firstMarket != null) {
				startAtHyperspaceLocation = firstMarket.getLocationInHyperspace();
			}
		}
		if (startAtHyperspaceLocation == null) {
			//Should never ever happen. If this happens, literally every market in the sector is dead.
			startAtHyperspaceLocation = Global.getSector().getPlayerFleet().getLocationInHyperspace();
			log.info("JDP_RETROGEN_EVENTS: 		[CONTINGENCY] You killed them all. Didn't you?");
		}

		Vector2f finalStartAtHyperspaceLocation = startAtHyperspaceLocation;

		List<Constellation> constellations = (Global.getSector().getStarSystems().stream()
				.map(StarSystemAPI::getConstellation)
				.filter(Objects::nonNull)
				.distinct()
				.toList())
				.stream()
				.sorted(Comparator.comparingDouble(c -> -Misc.getDistanceLY(c.getLocation(), finalStartAtHyperspaceLocation)))
				.collect(Collectors.toList());

		if (preferUnvisited) {
			constellations = constellations.stream()
					.filter(c -> c.getSystems().stream().allMatch(s -> s.getLastPlayerVisitTimestamp() == 0L))
					.toList();
		}
		if (preferUntouched) {
			constellations = constellations.stream()
					.filter(c -> c.getSystems().stream().noneMatch(s -> s.hasTag("$jdp_postgenSystem")))
					.toList();
		}

		return constellations;
	}*/

	public static List<Constellation> getConstellations(int degreesOfFreedom) {
		List<Constellation> constellations = (Global.getSector().getStarSystems().stream()
				.map(StarSystemAPI::getConstellation)
				.filter(Objects::nonNull)
				.distinct()
				.toList())
				.stream()
				.sorted(Comparator.comparingDouble(c -> -Misc.getDistanceLY(c.getLocation(), new Vector2f(0, 0))))
				.collect(Collectors.toList());

		if (degreesOfFreedom == 0  || degreesOfFreedom == 1) {
			constellations = constellations.stream()
					.filter(c -> c.getSystems().stream().allMatch(s -> s.getLastPlayerVisitTimestamp() == 0L))
					.toList();
		}
		if (degreesOfFreedom == 0) {
			constellations = constellations.stream()
					.filter(c -> c.getSystems().stream().noneMatch(s -> s.hasTag("$jdp_postgenSystem")))
					.toList();
		}

		return constellations;
	}

	public static boolean doCirclesIntersect(Vector2f centerA, float radiusA, Vector2f centerB, float radiusB) {
		return Math.hypot(centerA.x - centerB.x, centerA.y - centerB.y) <= (radiusA + radiusB);
	}

	//Built off of the bones of a script by Wisp for Persean Chronicles Questgiver Library.
	//Repurposed with permission
	public static boolean placeInSector(StarSystemAPI newSystem) {
		int degreesOfFreedom = 0;
		List<Constellation> constellations = getConstellations(degreesOfFreedom);

		while (constellations.isEmpty()) {
			degreesOfFreedom++;
			constellations = getConstellations(degreesOfFreedom);

			if (degreesOfFreedom == 3) {
				log.warn("JDP_RETROGEN_EVENTS: 		[CONTINGENCY] No valid constellations found at placeInSector");
				return false;
			}
		}

		//Average system size
		List<Float> radii = Global.getSector().getStarSystems().stream().filter(system -> !(system.hasTag(Tags.THEME_HIDDEN) || system.hasTag(Tags.THEME_SPECIAL))).map(StarSystemAPI::getMaxRadiusInHyperspace).toList();
		float sum = 0f;
		for (Float radius : radii) {
			sum = sum + radius;
		}
		float average = sum / radii.size();

		for (Constellation constellation : constellations) {
			List<Float> xCoords = constellation.getSystems().stream()
					.map(StarSystemAPI::getLocation)
					.map(Vector2f::getX)
					.toList();
			List<Float> yCoords = constellation.getSystems().stream()
					.map(StarSystemAPI::getLocation)
					.map(Vector2f::getY)
					.toList();

			float minX = xCoords.stream().min(Float::compareTo).orElseThrow();
			float maxX = xCoords.stream().max(Float::compareTo).orElseThrow();
			float lowerBoundX = minX - 500;
			float upperBoundX = maxX + 500;

			float minY = yCoords.stream().min(Float::compareTo).orElseThrow();
			float maxY = yCoords.stream().max(Float::compareTo).orElseThrow();
			float lowerBoundY = minY - 500;
			float upperBoundY = maxY + 500;

			for (int i = 0; i < 150000; i++) {
				Vector2f point = new Vector2f(
						Math.round((Math.random() * (upperBoundX - lowerBoundX)) + lowerBoundX),
						Math.round((Math.random() * (upperBoundY - lowerBoundY)) + lowerBoundY)
				);

				boolean doesPointIntersectWithAnySystems = constellation.getSystems().stream()
						.anyMatch(system -> doCirclesIntersect(point, average, system.getLocation(), system.getMaxRadiusInHyperspace() + 50));

				if (!doesPointIntersectWithAnySystems && !isPointNearCore(point)) {
					newSystem.setConstellation(constellation);
					constellation.getSystems().add(newSystem);
					newSystem.getLocation().set(point);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean shuffleLocation(StarSystemAPI newSystem, Boolean joinConstellation, Float definedMinX, Float definedMaxX, Float definedMinY, Float definedMaxY) {
		//Will attempt to join an existing constellation, and will prefer one that matches the age and type of the system
		//Can handle null defined bounds
		if (joinConstellation) {
			StarAge age;
			Constellation.ConstellationType type;
			Constellation systemConstellation = newSystem.getConstellation();

			if (systemConstellation != null) {
				age = newSystem.getConstellation().getAge();
				type = newSystem.getConstellation().getType();
			} else {
				age = null;
				type = null;
			}

			int degreesOfFreedom = 0;
			List<Constellation> constellations = getConstellations(degreesOfFreedom);
			if (!(age == null || type == null)) {
				constellations = constellations.stream()
						.filter(c -> c.getSystems().stream().allMatch(s -> s.getConstellation().getAge() == age && s.getConstellation().getType() == type))
						.toList();
			}

			while (constellations.isEmpty()) {
				degreesOfFreedom++;
				constellations = getConstellations(degreesOfFreedom);
				if (degreesOfFreedom <= 2) {
					if (!(age == null || type == null)) {
						constellations = constellations.stream()
								.filter(c -> c.getSystems().stream().allMatch(s -> s.getConstellation().getAge() == age && s.getConstellation().getType() == type))
								.toList();
					}
				}
				if (degreesOfFreedom == 4) {
					log.warn("JDP_RETROGEN_EVENTS: 		[CONTINGENCY] No valid constellations found at shuffleLocation");
					return false;
				}
			}

			for (Constellation constellation : constellations) {
				List<Float> xCoords = constellation.getSystems().stream()
						.map(StarSystemAPI::getLocation)
						.map(Vector2f::getX)
						.toList();
				List<Float> yCoords = constellation.getSystems().stream()
						.map(StarSystemAPI::getLocation)
						.map(Vector2f::getY)
						.toList();

				float minX = xCoords.stream().min(Float::compareTo).orElseThrow();
				float maxX = xCoords.stream().max(Float::compareTo).orElseThrow();
				float lowerBoundX = minX - 500;
				float upperBoundX = maxX + 500;

				float minY = yCoords.stream().min(Float::compareTo).orElseThrow();
				float maxY = yCoords.stream().max(Float::compareTo).orElseThrow();
				float lowerBoundY = minY - 500;
				float upperBoundY = maxY + 500;

				for (int i = 0; i < 15000; i++) {
					Vector2f point = new Vector2f(
							Math.round((Math.random() * (upperBoundX - lowerBoundX)) + lowerBoundX),
							Math.round((Math.random() * (upperBoundY - lowerBoundY)) + lowerBoundY)
					);

					boolean doesPointIntersectWithAnySystems = constellation.getSystems().stream()
							.anyMatch(system -> doCirclesIntersect(point, newSystem.getMaxRadiusInHyperspace() + 50, system.getLocation(), system.getMaxRadiusInHyperspace() + 50));

					if (!doesPointIntersectWithAnySystems && !isPointNearCore(point) && isPointWithinBounds(point, definedMinX, definedMaxX, definedMinY, definedMaxY)) {
						newSystem.setConstellation(constellation);
						constellation.getSystems().add(newSystem);
						newSystem.getLocation().set(point);
						log.info("JDP_RETROGEN_EVENTS: 		[CONTINGENCY] SHUFFLE SUCCESS (JOIN CONSTELLATION TRUE)");

						return true;
					}
				}
			}
			//Will not attempt to join an existing constellation, placed somewhere within the bounds defined
			//Can handle null defined bounds
		} else {
			for (int i = 0; i < 15000; i++) {
				Vector2f point = new Vector2f(
						Math.round((Math.random() * (definedMaxX - definedMinX)) + definedMinX),
						Math.round((Math.random() * (definedMaxY - definedMinY)) + definedMinY)
				);

				boolean doesPointIntersectWithAnySystems = Global.getSector().getStarSystems().stream()
						.anyMatch(system -> doCirclesIntersect(point, newSystem.getMaxRadiusInHyperspace() + 50, system.getLocation(), system.getMaxRadiusInHyperspace() + 50));

				if (!doesPointIntersectWithAnySystems) {
					newSystem.getLocation().set(point);
					log.info("JDP_RETROGEN_EVENTS: 		[CONTINGENCY] SHUFFLE SUCCESS (JOIN CONSTELLATION FALSE)");

					return true;
				}
			}
		}
		//Backup, will attempt to move the system to somewhere within the sector. Has a small chance to end up in the abyss.
		//Can handle null defined bounds
		for (int i = 0; i < 150000; i++) {
			Vector2f point = new Vector2f(
					Math.round((Math.random() * (78000f + 78000f)) - 78000f),
					Math.round((Math.random() * (48000f + 48000f)) - 48000f)
			);

			boolean doesPointIntersectWithAnySystems = Global.getSector().getStarSystems().stream()
					.anyMatch(system -> doCirclesIntersect(point, newSystem.getMaxRadiusInHyperspace() + 50, system.getLocation(), system.getMaxRadiusInHyperspace() + 50));

			if (!doesPointIntersectWithAnySystems && !isPointNearCore(point) && isPointWithinBounds(point, definedMinX, definedMaxX, definedMinY, definedMaxY)) {
				newSystem.getLocation().set(point);
				log.info("JDP_RETROGEN_EVENTS: 		[CONTINGENCY] SHUFFLE SUCCESS (LAST RESORT SHUFFLE)");

				return true;
			}
		}
		log.info("JDP_RETROGEN_EVENTS: 		[CONTINGENCY] FAILED TO SHUFFLE");

		return false;
	}

	public static boolean randomiseNames(StarSystemAPI system, Boolean randomiseStar) {
		if (randomiseStar.equals(true)) {
			//Nebula
			if (system.isNebula()) {
				String name = ProcgenUsedNames.pickName(NameGenData.TAG_NEBULA, null, null).nameWithRomanSuffixIfAny;
				if (name == "Nebula")
					name = ProcgenUsedNames.pickName(NameGenData.TAG_NEBULA, null, null).nameWithRomanSuffixIfAny;
				system.setName(name + " Nebula");
				system.getStar().setName(name);
				system.setOptionalUniqueId(name + " Nebula");
				//Multi Star
			} else if (system.getType().equals(StarSystemType.BINARY_FAR)
					|| system.getType().equals(StarSystemType.BINARY_CLOSE)
					|| system.getType().equals(StarSystemType.TRINARY_2CLOSE)
					|| system.getType().equals(StarSystemType.TRINARY_2FAR)
					|| system.getType().equals(StarSystemType.TRINARY_1CLOSE_1FAR)) {

				String name = ProcgenUsedNames.pickName(NameGenData.TAG_STAR, null, null).nameWithRomanSuffixIfAny;
				for (SectorEntityToken entity : system.getPlanets()) {
					if (entity.isStar()) {
						entity.setName(ProcgenUsedNames.pickName(NameGenData.TAG_STAR, null, null).nameWithRomanSuffixIfAny);
					}
				}
				system.setName(name + " Star System");
				system.getStar().setName(name);
				system.setBaseName(name);
				system.setOptionalUniqueId(name + " Star System");

				//Single Star
			} else {
				String name = ProcgenUsedNames.pickName(NameGenData.TAG_STAR, null, null).nameWithRomanSuffixIfAny;
				system.setName(name + " Star System");
				system.getStar().setName(name);
				system.setOptionalUniqueId(name + " Star System");
			}
		}

		for (SectorEntityToken entity : system.getPlanets()) {
			String parent = null;
			String nameType = NameGenData.TAG_PLANET;

			if (entity.getOrbitFocus() != null) parent = entity.getOrbitFocus().getName();

			if (((PlanetAPI) entity).isMoon()) nameType = NameGenData.TAG_MOON;
			else if (((PlanetAPI) entity).isStar()) continue;

			String name = (ProcgenUsedNames.pickName(nameType, parent, null).nameWithRomanSuffixIfAny);

			if (entity.getMarket() != null) entity.getMarket().setName(name);
			entity.setName(name);
		}
		return true;
	}

	public static boolean isPointNearCore(Vector2f point) {
		return (((point.x >= -24000) && (point.x <= 12000)) && ((point.y >= -18000) && (point.y <= 12000)));
	}

	public static boolean isPointWithinBounds(Vector2f point, Float definedMinX, Float definedMaxX, Float definedMinY, Float definedMaxY) {
		return (((definedMinX == null || point.x >= definedMinX) && (definedMaxX == null || point.x <= definedMaxX)) &&
				((definedMinY == null || point.y >= definedMinY) && (definedMaxY == null || point.y <= definedMaxY)));
	}

	public static Vector2f findSpace(StarSystemAPI system, SectorEntityToken newEntity) {
		List<Float> xCoords = system.getAllEntities().stream()
				.map(SectorEntityToken::getLocation)
				.map(Vector2f::getX)
				.toList();

		List<Float> yCoords = system.getAllEntities().stream()
				.map(SectorEntityToken::getLocation)
				.map(Vector2f::getY)
				.toList();

		float lowerBoundX = xCoords.stream().min(Float::compareTo).orElseThrow();
		float upperBoundX = xCoords.stream().max(Float::compareTo).orElseThrow();

		float lowerBoundY = yCoords.stream().min(Float::compareTo).orElseThrow();
		float upperBoundY = yCoords.stream().max(Float::compareTo).orElseThrow();

		for (int i = 0; i < 15000; i++) {
			Vector2f point = new Vector2f(
					Math.round((Math.random() * (upperBoundX - lowerBoundX)) + lowerBoundX),
					Math.round((Math.random() * (upperBoundY - lowerBoundY)) + lowerBoundY)
			);

			boolean doesPointIntersectWithAnyEntities = system.getAllEntities().stream()
					.anyMatch(entity -> doCirclesIntersect(point, newEntity.getRadius() + 50, entity.getLocation(), entity.getRadius() + 50));

			if (!doesPointIntersectWithAnyEntities) {
				return point;
			}
		}
		return new Vector2f(20000,20000);
	}
}

