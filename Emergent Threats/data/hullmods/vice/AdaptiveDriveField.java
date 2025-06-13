package data.hullmods.vice;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

import data.scripts.vice.util.RemnantSubsystemsUtil;

public class AdaptiveDriveField extends BaseLogisticsHullMod {

	private static int BURN_LEVEL_BONUS = 1;
	private static String CONFLICT_MOD = "augmentedengines";
	
	//Utility variables
	private RemnantSubsystemsUtil util = new RemnantSubsystemsUtil();
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		if (!isApplicableToShip(ship) && ship.getOwner() == 0) ship.getVariant().getHullMods().remove(id);
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains(CONFLICT_MOD)) return false;
		return (util.isApplicable(ship) && util.isOnlyRemnantMod(ship));
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains(CONFLICT_MOD)) {
			return "Incompatible with Augmented Drive Field";
		}
		if (!util.isApplicable(ship)) return util.getIncompatibleCauseString("manufacturer");
		if (!util.isOnlyRemnantMod(ship)) return util.getIncompatibleCauseString("modcount");
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + BURN_LEVEL_BONUS;
		return null;
	}
}

