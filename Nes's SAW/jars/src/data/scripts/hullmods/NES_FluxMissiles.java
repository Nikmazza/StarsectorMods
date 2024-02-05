package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;

import static data.scripts.utils.NES_Util.txt;
public class NES_FluxMissiles extends BaseHullMod {

	public final float ROF_BONUS = 100f; //bonus amount, +100% rate of fire
	public final float FLUX_MAX = 0.90f; //max bonus reached at 90% flux

	public void advanceInCombat(ShipAPI ship, float amount){
		if (!ship.isAlive()) return;
		MutableShipStatsAPI stats = ship.getMutableStats();

		float fluxceiling = Math.min((ship.getFluxLevel() / FLUX_MAX), 1f);
		stats.getMissileRoFMult().modifyMult(spec.getId(), 1 + ROF_BONUS * 0.01f * fluxceiling);
		stats.getMissileAmmoRegenMult().modifyMult(spec.getId(), 1 + ROF_BONUS * 0.01f * fluxceiling);
		stats.getMissileWeaponFluxCostMod().modifyMult(spec.getId(), 1 - ROF_BONUS * 0.01f * fluxceiling);

		if (ship == Global.getCombatEngine().getPlayerShip())
			Global.getCombatEngine().maintainStatusForPlayerShip("NES_FluxMissiles", "graphics/icons/hullsys/missile_autoforge.png", txt("hullmod_hotloadedmissiles1"), txt("hullmod_hotloadedmissiles2") + Math.round(ROF_BONUS * fluxceiling) + "%", false);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return txt("hullmod_hotloadedmissiles3");
		if (index == 1) return Math.round(ROF_BONUS) + "%";
		if (index == 2) return Math.round(FLUX_MAX * 100) + "%";
		return null;
	}
}