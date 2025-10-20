package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class SupercruiseEngines extends BaseHullMod {
	
	private static int BURN_LEVEL_BONUS = 1;
	private static float ZERO_FLUX_LEVEL = 15f;
		
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
		stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, ZERO_FLUX_LEVEL * 0.01f);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "+" + (int) BURN_LEVEL_BONUS;
		if (index == 1) return "" + (int) ZERO_FLUX_LEVEL + "%";
		return null;
	}
}