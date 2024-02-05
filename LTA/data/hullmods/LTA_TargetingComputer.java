package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import org.magiclib.util.MagicIncompatibleHullmods;

public class LTA_TargetingComputer extends BaseHullMod {

//	public static final float BONUS = 100f;
//	
//	public String getDescriptionParam(int index, HullSize hullSize) {
//		if (index == 0) return "" + (int)BONUS + "%";
//		return null;
//	}
//	
//	
//	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
//		stats.getBallisticWeaponRangeBonus().modifyPercent(id, BONUS);
//		stats.getEnergyWeaponRangeBonus().modifyPercent(id, BONUS);
//	}




	//ADDED TO FIX INCOMPATABILITY USING MAGICLIB
   // public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {






	//@Override
	//public boolean isApplicableToShip(ShipAPI ship) {
	//return !ship.getVariant().getHullMods().contains("dedicated_targeting_core") && !ship.getVariant().getHullMods().contains("targetingunit") && //!ship.getVariant().getHullMods().contains("advancedcore");
	
	//
	
	//}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains("dedicated_targeting_core")) {
			return "Incompatible with Dedicated Targeting Core";
		}
		if (ship.getVariant().getHullMods().contains("targetingunit")) {
			return "Incompatible with Integrated Targeting Unit";
		}
		if (ship.getVariant().getHullMods().contains("advancedcore")) {
			return "Incompatible with Advanced Targeting Core";
		}
		if (ship.getVariant().getHullMods().contains("frontshield")) {
			return "Incompatible with Makeshift Shield Generator";
		}
		return null;
	}

	
	public static float RANGE_BONUS = 37.5f;
	//public static float PD_MINUS = 0f;
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int)Math.round(RANGE_BONUS) + "%";
		if (index == 1) return "not compatible with Makeshift Shield Generator.";
		//if (index == 1) return "" + (int)Math.round(RANGE_BONUS - PD_MINUS) + "%";
		//if (index == 0) return "" + (int)RANGE_THRESHOLD;
		//if (index == 1) return "" + (int)((RANGE_MULT - 1f) * 100f);
		//if (index == 1) return "" + new Float(VISION_BONUS).intValue();
		return null;
	}
	


	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
			stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
			stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
		
			//stats.getNonBeamPDWeaponRangeBonus().modifyPercent(id, -PD_MINUS);
			//stats.getBeamPDWeaponRangeBonus().modifyPercent(id, -PD_MINUS);
		
			if(stats.getVariant().getHullMods().contains("dedicated_targeting_core")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "dedicated_targeting_core", "reclaimedadvancedcore");
			}

			if(stats.getVariant().getHullMods().contains("targetingunit")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "targetingunit", "LTA_TargetingComputer");
			}
	
			if(stats.getVariant().getHullMods().contains("advancedcore")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "advancedcore", "LTA_TargetingComputer");
			}
			
			if(stats.getVariant().getHullMods().contains("TSC_converted_hangar")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "TSC_converted_hangar", "LTA_TargetingComputer");
			}
			
			if(stats.getVariant().getHullMods().contains("TSC_ModularHangar")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "TSC_ModularHangar", "LTA_TargetingComputer");
			}
			
			if(stats.getVariant().getHullMods().contains("TSC_BuiltInHangar")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "TSC_BuiltInHangar", "LTA_TargetingComputer");
			}
			if(stats.getVariant().getHullMods().contains("frontshield")){
			MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), "frontshield", "LTA_TargetingComputer");
			}
		}
}
