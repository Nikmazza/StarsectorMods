package Jaydee8652.JaydeePiracy.scripts;

import Jaydee8652.JaydeePiracy.utils.jdp_Industries;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import java.util.*;

public class jdp_IndustryPlacer {

    public static void run() {
        placeFlowerfishHQ();
        //placeCourierPorts();
    }

    public static void placeFlowerfishHQ() {
        HashMap<String, String> h = new HashMap<>();
        h.put("ailmar", null);
        placeIndustries(h, jdp_Industries.JDP_FLOWERFISHHQ);
    }

    private static void placeIndustries(Map<String, String> planetIdMap, String industryId) {
        for (Map.Entry<String, String> entry : planetIdMap.entrySet()) {
            MarketAPI m;

            if (Global.getSector().getEconomy().getMarket(entry.getKey()) != null) {
                m = Global.getSector().getEconomy().getMarket(entry.getKey());

                if (!m.hasIndustry(industryId)
                        && !m.isPlayerOwned()
                        && !m.getFaction().getId().equals(Global.getSector().getPlayerFaction().getId())) {

                    m.addIndustry(industryId);

                    if (entry.getValue() == null) continue;

                    else m.getIndustry(industryId).setSpecialItem(new SpecialItemData(entry.getValue(), null));
                }
            }
        }
    }
}
