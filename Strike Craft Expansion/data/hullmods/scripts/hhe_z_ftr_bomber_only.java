package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class hhe_z_ftr_bomber_only extends BaseHullMod {

	public static final float FIGHTER_COST_PENALTY = 1000f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.FIGHTER_COST_MOD).modifyFlat(id, FIGHTER_COST_PENALTY);
		stats.getDynamic().getMod(Stats.INTERCEPTOR_COST_MOD).modifyFlat(id, FIGHTER_COST_PENALTY);
//		stats.getDynamic().getMod(Stats.BOMBER_COST_MOD).modifyFlat(id, FIGHTER_COST_PENALTY);
		stats.getDynamic().getMod(Stats.SUPPORT_COST_MOD).modifyFlat(id, FIGHTER_COST_PENALTY);
	}
	
	@Override
	public boolean affectsOPCosts() {
		return true;
	}
}

