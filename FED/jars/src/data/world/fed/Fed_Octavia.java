package data.world.fed;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import com.fs.starfarer.api.util.Misc;

public class Fed_Octavia {

    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Octavia VIII");
        system.setBaseName("Octavia VIII");
        system.getLocation().set(-9500, 13500);
        system.setBackgroundTextureFilename("graphics/FED/backgrounds/fed_background_tarti.jpg");
        system.setEnteredByPlayer(true);

        PlanetAPI fed_octavia_sun = system.initStar("Octavia", StarTypes.ORANGE, 550f, 500f); // 0.9 solar masses

        // SectorEntityToken scaria_nebula = Misc.addNebulaFromPNG("data/campaign/terrain/gneiss_nebula.png",
        //                                                        0, 0, // center of nebula
        //                                                       system, // location to add to
        //                                                      "terrain", "nebula_greenish", // "nebula_blue", // texture to use, uses xxx_map for map
        //                                                     4, 4, StarAge.AVERAGE); // number of cells in texture
        //system.addAsteroidBelt(gneiss, 50, 1600, 255, 65, 75); // 0.32 AU
        //PlanetAPI scaria = system.addPlanet("scaria", gneiss, "Federation", "br_blackrockplanet", 300, 140, 3400, 215);
        // PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS PLANETS 
        //java.lang.String id, SectorEntityToken focus, String name, String type, float angle, float radius, float orbitRadius, float orbitDays
        PlanetAPI fed_headquarters = system.addPlanet("fed_headquarters", fed_octavia_sun, "Scaria", "terran-eccentric", 200, 130, 6300, 310);
        fed_headquarters.getSpec().setTexture(Global.getSettings().getSpriteName("planets", "fed_scaria"));
        fed_headquarters.getSpec().setTilt(160);
        fed_headquarters.getSpec().setCloudColor(new Color(150, 120, 50, 150));
        fed_headquarters.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "scaria_planet_glow"));
        fed_headquarters.getSpec().setGlowColor(new Color(255, 235, 205, 155));
        fed_headquarters.getSpec().setUseReverseLightForGlow(false);
        fed_headquarters.setFaction("star_federation");
        fed_headquarters.applySpecChanges();
        fed_headquarters.setInteractionImage("illustrations", "fed_HQ");
        fed_headquarters.setCustomDescriptionId("fed_hq_description");

        PlanetAPI fed_paletwin = system.addPlanet("fed_paletwin", fed_octavia_sun, "Pale Twin", "desert", 300, 100, 2700, 270); // 0.0025 AU
        fed_paletwin.setCustomDescriptionId("fed_paletwin_description");
        fed_paletwin.setInteractionImage("illustrations", "federation_base");
        fed_paletwin.getSpec().setRotation(5f); // 5 degrees/second = 7.2 days/revolution
        fed_paletwin.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "barren"));
        fed_paletwin.getSpec().setGlowColor(new Color(255, 255, 255, 255));
        //paletwin.getSpec().setUseReverseLightForGlow(true);
        fed_paletwin.setCustomDescriptionId("fed_paletwin_description");
        fed_paletwin.setFaction("independent");
        fed_paletwin.applySpecChanges();

        MarketAPI fed_paletwin_indie_market = Global.getFactory().createMarket("fed_paletwin_indie_market", fed_paletwin.getName(), 5);
        fed_paletwin_indie_market.setPrimaryEntity(fed_paletwin);
        fed_paletwin_indie_market.setFactionId("independent");
        fed_paletwin_indie_market.addCondition(Conditions.POPULATION_5);
        fed_paletwin_indie_market.addCondition(Conditions.EXTREME_WEATHER);
        fed_paletwin_indie_market.addCondition(Conditions.HOT);
        fed_paletwin_indie_market.addCondition(Conditions.RUINS_WIDESPREAD);
        fed_paletwin_indie_market.addCondition(Conditions.ORE_ABUNDANT);
        fed_paletwin_indie_market.addCondition(Conditions.RARE_ORE_MODERATE);
        fed_paletwin_indie_market.addCondition(Conditions.HABITABLE);
        //fed_paletwin_indie_market.addCondition(Conditions.LUDDIC_MAJORITY);
        fed_paletwin_indie_market.addCondition(Conditions.ORGANICS_TRACE);
        fed_paletwin_indie_market.addCondition(Conditions.HABITABLE);
        fed_paletwin_indie_market.addIndustry(Industries.SPACEPORT);
        fed_paletwin_indie_market.addIndustry(Industries.REFINING);
        fed_paletwin_indie_market.addIndustry(Industries.MINING);
        fed_paletwin_indie_market.addIndustry(Industries.POPULATION);
        fed_paletwin_indie_market.addIndustry(Industries.GROUNDDEFENSES);
        fed_paletwin_indie_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        fed_paletwin_indie_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        fed_paletwin_indie_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        fed_paletwin_indie_market.getTariff().setBaseValue(0.5f);

        fed_paletwin_indie_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        fed_paletwin.setMarket(fed_paletwin_indie_market);
        sector.getEconomy().addMarket(fed_paletwin_indie_market, true);

        for (MarketConditionAPI cond : fed_paletwin_indie_market.getConditions()) {
            cond.setSurveyed(true);
        }

        PlanetAPI fed_cessitoc = system.addPlanet("fed_cessitoc", fed_octavia_sun, "Cessitoc", "toxic", 100, 230, 10000, 390);
        fed_cessitoc.setCustomDescriptionId("fed_cessitoc_description");
        fed_cessitoc.setInteractionImage("illustrations", "urban00");
        fed_cessitoc.getSpec().setRotation(5f); // 5 degrees/second = 7.2 days/revolution
        fed_cessitoc.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "asharu"));
        fed_cessitoc.getSpec().setGlowColor(new Color(255, 255, 255, 255));
        fed_cessitoc.setFaction("star_federation");
        //paletwin.getSpec().setUseReverseLightForGlow(true);
        fed_cessitoc.applySpecChanges();

        MarketAPI fed_cessitoc_market = Global.getFactory().createMarket("fed_cessitoc_market", fed_cessitoc.getName(), 6);
        fed_cessitoc_market.setPrimaryEntity(fed_cessitoc);
        fed_cessitoc_market.setFactionId("star_federation");
        fed_cessitoc_market.addCondition(Conditions.POPULATION_6);
        fed_cessitoc_market.addCondition(Conditions.TOXIC_ATMOSPHERE);
        fed_cessitoc_market.addCondition(Conditions.POLLUTION);
        fed_cessitoc_market.addCondition(Conditions.HOT);
        fed_cessitoc_market.addCondition(Conditions.ORE_SPARSE);
        fed_cessitoc_market.addCondition(Conditions.RARE_ORE_ABUNDANT);
        fed_cessitoc_market.addCondition(Conditions.ORGANICS_COMMON);
        fed_cessitoc_market.addCondition(Conditions.DISSIDENT);
        fed_cessitoc_market.addCondition(Conditions.VOLATILES_DIFFUSE);
        fed_cessitoc_market.addCondition(Conditions.VICE_DEMAND);
        fed_cessitoc_market.addCondition(Conditions.DARK);
        fed_cessitoc_market.addCondition(Conditions.DENSE_ATMOSPHERE);
        fed_cessitoc_market.addIndustry(Industries.SPACEPORT);
        fed_cessitoc_market.addIndustry(Industries.LIGHTINDUSTRY);
        fed_cessitoc_market.addIndustry(Industries.MINING);
        fed_cessitoc_market.addIndustry(Industries.FUELPROD);
        fed_cessitoc_market.addIndustry(Industries.POPULATION);
        fed_cessitoc_market.addIndustry(Industries.GROUNDDEFENSES);
        fed_cessitoc_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        fed_cessitoc_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        fed_cessitoc_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        fed_cessitoc_market.getTariff().setBaseValue(0.5f);

        for (MarketConditionAPI cond : fed_cessitoc_market.getConditions()) {
            cond.setSurveyed(true);
        }

        fed_cessitoc_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        fed_cessitoc.setMarket(fed_cessitoc_market);
        sector.getEconomy().addMarket(fed_cessitoc_market, true);

        // STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS STATIONS
        SectorEntityToken FED_HQ_Station = system.addCustomEntity("fed_headquarters_station", "Command Spire", "fed_headquarters_station", "star_federation");
        FED_HQ_Station.setCustomDescriptionId("fed_scaria_station_description");
        FED_HQ_Station.setCircularOrbitPointingDown(fed_headquarters, 290, 400, 10.2f); // Locked to scaria
        FED_HQ_Station.setInteractionImage("illustrations", "orbital");

        MarketAPI fed_headquarters_market = Global.getFactory().createMarket("fed_headquarters_market", fed_headquarters.getName(), 6);
        fed_headquarters_market.setPrimaryEntity(FED_HQ_Station);
        fed_headquarters_market.setFactionId("star_federation");
        fed_headquarters_market.addCondition(Conditions.POPULATION_6);
        fed_headquarters_market.addCondition(Conditions.EXTREME_WEATHER);
        fed_headquarters_market.addCondition(Conditions.POLLUTION);
        fed_headquarters_market.addCondition(Conditions.REGIONAL_CAPITAL);
        fed_headquarters_market.addCondition(Conditions.HABITABLE);
        fed_headquarters_market.addCondition(Conditions.FARMLAND_ADEQUATE);
        fed_headquarters_market.addCondition(Conditions.ORE_SPARSE);
        fed_headquarters_market.addCondition(Conditions.ORGANICS_TRACE);
        fed_headquarters_market.addIndustry(Industries.MEGAPORT);
        fed_headquarters_market.addIndustry(Industries.ORBITALWORKS);
        fed_headquarters_market.addIndustry(Industries.WAYSTATION);
        fed_headquarters_market.addIndustry(Industries.FARMING);
        fed_headquarters_market.addIndustry(Industries.POPULATION);
        fed_headquarters_market.addIndustry(Industries.HIGHCOMMAND);
        fed_headquarters_market.addIndustry(Industries.HEAVYBATTERIES, new ArrayList<>(Arrays.asList(Items.DRONE_REPLICATOR)));
        fed_headquarters_market.addIndustry("fed_starfortress");
        fed_headquarters_market.getIndustry("fed_starfortress").setAICoreId(Commodities.ALPHA_CORE);
        fed_headquarters_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        fed_headquarters_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        fed_headquarters_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        fed_headquarters_market.addSubmarket(Submarkets.GENERIC_MILITARY);
        fed_headquarters_market.getTariff().setBaseValue(0.5f);

        fed_headquarters_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        fed_headquarters.setMarket(fed_headquarters_market);
        sector.getEconomy().addMarket(fed_headquarters_market, true);

        FED_HQ_Station.setMarket(fed_headquarters_market);
        FED_HQ_Station.getOrbitFocus().setMarket(fed_headquarters_market);

        // PALE TWIN STATION RIDER STATION
        SectorEntityToken FED_PaleTwin_Outpost = system.addCustomEntity("fed_paletwin_station", "Rider Station", "fed_paletwin_station", "star_federation");
        FED_PaleTwin_Outpost.setCircularOrbitPointingDown(fed_paletwin, 270, 375, 12.2f); // Locked to paletwin
        FED_PaleTwin_Outpost.setInteractionImage("illustrations", "orbital");
        FED_PaleTwin_Outpost.setCustomDescriptionId("fed_outpost_description");

        MarketAPI fed_paletwin_station_market = Global.getFactory().createMarket("fed_paletwin_station_market", FED_PaleTwin_Outpost.getName(), 4);
        fed_paletwin_station_market.setPrimaryEntity(FED_PaleTwin_Outpost);
        fed_paletwin_station_market.setFactionId("star_federation");
        fed_paletwin_station_market.addCondition(Conditions.POPULATION_4);
        fed_paletwin_station_market.addCondition(Conditions.NO_ATMOSPHERE);
        fed_paletwin_station_market.addCondition(Conditions.LARGE_REFUGEE_POPULATION);
        fed_paletwin_station_market.addIndustry(Industries.SPACEPORT);
        fed_paletwin_station_market.addIndustry(Industries.ORBITALWORKS);
        fed_paletwin_station_market.addIndustry(Industries.STARFORTRESS);
        fed_paletwin_station_market.addIndustry(Industries.MILITARYBASE);
        fed_paletwin_station_market.addIndustry(Industries.POPULATION);
        fed_paletwin_station_market.addIndustry(Industries.HEAVYBATTERIES);
        fed_paletwin_station_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        fed_paletwin_station_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        fed_paletwin_station_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        fed_paletwin_station_market.addSubmarket(Submarkets.GENERIC_MILITARY);
        fed_paletwin_station_market.getTariff().setBaseValue(0.5f);

        fed_paletwin_station_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        FED_PaleTwin_Outpost.setMarket(fed_paletwin_station_market);
        sector.getEconomy().addMarket(fed_paletwin_station_market, true);

        SectorEntityToken FED_Cessitoc_Outpost = system.addCustomEntity("fed_cessitoc_station", "Fuel Depot 22", "fed_cessitoc_station", "independent");
        FED_Cessitoc_Outpost.setCircularOrbitPointingDown(fed_cessitoc, 270, 375, 20.2f); // Locked to paletwin
        FED_Cessitoc_Outpost.setInteractionImage("illustrations", "orbital");
        FED_Cessitoc_Outpost.setCustomDescriptionId("fed_cessitoc_fueldepot_description");

        MarketAPI fed_cessitoc_indie_station_market = Global.getFactory().createMarket("fed_cessitoc_indie_station_market", FED_Cessitoc_Outpost.getName(), 4);
        fed_cessitoc_indie_station_market.setPrimaryEntity(FED_Cessitoc_Outpost);
        fed_cessitoc_indie_station_market.setFactionId("independent");
        fed_cessitoc_indie_station_market.addCondition(Conditions.POPULATION_4);
        fed_cessitoc_indie_station_market.addCondition(Conditions.NO_ATMOSPHERE);
        fed_cessitoc_indie_station_market.addCondition(Conditions.FREE_PORT);
        fed_cessitoc_indie_station_market.addIndustry(Industries.SPACEPORT);
        fed_cessitoc_indie_station_market.addIndustry(Industries.ORBITALSTATION_HIGH);
        fed_cessitoc_indie_station_market.getIndustry(Industries.ORBITALSTATION_HIGH).setAICoreId(Commodities.ALPHA_CORE);
        fed_cessitoc_indie_station_market.addIndustry(Industries.FUELPROD);
        fed_cessitoc_indie_station_market.addIndustry(Industries.POPULATION);
        fed_cessitoc_indie_station_market.addIndustry(Industries.LIGHTINDUSTRY);
        fed_cessitoc_indie_station_market.addIndustry(Industries.HEAVYBATTERIES);
        fed_cessitoc_indie_station_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        fed_cessitoc_indie_station_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        fed_cessitoc_indie_station_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        fed_cessitoc_indie_station_market.getTariff().setBaseValue(0.5f);

        fed_cessitoc_indie_station_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        FED_Cessitoc_Outpost.setMarket(fed_cessitoc_indie_station_market);
        sector.getEconomy().addMarket(fed_cessitoc_indie_station_market, true);

        // STABLE LOCATIONS AND RELAYS  STABLE LOCATIONS AND RELAYS  STABLE LOCATIONS AND RELAYS  STABLE LOCATIONS AND RELAYS  STABLE LOCATIONS AND RELAYS 
        SectorEntityToken relay = system.addCustomEntity("fed_relay", "Scaria Relay", "comm_relay",
                "star_federation");
        relay.setCircularOrbit(fed_octavia_sun, 220, 3650, 215);

        SectorEntityToken stableloc2 = system.addCustomEntity(null, null, "stable_location", Factions.NEUTRAL);
        stableloc2.setCircularOrbitPointingDown(fed_octavia_sun, 40, 3650, 215f);

        SectorEntityToken stableloc3 = system.addCustomEntity(null, null, "stable_location", Factions.NEUTRAL);
        stableloc3.setCircularOrbitPointingDown(fed_octavia_sun, 310, 3650, 215f);

        // DECORATIONS DECORATIONS DECORATIONS DECORATIONS DECORATIONS DECORATIONS DECORATIONS DECORATIONS DECORATIONS DECORATIONS DECORATIONS DECORATIONS DECORATIONS 
        SectorEntityToken gate = system.addCustomEntity("octavia_gate", "Octavia Gate", // name - if null, defaultName from custom_entities.json will be used
                "inactive_gate", // type of object, defined in custom_entities.json
                null);
        gate.setCircularOrbit(fed_octavia_sun, 35 - 60, 9000, 700);

        system.addAsteroidBelt(fed_octavia_sun, 430, 5200, 256, 440, 470);

        system.addRingBand(fed_octavia_sun, "misc", "rings_dust0", 256f, 5, Color.white, 256f, 4000, 360f, "ring", "Dust Ring");
        system.addRingBand(fed_octavia_sun, "misc", "rings_dust0", 256f, 7, Color.white, 256f, 4010, 370f, "ring", "Dust Ring");
        system.addRingBand(fed_octavia_sun, "misc", "rings_dust0", 256f, 10, Color.white, 256f, 4020, 380f, "ring", "Dust Ring");

        system.addRingBand(fed_octavia_sun, "misc", "rings_dust0", 256f, 5, Color.lightGray, 256f, 8500, 560f, "ring", "Dust Ring");
        system.addRingBand(fed_octavia_sun, "misc", "rings_dust0", 256f, 7, Color.lightGray, 256f, 8510, 560f, "ring", "Dust Ring");
        system.addRingBand(fed_octavia_sun, "misc", "rings_dust0", 256f, 10, Color.lightGray, 256f, 8520, 560f, "ring", "Dust Ring");

        system.addRingBand(fed_octavia_sun, "misc", "rings_asteroids0", 256f, 2, Color.gray, 256f, 5250, 455f);
        system.addRingBand(fed_octavia_sun, "misc", "rings_asteroids0", 256f, 3, Color.lightGray, 256f, 5150, 445f);

        // system.addAsteroidBelt(nanoplanet, 70, 900, 128, 10, 16);
        //SectorEntityToken vigil = system.addCustomEntity("brstation2", "Vigil Station", "br_station", "blackrock_driveyards");
        //vigil.setCircularOrbitPointingDown(system.getEntityById("nanoplanet"), 90, 540, 11);
        //vigil.setInteractionImage("illustrations", "blackrock_vigil_station");
        // vigil.setCustomDescriptionId("blackrock_vigil");
        // JUMP POINTS  JUMP POINTS  JUMP POINTS  JUMP POINTS  JUMP POINTS  JUMP POINTS  JUMP POINTS  JUMP POINTS  JUMP POINTS  JUMP POINTS  JUMP POINTS  JUMP POINTS  
        JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("scaria_jp", "Sector 8 Jump");
        OrbitAPI orbit = Global.getFactory().createCircularOrbit(fed_headquarters, 90, 550, 25);
        jumpPoint.setOrbit(orbit);
        jumpPoint.setRelatedPlanet(fed_headquarters);
        jumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint);

        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint("paletwin_jp", "Pale Twin Portal");
        OrbitAPI orbit2 = Global.getFactory().createCircularOrbit(fed_paletwin, 100, 550, 25);
        jumpPoint2.setOrbit(orbit2);
        jumpPoint2.setRelatedPlanet(fed_paletwin);
        jumpPoint2.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint2);

        // PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN  PROCGEN   
        /*float radiusAfter = StarSystemGenerator.addOrbitingEntities(system, fed_octavia_sun, StarAge.AVERAGE,
                                                                    2, 4, // min/max entities to add
                                                                    10200, // radius to start adding at
                                                                    4, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                                                                    true); // whether to use custom or system-name based names
         */
        system.autogenerateHyperspaceJumpPoints(true, true); //begone evil clouds
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;

        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);
    }

    //   void cleanup(StarSystemAPI system) {
    //  HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
    //  NebulaEditor editor = new NebulaEditor(plugin);
    //  float minRadius = plugin.getTileSize() * 2f;
    //  float radius = system.getMaxRadiusInHyperspace();
    //  editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0, 360f);
    //   editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);
    //}
}
