package data.shipsystems.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.shipsystems.ssp_gonggong_system;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ssp_gonggong_systemAI implements ShipSystemAIScript {

    ShipAPI ship;
    ShipSystemAPI system;
    ShipwideAIFlags flags;
    CombatEngineAPI engine;
    IntervalUtil thinkTracker = new IntervalUtil(0.2f,0.4f);
    boolean shouldUse = false;
    public static float SyS_Range=800f;

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
        this.flags = flags;
        this.engine = engine;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        //每0.3秒左右进行一次AI思考
        thinkTracker.advance(amount);
        if(!thinkTracker.intervalElapsed()) return;

        //使用系统的意愿
        float willing = 0f;

        //如果本舰没有锁定目标，为其分配目标，分配结果为null则取消
        if(ship.getShipTarget() == null){ship.setShipTarget(findTarget(ship));}
        if(ship.getShipTarget() == null && !(ship.getShipTarget() instanceof ShipAPI)) return;


        ShipAPI t = ship.getShipTarget();
        if(t.getHullSize()== ShipAPI.HullSize.FIGHTER) return;//不锁战机
        if(ship.getMaxFlux()-ship.getCurrFlux()<900) return;//防止过载自己

        //自己状态不好的时候，直接使用系统击退双方
        if(t.getAIFlags() != null){
            if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.BACKING_OFF)) willing += 20f;
            if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.NEEDS_HELP)) willing += 20f;
            if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_AUTOFIRE_NON_ESSENTIAL_GROUPS)) willing += 25f;
        }
        //锁定目标越近越想用
        willing += 150*MathUtils.getDistance(ship,t)/SyS_Range;
        willing *= MathUtils.getRandomNumberInRange(0.75f,1.25f);//随机
        //目标幅能越高越想用
        willing += 60*t.getFluxLevel();
        //不浪费充能次数，满了就用
        if(ship.getSystem().getAmmo()/ship.getSystem().getMaxAmmo()==1){
            willing += 30* MathUtils.getRandomNumberInRange(0.8f,1.2f);
        }

        shouldUse = false;
        if(willing >= 100f) shouldUse = true;

        if(system.getState() == ShipSystemAPI.SystemState.IDLE && shouldUse){
            ship.useSystem();
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
}
