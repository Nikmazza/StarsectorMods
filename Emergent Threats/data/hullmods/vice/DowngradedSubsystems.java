package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class DowngradedSubsystems extends BaseHullMod {
	
	private static float PERFORMANCE_PENALTY = 20f;
	private static float MAINTENANCE_REDUCTION = 20f;
	private static float DP_REDUCTION = 5f;
	
	private static String AQ_HULLMOD = "vice_aquilon_microforge";
	private static String IX_HULLMOD = "ix_aquilon_microforge";
	private static String CONFLICT_MOD = "efficiency_overhaul";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getBallisticWeaponRangeBonus().modifyMult(id, 1f - PERFORMANCE_PENALTY * 0.01f);
		stats.getEnergyWeaponRangeBonus().modifyMult(id, 1f - PERFORMANCE_PENALTY * 0.01f);
		stats.getFluxCapacity().modifyMult(id, 1f - PERFORMANCE_PENALTY * 0.01f);
		stats.getFluxDissipation().modifyMult(id, 1f - PERFORMANCE_PENALTY * 0.01f);

		stats.getFuelUseMod().modifyMult(id, 1f - MAINTENANCE_REDUCTION * 0.01f);
		stats.getSuppliesPerMonth().modifyMult(id, 1f - MAINTENANCE_REDUCTION * 0.01f);

		stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, -DP_REDUCTION);
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(CONFLICT_MOD)) return false;
		return (ship.getVariant().hasHullMod(AQ_HULLMOD) || ship.getVariant().hasHullMod(IX_HULLMOD));
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (!ship.getVariant().hasHullMod(AQ_HULLMOD) && !ship.getVariant().hasHullMod(IX_HULLMOD)) return "Can only be applied to Aquilon-class Battlecruiser";
		if (ship.getVariant().hasHullMod(CONFLICT_MOD)) return "Incompatible with Efficiency Overhaul";
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) PERFORMANCE_PENALTY + "%";
		if (index == 1) return "" + (int) MAINTENANCE_REDUCTION + "%";
		if (index == 2) return "" + (int) DP_REDUCTION;
		return null;
	}
}