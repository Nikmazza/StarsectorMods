package data.scripts.orr.luna;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import lunalib.lunaRefit.BaseRefitButton;

public class InstallOnslaughtButton extends BaseRefitButton {
	
	private static String SKIN_MOD = "ML_skinSwap";
	private static String WRECK_MOD = "orr_missing_weapons";
	private static String MOD_HULLMOD = "orr_revised_armaments";
	private static String LG_MOD = "vice_onslaught_hull";
	private static String AGGRESSOR_MOD = "orr_aggressor";
	private static String ASMGRESSOR_MOD = "orr_asmgressor";
	private static String ONSLAUGHT_MOD = "orr_onslaught";
	private static String CGR_MOD = "orr_damper_field";
	private static String ASM_MOD = "orr_asmslaught";
	private static String DPL_MOD = "orr_dplslaught";
	
	private static String IX_MOD = "ix_ninth";
	private static String TW_MOD = "tw_trinity_retrofit";
	private static String LEFT_IX_ID = "WS CB1";
	private static String RIGHT_IX_ID = "WS CB2";

	private static String REM_MOD = "vice_incandescent_remnant";
	private static String EX_MOD = "vice_intrepid_hull";
	private static String LEFT_HT_ID = "WS 020";
	private static String RIGHT_HT_ID = "WS 021";
	
	private static String LEFT_ID = "WS 016";
	private static String RIGHT_ID = "WS 017";
	private static String DECO_SLOT = "WS 025";
	
	private static List<String> HULL_LIST = new ArrayList<String>();
	static {
		HULL_LIST.add("vice_incandescent");
		HULL_LIST.add("vice_incandescent_default_D");
		HULL_LIST.add("vice_onslaught_lg");
		HULL_LIST.add("vice_onslaught_lg_default_D");
		HULL_LIST.add("dpl_onslaught_alt");
		HULL_LIST.add("dpl_onslaught_alt_default_D");
		HULL_LIST.add("intrepid_ix");
		HULL_LIST.add("intrepid_ix_default_D");
		HULL_LIST.add("intrepid_tw");
		HULL_LIST.add("intrepid_tw_default_D");
	}
	
	private static List<String> BAN_LIST = new ArrayList<String>();
	static {
		BAN_LIST.add("vice_incandescent");
		BAN_LIST.add("vice_incandescent_default_D");
		//BAN_LIST.add("vice_intrepid_ex");
		//BAN_LIST.add("vice_intrepid_ex_default_D");
		BAN_LIST.add("vice_onslaught_lg");
		BAN_LIST.add("vice_onslaught_lg_default_D");
		BAN_LIST.add("onslaught_xiii");
		BAN_LIST.add("onslaught_xiii_default_D");
		BAN_LIST.add("intrepid_ix");
		BAN_LIST.add("intrepid_ix_default_D");
		BAN_LIST.add("intrepid_tw");
		BAN_LIST.add("intrepid_tw_default_D");
	}
	
	private static List<String> WEAPON_LIST = new ArrayList<String>();
	static {
		WEAPON_LIST.add("orr_ana");
		WEAPON_LIST.add("orr_giga");
		WEAPON_LIST.add("orr_har");
		WEAPON_LIST.add("orr_hmc");
		WEAPON_LIST.add("orr_nal");
		WEAPON_LIST.add("orr_tac");
		WEAPON_LIST.add("orr_tpc");
		WEAPON_LIST.add("orr_tpl");
	}

	private static List<String> SUFFIX_LIST = new ArrayList<String>();
	static {
		SUFFIX_LIST.add("_ana");
		SUFFIX_LIST.add("_giga");
		SUFFIX_LIST.add("_har");
		SUFFIX_LIST.add("_hmc");
		SUFFIX_LIST.add("_nal");
		SUFFIX_LIST.add("_tac");
		SUFFIX_LIST.add("_tpc");
		SUFFIX_LIST.add("_tpl");
	}
	
	private static List<String> WO_HULLMOD_LIST = new ArrayList<String>();
	static {
		WO_HULLMOD_LIST.add("orr_wo_anathema");
		WO_HULLMOD_LIST.add("orr_wo_giga");
		WO_HULLMOD_LIST.add("orr_wo_har");
		WO_HULLMOD_LIST.add("orr_wo_hmc");
		WO_HULLMOD_LIST.add("orr_wo_nal");
		WO_HULLMOD_LIST.add("orr_wo_tac");
		WO_HULLMOD_LIST.add("orr_wo_tpc");
		WO_HULLMOD_LIST.add("orr_wo_tpl");
	}
	
