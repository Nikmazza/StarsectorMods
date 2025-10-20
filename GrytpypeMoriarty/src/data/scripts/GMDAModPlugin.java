package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.thoughtworks.xstream.XStream;
import data.campaign.econ.industries.GMDA_refit;
import data.scripts.campaign.econ.GMDA_facility;
import data.scripts.campaign.fleets.GMDAFleetManager;
import data.scripts.campaign.submarkets.GMDA_MarketPlugin;
import data.scripts.util.GMDA_Util;

import java.util.List;

public class GMDAModPlugin extends BaseModPlugin {

    public static boolean Module_GMDA = true;
    public static boolean hasSWP = false;
    public static boolean hasTwigLib = false;
    public static boolean isExerelin = false;

    public static void syncGMDAScripts() {
        if (!Global.getSector().hasScript(GMDAFleetManager.class)) {
            Global.getSector().addScript(new GMDAFleetManager());
        }
        syncGMDAMarkets();
    }

    public static void syncGMDAScriptsExerelin() {
        if (!Global.getSector().hasScript(GMDAFleetManager.class)) {
            Global.getSector().addScript(new GMDAFleetManager());
        }
        syncGMDAMarkets();
    }

    private static void initGMDARelationships(SectorAPI sector) {

        // Relationships - hostile to almost everyone, unless they are independent
        FactionAPI gmda = sector.getFaction("gmda");
        FactionAPI gmda_pursuit = sector.getFaction("gmda_pursuit");

        List<FactionAPI> allFactions = sector.getAllFactions();
        for (FactionAPI curFaction : allFactions)
        {
            if (curFaction == gmda || curFaction.isNeutralFaction())
            {
                continue;
            }
            gmda.setRelationship(curFaction.getId(), RepLevel.SUSPICIOUS);

            if (curFaction == gmda_pursuit || curFaction.isNeutralFaction())
            {
                continue;
            }
            gmda_pursuit.setRelationship(curFaction.getId(), RepLevel.VENGEFUL);
        }

        FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI player = sector.getFaction(Factions.PLAYER);

		player.setRelationship(gmda.getId(), RepLevel.NEUTRAL);
		
        gmda.setRelationship(independent.getId(), RepLevel.FAVORABLE);
        gmda.setRelationship("templars", RepLevel.VENGEFUL);
        gmda.setRelationship(pirates.getId(), RepLevel.VENGEFUL);
        gmda.setRelationship("mayorate", RepLevel.VENGEFUL);
        gmda.setRelationship(kol.getId(), RepLevel.VENGEFUL);
        gmda.setRelationship(path.getId(), RepLevel.VENGEFUL);
		gmda.setRelationship("junk_pirates", RepLevel.VENGEFUL);
		gmda.setRelationship("exipirated", RepLevel.VENGEFUL);
		gmda.setRelationship("player_npc", RepLevel.NEUTRAL);
		gmda.setRelationship(Factions.PLAYER, RepLevel.NEUTRAL);


        player.setRelationship(gmda_pursuit.getId(), RepLevel.VENGEFUL);

        gmda_pursuit.setRelationship(independent.getId(), RepLevel.SUSPICIOUS);
    }

//    private static void initGMDAPatrolRelationships(SectorAPI sector) {

        // Relationships - hostile to almost everyone, unless they are independent
//        FactionAPI gmda_patrol = sector.getFaction("gmda_patrol");
//
//        List<FactionAPI> allFactions = sector.getAllFactions();
//        for (FactionAPI curFaction : allFactions)
//        {
//            if (curFaction == gmda_patrol || curFaction.isNeutralFaction())
//            {
//                continue;
//            }
//            gmda_patrol.setRelationship(curFaction.getId(), RepLevel.HOSTILE);
//        }

//        FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
//        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
//        FactionAPI kol = sector.getFaction(Factions.KOL);
//        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
//        FactionAPI player = sector.getFaction(Factions.PLAYER);

//        player.setRelationship(gmda_patrol.getId(), RepLevel.COOPERATIVE);

//        gmda_patrol.setRelationship(independent.getId(), RepLevel.FRIENDLY);
//      gmda_patrol.setRelationship("templars", RepLevel.VENGEFUL);
//      gmda_patrol.setRelationship(pirates.getId(), RepLevel.VENGEFUL);
//        gmda_patrol.setRelationship("mayorate", RepLevel.VENGEFUL);
//       gmda_patrol.setRelationship(kol.getId(), RepLevel.VENGEFUL);
//        gmda_patrol.setRelationship(path.getId(), RepLevel.VENGEFUL);
//        gmda_patrol.setRelationship("junk_pirates", RepLevel.VENGEFUL);
//        gmda_patrol.setRelationship("exipirated", RepLevel.VENGEFUL);
//        gmda_patrol.setRelationship("player_npc", RepLevel.COOPERATIVE);
//        gmda_patrol.setRelationship(Factions.PLAYER, RepLevel.COOPERATIVE);
//    }

    private static void syncGMDAMarkets() {
        int numIndependentMarkets = 0;
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.getFactionId().contentEquals(Factions.INDEPENDENT)) {
                numIndependentMarkets++;
            }
        }
        String gmdaMarkets[];
        if (numIndependentMarkets >= 10) {
            gmdaMarkets = new String[]{"agreus", "ilm", "new_maxios"};
        } else {
            gmdaMarkets = new String[]{"agreus", "ilm", "new_maxios"};
        }
        if (Module_GMDA) {
            Global.getSector().getFaction("gmda").setShowInIntelTab(true);
            for (String marketStr : gmdaMarkets) {
                MarketAPI market = Global.getSector().getEconomy().getMarket(marketStr);
                if (market == null) {
                    // Handles non-Corvus Mode Nexerelin
                    continue;
                }
                if (!market.hasCondition("GMDA_facility")) {
                    market.addCondition("GMDA_facility");
                    market.addSubmarket("gmda_market");
                    GMDA_Util.setMarketInfluence(market, "gmda");
                }
            }
        } else {
            Global.getSector().getFaction("gmda").setShowInIntelTab(false);
            for (String marketStr : gmdaMarkets) {
                MarketAPI market = Global.getSector().getEconomy().getMarket(marketStr);
                if (market == null) {
                    continue;
                }
                if (!market.hasCondition("GMDA_facility")) {
                    market.addCondition("GMDA_facility");
                    market.addSubmarket("gmda_market");
                    GMDA_Util.setMarketInfluence(market, "gmda");
                }
            }
        }
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


    @Override
    public void configureXStream(XStream x) {
        x.alias("GMDAFleetManager", GMDAFleetManager.class);
        x.alias("GMDA_facility", GMDA_facility.class);
        x.alias("GMDAMarketPlugin", GMDA_MarketPlugin.class);
        x.alias("GMDA_refit", GMDA_refit.class);
//        x.alias("GMDAPatrolFleetManager", GMDAPatrolFleetManager.class);
    }

    @Override
    public void onApplicationLoad() throws Exception {
        isExerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        hasTwigLib = Global.getSettings().getModManager().isModEnabled("ztwiglib");
        hasSWP = Global.getSettings().getModManager().isModEnabled("swp");
    }

    @Override
    public void onGameLoad(boolean newGame) {
            syncGMDAScripts();
    }

    @Override
    public void onNewGame() {
        initGMDARelationships(Global.getSector());
//        initGMDAPatrolRelationships(Global.getSector());
        syncGMDAScripts();
        GMDA_ModPluginAlt.initArcadiaRefit();
    }
}
