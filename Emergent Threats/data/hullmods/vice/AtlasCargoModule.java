package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class AtlasCargoModule extends BaseHullMod {

	private static float CARGO_BONUS = 0.2f;
	private static float MAINTENANCE_PERCENT = 40f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (stats.getVariant() != null && stats.getVariant().hasHullMod(HullMods.EXPANDED_CARGO_HOLDS)) return; 
		stats.getCargoMod().modifyFlat(id, stats.getVariant().getHullSpec().getCargo() * CARGO_BONUS);
		if (stats.getVariant() != null 
					&& stats.getVariant().hasHullMod(HullMods.CIVGRADE) 
					&& !stats.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) {
			stats.getSuppliesPerMonth().modifyPercent(id, MAINTENANCE_PERCENT);
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) (CARGO_BONUS * 100f) + "%";
		if (index == 1) return "" + (int) MAINTENANCE_PERCENT + "%";
		return null;
	}
}




