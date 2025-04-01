package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.combat.CombatUtils;


public class ssp_superdestoryer_hullmod extends BaseHullMod {
    public IntervalUtil interval = new IntervalUtil(0.5f,0.5f);//最小,最大
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (stats.getVariant().hasHullMod("comp_hull")){stats.getVariant().removePermaMod("comp_hull");}
        if (stats.getVariant().hasHullMod("comp_structure")) {stats.getVariant().removePermaMod("comp_structure");}
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if(ship == null){return;}
        if(!ship.isAlive()){return;}
        interval.advance(amount);
        if(interval.intervalElapsed()){
            //卖血耗散
            if(ship.getFluxLevel()>=0.75f){
                ship.setHitpoints(ship.getHitpoints()-150);
                ship.getFluxTracker().decreaseFlux(150);
            }
            //吸血光环
            for(ShipAPI s:CombatUtils.getShipsWithinRange(ship.getLocation(),1200)){
                if(!s.hasListenerOfClass(ssp_superdestoryer_hullmod_listener.class) && s.isAlive() && s!=ship){
                    s.addListener(new ssp_superdestoryer_hullmod_listener(ship));
                }
            }
        }
    }
    public static class ssp_superdestoryer_hullmod_listener implements DamageListener {
        protected ShipAPI ship;
        protected String id;
        protected float mult =1.00f;//结构偷取百分比
        public ssp_superdestoryer_hullmod_listener(ShipAPI ship){
            this.ship = ship;
        }
        public void reportDamageApplied(Object source, CombatEntityAPI target, ApplyDamageResultAPI result) {
            if(target instanceof ShipAPI && !((ShipAPI)target).isAlive()){
                ((ShipAPI)target).removeListenerOfClass(ssp_superdestoryer_hullmod_listener.class);
            }
            if(source instanceof ShipAPI){
                if(((ShipAPI) source).getVariant().hasHullMod("ssp_superdestoryer_hullmod")
                    && Misc.getDistance(((ShipAPI) source).getLocation(),target.getLocation())<=1200){
                if(((ShipAPI)source).getHitpoints()>=((ShipAPI)source).getMaxHitpoints()){//满血则加上限
                    if (result.isDps()){
                        ((ShipAPI) source).setMaxHitpoints(((ShipAPI) source).getHitpoints()+ (result.getTotalDamageToArmor()*0.5f+result.getDamageToHull())*mult*0.1f*0.1f);
                    }else{
                        ((ShipAPI) source).setMaxHitpoints(((ShipAPI) source).getHitpoints()+ (result.getTotalDamageToArmor()*0.5f+result.getDamageToHull())*mult*0.1f);
                    }
                } else if(((ShipAPI)source).getHitpoints()<((ShipAPI)source).getMaxHitpoints()){//未满血则回血
                    if (result.isDps()){
                        ((ShipAPI) source).setHitpoints(((ShipAPI) source).getHitpoints()+ (result.getTotalDamageToArmor()*0.5f+result.getDamageToHull())*mult*0.1f);
                    }else{
                        ((ShipAPI) source).setHitpoints(((ShipAPI) source).getHitpoints()+ (result.getTotalDamageToArmor()*0.5f+result.getDamageToHull())*mult);
                    }
                }
            }
            }
        }
    }
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if(index ==0){
            return "1200";
        }else if(index ==1){
            return "100%";
        }else if(index ==2){
            return "10%";
        }else if(index ==3){
            return "75%";
        }
        return null;
    }
}

