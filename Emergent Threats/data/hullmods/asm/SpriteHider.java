package data.hullmods.asm;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class SpriteHider extends BaseHullMod {
	
	@Override
	public void applyEffectsAfterShipAddedToCombatEngine(ShipAPI ship, String id) {
		ship.setSprite("vice_ships", "invisible");
	}

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		if (amount <= 0f || ship == null) return;
		if (ship.isHulk()) Global.getCombatEngine().removeEntity(ship);
	}
}