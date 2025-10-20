package data.scripts;

import com.fs.starfarer.api.Global;
import data.scripts.world.ArcadiaAddIndustry;
import exerelin.campaign.SectorManager;

public class GMDA_ModPluginAlt {

    static void initArcadiaRefit() {
        new ArcadiaAddIndustry().generate(Global.getSector());
        if (GMDAModPlugin.isExerelin && !SectorManager.getCorvusMode()) {
            return;
        }
    }
}
