package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicIncompatibleHullmods;

import java.util.*;

public class NES_AafArray extends BaseHullMod {

    private static final float ROF_MOD = 50f; //firerate bonus, 50f is half of AAF boost (also affects ammo regen)
    private static final float FLUX_MOD = 33; //flux cost reduction to balance out rof and keep ui readouts consistent
    private static final float ZERO_FLUX_BOOST = 75; // zero flux speed flat boost
    private static final float RANGE_THRESHOLD = 700f; //range cutoff limit
    private static final float RANGE_MULT = 0.25f; //range reduction multiplier above limit, same as SO for consistecy
    private static final float VENT_RATE_BONUS = 25f;//vent speed bonus %

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>(2);
    static {
        BLOCKED_HULLMODS.add("safetyoverrides");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                        ship.getVariant(),
                        tmp,
                        "NES_AafArray"
                );
            }
        }
    }


    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        stats.getBallisticRoFMult().modifyPercent(id, ROF_MOD);
        stats.getEnergyRoFMult().modifyPercent(id, ROF_MOD);

        stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1 - FLUX_MOD / 100);
        stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1 - FLUX_MOD / 100);

        stats.getBeamWeaponDamageMult().modifyMult(id, 1 + ROF_MOD / 1000 ); //balancing out rof effect on burst beam damage

        stats.getZeroFluxSpeedBoost().modifyFlat(id, ZERO_FLUX_BOOST);

        stats.getWeaponRangeThreshold().modifyFlat(id, RANGE_THRESHOLD);
        stats.getWeaponRangeMultPastThreshold().modifyMult(id, RANGE_MULT);

        stats.getDynamic().getStat("AMMO_REGEN").modifyMult("NES_AafArray", 1 + ROF_MOD * 0.01f);

        stats.getVentRateMult().modifyPercent(id, VENT_RATE_BONUS);

    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;
        if (ship.getFullTimeDeployed() >= 0.5f) return;

        Map<String, Object> customCombatData = Global.getCombatEngine().getCustomData();
        String id = ship.getId();

        if (customCombatData.get("NES_AafArray" + id) instanceof Boolean) return;

        MutableShipStatsAPI stats = ship.getMutableStats();

        for (WeaponAPI w : ship.getAllWeapons()) {
            float BASE_AMMO_REGEN = w.getSpec().getAmmoPerSecond();
            float ammoRegen = 1;
            if (w.getType() == WeaponAPI.WeaponType.BALLISTIC || w.getType() == WeaponAPI.WeaponType.ENERGY && w.usesAmmo() && BASE_AMMO_REGEN > 0){
                ammoRegen *= stats.getDynamic().getStat("AMMO_REGEN").getModifiedValue();
                w.getAmmoTracker().setAmmoPerSecond(w.getAmmoTracker().getAmmoPerSecond() * ammoRegen);
            }
        }

        customCombatData.put("NES_AafArray" + id, true);
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return Math.round(ROF_MOD) + "%"; //rof bonus
        if (index == 1) return Math.round(FLUX_MOD) + "%"; //flux cost
        if (index == 2) return "" + Math.round(ZERO_FLUX_BOOST); //flux boost
        if (index == 3) return "" + (int) VENT_RATE_BONUS + "%";
        if (index == 4) return Misc.getRoundedValue(RANGE_THRESHOLD); //range cutoff
        if (index == 5) return Global.getSettings().getHullModSpec("safetyoverrides").getDisplayName();
        return null;
    }
}


