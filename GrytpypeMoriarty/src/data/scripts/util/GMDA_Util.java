package data.scripts.util;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GMDA_Util {

    public static void removeMarketInfluence(MarketAPI market, String faction) {
        @SuppressWarnings("unchecked")
        List<String> influences = (List<String>) market.getMemoryWithoutUpdate().get("$ds_market_influences");

        if (influences != null) {
            influences.remove(faction);
        }
    }

    public static void setMarketInfluence(MarketAPI market, String faction) {
        @SuppressWarnings("unchecked")
        List<String> influences = (List<String>) market.getMemoryWithoutUpdate().get("$ds_market_influences");

        if (influences == null) {
            influences = new ArrayList<>(3);
            market.getMemoryWithoutUpdate().set("$ds_market_influences", influences);
        }

        if (!influences.contains(faction)) {
            influences.add(faction);
        }
    }
	
	public static int calculatePowerLevel(CampaignFleetAPI fleet) {
        int power = fleet.getFleetPoints();
        int offLvl = 0;
        int cdrLvl = 0;
        boolean commander = false;
        for (OfficerDataAPI officer : fleet.getFleetData().getOfficersCopy()) {
            if (officer.getPerson() == fleet.getCommander()) {
                commander = true;
                cdrLvl = officer.getPerson().getStats().getLevel();
            } else {
                offLvl += officer.getPerson().getStats().getLevel();
            }
        }
        if (!commander) {
            cdrLvl = fleet.getCommanderStats().getLevel();
        }
        power *= Math.sqrt(cdrLvl / 100f + 1f);
        int flatBonus = cdrLvl + offLvl + 10;
        if (power < flatBonus * 2) {
            flatBonus *= power / (float) (flatBonus * 2);
        }
        power += flatBonus;
        return power;
    }
	
	    public static double roundToSignificantFigures(double num, int n) {
        if (num == 0) {
            return 0;
        }

        final double d = Math.ceil(Math.log10(num < 0 ? -num : num));
        final int power = n - (int) d;

        final double magnitude = Math.pow(10, power);
        final long shifted = Math.round(num * magnitude);
        return shifted / magnitude;
    }

    public static long roundToSignificantFiguresLong(double num, int n) {
        return Math.round(roundToSignificantFigures(num, n));
    }
}