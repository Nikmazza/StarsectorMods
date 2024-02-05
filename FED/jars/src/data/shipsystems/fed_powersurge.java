package data.shipsystems;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;


public class fed_powersurge extends BaseShipSystemScript {

    public static final float ACCEL_BONUS = 100f;
    public static final float SPEED_BONUS_PERCENT = 150f;
    public static final float TURN_BONUS = 50f;
    public static final float TURN_ACCEL = 50f;
    public static final float TURN_BONUS_FLAT = 20f;
    public static final float TURN_ACCEL_FLAT = 30f;
    public static final float ROF_BONUS = 1.5f;
    public static final float BAL_FLUX_REDUCTION = 50f;
    public static final float REGEN_RATE = 1.5f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        float enginePower = ACCEL_BONUS * effectLevel;
        float speedBonusPercent = (SPEED_BONUS_PERCENT * effectLevel * 0.5f) + (SPEED_BONUS_PERCENT * 0.5f);

        float turnBonus = TURN_BONUS * effectLevel;
        float turnAccelBonus = TURN_ACCEL * effectLevel;

        float turnBonusFlat = TURN_BONUS_FLAT * effectLevel;
        float turnAccelerationFlat = TURN_ACCEL_FLAT * effectLevel;

        float mult = 1 + (ROF_BONUS * effectLevel);
        stats.getEnergyRoFMult().modifyMult(id, mult);
        stats.getEnergyWeaponFluxCostMod().modifyPercent(id, -BAL_FLUX_REDUCTION * effectLevel);
        stats.getBallisticRoFMult().modifyMult(id, mult);
        stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -BAL_FLUX_REDUCTION * effectLevel);
        stats.getAutofireAimAccuracy().modifyPercent(id, BAL_FLUX_REDUCTION);

        stats.getAcceleration().modifyFlat(id, enginePower);
        stats.getMaxSpeed().modifyPercent(id, speedBonusPercent);

        stats.getMaxTurnRate().modifyPercent(id, turnBonus);
        stats.getTurnAcceleration().modifyPercent(id, turnAccelBonus);

        stats.getMaxTurnRate().modifyFlat(id, turnBonusFlat);
        stats.getTurnAcceleration().modifyFlat(id, turnAccelerationFlat);
        stats.getDeceleration().modifyFlat(id, enginePower / 2f);
        stats.getEnergyAmmoRegenMult().modifyFlat(id, mult);
        stats.getBallisticAmmoRegenMult().modifyFlat(id, mult);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id
    ) {
        
        stats.getAutofireAimAccuracy().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getDeceleration().unmodify(id);
        stats.getBallisticRoFMult().unmodify(id);
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
        stats.getEnergyAmmoRegenMult().unmodify(id);
        stats.getBallisticAmmoRegenMult().unmodify(id); 
    }

    @Override
    public StatusData getStatusData(int index, State state,
             float effectLevel
    ) {
        float speedBonusFlat = SPEED_BONUS_PERCENT * effectLevel;
        float mult = 100 * (ROF_BONUS * effectLevel);
        float fluxReduction = (BAL_FLUX_REDUCTION * effectLevel);
        if (index == 0) {
            return new StatusData("engine power increased +" + (int) speedBonusFlat + "su", false);
        }
        if (index == 1) {
            return new StatusData("rate of fire +" + (int) mult + "%", false);
        }
        if (index == 2) {
            return new StatusData(" flux use -" + (int) fluxReduction + "%", false);
        }
        return null;
    }
}
