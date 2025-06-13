package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin.PickPriority;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.AutofireAIPlugin;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAIConfig;
import com.fs.starfarer.api.combat.ShipAIPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.ResourceDepositsCondition;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.impl.codex.CodexEntryPlugin;
import com.thoughtworks.xstream.XStream;
import data.scripts.ai.II_ApocalypseAI1;
import data.scripts.ai.II_ApocalypseAI2;
import data.scripts.ai.II_ApocalypseAI3;
import data.scripts.ai.II_BallistaAI;
import data.scripts.ai.II_BallistaStage2AI;
import data.scripts.ai.II_DummyAI;
import data.scripts.ai.II_FundaeAI;
import data.scripts.ai.II_FundaeMirvAI;
import data.scripts.ai.II_JavelinAI;
import data.scripts.ai.II_OnagerAI;
import data.scripts.ai.II_PulsarBombAI;
import data.scripts.ai.II_TitanAI;
import data.scripts.ai.II_TitanMIRVAI;
import data.scripts.campaign.II_CampaignPlugin;
import data.scripts.campaign.II_MarketRiggerScript;
import data.scripts.campaign.II_TitanManagerScript;
import data.scripts.campaign.II_TitanPunisher;
import data.scripts.campaign.econ.II_DefenderAssignmentAI;
import data.scripts.campaign.econ.II_HostileTerrain;
import data.scripts.campaign.econ.II_IGPatrolAssignmentAI;
import data.scripts.campaign.econ.II_ImperialDoctrine;
import data.scripts.campaign.econ.II_ImperialGuardHQ;
import data.scripts.campaign.econ.II_InterstellarBazaar;
import data.scripts.campaign.econ.II_OrbitalStation;
import data.scripts.campaign.econ.II_StellaCastellum;
import data.scripts.campaign.submarkets.II_EBaySubmarketPlugin;
import data.scripts.campaign.terrain.II_AtmosphericHazeTerrainPlugin;
import data.scripts.campaign.terrain.II_ChargedNebulaTerrainPlugin;
import data.scripts.campaign.terrain.II_ChargedStormBoost;
import data.scripts.campaign.terrain.II_TachyonFieldTerrainPlugin;
import data.scripts.everyframe.II_BlockedHullmodDisplayScript;
import data.scripts.hullmods.II_BasePackage;
import data.scripts.util.II_Util;
import data.scripts.weapons.ai.II_LightspearAI;
import data.scripts.world.imperium.II_Corsica;
import data.scripts.world.imperium.II_Ex_Vis;
import data.scripts.world.imperium.II_Thracia;
import data.scripts.world.imperium.II_Yma;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;

public class IIModPlugin extends BaseModPlugin {

    public static final String APOCALYPSE_MIRV_ID = "ii_apocalypse_mirv";
    public static final String ARMAGEDDON_NORMAL_ID = "ii_armageddon_normal";
    public static final String ARMAGEDDON_STANDARD_ID = "ii_armageddon_standard";
    public static final String ARMAGEDDON_ARMOR_ID = "ii_armageddon_armor";
    public static final String ARMAGEDDON_TARGETING_ID = "ii_armageddon_targeting";
    public static final String ARMAGEDDON_ELITE_ID = "ii_armageddon_elite";
    public static final String BALLISTA_LRM_ID = "ii_ballista_lrm";
    public static final String BALLISTA_STAGE2_ID = "ii_ballista_stage2";
    public static final String FUNDAE_SRM_ID = "ii_fundae_missile";
    public static final String FUNDAE_SUB_ID = "ii_fundae_submissile";
    public static final String JAVELIN_MRM_ID = "ii_javelin_mrm";
    public static final String SUPER_JAVELIN_ID = "ii_javelin_super";
    public static final String LIGHTSPEAR_ID = "ii_lightspear";
    public static final String LIGHTSPEAR_STATION_ID = "ii_lightspear_station";
    public static final String ONAGER_MIRV_ID = "ii_onager_mirv";
    public static final String PULSAR_BOMB_ID = "ii_pulsarbomb_bomb";
    public static final String TITAN_ID = "ii_titan";
    public static final String TITAN_ARMOR_ID = "ii_titan_armor";
    public static final String TITAN_TARGETING_ID = "ii_titan_targeting";
    public static final String TITAN_ELITE_ID = "ii_titan_elite";
    public static final String TITAN_ARMOR_DOOR_ID = "ii_titan_armor_door";
    public static final String TITAN_TARGETING_DOOR_ID = "ii_titan_targeting_door";
    public static final String TITAN_X_ID = "ii_boss_titanx";

