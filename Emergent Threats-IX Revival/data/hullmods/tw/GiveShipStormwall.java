package data.hullmods.tw;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class GiveShipStormwall extends BaseHullMod {

	private static String TSHARK_MOD = "tw_plasma_ramjet";
	private static String TSHARK_MAIN_SLOT = "WS 004";
	private static String ICONO_MAIN_SLOT = "WS ARR";
	private static String STORMWALL_ID = "stormwall_built_in_tw";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		String s = stats.getVariant().hasHullMod(TSHARK_MOD) ? TSHARK_MAIN_SLOT : ICONO_MAIN_SLOT;
		stats.getVariant().addWeapon(s, STORMWALL_ID);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Stormwall Suppressor";
		if (index == 1) return "Abolisher Cannon";
		return null;
	}
}