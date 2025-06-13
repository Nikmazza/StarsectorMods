package data.hullmods.orr;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class DamperField extends BaseHullMod {
	
	private static String CONFLICT_MOD = "frontshield";
	private static String DEFENSES = "Damper Field";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getVariant().getHullMods().remove(CONFLICT_MOD);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return DEFENSES;
		return null;
	}
}