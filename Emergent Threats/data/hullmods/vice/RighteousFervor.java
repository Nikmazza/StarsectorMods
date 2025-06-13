package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.Misc;

public class RighteousFervor extends BaseHullMod {
	
	private static float FIGHTER_REPLACEMENT_BONUS = 10f;
	private static float SHIELDLESS_BONUS = 10f;
	private static String PENALTY_MOD = "vice_mark_of_apostasy";
	private static String HVB_MOD = "vice_mission_deloy_faith";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//if ship was received as gift, do not display HVB deploy mod
		if (Global.getSector().getMemoryWithoutUpdate().is("$vice_faith_fury_failed", true)) {
			stats.getVariant().getPermaMods().remove(HVB_MOD);
			stats.getVariant().getHullMods().remove(HVB_MOD);
		}
		//if sided with Rabasi Kato, sabotage the ship
		if (Global.getSector().getMemoryWithoutUpdate().is("$vice_project_mayfly_failed", true)) {
			stats.getVariant().getPermaMods().add(PENALTY_MOD);
			stats.getVariant().getPermaMods().remove(id);
			stats.getVariant().getHullMods().add(PENALTY_MOD);
			stats.getVariant().getHullMods().remove(id);
		}
		
		float bonus = FIGHTER_REPLACEMENT_BONUS;
		if (stats.getVariant().getHullSpec().getShieldType() == ShieldType.NONE) bonus += SHIELDLESS_BONUS;
		stats.getFighterRefitTimeMult().modifyMult(id, 1f - FIGHTER_REPLACEMENT_BONUS * 0.01f);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		float bonus = FIGHTER_REPLACEMENT_BONUS + SHIELDLESS_BONUS;
		if (index == 0) return "" + (int) FIGHTER_REPLACEMENT_BONUS + "%";
		if (index == 1) return "" + (int) bonus + "%";
		return null;
	}	
}