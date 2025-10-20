package data.scripts.ix.util;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

public class RemoveSpecialFightersUtil {
	
	//used by PruneBantengMarketListener to clear scrapped ship 0 cost Nimbus/Starquake LPCs on other carriers
	private static String RADIANT_MOD = "ix_converted_hull";
	private static String TW_MOD = "tw_trinity_retrofit";
	private static String STARQUAKE_WING = "starquake_tw_wing";
	
	public static void deleteSpecialLPCs(List<String> wingList) {
		if (Global.getSector() == null || Global.getSector().getPlayerFleet() == null) return;
		List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getMembersWithFightersCopy();
		for (FleetMemberAPI member : fleetList) {
			ShipVariantAPI variant = member.getVariant();
			if (variant.getNonBuiltInWings().size() < 1f) return;
			for (String wing : wingList) {
				//do not apply for nimbus fighters on TW carriers, or Starquake on Radiant (TW)
				if (!wing.equals(STARQUAKE_WING) && variant.hasHullMod(TW_MOD)) continue;
				else if (wing.equals(STARQUAKE_WING) 
						&& variant.hasHullMod(RADIANT_MOD)
						&& variant.hasHullMod(TW_MOD)) continue;
				List<String> wings = variant.getWings();
				for (int i = 0; i < wings.size(); i++) {
					if (wings.get(i).equals(wing)) variant.setWingId(i, null);
				}
			}
		}
	}
}