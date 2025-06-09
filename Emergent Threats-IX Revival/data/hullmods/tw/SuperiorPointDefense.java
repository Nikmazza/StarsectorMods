package data.hullmods.tw;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class SuperiorPointDefense extends BaseHullMod {

	private static float COST_REDUCTION_S  = 1;
	private static float COST_REDUCTION_M  = 3;
	private static float COST_REDUCTION_L  = 5;
	private static float PD_RANGE_BONUS_FLAT = 50;
	
	private static float DAMAGE_BONUS = 50f;
	private static String CONFLICT_MOD = "pointdefenseai";
	private static String CONFLICT_MOD_NAME = "Integrated Point Defense AI";
	
	private static boolean hasConflictMod = false;
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (stats.getVariant().getHullMods().contains(CONFLICT_MOD)) {
			hasConflictMod = true;
			return;
		}
		stats.getDynamic().getMod(Stats.SMALL_PD_MOD).modifyFlat(id, -COST_REDUCTION_S);
		stats.getDynamic().getMod(Stats.MEDIUM_PD_MOD).modifyFlat(id, -COST_REDUCTION_M);
		stats.getDynamic().getMod(Stats.LARGE_PD_MOD).modifyFlat(id, -COST_REDUCTION_L);
		
		stats.getNonBeamPDWeaponRangeBonus().modifyFlat(id, PD_RANGE_BONUS_FLAT);
		stats.getDynamic().getMod(Stats.PD_IGNORES_FLARES).modifyFlat(id, 1f);
		stats.getDynamic().getMod(Stats.PD_BEST_TARGET_LEADING).modifyFlat(id, 1f);
		stats.getDamageToMissiles().modifyPercent(id, DAMAGE_BONUS);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		String s = hasConflictMod ? CONFLICT_MOD_NAME + ", hullmod deactivated": CONFLICT_MOD_NAME;
		if (index == 0) return "" + (int) COST_REDUCTION_S + "";
		if (index == 1) return "" + (int) COST_REDUCTION_M + "";
		if (index == 2) return "" + (int) COST_REDUCTION_L + "";
		if (index == 3) return "" + (int) PD_RANGE_BONUS_FLAT + "";
		if (index == 4) return "" + (int) DAMAGE_BONUS + "%";
		if (index == 5) return s;
		return null;
	}

	@Override
	public boolean affectsOPCosts() {
		return true;
	}
}