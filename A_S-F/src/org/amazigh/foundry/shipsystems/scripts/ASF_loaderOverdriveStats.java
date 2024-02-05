package org.amazigh.foundry.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class ASF_loaderOverdriveStats extends BaseShipSystemScript {

	public static final float ROF_BONUS = 9.0f;
	public static final float FLUX_REDUCTION = 80f;
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		
		float mult = 1f + (ROF_BONUS * effectLevel);
		stats.getBallisticRoFMult().modifyMult(id, mult);
		stats.getBallisticAmmoRegenMult().modifyMult(id, mult);
		stats.getMissileRoFMult().modifyMult(id, mult);
		stats.getMissileAmmoRegenMult().modifyMult(id, mult);
		
		float multc = FLUX_REDUCTION * effectLevel;
		stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -multc);
		stats.getMissileWeaponFluxCostMod().modifyPercent(id, -multc);
	}
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getBallisticRoFMult().unmodify(id);
		stats.getBallisticAmmoRegenMult().unmodify(id);
		stats.getMissileRoFMult().unmodify(id);
		stats.getMissileAmmoRegenMult().unmodify(id);
		
		stats.getBallisticWeaponFluxCostMod().unmodify(id);
		stats.getMissileWeaponFluxCostMod().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float mult = 1f + (ROF_BONUS * effectLevel);
		float bonusPercent = (int) ((mult - 1f) * 100f);
		
		if (index == 0) {
			return new StatusData("ballistic and missile reload rate +" + (int) bonusPercent + "%", false);
		}
		return null;
	}
}
