package data.campaign;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.themes.*;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner.ShipRecoverySpecialCreator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner.SpecialCreationContext;

import java.util.*;

public class NES_DerelictSpawnScript extends BaseThemeGenerator {

	public static final Logger LOGGER = Global.getLogger(data.campaign.NES_DerelictSpawnScript.class);

	public String getThemeId() {
		return Themes.MISC;
	}

	@Override
	public int getOrder() {
		return 1000000;
	}

	@Override
	public void generateForSector(ThemeGenContext context, float allowedUnusedFraction) {

		addDerelicts(context, "nes_fluorspar_d_Luxury", 2, 3, 1, 2, Tags.THEME_REMNANT);
		addDerelicts(context, "nes_hampter_hero_Strike", 0, 0, 1, 1, Tags.THEME_REMNANT);
		//addDerelicts(context, "revenant_Elite", 1, 2, 0, 1, Tags.THEME_REMNANT, Tags.THEME_RUINS, Tags.THEME_DERELICT, Tags.THEME_UNSAFE);

	}
	protected void addDerelicts(ThemeGenContext context, String variant,
								int minNonSalvageable, int maxNonSalvageable,
								int minSalvageable, int maxSalvageable,
								String ... allowedThemes) {
		if (Global.getSettings().getVariant(variant) != null) {
			LOGGER.info("Adding Nes teaser " + variant + " to star systems");

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
					LOGGER.info("      Added Nes teaser " + variant + " to " + data.system + "\n");
				}
				if (numSalvageable + numNonSalvageable <= 0) break;
			}
			LOGGER.info("Finished adding Nes teaser " + variant + " to star systems\n\n\n\n\n");
		}
	}
}