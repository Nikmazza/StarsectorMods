package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;

import data.scripts.vice.util.RemnantSubsystemsUtil;

public class AdaptiveTrajectoryAnalyzer extends BaseHullMod {
	
	private static float RANGE_BONUS_COMPOSITE = 50f;
	private static float RANGE_BONUS_MISSILE = 25f;
	private static String THIS_MOD = "magellan_trajectory_analyzer";
	private static String CONFLICT_MOD = "tw_modernized_rangefinder";
	private static String CONFLICT_MOD_2 = "magellan_trajectory_analyzer";
	private static String ARCHAIC = "archaic_c";

	//Utility variables
	private RemnantSubsystemsUtil util = new RemnantSubsystemsUtil();
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMissileWeaponRangeBonus().modifyPercent(id, RANGE_BONUS_MISSILE);
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		if (!isApplicableToShip(ship) && ship.getOwner() == 0) ship.getVariant().getHullMods().remove(id);
		else ship.addListener(new AdaptiveArchaicRangeModifier());
	}
	
	public static class AdaptiveArchaicRangeModifier implements WeaponRangeModifier {
		public AdaptiveArchaicRangeModifier() {}
		
		public float getWeaponRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			//if (weapon.getType() != WeaponType.COMPOSITE || !weapon.getSpec().hasTag(ARCHAIC)) return 0f;
			if (!weapon.getSpec().hasTag(ARCHAIC)) return 0f;
			else return RANGE_BONUS_COMPOSITE;
		}
		public float getWeaponRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			return 0f;
		}
		public float getWeaponRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		if (!util.isApplicable(ship) || !util.isOnlyRemnantMod(ship)) return false;
		return !ship.getVariant().hasHullMod(CONFLICT_MOD) && !ship.getVariant().hasHullMod(CONFLICT_MOD_2);
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (!util.isApplicable(ship)) return util.getIncompatibleCauseString("manufacturer");
		if (!util.isOnlyRemnantMod(ship)) return util.getIncompatibleCauseString("modcount");
		if (ship.getVariant().hasHullMod(CONFLICT_MOD) || ship.getVariant().hasHullMod(CONFLICT_MOD_2)) return "Comparable system already present";
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "archaic";
		if (index == 1) return "" + (int) RANGE_BONUS_COMPOSITE + "%";
		if (index == 2) return "" + (int) RANGE_BONUS_MISSILE + "%";
		return null;
	}
}