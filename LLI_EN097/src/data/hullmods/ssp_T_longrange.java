package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class ssp_T_longrange extends BaseHullMod {
    private static Map mag = new HashMap();
    static {
        mag.put(ShipAPI.HullSize.FRIGATE, 1f);
        mag.put(ShipAPI.HullSize.DESTROYER, 2f);
        mag.put(ShipAPI.HullSize.CRUISER, 3f);
        mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 4f);
    }
    public static float MANEUVER_BONUS = -30f;
    public class ssp_T_longrange_customdata{
        Map<ShipAPI, Float> HaHaHashmap = new HashMap();
        public ssp_T_longrange_customdata(){}
    }
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if(stats.getVariant()!=null && this.spec!=null && Misc.isSpecialMod(stats.getVariant(),this.spec)){
            stats.getVariant().removePermaMod(this.spec.getId());
        }
        stats.getMaxSpeed().modifyFlat(id, stats.getMaxSpeed().getBaseValue()*MANEUVER_BONUS*0.01f);
        stats.getAcceleration().modifyPercent(id, MANEUVER_BONUS);
        stats.getDeceleration().modifyPercent(id, MANEUVER_BONUS);
        stats.getTurnAcceleration().modifyPercent(id, MANEUVER_BONUS);
        stats.getMaxTurnRate().modifyPercent(id, MANEUVER_BONUS);
        stats.getZeroFluxSpeedBoost().modifyFlat(id,stats.getMaxSpeed().getBaseValue()*-MANEUVER_BONUS*0.01f);
    }
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff")
                && !ship.getVariant().hasHullMod("ssp_T_beam")
                && !ship.getVariant().hasHullMod("ssp_T_shortrange");
    }
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("ssp_cliff")) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if(ship.isAlive()){
            ssp_T_longrange_customdata CustomData = (ssp_T_longrange_customdata) Global.getCombatEngine().getCustomData().get("ssp_T_longrange_effect");
            if(CustomData==null){
                CustomData = new ssp_T_longrange_customdata();
                Global.getCombatEngine().getCustomData().put("ssp_T_longrange_effect",CustomData);
            }
            float value;
            if (CustomData.HaHaHashmap.containsKey(ship)) {
                value = (Float)CustomData.HaHaHashmap.get(ship);
            } else {
                value = 500F;
            }
            boolean isfireing=false;
            Object hullSize = ship.getHullSize();
            for (WeaponAPI weapon : ship.getAllWeapons()) {
                if (weapon.isFiring() && !weapon.hasAIHint(WeaponAPI.AIHints.PD) && !weapon.getType().equals(WeaponAPI.WeaponType.MISSILE)) {
                    isfireing=true;
                    break;
                }
                if(!weapon.isFiring() && !weapon.hasAIHint(WeaponAPI.AIHints.PD) && !weapon.getType().equals(WeaponAPI.WeaponType.MISSILE)){
                    isfireing=false;
                }
            }
            if(isfireing){
                value -= amount * 15f;
            } else {
                value += amount * (10*(1-ship.getFluxLevel()));
            }
            if (ship.getFluxTracker().isVenting() ) {
                value += amount * 50f;
            }
            value = MathUtils.clamp(value, 0f, 100f * (Float) mag.get(hullSize));
            CustomData.HaHaHashmap.put(ship,value);
            ship.getMutableStats().getNonBeamPDWeaponRangeBonus().modifyFlat("ssp_T_longrange_effect", -value);
            ship.getMutableStats().getBeamPDWeaponRangeBonus().modifyFlat("ssp_T_longrange_effect", -value);
            ship.getMutableStats().getBallisticWeaponRangeBonus().modifyFlat("ssp_T_longrange_effect", value);
            ship.getMutableStats().getEnergyWeaponRangeBonus().modifyFlat("ssp_T_longrange_effect", value);

            //可视化
            if (Global.getCombatEngine().getPlayerShip() == ship) {
                Global.getCombatEngine().maintainStatusForPlayerShip("ssp_T_longrange_effect", "graphics/icons/hullsys/targeting_feed.png", SSPI18nUtil.getHullModString("ssp_T_longrange_title"),
                        String.format(SSPI18nUtil.getHullModString("ssp_T_longrange"), (int) value), false);
            }


        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "30%";
        }else if(index ==1){
            return "100/200/300/400";
        }
        return null;
    }
}