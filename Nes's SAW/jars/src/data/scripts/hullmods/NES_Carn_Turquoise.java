package data.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

import java.awt.*;

public class NES_Carn_Turquoise extends NES_Carn_color_base_hullmod {

    private Color color = new Color(255, 160,75,255);
    //private Color color = new Color(255,100,100,255); //neat pink

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;
/*
        if (ship.getShield() != null) {
            ship.getShield().setInnerColor(new Color(115, 255, 200, 75));
        }
            ship.setVentFringeColor(new Color(90, 255, 150, 255));
            ship.setVentCoreColor(new Color(15, 135, 255, 75));
*/
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
        return TURQUOISE;
    }

    @Override
    protected String getAltSpriteSuffix() {
        return "_turquoise";
    }

    @Override
    protected void updateDecoWeapons(ShipAPI ship) {

        for (WeaponAPI weapon : ship.getAllWeapons()) {
            int frame;

            switch (weapon.getId()) {
                case "nes_carnelian_deco_cover":
                    frame = 3;
                    break;
                default:
                    continue;
            }

            weapon.getAnimation().setFrame(frame);
        }

    }
}
