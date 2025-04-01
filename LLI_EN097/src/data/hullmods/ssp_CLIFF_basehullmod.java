package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.SSPI18nUtil;

import java.awt.*;

public class ssp_CLIFF_basehullmod extends BaseHullMod {
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        boolean has = spec != null && ship.getVariant().hasHullMod(spec.getId());
        int num = getNum(ship);
        if (has) num--;
        int max = 2;
        if (num >= max || !ship.getVariant().hasHullMod("ssp_cliff")) {
            return false;
        }
        return super.isApplicableToShip(ship);
    }
    public String getUnapplicableReason(ShipAPI ship) {
        boolean has = spec != null && ship.getVariant().hasHullMod(spec.getId());
        int num = getNum(ship);
        if (has) num--;
        int max = 2;
        if (num >= max) {
            return SSPI18nUtil.getHullModString("Cliff_limited");
        }
        if (!ship.getVariant().hasHullMod("ssp_cliff") ) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }
    protected int getNum(ShipAPI ship) {
        //int num = (int) Math.round(ship.getMutableStats().getDynamic().getMod(NUM_LOGISTICS_MODS).computeEffective(0));
        int num = 0;
        for (String id : ship.getVariant().getHullMods()) {
            if (ship.getHullSpec().isBuiltInMod(id)) continue;
            if (ship.getVariant().getPermaMods().contains(id)) continue;

            HullModSpecAPI mod = Global.getSettings().getHullModSpec(id);
            if (mod.hasTag("ssp_CLIFF_basehullmod")) {
                num++;
            }
        }
        return num;
    }
}
