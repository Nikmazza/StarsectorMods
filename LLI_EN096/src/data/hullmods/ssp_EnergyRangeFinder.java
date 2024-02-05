package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;


public class ssp_EnergyRangeFinder extends BaseHullMod {
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) { }
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new ssp_EnergyRangeFinderRangeModifier());
    }
    public static class ssp_EnergyRangeFinderRangeModifier implements WeaponBaseRangeModifier {
        public ssp_EnergyRangeFinderRangeModifier(){}
        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0;
        }
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            return 1f;
        }
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
            if (weapon.getSlot() == null) { return 0f; }
            if (weapon.isBeam()) { return 0f; }
            if (weapon.getSlot().getWeaponType() == WeaponAPI.WeaponType.ENERGY)return 100f;
            return 0f;
        }
    }
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index ==0){
            return "100";
        }
        return null;
    }


}
