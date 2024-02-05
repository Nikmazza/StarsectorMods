package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import java.util.HashMap;
import java.util.Map;

public class ssp_vocaino_rangemod extends BaseHullMod {
    private static Map mag = new HashMap();
    public static final float RANGE_BUFF = 1.1f;
    public static final float BONUS1 = 15f;
    public static final float BONUS2 = 40f;
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMaxRecoilMult().modifyMult(id, 1f - (0.01f * BONUS1));
        stats.getRecoilPerShotMult().modifyMult(id, 1f - (0.01f *BONUS1));
        stats.getRecoilDecayMult().modifyMult(id, 1f - (0.01f * BONUS1));
        stats.getWeaponTurnRateBonus().modifyPercent(id, BONUS2);
        stats.getBeamWeaponTurnRateBonus().modifyPercent(id, BONUS2);
        stats.getBallisticWeaponRangeBonus().modifyMult(id,RANGE_BUFF);
        stats.getEnergyWeaponRangeBonus().modifyMult(id,RANGE_BUFF);
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

    }
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "15%";
        }else if (index == 1){
            return "40%";
        }else if (index ==2){
            return "10%";
        }
        return null;
    }
//    public static class ssp_vocaino_rangemodmodifier implements WeaponRangeModifier {
//        protected ShipAPI ship;
//        protected String id;
//        public ssp_vocaino_rangemodmodifier(ShipAPI ship, String id) {
//            this.ship = ship;
//            this.id = id;
//        }
//        public float getWeaponRangePercentMod(ShipAPI ship, WeaponAPI weapon) { return 0f;}
//        public float getWeaponRangeFlatMod(ShipAPI ship, WeaponAPI weapon){return 0f;}
//        public float getWeaponRangeMultMod(ShipAPI ship, WeaponAPI weapon){
//            float RANGE=1f;
//            if(weapon.getSlot().isHardpoint()){
//                if(weapon.getSlot().getSlotSize()== WeaponAPI.WeaponSize.MEDIUM){
//                    RANGE=RANGE_BUFF1;
//                }else if(weapon.getSlot().getSlotSize()== WeaponAPI.WeaponSize.SMALL){
//                    RANGE=RANGE_BUFF0;
//                }
//            }
//            if(weapon.getType()== WeaponAPI.WeaponType.MISSILE){RANGE=1f;}
//            return RANGE;
//        }
//    }
}