    public static boolean hasGraphicsLib = false;
    public static boolean hasMagicLib = false;
    public static boolean hasSWP = false;
    public static boolean hasStarshipLegends = false;
    public static boolean isExerelin = false;
    public static boolean scyExists = false;

    /* Support Byzantine Wine and Byzantine Vineyards */
    static {
        ResourceDepositsCondition.COMMODITY.put("ii_vineyards", "ii_wine");
        ResourceDepositsCondition.MODIFIER.put("ii_vineyards", 1);
        ResourceDepositsCondition.INDUSTRY.put("ii_wine", Industries.FARMING);
        ResourceDepositsCondition.BASE_MODIFIER.put("ii_wine", 0);
        ResourceDepositsCondition.BASE_ZERO.add("ii_wine");
    }

    /* Unused for now, but here for posterity */
//    private static void updateConditionSpecs() {
//        Object o;
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "atmosphere_no_pick", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec atmosphere_no_pick = (ConditionGenDataSpec) o;
//            atmosphere_no_pick.getMultipliers().put("ii_irradiated-bombarded", 10f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "no_atmosphere", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec no_atmosphere = (ConditionGenDataSpec) o;
//            no_atmosphere.getMultipliers().put("ii_auric", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "thin_atmosphere", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec thin_atmosphere = (ConditionGenDataSpec) o;
//            thin_atmosphere.getMultipliers().put("ii_irradiated-bombarded", 1f);
//            thin_atmosphere.getMultipliers().put("ii_auric", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "toxic_atmosphere", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec toxic_atmosphere = (ConditionGenDataSpec) o;
//            toxic_atmosphere.getMultipliers().put("ii_irradiated-bombarded", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "weather_no_pick", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec weather_no_pick = (ConditionGenDataSpec) o;
//            weather_no_pick.getMultipliers().put("ii_irradiated-bombarded", 10f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "extreme_weather", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec extreme_weather = (ConditionGenDataSpec) o;
//            extreme_weather.getMultipliers().put("ii_irradiated-bombarded", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "irradiated", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec irradiated = (ConditionGenDataSpec) o;
//            irradiated.getMultipliers().put("ii_irradiated-bombarded", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "biosphere_no_pick", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec biosphere_no_pick = (ConditionGenDataSpec) o;
//            biosphere_no_pick.getMultipliers().put("ii_irradiated-bombarded", 10000f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "inimical_biosphere", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec inimical_biosphere = (ConditionGenDataSpec) o;
//            inimical_biosphere.getMultipliers().put("ii_auric", 10f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "ore_sparse", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec ore_sparse = (ConditionGenDataSpec) o;
//            ore_sparse.getMultipliers().put("ii_cobalt", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "ore_moderate", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec ore_moderate = (ConditionGenDataSpec) o;
//            ore_moderate.getMultipliers().put("ii_cobalt", 5f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "ore_abundant", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec ore_abundant = (ConditionGenDataSpec) o;
//            ore_abundant.getMultipliers().put("ii_cobalt", 5f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "ore_rich", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec ore_rich = (ConditionGenDataSpec) o;
//            ore_rich.getMultipliers().put("ii_cobalt", 4f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "ore_ultrarich", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec ore_ultrarich = (ConditionGenDataSpec) o;
//            ore_ultrarich.getMultipliers().put("ii_cobalt", 3f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "rare_ore_no_pick", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec rare_ore_no_pick = (ConditionGenDataSpec) o;
//            rare_ore_no_pick.getMultipliers().put("ii_auric", 0f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "rare_ore_sparse", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec rare_ore_sparse = (ConditionGenDataSpec) o;
//            rare_ore_sparse.getMultipliers().put("ii_auric", 0f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "rare_ore_moderate", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec rare_ore_moderate = (ConditionGenDataSpec) o;
//            rare_ore_moderate.getMultipliers().put("ii_auric", 10f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "rare_ore_abundant", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec rare_ore_abundant = (ConditionGenDataSpec) o;
//            rare_ore_abundant.getMultipliers().put("ii_auric", 20f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "rare_ore_rich", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec rare_ore_rich = (ConditionGenDataSpec) o;
//            rare_ore_rich.getMultipliers().put("ii_auric", 20f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "rare_ore_ultrarich", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec rare_ore_ultrarich = (ConditionGenDataSpec) o;
//            rare_ore_ultrarich.getMultipliers().put("ii_auric", 10f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "volatiles_no_pick", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec volatiles_no_pick = (ConditionGenDataSpec) o;
//            volatiles_no_pick.getMultipliers().put("ii_auric", 100f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "volatiles_trace", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec volatiles_trace = (ConditionGenDataSpec) o;
//            volatiles_trace.getMultipliers().put("ii_auric", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "volatiles_diffuse", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec volatiles_diffuse = (ConditionGenDataSpec) o;
//            volatiles_diffuse.getMultipliers().put("ii_auric", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "volatiles_abundant", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec volatiles_abundant = (ConditionGenDataSpec) o;
//            volatiles_abundant.getMultipliers().put("ii_auric", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "volatiles_plentiful", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec volatiles_plentiful = (ConditionGenDataSpec) o;
//            volatiles_plentiful.getMultipliers().put("ii_auric", 1f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "ruins_no_pick", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec ruins_no_pick = (ConditionGenDataSpec) o;
//            ruins_no_pick.getMultipliers().put("ii_irradiated-bombarded", 400f);
//        }
//
//        o = Global.getSettings().getSpec(ConditionGenDataSpec.class, "decivilized", true);
//        if (o instanceof ConditionGenDataSpec) {
//            ConditionGenDataSpec decivilized = (ConditionGenDataSpec) o;
//            decivilized.getMultipliers().put("ii_irradiated-bombarded", 1f);
//        }
//    }
    @Override
    public void configureXStream(XStream x) {
        x.alias("II_HostileTerrain", II_HostileTerrain.class);
        x.alias("II_ImperialDoctrine", II_ImperialDoctrine.class);
        x.alias("II_ImperialGuardHQ", II_ImperialGuardHQ.class);
        x.alias("II_DefenderAssignmentAI", II_DefenderAssignmentAI.class);
        x.alias("II_OrbitalStation", II_OrbitalStation.class);
        x.alias("II_StellaCastellum", II_StellaCastellum.class);
        x.alias("II_IGPatrolAssignmentAI", II_IGPatrolAssignmentAI.class);
        x.alias("II_InterstellarBazaar", II_InterstellarBazaar.class);
        x.alias("II_EBaySubmarketPlugin", II_EBaySubmarketPlugin.class);
        x.alias("II_AtmosphericHazeTerrainPlugin", II_AtmosphericHazeTerrainPlugin.class);
        x.alias("II_ChargedNebulaTerrainPlugin", II_ChargedNebulaTerrainPlugin.class);
        x.alias("II_ChargedStormBoost", II_ChargedStormBoost.class);
        x.alias("II_TachyonFieldTerrainPlugin", II_TachyonFieldTerrainPlugin.class);
        x.alias("II_TitanManagerScript", II_TitanManagerScript.class);
        x.alias("II_TitanPunisher", II_TitanPunisher.class);
        x.alias("II_Corsica", II_Corsica.class);
        x.alias("II_Ex_Vis", II_Ex_Vis.class);
        x.alias("II_Thracia", II_Thracia.class);
        x.alias("II_Yma", II_Yma.class);
    }

