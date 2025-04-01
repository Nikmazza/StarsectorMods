package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ssp_CLIFF_fluxdepression extends ssp_CLIFF_basehullmod {
    public static float HPreturnPercent=0.8f;//结构返还比率
    public static float Depression_mult=1f;//基于每秒耗散的比例
    public IntervalUtil interval = new IntervalUtil(0.1f,0.1f);//最小,最大
    Color COLOR = new Color(100, 20, 20,100);
    Color COLOR2 = new Color(165, 5, 5, 255);
    public class ssp_CLIFF_fluxdepression_customdata{
        Map<ShipAPI, Float> HaHaHashmap = new HashMap();
        Map<ShipAPI, CombatEntityAPI> Spritemap = new HashMap();
        public ssp_CLIFF_fluxdepression_customdata(){}
    }
    @Override
    public void advanceInCombat(final ShipAPI ship, float amount) {
        float Mult = ship.getMutableStats().getDynamic().getMod("ssp_CLIFF_morehp").computeEffective(1f);
        interval.advance(amount);
        if(ship.isAlive()){
            ssp_CLIFF_fluxdepression_customdata CustomData = (ssp_CLIFF_fluxdepression_customdata) Global.getCombatEngine().getCustomData().get("ssp_CLIFF_fluxdepression");
            if(CustomData==null){
                CustomData = new ssp_CLIFF_fluxdepression_customdata();
                Global.getCombatEngine().getCustomData().put("ssp_CLIFF_fluxdepression",CustomData);
            }
            float value;
            if (CustomData.HaHaHashmap.containsKey(ship)) {
                value = (Float)CustomData.HaHaHashmap.get(ship);
            } else {
                value = 0F;
            }
//            if(!CustomData.Spritemap.containsKey(ship)){
//                CombatEntityAPI e = Global.getCombatEngine().addLayeredRenderingPlugin(new ssp_CLIFF_fluxdepression_render(ship));
//                CustomData.Spritemap.put(ship,e);
//            }
            if(interval.intervalElapsed()){
                if(ship.getHardFluxLevel()>0.98f && ship.getFluxLevel()<0.51f && ship.getCurrFlux()-ship.getFluxTracker().getHardFlux()<ship.getMaxFlux()*0.02f){ return;}//我也不知道我在防什么
                if(ship.getFluxLevel()>0.50f && !ship.getFluxTracker().isOverloadedOrVenting() && ship.getHitpoints()/ship.getMaxHitpoints()>0.05f){
                    ship.getFluxTracker().decreaseFlux(ship.getMutableStats().getFluxDissipation().modified*Depression_mult*0.1f);//降低幅能，每秒总共执行10次
                    ship.setHitpoints(ship.getHitpoints()-ship.getMutableStats().getFluxDissipation().modified*Depression_mult*0.1f*(2-Mult));
                    value+=ship.getMutableStats().getFluxDissipation().modified*Depression_mult*0.1f*(2-Mult)*(HPreturnPercent*Mult);//记录返还结构，返还部分消耗结构值，受其他插件加成
                    value = MathUtils.clamp(value, 0f, Float.MAX_VALUE);
                    Vector2f Vel = new Vector2f(0, 0);
                    for(int p=0;p<4;p++){
                        float angle = MathUtils.getRandomNumberInRange(0, 360);
                        Global.getCombatEngine().addNebulaSmoothParticle(MathUtils.getPoint(ship.getLocation(), ship.getCollisionRadius() * 0.75f, angle), Vel.set(VectorUtils.rotate(new Vector2f(200f, 0f), angle)), ship.getCollisionRadius() * 0.2f, 0.5f, 0.1f, 0.1f, 0.3f, COLOR);
                    }
                    for(int p=0;p<4;p++){
                        float angle = MathUtils.getRandomNumberInRange(0, 360);
                        Global.getCombatEngine().addNebulaParticle(MathUtils.getPoint(ship.getLocation(), ship.getCollisionRadius() * 0.55f, angle), Vel.set(VectorUtils.rotate(new Vector2f(170f, 0f), angle)), ship.getCollisionRadius() * 0.2f, 0.5f, 0.1f, 0.1f, 0.3f, COLOR);
                    }
                    CustomData.HaHaHashmap.put(ship,value);
                } else if(ship.getFluxLevel()<0.50f && value > 0f && ship.getHitpoints()<ship.getMaxHitpoints()){
                    float re=Math.min(value,ship.getMutableStats().getFluxDissipation().modified*Depression_mult*0.1f);
                    ship.setHitpoints(ship.getHitpoints()+re);
                    value-=re;
                    value = MathUtils.clamp(value, 0f, Float.MAX_VALUE);
                    CustomData.HaHaHashmap.put(ship,value);
                }
            }
            if (Global.getCombatEngine().getPlayerShip() == ship) {
                Global.getCombatEngine().maintainStatusForPlayerShip("ssp_CLIFF_fluxdepression_effect", "graphics/icons/hullsys/targeting_feed.png", SSPI18nUtil.getHullModString("ssp_CLIFF_fluxdepression_title"),
                        String.format(SSPI18nUtil.getHullModString("ssp_CLIFF_fluxdepression"), (int) value), false);
            }
        }
    }
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if(ship==null)return;
        float opad = 10f;
        float Mult = ship.getMutableStats().getDynamic().getMod("ssp_CLIFF_morehp").computeEffective(1f);
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_CLIFF_fluxdepression_tooltip0"),
                opad, h, "50%",""+(int)(ship.getMutableStats().getFluxDissipation().modified*Depression_mult*(2-Mult)),""+(int)(ship.getMutableStats().getFluxDissipation().modified*Depression_mult),"50%",""+(int)(HPreturnPercent*100*Mult)+"%");
        label.setHighlight("50%",""+(int)(ship.getMutableStats().getFluxDissipation().modified*Depression_mult*(2-Mult)),""+(int)(ship.getMutableStats().getFluxDissipation().modified*Depression_mult),"50%",""+(int)(HPreturnPercent*100*Mult)+"%");
        label.setHighlightColors(h,h,h,h,h);
        tooltip.addPara(SSPI18nUtil.getHullModString("ssp_CLIFF_basehullmod"), Color.GRAY, opad);
    }
//    public static class ssp_CLIFF_fluxdepression_render extends BaseCombatLayeredRenderingPlugin {
//        protected ShipAPI ship;
//        public ssp_CLIFF_fluxdepression_render(ShipAPI ship) {
//            this.ship = ship;
//        }
//        public boolean isExpired() {
//            return ship.isHulk();
//        }
//        public void render(CombatEngineLayers layer, ViewportAPI viewport) {
//            ssp_CLIFF_fluxdepression_customdata CustomData = (ssp_CLIFF_fluxdepression_customdata) Global.getCombatEngine().getCustomData().get("ssp_CLIFF_fluxdepression");
//            if(CustomData==null)return;
//            float value;
//            if (CustomData.HaHaHashmap.containsKey(ship)) {
//                value = (Float)CustomData.HaHaHashmap.get(ship);
//            } else {
//                value = 0F;
//            }
//            if(ship.getFluxLevel()>0.50f && !ship.getFluxTracker().isOverloadedOrVenting() && ship.getHitpoints()/ship.getMaxHitpoints()>0.05f){
//
//            }else if(ship.getFluxLevel()<=0.50f && value > 0f && ship.getHitpoints()<ship.getMaxHitpoints()){
//
//            }
//        }
//    }
}