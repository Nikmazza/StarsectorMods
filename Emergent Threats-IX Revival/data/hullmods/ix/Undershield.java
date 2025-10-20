package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class Undershield extends BaseHullMod {
	
	//display texts only
	private static int DEGREES_FLEET = 360;
	private static int DEGREES_STOCK = 160;
	private static int SHIELD_TOTAL = 2000;
	private static int SHIELD_COOLDOWN = 20;
	private boolean isNinth = false;
	
	//actual bonus
	private static float SHIELD_ARC_BONUS = 200f;
	
	//since modules can't check the hubship's stats and module variant can't access its MutableShipStatsAPI
	//this hullmod applies a copy of itself onto flourish_ix module, and when on the module, adds shield arc
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//if IX/TW skin
		if (stats.getVariant().hasHullMod("ix_ninth") || stats.getVariant().hasHullMod("tw_trinity_retrofit")) {
			isNinth = true;
			if (stats.getVariant().getModuleVariant("WS 010") != null) {
				stats.getVariant().getModuleVariant("WS 010").addMod(id);
				stats.getVariant().getModuleVariant("WS 010").getHullMods().remove("ix_limited_undershield");
			} 
		}
		//else shield module
		else if (stats.getVariant().hasHullMod("ix_microfluxshunt")) {
			stats.getShieldArcBonus().modifyFlat(id, SHIELD_ARC_BONUS);
		}
		//else non-module standard skin
		else {
			isNinth = false;
			if (stats.getVariant().getModuleVariant("WS 010") != null) {
				stats.getVariant().getModuleVariant("WS 010").getHullMods().remove(id);
				stats.getVariant().getModuleVariant("WS 010").addMod("ix_limited_undershield");
			}
		}
	}	
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		int degrees = isNinth ? DEGREES_FLEET : DEGREES_STOCK;
		if (index == 0) return "" + degrees;
		if (index == 1) return "" + SHIELD_TOTAL;
		if (index == 2) return "" + SHIELD_COOLDOWN;
		return null;
	}
}