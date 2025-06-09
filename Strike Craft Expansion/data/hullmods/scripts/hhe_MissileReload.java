package data.hullmods.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;

public class hhe_MissileReload extends BaseHullMod {

	private static final float MISSILE_RELOAD_RATE = 33f;
        private static final float MISSILE_REGEN = 1.33f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		////stats.getBallisticRoFMult().modifyMult(id, 1f + MISSILE_RELOAD_RATE * 0.01f);
		////stats.getEnergyRoFMult().modifyMult(id, 1f + MISSILE_RELOAD_RATE * 0.01f);
		stats.getMissileRoFMult().modifyMult(id, 1f + MISSILE_RELOAD_RATE * 0.01f);
	}

    public void advanceInCombat (ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine.isPaused() || !ship.isAlive())
            return;
        for (WeaponAPI w : ship.getAllWeapons()) {
            float reloadRate = w.getSpec().getAmmoPerSecond();
            float AdjustedRate = reloadRate * MISSILE_REGEN;
            if (w.getType() == WeaponAPI.WeaponType.MISSILE && w.usesAmmo() && reloadRate > 0.0F)
                w.getAmmoTracker().setAmmoPerSecond(AdjustedRate);
            }
    }        
        
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) MISSILE_RELOAD_RATE + "%";
		return null;
	}


}
