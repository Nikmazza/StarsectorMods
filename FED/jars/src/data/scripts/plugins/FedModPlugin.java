/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.scripts.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
//import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
//import com.fs.starfarer.api.combat.MissileAIPlugin;
//import com.fs.starfarer.api.combat.MissileAPI;

import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.campaign.fed_DerelictSpawnScript;
import data.world.fed.FedGen;
import static data.world.fed.FedGen.initFactionRelationships;
import exerelin.campaign.SectorManager;
//import org.dark.shaders.light.LightData;
//import org.dark.shaders.util.ShaderLib;
//import org.dark.shaders.util.TextureData;

public class FedModPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() {
        {
            boolean hasLazyLib = Global.getSettings().getModManager().isModEnabled("lw_lazylib");
            if (!hasLazyLib) {
                throw new RuntimeException("Star Federation requires LazyLib!"
                        + "\nGet it at http://fractalsoftworks.com/forum/index.php?topic=5444");
            }
            /*boolean hasGraphicsLib = Global.getSettings().getModManager().isModEnabled("shaderLib");
            if (hasGraphicsLib) {
                ShaderLib.init();
                //LightData.readLightDataCSV("data/lights/brdy_light_data.csv");
                //TextureData.readTextureDataCSV("data/lights/brdy_texture_data.csv");
            }
            if (!hasGraphicsLib) {
                throw new RuntimeException("Star Federation requires GraphicsLib!"
                        + "\nGet it at http://fractalsoftworks.com/forum/index.php?topic=10982");
            }*/
            boolean hasMagicLib = Global.getSettings().getModManager().isModEnabled("MagicLib");
            if (!hasMagicLib) {
                throw new RuntimeException("The Star Federation requires MagicLib!" + "\nGet it at http://fractalsoftworks.com/forum/index.php?topic=13718");
            }
        }
    }

    @Override
    public void onNewGame() {
        initFed();
    }

    @Override
    public void onNewGameAfterProcGen() {
        //Spawning hidden things
        fed_DerelictSpawnScript.spawnDerelicts(Global.getSector());
    }

    public void onGameLoad(boolean newGame) {

        boolean hasStarFederation = SharedData.getData().getPersonBountyEventData().isParticipating("star_federation");

        if (!hasStarFederation) {
            initFed();
            
            FedGen.createInitialPeople();
            if (Global.getSector().getMemoryWithoutUpdate().contains("$fed_spawned_derelicts")) {
                fed_DerelictSpawnScript.spawnDerelicts(Global.getSector());
            }
        }
    }

    private static void initFed() {
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (haveNexerelin && !SectorManager.getManager().isCorvusMode()) {
                SharedData.getData().getPersonBountyEventData().addParticipatingFaction("star_federation");
            return;
        }
        new FedGen().generate(Global.getSector());
        
    }
}
