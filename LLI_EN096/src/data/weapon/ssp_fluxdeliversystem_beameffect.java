package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.TimeoutTracker;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ssp_fluxdeliversystem_beameffect implements BeamEffectPlugin {
    protected boolean wasZero = true;
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        CombatEntityAPI Target = beam.getDamageTarget();
        ShipAPI ship_from = beam.getSource();
        if(beam.getDamageTarget()==null){return;}
        if (Target instanceof ShipAPI && beam.getBrightness() >= 1f && beam.getWeapon() != null) {
            float dur = beam.getDamage().getDpsDuration();
            if (!wasZero) dur = 0;
            wasZero = beam.getDamage().getDpsDuration() <= 0;
            if (dur > 0) {
                //boolean hitShield = Target.getShield() != null && Target.getShield().isWithinArc(beam.getTo());
                if (beam.getDamageTarget()!=null) {
                    ShipAPI target=(ShipAPI) Target;
                    if (!target.hasListenerOfClass(ssp_fluxdeliversystem_listener.class)) {
                        target.addListener(new ssp_fluxdeliversystem_listener(target,ship_from));
                    }
                    List<ssp_fluxdeliversystem_listener> listeners = target.getListeners(ssp_fluxdeliversystem_listener.class);
                    if (listeners.isEmpty()) return; // ???

                    ssp_fluxdeliversystem_listener listener = listeners.get(0);
                    listener.notifyHit(beam.getWeapon());
                }
            }
        }
    }
    public static class ssp_fluxdeliversystem_listener implements AdvanceableListener {
        protected ShipAPI target;
        protected ShipAPI ship_from;
        protected TimeoutTracker<WeaponAPI> recentHits = new TimeoutTracker<WeaponAPI>();
        protected TimeoutTracker<WeaponAPI> ArcSpawnerList = new TimeoutTracker<WeaponAPI>();
        public IntervalUtil interval = new IntervalUtil(0.1f,0.1f);//最小,最大
        public ssp_fluxdeliversystem_listener(ShipAPI target,ShipAPI ship_from) {
            this.target = target;
            this.ship_from = ship_from;
        }

        public void notifyHit(WeaponAPI w) { recentHits.add(w,0.5f,0.5f); }
        public void ArcSpawner(WeaponAPI w) { ArcSpawnerList.add(w,2f,2f); }

        public void advance(float amount) {
            if(Global.getCombatEngine().isPaused())return;
            recentHits.advance(amount);
            ArcSpawnerList.advance(amount);
            interval.advance(amount*5f);
            float Average_fluxlevel=0f;
            float higher=0f;
            float lower=0f;
            int Beams_number = recentHits.getItems().size();
            if(Beams_number==0){target.removeListener(this);}
            List<WeaponAPI>beamweaponshiplist = recentHits.getItems(); //每个命中的光束武器
            ArrayList<Float> beamweaponshiplist_fluxlevel = new ArrayList<>(); //每根光束所属舰船的幅能水平
            ArrayList<ShipAPI> beamweaponshiplist_ship = new ArrayList<>(); //每根光束所属舰船
            for (WeaponAPI W:beamweaponshiplist){
                beamweaponshiplist_fluxlevel.add(W.getShip().getFluxLevel());//为“每根光束所属舰船的幅能水平”这个arraylist添加元素
                beamweaponshiplist_ship.add(W.getShip());//为“每根光束所属舰船”这个arraylist添加元素(arrayayayayalist)
            }
            for(int i=0;i<beamweaponshiplist_fluxlevel.size();i++){
                Average_fluxlevel+=beamweaponshiplist_fluxlevel.get(i)/beamweaponshiplist_fluxlevel.size(); //求平均值
            }
            for(float f:beamweaponshiplist_fluxlevel){//根据平均值取得高于或低于平均值的“光束武器所属舰船”数量
                if (f<Average_fluxlevel) {lower+=1;}
                if (f>Average_fluxlevel) {higher+=1;}
            }
            for (ShipAPI ship:beamweaponshiplist_ship){//根据“每根光束所属舰船”调整幅能
                if(interval.intervalElapsed()){
                if(ship.getFluxLevel()<Average_fluxlevel){ship.getFluxTracker().increaseFlux((higher+lower)/lower*2,false);}
                if(ship.getFluxLevel()>Average_fluxlevel){ship.getFluxTracker().decreaseFlux((higher+lower)/higher*2);}
                }
            }
            //测试用文本显示，很卡别用
           // Global.getCombatEngine().addFloatingText(target.getLocation(),"average"+Average_fluxlevel+"higher"+higher+"lower"+lower+"beamsnumber"+beamweaponshiplist_ship.size(),70f, Color.red,target,1f,1f);
            int Arc_spawner = ArcSpawnerList.getItems().size();
            if(Arc_spawner>0){
                if (interval.intervalElapsed()) {
                    float pierceChance = 0.1f;
                    pierceChance *= target.getMutableStats().getDynamic().getValue(Stats.SHIELD_PIERCED_MULT);
                    boolean piercedShieldarc =  (float) Math.random() < pierceChance;
                    if (piercedShieldarc) {
                        Global.getCombatEngine().spawnEmpArcPierceShields(
                                null,
                                target.getLocation(),
                                target,
                                target,
                                DamageType.ENERGY,
                                0f, // damage
                                Beams_number*50f, // emp
                                100000f, // max range
                                "ssp_fluxdeliversystem_emp_impact",
                                 1f,
                                new Color(10,155,90,255),
                                new Color(10,155,90,255)
                        );
                    }
                }
            }


        }
    }

}
