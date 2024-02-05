package data.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class fed_asb extends BaseShipSystemScript {

    public static final float ROF_BONUS = 6f;
    public static final float RANGE_BONUS = 1.5f;
    public static final float FLUX_REDUCTION = 50f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        float mult = 1f + RANGE_BONUS * effectLevel;
        stats.getEnergyWeaponRangeBonus().modifyMult(id, mult);
        stats.getEnergyRoFMult().modifyMult(id, ROF_BONUS);
        stats.getEnergyWeaponFluxCostMod().modifyPercent(id, -FLUX_REDUCTION);
    }
    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getEnergyRoFMult().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getEnergyWeaponRangeBonus().unmodify(id);
    }
    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        float mult = 1f + ROF_BONUS * effectLevel;
        float bonusPercent = (int) ((mult - 1f) * 100f);
        if (index == 0) {
            return new StatusData("ASB range and firerate +" + (int) bonusPercent + "%", false);
        }
        if (index == 1) {
            return new StatusData("ASB flux use -" + (int) FLUX_REDUCTION + "%", false);
        }
        return null;
    }
}
