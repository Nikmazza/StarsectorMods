package data.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.*;

public class NES_Phosphophyllite extends NES_Fluorspar_color_base_hullmod {

/*  Stat tweaks per color can go here

    private static final float ZERO_FLUX_BOOST = 69;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getZeroFluxSpeedBoost().modifyFlat(id, ZERO_FLUX_BOOST);
    }

 */
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        if (ship.getShield() != null) {
            ship.getShield().setInnerColor(new Color(115, 255, 200, 75));
        }
            ship.setVentFringeColor(new Color(90, 255, 150, 255));
            ship.setVentCoreColor(new Color(15, 135, 255, 75));
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
        return PHOSPHOPHYLLITE;
    }

    @Override
    protected String getAltSpriteSuffix() {
        return null; //base sprite, no need to replace
    }

    @Override
    protected void updateDecoWeapons(ShipAPI ship) {

    }
}