    @Override
    public void onApplicationLoad() throws IOException, JSONException {
        boolean hasLazyLib = Global.getSettings().getModManager().isModEnabled("lw_lazylib");
        if (!hasLazyLib) {
            throw new RuntimeException("The Interstellar Imperium requires LazyLib by LazyWizard!");
        }

        isExerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        hasGraphicsLib = Global.getSettings().getModManager().isModEnabled("shaderLib");
        hasSWP = Global.getSettings().getModManager().isModEnabled("swp");
        hasStarshipLegends = Global.getSettings().getModManager().isModEnabled("sun_starship_legends");
        scyExists = Global.getSettings().getModManager().isModEnabled("SCY");

        hasMagicLib = Global.getSettings().getModManager().isModEnabled("MagicLib");

        if (hasGraphicsLib) {
            II_ModPluginAlt.initShaderLib();
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        //updateConditionSpecs();
        Global.getSector().registerPlugin(new II_CampaignPlugin());
        Global.getSector().addTransientScript(new II_TitanManagerScript());
        Global.getSector().addTransientScript(new II_BlockedHullmodDisplayScript());
        if (!Global.getSector().hasScript(II_MarketRiggerScript.class)) {
            Global.getSector().addScript(new II_MarketRiggerScript());
        }
        Global.getSector().getListenerManager().addListener(new II_TitanPunisher(), true);
        II_ModPluginAlt.adjustExerelin();

        syncCodex();
    }

    @Override
    public void onNewGame() {
        ProcgenUsedNames.notifyUsed("Thracia");
        ProcgenUsedNames.notifyUsed("Babylon");
        ProcgenUsedNames.notifyUsed("Byzantium");
        ProcgenUsedNames.notifyUsed("Perinthus");
        ProcgenUsedNames.notifyUsed("Serdica");
        ProcgenUsedNames.notifyUsed("Traian");
        ProcgenUsedNames.notifyUsed("Hadrian");
        ProcgenUsedNames.notifyUsed("Cirrus");
        ProcgenUsedNames.notifyUsed("Cassus");

        ProcgenUsedNames.notifyUsed("Corsica");
        ProcgenUsedNames.notifyUsed("Aleria");
        ProcgenUsedNames.notifyUsed("Cydonia");
        ProcgenUsedNames.notifyUsed("Carthage");
        ProcgenUsedNames.notifyUsed("Hades");
        ProcgenUsedNames.notifyUsed("Arafa");
        ProcgenUsedNames.notifyUsed("Inferi");
        ProcgenUsedNames.notifyUsed("Vetus");
        ProcgenUsedNames.notifyUsed("Mortalis");

        ProcgenUsedNames.notifyUsed("Ex Vis");
        ProcgenUsedNames.notifyUsed("Saltus Divinus");
        ProcgenUsedNames.notifyUsed("Nova Capitalis");
        ProcgenUsedNames.notifyUsed("Sepulchrum");
        ProcgenUsedNames.notifyUsed("Remotum");
        ProcgenUsedNames.notifyUsed("Pulvis");
        ProcgenUsedNames.notifyUsed("Labes");
        ProcgenUsedNames.notifyUsed("Infernalis");

        //updateConditionSpecs();
        if (!Global.getSector().hasScript(II_MarketRiggerScript.class)) {
            Global.getSector().addScript(new II_MarketRiggerScript());
        }
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("interstellarimperium");
        II_ModPluginAlt.initII();
        II_ModPluginAlt.adjustExerelin();

        syncCodex();
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();

        MarketAPI market = Global.getSector().getEconomy().getMarket("ii_byzantium");
        if (market != null) {
            PersonAPI admin = Global.getFactory().createPerson();
            admin.setId("ii_minerva");
            admin.setFaction("interstellarimperium");
            admin.setGender(Gender.FEMALE);
            admin.setPostId("ii_dea");
            admin.setRankId("ii_dea");
            admin.setImportance(PersonImportance.VERY_HIGH);
            admin.getName().setFirst("Minerva");
            admin.getName().setLast("");
            admin.setPersonality(Personalities.RECKLESS);
            admin.setPortraitSprite("graphics/imperium/portraits/ii_minerva.png");

            admin.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
            admin.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
            admin.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
            admin.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
            admin.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
            admin.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
            admin.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
            admin.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
            admin.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
            admin.getStats().setSkillLevel(Skills.ENERGY_WEAPON_MASTERY, 2);
            admin.getStats().setSkillLevel(Skills.HYPERCOGNITION, 1);
            admin.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            admin.getStats().setLevel(10);
            admin.setAICoreId(Commodities.ALPHA_CORE);

            ip.addPerson(admin);

            market.setAdmin(admin);
            market.getCommDirectory().addPerson(admin, 0);
            market.addPerson(admin);
        }

        market = Global.getSector().getEconomy().getMarket("ii_nova_capitalis");
        if (market != null) {
            PersonAPI admin = Global.getFactory().createPerson();
            admin.setId("ii_vulcan");
            admin.setFaction("interstellarimperium");
            admin.setGender(Gender.MALE);
            admin.setPostId("ii_deus");
            admin.setRankId("ii_deus");
            admin.setImportance(PersonImportance.VERY_HIGH);
            admin.getName().setFirst("Vulcan");
            admin.getName().setLast("");
            admin.setPortraitSprite("graphics/imperium/portraits/ii_vulcan.png");

            admin.getStats().setSkillLevel(Skills.HYPERCOGNITION, 1);
            admin.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            admin.setAICoreId(Commodities.ALPHA_CORE);

            ip.addPerson(admin);

            market.setAdmin(admin);
            market.getCommDirectory().addPerson(admin, 0);
            market.addPerson(admin);
        }

        market = Global.getSector().getEconomy().getMarket("ii_aleria");
        if (market != null) {
            PersonAPI admin = Global.getFactory().createPerson();
            admin.setId("ii_justitia");
            admin.setFaction("interstellarimperium");
            admin.setGender(Gender.FEMALE);
            admin.setPostId("ii_avia");
            admin.setRankId("ii_avia");
            admin.setImportance(PersonImportance.VERY_HIGH);
            admin.getName().setFirst("Justitia");
            admin.getName().setLast("");
            admin.setPortraitSprite("graphics/imperium/portraits/ii_justitia.png");

            admin.getStats().setSkillLevel(Skills.HYPERCOGNITION, 1);
            admin.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            admin.setAICoreId(Commodities.ALPHA_CORE);

            ip.addPerson(admin);

            market.setAdmin(admin);
            market.getCommDirectory().addPerson(admin, 0);
            market.addPerson(admin);
        }
    }

    @Override
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        switch (missile.getProjectileSpecId()) {
            case BALLISTA_LRM_ID -> {
                return new PluginPick<>(new II_BallistaAI(missile, launchingShip), PickPriority.MOD_SET);
            }
            case BALLISTA_STAGE2_ID -> {
                return new PluginPick<>(new II_BallistaStage2AI(missile, launchingShip), PickPriority.MOD_SET);
            }
            case JAVELIN_MRM_ID, SUPER_JAVELIN_ID -> {
                return new PluginPick<>(new II_JavelinAI(missile, launchingShip), PickPriority.MOD_SET);
            }
            case PULSAR_BOMB_ID -> {
                return new PluginPick<>(new II_PulsarBombAI(missile, launchingShip), PickPriority.MOD_SET);
            }
            case FUNDAE_SRM_ID -> {
                if ((launchingShip != null) && (II_Util.getNonDHullId(launchingShip.getHullSpec()).contentEquals(TITAN_ARMOR_ID)
                        || (II_Util.getNonDHullId(launchingShip.getHullSpec()).contentEquals(TITAN_X_ID) && (missile.getWeapon() != null) && missile.getWeapon().getId().contentEquals("ii_boss_fundae")))) {
                    return new PluginPick<>(new II_FundaeMirvAI(missile, launchingShip), PickPriority.MOD_SET);
                } else {
                    return new PluginPick<>(new II_FundaeAI(missile, launchingShip, true), PickPriority.MOD_SET);
                }
            }
            case FUNDAE_SUB_ID -> {
                if ((launchingShip != null) && (II_Util.getNonDHullId(launchingShip.getHullSpec()).contentEquals(TITAN_ARMOR_ID)
                        || II_Util.getNonDHullId(launchingShip.getHullSpec()).contentEquals(TITAN_X_ID) && (missile.getWeapon() != null) && missile.getWeapon().getId().contentEquals("ii_boss_fundae"))) {
                    return new PluginPick<>(new II_FundaeMirvAI(missile, launchingShip), PickPriority.MOD_SET);
                } else {
                    return new PluginPick<>(new II_FundaeAI(missile, launchingShip, false), PickPriority.MOD_SET);
                }
            }
            case APOCALYPSE_MIRV_ID, ARMAGEDDON_NORMAL_ID -> {
                float which = (float) Math.random();
                if (which <= 0.4f) {
                    return new PluginPick<>(new II_ApocalypseAI1(missile, launchingShip), PickPriority.MOD_SET);
                } else if (which <= 0.8f) {
                    return new PluginPick<>(new II_ApocalypseAI2(missile, launchingShip), PickPriority.MOD_SET);
                } else if (which <= 0.9f) {
                    return new PluginPick<>(new II_ApocalypseAI3(missile, launchingShip), PickPriority.MOD_SET);
                }
                // else do vanilla guidance
            }
            case ARMAGEDDON_STANDARD_ID, ARMAGEDDON_ARMOR_ID, ARMAGEDDON_TARGETING_ID, ARMAGEDDON_ELITE_ID -> {
            }
            case ONAGER_MIRV_ID -> {
                return new PluginPick<>(new II_OnagerAI(missile, launchingShip), PickPriority.MOD_SET);
            }
            default -> {
            }
        }
        // do vanilla guidance
        return null;
    }

