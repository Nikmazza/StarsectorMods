package com.fs.starfarer.api.impl.campaign.procgen.themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CustomEntitySpecAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.CoronalTapParticleScript;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictShipData;
import com.fs.starfarer.api.impl.campaign.econ.impl.PlanetaryShield;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.DefenderDataOverride;
import com.fs.starfarer.api.impl.campaign.procgen.NameGenData;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetGenDataSpec;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames.NamePick;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator.StarSystemType;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner.ShipRecoverySpecialCreator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner.SpecialCreationContext;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.PerShipData;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;


public class II_BG_derelict extends BaseThemeGenerator {

	static {
		SectorThemeGenerator.generators.add(new II_BG_derelict());
	}

	public String getThemeId() {
		return Themes.MISC;
	}


	@Override
	public int getOrder() {
		return 1000001;
	}

	@Override
	public void generateForSector(ThemeGenContext context, float allowedUnusedFraction) {



		addDerelicts(context, "invictus_ii_Standard", 1, 2, 0, 1, Tags.THEME_REMNANT);
		addDerelicts(context, "heron_ii_Support", 1, 3, 0, 3, Tags.THEME_REMNANT);
		addDerelicts(context, "hammerhead_ii_Standard", 1, 4, 1, 5, Tags.THEME_REMNANT);
		addDerelicts(context, "eagle_ii_Elite", 1, 3, 0, 3, Tags.THEME_REMNANT);
		addDerelicts(context, "conquest_ii_Elite", 1, 3, 0, 2, Tags.THEME_REMNANT);
		addDerelicts(context, "brawler_ii_Artillery", 1, 6, 1, 8, Tags.THEME_REMNANT);
		addDerelicts(context, "champion_ii_Elite", 1, 2, 0, 3, Tags.THEME_REMNANT);

	}

	
	protected void addDerelicts(ThemeGenContext context, String variant,
					int minNonSalvageable, int maxNonSalvageable,
					int minSalvageable, int maxSalvageable,
					String ... allowedThemes) {
		if (Global.getSettings().getVariant(variant) != null) {
			if (DEBUG) System.out.println("Adding " + variant + " to star systems");

			Set<String> tags = new HashSet<String>(Arrays.asList(allowedThemes));

			int numSalvageable = minSalvageable + random.nextInt(maxSalvageable - minSalvageable + 1);
			int numNonSalvageable = minNonSalvageable + random.nextInt(maxNonSalvageable - minNonSalvageable + 1);

			List<Constellation> list = new ArrayList<Constellation>(context.constellations);
			Collections.shuffle(list, random);

			List<StarSystemData> systems = new ArrayList<StarSystemData>();
			for (Constellation c : list) {
				for (StarSystemAPI system : c.getSystems()) {
					StarSystemData data = computeSystemData(system);
					systems.add(data);
				}
			}

			Collections.shuffle(systems, random);
			for (StarSystemData data  : systems) {
				boolean matches = false;
				for (String tag : data.system.getTags()) {
					if (tags.contains(tag)) {
						matches = true;
						break;
					}
				}
				if (!matches) continue;

				EntityLocation loc = pickAnyLocation(random, data.system, 70f, null);
				AddedEntity ae = addDerelictShip(data, loc, variant);
				if (ae != null) {
					if (numSalvageable > 0) {
						numSalvageable--;
						ShipRecoverySpecialCreator creator = new ShipRecoverySpecialCreator(random, 0, 0, false, null, null);
						Object specialData = creator.createSpecial(ae.entity, new SpecialCreationContext());
						if (specialData != null) {
							Misc.setSalvageSpecial(ae.entity, specialData);
						}
					} else {
						numNonSalvageable--;
						SalvageSpecialAssigner.assignSpecials(ae.entity, true);
					}
					if (DEBUG) System.out.println("      Added " + variant + " to " + data.system + "\n");
				}
				if (numSalvageable + numNonSalvageable <= 0) break;
			}
			//if (numSalvageable + numNonSalvageable <= 0) break;

			if (DEBUG) System.out.println("Finished adding " + variant + " to star systems\n\n\n\n\n");
		}
	}



}
















