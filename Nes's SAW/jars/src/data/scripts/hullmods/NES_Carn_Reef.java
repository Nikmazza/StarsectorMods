package data.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

import java.awt.*;

public class NES_Carn_Reef extends NES_Carn_color_base_hullmod {

    private Color color = new Color(255, 150, 170, 255);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;
/*
        if (ship.getShield() != null) {
            ship.getShield().setInnerColor(new Color(255, 115, 125, 75));
        }
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
        return REEF;
    }

    @Override
    protected String getAltSpriteSuffix() {
        return "_reef";
    }

    @Override
    protected void updateDecoWeapons(ShipAPI ship) {

        for (WeaponAPI weapon : ship.getAllWeapons()) {
            int frame;

            switch (weapon.getId()) {
                case "nes_carnelian_deco_cover":
                    frame = 1;
                    break;
                default:
                    continue;
            }

            weapon.getAnimation().setFrame(frame);
        }

    }
}
