package data.missions.fed_randomVsFed;

import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import data.missions.BaseRandomFEDMissionDefinition;

public class MissionDefinition extends BaseRandomFEDMissionDefinition
{
    @Override
    public void defineMission(MissionDefinitionAPI api)
    {
        chooseFactions(null, "star_federation");
        super.defineMission(api);
    }
}