package Jaydee8652.JaydeePiracy;

import Jaydee8652.JaydeePiracy.campaign.econ.industries.jdp_ItemEffectsRepo;
import Jaydee8652.JaydeePiracy.campaign.econ.raid.jdp_GroundRaidObjectivesCreator;
import Jaydee8652.JaydeePiracy.scripts.jdp_IndustryPlacer;
import Jaydee8652.JaydeePiracy.scripts.jdp_gen;
import Jaydee8652.JaydeePiracy.scripts.jdp_retrogen;
import Jaydee8652.JaydeePiracy.utils.codex.jdp_CodexData;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.GenericPluginManagerAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.BaseModPlugin;
import Jaydee8652.JaydeePiracy.plugins.OmegaCoreOfficerPluginImpl;
import Jaydee8652.JaydeePiracy.plugins.CampaignPluginImpl;
import Jaydee8652.JaydeePiracy.utils.JaydeePiracyIDs;
import Jaydee8652.JaydeePiracy.scripts.jdp_OmegaListener;
import Jaydee8652.JaydeePiracy.campaign.procgen.jdp_DerelictMissileDefenderPluginImpl;
import org.apache.log4j.Logger;


public class JaydeePiracyPlugin extends BaseModPlugin {
    public static void syncjdpScripts() {
        SectorAPI sector = Global.getSector();
        GenericPluginManagerAPI plugins = sector.getGenericPlugins();
        if (!plugins.hasPlugin(jdp_DerelictMissileDefenderPluginImpl.class)) {
            plugins.addPlugin(new jdp_DerelictMissileDefenderPluginImpl(), true);
        }
    }

    public void onApplicationLoad() {
        jdp_ItemEffectsRepo.addItemEffectsToVanillaRepo();//Planetkiller Colony Item
    }

    public void onNewGame() {
        //Not yet used
    }

    public void onNewGameAfterEconomyLoad() {
    }

    public void onGameLoad(boolean newGame) {
        MarketAPI jangala = Global.getSector().getEconomy().getMarket("jangala");
        if (jangala != null) {
            Jaydee8652.JaydeePiracy.utils.jdp_People.jdp_createMiscCharacters();//Creates Hayes Calhoum. Base commander of the Interfector.
        }

        jdp_IndustryPlacer.placeFlowerfishHQ();//Places Flowerfish HQ on Ailmar
        new jdp_retrogen().generate(Global.getSector());//Generates Systems Retroactively

        Global.getSector().registerPlugin(new CampaignPluginImpl());//Allows JDP Omega Cores to be officers
        Global.getSector().getPlayerFleet().addEventListener(new jdp_OmegaListener());//Listener for beating a Hypershunt

        //Ground Raid Objectives
        if (!Global.getSector().getListenerManager().hasListenerOfClass(jdp_GroundRaidObjectivesCreator.class)) {
            Global.getSector().getListenerManager().addListener(new jdp_GroundRaidObjectivesCreator());
        }

        syncjdpScripts();
    }

    public void onNewGameAfterProcGen() {
        new jdp_gen().generate(Global.getSector());//Generates Systems
        jdp_gen.addMadokaBuffalo(Global.getSector());//Generates PMMM Buffalo
    }

    @Override
    public void onAboutToLinkCodexEntries() {
        super.onAboutToLinkCodexEntries();

        jdp_CodexData.linkCodexEntries();
    }
}

