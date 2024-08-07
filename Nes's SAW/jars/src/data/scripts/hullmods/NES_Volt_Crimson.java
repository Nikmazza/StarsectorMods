package data.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;

import java.awt.*;

public class NES_Volt_Crimson extends NES_Volt_color_base_hullmod {

    private Color color = new Color(255,145,75,255);
    private Color color2 = new Color(100,100,100,25);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        //check for safety ovverides to prevent incorrect color blending
        if (!ship.getVariant().getHullMods().contains("safetyoverrides")) {
            ship.getEngineController().fadeToOtherColor(this, color, color2, 1, 1f);
        }
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
        return CRIMSON;
    }

    @Override
    protected String getAltSpriteSuffix() {
        return "_crimson";
    }

    @Override
    protected void updateDecoWeapons(ShipAPI ship) {

    }
}
