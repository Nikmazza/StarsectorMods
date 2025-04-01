package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.SSPI18nUtil;

public class ssp_SYS_c  extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getMod("ssp_cliff_Mult").modifyMult(id,2f);
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff")
                && !ship.getVariant().hasHullMod("ssp_SYS_p")
                && !ship.getVariant().hasHullMod("ssp_SYS_s");
    }
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("ssp_cliff") ) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }

}
