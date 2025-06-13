package data.scripts.vice.util;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

public class RemoveSpecialFightersUtil {
	
	//deletes incorrect special fighters when fitted to carriers
	//used by PruneBantengMarketListener and from built-in mods on carriers that can take hullmod spawned fighter
	
	private static String THUNDERBOX_MOD = "vice_vast_launch_bays";
	private static String EXHORTATION_MOD = "vice_converted_battlecarrier";
	private static String PROTEUS_MOD = "vice_proteus_handler";
	private static String STAR_ODYSSEY_MOD = "vice_odyssey_milspec_cabal";
	
	private static String THUNDERBOX_WING_A = "vice_kite_atk_wing";
	private static String THUNDERBOX_WING_B = "vice_kite_bmr_wing";
	private static String EXHORTATION_WING = "vice_kite_lg_wing";
	private static String PROTEUS_WING = "vice_kite_dem_wing";
	private static String STAR_ODYSSEY_WING = "vice_disruptor_drone_wing";
	
	public static void deleteSpecialLPCs(List<String> wingList) {
		if (Global.getSector() == null || Global.getSector().getPlayerFleet() == null) return;
		List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getMembersWithFightersCopy();
		for (FleetMemberAPI member : fleetList) {
			ShipVariantAPI variant = member.getVariant();
			if (variant.getNonBuiltInWings().size() < 1f) return;
			//let carrier built-in hullmods do their own special check with truncated figher ban list
			if (variant.hasHullMod(THUNDERBOX_MOD)
					|| variant.hasHullMod(EXHORTATION_MOD)
					|| variant.hasHullMod(PROTEUS_MOD)
					|| variant.hasHullMod(STAR_ODYSSEY_MOD)) return;
			for (String wing : wingList) {
				List<String> wings = variant.getWings();
				for (int i = 0; i < wings.size(); i++) {
					if (wings.get(i).equals(wing)) variant.setWingId(i, null);
				}
			}
		}
	}
	
	//should really do a hashmap for this
	public static void deleteSpecialLPCsFromHull(ShipVariantAPI variant, String hullmod) {
		List<String> wingList = new ArrayList<String> ();
		if (hullmod.equals(THUNDERBOX_MOD)) {
			wingList.add(EXHORTATION_WING);
			wingList.add(PROTEUS_WING);
			wingList.add(STAR_ODYSSEY_WING);
		}
		else if (hullmod.equals(EXHORTATION_MOD)) {
			wingList.add(THUNDERBOX_WING_A);
			wingList.add(THUNDERBOX_WING_B);
			wingList.add(PROTEUS_WING);
			wingList.add(STAR_ODYSSEY_WING);
		}
		else if (hullmod.equals(PROTEUS_MOD)) {
			wingList.add(THUNDERBOX_WING_A);
			wingList.add(THUNDERBOX_WING_B);
			wingList.add(EXHORTATION_WING);
			wingList.add(STAR_ODYSSEY_WING);			
		}
		else if (hullmod.equals(STAR_ODYSSEY_MOD)) {
			wingList.add(THUNDERBOX_WING_A);
			wingList.add(THUNDERBOX_WING_B);
			wingList.add(EXHORTATION_WING);
			wingList.add(PROTEUS_WING);
		}
		for (String wing : wingList) {
			List<String> wings = variant.getWings();
			for (int i = 0; i < wings.size(); i++) {
				if (wings.get(i).equals(wing)) variant.setWingId(i, null);
			}
		}
	}
}