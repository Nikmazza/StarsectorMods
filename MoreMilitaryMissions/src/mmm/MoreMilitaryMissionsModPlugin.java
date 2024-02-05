package mmm;

import java.text.MessageFormat;
import java.util.*;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarData;
import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;
import com.fs.starfarer.api.util.Misc;
import mmm.missions.DefenseMission;
import mmm.missions.MmmHubMissionBarEventWrapper;
import mmm.missions.RepairMission;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicSettings;

public class MoreMilitaryMissionsModPlugin extends BaseModPlugin {
    private static final Logger log = Global.getLogger(MoreMilitaryMissionsModPlugin.class);
    static {
        if (MagicSettings.getBoolean("MoreMilitaryMissions", "MmmDebug")) {
            log.setLevel(Level.ALL);
        }
    }

    // Note that BarCMD saves list of bar events that was shown for 20-40 days, so if it fails to create a mission for
    // the event once, then the mission won't be called again for up to 20-40 days. To fix that we inject the ID back
    // in with a CampaignEventListener everytime the market is visited. Additionally,
    // BarEventManager.notifyWasInteractedWith removes accepted event from PortsideBarData, so we have to add that back
    // as well.
    class MissionInjector extends BaseCampaignEventListener {
        MissionInjector() {
            super(false);  // Don't add permanently
        }

        @Override
        public void reportPlayerOpenedMarket(MarketAPI market) {
            // BarCMD calls BarEventManager.notifyWasInteractedWith, which removes the event from PortsideBarData.
            // So we need to add them back here.
            PortsideBarData data = PortsideBarData.getInstance();
            List<String> expected_event_ids = Arrays.asList(DefenseMission.MISSION_ID, RepairMission.MISSION_ID);
            ArrayList<String> missing_event_ids = new ArrayList<>(expected_event_ids);
            for (PortsideBarEvent event : data.getEvents()) {
                missing_event_ids.remove(event.getBarEventId());
            }

            for (String event_id : missing_event_ids) {
                log.debug("Added " + event_id + " to PortsideBarData in " + market.getName());
                data.addEvent(new MmmHubMissionBarEventWrapper(event_id));
            }

            // Ensure the mission id is in $BarCMD_shownEvents if the key exists.
            final String KEY = "$BarCMD_shownEvents";
            MemoryAPI memory = market.getMemoryWithoutUpdate();
            if (memory.contains(KEY)) {
                List<String> stored_event_ids = (List<String>) memory.get(KEY);
                missing_event_ids = new ArrayList<>(expected_event_ids);
                missing_event_ids.removeAll(stored_event_ids);
                stored_event_ids.addAll(missing_event_ids);
                for (String event_id : missing_event_ids) {
                    log.debug("Added " + event_id + " to $BarCMD_shownEvents in " + market.getName());
                }
            }
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        log.info("onGameLoad called");

        Global.getSector().addTransientListener(new MissionInjector());

        if (log.getLevel() != Level.ALL) return;

        for (FactionAPI faction : Global.getSector().getAllFactions()) {
            log.debug(MessageFormat.format(
                    "Faction {0} (id={1}, intel={2}) has {3} ships,", faction.getDisplayName(), faction.getId(),
                    faction.isShowInIntelTab(), faction.getKnownShips().size()));
        }

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            // Fix for saves created before 0.1.1; ensure enemy fleets are stored using the neutral faction
            MemoryAPI memory = market.getMemoryWithoutUpdate();
            DefenseMission.MissionData data = (DefenseMission.MissionData) memory.get(DefenseMission.MISSION_DATA_KEY);
            if (data != null) {
                for (CampaignFleetAPI fleet : data.fleets) {
                    if (!fleet.getFaction().getId().equals(Factions.NEUTRAL)) {
                        Vector2f loc = fleet.getLocation();
                        if (loc.getX() < -25999f && loc.getY() < -25999f) {
                            fleet.setFaction(Factions.NEUTRAL, true);
                            log.debug("Fixing faction for fleet targeting market " + market.getName());
                        }
                    }
                }
            }
            // End fix

            Boolean is_functional = null;
            Integer station_fp = null;
            Integer station_dp = null;
            Industry industry = Misc.getStationIndustry(market);
            boolean has_station = industry != null;
            String station_name = null;
            if (has_station) {
                is_functional = industry.isFunctional();
                CampaignFleetAPI station = Misc.getStationFleet(market);
                if (station != null) {
                    station_fp = DefenseMission.getFleetStrength(station);
                    for (FleetMemberAPI member : station.getFleetData().getMembersListCopy()) {
                        station_name = member.getVariant().getFullDesignationWithHullName();
                        station_dp = Math.round(member.getDeploymentPointsCost());
                    }
                }
            }

            log.debug(MessageFormat.format(
                    "{0} faction {1} market in {2}: has_station={3}, is_functional={4}, station_name={5}, " +
                            "station_fp={6}, station_dp={7}",
                    market.getFaction().getDisplayName(), market.getName(), market.getStarSystem().getName(),
                    has_station, is_functional, station_name, station_fp, station_dp));
        }
    }
}
