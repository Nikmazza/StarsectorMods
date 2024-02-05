package birdwanderer.birdscollection.data.scripts.world.systems;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import java.util.ArrayList;
import java.util.Arrays;


public class bc_zagan {

    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.getStarSystem("Zagan");

        SectorEntityToken waterloo = system.addCustomEntity("waterloo", "Port Waterloo", "station_side03",
                "faysco");
        waterloo.setCircularOrbitPointingDown(system.getEntityById("geburah"), 0, 250, 100);
        waterloo.setInteractionImage("illustrations", "hound_hangar");
        waterloo.setCustomDescriptionId("waterloo");

        MarketAPI waterloo_market = addMarketplace.addMarketplace("faysco", waterloo,
                null,
                "Port Waterloo",
                4,
                new ArrayList<>(Arrays.asList(
                        Conditions.POPULATION_4,
                        Conditions.OUTPOST,
                        Conditions.FREE_PORT)),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.HEAVYBATTERIES,
                        Industries.WAYSTATION,
                        Industries.LIGHTINDUSTRY,
                        Industries.ORBITALSTATION,
                        Industries.PATROLHQ)),
                new ArrayList<>(Arrays.asList(Submarkets.SUBMARKET_STORAGE,
                        Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_OPEN)),
                0.15f
        );
    }
}
