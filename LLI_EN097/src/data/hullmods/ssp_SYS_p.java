package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;

import java.awt.*;

public class ssp_SYS_p extends BaseHullMod {
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff") && !ship.getVariant().hasHullMod("ssp_SYS_c")&& !ship.getVariant().hasHullMod("ssp_SYS_s");
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
                SSPI18nUtil.getHullModString("ssp_SYS_p_tooltip0"),
                opad, h, "");
        label.setHighlight();
        label.setHighlightColors();
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
                            "40%", "40%");
                    label.setHighlight("40%", "40%");
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
}
