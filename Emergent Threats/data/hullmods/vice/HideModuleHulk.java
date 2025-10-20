package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class HideModuleHulk extends BaseHullMod {

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		if (amount <= 0f || ship == null) return;
		if (ship.isHulk()) Global.getCombatEngine().removeEntity(ship);
	}
}