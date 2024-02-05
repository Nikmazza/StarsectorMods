package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

import com.fs.starfarer.combat.ai.missile.MissileAI;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class ssp_HitMeYouWeekMissile extends BaseShipSystemScript {
    public static Object KEY_SHIP = new Object();
    public static Object KEY_TARGET = new Object();

    public static float SYSTEM_RANGE = 2500f;

    public IntervalUtil interval = new IntervalUtil(0.1f,0.1f);//最小,最大

    public static Color JITTER_COLOR = new Color(255,55,55,255);
    public static Color JITTER_UNDER_COLOR = new Color(255,55,55,255);

    public static class TargetData {
        public ShipAPI ship;
        public ShipAPI target;
        public EveryFrameCombatPlugin targetEffectPlugin;
        public BaseCombatLayeredRenderingPlugin targetRenderPlugin;
        public float Stage;
        public float elaspedAfterInState;
        public TargetData(ShipAPI ship, ShipAPI target) {
            this.ship = ship;
            this.target = target;
        }
    }

    public void apply(MutableShipStatsAPI stats, final String id, State state, final float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        float RANGE =800f;
        float DAMAGE_MULT = 1.10f;
        if (ship.getVariant().hasHullMod("ssp_LongerRange")) {DAMAGE_MULT=1.20f;}
//        if (ship.getVariant().hasHullMod("ssp_ShortRange")) {RANGE=1000f;}
        final String targetDataKey = ship.getId()+"ssp_HitMeYouWeekMissile_target_data";

        Object targetDataObj = Global.getCombatEngine().getCustomData().get(targetDataKey);

        if (state == State.IN && targetDataObj == null ) {
            ShipAPI target = findTarget(ship);
            Global.getCombatEngine().getCustomData().put(targetDataKey, new ssp_HitMeYouWeekMissile.TargetData(ship, target));
        } else if (state == State.IDLE && targetDataObj != null) {
            Global.getCombatEngine().getCustomData().remove(targetDataKey);
            ((ssp_HitMeYouWeekMissile.TargetData)targetDataObj).Stage = 1f;
            targetDataObj = null;
        }
        if (targetDataObj == null || ((ssp_HitMeYouWeekMissile.TargetData) targetDataObj).target == null) return;

        final ssp_HitMeYouWeekMissile.TargetData targetData = (ssp_HitMeYouWeekMissile.TargetData) targetDataObj;
        targetData.Stage = 1f+ effectLevel ;
        if (targetData.targetEffectPlugin == null) {
            final float finalRANGE = RANGE;
            final float finalDAMAGE_MULT = DAMAGE_MULT;
            //吸引导弹
            targetData.targetEffectPlugin = new BaseEveryFrameCombatPlugin() {
                @Override
                public void advance(float amount, List<InputEventAPI> events) {
                    interval.advance(amount);
                    if (Global.getCombatEngine().isPaused()) return;
                    if (targetData.target==null) return;
                    //如果玩家被锁定则左侧出现提示文本
                    if (targetData.target == Global.getCombatEngine().getPlayerShip()) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(KEY_TARGET,
                                targetData.ship.getSystem().getSpecAPI().getIconSpriteName(),
                                targetData.ship.getSystem().getDisplayName(),
                                SSPI18nUtil.getShipSystemString("ssp_HitMeYouWeekMissile_enemy"), true);
                    }
                    //移除易伤效果
                    if (targetData.Stage <= 1f || targetData.target.isHulk()) {
                        targetData.target.getMutableStats().getHullDamageTakenMult().unmodify(id);
                        targetData.target.getMutableStats().getArmorDamageTakenMult().unmodify(id);
                        targetData.target.getMutableStats().getShieldDamageTakenMult().unmodify(id);
                        targetData.target.getMutableStats().getEmpDamageTakenMult().unmodify(id);
                        Global.getCombatEngine().removePlugin(targetData.targetEffectPlugin);
                    } else {//添加易伤效果
                        targetData.target.getMutableStats().getHullDamageTakenMult().modifyMult(id, finalDAMAGE_MULT);
                        targetData.target.getMutableStats().getArmorDamageTakenMult().modifyMult(id, finalDAMAGE_MULT);
                        targetData.target.getMutableStats().getShieldDamageTakenMult().modifyMult(id, finalDAMAGE_MULT);
                        targetData.target.getMutableStats().getEmpDamageTakenMult().modifyMult(id, finalDAMAGE_MULT);
                        //间隔一段时间施加偏转力度调整导弹的速度方向
                        if (interval.intervalElapsed()) {
                            for (MissileAPI M : AIUtils.getNearbyEnemyMissiles(targetData.target, finalRANGE)) {
                                //DEM豁免检定
                                if(M.getSpec().getBehaviorSpec()!=null && M.getSpec().getBehaviorSpec().getBehavorString()!=null && M.getSpec().getBehaviorSpec().getBehavorString().equals("CUSTOM")) continue;
                                //被吸引前的速度
                                final float speed=M.getVelocity().length();
                                //一个方向向量
                                Vector2f d = VectorUtils.getDirectionalVector(M.getLocation(), targetData.target.getLocation());
                                //根据距离设置力度
                                float ScaleMult = 1.1f - (Misc.getDistance(M.getLocation(), targetData.target.getLocation()))/finalRANGE;
                                if(ScaleMult>1f)ScaleMult=1f;
                                if(ScaleMult<0f)ScaleMult=0f;
                                //施加偏转力
                                d.scale(0.3f*ScaleMult*(float) Math.sqrt(M.getVelocity().lengthSquared()));
                                Vector2f.add(d, M.getVelocity(), M.getVelocity());
                                //调整(X,Y)以恢复被吸引前的速度
                                float SUOFANG=speed/M.getVelocity().length();
                                M.getVelocity().setX(M.getVelocity().getX()*SUOFANG);
                                M.getVelocity().setY(M.getVelocity().getY()*SUOFANG);
                                //M.setFacing(VectorUtils.getFacing(M.getVelocity()));//这会导致一些导弹乱飞
                            }
                        }
                    }
                }
            };
            //吸力范围显示
            targetData.targetRenderPlugin = new BaseCombatLayeredRenderingPlugin() {
                public void render(CombatEngineLayers layer, ViewportAPI viewport) {
                        SpriteAPI sprite = Global.getSettings().getSprite("fx", "ssp_HitMeYouWeekMissile");
                        sprite.setColor(new Color(255, 55, 55, 120));
                        sprite.setWidth(finalRANGE*2);
                        sprite.setHeight(finalRANGE*2);
                        sprite.setAdditiveBlend();
                        sprite.setAlphaMult(1f);
                        sprite.renderAtCenter(targetData.target.getLocation().x, targetData.target.getLocation().y);
                }
                @Override
                public float getRenderRadius() {
                    return 5000;
                }
                @Override
                public boolean isExpired() {
                    return targetData.Stage <= 1f || targetData.target.isHulk();
                }
            };
            Global.getCombatEngine().addLayeredRenderingPlugin(targetData.targetRenderPlugin);
            Global.getCombatEngine().addPlugin(targetData.targetEffectPlugin);
        }


        if (effectLevel > 0) {
            if (state != ShipSystemStatsScript.State.IN) {
                targetData.elaspedAfterInState += Global.getCombatEngine().getElapsedInLastFrame();
            }
            float shipJitterLevel = 0;
            if (state == ShipSystemStatsScript.State.IN) {
                shipJitterLevel = effectLevel;
            } else {
                float durOut = 0.5f;
                shipJitterLevel = Math.max(0, durOut - targetData.elaspedAfterInState) / durOut;
            }

            float maxRangeBonus = 50f;
            float jitterRangeBonus = shipJitterLevel * maxRangeBonus;

            if (shipJitterLevel > 0) {
                targetData.target.setJitter(KEY_SHIP, JITTER_COLOR, shipJitterLevel, 8, 10f, 10 + jitterRangeBonus);
            }
            if (effectLevel > 0) {
                targetData.target.setJitterUnder(KEY_TARGET, JITTER_UNDER_COLOR, effectLevel, 8, 10f, 10 + jitterRangeBonus);
            }
        }
    }


    public void unapply(MutableShipStatsAPI stats, String id) {

    }
    protected ShipAPI findTarget(ShipAPI ship) {
        float range = getMaxRange(ship);
        boolean player = ship == Global.getCombatEngine().getPlayerShip();
        ShipAPI target = ship.getShipTarget();

        if (ship.getShipAI() != null && ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.TARGET_FOR_SHIP_SYSTEM)){
            target = (ShipAPI) ship.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.TARGET_FOR_SHIP_SYSTEM);
        }

        if (target != null) {
            float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
            float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
            if (dist > range + radSum) target = null;
        } else {
            if (target == null || target.getOwner() == ship.getOwner()) {
                if (player) {
                    target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), ShipAPI.HullSize.FRIGATE, range, true);
                } else {
                    Object test = ship.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.MANEUVER_TARGET);
                    if (test instanceof ShipAPI) {
                        target = (ShipAPI) test;
                        float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
                        float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
                        if (dist > range + radSum) target = null;
                    }
                }
            }
            if (target == null) {
                target = Misc.findClosestShipEnemyOf(ship, ship.getLocation(), ShipAPI.HullSize.FRIGATE, range, true);
            }
        }

        return target;
    }
    
    public static float getMaxRange(ShipAPI ship) {
        float R=0f;
        if(ship.getVariant().hasHullMod("ssp_ShortRange")){R=500f;}
        return ship.getMutableStats().getSystemRangeBonus().computeEffective(SYSTEM_RANGE+R);
        //return RANGE;
    }


    public ShipSystemStatsScript.StatusData getStatusData(int index, State state, float effectLevel) {
        if (effectLevel > 0) {
            if (index == 0) {
                return new ShipSystemStatsScript.StatusData(SSPI18nUtil.getShipSystemString("ssp_HitMeYouWeekMissile"), false);
            }
        }
        return null;
    }


    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (system.isOutOfAmmo()) return null;
        if (system.getState() != ShipSystemAPI.SystemState.IDLE) return null;

        ShipAPI target = findTarget(ship);
        if (target != null && target != ship) {
            return "Ready";
        }
        if ((target == null) && ship.getShipTarget() != null) {
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
    public float getActiveOverride(ShipAPI ship) {
        if (ship != null) {
            if (ship.getVariant().hasHullMod("ssp_LongerRange")) {return 10f; }
        }
        return -1;
    }

}



