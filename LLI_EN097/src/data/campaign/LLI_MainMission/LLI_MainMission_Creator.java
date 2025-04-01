package data.campaign.LLI_MainMission;

import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventCreator;

public class LLI_MainMission_Creator extends BaseBarEventCreator {
    public PortsideBarEvent createBarEvent() {
        return new LLI_MainMission_Part1();
    }

    @Override
    public float getBarEventAcceptedTimeoutDuration() {
        return 10000000000f; // effectively infinite
    }

    @Override
    public float getBarEventFrequencyWeight() {
        return 10f;
    }
}
