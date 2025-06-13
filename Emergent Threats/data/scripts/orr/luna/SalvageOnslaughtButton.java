package data.scripts.orr.luna;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import lunalib.lunaRefit.BaseRefitButton;
import lunalib.lunaUI.elements.LunaSpriteElement;

public class SalvageOnslaughtButton extends BaseRefitButton {
	
	private static String WRECK_MOD = "orr_missing_weapons";
	private static String MOD_HULLMOD = "orr_revised_armaments";
	private static String LG_MOD = "vice_onslaught_hull";
	private static String AGGRESSOR_MOD = "orr_aggressor";
	private static String ONSLAUGHT_MOD = "orr_onslaught";
	private static String CGR_MOD = "orr_damper_field";
	
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
	
	private static List<String> WEAPON_LIST = new ArrayList<String>();
	static {
		WEAPON_LIST.add("orr_ana");
		WEAPON_LIST.add("orr_giga");
		WEAPON_LIST.add("orr_hmc");
		WEAPON_LIST.add("orr_nal");
		WEAPON_LIST.add("orr_tac");
		WEAPON_LIST.add("orr_tpc");
		WEAPON_LIST.add("orr_tpl");
	}
	
	private static List<String> HULL_LIST = new ArrayList<String>();
	static {
		HULL_LIST.add("vice_incandescent");
		HULL_LIST.add("vice_incandescent_default_D");
		HULL_LIST.add("vice_intrepid_ex");
		HULL_LIST.add("vice_intrepid_ex_default_D");
		HULL_LIST.add("vice_onslaught_lg");
		HULL_LIST.add("vice_onslaught_lg_default_D");
		HULL_LIST.add("dpl_onslaught_alt");
		HULL_LIST.add("dpl_onslaught_alt_default_D");
	}
	
	private static List<String> BAN_LIST = new ArrayList<String>();
	static {
		BAN_LIST.add("vice_incandescent");
		BAN_LIST.add("vice_incandescent_default_D");
		BAN_LIST.add("vice_intrepid_ex");
		BAN_LIST.add("vice_intrepid_ex_default_D");
		BAN_LIST.add("vice_onslaught_lg");
		BAN_LIST.add("vice_onslaught_lg_default_D");
	}
	
	@Override
	public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "Salvage Built-in Weapons";
	}
	
	@Override
	public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "graphics/orr/hullmods/work_order.png";
	}
	
	@Override
	public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
		return 101;
	}
	
	@Override
	public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return true;
	}
	
	@Override
	public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		tooltip.addPara("Salvage Built-in Weapons", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
		tooltip.addSpacer(5f);

		tooltip.addPara("Spend a Story Point to remove the twin built-in weapons of an Onslaught or related hull.", 0f);
		
		if (variant.hasHullMod(WRECK_MOD)) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Built-in weapons already removed.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
		else if (market == null || !market.hasSpaceport()) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Must be docked at a spaceport.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
		else if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 1) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Insufficient Story Points", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
		else if (isBanned(member)) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Warning: Due to the complexity of this ship, once its built-in weapons have been removed, they cannot be replaced. The ship will be rendered unusable.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
	}
	
	@Override
	public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {
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
		
		String deco = variant.getWeaponSpec(left).getWeaponId() + "_deco";
		//Needs custom method once other Intrepid hulls are included
		if (variant.hasHullMod(EX_MOD)) deco = "orr_nal_deco";
		if (variant.hasHullMod(REM_MOD)) deco = "orr_hmc_deco";
		else if (variant.hasHullMod(LG_MOD)) deco = "orr_giga_deco";
		else if (member.getHullId().equals("dpl_onslaught_alt") || member.getHullId().equals("orr_onslaught_dpl_tpl")) deco = "orr_tpl_deco";
		giveWeapon(deco);
		variant.getPermaMods().remove(MOD_HULLMOD);
		variant.getHullMods().remove(MOD_HULLMOD);
		variant.addPermaMod(WRECK_MOD);
		Global.getSector().getCharacterData().getPerson().getStats().spendStoryPoints(1, false, null, false, "");
		refreshVariant();
		refreshButtonList();
	}
	
	//Makes the button not clickable if mod cannot be fitted
	@Override
	public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		if (variant.hasHullMod(WRECK_MOD)) return false;
		if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 1) return false;
		return isOnslaught(variant, member) && market != null && market.hasSpaceport();
	}
	
	@Override
	public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return isOnslaught(variant, member);
	}
	
	private static boolean isBanned(FleetMemberAPI member) {
		for (String s : BAN_LIST) {
			String id = member.getHullId();
			if (id.equals(s)) return true;
		}
		return false;
	}
	
	private static boolean isOnslaught(ShipVariantAPI variant, FleetMemberAPI member) {
		if (variant.hasHullMod(WRECK_MOD) 
					|| variant.hasHullMod(MOD_HULLMOD)
					|| variant.hasHullMod(AGGRESSOR_MOD) 
					|| variant.hasHullMod(ONSLAUGHT_MOD)
					|| variant.hasHullMod(CGR_MOD)) return true;
		for (String s : HULL_LIST) {
			if (s.equals(member.getHullId())) return true;
		}
		return false;
	}
	
	private static void giveWeapon(String deco) {
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		CharacterDataAPI player = Global.getSector().getCharacterData();
		cargo.addWeapons(deco, 1);
	}
}