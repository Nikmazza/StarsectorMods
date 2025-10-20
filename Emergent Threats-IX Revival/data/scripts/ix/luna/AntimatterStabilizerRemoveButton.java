package data.scripts.ix.luna;

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

public class AntimatterStabilizerRemoveButton extends BaseRefitButton {

	private static String CONTROLLER_HULLMOD = "ix_charge_controller";	
	private static String ITEM_ID = "ix_antimatter_stabilizer";

	@Override
	public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "Remove Antimatter Stabilizer";
	}

	@Override
	public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "graphics/hullmods/ix_charge_inhibitor.png";
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
		tooltip.addPara("Remove Antimatter Stabilizer", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
		tooltip.addSpacer(5f);
		tooltip.addPara("Remove the Antimatter Stabilizer from this ship and deactivate its energy weapon altering hullmod.", 0f);
		
		if (market == null || !market.hasSpaceport()) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Must be docked at a spaceport.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
	}

	@Override
	public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {
		variant.getPermaMods().remove(CONTROLLER_HULLMOD);
		variant.getHullMods().remove(CONTROLLER_HULLMOD);
		addItemToInventory();
		refreshVariant();
		refreshButtonList();
	}
	
	@Override
	public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		if (market == null || !market.hasSpaceport()) return false;
		return variant.hasHullMod(CONTROLLER_HULLMOD);
	}

	@Override
	public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return variant.hasHullMod(CONTROLLER_HULLMOD);
	}
	
	private void addItemToInventory() {
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		cargo.addCommodity(ITEM_ID, 1f);
	}
}