    @Override
    public PluginPick<ShipAIPlugin> pickShipAI(FleetMemberAPI member, ShipAPI ship) {
        switch (II_Util.getNonDHullId(ship.getHullSpec())) {
            case TITAN_ARMOR_ID, TITAN_TARGETING_ID -> {
                return new PluginPick<>(new II_TitanMIRVAI(ship), PickPriority.HIGHEST);
            }
            case TITAN_ID, TITAN_ELITE_ID -> {
                return new PluginPick<>(new II_TitanAI(ship), PickPriority.HIGHEST);
            }
            case TITAN_ARMOR_DOOR_ID, TITAN_TARGETING_DOOR_ID -> {
                return new PluginPick<>(new II_DummyAI(), PickPriority.HIGHEST);
            }
            default -> {
            }
        }

        if (ship.isFighter()) {
            return null;
        }

        if (ship.getHullSpec().getHullId().startsWith("ii_")) {
            /* Don't mess with tournaments */
            if (!Global.getSettings().getModManager().isModEnabled("aibattles")) {
                if (ship.getVariant().hasHullMod(II_BasePackage.ELITE_PACKAGE)
                        || (II_Util.getNonDHullId(ship.getHullSpec()).contentEquals("ii_adamas") && !ship.getVariant().hasHullMod(II_BasePackage.TARGETING_PACKAGE))) {
                    ShipAIConfig config = new ShipAIConfig();
                    config.personalityOverride = II_Util.getMoreAggressivePersonality(member, ship);
                    return new PluginPick<>(Global.getSettings().createDefaultShipAI(ship, config), PickPriority.MOD_SET);
                }
            }
        }

        return null;
    }

