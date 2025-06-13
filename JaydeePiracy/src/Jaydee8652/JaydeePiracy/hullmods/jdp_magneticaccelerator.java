package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class jdp_magneticaccelerator extends BaseHullMod {

	public static float SPEED_BONUS = 200f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getProjectileSpeedMult().modifyPercent(id , SPEED_BONUS);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) SPEED_BONUS + "%";
		return null;
	}
}




