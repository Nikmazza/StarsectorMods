package data.hullmods.tw;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class TigersharkStormwall extends BaseHullMod {

	private static String MAIN_SLOT = "WS 004";
	private static String STORMWALL_ID = "stormwall_built_in_tw";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getVariant().addWeapon(MAIN_SLOT, STORMWALL_ID);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Stormwall Suppressor";
		if (index == 1) return "Abolisher Cannon";
		return null;
	}
}