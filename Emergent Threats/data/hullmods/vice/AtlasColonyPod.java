package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class AtlasColonyPod extends BaseHullMod {
	
	private static float SURVEY_DISCOUNT = 30f;
	private static float SURVEY_MINIMUM = 5f;
	private static float CARGO_PENALTY = 80f;
	private static float FUEL_BONUS = 50f;
	private static float CREW_FLAT_BONUS = 1100f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (stats.getVariant().hasHullMod("additional_berthing")) return;
		
		stats.getCargoMod().modifyMult(id, 1f - CARGO_PENALTY * 0.01f);
		stats.getFuelMod().modifyMult(id, 1f + FUEL_BONUS * 0.01f);
		stats.getMaxCrewMod().modifyFlat(id, CREW_FLAT_BONUS);
		
		if (!stats.getVariant().hasHullMod("surveying_equipment")) {
			stats.getDynamic().getMod(Stats.getSurveyCostReductionId(Commodities.HEAVY_MACHINERY)).modifyFlat(id, SURVEY_DISCOUNT);
			stats.getDynamic().getMod(Stats.getSurveyCostReductionId(Commodities.SUPPLIES)).modifyFlat(id, SURVEY_DISCOUNT);
		}	
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) CARGO_PENALTY + "%";
		if (index == 1) return "" + (int) FUEL_BONUS + "%";
		if (index == 2) return "" + (int) (CREW_FLAT_BONUS + 100f);
		if (index == 3) return "" + (int) SURVEY_DISCOUNT;
		if (index == 4) return "" + (int) SURVEY_MINIMUM;
		return null;
	}
}




