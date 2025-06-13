package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class UnstableInjectorFtr extends BaseHullMod {

	private static float RANGE_MULT = 0.85f;
	private static float SPEED_BONUS = 25f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS);
		stats.getBallisticWeaponRangeBonus().modifyMult(id, RANGE_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}
}
