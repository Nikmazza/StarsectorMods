package data.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class fed_evasionlight extends BaseShipSystemScript {

        public static final float ACCEL_BONUS = 200f;
        public static final float SPEED_BONUS_FLAT = 100f;
        public static final float TURN_BONUS = 100f;
        public static final float TURN_ACCEL = 100f;
        public static final float TURN_BONUS_FLAT = 20f;
        public static final float TURN_ACCEL_FLAT = 30f;
	
        @Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		
                float enginePower = ACCEL_BONUS * effectLevel;
                float speedBonusFlat = SPEED_BONUS_FLAT * effectLevel;
                
                float turnBonus = TURN_BONUS * effectLevel;
                float turnAccelBonus = TURN_ACCEL * effectLevel;
                
                float turnBonusFlat = TURN_BONUS_FLAT * effectLevel;
                float turnAccelerationFlat = TURN_ACCEL_FLAT * effectLevel;
                

                
                
                stats.getAcceleration().modifyFlat(id, enginePower);
                stats.getMaxSpeed().modifyFlat(id, speedBonusFlat);
                
                stats.getMaxTurnRate().modifyPercent(id, turnBonus);
                stats.getTurnAcceleration().modifyPercent(id, turnAccelBonus);
                
                stats.getMaxTurnRate().modifyFlat(id, turnBonusFlat);
                stats.getTurnAcceleration().modifyFlat(id, turnAccelerationFlat);
                stats.getDeceleration().modifyFlat(id,enginePower/2f);
                
	}
    
        @Override
	public void unapply(MutableShipStatsAPI stats, String id) {
                stats.getAcceleration().unmodify(id);
                stats.getMaxSpeed().unmodify(id);
                stats.getTurnAcceleration().unmodify(id);
                stats.getMaxTurnRate().unmodify(id);
                stats.getDeceleration().unmodify(id);
	}
	
      
        @Override
	public StatusData getStatusData(int index, State state, float effectLevel) {
                float speedBonusFlat = SPEED_BONUS_FLAT * effectLevel;
		if (index == 0) {
			return new StatusData("engine power increased +" + (int) speedBonusFlat + "su", false);
		}
		return null;
	}
}