    @Override
    public PluginPick<AutofireAIPlugin> pickWeaponAutofireAI(WeaponAPI weapon) {
        if (LIGHTSPEAR_ID.contentEquals(weapon.getId()) || LIGHTSPEAR_STATION_ID.contentEquals(weapon.getId())) {
            return new PluginPick<>(new II_LightspearAI(weapon), PickPriority.MOD_SET);
        }
        return null;
    }

    @Override
    public void onCodexDataGenerated() {
        syncCodex();

        List<String> imperialPackages = new ArrayList<>();
        imperialPackages.add(II_BasePackage.ARMOR_PACKAGE);
        imperialPackages.add(II_BasePackage.TARGETING_PACKAGE);
        imperialPackages.add(II_BasePackage.ELITE_PACKAGE);
        for (String imperialPackage : imperialPackages) {
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_flares"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_microforge"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_turbofeeder"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_impulsebooster"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_overdrive"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_magnumsalvo"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_commandcenter"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_shockbuster"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_celeritydrive"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_luxfinis"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getShipSystemEntryId("ii_arbalestloader"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getWeaponEntryId("ii_lightspear"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getWeaponEntryId("ii_titan_w"));
            CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), CodexDataV2.getWeaponEntryId("ii_magna_fulmen"));
        }

        List<String> allImperialPackages = new ArrayList<>(imperialPackages);
        allImperialPackages.add(II_BasePackage.CORE_UPGRADES);
        CodexEntryPlugin ships = CodexDataV2.getEntry(CodexDataV2.CAT_SHIPS);
        for (CodexEntryPlugin ship : ships.getChildren()) {
            if (ship.getParam() instanceof ShipHullSpecAPI spec) {
                if (spec.getHullId().startsWith("ii_") && (spec.getHullSize() != HullSize.FIGHTER) && !spec.getBuiltInMods().contains(II_BasePackage.NO_PACKAGE)
                        && !spec.getHullId().startsWith("ii_titan")) {
                    for (String imperialPackage : allImperialPackages) {
                        CodexDataV2.makeRelated(CodexDataV2.getHullmodEntryId(imperialPackage), ship.getId());
                    }
                }
            }
        }

