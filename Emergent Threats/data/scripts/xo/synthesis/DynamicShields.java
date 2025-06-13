package data.scripts.xo.synthesis;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

public class DynamicShields extends SCBaseSkillPlugin {
    
	//reactive combat shields 10% damage reduction bonus handled by hullmod class
	private static float HARD_SHIELD_BONUS = 6.2f; //1 * 80% * 93.8% ≈ 0.75
	
	@Override
    public String getAffectsString() {
        return "all ships in the fleet";
    }
	
    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
		tooltip.addPara("Hardened Shields damage reduction improved by an additional 5%%", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("Reactive Combat Shields damage reduction improved by an additional 10%% when active", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("Acquire the Flux Dissipator adaptive hullmod", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Flux Dissipator");
    }

    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
        if (variant.hasHullMod("hardenedshieldemitter")) {
			stats.getShieldDamageTakenMult().modifyMult(id, 1f - HARD_SHIELD_BONUS * 0.01f);
		}
		else if (variant.hasHullMod("ix_reactive_combat_shields") && data.isNPC()) variant.addMod("ix_reactive_checker");
    }
	
	@Override
	public void onActivation(SCData data) {
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_dynamic_shields_is_active", true);
		if (data.isPlayer() && !Global.getSector().getMemoryWithoutUpdate().is("$gave_DS_hullmods", true)) {
			CharacterDataAPI player = Global.getSector().getCharacterData();
			player.addHullMod("vice_adaptive_flux_dissipator");
			Global.getSector().getMemoryWithoutUpdate().set("$gave_DS_hullmods", true);
		}
	}
	
	@Override
	public void onDeactivation(SCData data) {
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_dynamic_shields_is_active", false);
	}
}
