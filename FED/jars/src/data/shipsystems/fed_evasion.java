package data.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class fed_evasion extends BaseShipSystemScript {

        public static final float SHIELD_EFFICENCY = 50f;
        public static final float ACCEL_BONUS = 200f;
        public static final float SPEED_BONUS_FLAT = 100f;
        public static final float TURN_BONUS = 150f;
        public static final float TURN_ACCEL = 150f;
        public static final float TURN_BONUS_FLAT = 5f;
        public static final float TURN_ACCEL_FLAT = 10f;
        public static final float ROF_PENALTY_PERCENT = 50f;
	
        @Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		
                float shieldResist = SHIELD_EFFICENCY * effectLevel;
                
                float enginePower = ACCEL_BONUS * effectLevel;
                float speedBonusFlat = SPEED_BONUS_FLAT * effectLevel;
                
                float turnBonus = TURN_BONUS * effectLevel;
                float turnAccelBonus = TURN_ACCEL * effectLevel;
                
                
                float turnBonusFlat = TURN_BONUS_FLAT * effectLevel;
                float turnAccelerationFlat = TURN_ACCEL_FLAT * effectLevel;
                
                float fireRatePenalty = ROF_PENALTY_PERCENT * effectLevel;
                
                
                
                stats.getShieldDamageTakenMult().modifyPercent(id, -shieldResist);
                
                stats.getAcceleration().modifyFlat(id, enginePower);
                stats.getMaxSpeed().modifyFlat(id, speedBonusFlat);
                stats.getDeceleration().modifyFlat(id, enginePower/2f);
                
                stats.getMaxTurnRate().modifyPercent(id, turnBonus);
                stats.getTurnAcceleration().modifyPercent(id, turnAccelBonus);
                
                stats.getMaxTurnRate().modifyFlat(id, turnBonusFlat);
                stats.getTurnAcceleration().modifyFlat(id, turnAccelerationFlat);
                
                stats.getBallisticRoFMult().modifyPercent(id, -fireRatePenalty);
                stats.getEnergyRoFMult().modifyPercent(id, -fireRatePenalty);
                stats.getMissileRoFMult().modifyPercent(id, -fireRatePenalty);
	}
    
        @Override
	public void unapply(MutableShipStatsAPI stats, String id) {
                stats.getShieldAbsorptionMult().unmodify(id);
                stats.getAcceleration().unmodify(id);
                stats.getMaxSpeed().unmodify(id);
                stats.getTurnAcceleration().unmodify(id);
                stats.getMaxTurnRate().unmodify(id);
                stats.getBallisticRoFMult().unmodify(id);
                stats.getEnergyRoFMult().unmodify(id);
                stats.getMissileRoFMult().unmodify(id);
                stats.getDeceleration().unmodify(id);
	}
	
       
        @Override
	public StatusData getStatusData(int index, State state, float effectLevel) {
                float shieldResist = SHIELD_EFFICENCY * effectLevel;
                float speedBonus = SPEED_BONUS_FLAT * effectLevel;
                float fireRatePenalty = ROF_PENALTY_PERCENT * effectLevel;
		if (index == 0) {
			return new StatusData("shield damage taken -" + (int) shieldResist + "%", false);
		}
		if (index == 1) {
			return new StatusData("engine power increased +" + (int) speedBonus + "su", false);
		}
                if (index == 2) {
                        return new StatusData("weapons depowered -" + (int) fireRatePenalty + "% fire rate", false);
                }
		return null;
	}
}