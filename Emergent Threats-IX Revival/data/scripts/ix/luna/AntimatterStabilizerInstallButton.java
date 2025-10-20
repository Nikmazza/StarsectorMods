package data.scripts.ix.luna;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import lunalib.lunaRefit.BaseRefitButton;

public class AntimatterStabilizerInstallButton extends BaseRefitButton {

	private static String CONTROLLER_HULLMOD = "ix_charge_controller";	
	private static String ITEM_ID = "ix_antimatter_stabilizer";

	@Override
	public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "Install Antimatter Stabilizer";
	}

	@Override
	public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "graphics/hullmods/ix_charge_amplifier.png";
	}

	@Override
	public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
		return 106;
	}

	@Override
	public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return true;
	}

	@Override
	public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		Color warnColor = Misc.getNegativeHighlightColor();
		tooltip.addPara("Install Antimatter Stabilizer", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
		tooltip.addSpacer(5f);
		tooltip.addPara("Spend a Story Point to install an Antimatter Stabilizer.", 0f);
		tooltip.addSpacer(5f);
		tooltip.addPara("The ship will receive a %s penalty to its peak operating time, but gains three separate modes that affect energy weapon performance.", 0f, Misc.getHighlightColor(), "25%");
		int spTotal = getStoryPointTotal();
		if (market == null || !market.hasSpaceport() || !hasItemInInventory() || spTotal < 1) {
			tooltip.addSpacer(5f);
		}
		if (market == null || !market.hasSpaceport()) {
			tooltip.addSpacer(5f);
			tooltip.addPara("Must be docked at a spaceport.", 0f, warnColor, warnColor);
		}
		if (!hasItemInInventory()) {
			tooltip.addSpacer(5f);
			tooltip.addPara("No Antimatter Stabilizer available.", 0f, warnColor, warnColor);
		}
		if (spTotal < 1) {
			tooltip.addSpacer(5f);
			tooltip.addPara("Insufficient Story Points", 0f, warnColor, warnColor);
		}
	}

	@Override
	public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {
		variant.getHullMods().add(CONTROLLER_HULLMOD);
		variant.getPermaMods().add(CONTROLLER_HULLMOD);
		RemoveItemFromInventory();
		Global.getSector().getCharacterData().getPerson().getStats().spendStoryPoints(1, false, null, false, "");
		refreshVariant();
		refreshButtonList();
	}
	
	@Override
	public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		if (market == null || !market.hasSpaceport()) return false;
		if (getStoryPointTotal() < 1) return false;
		if (!hasItemInInventory()) return false;
		return !variant.hasHullMod(CONTROLLER_HULLMOD);
	}
	
	@Override
	public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return !variant.hasHullMod(CONTROLLER_HULLMOD);
	}
	
	private int getStoryPointTotal() {
		return Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints();
	}
	
	private boolean hasItemInInventory() {
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		return cargo.getCommodityQuantity(ITEM_ID) > 0f;
	}
	
	private void RemoveItemFromInventory() {
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		cargo.removeCommodity(ITEM_ID, 1f);
	}
}