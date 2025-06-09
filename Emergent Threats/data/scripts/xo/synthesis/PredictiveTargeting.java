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

public class PredictiveTargeting extends SCBaseSkillPlugin {
    
	private static float BEAM_DAMAGE_BONUS = 5f;
	private static float BEAM_RANGE_BONUS = 100f;
	private static float PULSE_RANGE_BONUS = 100f;
	private static String BEAM_MOD_ID = "vice_adaptive_emitter_diodes";
	private static String PULSE_MOD_ID = "vice_adaptive_pulse_resonator";
	
	@Override
    public String getAffectsString() {
        return "all ships in the fleet";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
		tooltip.addPara("+5%% energy weapon damage", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("Adaptive Emitter Diodes range penalty negated", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("Adaptive Pulse Resonator range bonus increased to 200 su", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("Acquire the Emitter Diodes, Pulse Resonator, and Trajectory Analyzer adaptive hullmods", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Emitter Diodes", "Pulse Resonator", "Trajectory Analyzer");
    }

    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
		stats.getEnergyWeaponDamageMult().modifyPercent(id, BEAM_DAMAGE_BONUS);
		if (stats.getVariant().hasHullMod(BEAM_MOD_ID)) {
			stats.getBeamWeaponRangeBonus().modifyFlat(id, BEAM_RANGE_BONUS);
		}
		if (stats.getVariant().hasHullMod(PULSE_MOD_ID)) {
			stats.getEnergyWeaponRangeBonus().modifyFlat(id, PULSE_RANGE_BONUS);
			stats.getBeamWeaponRangeBonus().modifyFlat(id, -PULSE_RANGE_BONUS);
		}
    }
	
	@Override
	public void onActivation(SCData data) {
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_predictive_targeting_is_active", true);
		if (data.isPlayer() && !Global.getSector().getMemoryWithoutUpdate().is("$gave_PT_hullmods", true)) {
			CharacterDataAPI player = Global.getSector().getCharacterData();
			player.addHullMod("vice_adaptive_emitter_diodes");
			player.addHullMod("vice_adaptive_pulse_resonator");
			player.addHullMod("vice_adaptive_trajectory_analyzer");
			Global.getSector().getMemoryWithoutUpdate().set("$gave_PT_hullmods", true);
		}
	}
	
	@Override
	public void onDeactivation(SCData data) {
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_predictive_targeting_is_active", false);
	}
}
