package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.rpg.Person;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;


public class jdp_GrantSkill extends BaseCommandPlugin {
	private TextPanelAPI textPanel;

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		this.textPanel = dialog.getTextPanel();

		textPanel.addPara("Acquired a new skill.", Misc.getHighlightColor(), Misc.getHighlightColor());

		TooltipMakerAPI tooltip = textPanel.beginTooltip();

		tooltip.setParaFont(Fonts.ORBITRON_12);
		tooltip.addPara("(Hover over the icon for a detailed description)", 0f, Misc.getGrayColor(), Misc.getGrayColor());
		Person fake = (Person) Global.getFactory().createPerson();
		fake.setFaction("player");
		fake.getStats().setSkillLevel("jdp_omegakin", 2f);
		tooltip.addSkillPanel(fake, 0f);

		textPanel.addTooltip();

		Global.getSector().getPlayerPerson().getStats().setSkillLevel("jdp_omegakin", 2f);
		return true;
	}
}








