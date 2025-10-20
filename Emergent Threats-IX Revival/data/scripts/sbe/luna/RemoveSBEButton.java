package data.scripts.sbe.luna;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import lunalib.lunaRefit.BaseRefitButton;

public class RemoveSBEButton extends BaseRefitButton {
	
	private static String SBE_GUN_MOD = "ix_sbe";
	private static String NO_GUN_MOD = "ix_dedicated_ewar_suite";
	private static String ECM_MOD = "ecm";
	private static String INTREPID_MOD = "ix_intrepid_sbe_handler";
	private static String RADIANT_MOD = "ix_radiant_sbe_handler";
	private static String ITEM_ID = "ix_antimatter_stabilizer";
	private static String WEAPON_ID = "sbe_modular_ix";
	
	@Override
	public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "Remove Soliton Burst Emitter";
	}
	
	@Override
	public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "graphics/hullmods/ix_sbe.png";
	}
	
	@Override
	public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
		return 107;
	}
	
	@Override
	public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return true;
	}
	
	@Override
	public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		Color warnColor = Misc.getNegativeHighlightColor();
		Color goodColor = Misc.getPositiveHighlightColor();
		tooltip.addPara("Remove Soliton Burst Emitter", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
		tooltip.addSpacer(5f);
		tooltip.addPara("Spend a Story Point to remove the built-in Soliton Burst Emitter on this hull and replace it with a Dedicated EWAR Suite. If an %s is present in cargo, the weapon will be assembled into a modular form usable on other ships. Otherwise the weapon is lost.", 10f, Misc.getHighlightColor(), "Antimatter Stabilizer");
		tooltip.addSpacer(5f);
		tooltip.addPara("A dedicated electronic warfare suite will free up %s OP, grant %s ECM rating, and launch a wide area EMP burst near the closest enemy ship or missile within %s su, once every %s seconds. Replaces any existing ECM Package hullmod.", 10f, Misc.getHighlightColor(), "20", "6%", "1000", "5");
		
		boolean isReceivingWarning = false;
		if (market == null || !market.hasSpaceport()) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Must be docked at a spaceport.", 0f, warnColor, warnColor);
			isReceivingWarning = true;
		}
		else if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 1) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Insufficient Story Points", 0f, warnColor, warnColor);
			isReceivingWarning = true;
		}
		if (!isReceivingWarning) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Warning: This process cannot be undone. Any existing ECM Package will be lost.", 0f, warnColor, warnColor);
			if (!hasItemInInventory()) {
				tooltip.addSpacer(5f);
				tooltip.addPara("Warning: No Antimatter Stabilizer available. The removed Soliton Burst Emitter will be lost.", 0f, warnColor, warnColor);
			}
			else {
				tooltip.addSpacer(5f);
				tooltip.addPara("One Antimatter Stabilizer will be consumed to forge a modular Soliton Burst Emitter.", 0f, goodColor, goodColor);
			}
		}
	}
	
	@Override
	public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {
		variant.addPermaMod(NO_GUN_MOD);
		if (hasItemInInventory()) {
			removeItemFromInventory();
			addGunToInventory();
		}
		Global.getSector().getCharacterData().getPerson().getStats().spendStoryPoints(1, false, null, false, "");
		refreshVariant();
		refreshButtonList();
	}
	
	private void addGunToInventory() {
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		cargo.addWeapons(WEAPON_ID, 1);
	}
	
	@Override
	public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		if (market == null || !market.hasSpaceport()) return false;
		if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 1) return false;
		return variant.hasHullMod(SBE_GUN_MOD);
	}
	
	@Override
	public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return variant.hasHullMod(SBE_GUN_MOD);
	}
	
	private boolean hasItemInInventory() {
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		return cargo.getCommodityQuantity(ITEM_ID) > 0f;
	}
	
	private void removeItemFromInventory() {
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		cargo.removeCommodity(ITEM_ID, 1f);
	}
}