package data.hullmods.sbe;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class DedicatedEWAR extends BaseHullMod {

	private static float ECM_BONUS = 6f;
	private static String DISCOUNT_MOD = "ix_ecm_discount_20";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, ECM_BONUS);
		//stats.getVariant().getHullMods().remove("ecm"); //done in BattleshipSBEHandler
		
		//for avoiding concurrent modification crashes, apply discount hullmod to player ships only
		if (stats.getFleetMember() != null && Global.getSector().getPlayerFleet() != null) {
			List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
			for (FleetMemberAPI member : fleetList) {
				if (member.getVariant().getHullVariantId() == stats.getVariant().getHullVariantId()) {
					return;
				}
			}
		}
		stats.getVariant().getHullMods().add(DISCOUNT_MOD);
	}
	
	//add EWAR attack
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Soliton Burst Emitter";
		if (index == 1) return "20";
		if (index == 2) return "" + (int) ECM_BONUS + "%";
		if (index == 3) return "1000";
		if (index == 4) return "5";
		return null;
	}
}