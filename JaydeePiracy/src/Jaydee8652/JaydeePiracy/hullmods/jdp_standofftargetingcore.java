package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import org.magiclib.hullmods.MagicIncompatibleWarning;
import org.magiclib.util.MagicIncompatibleHullmods;

public class jdp_standofftargetingcore extends BaseHullMod {

	public static float RANGE_BONUS = 100f;
	public static float PD_MINUS = 40f;
	public static float VISION_BONUS = 1000f;
	public static float AUTOFIRE_AIM = 0.5f;
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
		stats.getNonBeamPDWeaponRangeBonus().modifyPercent(id, -PD_MINUS);		
		
		stats.getSightRadiusMod().modifyFlat(id, VISION_BONUS);
		stats.getAutofireAimAccuracy().modifyFlat(id, AUTOFIRE_AIM);

		//Incompatibilities
		if(stats.getVariant().getHullMods().contains("dedicated_targeting_core")){
			//if someone tries to install dedicated_targeting_core, remove it
			MagicIncompatibleHullmods.removeHullmodWithWarning(
				stats.getVariant(),
				"dedicated_targeting_core",
				"jdp_standofftargetingcore"
				);
		}
		if(stats.getVariant().getHullMods().contains("targetingunit")){
			//if someone tries to install targetingunit, remove it
			MagicIncompatibleHullmods.removeHullmodWithWarning(
				stats.getVariant(),
				"targetingunit",
				"jdp_standofftargetingcore"
				);
		}
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return Math.round(AUTOFIRE_AIM) + " standard unit";
        if (index == 1) return Math.round(RANGE_BONUS) + "%";
		if (index == 2) return Math.round(RANGE_BONUS-PD_MINUS) + "%";
		if (index == 3) return "" + (int)Math.round(VISION_BONUS);
        return null;
    }
}
