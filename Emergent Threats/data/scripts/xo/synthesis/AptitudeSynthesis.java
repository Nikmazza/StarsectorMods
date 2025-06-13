package data.scripts.xo.synthesis;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import second_in_command.SCData;
import second_in_command.specs.SCAptitudeSection;
import second_in_command.specs.SCBaseAptitudePlugin;

import lunalib.lunaSettings.LunaSettings;

public class AptitudeSynthesis extends SCBaseAptitudePlugin {
	
	private static String IN_FAC_ID = "independent";
	private static String IX_FAC_ID = "ix_battlegroup";
	private static String TW_FAC_ID = "ix_trinity";
	private static String TT_FAC_ID = "tritachyon";

    //The ID of the skill that is always active
    @Override
    public String getOriginSkillId() {
        return "xo_synthesis_subsystem_integration";
    }
	
	@Override
	public void addCodexDescription(TooltipMakerAPI tooltip) {
        tooltip.addPara("Synthesis enables the unrestricted use of AI Adaptive Subsystems, specialized hullmods normally limited either to Remnant automated hulls, or individual subsystem integrated ships. Depending on skill choice, the aptitude can also reduce the fleet wide CR penalty for using IX Battlegroup Panoptic Interfaces, as well as enable the use of a small number of automated ships. \n\nSynthesis officers can be recruited from the IX Battlegroup, Trinity Worlds, Tri-Tachyon, and occasionally on independent worlds.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Synthesis", "AI Adaptive Subsystems");
    }
	
    //Determines which skills are added and how they are sectioned off in the UI
    @Override
    public void createSections() {
		
        SCAptitudeSection section1 = new SCAptitudeSection(true, 0, "technology1");
		section1.addSkill("xo_synthesis_command_subroutine");
		section1.addSkill("xo_synthesis_drone_tactics");
		section1.addSkill("xo_synthesis_dynamic_shields");
		section1.addSkill("xo_synthesis_mind_machine_interface");
		section1.addSkill("xo_synthesis_reactor_monitoring");
		section1.addSkill("xo_synthesis_spacetime_analytics");
        addSection(section1);

        SCAptitudeSection section2 = new SCAptitudeSection(false, 3, "technology3");
		section2.addSkill("xo_synthesis_compact_automation");
		section2.addSkill("xo_synthesis_predictive_targeting");
        addSection(section2);
		
        SCAptitudeSection section3 = new SCAptitudeSection(false, 4, "technology5");
		section3.addSkill("xo_synthesis_temporal_accelerator");
		section3.addSkill("xo_synthesis_total_integration");
        addSection(section3);
    }

    public Float getMarketSpawnweight(MarketAPI market){
        float weight = spec.getSpawnWeight();
		String id = "";
		if (market.getFaction() != null) id = market.getFaction().getId();
		
        if (IX_FAC_ID.equals(id)) weight *= 2f;
		else if (TW_FAC_ID.equals(id)) weight *= 2f;
        else if (TT_FAC_ID.equals(id)) weight *= 1f;
		else if (IN_FAC_ID.equals(id)) weight *= 0.5f;
		else weight = 0f;
        return weight;
    }

    @Override
    public Float getNPCFleetSpawnWeight(SCData data, CampaignFleetAPI fleet) {
		String id = "";
		boolean isSynthesisEnabled = LunaSettings.getBoolean("EmergentThreats_Vice", "vice_synthesisEnabled");
		if (fleet.getFaction() != null) id = fleet.getFaction().getId();
        if (IX_FAC_ID.equals(id)) return isSynthesisEnabled ? Float.MAX_VALUE : 0f;
        else if (TW_FAC_ID.equals(id)) return isSynthesisEnabled ? Float.MAX_VALUE : 0f;
		else if (TT_FAC_ID.equals(id)) return isSynthesisEnabled ? 2f : 0f;
		else if ("vantage_group".equals(id)) return Float.MAX_VALUE; //hvb boss
        return 0f;
    }
}