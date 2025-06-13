package data.scripts.vice.luna;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import lunalib.lunaRefit.BaseRefitButton;

public class RemoveMissionDeployHullmod extends BaseRefitButton {

	private static String HULLMOD_ID_1 = "vice_mission_deloy_vantage";
	private static String HULLMOD_ID_2 = "vice_mission_deloy_faith";
	private static String HULLMOD_ID_3 = "vice_mission_deloy_mayfly";
	private static String HULLMOD_ID_4 = "vice_mission_deloy_diamond";
	
	@Override
	public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "Remove HVB Hullmod";
	}

	@Override
	public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "graphics/vice/hullmods/mission_deploy.png";
	}

	@Override
	public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
		return 4006;
	}

	@Override
	public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return true;
	}

	@Override
	public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		tooltip.addPara("Remove HVB Hullmod", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
		tooltip.addSpacer(5f);
		tooltip.addPara("Remove the Ascended Tactical AI, Religious Zeal, Ruthless Networked AI, or Temporal Ghost hullmods.", 0f);
	}

	@Override
	public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {
		variant.getPermaMods().remove(HULLMOD_ID_1);
		variant.getPermaMods().remove(HULLMOD_ID_2);
		variant.getPermaMods().remove(HULLMOD_ID_3);
		variant.getPermaMods().remove(HULLMOD_ID_4);
		variant.getHullMods().remove(HULLMOD_ID_1);
		variant.getHullMods().remove(HULLMOD_ID_2);
		variant.getHullMods().remove(HULLMOD_ID_3);
		variant.getHullMods().remove(HULLMOD_ID_4);
		refreshVariant();
		refreshButtonList();
	}

	//Makes the button not clickable if mod cannot be fitted
	@Override
	public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return (variant.hasHullMod(HULLMOD_ID_1) || variant.hasHullMod(HULLMOD_ID_2) 
				|| variant.hasHullMod(HULLMOD_ID_3) || variant.hasHullMod(HULLMOD_ID_4));
	}

	@Override
	public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return (variant.hasHullMod(HULLMOD_ID_1) || variant.hasHullMod(HULLMOD_ID_2) 
				|| variant.hasHullMod(HULLMOD_ID_3) || variant.hasHullMod(HULLMOD_ID_4));
	}
}