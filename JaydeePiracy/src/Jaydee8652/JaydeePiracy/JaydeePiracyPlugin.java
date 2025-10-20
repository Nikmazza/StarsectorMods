package Jaydee8652.JaydeePiracy;

import Jaydee8652.JaydeePiracy.campaign.econ.industries.jdp_ItemEffectsRepo;
import Jaydee8652.JaydeePiracy.campaign.econ.raid.jdp_GroundRaidObjectivesCreator;
import Jaydee8652.JaydeePiracy.campaign.procgen.jdp_LampDefenderPluginImpl;
import Jaydee8652.JaydeePiracy.plugins.jdp_lunaSettings;
import Jaydee8652.JaydeePiracy.scripts.*;
import Jaydee8652.JaydeePiracy.scripts.skills.flowerfish.jdp_flowerfishFlexibleContracts;
import Jaydee8652.JaydeePiracy.utils.codex.jdp_CodexData;
import Jaydee8652.JaydeePiracy.utils.rulecmd.jdp_InitialiseFrictionless;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.listeners.RefitScreenListener;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.BaseModPlugin;
import Jaydee8652.JaydeePiracy.plugins.OmegaCoreOfficerPluginImpl;
import Jaydee8652.JaydeePiracy.plugins.CampaignPluginImpl;
import Jaydee8652.JaydeePiracy.utils.JaydeePiracyIDs;
import Jaydee8652.JaydeePiracy.campaign.procgen.jdp_DerelictMissileDefenderPluginImpl;
import Jaydee8652.JaydeePiracy.compat.seatsofpower.*;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import data.listeners.timeline.MiscEventListener;
import data.memory.AoTDSopMemFlags;
import data.scripts.managers.TimelineListenerManager;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;


public class JaydeePiracyPlugin extends BaseModPlugin {
    private static final Logger log = Global.getLogger(JaydeePiracyPlugin.class);


    public void addTransientScripts() {
        //Seats of Power compatability
        if (Global.getSettings().getModManager().isModEnabled("aotd_sop")) {
            TimelineListenerManager.getInstance().addNewListener(new MiscEventListener(AoTDSopMemFlags.MISC_EVENT,new jdp_OmegakinEvent()));
            TimelineListenerManager.getInstance().addNewListener(new MiscEventListener(AoTDSopMemFlags.FIRST_ITEM,new jdp_InterfectorEvent()));
        }
    }
    
    public static void syncjdpScripts() {
        SectorAPI sector = Global.getSector();
        GenericPluginManagerAPI plugins = sector.getGenericPlugins();

        if (!plugins.hasPlugin(jdp_DerelictMissileDefenderPluginImpl.class)) {
            plugins.addPlugin(new jdp_DerelictMissileDefenderPluginImpl(), true);
        }
        if (!plugins.hasPlugin(jdp_LampDefenderPluginImpl.class)) {
            plugins.addPlugin(new jdp_LampDefenderPluginImpl(), true);
        }
    }

    public void onApplicationLoad() {
        jdp_ItemEffectsRepo.addItemEffectsToVanillaRepo();//Planetkiller Colony Item
    }

    public void onNewGame() {
    }

    public void onNewGameAfterEconomyLoad() {
    }

    public void onGameLoad(boolean newGame) {
        //This is so messy...

        new jdp_retrogen().generate(Global.getSector());//Generates Retroactively


        addTransientScripts();//Loads Transient scripts. (Currently only used for AoTD SoP Crossmod)

        jdp_IndustryPlacer.placeFlowerfishHQ();//Places Flowerfish HQ on Ailmar

        Global.getSector().registerPlugin(new CampaignPluginImpl());//Allows JDP Omega Cores to be officers

        Global.getSector().getPlayerFleet().addEventListener(new jdp_OmegaListener());//Listener for beating a Hypershunt

        Global.getSector().getPlayerPerson().getStats().setSkillLevel("jdp_hullmodapplicator", 1);//Automatically adds hullmods

        new jdp_HullModManager();

         new jdp_InitialiseFrictionless().updateFrictionless();//Update the name of Frictionless NMC

        //Ground Raid Objectives
        if (!Global.getSector().getListenerManager().hasListenerOfClass(jdp_GroundRaidObjectivesCreator.class)) {
            Global.getSector().getListenerManager().addListener(new jdp_GroundRaidObjectivesCreator());
        }
        if (jdp_lunaSettings.jdp_experimentalFlowerfish().equals(true) && !Global.getSector().getListenerManager().hasListenerOfClass(jdp_addXO.class)) Global.getSector().getListenerManager().addListener(new jdp_addXO(), false);

        /*if (!Global.getSector().hasScript(JaydeePiracyPlugin.boostOfficer.class)) {
            Global.getSector().addScript(new JaydeePiracyPlugin.boostOfficer());
        }*/

        syncjdpScripts();

        orbitValidator();
        starAgeValidator();

        //conditionValidator("$jdp_orbitalFusionLampKey");
    }

