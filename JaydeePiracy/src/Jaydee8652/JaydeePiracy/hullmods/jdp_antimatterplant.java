package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class jdp_antimatterplant extends BaseHullMod {

	public static float BURN_BONUS = 3;
	public static float PROFILE_MULT = 400;
	public static float OVERLOAD_BONUS = 25f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.FLEET_BURN_BONUS).modifyFlat(id, BURN_BONUS);
		stats.getSensorProfile().modifyFlat(id, PROFILE_MULT);
		stats.getOverloadTimeMod().modifyMult(id, 1f - OVERLOAD_BONUS * 0.01f);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) BURN_BONUS + "";
		if (index == 1) return "" + (int) PROFILE_MULT + "";
		if (index == 2) return "" + (int) OVERLOAD_BONUS + "%";
		return null;
	}
}




