package data.scripts.world.systems;

import com.fs.starfarer.api.EveryFrameScript;
import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import java.util.ArrayList;
import java.util.Arrays;
/*
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import data.scripts.world.XLU.addMarketplace;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import java.util.ArrayList;
import java.util.Arrays;
*/

public class yna_Home{

        public static MarketAPI addMarketplace(String factionID, SectorEntityToken primaryEntity,
                ArrayList<SectorEntityToken> connectedEntities, String name, int size,
                ArrayList<String> conditionList, ArrayList<ArrayList<String>> industryList, ArrayList<String> submarkets,
                float tarrif, boolean freePort) {
            EconomyAPI globalEconomy = Global.getSector().getEconomy();
            String planetID = primaryEntity.getId();
            String marketID = planetID/* + "_market"*/;

            MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
            newMarket.setFactionId(factionID);
            newMarket.setPrimaryEntity(primaryEntity);
            newMarket.getTariff().modifyFlat("generator", tarrif);
            newMarket.getLocationInHyperspace().set(primaryEntity.getLocationInHyperspace());

            if (null != submarkets) {
                for (String market : submarkets) {
                    newMarket.addSubmarket(market);
                }
            }

            for (String condition : conditionList) {
                newMarket.addCondition(condition);
            }

            for (ArrayList<String> industryWithParam : industryList) {
                String industry = industryWithParam.get(0);
                if (industryWithParam.size() == 1) {
                    newMarket.addIndustry(industry);
                } else {
                    newMarket.addIndustry(industry, industryWithParam.subList(1, industryWithParam.size()));
                }
            }

            if (null != connectedEntities) {
                for (SectorEntityToken entity : connectedEntities) {
                    newMarket.getConnectedEntities().add(entity);
                }
            }

            newMarket.setFreePort(freePort);
            globalEconomy.addMarket(newMarket, true);
            primaryEntity.setMarket(newMarket);
            primaryEntity.setFaction(factionID);

            if (null != connectedEntities) {
                for (SectorEntityToken entity : connectedEntities) {
                    entity.setMarket(newMarket);
                    entity.setFaction(factionID);
                }
            }

            return newMarket;
        }

