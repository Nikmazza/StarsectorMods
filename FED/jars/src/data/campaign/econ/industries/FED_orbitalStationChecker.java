
//Script obtained from Shadowyards Heavy Industries with permissions from MShadowy

package data.campaign.econ.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

public class FED_orbitalStationChecker extends OrbitalStation {
    
    @Override
    public boolean isAvailableToBuild() {
        SectorAPI sector = Global.getSector();
        
        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI shadow = sector.getFaction("star_federation");
        //while it may not be strictly necessary we want to be sure that the market exists
        boolean canBuild = market != null &&
                (player.getRelationshipLevel(shadow).isAtWorst(RepLevel.WELCOMING) ||
                    Global.getSector().getPlayerFaction().knowsIndustry(getId()));
        
        return canBuild;
    }

    @Override
    public String getUnavailableReason() {
        return "Station type unavailable.";
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }
    
    @Override
    protected void ensureStationEntityIsSetOrCreated() {
        if (stationEntity == null) {
            for (SectorEntityToken entity : market.getConnectedEntities()) {
                if (entity.hasTag(Tags.STATION) && !entity.hasTag("no_orbital_station")) {
                    stationEntity = entity;
                    usingExistingStation = true;
                    break;
                }
            }
        }

        if (stationEntity == null) {
            stationEntity = market.getContainingLocation().addCustomEntity(
                    null, market.getName() + " Station", Entities.STATION_BUILT_FROM_INDUSTRY, market.getFactionId());
            SectorEntityToken primary = market.getPrimaryEntity();
            float orbitRadius = primary.getRadius() + 150f;
            stationEntity.setCircularOrbitWithSpin(primary, (float) Math.random() * 360f, orbitRadius, orbitRadius / 10f, 5f, 5f);
            market.getConnectedEntities().add(stationEntity);
            stationEntity.setMarket(market);
        }
    }
}

