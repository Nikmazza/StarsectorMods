package data.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.*;

public class NES_Aquamarine extends NES_Fluorspar_color_base_hullmod {

    private Color color = new Color(120, 180, 255,255);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        if (ship.getShield() != null) {
            ship.getShield().setInnerColor(new Color(115, 225, 255, 75));
        }
            ship.setVentFringeColor(new Color(50, 190, 240, 255));
            ship.setVentCoreColor(new Color(210, 240, 255, 125));

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
        return AQUAMARINE;
    }

    @Override
    protected String getAltSpriteSuffix() {
        return "_aquamarine";
    }

    @Override
    protected void updateDecoWeapons(ShipAPI ship) {

    }
}