    public void orbitValidator() {
        log.info("JDP_DEBUG: Orbit Validator Running");
        float count = 0;

        ArrayList<SectorEntityToken> tokens = new ArrayList<SectorEntityToken>();

        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            tokens.addAll(system.getAllEntities());
        }
        tokens.addAll(Global.getSector().getHyperspace().getAllEntities());

        for (SectorEntityToken token : tokens) {
            float x = token.getLocation().x;
            float y = token.getLocation().y;
            if (Float.isNaN(x) || Float.isInfinite(x) || Float.isNaN(y) || Float.isInfinite(y)) {
                count++;
                log.warn("JDP_DEBUG: Entity [" + token.getFullName() + "] [" + token.getId() + "] in ["
                        + token.getContainingLocation().getNameWithLowercaseType() + "] has an invalid orbit");
            }
        }
        if (count == 0) {
            log.info("JDP_DEBUG: Orbit Validator found no issues");
        }
    }

    public void conditionValidator(String tag) {
        log.info("JDP_DEBUG: Condition Validator Searching for [" + tag + "]");

        PlanetAPI planet = (PlanetAPI) Global.getSector().getMemoryWithoutUpdate().get(tag);
        if (planet == null) {
            log.warn("JDP_DEBUG: Condition Validator could not find [" + tag + "]");
            return;
        }

        for(MarketConditionAPI condition : planet.getMarket().getConditions()) {
            log.info("JDP_DEBUG: Condition [" + condition.getName() + "] [" + condition.getId() + "] ["+ condition.getSpec().getIcon() +"]");
        }
    }

    public void starAgeValidator() {
        log.info("JDP_DEBUG: Star Age Validator Running");
        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            if (system.getAge() == null) {
                log.warn("JDP_DEBUG: System [" + system.getBaseName() + "] [" + system.getId() + "] at ["
                        + system.getLocation() + "] has null age");
                log.info("JDP_DEBUG: Has tags [" + system.getTags() + "]");
            }
        }
    }

    @Override
    public void onAboutToLinkCodexEntries() {
        super.onAboutToLinkCodexEntries();
        jdp_CodexData.linkCodexEntries();
    }

    public static class boostOfficer implements EveryFrameScript {

        private IntervalUtil interval = new IntervalUtil(2f, 3f);

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean runWhilePaused() {
            return true;
        }

        @Override
        public void advance(float amount) {
            interval.advance(amount);

            if (interval.intervalElapsed()) {
                log.info("JDP_DEBUG: Boosting Officer Rates");

                List<MarketAPI> markets = Global.getSector().getEconomy().getMarketsCopy();
                for (MarketAPI market : markets) {
                    market.getStats().getDynamic().getMod(Stats.OFFICER_PROB_MOD).modifyFlat("jdp_flowerfishFlexibleContracts", 9999f);
                    market.getStats().getDynamic().getMod(Stats.OFFICER_ADDITIONAL_PROB_MULT_MOD).modifyFlat("jdp_flowerfishFlexibleContracts", 9999f);

                    market.getStats().getDynamic().getMod(Stats.OFFICER_PROB_MOD).modifyMult("jdp_flowerfishFlexibleContracts", 9999f);
                    market.getStats().getDynamic().getMod(Stats.OFFICER_ADDITIONAL_PROB_MULT_MOD).modifyMult("jdp_flowerfishFlexibleContracts", 9999f);
                }
            }
        }
    }
}