        CodexDataV2.makeRelated(CodexDataV2.getShipEntryId("ii_titan"), CodexDataV2.getShipEntryId("ii_titan_armor"), CodexDataV2.getShipEntryId("ii_titan_targeting"),
                CodexDataV2.getShipEntryId("ii_titan_elite"), CodexDataV2.getWeaponEntryId("ii_titan_w"), CodexDataV2.getShipEntryId("ii_olympus"));
        CodexDataV2.makeRelated(CodexDataV2.getShipSystemEntryId("ii_drone_station"), CodexDataV2.getShipEntryId("ii_station3"), CodexDataV2.getShipEntryId("ii_tribunal"));
        CodexDataV2.makeRelated(CodexDataV2.getShipSystemEntryId("ii_drone_station_super"), CodexDataV2.getShipEntryId("ii_station4"), CodexDataV2.getShipEntryId("ii_tribunal_shock"));
        CodexDataV2.makeRelated(CodexDataV2.getShipEntryId("ii_clypeus"), CodexDataV2.getShipSystemEntryId("ii_drone_station2_half"));
        CodexDataV2.makeRelated(CodexDataV2.getShipEntryId("ii_clypeus"), CodexDataV2.getShipEntryId("ii_station2"));
        CodexDataV2.makeRelated(CodexDataV2.getShipEntryId("ii_clypeus"), CodexDataV2.getShipSystemEntryId("ii_drone_station2"));
        CodexDataV2.makeRelated(CodexDataV2.getShipEntryId("ii_clypeus"), CodexDataV2.getShipEntryId("ii_station3"));
        CodexDataV2.makeRelated(CodexDataV2.getShipEntryId("ii_clypeus"), CodexDataV2.getShipSystemEntryId("ii_drone_station2_super"));
        CodexDataV2.makeRelated(CodexDataV2.getShipEntryId("ii_clypeus"), CodexDataV2.getShipEntryId("ii_station4"));
        CodexDataV2.makeRelated(CodexDataV2.getWeaponEntryId("ii_photonblaster_left"), CodexDataV2.getWeaponEntryId("ii_lightspear"), CodexDataV2.getShipSystemEntryId("ii_luxfinis"));
        CodexDataV2.makeRelated(CodexDataV2.getWeaponEntryId("ii_photonbeam_station"), CodexDataV2.getWeaponEntryId("ii_lightspear_station"), CodexDataV2.getShipSystemEntryId("ii_luxfinis"));
        CodexDataV2.makeRelated(CodexDataV2.getWeaponEntryId("ii_fundae_s"), CodexDataV2.getWeaponEntryId("ii_onager"));
        CodexDataV2.makeRelated(CodexDataV2.getWeaponEntryId("ii_magna_fulmen"), CodexDataV2.getShipSystemEntryId("ii_arbalestloader"));
        CodexDataV2.makeRelated(CodexDataV2.getWeaponEntryId("ii_armageddon"), CodexDataV2.getShipSystemEntryId("ii_magnumsalvo"));
        CodexDataV2.makeRelated(CodexDataV2.getWeaponEntryId("ii_armageddon_station"), CodexDataV2.getShipSystemEntryId("ii_magnumsalvo_station"));
        CodexDataV2.makeRelated(CodexDataV2.getWeaponEntryId("ii_apocalypse_mirv"), CodexDataV2.getShipEntryId("ii_boss_titanx"));
        CodexDataV2.makeRelated(CodexDataV2.getWeaponEntryId("ii_fundae_mirv"), CodexDataV2.getShipEntryId("ii_boss_titanx"));

