package data.hullmods.vice;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

import data.scripts.vice.util.NameListUtil;

public class ShipRenamerLaertes extends BaseHullMod {
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (stats.getFleetMember() != null) stats.getFleetMember().setShipName(NameListUtil.TTS_LAERTES);
		boolean isDelete = false;
		if (stats.getFleetMember() != null && Global.getSector().getPlayerFleet() != null) {
			List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
			for (FleetMemberAPI member : fleetList) {
				if (member.getVariant().getHullVariantId() == stats.getVariant().getHullVariantId()) {
					isDelete = true;
				}
			}
		}
		
		if (isDelete && stats.getFleetMember() != null 
					&& stats.getFleetMember().getShipName().equals(NameListUtil.TTS_LAERTES)) {
			stats.getVariant().getPermaMods().remove(id);
			stats.getVariant().getHullMods().remove(id);
		}
	}
}