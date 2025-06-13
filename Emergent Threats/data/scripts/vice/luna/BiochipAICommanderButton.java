package data.scripts.vice.luna;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import lunalib.lunaRefit.BaseRefitButton;

public class BiochipAICommanderButton extends BaseRefitButton {

	private static String BIOCHIP_SKILL_ID = "vice_ai_commander";
	private static String BIOCHIP_ID = "vice_biochip_ai_commander";
	
	@Override
	public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "Biochip (AI Fleet Commander)";
	}

	@Override
	public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
		return "graphics/vice/icons/mayfly_biochip.png";
	}

	@Override
	public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
		return 4999;
	}

	@Override
	public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return true;
	}

	@Override
	public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		tooltip.addPara("Cerebral Biochip (AI Fleet Commander)", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
		tooltip.addSpacer(5f);

		tooltip.addPara("Implant the fleet commander with a biochip, granting the AI Fleet Command skill.", 0f);
		
		if (member.getCaptain() != null && member.getCaptain().getStats().hasSkill(BIOCHIP_SKILL_ID)) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Skill has been applied.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
		
		else if (!isValidCaptain(member.getCaptain())) {
			tooltip.addSpacer(10f);
			tooltip.addPara("Can only be applied to the fleet commander on the flagship's fitting screen.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
		}
	}

	@Override
	public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {
		PersonAPI person = member.getCaptain();
		person.getStats().increaseSkill(BIOCHIP_SKILL_ID);
		if (person.equals(Global.getSector().getPlayerPerson())) person.getStats().increaseSkill(BIOCHIP_SKILL_ID);
		removeBiochip();
		refreshButtonList();
	}

	//Makes the button not clickable if mod cannot be fitted
	@Override
	public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		if (variant.hasHullMod("automated")) return false;
		if (!isValidCaptain(member.getCaptain())) return false;
		return hasBiochip();
	}

	@Override
	public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
		return hasBiochip();
	}
	
	private boolean isValidCaptain(PersonAPI person) {
		if (person == null || !person.isPlayer()) return false;
		return !person.getStats().hasSkill(BIOCHIP_SKILL_ID);
	}
	
	private boolean hasBiochip() {
		boolean hasChip = false;
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		for (CargoStackAPI s : cargo.getStacksCopy()) {
			if (s.isSpecialStack() && s.getSpecialItemSpecIfSpecial().getId().equals(BIOCHIP_ID)) {
				hasChip = true;
			}
		}
		return hasChip;
	}
	
	private void removeBiochip() {
		boolean hasDeleted = false;
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		for (CargoStackAPI s : cargo.getStacksCopy()) {
			if (s.isSpecialStack() && s.getSpecialItemSpecIfSpecial().getId().equals(BIOCHIP_ID)) {
				if (!hasDeleted) {
					s.subtract(1f);
					hasDeleted = true;
				}
			}
		}
	}
}