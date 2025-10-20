package data.scripts.orr.luna;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;


import lunalib.lunaRefit.BaseRefitButton;

public class CopyOldslaughtButton extends BaseRefitButton {
	
	private static String WEAPON_ID = "orr_har_deco";
	
	@Override
	public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "Draft Heavy Adjudicator Blueprint";
	}
	
	@Override
	public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "graphics/orr/hullmods/blueprint.png";
	}
	
	@Override
	public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
		return 102;
	}
	
	@Override
	public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return true;
	}
	
	@Override
	public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		tooltip.addPara("Draft Heavy Adjudicator Blueprint", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
		tooltip.addSpacer(5f);

		tooltip.addPara("You can circumvent the ancient forge template encryption protocols on the built-in weapons for this ship. Drafting the Heavy Adjudicator blueprint will allow you to construct modern replicas that can be installed on standard Onslaught battleships. Requires 2 Story Points.", 0f);
		
		if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 2) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Insufficient Story Points", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
	}
	
	@Override
	public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {
		Global.getSector().getCharacterData().getPerson().getStats().spendStoryPoints(2, false, null, false, "");
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		cargo.addSpecial(new SpecialItemData("weapon_bp", WEAPON_ID), 1);
		Global.getSector().getMemoryWithoutUpdate().set("$copied_har_blueprint", true);
		refreshVariant();
		refreshButtonList();
	}
	
	//Makes the button not clickable if mod cannot be fitted
	@Override
	public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 2) return false;
		return isOldslaught(variant);
	}
	
	@Override
	public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		if (Global.getSector().getMemoryWithoutUpdate().is("$copied_har_blueprint", true)) return false;
		return isOldslaught(variant);
	}
	
	private static boolean isOldslaught(ShipVariantAPI variant) {
		String s = variant.getHullSpec().getHullId();
		return (s.equals("onslaught_mk1") || s.equals("onslaught_mk1_default_D"));
	}
}