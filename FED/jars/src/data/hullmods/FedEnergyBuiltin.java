package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class FedEnergyBuiltin extends BaseHullMod {

    private static final float ENERGY_DAMAGE_INCREASE = 20f;
    private static final float ENERGY_FIRE_RATE_REDUCTION = 20f;
    
    private static final float ENERGY_MAG_SIZE_REDUCTION = 0.9f;
    private static final float ENERGY_AMMO_REGEN_REDUCTION = 0.9f;
    
    private static final float ENERGY_PROJECTILE_SPEED_REDUCTION = 10f;
    private static final float FED_ENERGY_RANGE_BONUS = 100f;
    
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        
        stats.getEnergyWeaponDamageMult().modifyPercent(id, ENERGY_DAMAGE_INCREASE);
        stats.getEnergyRoFMult().modifyPercent(id, -ENERGY_FIRE_RATE_REDUCTION);
        
        stats.getEnergyAmmoBonus().modifyMult(id, ENERGY_MAG_SIZE_REDUCTION);
        stats.getEnergyAmmoRegenMult().modifyMult(id, ENERGY_MAG_SIZE_REDUCTION);
        
        stats.getEnergyProjectileSpeedMult().modifyPercent(id, -ENERGY_PROJECTILE_SPEED_REDUCTION);
        stats.getEnergyWeaponRangeBonus().modifyFlat(id, FED_ENERGY_RANGE_BONUS);
        stats.getAutofireAimAccuracy().modifyPercent(id, ENERGY_PROJECTILE_SPEED_REDUCTION);
        
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) {
            return "" + (int) ENERGY_DAMAGE_INCREASE + "%";
        }
        if (index == 2) {
            return "" + (int) ENERGY_FIRE_RATE_REDUCTION + "%";
        }
        if (index == 1) {
            return "" + (int) FED_ENERGY_RANGE_BONUS + "";
        }
        
        
        if (index == 3) {
            return "" + Math.round((1f - ENERGY_MAG_SIZE_REDUCTION)*100f) + "%";
        }
        if (index == 4) {
            return "" + Math.round((1f - ENERGY_AMMO_REGEN_REDUCTION)*100f) + "%";
        }
        
        
        if (index == 5) {
            return "" + (int) (ENERGY_PROJECTILE_SPEED_REDUCTION) + "%";
        }
        
        
        return null;
    }
}
