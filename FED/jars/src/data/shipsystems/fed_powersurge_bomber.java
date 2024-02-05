package data.shipsystems;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;


public class fed_powersurge_bomber extends BaseShipSystemScript {

    public static final float ACCEL_BONUS = 50f;
    public static final float SPEED_BONUS_PERCENT = 100f;
    public static final float TURN_BONUS = 25f;
    public static final float TURN_ACCEL = 25f;
    public static final float TURN_BONUS_FLAT = 10f;
    public static final float TURN_ACCEL_FLAT = 10f;
    public static final float ROF_BONUS = 0.5f;
    public static final float BAL_FLUX_REDUCTION = 25f;
    public static final float REGEN_RATE = 0.25f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        float enginePower = ACCEL_BONUS * effectLevel;
        float speedBonusPercent = (SPEED_BONUS_PERCENT * effectLevel * 0.5f) + (SPEED_BONUS_PERCENT * 0.5f);

        float turnBonus = TURN_BONUS * effectLevel;
        float turnAccelBonus = TURN_ACCEL * effectLevel;

        float turnBonusFlat = TURN_BONUS_FLAT * effectLevel;
        float turnAccelerationFlat = TURN_ACCEL_FLAT * effectLevel;

        float mult = 1 + (ROF_BONUS * effectLevel);
        stats.getMissileRoFMult().modifyMult(id, mult + 0.5f);
        stats.getMissileWeaponFluxCostMod().modifyPercent(id, -BAL_FLUX_REDUCTION * effectLevel);
        stats.getMissileAccelerationBonus().modifyPercent(id, mult/5f * 100f);
        stats.getMissileGuidance().modifyPercent(id, -mult/2f);
        stats.getMissileTurnAccelerationBonus().modifyPercent(id, -mult/3f);
        stats.getMissileMaxTurnRateBonus().modifyPercent(id, -mult/5f);
        stats.getMissileMaxSpeedBonus().modifyPercent(id, mult/8f * 100f);
        stats.getMissileAmmoRegenMult().modifyPercent(id, mult/2f * 100f);
        stats.getBallisticRoFMult().modifyMult(id, mult);
        stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -BAL_FLUX_REDUCTION * effectLevel);
        stats.getBallisticAmmoRegenMult().modifyPercent(id, REGEN_RATE * 100f * effectLevel);
        stats.getEnergyRoFMult().modifyMult(id, mult);
        stats.getEnergyWeaponFluxCostMod().modifyPercent(id, -BAL_FLUX_REDUCTION * effectLevel);
        stats.getEnergyAmmoRegenMult().modifyPercent(id, REGEN_RATE * 100f * effectLevel);
        stats.getAutofireAimAccuracy().modifyPercent(id, BAL_FLUX_REDUCTION * effectLevel);

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
        stats.getMissileRoFMult().unmodify(id);
        stats.getMissileWeaponFluxCostMod().unmodify(id);
        stats.getMissileAccelerationBonus().unmodify(id);
        stats.getMissileGuidance().unmodify(id);
        stats.getMissileTurnAccelerationBonus().unmodify(id);
        stats.getMissileMaxTurnRateBonus().unmodify(id);
        stats.getMissileMaxSpeedBonus().unmodify(id);
        stats.getMissileAmmoRegenMult().unmodify(id);
        stats.getBallisticRoFMult().unmodify(id);
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getBallisticAmmoRegenMult().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getEnergyAmmoRegenMult().unmodify(id);
        stats.getAutofireAimAccuracy().unmodify(id);
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
            return new StatusData("flux use -" + (int) fluxReduction + "%", false);
        }
        if (index == 3) {
            return new StatusData("missile performance enhanced", false);
        }
        return null;
    }
}
