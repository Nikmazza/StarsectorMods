package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class HMI_Locomotive_engines_off extends BaseHullMod {

//Code Courtesy of Rarasek
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (ship == null || !ship.isAlive()) {
            return;
        }
        if (ship.isRenderEngines()) {
            ship.setRenderEngines(false);
        }
    }
}