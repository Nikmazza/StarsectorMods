package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import data.SSPI18nUtil;
import org.magiclib.util.MagicIncompatibleHullmods;

public class ssp_modetransform extends BaseHullMod {

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        if (ship.getVariant().getHullMods().contains("advancedoptics")) {
            //if someone tries to install sussy hullmodsus, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "advancedoptics",
                    "ssp_modetransform"
            );
        }else if(ship.getVariant().getHullMods().contains("high_scatter_amp")) {
            //if someone tries to install sussy hullmodsus, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "high_scatter_amp",
                    "ssp_modetransform"
            );
        }
    }
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff")
                &&!ship.getVariant().hasHullMod("advancedoptics")
                &&!ship.getVariant().hasHullMod("high_scatter_amp");
    }
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("ssp_cliff")) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return Global.getSettings().getHullModSpec("advancedoptics").getDisplayName();
        }else if(index == 1){
            return Global.getSettings().getHullModSpec("high_scatter_amp").getDisplayName();
        }
        return null;
    }
}
