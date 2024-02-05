package birdwanderer.birdscollection.data.scripts.world.systems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;

import birdwanderer.birdscollection.data.scripts.world.systems.addMarketplace;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;

import static birdwanderer.birdscollection.data.scripts.world.systems.addMarketplace.addMarketplace;


public class bc_deixis {
    public void generate(SectorAPI sector) {

        StarSystemAPI system = sector.createStarSystem("Deixis");
        system.getLocation().set(-8000, 15000); //north and left a bit

        system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

        // create the star and generate the hyperspace anchor for this system
        PlanetAPI deixisStar = system.initStar("deixisStar", // unique id for this star
                "star_red_dwarf", // id in planets.json
                650f, // radius (in pixels at default zoom)
                200); // corona radius, from star edge
        system.setLightColor(new Color(239, 155, 128)); // light color in entire system, affects all entities

        float asteroidBelt1Dist = 4150f;
        //asteroid belt1 ring
        system.addAsteroidBelt(deixisStar, 1000, asteroidBelt1Dist, 800, 250, 400, Terrain.ASTEROID_BELT, "Palas' Joke");
        system.addRingBand(deixisStar, "misc", "rings_asteroids0", 256f, 3, Color.gray, 256f, asteroidBelt1Dist - 200, 250f);
        system.addRingBand(deixisStar, "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, asteroidBelt1Dist, 350f);
        system.addRingBand(deixisStar, "misc", "rings_asteroids0", 256f, 2, Color.gray, 256f, asteroidBelt1Dist + 200, 400f);

        system.addAsteroidBelt(deixisStar, 100, asteroidBelt1Dist - 3000, 100, 250, 400, Terrain.ASTEROID_BELT, "Inner Remnant");
        system.addRingBand(deixisStar, "misc", "rings_asteroids0", 256f, 2, Color.gray, 256f, asteroidBelt1Dist - 3000, 400f);

        float qanatDist = 1950f;
        float folgersDist = 5550f;
        float tenskullDist = 700f;
        float polsteinDist = 3150f;
        float commrelayDist = 2250f;
        float buoyDist = 10500f;
        float jumpFringeDist = 7000f;
        float jumpQanatDist = 2750f;
        float robertsDist = 9150f;

        SectorEntityToken inner_relay = system.addCustomEntity("deixis_relay", "Deixis Relay", "comm_relay",
                "faysco");
        inner_relay.setCircularOrbit(deixisStar, 220f, commrelayDist, 200f);

        SectorEntityToken outer_buoy = system.addCustomEntity("deixis_buoy", "Deixis Buoy", "nav_buoy_makeshift",
                "faysco");
        outer_buoy.setCircularOrbit(deixisStar, 10f, buoyDist, 1200f);


        PlanetAPI qanat = system.addPlanet("qanat",
                deixisStar,
                "Qanat",
                "barren3",
                400,
                100f,
                qanatDist,
                180f);
        qanat.setCustomDescriptionId("qanat"); //reference descriptions.csv
        qanat.getMarket().addCondition(Conditions.RUINS_WIDESPREAD);
        qanat.getMarket().addCondition(Conditions.LOW_GRAVITY);
        qanat.getMarket().addCondition(Conditions.HOT);
        qanat.getMarket().addCondition(Conditions.DECIVILIZED);
        qanat.getMarket().addCondition(Conditions.ORE_SPARSE);
        qanat.getMarket().addCondition(Conditions.RARE_ORE_RICH);

        PlanetAPI folgers = system.addPlanet("folgers",
                deixisStar,
                "Folgers",
                "ice_giant",
                1000,
                300f,
                folgersDist,
                8000f);
        folgers.getMarket().addCondition(Conditions.HIGH_GRAVITY);
        folgers.getMarket().addCondition(Conditions.VERY_COLD);
        folgers.getMarket().addCondition(Conditions.DARK);
        folgers.getMarket().addCondition(Conditions.VOLATILES_ABUNDANT);

        PlanetAPI tenskull = system.addPlanet("tenskull",
                folgers,
                "Ten Skulls",
                "frozen",
                100,
                50f,
                tenskullDist,
                200f);
        tenskull.setCustomDescriptionId("tenskull");

        PlanetAPI polstein = system.addPlanet("polstein",
                deixisStar,
                "Polstein",
                "arid",
                180,
                90f,
                polsteinDist,
                200f);
        polstein.setCustomDescriptionId("polstein"); //reference descriptions.csv

        PlanetAPI roberts = system.addPlanet("roberts",
                deixisStar,
                "Roberts",
                "bc_gas_giant",
                180,
                600f,
                robertsDist,
                800f);

        roberts.getMarket().addCondition(Conditions.HIGH_GRAVITY);
        roberts.getMarket().addCondition(Conditions.VERY_COLD);
        roberts.getMarket().addCondition(Conditions.DARK);
        roberts.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);
        roberts.getMarket().addCondition(Conditions.TOXIC_ATMOSPHERE);
        roberts.getMarket().addCondition(Conditions.EXTREME_WEATHER);

        roberts.getSpec().setPlanetColor(new Color(163, 185, 242,255));
        roberts.getSpec().setAtmosphereColor(new Color(150,170,240,150));
        roberts.getSpec().setCloudColor(new Color( 158, 127, 241,130));
        roberts.getSpec().setIconColor(new Color( 208, 161, 250,255));
        roberts.applySpecChanges();
        roberts.setCustomDescriptionId("roberts"); //reference descriptions.csv

        system.addRingBand(roberts, "misc", "rings_special0", 256f, 3, Color.MAGENTA, 256f, 600, 250f);
        system.addRingBand(roberts, "misc", "rings_special0", 256f, 1, Color.MAGENTA, 156f, 1600, 550f);

        SectorEntityToken roberts_array = system.addCustomEntity("roberts_array", "Roberts Sensor Array", "sensor_array_makeshift",
                "faysco");
        roberts_array.setCircularOrbit(roberts, 180f, 1100, 150f);

        PlanetAPI knarl = system.addPlanet("knarl",
                roberts,
                "Knarl",
                "cryovolcanic",
                180,
                90f,
                2000,
                600f);
        knarl.setCustomDescriptionId("knarl");

        MarketAPI knarl_market = addMarketplace.addMarketplace("faysco", knarl,
                null,
                "Knarl",
                5,
                new ArrayList<>(Arrays.asList(
                        Conditions.POPULATION_5,
                        Conditions.DISSIDENT,
                        Conditions.RARE_ORE_RICH,
                        Conditions.VOLATILES_PLENTIFUL,
                        Conditions.VERY_COLD,
                        Conditions.NO_ATMOSPHERE,
                        Conditions.FREE_PORT)),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.HEAVYBATTERIES,
                        Industries.WAYSTATION,
                        Industries.CRYOSANCTUM)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
                0.15f
        );
        knarl_market.addIndustry(Industries.MINING, new ArrayList<>(Arrays.asList(Items.MANTLE_BORE)));


