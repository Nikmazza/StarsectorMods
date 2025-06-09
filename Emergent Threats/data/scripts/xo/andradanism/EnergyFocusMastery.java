package data.scripts.xo.andradanism;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

public class EnergyFocusMastery extends SCBaseSkillPlugin {
    
	private static float PULSE_RANGE_BONUS = 100f;
	
	@Override
    public String getAffectsString() {
        return "all non-automated ships with an Energy or Modular Bolt Coherer";
    }
    
	@Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
		tooltip.addPara("Every well aimed shot takes us one step closer to victory.", 0f, Misc.getTextColor(), Misc.getHighlightColor());
		tooltip.addPara("  -Quotations from the Supreme Executor", 0f, Misc.getTextColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("Energy and Modular Bolt Coherer range bonus increased to 200 su for crewed ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("Gain access to the Modular Bolt Coherer hullmod", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
	}

    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
		if (variant.hasHullMod("coherer") && !variant.hasHullMod("automated")) {
			stats.getEnergyWeaponRangeBonus().modifyFlat(id, PULSE_RANGE_BONUS);
			stats.getBeamWeaponRangeBonus().modifyFlat(id, -PULSE_RANGE_BONUS);
		}
    }
	
	//memkey for hullmod display text only
	@Override
	public void onActivation(SCData data) {
		if (data.isPlayer()) {
			CharacterDataAPI player = Global.getSector().getCharacterData();
			player.addHullMod("vice_modular_bolt_coherer");
			Global.getSector().getMemoryWithoutUpdate().set("$xo_energy_focus_mastery_is_active", true);
		}
	}
	
	@Override
	public void onDeactivation(SCData data) {
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_energy_focus_mastery_is_active", false);
	}
}