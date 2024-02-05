package data.world.systems;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static data.world.SSPWorldGen.addMarketplace;


public class SSP_Scarborough {
	public void generate(SectorAPI sector) {
		//create a star system
	StarSystemAPI system = sector.createStarSystem("Scarborough");
		//set its location
	system.getLocation().set(-20500f, -5500f);
		//set background image
	system.setBackgroundTextureFilename("graphics/backgrounds/background6.jpg");
		//the star
	PlanetAPI Scarborough_Star = system.initStar("Scarborough", // unique id for this star
				"star_white",  // id in planets.json
				150f, 		  // radius (in pixels at default zoom)
				180, // corona
				3f, // solar wind burn level
				0.05f, // flare probability
				1f); // CR loss multiplier, good values are in the range of 1-5
		//background light color
	system.setLightColor(new Color(185, 185, 180));
	//Mirage_Star AsteroidBelt
	system.addAsteroidBelt(Scarborough_Star, 250, 3200, 400, 200, 250, Terrain.ASTEROID_BELT, "Asteroid Belt");
	system.addRingBand(Scarborough_Star, "misc", "rings_asteroids0", 256f, 0, Color.white, 256f, 3200, 226f, null, null);
		// build the MirageCity
	SectorEntityToken MirageCity = system.addCustomEntity("MirageCity", "Mirage Astropolis", "station_side07", "LLI");
		//make a custom description which is specified in descriptions.csv
		MirageCity.setCustomDescriptionId("MirageCity");
		MirageCity.setInteractionImage("illustrations", "cargo_loading");
		//location
		MirageCity.setCircularOrbitWithSpin(Scarborough_Star, 0, 1300, 160, 2, 4);
		//a new market
		MarketAPI MirageCityMarket = addMarketplace("LLI", MirageCity, null
				, MirageCity.getName(), 5,
				new ArrayList<>(
						Arrays.asList(
								Conditions.POPULATION_5 // population
						)),
				new ArrayList<>(
						Arrays.asList(
								Submarkets.GENERIC_MILITARY,
								Submarkets.SUBMARKET_BLACK,
								Submarkets.SUBMARKET_OPEN,
								Submarkets.SUBMARKET_STORAGE
						)),
				new ArrayList<>(
						Arrays.asList(
								Industries.POPULATION,
								Industries.MEGAPORT,
								Industries.STARFORTRESS_MID,
								Industries.LIGHTINDUSTRY,
								Industries.MILITARYBASE,
								Industries.WAYSTATION,
								Industries.HEAVYBATTERIES
						)),
				0.3f,
				true,
				true);
		// build the AdeniumObesum
		SectorEntityToken AdeniumObesum = system.addPlanet("AdeniumObesum", Scarborough_Star,"Adenium Obesum","barren",125, 160f, 2100f, 410f);
		//make a custom description which is specified in descriptions.csv
		AdeniumObesum.setCustomDescriptionId("AdeniumObesum");
		AdeniumObesum.setInteractionImage("illustrations", "hound_hangar");
		//a new market
		MarketAPI AdeniumObesumMarket = addMarketplace("LLI", AdeniumObesum, null
				, AdeniumObesum.getName(), 7,
				new ArrayList<>(
						Arrays.asList(
								Conditions.POPULATION_7, // population
								Conditions.NO_ATMOSPHERE,
								Conditions.LOW_GRAVITY,
								Conditions.RARE_ORE_ABUNDANT,
								Conditions.ORE_SPARSE,
								Conditions.RUINS_SCATTERED
						)),
				new ArrayList<>(
						Arrays.asList(
								Submarkets.GENERIC_MILITARY,
								Submarkets.SUBMARKET_BLACK,
								Submarkets.SUBMARKET_OPEN,
								Submarkets.SUBMARKET_STORAGE
						)),
				new ArrayList<>(
						Arrays.asList(
								Industries.POPULATION,
								Industries.SPACEPORT,
								Industries.STARFORTRESS_MID,
								Industries.HIGHCOMMAND,
								Industries.ORBITALWORKS,
								Industries.REFINING,
								Industries.MINING,
								Industries.HEAVYBATTERIES,
								"ssp_guardian"
						)),
				0.3f,
				false,
				false);
	SectorEntityToken Dust_GasGiant = system.addPlanet("Dust",Scarborough_Star,"Dust","gas_giant", 230, 350, 5500, 250);
		//Dust_GasGiant AsteroidBelt
		system.addAsteroidBelt(Dust_GasGiant, 50, 550, 140, 100, 150, Terrain.ASTEROID_BELT, "Asteroid Belt");
		system.addRingBand(Dust_GasGiant, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 550, 226f, null, null);
		//Dust_I
		SectorEntityToken Dust_I = system.addPlanet("Dust I",Dust_GasGiant,"Dust I","frozen3",45, 80, 800, 25);
		Dust_I.setCustomDescriptionId("Dust_I");
		Dust_I.setInteractionImage("illustrations", "hound_hangar");
		//a new market
		MarketAPI Dust_IMarket = addMarketplace("LLI", Dust_I, null
				, Dust_I.getName(), 3,
				new ArrayList<>(
						Arrays.asList(
								Conditions.POPULATION_3, // population
								Conditions.LOW_GRAVITY,
								Conditions.VERY_COLD,
								Conditions.EXTREME_WEATHER,
								Conditions.VOLATILES_DIFFUSE,
								Conditions.RARE_ORE_ULTRARICH
						)),
				new ArrayList<>(
						Arrays.asList(
								Submarkets.SUBMARKET_BLACK,
								Submarkets.SUBMARKET_OPEN,
								Submarkets.SUBMARKET_STORAGE
						)),
				new ArrayList<>(
						Arrays.asList(
								Industries.POPULATION,
								Industries.SPACEPORT,
								Industries.MINING,
								Industries.GROUNDDEFENSES

						)),
				0.3f,
				false,
				false);
	SectorEntityToken Dust_II = system.addPlanet("Dust II",Dust_GasGiant,"Dust II","tundra",110, 120, 1400, 45);
		Dust_II.setCustomDescriptionId("Dust_II");
		Dust_II.setInteractionImage("illustrations", "hound_hangar");
		//a new market
		MarketAPI Dust_IIMarket = addMarketplace("luddic_church", Dust_II, null
				, Dust_II.getName(), 4,
				new ArrayList<>(
						Arrays.asList(
								Conditions.POPULATION_4, // population
								Conditions.COLD,
								Conditions.MILD_CLIMATE,
								Conditions.FARMLAND_RICH,
								Conditions.ORGANICS_COMMON,
								Conditions.HABITABLE
						)),
				new ArrayList<>(
						Arrays.asList(
								Submarkets.SUBMARKET_BLACK,
								Submarkets.SUBMARKET_OPEN,
								Submarkets.SUBMARKET_STORAGE
						)),
				new ArrayList<>(
						Arrays.asList(
								Industries.POPULATION,
								Industries.SPACEPORT,
								Industries.FARMING

						)),
				0.3f,
				false,
				false);
	//add specal items
	//LLIRoseMarket.getIndustry(Industries.REFINING).setSpecialItem(new SpecialItemData(Items.CATALYTIC_CORE, null));
	//LLIRoseMarket.getIndustry(Industries.MINING).setSpecialItem(new SpecialItemData(Items.MANTLE_BORE, null));
	//Add a relay
	SectorEntityToken Mirage_relay = system.addCustomEntity("Mirage_relay", // unique id
				"Comm Relay Alpha-14-Gen Five", // name - if null, defaultName from custom_entities.json will be used
				"comm_relay", // type of object, defined in custom_entities.json
				"LLI"); // faction
		Mirage_relay.setCircularOrbitPointingDown( Scarborough_Star, 0 + 30, 1900, 220);
	// Mirage Jump-point,Inside the system
	JumpPointAPI jumpPoint_Inside = Global.getFactory().createJumpPoint("Mirage_jump", "Mirage Jump-point");
	jumpPoint_Inside.setCircularOrbit(Scarborough_Star, 130 - 60, 1600, 250);
	jumpPoint_Inside.setStandardWormholeToHyperspaceVisual();
	system.addEntity(jumpPoint_Inside);
	// generates hyperspace destinations for in-system jump points
	system.autogenerateHyperspaceJumpPoints(true, true);
	}
	//Learning from Tart scripts
	//Clean nearby Nebula(nearby system)
//	private void cleanup(StarSystemAPI system) {
//		HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
//		NebulaEditor editor = new NebulaEditor(plugin);
//		float minRadius = plugin.getTileSize() * 2f;

//		float radius = system.getMaxRadiusInHyperspace();
//		editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0, 360f);
//		editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);
//	}
}
