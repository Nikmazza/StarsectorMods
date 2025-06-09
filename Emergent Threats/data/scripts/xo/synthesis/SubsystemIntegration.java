package data.scripts.xo.synthesis;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

import data.scripts.vice.listeners.EnemyEncounterListener;
import data.scripts.vice.util.RemnantSubsystemsUtil;

public class SubsystemIntegration extends SCBaseSkillPlugin {
    
	private static float CR_BOOST = 0.05f;
	private RemnantSubsystemsUtil util = new RemnantSubsystemsUtil();
	
	@Override
    public String getAffectsString() {
        return "all ships in the fleet";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
        tooltip.addPara("All ships can use AI Adaptive Subsystems, ships that can already do so gain 5%% to combat readiness", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("Acquire the AI Subsystem Integration hullmod", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "AI Subsystem Integration");
    }

    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
        if (util.isApplicableWithoutSynthesis(variant)) stats.getMaxCombatReadiness().modifyFlat(id, CR_BOOST, "Synthesis executive officer");
		
    }

	@Override
    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) {
		
    }
	
	@Override
	public void onActivation(SCData data) {
		if (data.isNPC()) data.getFleet().getMemoryWithoutUpdate().set("$xo_synthesis_fleet", true);
		if (data.isPlayer() && !Global.getSector().getMemoryWithoutUpdate().is("$gave_SI_hullmods", true)) {
			CharacterDataAPI player = Global.getSector().getCharacterData();
			player.addHullMod("vice_ai_subsystem_integration");
			Global.getSector().getMemoryWithoutUpdate().set("$gave_SI_hullmods", true);
		}
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_synthesis_is_active", true);
	}
	
	@Override
	public void onDeactivation(SCData data) {
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_synthesis_is_active", false);
	}
}
