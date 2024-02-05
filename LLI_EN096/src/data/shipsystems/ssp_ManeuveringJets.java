package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.SSPI18nUtil;

public class ssp_ManeuveringJets extends BaseShipSystemScript {
	protected float LONGER_RANGE=1f;
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		if(stats.getVariant().hasHullMod("ssp_LongerRange")){LONGER_RANGE=2f;}

		stats.getMaxSpeed().modifyFlat(id, 50f* effectLevel*LONGER_RANGE);
		stats.getAcceleration().modifyPercent(id, 200f * effectLevel*LONGER_RANGE);
		stats.getDeceleration().modifyPercent(id, 200f * effectLevel*LONGER_RANGE);
		stats.getTurnAcceleration().modifyFlat(id, 30f * effectLevel*LONGER_RANGE);
		stats.getTurnAcceleration().modifyPercent(id, 200f * effectLevel*LONGER_RANGE);
		stats.getMaxTurnRate().modifyFlat(id, 15f* effectLevel*LONGER_RANGE);
		stats.getMaxTurnRate().modifyPercent(id, 100f* effectLevel*LONGER_RANGE);
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
	}
	@Override
	public float getInOverride(ShipAPI ship) {
		if (ship != null && ship.getVariant().hasHullMod("ssp_ShortRange")) {
			return 0.25f;
		}
		if (ship != null && ship.getVariant().hasHullMod("ssp_LongerRange")) {
			return 2;
		}
		return -1;
	}

	@Override
	public float getActiveOverride(ShipAPI ship) {
		if (ship != null && ship.getVariant().hasHullMod("ssp_ShortRange")) {
			return 6f;
		}
		if (ship != null && ship.getVariant().hasHullMod("ssp_LongerRange")) {
			return 1;
		}
		return -1;
	}
	@Override
	public float getOutOverride(ShipAPI ship) {
		if (ship != null && ship.getVariant().hasHullMod("ssp_ShortRange")) {
			return 0.25f;
		}
		if (ship != null && ship.getVariant().hasHullMod("ssp_LongerRange")) {
			return 2;
		}
		return -1;
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_ManeuveringJets0"), false);
		} else if (index == 1) {
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_ManeuveringJets1")+(int)(50*effectLevel*LONGER_RANGE), false);
		}
		return null;
	}
}
