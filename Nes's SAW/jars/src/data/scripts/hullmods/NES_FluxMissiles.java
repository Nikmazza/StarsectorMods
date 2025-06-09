package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import static data.scripts.utils.NES_Util.txt;
public class NES_FluxMissiles extends BaseHullMod {

	public final float ROF_BONUS = 75f;

	public static float CREW_CASUALTIES = 50; //pre-arming missiles inside their racks is extremely hazardous

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getCrewLossMult().modifyPercent(id, CREW_CASUALTIES);
	}

	public void advanceInCombat(ShipAPI ship, float amount){
		if (!ship.isAlive()) return;
		MutableShipStatsAPI stats = ship.getMutableStats();

		stats.getMissileRoFMult().modifyMult(spec.getId(), 1 + ROF_BONUS * 0.01f);
		stats.getMissileAmmoRegenMult().modifyMult(spec.getId(), 1 + ROF_BONUS * 0.01f);
		stats.getMissileWeaponFluxCostMod().modifyMult(spec.getId(), 1 - ROF_BONUS * 0.01f);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return Math.round(ROF_BONUS) + "%";
		if (index == 1) return "" + (int) CREW_CASUALTIES + "%";
		return null;
	}
}