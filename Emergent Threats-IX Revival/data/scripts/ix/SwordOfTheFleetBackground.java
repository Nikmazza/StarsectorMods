package data.scripts.ix;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import exerelin.campaign.backgrounds.BaseCharacterBackground;
import exerelin.utilities.NexFactionConfig;

public class SwordOfTheFleetBackground extends BaseCharacterBackground {
	
	private static String IX_FACTION_ID = "ix_battlegroup";
	private static String IX_SKILL_ID = "ix_sword_of_the_fleet";
	private static String IX_SKILL_NAME = "Sword of the Fleet";

	@Override
	public boolean shouldShowInSelection(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
		return (factionConfig.factionId.equals("player"));
	}

	@Override
	public void onNewGameAfterTimePass(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
		Global.getSector().getPlayerPerson().getStats().setSkillLevel(IX_SKILL_ID, 2f);
		Global.getSector().getFaction(IX_FACTION_ID).setRelationship("player", 0.60f);
	}

	@Override
	public void addTooltipForSelection(TooltipMakerAPI tooltip, FactionSpecAPI factionSpec, NexFactionConfig factionConfig, Boolean expanded) {
		super.addTooltipForSelection(tooltip, factionSpec, factionConfig, expanded);

		if (expanded) {
			String s = "Gain the %s skill and %s starting reputation with the IX Battlegroup";
            tooltip.addSpacer(10f);
            tooltip.addPara(s, 0f, Misc.getTextColor(), Misc.getHighlightColor(), IX_SKILL_NAME, "60");
		}
	}
}