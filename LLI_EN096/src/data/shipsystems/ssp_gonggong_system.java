package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;
import data.SSPMisc;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ssp_gonggong_system extends BaseShipSystemScript {
    public static float SyS_Range=800f;
    protected boolean RunOnce=false;
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship=(ShipAPI)stats.getEntity();
        if(!RunOnce){
            RunOnce=true;
            if(ship.getVariant().hasHullMod("ssp_LongerRange")){ship.getFluxTracker().increaseFlux(ship.getSystem().getFluxPerUse()*2,true);}
        }
    }
    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship=(ShipAPI)stats.getEntity();
        RunOnce=false;
        ShipAPI Target=findTarget(ship);
        if(Target!=null){
            //船体插件补正
            float F = 200f;
            float D = 1f;
            if(ship.getVariant().hasHullMod("ssp_LongerRange")){ F=400f; D=2f;}
            //释放距离补正
            float DistanceModify=0.3f+0.7f*SyS_Range/MathUtils.getDistance(ship.getLocation(),Target.getLocation());
            //舰船级别补正
            float HullSizeModify=1f;
            if(Target.getHullSize() == ShipAPI.HullSize.FRIGATE){HullSizeModify=1.1f;}
            else if(Target.getHullSize() == ShipAPI.HullSize.DESTROYER){HullSizeModify=0.9f;}
            else if(Target.getHullSize() == ShipAPI.HullSize.CRUISER){HullSizeModify=0.65f;}
            else if(Target.getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP){HullSizeModify=0.3f;}
            //计算力度施加角度
            float Angle = VectorUtils.getAngle(ship.getLocation(), Target.getLocation());
            //施加力度并阻碍转向
            Target.getVelocity().set(VectorUtils.rotate(new Vector2f(F*DistanceModify*HullSizeModify, 0), Angle));
            ship.getVelocity().set(VectorUtils.rotate(new Vector2f(F*DistanceModify*HullSizeModify*-0.25f, 0), Angle));
            Target.setAngularVelocity(Target.getAngularVelocity()*(100/F));
            for(int i=0;i<10;i++){
                Global.getCombatEngine().spawnEmpArc(
                        ship,
                        MathUtils.getRandomPointInCircle(ship.getLocation(),ship.getCollisionRadius()*0.8f),
                        ship,
                        Target,
                        DamageType.ENERGY,
                        70f*D*DistanceModify/HullSizeModify,
                        0f,
                        10000,
                        null,
                        1,
                        new Color(240, 25, 100,150),
                        new Color(255, 45, 95, 255));
            }
            for(int u=0;u<40;u++){
                float Random=MathUtils.getRandomNumberInRange(1f,3f);
                Global.getCombatEngine().addNegativeNebulaParticle(
                        Target.getLocation(),
                        //Vel.set(VectorUtils.rotate(new Vector2f(10f*Random, 0f), MathUtils.getRandomNumberInRange(0,360))),
                        MathUtils.getPointOnCircumference(null,100*Random, MathUtils.getRandomNumberInRange(0f, 360f)),
                        60f,
                        1.4f,
                        0.1f,
                        1f,
                        MathUtils.getRandomNumberInRange(1f, 2f),
                        SSPMisc.Anti_Color(new Color(240, 25, 100,255))
                );
            }
        }
    }

    protected ShipAPI findTarget(ShipAPI ship) {
        boolean player = ship == Global.getCombatEngine().getPlayerShip();
        ShipAPI target = ship.getShipTarget();
        if (ship.getShipAI() != null && ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.TARGET_FOR_SHIP_SYSTEM)){
            target = (ShipAPI) ship.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.TARGET_FOR_SHIP_SYSTEM);
        }
        if (target != null) {
            float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
            float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
            if (dist > SyS_Range + radSum) target = null;
        } else {
            if (target == null || target.getOwner() == ship.getOwner()) {
                if (player) {
                    target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), ShipAPI.HullSize.FRIGATE, SyS_Range, true);
                } else {
                    Object test = ship.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.MANEUVER_TARGET);
                    if (test instanceof ShipAPI) {
                        target = (ShipAPI) test;
                        float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
                        float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
                        if (dist > SyS_Range + radSum) target = null;
                    }
                }
            }
            if (target == null) {
                target = Misc.findClosestShipEnemyOf(ship, ship.getLocation(), ShipAPI.HullSize.FRIGATE, SyS_Range, true);
            }
        }
        return target;
    }
    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (system.isOutOfAmmo()) return null;
        if (system.getState() != ShipSystemAPI.SystemState.IDLE) return null;

        ShipAPI target = findTarget(ship);
        if (target != null && target != ship) {
            return "Ready";
        }
        if (target == null && ship.getShipTarget() != null) {
            return "Too Far";
        }
        return "No Target";
    }
    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        //if (true) return true;
        ShipAPI target = findTarget(ship);
        if(target != null && target.getHullSize()== ShipAPI.HullSize.FIGHTER){return false;}
        return target != null && target != ship;
    }
    @Override
    public int getUsesOverride(ShipAPI ship) {
        if (ship != null) {
            if (ship.getVariant().hasHullMod("ssp_ShortRange")) {return 9; }
        }
        return -1;
    }
}
