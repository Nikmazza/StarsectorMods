package data.hullmods.asm;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class DismantlerEffects extends BaseHullMod {
	
	private static float LOST_HP = 50f; // 1/40 of hull total lost per second
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getWeaponDamageTakenMult().modifyMult(id, 0f);
	}
	
	@Override
	public void applyEffectsAfterShipAddedToCombatEngine(ShipAPI ship, String id) {
		ship.setSprite("vice_ships", "invisible");
	}
	
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		if (amount <= 0f || ship == null) return;
		if (ship.isHulk()) Global.getCombatEngine().removeEntity(ship);
		
		float currentHP = ship.getHitpoints();
		float newHP = currentHP - (LOST_HP * amount);
		if (newHP <= 0f) Global.getCombatEngine().removeEntity(ship);
		else ship.setHitpoints(newHP);
	}
}