package data.shipsystems.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.ProjectileSpawnType;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.LazyLib;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

import static com.fs.starfarer.api.combat.ShipSystemAPI.SystemState.IDLE;

public class ssp_HitMeYouWeekMissileAI implements ShipSystemAIScript {

  ShipAPI ship;
  ShipSystemAPI system;
  ShipwideAIFlags flags;
  CombatEngineAPI engine;
  IntervalUtil thinkTracker = new IntervalUtil(0.2f,0.4f);
  boolean shouldUse = false;

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

    //本舰没有锁定目标，取消思考
    if(ship.getShipTarget() == null && !(ship.getShipTarget() instanceof ShipAPI)) return;

    ShipAPI t = ship.getShipTarget();
    if(t.getHullSize()== ShipAPI.HullSize.FIGHTER) return;//不锁战机
    //这里的检索距离应该直接引用system文件里面的变量，但是设置了私有无法访问所以写死了
    //计算目标敌人周围所有敌对导弹的伤害总量
    float total = 1f;
    for( MissileAPI m : AIUtils.getNearbyEnemyMissiles(t,1400f)){
      if(m == null) continue;
      //if(m.getProjectileSpec().getSpawnType() != ProjectileSpawnType.MISSILE) continue;
      //对于破片导弹，判断实际伤害是纸面伤害/4
      if(m.getDamage().getType() == DamageType.FRAGMENTATION) {
        total += m.getDamage().getDamage()/4f;
      }else {
        total += m.getDamage().getDamage();
      }

    }

    //向log中打印本次思考的总伤害数据
    //Global.getLogger(this.getClass()).info(total+"");

    //周围导弹总伤害越高越想用技能
    //理论上当附近有超过2000总伤害的导弹时就想用技能了，所以设定当伤害为2000时，willing差不多到100
    willing += total/25f;
    willing *= MathUtils.getRandomNumberInRange(0.75f,1.25f);

    //目标幅能状态越差，越想用技能，如果目标幅能满了，周围只有1000伤害也会强开f，willing给到差不多50
    willing += (t.getFluxLevel() * 50f );

    //自己状态不好的时候，越不想使用系统增加自己的幅能负担
    if(t.getAIFlags() != null){
      if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.BACKING_OFF)) willing -= 10f;
      if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.NEEDS_HELP)) willing -= 10f;
      if(ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_AUTOFIRE_NON_ESSENTIAL_GROUPS)) willing -= 15f;
    }

    shouldUse = false;
    if(willing >= 100f) shouldUse = true;

    if(system.getState() == ShipSystemAPI.SystemState.IDLE && shouldUse){
      ship.useSystem();
    }
  }


}
