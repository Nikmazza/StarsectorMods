package data.hullmods.tw;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

public class UndershieldAurora extends BaseHullMod {
	
	private static int SHIELD_DEGREES = 360;
	private static int SHIELD_TOTAL = 8000;
	private static int SHIELD_COOLDOWN = 40;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (!stats.getVariant().hasHullMod("tw_aurora_asm_handler")) stats.getVariant().removeTag(Tags.UNRECOVERABLE);
	}	
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + SHIELD_DEGREES;
		if (index == 1) return "" + SHIELD_TOTAL;
		if (index == 2) return "" + SHIELD_COOLDOWN;
		return null;
	}
}