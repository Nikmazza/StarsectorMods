package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class hhe_z_wpnflux_reduce extends BaseHullMod {

	public static final float WEAPON_FLUX_REDUCTION = 33f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
	
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - WEAPON_FLUX_REDUCTION * 0.01f);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - WEAPON_FLUX_REDUCTION * 0.01f);
		stats.getMissileWeaponFluxCostMod().modifyMult(id, 1f - WEAPON_FLUX_REDUCTION * 0.01f);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) WEAPON_FLUX_REDUCTION + "%";
		return null;
	}
	
}
