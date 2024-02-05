package data.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class fed_wepfluxreducer extends BaseShipSystemScript {

	public static final float ROF_BONUS = 1.75f;
        public static final float BAL_FLUX_REDUCTION = 33f;
        public static final float RANGE_BOOST = 15f;
        
        
        
	
        @Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		
		float mult = -0.5f + (0.5f + ROF_BONUS * effectLevel);
                stats.getEnergyRoFMult().modifyMult(id, mult);
                stats.getEnergyWeaponFluxCostMod().modifyPercent(id, -BAL_FLUX_REDUCTION * effectLevel);
                stats.getEnergyAmmoRegenMult().modifyPercent(id, BAL_FLUX_REDUCTION);
		stats.getBallisticRoFMult().modifyMult(id, mult);
		stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -BAL_FLUX_REDUCTION * effectLevel);
                stats.getBallisticAmmoRegenMult().modifyPercent(id, BAL_FLUX_REDUCTION);
                stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BOOST);
                stats.getBallisticProjectileSpeedMult().modifyPercent(id, RANGE_BOOST);
                stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BOOST);
                stats.getEnergyProjectileSpeedMult().modifyPercent(id, RANGE_BOOST);
	}
        @Override
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getEnergyRoFMult().unmodify(id);
                stats.getEnergyWeaponFluxCostMod().unmodify(id);
                stats.getEnergyAmmoRegenMult().unmodify(id);
		stats.getBallisticRoFMult().unmodify(id);
		stats.getBallisticWeaponFluxCostMod().unmodify(id);
                stats.getBallisticAmmoRegenMult().unmodify(id);
                stats.getBallisticWeaponRangeBonus().unmodify(id);
                stats.getBallisticProjectileSpeedMult().unmodify(id);
                stats.getEnergyWeaponRangeBonus().unmodify(id);
                stats.getEnergyProjectileSpeedMult().unmodify(id);
	}
	
        @Override
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float mult = -1f + (1f + ROF_BONUS * effectLevel);
		float bonusPercentBallistic = (int) ((mult - 1f) * 100f);
		if (index == 0) {
			return new StatusData("rate of fire +" + (int) bonusPercentBallistic + "%", false);
		}
		if (index == 1) {
			return new StatusData(" flux use -" + (int) BAL_FLUX_REDUCTION + "%", false);
		}
                //if (index == 2) {
                //        return new StatusData("energy flux use -" + (int) EN_FLUX_REDUCTION + "%", false);
                //}
		return null;
	}
}