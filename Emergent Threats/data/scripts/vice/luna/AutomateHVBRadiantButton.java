package data.scripts.vice.luna;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import com.fs.starfarer.api.fleet.FleetMemberType;

import lunalib.lunaRefit.BaseRefitButton;
import lunalib.lunaUI.elements.LunaSpriteElement;

public class AutomateHVBRadiantButton extends BaseRefitButton {
	
	private static String HVB_HULL = "vice_radiant_hvb";
	private static String HVB_D_HULL = "vice_radiant_hvb_default_D";
	private static String AUTO_HULL = "vice_radiant_hvb_auto";
	
	private static String EX_MOD = "vice_experimental_hull_hvb";
	private static String OLD_MOD = "vice_ai_subsystem_integration";
	
	@Override
	public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "Automate Radiant (EX)";
	}
	
	@Override
	public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "graphics/hullmods/automated.png";
	}
	
	@Override
	public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
		return 1;
	}
	
	@Override
	public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return true;
	}
	
	@Override
	public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		tooltip.addPara("Automate Radiant (EX)", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
		tooltip.addSpacer(5f);

		tooltip.addPara("Spend 3 Story Points to convert the Radiant (EX) into a fully automated hull. This process cannot be reversed.", 0f);
		
		if (market == null || !market.hasSpaceport()) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Must be docked at a spaceport.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}

		else if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 3) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Insufficient Story Points", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
	}
	
	@Override
	public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {    variant.setHullSpecAPI(Global.getSettings().getHullSpec(AUTO_HULL));
		//variant.getPermaMods().remove(EX_MOD);
		//variant.getPermaMods().remove(OLD_MOD);
		variant.getHullMods().remove(EX_MOD);
		variant.getHullMods().remove(OLD_MOD);
		variant.autoGenerateWeaponGroups();
		Global.getSector().getCharacterData().getPerson().getStats().spendStoryPoints(3, false, null, false, "");
		refreshVariant();
		refreshButtonList();
	}
	
	@Override
	public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {		
		if (market == null || !market.hasSpaceport()) return false;
		if (Global.getSector().getCharacterData().getPerson().getStats().getStoryPoints() < 3) return false;
		return true;
	}
	
	@Override
	public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return variant.hasHullMod(EX_MOD) ;
	}
}