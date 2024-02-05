package data.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.fleet.FleetMember;
import data.SSPI18nUtil;

import java.util.List;
import java.util.Map;

public class LLICMD_BuyWaveM extends BaseCommandPlugin {
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;
        if(Global.getSector().getPlayerFleet().getCargo().getCredits().get()>=40000){
            FleetMemberAPI member= new FleetMember(1,"ssp_wave_M_standard", FleetMemberType.SHIP);
            Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);
            addShipGainText(member, dialog.getTextPanel());
            Global.getSector().getPlayerFleet().getCargo().getCredits().add(-40000);
        }else{
            addnotenoughcredits( dialog.getTextPanel());
        }

        return true;
    }
    public static void addShipGainText(FleetMemberAPI member, TextPanelAPI text) {
        text.setFontSmallInsignia();
        text.addParagraph(SSPI18nUtil.getStoryString("LLICMD_BuyWaveM_0"),Misc.getNegativeHighlightColor());
        text.addParagraph(SSPI18nUtil.getStoryString("LLICMD_BuyWaveM_1")+ member.getVariant().getFullDesignationWithHullNameForShip(), Misc.getPositiveHighlightColor());
        text.highlightInLastPara(Misc.getHighlightColor(), member.getVariant().getFullDesignationWithHullNameForShip());
        text.addParagraph(SSPI18nUtil.getStoryString("LLICMD_BuyWaveM_2")+String.valueOf((int) Global.getSector().getPlayerFleet().getCargo().getCredits().get())+"",Misc.getPositiveHighlightColor());
        text.highlightInLastPara(Misc.getHighlightColor(), String.valueOf((int) Global.getSector().getPlayerFleet().getCargo().getCredits().get()));
        text.setFontInsignia();
    }
    public static void addnotenoughcredits(TextPanelAPI text) {
        text.setFontSmallInsignia();
        text.addParagraph(SSPI18nUtil.getStoryString("LLICMD_BuyWaveM_3"),Misc.getNegativeHighlightColor());
        //text.highlightInLastPara(Misc.getHighlightColor(), "你没有足够的星币");
        text.setFontInsignia();

    }
}
