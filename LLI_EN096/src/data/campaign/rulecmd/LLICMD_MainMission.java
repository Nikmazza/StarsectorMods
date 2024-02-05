package data.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.fleet.FleetMember;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LLICMD_MainMission extends BaseCommandPlugin {
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        int num = params.get(0).getInt(memoryMap);
        Global.getSector().getPlayerStats().addStoryPoints(num, dialog.getTextPanel(), false);
        ArrayList<String> sdList= new ArrayList<>();
        sdList.add("champion_Strike");
        sdList.add("dominator_Assault");
        sdList.add("aurora_Strike");
        sdList.add("eagle_xiv_Elite");
        sdList.add("ssp_thunder_SIM");
        sdList.add("ssp_TDS_standard");
        sdList.add("ssp_lanina_standard");
        sdList.add("ssp_naga_standard");
        sdList.add("ssp_tide_T_standard");
        int randomsd=MathUtils.getRandomNumberInRange(0,sdList.size());
        FleetMemberAPI member= new FleetMember(1,sdList.get(randomsd), FleetMemberType.SHIP);
        Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);
        addShipGainText(member, dialog.getTextPanel());
        return true;
    }
    public static void addShipGainText(FleetMemberAPI member, TextPanelAPI text) {
        text.setFontSmallInsignia();
        text.addParagraph("Gained " + member.getVariant().getFullDesignationWithHullNameForShip(), Misc.getPositiveHighlightColor());
        text.highlightInLastPara(Misc.getHighlightColor(), member.getVariant().getFullDesignationWithHullNameForShip());
        text.setFontInsignia();
    }
}
