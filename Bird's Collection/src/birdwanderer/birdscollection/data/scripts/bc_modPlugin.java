package birdwanderer.birdscollection.data.scripts;

import birdwanderer.birdscollection.data.scripts.world.bc_modGen;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import exerelin.campaign.SectorManager;
import lunalib.lunaSettings.LunaSettings;

public class bc_modPlugin extends BaseModPlugin {
    @Override
    public void onNewGame() {

        {
            boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
            boolean hasFaysco = Boolean.TRUE.equals(LunaSettings.getBoolean("bc", "bc_enable_faysco"));
            if (!haveNexerelin || SectorManager.getManager().isCorvusMode())
                if (hasFaysco) {
                    new bc_modGen().generate(Global.getSector());
                }

        }
    }
}
