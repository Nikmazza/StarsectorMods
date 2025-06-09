package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class CompactAutomationError extends BaseHullMod {

	public static float CR_PENALTY = 100f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxCombatReadiness().modifyFlat(id, -CR_PENALTY * 0.01f, "Equipment malfunction");
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Synthesis";
		if (index == 1) return "Compact Automation";
		if (index == 2) return "-" + (int) CR_PENALTY + "%";
		return null;
	}
}