package mmm.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionBarEventWrapper;
import com.fs.starfarer.api.loading.PersonMissionSpec;

import java.util.Random;

// Same as HubMissionBarEventWrapper, but returns true for isAlwaysShow, does not lock itself on a market, and does not
// use BarEventSpec.
public class MmmHubMissionBarEventWrapper extends HubMissionBarEventWrapper {
    public MmmHubMissionBarEventWrapper(String specId) {
        // Note that we don't actually use super.spec
        super(specId);
    }

    // Adapted from HubMissionBarEventWrapper; does not check shownAt
    @Override
    public boolean shouldShowAtMarket(MarketAPI market) {
        abortMission();

        if (!specId.equals(specId)) return false;  // sanity check

        genRandom = new Random(seed + market.getId().hashCode() * 181783497276652981L);
        if (specId.equals(DefenseMission.MISSION_ID)) {
            mission = new DefenseMission();
        } else if (specId.equals(RepairMission.MISSION_ID)) {
            mission = new RepairMission();
        } else {
            return false;
        }
        mission.setMissionId(specId);
        mission.setGenRandom(genRandom);

        PersonMissionSpec spec = Global.getSettings().getMissionSpec(specId);
        if (spec != null && spec.getIcon() != null) {
            mission.setIconName(spec.getIcon());
        }

        return mission.shouldShowAtMarket(market);
    }

    @Override
    public boolean isAlwaysShow() {
        return true;
    }
}
