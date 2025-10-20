package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.PhaseCloakStats;
import org.magiclib.util.MagicIncompatibleHullmods;

public class jdp_maerulanphasecoils extends BaseHullMod {

	public static float FLUX_THRESHOLD_INCREASE_PERCENT = 100f;
	public static float PHASE_COOLDOWN_REDUCTION = 80f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getPhaseCloakCooldownBonus().modifyMult(id, 1f - PHASE_COOLDOWN_REDUCTION / 100f);
		stats.getDynamic().getMod(
				Stats.PHASE_CLOAK_FLUX_LEVEL_FOR_MIN_SPEED_MOD).modifyPercent(id, FLUX_THRESHOLD_INCREASE_PERCENT);

		//Incompatibilities
		if(stats.getVariant().getHullMods().contains("adaptive_coils")){
			//if someone tries to install adaptive_coils, remove it
			MagicIncompatibleHullmods.removeHullmodWithWarning(
					stats.getVariant(),
					"adaptive_coils",
					"jdp_maerulanphasecoils"
			);
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round(PHASE_COOLDOWN_REDUCTION) + "%";
		if (index == 1) return "" + (int) Math.round(FLUX_THRESHOLD_INCREASE_PERCENT) + "%";
		return null;
	}
}

