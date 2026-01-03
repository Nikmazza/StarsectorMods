package data.hullmods.vice;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class AtlasFuelCalculator extends BaseHullMod {
	
	//should match AtlasFuelScoop
	private static float FUEL_BONUS_1 = 0.15f;
	private static float FUEL_BONUS_2 = 0.22f;
	private static float FUEL_BONUS_3 = 0.25f;
	
	private static String SCOOP_MOD_ID = "vice_atlas_fuel_scoop";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean isPlayer = false;
		ShipVariantAPI variant = stats.getVariant();
		if (stats.getFleetMember() == null || Global.getSector().getPlayerFleet() == null) {
			stats.getFuelUseMod().unmodify(id);
			variant.getPermaMods().remove(id);
			variant.getHullMods().remove(id);
			return;
		}
		
		int scoopCount = 0;
		List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
		for (FleetMemberAPI member : fleetList) {
			if (member.getVariant().getHullVariantId() == stats.getVariant().getHullVariantId()) isPlayer = true;
			if (member.getVariant().hasHullMod(SCOOP_MOD_ID)) scoopCount++;
		}
		if (!isPlayer || scoopCount == 0) {
			stats.getFuelUseMod().unmodify(id);
			variant.getPermaMods().remove(id);
			variant.getHullMods().remove(id);
			return;
		}
		float bonus = 0f;
		if (scoopCount == 1) bonus = FUEL_BONUS_1;
		else if (scoopCount == 2) bonus = FUEL_BONUS_2;
		else if (scoopCount >= 3) bonus = FUEL_BONUS_3;
		bonus = 1f - bonus;
		stats.getFuelUseMod().modifyMult(id, bonus);
	}
}