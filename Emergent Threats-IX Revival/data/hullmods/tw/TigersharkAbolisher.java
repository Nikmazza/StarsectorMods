package data.hullmods.tw;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class TigersharkAbolisher extends BaseHullMod {

	private static String MAIN_SLOT = "WS 004";
	private static String ABOLISHER_ID = "abolisher_built_in_tw";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getVariant().addWeapon(MAIN_SLOT, ABOLISHER_ID);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Abolisher Cannon";
		if (index == 1) return "Stormwall Suppressor";
		return null;
	}
}