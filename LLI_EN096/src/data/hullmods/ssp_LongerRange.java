package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ssp_LongerRange extends BaseHullMod {
    private static Map mag = new HashMap();
    static {
        mag.put(ShipAPI.HullSize.FRIGATE, 1f);
        mag.put(ShipAPI.HullSize.DESTROYER, 2f);
        mag.put(ShipAPI.HullSize.CRUISER, 3f);
        mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 4f);
    }
    public static float MANEUVER_BONUS = -30f;
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if(stats.getVariant()!=null && this.spec!=null && Misc.isSpecialMod(stats.getVariant(),this.spec)){
            stats.getVariant().removePermaMod(this.spec.getId());
        }
        stats.getMaxSpeed().modifyPercent(id, MANEUVER_BONUS);
        stats.getAcceleration().modifyPercent(id, MANEUVER_BONUS);
        stats.getDeceleration().modifyPercent(id, MANEUVER_BONUS);
        stats.getTurnAcceleration().modifyPercent(id, MANEUVER_BONUS);
        stats.getMaxTurnRate().modifyPercent(id, MANEUVER_BONUS);
        stats.getZeroFluxSpeedBoost().modifyFlat(id,stats.getMaxSpeed().getBaseValue()*-MANEUVER_BONUS*0.01f);
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        //ship.addListener(new ssp_Listener_LongerRange(ship, id));
    }
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff") && !ship.getVariant().hasHullMod("ssp_LowerThreshold")&& !ship.getVariant().hasHullMod("ssp_ShortRange");
    }
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("ssp_cliff")) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }
    //新描述哦
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_LongerRange_tooltip0"),
                opad, h, "30"+"%");
        label.setHighlight("30"+"%");
        label.setHighlightColors(b,h);

        tooltip.addSectionHeading(SSPI18nUtil.getHullModString("ssp_LongerRange_SectionHeading1"), Alignment.MID, opad);
        label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_LongerRange_tooltip1"),
                    opad, h, "100/200/300/400");
        label.setHighlight("100/200/300/400");
        label.setHighlightColors(h);
        //战术系统描述
        if(ship!=null && ship.getSystem() != null) {
            switch (ship.getSystem().getId()) {
                case "ssp_ammofeedjet":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ammofeedjet_P")
                            , opad, h,
                            "25","75%");
                    label.setHighlight("25","75%");
                    label.setHighlightColors(Color.green,b);
                    break;
                case "ssp_lanina_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_lanina_system_P")
                            , opad, h,
                            "2", "75");
                    label.setHighlight("2", "75");
                    label.setHighlightColors(b, Color.green);
                    break;
                case "ssp_ManeuveringJets":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ManeuveringJets_P")
                            , opad, h,
                            "50%", "100%");
                    label.setHighlight("50%", "100%");
                    label.setHighlightColors(b, Color.green);
                    break;
                case "ssp_ThunderCharge":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ThunderCharge_P")
                            , opad, h,
                            "100%", "100%","40%","x3");
                    label.setHighlight("100%", "100%","40%","x3");
                    label.setHighlightColors(b, Color.green,b,Color.green);
                    break;
                case "ssp_microburn":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_microburn_P")
                            , opad, h,
                            "1", "50%", "400%");
                    label.setHighlight("1", "50%", "400%");
                    label.setHighlightColors(b, b, Color.green);
                    break;
                case "ssp_PhaseTransferField":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_PhaseTransferField_P")
                            , opad, h,
                            "50%","300%");
                    label.setHighlight("50%","300%");
                    label.setHighlightColors(b,Color.green);
                    break;
                case "ssp_AdvanceVenting":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_AdvanceVenting_P")
                            , opad, h,
                            "9","x4");
                    label.setHighlight("9","x4");
                    label.setHighlightColors(b,Color.green);
                    break;
                case "ssp_HitMeYouWeekMissile":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_HitMeYouWeekMissile_P")
                            , opad, h,
                            "20%", "5s");
                    label.setHighlight("20%", "5s");
                    label.setHighlightColors(Color.green, b);
                    break;
                case "ssp_missleswarm":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_missleswarm_P")
                            , opad, h,
                            "");
                    label.setHighlight("");
                    label.setHighlightColors(h);
                    break;
                case "ssp_AmmoFeed":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_AmmoFeed_P")
                            , opad, h,
                            "6s", "2s");
                    label.setHighlight("6s", "2s");
                    label.setHighlightColors(b, Color.green);
                    break;
                case "ssp_HighEnergyFocus":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_HighEnergyFocus_P")
                            , opad, h,
                             "50%");
                    label.setHighlight( "50%");
                    label.setHighlightColors(b);
                    break;
                case "ssp_zhurong_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_zhurong_system_P")
                            , opad, h,
                            "50%","x3");
                    label.setHighlight("50%","x3");
                    label.setHighlightColors(Color.green,b);
                    break;
                case "ssp_TDS_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_TDS_system_P")
                            , opad, h,
                            "50%","+1","x3");
                    label.setHighlight("50%","+1","x3");
                    label.setHighlightColors(Color.green,Color.green,b);
                    break;
                case "ssp_gonggong_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Polarisation"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_gonggong_system_P")
                            , opad, h,
                            "x2","x3");
                    label.setHighlight("x2","x3");
                    label.setHighlightColors(Color.green,b);
                    break;
            }
        }
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        //value = MathUtils.clamp(value, 0f, max_value * (Float) mag.get(hullSize));
        if(ship.isAlive()){
            ssp_LongerRange_customdata CustomData = (ssp_LongerRange_customdata) Global.getCombatEngine().getCustomData().get("ssp_LongerRange_effect");
            if(CustomData==null){
                CustomData = new ssp_LongerRange_customdata();
                Global.getCombatEngine().getCustomData().put("ssp_LongerRange_effect",CustomData);
            }
            float value;
            if (CustomData.HaHaHashmap.containsKey(ship)) {
                value = (Float)CustomData.HaHaHashmap.get(ship);
            } else {
                value = 500F;
            }
            boolean isfireing=false;
            Object hullSize = ship.getHullSize();
            for (WeaponAPI weapon : ship.getAllWeapons()) {
                if (weapon.isFiring() && !weapon.hasAIHint(WeaponAPI.AIHints.PD) && !weapon.getType().equals(WeaponAPI.WeaponType.MISSILE)) {
                    isfireing=true;
                    break;
                }
                if(!weapon.isFiring() && !weapon.hasAIHint(WeaponAPI.AIHints.PD) && !weapon.getType().equals(WeaponAPI.WeaponType.MISSILE)){
                    isfireing=false;
                }
            }
            if(isfireing){
                value -= amount * 15f;
            } else {
                value += amount * (2+3*(1-ship.getFluxLevel()));
            }
            if (ship.getFluxTracker().isVenting() ) {
                value += amount * 50f;
            }
            value = MathUtils.clamp(value, 0f, 100f * (Float) mag.get(hullSize));
            CustomData.HaHaHashmap.put(ship,value);
            ship.getMutableStats().getNonBeamPDWeaponRangeBonus().modifyFlat("ssp_LongerRange_effect", -value);
            ship.getMutableStats().getBeamPDWeaponRangeBonus().modifyFlat("ssp_LongerRange_effect", -value);
            ship.getMutableStats().getBallisticWeaponRangeBonus().modifyFlat("ssp_LongerRange_effect", value);
            ship.getMutableStats().getEnergyWeaponRangeBonus().modifyFlat("ssp_LongerRange_effect", value);

            //可视化
            if (Global.getCombatEngine().getPlayerShip() == ship) {
                Global.getCombatEngine().maintainStatusForPlayerShip("ssp_LongerRange_effect", "graphics/icons/hullsys/targeting_feed.png", SSPI18nUtil.getHullModString("ssp_LongerRange_title"),
                        String.format(SSPI18nUtil.getHullModString("ssp_LongerRange"), (int) value), false);
            }


        }
    }
    public class ssp_LongerRange_customdata{
        Map<ShipAPI, Float> HaHaHashmap = new HashMap();
        public ssp_LongerRange_customdata(){}
    }
