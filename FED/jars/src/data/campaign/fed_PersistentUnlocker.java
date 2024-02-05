
//Script obtained from THI with permission from Mesotronik

package data.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

public class fed_PersistentUnlocker extends BaseCampaignEventListener
{
    public static final String FILENAME = "fed_persistent_data";
    public static boolean knowDerelicts = false;
    public static boolean knowRemnants = false;

    static
    {
        loadData();
    }

    public static void loadData()
    {
        try
        {
            String input = Global.getSettings().readTextFileFromCommon(FILENAME);
            JSONObject json = new JSONObject(input);
            knowDerelicts = json.optBoolean("knowDerelicts", false);
            knowRemnants = json.optBoolean("knowRemnants", false);
        }
        catch (IOException | JSONException ex)
        {
            // Do nothing
        }
    }

    public static void saveData()
    {
        // Generate a string with JSON formatting
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\n\t\"knowDerelicts\":").append(knowDerelicts).append(",");
        sb.append("\n\t\"knowRemnants\":").append(knowRemnants).append(",");
        sb.append("\n}");

        // Write the file
        try
        {
            Global.getSettings().writeTextFileToCommon(FILENAME, sb.toString());
        }
        catch (IOException ex)
        {
            Global.getLogger(fed_PersistentUnlocker.class).warn("Failed to write common file: " + ex);
        }
    }

    public fed_PersistentUnlocker()
    {
        super(false);
    }

    // Report if we met derelicts and/or Remnants
    @Override
    public void reportBattleFinished(CampaignFleetAPI primaryWinner, BattleAPI battle)
    {
        if (!battle.isPlayerInvolved())
        {
            return;
        }

        boolean wantUpdate = false;
        for (CampaignFleetAPI fleet : battle.getBothSides())
        {
            if (fleet.getFaction().getId().equals(Factions.DERELICT) && !knowDerelicts)
            {
                knowDerelicts = true;
                wantUpdate = true;
            }
            if (fleet.getFaction().getId().equals(Factions.REMNANTS) && !knowRemnants)
            {
                knowRemnants = true;
                wantUpdate = true;
            }
        }
        if (wantUpdate)
        {
            saveData();
        }
    }
}
