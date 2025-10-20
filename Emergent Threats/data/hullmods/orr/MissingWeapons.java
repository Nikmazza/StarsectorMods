package data.hullmods.orr;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;

public class MissingWeapons extends BaseHullMod {

	public static float CR_PENALTY = 100f;
	
	private static String MOD_HULLMOD = "orr_revised_armaments";
	private static String LEFT_ID = "WS 016";
	private static String RIGHT_ID = "WS 017";
	private static String DECO_SLOT = "WS 025";
	
	private static String IX_MOD = "ix_ninth";
	private static String TW_MOD = "tw_trinity_retrofit";
	private static String LEFT_IX_ID = "WS CB1";
	private static String RIGHT_IX_ID = "WS CB2";

	private static String EX_MOD = "vice_intrepid_hull";
	private static String REM_MOD = "vice_incandescent_remnant";
	private static String LEFT_HT_ID = "WS 020";
	private static String RIGHT_HT_ID = "WS 021";

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxCombatReadiness().modifyFlat(id, -CR_PENALTY * 0.01f, "Missing Weapons");
		ShipVariantAPI variant = stats.getVariant();
		String left = LEFT_ID;
		String right = RIGHT_ID;
		if (variant.hasHullMod(IX_MOD) || variant.hasHullMod(TW_MOD)) {
			left = LEFT_IX_ID;
			right = RIGHT_IX_ID;
		}
		else if (variant.hasHullMod(EX_MOD) || variant.hasHullMod(REM_MOD)) {
			left = LEFT_HT_ID;
			right = RIGHT_HT_ID;
		}
		variant.clearSlot(left);
		variant.clearSlot(right);
		variant.addWeapon(DECO_SLOT, getWreckMod(variant));
		removeDecosFromInventory();
	}
	
	private static String getWreckMod(ShipVariantAPI v) {
		if (v.hasHullMod("orr_aggressor") && v.hasHullMod("fourteenth")) return "orr_wreck_agg_xiv";
		else if (v.hasHullMod("orr_aggressor")) return "orr_wreck_agg";
		else if (v.hasHullMod("orr_asmgressor")) return "orr_wreck_agg_asm";
		else if (v.hasHullMod("orr_asmslaught")) return "orr_wreck_asm";
		else if (v.hasHullMod("orr_damper_field")) return "orr_wreck_cgr";
		else if (v.hasHullMod("vice_onslaught_hull")) return "orr_wreck_lg";
		else if (v.hasHullMod("vice_intrepid_hull")) return "orr_wreck_ex";
		else if (v.hasHullMod("vice_incandescent_remnant")) return "orr_wreck_rem";
		else if (v.hasHullMod("orr_onslaught") && v.hasHullMod("fourteenth")) return "orr_wreck_xiv";
		else if (v.hasHullMod("orr_onslaught") || v.hasHullMod("thirteenth")) return "orr_wreck_orr";
		return "orr_wreck_dpl"; //Phase Lab Onslaught, need to make more specific if other variants get added
	}
	
	private static void removeDecosFromInventory() {
		try {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			for (CargoStackAPI s : cargo.getStacksCopy()) {
				if (s.isWeaponStack()) {
					if (s.getWeaponSpecIfWeapon().getType().equals(WeaponType.DECORATIVE)) cargo.removeStack(s);
				}
			}
		}
		catch (Exception e) {}
	}
}