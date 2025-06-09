package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class CompactAutomationDisplay extends BaseHullMod {
	
	//dummy hullmod, effect is negative OP cost of hidden version
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		int bonus = 0;
		if (hullSize == HullSize.FRIGATE) bonus = 3;
		else if (hullSize == HullSize.DESTROYER) bonus = 5;
		else if (hullSize == HullSize.CRUISER) bonus = 7;
		else if (hullSize == HullSize.CAPITAL_SHIP) bonus = 10;
		
		if (index == 0) return "" + bonus;
		if (index == 1) return "major malfunctions";
		return null;
	}
}