package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class ssp_ThunderCharge extends BaseShipSystemScript {

    public static final Color JITTER_COLOR = new Color(145,145,255,100);
    public static final Color JITTER_UNDER_COLOR = new Color(100,100,255,225);
    protected float arc_number=0f;
    public static float MaxSpeed=50f;
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        float Range = 1000f;
        float arc = 40;
        float arc_fluxAmount = 25f;
        float Damage=2f;
        float Interval=1f;
        ShipAPI ship = null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
            id = id + "_" + ship.getId();
        } else {
            return; }
        //取得目标
        List<CombatEntityAPI> TargetInRange = CombatUtils.getEntitiesWithinRange(ship.getLocation(), Range);
        Vector2f from= MathUtils.getRandomPointInCircle(ship.getLocation(),ship.getCollisionRadius());
        if(ship.getVariant().hasHullMod("ssp_LongerRange")){ arc_fluxAmount=50; arc=80;Damage=3;Interval=0.60f;}
        if(ship.getVariant().hasHullMod("ssp_ShortRange")){ arc_fluxAmount=0;Interval=1.2f;}
        stats.getDynamic().getMod("ssp_ThunderCharge_HullmodDamage").modifyMult(id,Damage);
        stats.getDynamic().getMod("ssp_ThunderCharge_HullmodInterval").modifyMult(id,Interval);
        //放电
        for (CombatEntityAPI Target : TargetInRange) {
            if (arc <= arc_number) break;
            if (Target instanceof ShipAPI) {continue;}
            if (ThisMissileWillNotHit(Target,ship)) continue;
            if(effectLevel>arc_number/arc){
            if (Target.getOwner() != ship.getOwner() && Target instanceof MissileAPI) {
                    Global.getCombatEngine().spawnEmpArc(ship, from, ship, Target, DamageType.FRAGMENTATION, 80f, 0f, 10000, null, 10, JITTER_COLOR, JITTER_COLOR);
                    ship.getFluxTracker().increaseFlux(arc_fluxAmount, false);
                    arc_number++;
                }}
        }
        //加速
        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
            stats.getMaxTurnRate().unmodify(id);
        } else {
            stats.getMaxSpeed().modifyFlat(id, MaxSpeed );
            stats.getAcceleration().modifyPercent(id, 100f);
            stats.getDeceleration().modifyPercent(id, 100f);
            stats.getTurnAcceleration().modifyFlat(id, 10f);
            stats.getTurnAcceleration().modifyPercent(id, 100f);
            stats.getMaxTurnRate().modifyFlat(id, 10f);
            stats.getMaxTurnRate().modifyPercent(id, 50f);
        }
        //特效
        float jitterLevel = effectLevel;
        float jitterRangeBonus = 0;
        float maxRangeBonus = 10f;
        if (state == State.IN) {
            jitterLevel = effectLevel / (1f / ship.getSystem().getChargeUpDur());
            if (jitterLevel > 1) {
                jitterLevel = 1f;
            }
            jitterRangeBonus = jitterLevel * maxRangeBonus;
        } else if (state == State.ACTIVE) {
            jitterLevel = 1f;
            jitterRangeBonus = maxRangeBonus;
        } else if (state == State.OUT) {
            jitterRangeBonus = jitterLevel * maxRangeBonus;
        }
        jitterLevel = (float) Math.sqrt(jitterLevel);
        effectLevel *= effectLevel;
        ship.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel, 8, 0f, 6f + jitterRangeBonus);

    }
    public boolean ThisMissileWillNotHit(CombatEntityAPI missile ,ShipAPI ship){
        if (missile instanceof MissileAPI){
            //if(!(((MissileAPI) missile).getMissileAI() instanceof GuidedMissileAI)){
                //‘实际角’的角度
                float HullHitAngle= Math.abs(VectorUtils.getAngle(missile.getLocation(),ship.getLocation())-VectorUtils.getFacing(missile.getVelocity()));
                if(HullHitAngle>180){HullHitAngle=360-HullHitAngle;}
                //直角三角形的边-导弹与舰船距离
                float T1=Misc.getDistance(missile.getLocation(),ship.getLocation());
                //直角三角形的另一条边-舰船碰撞半径
                float T2=ship.getCollisionRadius();
                //直角三角形的斜边-仅计算用
                float T3=(float)Math.sqrt((T1*T1)+(T2*T2));
                //求‘计算角’的cos值
                float acosA=(float) Math.acos(((T3*T3)+(T1*T1)-(T2*T2))/(2*T3*T1));
                if(acosA>(Math.PI/2)){acosA= (float) (Math.PI-acosA);}
                //计算角如果小于实际角那么视为导弹不会击中
                if(Math.PI*(HullHitAngle/360)>acosA){return true;}
            //}
        }
        return false;
    }
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
            id = id + "_" + ship.getId();
        } else { return; }
        stats.getDynamic().getMod("ssp_ThunderCharge_HullmodDamage").unmodify(id);
        stats.getDynamic().getMod("ssp_ThunderCharge_HullmodInterval").unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
        arc_number=0f;
    }
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(SSPI18nUtil.getShipSystemString("ssp_ThunderCharge0"), false);
        }else if (index == 1) {
            return new StatusData(SSPI18nUtil.getShipSystemString("ssp_ThunderCharge1")+(int)MaxSpeed, false);
        }
        return null;
    }
}
