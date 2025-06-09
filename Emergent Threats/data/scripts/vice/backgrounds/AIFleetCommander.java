package data.scripts.vice.backgrounds;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.campaign.FactionSpecAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import exerelin.campaign.backgrounds.BaseCharacterBackground;
import exerelin.utilities.NexFactionConfig;

public class AIFleetCommander extends BaseCharacterBackground {
	
	private static String SKILL_ID = "vice_ai_commander";
	private static String SKILL_NAME = "AI Fleet Commander";

	@Override
	public boolean shouldShowInSelection(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
		return true;
	}

	@Override
	public void onNewGameAfterTimePass(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
		Global.getSector().getPlayerPerson().getStats().setSkillLevel(SKILL_ID, 1f);
		Global.getSector().getMemoryWithoutUpdate().set("$vice_ai_commander_bg_start", true);
	}

	@Override
	public void addTooltipForSelection(TooltipMakerAPI tooltip, FactionSpecAPI factionSpec, NexFactionConfig factionConfig, Boolean expanded) {
		super.addTooltipForSelection(tooltip, factionSpec, factionConfig, expanded);

		if (expanded) {
			String s = "Gain the AI Fleet Commander skill, which reduces deployment cost for AI ships in your fleet.";
            tooltip.addSpacer(10f);
            tooltip.addPara(s, 0f, Misc.getTextColor(), Misc.getHighlightColor(), SKILL_NAME);
		}
	}
}