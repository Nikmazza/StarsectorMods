package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.Misc;

public class jdp_externalmissileracks extends BaseHullMod {

	public static final float HANDLING_MULT = 0.90f;
	public static final float ARMOUR_MULT = 0.50f;
	public static final float HULL_BONUS = 0.40f;
	public static float AMMO_BONUS = 100f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMissileAmmoBonus().modifyPercent(id, AMMO_BONUS);
		stats.getMaxSpeed().modifyMult(id, HANDLING_MULT);
		stats.getAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getDeceleration().modifyMult(id, HANDLING_MULT);
		stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
		stats.getTurnAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getArmorBonus().modifyMult(id, ARMOUR_MULT);
		stats.getHullBonus().modifyMult(id, HULL_BONUS);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) AMMO_BONUS + "%";
		if (index == 1) return "" + (int) Math.round((1f - HULL_BONUS) * 100f) + "%";
		if (index == 2) return "" + (int) Math.round((1f - ARMOUR_MULT) * 100f) + "%";
		if (index == 3) return "" + (int) Math.round((1f - HANDLING_MULT) * 100f) + "%";
		return null;
	}

}
