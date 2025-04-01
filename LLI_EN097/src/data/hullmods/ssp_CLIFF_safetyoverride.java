package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;

import java.awt.*;

public class ssp_CLIFF_safetyoverride extends ssp_CLIFF_basehullmod {
    private static final float PEAK_MULT = 0.33f;
    private static final float ExtraTimeMult = 2f;
    public IntervalUtil interval = new IntervalUtil(0.1f,0.1f);//最小,最大
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getPeakCRDuration().modifyMult(id, PEAK_MULT);
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        float Mult = ship.getMutableStats().getDynamic().getMod("ssp_CLIFF_morehp").computeEffective(1f);
        float hplosteverysecond=ship.getMaxHitpoints()/ship.getMutableStats().getPeakCRDuration().computeEffective(ship.getHullSpec().getNoCRLossTime())*(2-Mult);
        interval.advance(amount);
        //调整一下船体的外观
        if(ship.getShield()!=null){
            ship.getShield().setRingColor(new Color(200,40,60,255));
            ship.getShield().setInnerColor(new Color(215,10,30,60));
        }
        ship.getEngineController().fadeToOtherColor(this, new Color(215,10,30,255), null, 1f, 1f);
        //时流
        ship.getMutableStats().getTimeMult().modifyMult("ssp_CLIFF_safetyoverride",1+((1-(ship.getHitpoints()/ship.getMaxHitpoints())))*ExtraTimeMult);
        //可视化+子弹时间
        if(Global.getCombatEngine().getPlayerShip()==ship){
            Global.getCombatEngine().maintainStatusForPlayerShip("ssp_CLIFF_safetyoverride_effect0", "graphics/icons/hullsys/targeting_feed.png", SSPI18nUtil.getHullModString("ssp_CLIFF_safetyoverride_title0"),
                    String.format(SSPI18nUtil.getHullModString("ssp_CLIFF_safetyoverride0"), (int)(ship.getMutableStats().getTimeMult().modified*100)+"%"), false);
            Global.getCombatEngine().maintainStatusForPlayerShip("ssp_CLIFF_safetyoverride_effect1", "graphics/icons/hullsys/targeting_feed.png", SSPI18nUtil.getHullModString("ssp_CLIFF_safetyoverride_title1"),
                    String.format(SSPI18nUtil.getHullModString("ssp_CLIFF_safetyoverride1"), (int)(ship.getHitpoints()/hplosteverysecond)), true);
            Global.getCombatEngine().getTimeMult().modifyMult("ssp_CLIFF_safetyoverride", 1f / (1+(1-(ship.getHitpoints()/ship.getMaxHitpoints()))*ExtraTimeMult));
        }else {
            Global.getCombatEngine().getTimeMult().unmodify("ssp_CLIFF_safetyoverride");
        }
        //流失结构
        if(interval.intervalElapsed() && ship.isAlive()){
            if(ship.getHitpoints()-hplosteverysecond*0.1f>0){
            ship.setHitpoints(ship.getHitpoints()-hplosteverysecond*0.1f);//一秒10次
            }else if(ship.getHitpoints()-hplosteverysecond*0.1f<=0){
                Global.getCombatEngine().applyDamage(ship,ship.getLocation(),ship.getMaxHitpoints(), DamageType.ENERGY,0,true,false,null);
            }
        }
        //清除残骸的时流
        if(ship.isHulk()){ ship.getMutableStats().getTimeMult().unmodify("ssp_CLIFF_safetyoverride");}
    }
    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("safetyoverrides")) {
            return false;
        }
        return super.isApplicableToShip(ship);
    }
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if(ship==null)return;
        float opad = 10f;
        float Mult = ship.getMutableStats().getDynamic().getMod("ssp_CLIFF_morehp").computeEffective(1f);
        float hplosteverysecond=ship.getMaxHitpoints()/ship.getMutableStats().getPeakCRDuration().computeEffective(ship.getHullSpec().getNoCRLossTime())*(2-Mult);
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_CLIFF_safetyoverride_tooltip0"),
                opad, h, "67%",""+(int)hplosteverysecond,"x"+(int)(ExtraTimeMult+1),Global.getSettings().getHullModSpec("safetyoverrides").getDisplayName());
        label.setHighlight("67%",""+(int)hplosteverysecond,"x"+(int)(ExtraTimeMult+1),Global.getSettings().getHullModSpec("safetyoverrides").getDisplayName());
        label.setHighlightColors(h,h,h,h);
        tooltip.addPara(SSPI18nUtil.getHullModString("ssp_CLIFF_basehullmod"), Color.GRAY, opad);
    }
}
