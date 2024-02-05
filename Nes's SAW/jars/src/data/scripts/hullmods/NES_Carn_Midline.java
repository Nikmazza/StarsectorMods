package data.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

import java.awt.*;

public class NES_Carn_Midline extends NES_Carn_color_base_hullmod {
    private Color color = new Color(100,165,255,255);

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        if (ship.getFluxTracker().isEngineBoostActive()) {
            float nyoom = ship.getVelocity().length() / ship.getMaxSpeed();
            ship.getEngineController().fadeToOtherColor(this, color, null, nyoom, 1f);
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
        return MIDLINE;
    }

    @Override
    protected String getAltSpriteSuffix() {
        return null;
    }

    @Override
    protected void updateDecoWeapons(ShipAPI ship) {

        for (WeaponAPI weapon : ship.getAllWeapons()) {
            int frame;

            switch (weapon.getId()) {
                case "nes_carnelian_deco_cover":
                    frame = 0;
                    break;
                default:
                    continue;
            }

            weapon.getAnimation().setFrame(frame);
        }

    }
}
