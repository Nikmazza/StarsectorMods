package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class hhe_z_ftr_helot_bonus extends BaseHullMod {

    public static final float HHE_HELOT_WEAPON_PROJ_SPEED_INCREASE = 35f;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getProjectileSpeedMult().modifyMult(id, 1 + HHE_HELOT_WEAPON_PROJ_SPEED_INCREASE * 0.01f);
    }
}