        CodexDataV2.makeUnrelated(CodexDataV2.getHullmodEntryId("magazines"), CodexDataV2.getWeaponEntryId("ii_lightspear"));
        CodexDataV2.makeUnrelated(CodexDataV2.getHullmodEntryId("magazines"), CodexDataV2.getWeaponEntryId("ii_lightspear_station"));
        CodexDataV2.makeUnrelated(CodexDataV2.getHullmodEntryId("magazines"), CodexDataV2.getWeaponEntryId("ii_pulsarbomb"));
        CodexDataV2.makeUnrelated(CodexDataV2.getHullmodEntryId("magazines"), CodexDataV2.getWeaponEntryId("ii_photonbeam_station"));
        CodexDataV2.makeUnrelated(CodexDataV2.getHullmodEntryId("missile_autoloader"), CodexDataV2.getWeaponEntryId("ii_javelin_f"));
        CodexDataV2.makeUnrelated(CodexDataV2.getShipEntryId("ii_titan_armor_door"), CodexDataV2.getShipEntryId("ii_titan_armor"));
        CodexDataV2.makeUnrelated(CodexDataV2.getShipEntryId("ii_titan_targeting_door"), CodexDataV2.getShipEntryId("ii_titan_targeting"));
    }

    public static void syncCodex() {
        if (!hasSWP) {
            if ((Global.getSector() == null) || !Global.getSector().getMemoryWithoutUpdate().getBoolean("$ii_yamatostart")) {
                Global.getSettings().getHullSpec("ii_boss_dominus").addTag(Tags.INVISIBLE_IN_CODEX);
                Global.getSettings().getWeaponSpec("ii_boss_wavemotion").addTag(Tags.INVISIBLE_IN_CODEX);
                Global.getSettings().getShipSystemSpec("ii_boss_modifiedbooster").addTag(Tags.INVISIBLE_IN_CODEX);
            }
            Global.getSettings().getHullSpec("ii_boss_titanx").addTag(Tags.INVISIBLE_IN_CODEX);
            Global.getSettings().getWeaponSpec("ii_boss_apocalypse").addTag(Tags.INVISIBLE_IN_CODEX);
            Global.getSettings().getWeaponSpec("ii_boss_fundae").addTag(Tags.INVISIBLE_IN_CODEX);
            Global.getSettings().getShipSystemSpec("ii_empburst").addTag(Tags.INVISIBLE_IN_CODEX);
        }
    }
}
