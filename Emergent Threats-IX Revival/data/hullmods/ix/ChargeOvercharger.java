package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class ChargeOvercharger extends BaseHullMod {
	
	private static String CONTROLLER_ID = "ix_charge_controller";
	private static String OVR_H_ID = "ix_charge_overcharger_handler";
	private static float PEAK_PENALTY_PERCENT = 25f;
	private static float ENERGY_BOOST = 20f;
	private static float RANGE_PENALTY = 30f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI variant = stats.getVariant();
		//if stabilizer has been removed, delete controller then delete itself
		if (!variant.hasHullMod(CONTROLLER_ID)) {
			variant.getHullMods().remove(OVR_H_ID);
			variant.getHullMods().remove(id);
		}
		stats.getPeakCRDuration().modifyPercent(id, -PEAK_PENALTY_PERCENT);
		stats.getEnergyRoFMult().modifyMult(id, 1f + ENERGY_BOOST * 0.01f);
		stats.getEnergyWeaponRangeBonus().modifyMult(id, 1f - RANGE_PENALTY * 0.01f);
	}
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) PEAK_PENALTY_PERCENT + "%";
		if (index == 1) return "overcharge";
		if (index == 2) return "" + (int) ENERGY_BOOST + "%";
		if (index == 3) return "" + (int) RANGE_PENALTY + "%";
		return null;
	}
}