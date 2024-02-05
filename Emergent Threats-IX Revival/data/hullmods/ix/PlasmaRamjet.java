package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class PlasmaRamjet extends BaseHullMod {

	//dummy hullmod, enhancements done with ship system stats
	private static float DURATION_INCREASE = 33f;
	private static float SPEED_INCREASE = 20;
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) DURATION_INCREASE + "%";
		if (index == 1) return "" + (int) SPEED_INCREASE + "%";
		return null;
	}
}