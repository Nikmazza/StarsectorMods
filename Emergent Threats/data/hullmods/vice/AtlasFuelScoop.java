package data.hullmods.vice;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class AtlasFuelScoop extends BaseHullMod {

	private static int BURN_LEVEL_BONUS = 1;
	private static float FUEL_BONUS_1 = 0.15f;
	private static float FUEL_BONUS_2 = 0.22f;
	private static float FUEL_BONUS_3 = 0.25f;
	private static String CALC_MOD_ID = "vice_atlas_fuel_calculator";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
		boolean isPlayerFleet = false;
		if (stats.getFleetMember() != null && Global.getSector().getPlayerFleet() != null) {
			List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
			for (FleetMemberAPI member : fleetList) {
				if (member.getVariant().getHullVariantId() == stats.getVariant().getHullVariantId()) {
					isPlayerFleet = true;
				}
			}
			if (!isPlayerFleet) return;
			//spreads fuel use calculator mod to each ship in fleet, mod does actual math for each ship
			for (FleetMemberAPI member : fleetList) {
				member.getVariant().addPermaMod(CALC_MOD_ID);
			}
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + BURN_LEVEL_BONUS;
		if (index == 1) return "" + (int) (FUEL_BONUS_1 * 100f) + "%";
		if (index == 2) return "" + (int) (FUEL_BONUS_2 * 100f) + "%";
		if (index == 3) return "" + (int) (FUEL_BONUS_3 * 100f) + "%";
		return null;
	}
}