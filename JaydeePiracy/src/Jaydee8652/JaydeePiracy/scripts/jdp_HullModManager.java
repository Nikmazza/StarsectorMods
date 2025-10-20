package Jaydee8652.JaydeePiracy.scripts;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.RefitScreenListener;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.util.ListMap;
import com.fs.starfarer.api.util.Misc;

public class jdp_HullModManager implements RefitScreenListener {
	@Override
	public void reportFleetMemberVariantSaved(FleetMemberAPI member, MarketAPI dockedAt) {
		List<String> wings = member.getVariant().getWings();
		ShipVariantAPI ship = member.getVariant();
		boolean hasGroundSupportShips = false;
		if (!wings.isEmpty()) {
			for (int i = 0; i < wings.size(); i++) {
				if (ship.getWing(i) == null) {
					break;
				}
				if (ship.getWing(i).getTags().contains("jdp_groundSupportFighter")) {
					hasGroundSupportShips = true;
					if (!ship.hasHullMod("jdp_supportfighter")) {
						ship.addPermaMod("jdp_supportfighter");
					}
				}
			}
		}
		if (!hasGroundSupportShips) {
			ship.removePermaMod("jdp_supportfighter");
		}
	}
}












