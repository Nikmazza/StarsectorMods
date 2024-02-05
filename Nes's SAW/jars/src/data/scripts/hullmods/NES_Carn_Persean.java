package data.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

public class NES_Carn_Persean extends NES_Carn_color_base_hullmod {
/*
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (ship.getShield() != null) {
            ship.getShield().setInnerColor(new Color(255, 190, 115, 75));
        }
            ship.setVentFringeColor(new Color(240, 150, 50, 255));
            ship.setVentCoreColor(new Color(255, 80, 10, 125));
    }
*/
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
        return PERSEAN;
    }

    @Override
    protected String getAltSpriteSuffix() {
        return "_persean";
    }

    @Override
    protected void updateDecoWeapons(ShipAPI ship) {

        for (WeaponAPI weapon : ship.getAllWeapons()) {
            int frame;

            switch (weapon.getId()) {
                case "nes_carnelian_deco_cover":
                    frame = 4;
                    break;
                default:
                    continue;
            }

            weapon.getAnimation().setFrame(frame);
        }

    }
}
