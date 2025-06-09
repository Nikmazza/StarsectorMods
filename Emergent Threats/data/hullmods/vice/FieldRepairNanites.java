package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.Misc;

public class FieldRepairNanites extends BaseHullMod {
	
	private static float HEAL_HULL_AMOUNT = 1f; //1% hull per second
	private static float HEAL_HULL_CAP = 125f; //max hull heal per second
	private static float HEAL_HULL_CAP_SMOD = 175f; //max hull heal with s-mod
	private static float CREW_CASUALTIES = 15;
	private static String CONFLICT_MOD = "vice_adaptive_entropy_arrester";
	private static String CONFLICT_MOD_2 = "ix_entropy_arrester";
	private static String THIS_MOD = "vice_field_repair_nanites";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (!Misc.isAutomated(stats)) stats.getCrewLossMult().modifyPercent(id, CREW_CASUALTIES);
	}
	
	@Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;
		float currentHP = ship.getHitpoints();
		float maxHP = ship.getMaxHitpoints();
		float maxHealHP = isSMod(ship) ? HEAL_HULL_CAP_SMOD : HEAL_HULL_CAP;
		
		if (currentHP < maxHP) {
			float healedHP = maxHP * HEAL_HULL_AMOUNT * 0.01f;
			if (healedHP > maxHealHP) healedHP = maxHealHP;
			float newHP = currentHP + (healedHP * amount);
			if (newHP < maxHP) ship.setHitpoints(newHP);
			else ship.setHitpoints(maxHP);
		}
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(CONFLICT_MOD)) return false;
		if (ship.getVariant().hasHullMod(CONFLICT_MOD_2)) return false;
		return true;
	}

	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(CONFLICT_MOD)) return "Incompatible with Adaptive Entropy Arrester";
		if (ship.getVariant().hasHullMod(CONFLICT_MOD_2)) return "Incompatible with Entropy Arrester";
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) HEAL_HULL_AMOUNT + "%";
		if (index == 1) return "" + (int) HEAL_HULL_CAP;
		if (index == 2) return "" + (int) CREW_CASUALTIES + "%";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) HEAL_HULL_CAP_SMOD;
		return null;
	}
}