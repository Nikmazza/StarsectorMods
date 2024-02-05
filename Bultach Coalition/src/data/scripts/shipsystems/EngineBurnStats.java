package data.scripts.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class EngineBurnStats extends BaseShipSystemScript {
	public static final float SPEED = 150f;
	public static final float ACC = 80f;

	@Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		stats.getMaxSpeed().modifyFlat(id, SPEED * effectLevel);
		stats.getAcceleration().modifyPercent(id, 150f * effectLevel);
		stats.getDeceleration().modifyPercent(id, 150f * effectLevel);
		stats.getMaxTurnRate().modifyPercent(id, 150f * effectLevel);
		stats.getTurnAcceleration().modifyPercent(id, 150f * effectLevel);
	}

	@Override
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
	}

	@Override
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData("Increased speed and maneuverability", false);
		}
		return null;
	}

}