//  感谢陪伴
//    public static class ssp_Listener_LongerRange implements AdvanceableListener {
//        private static Map mag = new HashMap();
//        static {
//            mag.put(ShipAPI.HullSize.FRIGATE, 1f);
//            mag.put(ShipAPI.HullSize.DESTROYER, 2f);
//            mag.put(ShipAPI.HullSize.CRUISER, 3f);
//            mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 4f);
//        }
//        protected float max_value=75f;
//        protected float value=500f;
//        boolean isfireing=false;
//        protected ShipAPI ship;
//        protected String id;
//        public ssp_Listener_LongerRange(ShipAPI ship, String id) {
//            this.ship = ship;
//            this.id = id;
//        }
//        public void advance(float amount) {
//            Object hullSize = ship.getHullSize();
//
//                value = MathUtils.clamp(value, 0f, max_value * (Float) mag.get(hullSize));
//                for (WeaponAPI weapon : ship.getAllWeapons()) {
//                    if (weapon.isFiring() && !weapon.hasAIHint(WeaponAPI.AIHints.PD) && !weapon.getType().equals(WeaponAPI.WeaponType.MISSILE)) {
//                        isfireing=true;
//                        break;
//                    }
//                    if(!weapon.isFiring() && !weapon.hasAIHint(WeaponAPI.AIHints.PD) && !weapon.getType().equals(WeaponAPI.WeaponType.MISSILE)){
//                        isfireing=false;
//                    }
//                }
//                if(isfireing){
//                    value -= amount * 15f;
//                } else {
//                    value += amount * (2+3*(1-ship.getFluxLevel()));
//                }
//                if (ship.getFluxTracker().isVenting() ) {
//                    value += amount * 50f;
//                }
//                ship.getMutableStats().getNonBeamPDWeaponRangeBonus().modifyFlat(id, -value);
//                ship.getMutableStats().getBeamPDWeaponRangeBonus().modifyFlat(id, -value);
//                ship.getMutableStats().getBallisticWeaponRangeBonus().modifyFlat(id, value);
//                ship.getMutableStats().getEnergyWeaponRangeBonus().modifyFlat(id, value);
//
//            //可视化
//            if (Global.getCombatEngine().getPlayerShip() == ship) {
//                Global.getCombatEngine().maintainStatusForPlayerShip(id, "graphics/icons/hullsys/targeting_feed.png", SSPI18nUtil.getHullModString("ssp_LongerRange_title"),
//                        String.format(SSPI18nUtil.getHullModString("ssp_LongerRange"), (int) value), false);
//            }
//        }
//    }
}
