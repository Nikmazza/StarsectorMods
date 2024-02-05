package data.missions.fed_fedVsRandom;

import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import data.missions.BaseRandomFEDMissionDefinition;

public class MissionDefinition extends BaseRandomFEDMissionDefinition
{
    @Override
    public void defineMission(MissionDefinitionAPI api)
    {
        chooseFactions("star_federation", null);
        super.defineMission(api);
    }
}
