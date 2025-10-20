package data.hullmods.vice;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class FeedbackHandler extends BaseHullMod {
	
	private static String FEEDBACK_MOD = "vice_feedback_error";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		int feedbackCount = 0;
		ShipVariantAPI var = stats.getVariant();
		List<String> weaponSlots = var.getNonBuiltInWeaponSlots();
		
		/**
		if ((var.hasHullMod("ix_intrepid_sbe") || var.hasHullMod("ix_radiant_sbe")) 
					&& !var.hasHullMod("ix_advanced_ecm_suite")) feedbackCount++;
		**/
		
		for (String slot : weaponSlots) {
			if (var.getWeaponSpec(slot).hasTag("feedback_weapon")) feedbackCount++;
		}
		
		float maxCount = 1; //XO skill Reactor Monitoring increase max feedback weapon per ship by 1
		if (Global.getSector().getMemoryWithoutUpdate().is("$xo_reactor_monitoring_is_active", true)) maxCount++;
		
		if (feedbackCount <= maxCount) {
			var.getHullMods().remove(FEEDBACK_MOD); 
			if (feedbackCount < 1) var.getHullMods().remove(id); //delete handler if no feedback weapon present
		} 
		else var.addMod(FEEDBACK_MOD); //dummy mod used by weapon scripts to check for feedback
	}
}