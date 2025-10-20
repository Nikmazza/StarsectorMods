package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SectorThemeGenerator;
import data.campaign.NES_DerelictSpawnScript;

public class NES_ModPlugin extends BaseModPlugin {
    //make selected portraits unique to the player
    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/portraits/nes_mysterios_stranger.png");
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/portraits/nes_Chalco VI.png");
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/portraits/nes_Orion.png");
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.MALE).remove("graphics/portraits/nes_legionesian_demoncore.png");
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/nes_luzaitis_sunderest.png");
        Global.getSector().getPlayerFaction().getPortraits(FullName.Gender.FEMALE).remove("graphics/portraits/nes_cille.png");
    }

    //spawning teaser derelicts
    @Override
    public void onApplicationLoad() {
        SectorThemeGenerator.generators.add(new NES_DerelictSpawnScript());
    }
}