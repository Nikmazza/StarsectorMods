package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class ssp_SystemUsedTimes extends BaseHullMod {
    public static final Color COLOR = new Color(255,95,95,150);
    public static final Color COLOR2 = new Color(255,195,195,150);
    public static float dealdamage=50f;
    public static float Range = 300f;
    public IntervalUtil interval = new IntervalUtil(0.8f,2f);//最小,最大
    protected float ArcFinalRange=0f;
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if(index == 0) return (int)dealdamage+"";
        return null;
    }
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return true; }
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float opad = 0f;
        Color h = Misc.getHighlightColor();
        ArcFinalRange=ship.getCollisionRadius()+Range;
        tooltip.addPara(SSPI18nUtil.getHullModString("ssp_SystemUsedTimes_Description"), opad, h, String.valueOf((int)ArcFinalRange),Global.getSettings().getShipSystemSpec("ssp_ThunderCharge").getName());
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        float DamageMult = ship.getMutableStats().getDynamic().getMod("ssp_ThunderCharge_HullmodDamage").computeEffective(1f);
        float IntervalMult = ship.getMutableStats().getDynamic().getMod("ssp_ThunderCharge_HullmodInterval").computeEffective(1f);
        if(ship.isHulk()){return;}
        //取得目标
        List<CombatEntityAPI> TargetInRange = CombatUtils.getEntitiesWithinRange(ship.getLocation(), (Range+ship.getCollisionRadius())*DamageMult);
        Vector2f from=MathUtils.getRandomPointInCircle(ship.getLocation(),ship.getCollisionRadius());
        interval.advance(amount * 1.5f * IntervalMult);//数值越大，频率越快
        if (interval.intervalElapsed()) {
            for (CombatEntityAPI Target : TargetInRange) {
                if(Target instanceof ShipAPI && ((ShipAPI) Target).isHulk()) continue;
                if (Target.getOwner() != ship.getOwner() && Target.getCollisionClass() == CollisionClass.SHIP) {
                    Global.getCombatEngine().spawnEmpArc(ship, from, null, Target, DamageType.ENERGY, dealdamage* DamageMult, 0, 100000, null, 10, COLOR, COLOR2);
//                    float Angle = VectorUtils.getAngle(ship.getLocation(), Target.getLocation());
//                    float F =  ship.getMass()/(Target.getMass()*1.5f);
//                   Target.getVelocity().set(VectorUtils.rotate(new Vector2f((100f+50*Random)*F, 50f*Random*F), Angle));
                }
                if(Target.getOwner() != ship.getOwner() && Target.getCollisionClass() == CollisionClass.FIGHTER) {
                    Global.getCombatEngine().spawnEmpArc(ship, from, null, Target, DamageType.ENERGY, dealdamage* DamageMult, 0, 100000, null, 10, COLOR, COLOR2);
                }
            }
        }
    }
}
