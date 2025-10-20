package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

public class ArcadiaAddIndustry {
    public void generate(SectorAPI sector) {

        String agreuscheck[];
        agreuscheck = new String[]{"agreus"};
        for (String marketStr : agreuscheck) {
            MarketAPI market = Global.getSector().getEconomy().getMarket(marketStr);
            if (market == null) {
                // Handles non-Corvus Mode Nexerelin
                continue;
            }
            if (!market.hasIndustry("GMDA_refit_agreus")) {
                market.addIndustry("GMDA_refit_agreus");
            }
        }

        String ilmcheck[];
        ilmcheck = new String[]{"ilm"};
        for (String marketStr : ilmcheck) {
            MarketAPI market = Global.getSector().getEconomy().getMarket(marketStr);
            if (market == null) {
                // Handles non-Corvus Mode Nexerelin
                continue;
            }
            if (!market.hasIndustry("GMDA_refit_ilm")) {
                market.addIndustry("GMDA_refit_ilm");
            }
        }

        String maxioscheck[];
        maxioscheck = new String[]{"new_maxios"};
        for (String marketStr : maxioscheck) {
            MarketAPI market = Global.getSector().getEconomy().getMarket(marketStr);
            if (market == null) {
                // Handles non-Corvus Mode Nexerelin
                continue;
            }
            if (!market.hasIndustry("GMDA_refit")) {
                market.addIndustry("GMDA_refit");
            }
        }
    }
}
