package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class csp_overlordA extends BaseHullMod {
    public csp_overlordA() {
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        boolean haveGettag = Global.getSettings().getModManager().isModEnabled("armaa");
        if (haveGettag) {
            ship.getVariant().addPermaMod("armaa_skyMindAlpha");
        }

    }
}
