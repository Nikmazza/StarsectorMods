package data.shipsystems.ai;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class ssp_ThunderChargeAI implements ShipSystemAIScript {

    ShipAPI ship;
    ShipSystemAPI system;
    ShipwideAIFlags flags;
    CombatEngineAPI engine;
    IntervalUtil thinkTracker = new IntervalUtil(0.2f,0.4f);
    boolean shouldUse = false;

    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.flags = flags;
        this.engine = engine;
        this.system = system;
    }

    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {

        thinkTracker.advance(amount);
        if(!thinkTracker.intervalElapsed()) return;
        //使用系统的意愿
        float willing = 0f;
        float MissileHitPointTotal = 1f;
        //用于防空
        for( MissileAPI m : AIUtils.getNearbyEnemyMissiles(ship,1000f)){
            if(m == null) continue;
            MissileHitPointTotal += m.getHitpoints();
        }
        //用于攻击敌人舰船，对战机防空
        for( ShipAPI s :AIUtils.getNearbyEnemies(ship,1000f)){
            if(s.getHullSize()== ShipAPI.HullSize.CAPITAL_SHIP){willing+=1* (1-Math.sqrt(ship.getFluxLevel()));}
            if(s.getHullSize()== ShipAPI.HullSize.CRUISER){willing+=2* (1-Math.sqrt(ship.getFluxLevel()));}
            if(s.getHullSize()== ShipAPI.HullSize.DESTROYER){willing+=6* (1-Math.sqrt(ship.getFluxLevel()));}
            if(s.getHullSize()== ShipAPI.HullSize.FRIGATE){willing+=10* (1-Math.sqrt(ship.getFluxLevel()));}
            if(s.getHullSize()== ShipAPI.HullSize.FIGHTER && s.getShield()==null){MissileHitPointTotal+=s.getHitpoints()*1.5f;}
            if(s.getHullSize()== ShipAPI.HullSize.FIGHTER && s.getShield()!=null){MissileHitPointTotal+=(s.getHitpoints()+s.getMaxFlux()*4)*1.5f;}
        }
        //周围导弹总血量越高越想用技能
        //理论上当附近有超过1000总血量的导弹、战机时就想用技能了，所以设定当血量为1000时，willing差不多到100
        willing += MissileHitPointTotal/10f;
        willing *= MathUtils.getRandomNumberInRange(0.75f,1.25f);
        //自己状态不好的时候，使用系统跑路
        if(ship.getAIFlags() != null){
            if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.BACKING_OFF)) willing += 30f;
            if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.NEEDS_HELP)) willing += 30f;
            if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_AUTOFIRE_NON_ESSENTIAL_GROUPS)) willing += 35f;
        }
        //防止过载自己
        if(ship.getVariant().hasHullMod("ssp_LongerRange")){
            if(ship.getMaxFlux()-ship.getFluxTracker().getCurrFlux() < 4000){return;}
        }else if(!ship.getVariant().hasHullMod("ssp_ShortRange") && !ship.getVariant().hasHullMod("ssp_LongerRange")){
            if(ship.getMaxFlux()-ship.getFluxTracker().getCurrFlux() < 1000){return;}
        }
        //使用技能赶路
        if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.MANEUVER_RANGE_FROM_TARGET)||ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.MANEUVER_TARGET)){willing += 100f;}
        //最终判断阶段
        shouldUse = false;
        if(willing >= 100f) shouldUse = true;
        if(system.getState() == ShipSystemAPI.SystemState.IDLE && shouldUse){
            ship.useSystem();
        }

    }
}