	@Override
	public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "Install Built-in Weapons";
	}
	
	@Override
	public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "graphics/orr/hullmods/revised_armaments.png";
	}
	
	@Override
	public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
		return 100;
	}
	
	@Override
	public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return true;
	}
	
	@Override
	public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		tooltip.addPara("Install Built-in Weapons", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
		tooltip.addSpacer(5f);

		tooltip.addPara("Spend a Story Point to replace the built-in weapons of an Onslaught or related hull. Any existing built-in weapons that have not been removed will be lost.", 0f);
		
		String wo = getWorkOrder(variant);
		boolean hasWorkOrder = (wo != null);
		String decoId = "";
		if (hasWorkOrder) decoId = getWeaponFromWorkOrder(wo) + "_deco";
		boolean hasWeapon = hasWeaponInCargo(decoId);
		
		if (isBanned(member)) {
			tooltip.addSpacer(10f);
			tooltip.addPara("This ship is already too extensively modified and cannot receive new built-in weapons.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
		else if (!isOnslaught(variant, member)) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Unsuitable hull type.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
		else if (market == null || !market.hasSpaceport()) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Must be docked at a spaceport.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
		else if (!hasWorkOrder) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Missing work order. Add the desired hullmod prior to installation.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
		else if (!hasWeapon) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Work ordered weapon is missing from cargo.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
		else if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 1) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Insufficient Story Points", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
	}
	
	private static String getPrefix(ShipVariantAPI v) {
		if (v.hasHullMod(AGGRESSOR_MOD) && v.hasHullMod("fourteenth")) return "orr_aggressor_xiv";
		else if (v.hasHullMod(AGGRESSOR_MOD)) return "orr_aggressor_orr";
		else if (v.hasHullMod(ASMGRESSOR_MOD)) return "orr_aggressor_asm";
		
		else if (v.hasHullMod(EX_MOD)) return "orr_intrepid_exg";
		
		else if (v.hasHullMod(CGR_MOD)) return "orr_onslaught_cgr";
		else if (v.hasHullMod(ASM_MOD)) return "orr_onslaught_asm";
		
		else if (v.hasHullMod(ONSLAUGHT_MOD) && v.hasHullMod("fourteenth")) return "orr_onslaught_xiv";
		else if (v.hasHullMod(ONSLAUGHT_MOD)) return "orr_onslaught_orr";
		
		else if (v.hasHullMod(DPL_MOD)) return "orr_onslaught_dpl";
		//else if (v.hasHullMod("vice_incandescent_remnant")) orr_incandescent_rem;
				
		return "orr_onslaught_orr";
	}
	
	private static String getSuffix(String weaponId) {
		for (int i = 0; i < WEAPON_LIST.size(); i++) {
			if (weaponId.equals((String) WEAPON_LIST.get(i))) return (String) SUFFIX_LIST.get(i);
		}
		return null;
	}
	
	@Override
	public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {
		variant.getPermaMods().remove(SKIN_MOD);
		variant.getHullMods().remove(SKIN_MOD);
		
		String wo = getWorkOrder(variant);
		String weaponId = getWeaponFromWorkOrder(wo);
		String decoId = weaponId + "_deco";	
		
		removeWeaponFromCargo(decoId);
		
		String prefix = getPrefix(variant);
		String suffix = getSuffix(weaponId);
        variant.setHullSpecAPI(Global.getSettings().getHullSpec(prefix + suffix));
		variant.getHullMods().remove(wo);
		variant.getPermaMods().remove(WRECK_MOD);
		variant.getHullMods().remove(WRECK_MOD);
		variant.addPermaMod(MOD_HULLMOD);
		variant.clearSlot(DECO_SLOT);
		variant.autoGenerateWeaponGroups();
		Global.getSector().getCharacterData().getPerson().getStats().spendStoryPoints(1, false, null, false, "");
		refreshVariant();
		refreshButtonList();
	}
	
	//Makes the button not clickable if mod cannot be fitted
	@Override
	public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		if (!isOnslaught(variant, member) || market == null || !market.hasSpaceport()) return false;
		if (isBanned(member)) return false;
		
		String wo = getWorkOrder(variant);
		boolean hasWorkOrder = (wo != null);
		if (!hasWorkOrder) return false;
		
		String decoId = getWeaponFromWorkOrder(wo) + "_deco";
		boolean hasWeapon = hasWeaponInCargo(decoId);
		if (!hasWeapon) return false;
		
		if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 1) return false;
		return true;
	}
	
	@Override
	public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return isOnslaught(variant, member) || isBanned(member);
	}
	
	private static String getWeaponFromWorkOrder(String modId) {
		for (int i = 0; i < WO_HULLMOD_LIST.size(); i++) {
			if (modId.equals((String) WO_HULLMOD_LIST.get(i))) return (String) WEAPON_LIST.get(i);
		}
		return null;
	}
	
	private static String getWorkOrder(ShipVariantAPI variant) {
		for (String mod : WO_HULLMOD_LIST) {
			if (variant.hasHullMod(mod)) return (String) mod;
		}
		return null;
	}
	
	private static boolean hasWeaponInCargo(String weaponId) {
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		for (CargoStackAPI s : cargo.getStacksCopy()) {
			if (s.isWeaponStack() && s.getWeaponSpecIfWeapon().getWeaponId().equals(weaponId)) return true;
		}
		return false;
	}
	
	private static boolean isOnslaught(ShipVariantAPI variant, FleetMemberAPI member) {
		if (variant.hasHullMod(WRECK_MOD) 
					|| variant.hasHullMod(MOD_HULLMOD)
					|| variant.hasHullMod(AGGRESSOR_MOD) 
					|| variant.hasHullMod(ASMGRESSOR_MOD) 
					|| variant.hasHullMod(ONSLAUGHT_MOD)
					|| variant.hasHullMod(CGR_MOD)
					|| variant.hasHullMod(ASM_MOD)
					|| variant.hasHullMod(DPL_MOD)
					|| variant.hasHullMod(EX_MOD)) return true;
		for (String s : HULL_LIST) {
			if (s.equals(member.getHullId())) return true;
		}
		return false;
	}
	
	private static boolean isBanned(FleetMemberAPI member) {
		for (String s : BAN_LIST) {
			String id = member.getHullId();
			if (id.equals(s)) return true;
		}
		return false;
	}
	
	private static void removeWeaponFromCargo(String weaponId) {
		boolean hasDeleted = false;
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		for (CargoStackAPI s : cargo.getStacksCopy()) {
			if (hasDeleted) return;
			if (s.isWeaponStack() && s.getWeaponSpecIfWeapon().getWeaponId().equals(weaponId)) {
				float size = s.getSize();
				if (size == 1f) cargo.removeStack(s);
				else s.subtract(1f);
				hasDeleted = true;
			}
		}
	}
}