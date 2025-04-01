package data.hullmods;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;

import java.awt.*;


public class ssp_SYS_s extends BaseHullMod {
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff") && !ship.getVariant().hasHullMod("ssp_SYS_p")&& !ship.getVariant().hasHullMod("ssp_SYS_c");
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
                SSPI18nUtil.getHullModString("ssp_SYS_s_tooltip0"),
                opad, h, "");
        label.setHighlight();
        label.setHighlightColors();
        //战术系统描述
        if(ship!=null && ship.getSystem() != null) {
            switch (ship.getSystem().getId()) {
                case "ssp_ammofeedjet":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ammofeedjet_S")
                            , opad, h,
                            "100%");
                    label.setHighlight("100%");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_lanina_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_lanina_system_S")
                            , opad, h,
                            "1");
                    label.setHighlight("1");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_ManeuveringJets":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ManeuveringJets_S")
                            , opad, h,
                            "");
                    label.setHighlight("");
                    label.setHighlightColors();
                    break;
                case "ssp_ThunderCharge":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ThunderCharge_S")
                            , opad, h,
                            "20%");
                    label.setHighlight("20%");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_microburn":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_microburn_S")
                            , opad, h,
                            "1", "20%");
                    label.setHighlight("1", "20%");
                    label.setHighlightColors(Color.green, Color.green);
                    break;
                case "ssp_PhaseTransferField":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_PhaseTransferField_S")
                            , opad, h,
                            "20%");
                    label.setHighlight("20%");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_AdvanceVenting":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_AdvanceVenting_S")
                            , opad, h,
                            "200");
                    label.setHighlight("200");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_HitMeYouWeekMissile":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_HitMeYouWeekMissile_S")
                            , opad, h,
                            "500");
                    label.setHighlight("500");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_missleswarm":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_missleswarm_S")
                            , opad, h,
                            "");
                    label.setHighlight("");
                    label.setHighlightColors(h);
                    break;
                case "ssp_AmmoFeed":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_AmmoFeed_S")
                            , opad, h,
                            "20%");
                    label.setHighlight("20%");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_HighEnergyFocus":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_HighEnergyFocus_S")
                            , opad, h,
                            "1s");
                    label.setHighlight("1s");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_zhurong_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_zhurong_system_S")
                            , opad, h,
                            "6");
                    label.setHighlight("6");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_TDS_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_TDS_system_S")
                            , opad, h,
                            "");
                    label.setHighlight("");
                    label.setHighlightColors();
                    break;
                case "ssp_gonggong_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_gonggong_system_S")
                            , opad, h,
                            "6");
                    label.setHighlight("6");
                    label.setHighlightColors(Color.green);
                    break;
            }
        }
    }
}
