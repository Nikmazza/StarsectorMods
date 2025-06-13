package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class BrightonBase extends BaseHullMod {

	public static final float PIERCE_MULT = 1.25f;
	public static final float SHIELD_BONUS = 20f;
	public static final float ARMOUR_MULT = 0.75f;
	public static final float WEAPON_MULT = 0.15f;
	public static final float CR_MULT = 2f;
	public static final float SUPPLY_MULT = 0.75f;
	public static final float MISSILE_MULT = 0.75f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getShieldDamageTakenMult().modifyMult(id, 1f + (SHIELD_BONUS * 0.01f));
		stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyMult(id, PIERCE_MULT);
		stats.getArmorBonus().modifyMult(id, ARMOUR_MULT);

		stats.getFluxDissipation().modifyMult(id, ARMOUR_MULT);
		stats.getFluxCapacity().modifyMult(id, ARMOUR_MULT);

		stats.getAcceleration().modifyMult(id, ARMOUR_MULT);
		stats.getDeceleration().modifyMult(id, ARMOUR_MULT);
		stats.getMaxSpeed().modifyMult(id, ARMOUR_MULT);

		stats.getPeakCRDuration().modifyMult(id, 1f - WEAPON_MULT);
		stats.getCRLossPerSecondPercent().modifyMult(id, CR_MULT);

		stats.getWeaponHealthBonus().modifyMult(id, 1f - WEAPON_MULT);
		stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f + WEAPON_MULT);

		stats.getMissileAmmoRegenMult().modifyMult(id, 1f - MISSILE_MULT);

		stats.getEngineHealthBonus().modifyMult(id, 1f - WEAPON_MULT);
		stats.getEngineDamageTakenMult().modifyMult(id, 1f + WEAPON_MULT);

		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((1f - ARMOUR_MULT)*100) + "%";
		if (index == 1) return "" + (int) SHIELD_BONUS + " %";
		if (index == 2) return "" + (int) ((1f - ARMOUR_MULT)*100) + "%";
		if (index == 3)return "" + (int) ((1f - WEAPON_MULT)*100) + "%";
		if (index == 4)return "" + (int) CR_MULT;
		if (index == 5)return "" + (int) ((MISSILE_MULT)*100) + "%";
		if (index == 6)return "" + (int) ((MISSILE_MULT)*100) + "%";
		if (index == 7)return "superior missile-focused layout";
		if (index == 8)return "" + (int) ((1-SUPPLY_MULT)*100) + "%";

		return null;
	}
}
