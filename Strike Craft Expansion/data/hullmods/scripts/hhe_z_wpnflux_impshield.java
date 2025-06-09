package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class hhe_z_wpnflux_impshield extends BaseHullMod {

	public static final float FTR_WEAPON_FLUX = 0f;
	public static final float FTR_SHIELD_TURN = 33f;
	public static final float FTR_SHIELD_UNFOLD = 33f;
	public static final float FTR_SHIELD_UPKEEP = 33f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
	
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, FTR_WEAPON_FLUX);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, FTR_WEAPON_FLUX);
		stats.getMissileWeaponFluxCostMod().modifyMult(id, FTR_WEAPON_FLUX);
	
		stats.getShieldTurnRateMult().modifyPercent(id, FTR_SHIELD_TURN);
		stats.getShieldUnfoldRateMult().modifyPercent(id, FTR_SHIELD_UNFOLD);
		stats.getShieldUpkeepMult().modifyMult(id, 1f - FTR_SHIELD_UPKEEP * 0.01f);
	}
	
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null && ship.getShield() != null;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		return "Ship has no shields";
	}
}
