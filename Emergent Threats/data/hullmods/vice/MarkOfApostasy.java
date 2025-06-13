package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.Misc;

public class MarkOfApostasy extends BaseHullMod {
	
	public static float CR_PENALTY = 100f;
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxCombatReadiness().modifyFlat(id, -CR_PENALTY * 0.01f, "Apostate");
	}
}