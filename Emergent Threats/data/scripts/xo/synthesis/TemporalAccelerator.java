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

public class TemporalAccelerator extends SCBaseSkillPlugin {
    
	private static float ROF_BONUS = 10f;
	private static float SYSTEM_COOLDOWN_BONUS = 15f;
	private static float TIME_ACCELERATION_BONUS = 5f;
	private static String PHASE_MOD_ID = "vice_adaptive_phase_coils";
	private static String TIME_MOD_ID = "vice_adaptive_temporal_shell";
	
	@Override
    public String getAffectsString() {
        return "all ships in the fleet";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
		tooltip.addPara("+10%% rate of fire for all weapons", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("Adaptive Phase Coils subsystem improves ship system recharge rate by 15%%", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltip.addPara("Adaptive Temporal Shell time flow bonus increased to 15%%", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("Acquire the Phase Coils and Temporal Shell adaptive hullmods", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Phase Coils", "Temporal Shell");
	}
	
    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
		stats.getBallisticRoFMult().modifyPercent(id, ROF_BONUS);
		stats.getEnergyRoFMult().modifyPercent(id, ROF_BONUS);
		stats.getMissileRoFMult().modifyPercent(id, ROF_BONUS);
		if (stats.getVariant().hasHullMod(PHASE_MOD_ID)) {
			stats.getSystemCooldownBonus().modifyMult(id, 1f - SYSTEM_COOLDOWN_BONUS * 0.01f);
		}
	}
	
	@Override
	public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, String id) {
		if (ship.getVariant().hasHullMod(TIME_MOD_ID)) {
			ship.getMutableStats().getTimeMult().modifyMult(id, 1f + TIME_ACCELERATION_BONUS * 0.01f);
		}
	}
	
	@Override
	public void onActivation(SCData data) {
		if (data.isPlayer() && !Global.getSector().getMemoryWithoutUpdate().is("$gave_TA_hullmods", true)) {
			CharacterDataAPI player = Global.getSector().getCharacterData();
			player.addHullMod("vice_adaptive_phase_coils");
			player.addHullMod("vice_adaptive_temporal_shell");
			Global.getSector().getMemoryWithoutUpdate().set("$gave_TA_hullmods", true);
		}
	}
}
