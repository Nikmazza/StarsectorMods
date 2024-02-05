package birdwanderer.birdscollection.data.scripts.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import org.magiclib.util.MagicIncompatibleHullmods;

public class BcFaysTargetingCore extends BaseHullMod {

	public static final float RANGE_BONUS = 70f;
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (RANGE_BONUS + "%");
		return null;
	}

	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
		stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);

		if(stats.getVariant().getHullMods().contains("targetingunit")){
			//if someone tries to install heavy armor, remove it
			MagicIncompatibleHullmods.removeHullmodWithWarning(
					stats.getVariant(),
					"targetingunit",
					"bc_fays_targeting_core"
			);
		}
		if(stats.getVariant().getHullMods().contains("dedicated_targeting_core")){
			//if someone tries to install heavy armor, remove it
			MagicIncompatibleHullmods.removeHullmodWithWarning(
					stats.getVariant(),
					"dedicated_targeting_core",
					"bc_fays_targeting_core"
			);
		}
	}

	
}
