package data.shipsystems.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.shipsystems.ssp_missleswarm;
import org.lwjgl.util.vector.Vector2f;

public class ssp_missileswarmAI implements ShipSystemAIScript {

    ShipAPI ship;
    ShipSystemAPI system;
    ShipwideAIFlags flags;
    CombatEngineAPI engine;
    IntervalUtil thinkTracker = new IntervalUtil(10f,10f);
    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
        this.flags = flags;
        this.engine = engine;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        thinkTracker.advance(amount);
        ssp_missleswarm.ssp_missleswarm_customdata CustomData = (ssp_missleswarm.ssp_missleswarm_customdata) Global.getCombatEngine().getCustomData().get("ssp_missleswarm_mode");
        if (CustomData == null){ship.useSystem();}//先用一遍添加customdata
        if (target == null) return; //本舰没有锁定目标，取消
        if(CustomData!=null && CustomData.HaHaHashmap.get(ship)!=null ){
            if(CustomData.HaHaHashmap.get(ship) != WhichTarget(target)){ship.useSystem();}
            if(thinkTracker.intervalElapsed()){ship.useSystem();}
        }
    }
    public int WhichTarget(ShipAPI target){
        //0破片 1动能 2高爆
        int type = 0;
        if(target != null){
            if(target.getShield() == null){
                if(target.getHitpoints() <  target.getMaxHitpoints()*0.9f) {type=0;}
                if(target.getHitpoints() >= target.getMaxHitpoints()*0.9f) {type=2;}
            }
            if(target.getShield() != null){
                if(target.getFluxLevel()>0.9f|| target.getFluxTracker().isOverloadedOrVenting()){
                    if(target.getHitpoints() <  target.getMaxHitpoints()*0.9f) {type=0;}
                    if(target.getHitpoints() >= target.getMaxHitpoints()*0.9f) {type=2;}
                }else if(target.getFluxLevel()<=0.9f) {type=1;}
            }
            if(target.getHullSize() == ShipAPI.HullSize.FIGHTER) {type=0;}
        return type;
        }
        return 0;
    }
}
