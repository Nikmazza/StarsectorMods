package data.scripts;
import data.scripts.world.LTA_gen;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

import org.json.JSONException;
import org.json.JSONObject;

import org.apache.log4j.Level;

import java.io.IOException;


public class LTA_modPlugin extends BaseModPlugin {

    public static boolean TECHDUINNINSYSTEM;
    
    ////////////////////////////////////////
    //                                    //
    //        ON NEW GAME CREATION        //
    //                                    //
    ////////////////////////////////////////
    
    @Override
    public void onNewGame() {
//        if (!Global.getSettings().getModManager().isModEnabled("nexerelin") || SectorManager.getCorvusMode()){
            new LTA_gen().generate(Global.getSector());
//        }        
    }
    
    public void onApplicationLoad() {
        
        try {
            loadSettings();
            
        } catch (IOException | JSONException e) {
            Global.getLogger(LTA_modPlugin.class).log(Level.ERROR, "Failed to load LTA_OPTIONS.json" + e.getMessage());
        }
    }
    
    private static void loadSettings() throws IOException, JSONException {
        JSONObject setting = Global.getSettings().loadJSON("LTA_OPTIONS.json");
        TECHDUINNINSYSTEM = setting.getBoolean("enable_TechDuinnIsInSystem");
    }
    
    ////////////////////////////////////////
    //                                    //
    //            ON GAME LOAD            //
    //                                    //
    ////////////////////////////////////////
    
    @Override
    public void onGameLoad(boolean newGame){
    }    
}