	public void generate(SectorAPI sector) {
		
		final StarSystemAPI system = sector.createStarSystem("Ynadar Prime");
                system.getLocation().set(26000, -6500);
		LocationAPI hyper = Global.getSector().getHyperspace();
		
		system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");
		
		// create the star and generate the hyperspace anchor for this system
		PlanetAPI star = system.initStar("yna_home",
				"star_yellow", // id in planets.json
				700f,
				320, // extent of corona outside star
				8f, // solar wind burn level
				1f, // flare probability
				3f); // CR loss multiplier, good values are in the range of 1-5
		
		system.setLightColor(new Color(210, 225, 200)); // light color in entire system, affects all entities
		
		
		/* The asteroid belt - some notable large ones? */ 
		system.addAsteroidBelt(star, 100, 3500, 1200, 150, 250);
		
		/*
		 * addPlanet() parameters:
		 * 1. What the planet orbits (orbit is always circular)
		 * 2. Name
		 * 3. Planet type id in planets.json
		 * 4. Starting angle in orbit, i.e. 0 = to the right of the star
		 * 5. Planet radius, pixels at default zoom
		 * 6. Orbit radius, pixels at default zoom
		 * 7. Days it takes to complete an orbit. 1 day = 10 seconds.
		 */
		
		// Or: Grimnir   / The Einherjar / Heidrun / Eikthyrnir ?
		PlanetAPI ynadar1 = system.addPlanet("ynadar1", star, "Kalor", "barren_venuslike", 210, 120, 4200, 156);
		ynadar1.setCustomDescriptionId("yna_planet_kalor");
		
        MarketAPI KalorMarket = addMarketplace("ynadar", ynadar1,
                null,
                "Kalor", 4, // 2 industry limit
                new ArrayList<>(Arrays.asList(
                        Conditions.THIN_ATMOSPHERE,
                        Conditions.HOT, 
                        Conditions.ORGANICS_TRACE,
                        Conditions.ORE_MODERATE,
                        Conditions.RARE_ORE_RICH,
                        Conditions.POPULATION_4)),
                new ArrayList<>(Arrays.asList(
                        new ArrayList<>(Arrays.asList(Industries.ORBITALSTATION_MID)),
                        new ArrayList<>(Arrays.asList(Industries.PATROLHQ, Commodities.GAMMA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.MINING, Commodities.ALPHA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.REFINING, Commodities.ALPHA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.POPULATION, Commodities.GAMMA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.HEAVYBATTERIES, Commodities.GAMMA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.SPACEPORT)))),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE, Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN)),
                0.3f,
                false
        );

		PlanetAPI ynadar2 = system.addPlanet("ynadar2", star, "Skulato", "toxic", 210, 120, 5600, 220);
		ynadar2.setCustomDescriptionId("yna_planet_skulato");
		
		// Skulato Gate
		SectorEntityToken gate = system.addCustomEntity("skulato_gate", // unique id
				 "Skulato Gate", // name - if null, defaultName from custom_entities.json will be used
				 "inactive_gate", // type of object, defined in custom_entities.json
				 null); // faction
		gate.setCircularOrbit(system.getEntityById("ynadar2"), 0, 600, 350);

		SectorEntityToken skulato_buoy_loc = system.addCustomEntity(null,null, "nav_buoy_makeshift", "ynadar"); 
		skulato_buoy_loc.setCircularOrbitPointingDown(star, 125, 6200, 220);	
                
		PlanetAPI ynadar3 = system.addPlanet("ynadar3", star, "Jandor", "terran", 0, 220, 7500, 350);
		ynadar3.setCustomDescriptionId("yna_planet_jandor");
		
        SectorEntityToken jandor_station = system.addCustomEntity("jand_station1", "Jandor Station", "station_midline2", "ynadar");
        jandor_station.setCircularOrbitPointingDown(system.getEntityById("ynadar3"), 45, 340, 120);
        
        MarketAPI jandorMarket = addMarketplace("ynadar", ynadar3,
                new ArrayList<>(Arrays.asList(jandor_station)),
                "Jandor", 7, // 4 industry limit
                new ArrayList<>(Arrays.asList(
                        Conditions.TERRAN,
                        Conditions.HABITABLE,
                        Conditions.RUINS_EXTENSIVE,
                        Conditions.ORGANICS_TRACE,
                        Conditions.FARMLAND_POOR,
                        Conditions.ORE_MODERATE,
                        Conditions.RARE_ORE_SPARSE,
                        Conditions.POPULATION_7)),
                new ArrayList<>(Arrays.asList(
                        new ArrayList<>(Arrays.asList(Industries.STARFORTRESS_MID, Commodities.ALPHA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.ORBITALWORKS, Items.PRISTINE_NANOFORGE, Commodities.BETA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.HEAVYBATTERIES, Commodities.ALPHA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.HIGHCOMMAND, Commodities.ALPHA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.FARMING, Commodities.ALPHA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.REFINING, Commodities.GAMMA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.POPULATION, Commodities.GAMMA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.SPACEPORT)))),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE, Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.GENERIC_MILITARY)),
                0.3f,
                false
        );
                
		JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("jandor_gate", "Jandor Gate");
		OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 15, 8700, 350);
		jumpPoint.setOrbit(orbit);
		jumpPoint.setRelatedPlanet(ynadar3);
		jumpPoint.setStandardWormholeToHyperspaceVisual();
		system.addEntity(jumpPoint);
		
		/*
		 * addRingBand() parameters:
		 * 1. What it orbits
		 * 2. Category under "graphics" in settings.json
		 * 3. Key in category
		 * 4. Width of band within the texture
		 * 5. Index of band
		 * 6. Color to apply to band
		 * 7. Width of band (in the game)
		 * 8. Orbit radius (of the middle of the band)
		 * 9. Orbital period, in days
		 */
                
		PlanetAPI ynadar4 = system.addPlanet("ynadar4", star, "Hekosi", "tundra", 0, 220, 11500, 450);
		ynadar4.setCustomDescriptionId("yna_planet_hekosi");
                
		SectorEntityToken hekosi_sat_loc = system.addCustomEntity(null,null, "sensor_array", "ynadar"); 
		hekosi_sat_loc.setCircularOrbitPointingDown(star, 0, 10800, 450);		
                
        MarketAPI HekosiMarket = addMarketplace("ynadar", ynadar4,
                null,
                "Hekosi", 5, // 3 industry limit
                new ArrayList<>(Arrays.asList(
                        Conditions.COLD,
                        Conditions.HABITABLE,
                        Conditions.MILD_CLIMATE,
                        Conditions.RUINS_SCATTERED,
                        Conditions.VOLATILES_ABUNDANT,
                        Conditions.ORE_RICH,
                        Conditions.RARE_ORE_SPARSE,
                        Conditions.POPULATION_5)),
                new ArrayList<>(Arrays.asList(
                        new ArrayList<>(Arrays.asList(Industries.POPULATION)),
                        new ArrayList<>(Arrays.asList(Industries.SPACEPORT)),
                        new ArrayList<>(Arrays.asList(Industries.GROUNDDEFENSES, Commodities.GAMMA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.MINING, Commodities.BETA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.FUELPROD, Items.SYNCHROTRON, Commodities.ALPHA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.LIGHTINDUSTRY, Commodities.BETA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.PATROLHQ)))),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE, Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN)),
                0.3f,
                false
        );
                
		SectorEntityToken miran_sat_loc = system.addCustomEntity(null,null, "comm_relay_makeshift", "ynadar"); 
		miran_sat_loc.setCircularOrbitPointingDown(star, 0, 12200, 650);		
                
		PlanetAPI ynadar5 = system.addPlanet("ynadar5", star, "Miran", "ice_giant", 0, 700, 16500, 650);
                
		PlanetAPI ynadar5a = system.addPlanet("ynadar5a", ynadar5, "Miran A", "tundra", 0, 75, 900, 80);
		ynadar5a.setCustomDescriptionId("yna_planet_miran_a");
                
        MarketAPI MiranMarket = addMarketplace("independent", ynadar5a,
                null,
                "Miran A", 5, // 3 industry limit
                new ArrayList<>(Arrays.asList(
                        Conditions.THIN_ATMOSPHERE, 
                        Conditions.RUINS_SCATTERED,
                        Conditions.VOLATILES_PLENTIFUL,
                        Conditions.ORE_SPARSE,
                        Conditions.RARE_ORE_RICH,
                        Conditions.FREE_PORT,
                        Conditions.POPULATION_5)),
                new ArrayList<>(Arrays.asList(
                        new ArrayList<>(Arrays.asList(Industries.POPULATION)),
                        new ArrayList<>(Arrays.asList(Industries.SPACEPORT)),
                        new ArrayList<>(Arrays.asList(Industries.MINING)),
                        new ArrayList<>(Arrays.asList(Industries.FUELPROD, Items.SYNCHROTRON, Commodities.BETA_CORE)),
                        //new ArrayList<>(Arrays.asList(Industries.COMMERCE, Commodities.GAMMA_CORE)),
                        new ArrayList<>(Arrays.asList(Industries.MILITARYBASE, Commodities.GAMMA_CORE)))),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE, Submarkets.SUBMARKET_BLACK, Submarkets.SUBMARKET_OPEN, Submarkets.GENERIC_MILITARY)),
                0.3f,
                false
        );
		SectorEntityToken miran_mirror1 = system.addCustomEntity("miran_mirror1", "Miran Stellar Mirror", "stellar_mirror", "independent");
		miran_mirror1.setCircularOrbitPointingDown(system.getEntityById("ynadar5a"), 0, 120, 80);		
		miran_mirror1.setCustomDescriptionId("stellar_mirror");
				
		SectorEntityToken miran_mirror2 = system.addCustomEntity("miran_mirror2", "Miran Stellar Mirror", "stellar_mirror", "independent");
		miran_mirror2.setCircularOrbitPointingDown(system.getEntityById("ynadar5a"), 180, 120, 80);		
		miran_mirror2.setCustomDescriptionId("stellar_mirror");

		PlanetAPI ynadar5b = system.addPlanet("ynadar5b", ynadar5, "Miran B", "frozen", 0, 65, 1300, 70);
		PlanetAPI ynadar5c = system.addPlanet("ynadar5c", ynadar5, "Miran C", "frozen", 0, 50, 1600, 20);
		PlanetAPI ynadar5d = system.addPlanet("ynadar5d", ynadar5, "Miran D", "frozen", 0, 30, 1800, 100);
		
		system.autogenerateHyperspaceJumpPoints(true, true);
		StarSystemGenerator.addSystemwideNebula(system, StarAge.OLD);
	}
	
    public static class Demilitarize implements EveryFrameScript {

        private final MarketAPI market;

        Demilitarize(MarketAPI market) {
            this.market = market;
        }

        @Override
        public void advance(float amount) {
            if (market.hasSubmarket(Submarkets.GENERIC_MILITARY)) {
                market.removeSubmarket(Submarkets.GENERIC_MILITARY);
            }
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean runWhilePaused() {
            return false;
        }
    }
}
