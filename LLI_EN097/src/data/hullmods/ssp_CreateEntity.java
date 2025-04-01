package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ssp_CreateEntity {
    public static class ProcessEntityMoving extends BaseCombatLayeredRenderingPlugin {
        public MissileAPI entity;
        public float time = 0f;
        //最大飞行时间
        public float lifeTime = 20f;
        public boolean shouldEnd = false;
        public float toHeal = 10f;
        public ShipAPI to;
        ProcessEntityMoving(MissileAPI missile, float toHeal, ShipAPI to){
            entity = missile;
            this.toHeal = toHeal;
            this.to = to;
        }
        @Override
        public void advance(float amount) {
            time+=amount;
            //理论上一个没有Collision并且不渲染目标方框的导弹不应该被任何东西干扰
            //但是架不住新手乱写
            //监测这个导弹是否依旧在战场上，存在时间是否超时，ai是否被人篡改
            if(!Global.getCombatEngine().isEntityInPlay(entity)) shouldEnd = true;
            if(!Global.getCombatEngine().isEntityInPlay(to) || !to.isAlive()) shouldEnd = true;
            if(time > lifeTime) shouldEnd = true;
            if(!(entity.getMissileAI() instanceof GuidedMissileAI)) shouldEnd = true;
            GuidedMissileAI ai = (GuidedMissileAI) entity.getMissileAI();
            CombatEntityAPI target = ai.getTarget();
            if(target!= to) {
                ai.setTarget(to);
            }
            //如果导弹受到干扰，取消效果并且移除导弹
            if(shouldEnd) return;
            //单纯检测距离用平方可以少一道开方，开方是非常消耗算力的，平方无所谓
            Vector2f targetLoc = to.getLocation();
            float dist = to.getCollisionRadius() * 0.75f;
            if(MathUtils.getDistanceSquared(entity.getLocation(), targetLoc) < dist * dist && target!=null){
                //回血
                float lostHp = target.getMaxHitpoints() - target.getHitpoints();//掉了多少血
                float Mult = ((ShipAPI) target).getMutableStats().getDynamic().getMod("ssp_CLIFF_morehp").computeEffective(1f);
                if(lostHp > 0f){
                    lostHp = Math.min(lostHp, toHeal*Mult);
                    target.setHitpoints(target.getHitpoints() + lostHp);
                    Global.getCombatEngine().addFloatingDamageText(to.getLocation(), lostHp, Color.GREEN, to, null);
                }else if(lostHp <= 0f){
                    target.setHitpoints(target.getHitpoints() + toHeal*Mult*0.1f);
                    ((ShipAPI) target).setMaxHitpoints(target.getMaxHitpoints() + toHeal*Mult*0.1f);
                    Global.getCombatEngine().addFloatingDamageText(to.getLocation(), toHeal*Mult*0.1f, Color.GREEN, to, null);
                }
                //---------------------------------//
                //TODO: 你要加什么到达时的特效就在这里加

                //---------------------------------//
                shouldEnd = true;
            }
            //因为导弹是隐形的，每秒在导弹的位置生产一个绿点debug用
            //Global.getCombatEngine().addSmoothParticle(entity.getLocation(),Misc.ZERO,20f,0.2f,Global.getCombatEngine().getElapsedInLastFrame()*4f,Color.green);
        }
        @Override
        public boolean isExpired() {
            if(shouldEnd) {
                //手动删除弹丸
                Global.getCombatEngine().removeEntity(entity);
                //返回为true一次以后，这个plugin会被自动移除
                return true;
            }
            return false;
        }
    }
}
