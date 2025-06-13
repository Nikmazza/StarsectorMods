package data.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;

import org.apache.log4j.Logger;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;

public class PMMMVEModPlugin extends BaseModPlugin {

    private static final Logger log = Global.getLogger(PMMMVEModPlugin.class);
    private static boolean hasGraphicsLib = false;

    public void onApplicationLoad() {
        // Handle shaders if the graphics library is enabled
        hasGraphicsLib = Global.getSettings().getModManager().isModEnabled("shaderLib");

        if (hasGraphicsLib) {
            ShaderLib.init();

            if (ShaderLib.areShadersAllowed() && ShaderLib.areBuffersAllowed()) {
                TextureData.readTextureDataCSV("data/config/pmm_texture_data.csv");
                log.info("PMMMVE shaders active");
            }
        }

        // Log the plugin initialization
        log.info("Welcome to PMMM! I'm MiniDeth3, and I'm in your logs now...");
    }
}
