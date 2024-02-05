package data.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.*;

public class NES_RoseQuartz extends NES_Fluorspar_color_base_hullmod {

    private Color color = new Color(255, 150, 170, 255);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        if (ship.getShield() != null) {
            ship.getShield().setInnerColor(new Color(255, 115, 150, 75));
        }
            ship.setVentFringeColor(new Color(255, 150, 170, 255));
            ship.setVentCoreColor(new Color(195, 10, 255, 75));

            ship.getEngineController().fadeToOtherColor(this, color, null, 1, 1f);
    }

    @Override
    public int getDisplaySortOrder() {
        return 2000;
    }

    @Override
    public int getDisplayCategoryIndex() {
        return 2;
    }

    @Override
    protected String getHullModId() {
        return ROSEQUARTZ;
    }

    @Override
    protected String getAltSpriteSuffix() {
        return "_rosequartz";
    }

    @Override
    protected void updateDecoWeapons(ShipAPI ship) {

    }
}

