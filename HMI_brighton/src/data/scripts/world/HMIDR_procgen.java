package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.scripts.world.systems.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static com.fs.starfarer.api.impl.campaign.ids.Tags.THEME_RUINS;
import static com.fs.starfarer.api.impl.campaign.ids.Tags.THEME_RUINS_MAIN;

@SuppressWarnings("unchecked")
    public class HMIDR_procgen implements SectorGeneratorPlugin {
    @Override
    public void generate(SectorAPI sector) {
        new HMI_manchester().generate(sector);
        new HMI_tabitha().generate(sector);

        FactionAPI domres = sector.getFaction("domres");

        List<FactionAPI> allFactions = sector.getAllFactions();

        for (FactionAPI curFaction : allFactions) {
            if (curFaction == domres || curFaction.isNeutralFaction()) {
                continue;
            }
            domres.setRelationship(curFaction.getId(), RepLevel.VENGEFUL);
        }
    }
}

