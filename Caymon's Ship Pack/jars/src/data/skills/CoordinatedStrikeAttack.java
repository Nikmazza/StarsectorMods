package data.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

import java.util.ArrayList;
import java.util.List;

public class CoordinatedStrikeAttack extends SCBaseSkillPlugin {

	public static float FLUX_PER_OP = 1f;
	public static float CAP_PER_OP = 10;

	public String getAffectsString() {
		return "all ships with fighter bays";
	}

	public void addTooltip(SCData scData, TooltipMakerAPI tooltipMakerAPI) {
		tooltipMakerAPI.addPara("+1 flux dissipation per ordnance point spend on fighters", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltipMakerAPI.addPara("+10 flux capacity per ordnance point spend on fighters", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
	}

	public static int computeFighterOPCost(MutableShipStatsAPI stats) {
		int totalOp = 0;
		for (String wingId : stats.getVariant().getFittedWings()){
			totalOp += Global.getSettings().getFighterWingSpec(wingId).getOpCost(stats);
		}
		return totalOp;
	}

	public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id){

			if (stats.getVariant() != null) {
				float flux = FLUX_PER_OP * computeFighterOPCost(stats);
				stats.getFluxDissipation().modifyFlat(id, flux);
			}
		if (stats.getVariant() != null) {
			float flux = CAP_PER_OP * computeFighterOPCost(stats);
			stats.getFluxCapacity().modifyFlat(id, flux);
		}
	}
}











