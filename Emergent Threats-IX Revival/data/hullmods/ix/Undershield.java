package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class Undershield extends BaseHullMod {
	
	//display texts only
	private static String STOCK_MOD_ID = "ix_undershield_stock";
	private static String FLEET_MOD_ID = "ix_undershield_fleet";
	private static int DEGREES_FLEET = 360;
	private static int DEGREES_STOCK = 160;
	private static String MOD_ID = "";
	
	//actual bonus
	private static float SHIELD_ARC_BONUS = 200f;
	
	//since modules can't check the hubship's stats and module variant can't access its MutableShipStatsAPI
	//this hullmod applies a copy of itself onto flourish_ix module, and when on the module, adds shield arc
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (stats.getVariant().getHullSpec().getHullId().equals("flourish_ix")) {
			if (stats.getVariant().getModuleVariant("WS 010") != null) {
				stats.getVariant().getModuleVariant("WS 010").addPermaMod(id);
			} 
		}
		else if (stats.getVariant().getHullSpec().getHullId().equals("flourish_stock")) {
			if (stats.getVariant().getModuleVariant("WS 010") != null) {
				stats.getVariant().getModuleVariant("WS 010").removePermaMod(id);
			}
		} 
		else if (stats.getVariant().getHullSpec().getHullId().equals("flourish_undershield")) {
			stats.getShieldArcBonus().modifyFlat(id, SHIELD_ARC_BONUS);
		}
	}	
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		int degrees = MOD_ID.equals(FLEET_MOD_ID) ? DEGREES_FLEET : DEGREES_STOCK;
		if (index == 0) return "" + degrees;
		return null;
	}
}