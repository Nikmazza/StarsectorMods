package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.ConstellationGen;
import com.thoughtworks.xstream.XStream;
import data.campaign.fleets.HMIExecFleetManager;
import data.campaign.fleets.HMIScavFleetRouteManager;
import data.campaign.fleets.MessFleetManager;
import data.scripts.world.HMI_gen;
import data.scripts.world.HMI_procgen;
import data.scripts.world.HMI_lootmessget;
import exerelin.campaign.SectorManager;
import data.campaign.intel.missions.kurita.HMIQuestUtils;
import org.apache.log4j.Level;
import org.json.JSONException;
import org.json.JSONObject;
import exerelin.campaign.SectorManager;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;

import java.io.IOException;
import java.util.List;

import static com.fs.starfarer.api.impl.campaign.ids.Tags.*;

public class HMI_modPlugin extends BaseModPlugin {

    public static boolean haveNexerelin = false;
    public static boolean Module_HMI = true;

    static private boolean graphicsLibAvailable = false;
    static public boolean isGraphicsLibAvailable () {
        return graphicsLibAvailable;
    }

    public void configureXStream(XStream x) {
		x.alias("MessFleetManager", MessFleetManager.class);
        x.alias("HMI_lootmessget", HMI_lootmessget.class);
//        x.alias("HMIExecFleetManager", HMIExecFleetManager.class);
    }
	
    public static void syncHMIScripts() {
        if (!Global.getSector().hasScript(HMI_lootmessget.class)) {
            Global.getSector().addScript(new HMI_lootmessget());
        }
//        if (!Global.getSector().hasScript(HMIExecFleetManager.class)) {
//            Global.getSector().addScript(new HMIExecFleetManager());
//      }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        {
            syncHMIScripts();
            HMIQuestUtils.setupKuritaContactMissions();

            boolean hasGraphicsLib = Global.getSettings().getModManager().isModEnabled("shaderLib");
            if (hasGraphicsLib) {
                graphicsLibAvailable = true;
                ShaderLib.init();
                LightData.readLightDataCSV("data/lights/hmi_light_data.csv");
            } else {
                graphicsLibAvailable = false;
            }
        }
    }


    @Override
    public void onNewGame() {
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
            new HMI_gen().generate(Global.getSector());
        }
        new HMI_procgen().generate(Global.getSector());
		
    }

    public void onNewGameAfterEconomyLoad() {
        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            if (system.getTags().equals("THEME_RUINS") || system.getTags().equals("THEME_RUINS_MAIN") || system.getTags().equals("THEME_INTERESTING")) {
                HMIScavFleetRouteManager fleets = new HMIScavFleetRouteManager(system);
                system.addScript(fleets);
            }
        }
    }
}
	

