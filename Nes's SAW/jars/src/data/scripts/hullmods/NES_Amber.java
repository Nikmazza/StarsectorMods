package data.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.*;

public class NES_Amber extends NES_Fluorspar_color_base_hullmod {

    private Color color = new Color(255, 190, 115,255);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        if (ship.getShield() != null) {
            ship.getShield().setInnerColor(new Color(255, 190, 115, 75));
        }
            ship.setVentFringeColor(new Color(240, 150, 50, 255));
            ship.setVentCoreColor(new Color(255, 80, 10, 125));

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
        return AMBER;
    }

    @Override
    protected String getAltSpriteSuffix() {
        return "_amber";
    }

    @Override
    protected void updateDecoWeapons(ShipAPI ship) {

    }
}
