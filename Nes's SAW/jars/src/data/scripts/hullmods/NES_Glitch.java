package data.scripts.hullmods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.*;

public class NES_Glitch extends BaseHullMod {

    private Color color = new Color(255, 125, 150,255);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        if (ship.getShield() != null) {
            ship.getShield().setInnerColor(new Color(160, 115, 255, 75));
        }
        ship.setVentFringeColor(new Color(100, 50, 240, 255));
        ship.setVentCoreColor(new Color(255, 115, 130, 125));

        float nyoom = ship.getVelocity().length() / ship.getMaxSpeed();
        ship.getEngineController().fadeToOtherColor(this, color, null, nyoom, 1f);
    }
}