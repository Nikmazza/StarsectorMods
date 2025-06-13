package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class jdp_frictionlesshull extends BaseHullMod {

	public static float ARMOR_MULT = 0.6f;
	private static final float SUPPLY_USE_MULT = 2f;
	private static final float PEAK_MULT = 0.5f;
	public static float TIMEFLOW_BONUS = 200f;
	public static float CORONA_EFFECT_MULT = 0f;


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//That's good!
		stats.getTimeMult().modifyPercent(id , TIMEFLOW_BONUS);
		float mult = CORONA_EFFECT_MULT;
		stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, mult);

		//That's bad!
		stats.getEffectiveArmorBonus().modifyMult(id, ARMOR_MULT);
		stats.getMinArmorFraction().modifyMult(id, ARMOR_MULT);
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);
		stats.getPeakCRDuration().modifyMult(id, PEAK_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) TIMEFLOW_BONUS + "%";
		if (index == 1) return "" + (int) Math.round((1f - CORONA_EFFECT_MULT) * 100f) + "%";
		if (index == 2) return "" + (int) Math.round(ARMOR_MULT * 100f) + "%";
		if (index == 3) return "" + (int)((SUPPLY_USE_MULT - 1f) * 100f) + "%";
		if (index == 4) return "2";
		return null;
	}
}




