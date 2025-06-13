package data.scripts.xo.andradanism;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

public class ForwardToVictory extends SCBaseSkillPlugin {
    
	private static float SENSOR_BONUS_S = 15f;
	private static float SENSOR_BONUS_M = 30f;
	private static float SENSOR_BONUS_L = 45f;
	private static float SENSOR_BONUS_C = 75f;
	
	@Override
    public String getAffectsString() {
        return "all ships in the fleet";
    }
    
	@Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
		tooltip.addPara("Strike first, strike fast, and you shall always know victory.", 0f, Misc.getTextColor(), Misc.getHighlightColor());
		tooltip.addPara("  -Quotations from the Supreme Executor", 0f, Misc.getTextColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("+1 increased maximum burn for ships with less than 9 burn speed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("Ship sensor strength increased by 15/30/45/75, based on hull size", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("Maximum burn bonus is exclusive with Tactical aptitude Rapid Response skill", 0f, Misc.getNegativeHighlightColor(), Misc.getHighlightColor());
	}

    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
		float bonus = 0f;
		if (hullSize.equals(HullSize.FRIGATE)) bonus = SENSOR_BONUS_S;
		else if (hullSize.equals(HullSize.DESTROYER)) bonus = SENSOR_BONUS_M;
		else if (hullSize.equals(HullSize.CRUISER)) bonus = SENSOR_BONUS_L;
		else if (hullSize.equals(HullSize.CAPITAL_SHIP)) bonus = SENSOR_BONUS_C;
		stats.getSensorStrength().modifyFlat(id, bonus);
		
		if (data.isSkillActive("sc_tactical_rapid_response")) return;
		if (stats.getMaxBurnLevel().getModifiedValue() < 9f) stats.getMaxBurnLevel().modifyFlat(id, 1f);
		
    }
}