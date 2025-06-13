package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class BrightonRemnant extends BaseHullMod {

	public static final float WEAPON_MULT = 0.25f;


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		stats.getWeaponHealthBonus().modifyMult(id, 1f - WEAPON_MULT);
		stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f + WEAPON_MULT);

		stats.getEngineHealthBonus().modifyMult(id, WEAPON_MULT);
		stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f + WEAPON_MULT);

	}

	public String getDescriptionParam(int index, HullSize hullSize) {

		if (index == 0) return "" + (int) ((WEAPON_MULT) * 100) + "%";
		if (index == 1) return "" + (int) ((WEAPON_MULT) * 100) + "%";
		return null;
	}
}