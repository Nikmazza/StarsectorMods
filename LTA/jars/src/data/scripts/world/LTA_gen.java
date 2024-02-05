package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.NascentGravityWellAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.util.Misc;
import static data.scripts.LTA_modPlugin.TECHDUINNINSYSTEM;
import data.scripts.world.systems.LTA_Magec;
import org.lwjgl.util.vector.Vector2f;

public class LTA_gen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
		
		        new LTA_Magec().generate(sector);
		
                        
                        if (TECHDUINNINSYSTEM) {
                StarSystemAPI Magec = sector.getStarSystem("Magec");
                if (Magec == null) return;
                SectorEntityToken LTA_Tech_Duinn_system = Magec.addCustomEntity("LTA_Tech_Duinn_system", "Tech Duinn", "LTA_Tech_Duinn_system", "pirates");
//	prismEntity.setCircularOrbitWithSpin(hyperspace.createToken(LTA_LOC), ExerelinUtilsAstro.getRandomAngle(rand), 150, 60, 30, 30);
		MarketAPI market = Global.getFactory().createMarket("LTA_Tech_Duinn_system" /*+ "_market"*/, "Tech Duinn", 7);
                LTA_Tech_Duinn_system.setDiscoverable(true);
                LTA_Tech_Duinn_system.setSensorProfile(2000f);
		market.setFactionId(Factions.PIRATES);
		market.addCondition(Conditions.POPULATION_7);
                String token = market.addCondition(Conditions.VOLATILES_DIFFUSE);
                market.getSpecificCondition(token).setSurveyed(true);
		//market.addCondition(Conditions.FREE_PORT);
		market.addCondition(Conditions.ORGANIZED_CRIME);
		market.addCondition(Conditions.POOR_LIGHT);
                market.addCondition(Conditions.NO_ATMOSPHERE);
                market.addCondition(Conditions.STEALTH_MINEFIELDS);
                market.addCondition(Conditions.DISSIDENT);
                market.addCondition(Conditions.URBANIZED_POLITY);
                //market.addCondition(Conditions.INDUSTRIAL_POLITY);
                market.addCondition(Conditions.REGIONAL_CAPITAL);

		market.addIndustry(Industries.POPULATION);
		//market.addIndustry("hydroponics");
		//market.addIndustry(Industries.LIGHTINDUSTRY);
		market.addIndustry(Industries.FUELPROD);
		//market.addIndustry(Industries.MILITARYBASE);
		market.addIndustry(Industries.HIGHCOMMAND);
		market.addIndustry(Industries.MEGAPORT);
		market.addIndustry(Industries.MINING);
		//market.addIndustry(Industries.REFINING);                
		market.addIndustry(Industries.ORBITALWORKS);
		market.addIndustry(Industries.WAYSTATION);
		market.addIndustry(Industries.HEAVYBATTERIES);
		//market.addIndustry(Industries.TAG_URBAN);
                //market.addIndustry(Industries.TAG_INDUSTRIAL);
		market.addIndustry(Industries.STARFORTRESS);	// Arrays.asList(new String[]{Commodities.ALPHA_CORE}));
		
		market.setFreePort(true);
		market.addSubmarket(Submarkets.SUBMARKET_OPEN);
		//market.addSubmarket(Submarkets.GENERIC_MILITARY);
		market.addSubmarket(Submarkets.SUBMARKET_BLACK);
		market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
		//MarketAPI.reapplyConditions(Conditions.VOLATILES_TRACE);
		market.getTariff().modifyFlat("generator", sector.getFaction(Factions.PIRATES).getTariffFraction());
		market.setPrimaryEntity(LTA_Tech_Duinn_system);
		LTA_Tech_Duinn_system.setMarket(market);
		LTA_Tech_Duinn_system.setFaction(Factions.PIRATES);
		market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);	// not doing this makes market condition tooltips fail to appear
		sector.getEconomy().addMarket(market, true);
                //Vector2f loc = new Vector2f(4000, -6000);  // I think this is roughly the center of magec
                StarSystemAPI MAGICMAGECFINDERBOOGALEE = Global.getSector().getStarSystem("Magec");
                LTA_Tech_Duinn_system.setCircularOrbitPointingDown(MAGICMAGECFINDERBOOGALEE.getEntityById("magec"), 5, 25000, 360); // angle, distance, time in days
                
                NascentGravityWellAPI well = Global.getSector().createNascentGravityWell(LTA_Tech_Duinn_system, 50f);
                Global.getSector().getHyperspace().addEntity(well);
                well.autoUpdateHyperLocationBasedOnInSystemEntityAtRadius(LTA_Tech_Duinn_system, 1800);
		//PlanetAPI UGW = Magec.addPlanet("UGW", Magec, "Gravity Signiture", "barren", 230, 100, 2675, 100);
		//Misc.initConditionMarket(UGW);
		//UGW.getMarket().addCondition(Conditions.DECIVILIZED);
		//UGW.getMarket().addCondition(Conditions.RUINS_EXTENSIVE);
		//UGW.getMarket().getFirstCondition(Conditions.RUINS_EXTENSIVE).setSurveyed(true);                
                
                
                        }
                        
                        else if (!TECHDUINNINSYSTEM)
                
                        {
                LocationAPI hyperspace = sector.getHyperspace();
                SectorEntityToken LTA_Tech_Duinn = hyperspace.addCustomEntity("LTA_Tech_Duinn", "Tech Duinn", "LTA_Tech_Duinn", "pirates");
//	prismEntity.setCircularOrbitWithSpin(hyperspace.createToken(LTA_LOC), ExerelinUtilsAstro.getRandomAngle(rand), 150, 60, 30, 30);
		MarketAPI market = Global.getFactory().createMarket("LTA_Tech_Duinn" /*+ "_market"*/, "Tech Duinn", 7);
                //LTA_Tech_Duinn.setDiscoverable(true);
                //LTA_Tech_Duinn.setSensorProfile(1000f);
		market.setFactionId(Factions.PIRATES);
		market.addCondition(Conditions.POPULATION_7);
                String token = market.addCondition(Conditions.VOLATILES_DIFFUSE);
                market.getSpecificCondition(token).setSurveyed(true);
		//market.addCondition(Conditions.FREE_PORT);
		market.addCondition(Conditions.ORGANIZED_CRIME);
		market.addCondition(Conditions.POOR_LIGHT);
                market.addCondition(Conditions.NO_ATMOSPHERE);
                market.addCondition(Conditions.STEALTH_MINEFIELDS);
                market.addCondition(Conditions.DISSIDENT);
                market.addCondition(Conditions.URBANIZED_POLITY);
                //market.addCondition(Conditions.INDUSTRIAL_POLITY);
                market.addCondition(Conditions.REGIONAL_CAPITAL);

		market.addIndustry(Industries.POPULATION);
		//market.addIndustry("hydroponics");
		//market.addIndustry(Industries.LIGHTINDUSTRY);
		market.addIndustry(Industries.FUELPROD);
		//market.addIndustry(Industries.MILITARYBASE);
		market.addIndustry(Industries.HIGHCOMMAND);
		market.addIndustry(Industries.MEGAPORT);
		market.addIndustry(Industries.MINING);
		//market.addIndustry(Industries.REFINING);                
		market.addIndustry(Industries.ORBITALWORKS);
		market.addIndustry(Industries.WAYSTATION);
		market.addIndustry(Industries.HEAVYBATTERIES);
		//market.addIndustry(Industries.TAG_URBAN);
                //market.addIndustry(Industries.TAG_INDUSTRIAL);
		market.addIndustry(Industries.STARFORTRESS);	// Arrays.asList(new String[]{Commodities.ALPHA_CORE}));
		
		market.setFreePort(true);
		market.addSubmarket(Submarkets.SUBMARKET_OPEN);
		//market.addSubmarket(Submarkets.GENERIC_MILITARY);
		market.addSubmarket(Submarkets.SUBMARKET_BLACK);
		market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
		//MarketAPI.reapplyConditions(Conditions.VOLATILES_TRACE);
		market.getTariff().modifyFlat("generator", sector.getFaction(Factions.PIRATES).getTariffFraction());
		market.setPrimaryEntity(LTA_Tech_Duinn);
		LTA_Tech_Duinn.setMarket(market);
		LTA_Tech_Duinn.setFaction(Factions.PIRATES);
		market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);	// not doing this makes market condition tooltips fail to appear
		sector.getEconomy().addMarket(market, true);
                Vector2f loc = new Vector2f(4000, -6000);  // I think this is roughly the center of the core worlds
                LTA_Tech_Duinn.setCircularOrbitPointingDown(hyperspace.createToken(loc), 0, 18000, 360); // angle, distance, time in days     
                        }
    }
}