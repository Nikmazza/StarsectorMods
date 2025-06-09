package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class hhe_z_overload_reduction extends BaseHullMod {

	public static final float HHE_OVERLOAD_REDUCTION = 33f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getOverloadTimeMod().modifyMult(id, 1f - (HHE_OVERLOAD_REDUCTION / 100f));
	}
}
