package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class FederationFireControl extends BaseHullMod {
    
        private static final float RECOIL_REDUCTION = 20f;
        private static final float PROJETILE_SPEED_MULT = 10f;
        private static final float RANGE_BONUS = 100f;
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            //stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
            //stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
            stats.getRecoilPerShotMult().modifyPercent(id, -RECOIL_REDUCTION);
            stats.getMaxRecoilMult().modifyPercent(id, -RECOIL_REDUCTION);
            stats.getRecoilDecayMult().modifyPercent(id, RECOIL_REDUCTION);
		
            stats.getBallisticWeaponRangeBonus().modifyFlat(id, RANGE_BONUS);
            stats.getEnergyWeaponRangeBonus().modifyFlat(id, RANGE_BONUS);
            stats.getBeamWeaponRangeBonus().modifyFlat(id, -RANGE_BONUS);
                
            stats.getProjectileSpeedMult().modifyPercent(id, PROJETILE_SPEED_MULT);
           
	}
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
               	if (index == 2) return "" + (int)Math.round(RECOIL_REDUCTION) + "%";
                if (index == 0) return "" + (int)Math.round(PROJETILE_SPEED_MULT) + "%";
                if (index == 1) return "" + (int)Math.round(RANGE_BONUS) + "su";
                
		return null;
	}
}

