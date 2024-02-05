package data.scripts.hullmods;

//leaving this for save compatability, clean this up in save a breaking update sometime

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

import java.awt.*;
public class NES_ZeroFluxColor extends BaseHullMod {
    /*
    private Color color = new Color(100,165,255,255);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        if (ship.getFluxTracker().isEngineBoostActive()) {
            float nyoom = ship.getVelocity().length() / ship.getMaxSpeed();
            ship.getEngineController().fadeToOtherColor(this, color, null, nyoom, 1f);
            //ship.getEngineController().extendFlame(this, 0.1f, 0.1f, 0.1f);
        }
    }

     */
}