        SectorEntityToken extractorstation = system.addCustomEntity("extractorstation", "Garrison Post Kappa", "bc_station_side", "faysco");
        extractorstation.setCircularOrbitPointingDown(roberts, 0f, 1100, 150f);
        extractorstation.setCustomDescriptionId("garrison_post");
        MarketAPI extractorstation_market = addMarketplace.addMarketplace("faysco", extractorstation,
                null,
                "Garrison Post Kappa",
                4,
                new ArrayList<>(Arrays.asList(
                        Conditions.POPULATION_4,
                        Conditions.OUTPOST)),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.MILITARYBASE,
                        Industries.HEAVYBATTERIES,
                        Industries.BATTLESTATION_MID)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
                0.15f
        );



        SectorEntityToken faystation = system.addCustomEntity("faysstation", "Fays Station", "station_midline3", "faysco");
        faystation.setCircularOrbitPointingDown(qanat, 22f, 240f, 120f);
        faystation.setCustomDescriptionId("faystation");
        MarketAPI faystation_market = addMarketplace.addMarketplace("faysco", faystation,
                null,
                "Fays Station",
                6,
                new ArrayList<>(Arrays.asList( Conditions.ORE_MODERATE,
                        Conditions.INDUSTRIAL_POLITY,
                        Conditions.VICE_DEMAND,
                        Conditions.POPULATION_6)),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.MEGAPORT,
                        Industries.HEAVYBATTERIES,
                        Industries.STARFORTRESS_MID,
                        Industries.FUELPROD,
                        Industries.REFINING
                )),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
                0.15f
        );
        faystation_market.addCondition(Conditions.HOT);
        faystation_market.addIndustry(Industries.ORBITALWORKS, new ArrayList<>(Arrays.asList(Items.CORRUPTED_NANOFORGE)));
        faystation_market.addIndustry(Industries.MILITARYBASE, new ArrayList<>(Arrays.asList(Items.CRYOARITHMETIC_ENGINE)));
        faystation_market.getIndustry(Industries.MILITARYBASE).setAICoreId(Commodities.GAMMA_CORE);
        faystation_market.getIndustry(Industries.STARFORTRESS_MID).setAICoreId(Commodities.GAMMA_CORE);


        addMarketplace.addMarketplace("faysco", polstein,
                null,
                "Polstein",
                5,
                new ArrayList<>(Arrays.asList(Conditions.LOW_GRAVITY,
                        Conditions.ORE_MODERATE,
                        Conditions.HABITABLE,
                        Conditions.FARMLAND_ADEQUATE,
                        Conditions.POLLUTION,
                        Conditions.ORGANICS_COMMON,
                        Conditions.POPULATION_5)),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.FARMING,
                        Industries.GROUNDDEFENSES,
                        Industries.MINING,
                        Industries.LIGHTINDUSTRY)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
                0.15f
        );

        addMarketplace.addMarketplace("pirates", tenskull,
                null,
                "Ten Skulls",
                4,
                new ArrayList<>(Arrays.asList( Conditions.ORGANIZED_CRIME,
                        Conditions.LOW_GRAVITY,
                        Conditions.ORE_ABUNDANT,
                        Conditions.COLD,
                        Conditions.DARK,
                        Conditions.POPULATION_4)),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.GROUNDDEFENSES,
                        Industries.MINING,
                        Industries.ORBITALSTATION,
                        Industries.PATROLHQ)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
                0.15f
        );

        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(
                "fringe_jump",
                "Fringe Jump-point");

        jumpPoint1.setCircularOrbit(system.getEntityById("deixisStar"), 2, jumpFringeDist, 4000f);
        jumpPoint1.setStandardWormholeToHyperspaceVisual();

        system.addEntity(jumpPoint1);

        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(
                "qanat_jump",
                "Qanat Jump-point");

        jumpPoint2.setCircularOrbit(system.getEntityById("deixisStar"), 400, jumpQanatDist, 180f);
        jumpPoint2.setStandardWormholeToHyperspaceVisual();

        system.addEntity(jumpPoint2);

        StarSystemGenerator.addSystemwideNebula(system, StarAge.AVERAGE);

        system.autogenerateHyperspaceJumpPoints(true, false);

        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;

        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);
    }
 }