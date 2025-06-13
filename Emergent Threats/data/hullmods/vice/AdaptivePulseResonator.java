package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import data.scripts.vice.util.RemnantSubsystemsUtil;

public class AdaptivePulseResonator extends BaseHullMod {

	private static float PULSE_RANGE_BONUS = 100f;
	private static float PULSE_RANGE_BONUS_XO = 200f; //text only, actual bonus handled by XO Spacetime Analytics
	private static String MBC_ID = "vice_modular_bolt_coherer";
	
	//Utility variables
	private RemnantSubsystemsUtil util = new RemnantSubsystemsUtil();
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getEnergyWeaponRangeBonus().modifyFlat(id, PULSE_RANGE_BONUS);
		stats.getBeamWeaponRangeBonus().modifyFlat(id, -PULSE_RANGE_BONUS);
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		if (!isApplicableToShip(ship) && ship.getOwner() == 0) ship.getVariant().getHullMods().remove(id);
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		if (util.hasDriveField(ship)) return false;
		if (ship.getVariant().hasHullMod("coherer") || ship.getVariant().hasHullMod(MBC_ID)) return false;
		return (util.isApplicable(ship) && util.isOnlyRemnantMod(ship));
	}
	
	private boolean isPredictiveTargetingActive() {
		return (Global.getSector().getMemoryWithoutUpdate().is("$xo_predictive_targeting_is_active", true));
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (util.hasDriveField(ship)) return util.getIncompatibleCauseString("drivefield");
		if (ship.getVariant().hasHullMod("coherer") || ship.getVariant().hasHullMod(MBC_ID)) return "Incompatible with Energy Bolt Coherer";
		if (!util.isApplicable(ship)) return util.getIncompatibleCauseString("manufacturer");
		if (!util.isOnlyRemnantMod(ship)) return util.getIncompatibleCauseString("modcount");
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		float range = isPredictiveTargetingActive() ? PULSE_RANGE_BONUS_XO : PULSE_RANGE_BONUS;
		if (index == 0) return "" + (int) range;
		
		return null;
	}
}
