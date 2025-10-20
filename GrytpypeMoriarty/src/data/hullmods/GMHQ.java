package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class GMHQ extends BaseHullMod {

	private static final float DISSIPATION_MULT = 1.25f;
    private static final float CAPACITY_MULT = 1.25f;
	private static final float HANDLING_MULT = 1.25f;
	private static final float CR_DEG_MULT = 4f;
	private static final float CAPACITY_PENALTY = 0.20f;
    private static final float DEPLOY_MULT= 1.50f;
    private static final float LOGISTICS_PENALTY = 25f;

    @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxDissipation().modifyMult(id, DISSIPATION_MULT);
		stats.getAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getDeceleration().modifyMult(id, HANDLING_MULT);
		stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
		stats.getTurnAcceleration().modifyMult(id, HANDLING_MULT);
        stats.getFluxCapacity().modifyMult(id, CAPACITY_MULT);
        stats.getCargoMod().modifyMult(id, CAPACITY_PENALTY);
        stats.getFuelMod().modifyMult(id, CAPACITY_PENALTY);
        stats.getCRPerDeploymentPercent().modifyMult(id, DEPLOY_MULT);
		stats.getCRLossPerSecondPercent().modifyMult(id, CR_DEG_MULT);
        stats.getSuppliesPerMonth().modifyPercent(id, LOGISTICS_PENALTY);
        stats.getSuppliesToRecover().modifyPercent(id, LOGISTICS_PENALTY);
	}



	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((DISSIPATION_MULT - 1f) * 100f)+ "%"; // + Strings.X;
        if (index == 1) return "" + (int) ((HANDLING_MULT- 1f) * 100f)+ "%"; // + Strings.X;
        if (index == 2) return "" + (int) ((CR_DEG_MULT)); // + Strings.X;
        if (index == 3) return "" + (int) ((1f - CAPACITY_PENALTY) * 100f)+ "%"; // + Strings.X;
        if (index == 4) return "" + (int) ((DEPLOY_MULT - 1f) * 100f)+ "%"; // + Strings.X;
        if (index == 5) return "" + (int) ((LOGISTICS_PENALTY))+ "%"; // + Strings.X;

		return null;
	}
}