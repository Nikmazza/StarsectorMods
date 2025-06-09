package data.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class NES_FluxGrid extends BaseHullMod {

	private static final float FLUX_MULT = 2f;
	public static float SHIELD_PERCENT = 40f;
	public static float UPKEEP_PERCENT = 100f;
	public static float SPEED_MULT = 0.75f;
	public static final float PROFILE_PERCENT = 50f;


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxDissipation().modifyMult(id, FLUX_MULT);
		stats.getFluxCapacity().modifyMult(id, FLUX_MULT);

		stats.getShieldDamageTakenMult().modifyPercent(id, SHIELD_PERCENT);
		stats.getShieldUpkeepMult().modifyPercent(id, UPKEEP_PERCENT);

		stats.getMaxSpeed().modifyMult(id, SPEED_MULT);
		stats.getAcceleration().modifyMult(id, SPEED_MULT);
		stats.getDeceleration().modifyMult(id, SPEED_MULT);
		//stats.getTurnAcceleration().modifyMult(id, SPEED_MULT);
		//stats.getMaxTurnRate().modifyMult(id, SPEED_MULT);

		stats.getSensorProfile().modifyPercent(id, PROFILE_PERCENT);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) FLUX_MULT + "";
		if (index == 1) return "" + (int) SHIELD_PERCENT + "%";
		if (index == 2) return "" + (int) UPKEEP_PERCENT + "%";
		if (index == 3) return "" + (int) Math.round((1f - SPEED_MULT) * 100f) + "%";
		if (index == 4) return "" + (int) PROFILE_PERCENT + "%";
		return null;
	}
}