package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class ChargeInhibitor extends BaseHullMod {
	
	private static String CONTROLLER_ID = "ix_charge_controller";
	private static String INHB_H_ID = "ix_charge_inhibitor_handler";
	private static float PEAK_PENALTY_PERCENT = 25f;
	private static float ENERGY_BOOST = 15f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI variant = stats.getVariant();
		//if stabilizer has been removed, delete controller then delete itself
		if (!variant.hasHullMod(CONTROLLER_ID)) {
			variant.getHullMods().remove(INHB_H_ID);
			variant.getHullMods().remove(id);
		}
		stats.getPeakCRDuration().modifyPercent(id, -PEAK_PENALTY_PERCENT);
		stats.getEnergyWeaponDamageMult().modifyMult(id, 1f - ENERGY_BOOST * 0.01f);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - ENERGY_BOOST * 0.01f);
	}
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) PEAK_PENALTY_PERCENT + "%";
		if (index == 1) return "inhibit";
		if (index == 2) return "" + (int) ENERGY_BOOST + "%";
		return null;
	}
}