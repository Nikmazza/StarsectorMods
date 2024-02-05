package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class FedSecondaryShieldEffect extends BaseHullMod {
    
        private final float HE_DAMAGE_TAKEN_PERCENT = 100f;
        private final float HARDFLUX_DISSIPATION_PERCENT = 100;
        private final float OVERLOAD_ADDITIVE = 5f;
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            //stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
            //stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
            stats.getHighExplosiveShieldDamageTakenMult().modifyPercent(id, HE_DAMAGE_TAKEN_PERCENT);
            stats.getHardFluxDissipationFraction().modifyFlat(id, (float)HARDFLUX_DISSIPATION_PERCENT);
            stats.getOverloadTimeMod().modifyFlat(id, OVERLOAD_ADDITIVE);
           
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
               	if (index == 1) return "" + (int)Math.round(HARDFLUX_DISSIPATION_PERCENT) + "%";
                if (index == 2) return "" + (int)Math.round(HE_DAMAGE_TAKEN_PERCENT) + "*";
                if (index == 0) return "unfolds instantly in all directions";
                
		return null;
	}
}

