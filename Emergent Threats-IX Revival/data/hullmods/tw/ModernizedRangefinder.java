package data.hullmods.tw;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;

public class ModernizedRangefinder extends BaseHullMod {
	
	private static float RANGE_BONUS_UNIF = 40f;
	private static float RANGE_BONUS_BASE = 50f;
	private static float RANGE_BONUS_GOOD = 60f;
	private static float RANGE_BONUS_CTC = 80f;
	private static float RANGE_BONUS_BEST = 100f;
	private static float RANGE_BONUS_FLAT = 200f;
	private static float DFC_BONUS_FLAT = 800f;
	private static String THIS_MOD = "tw_modernized_rangefinder";
	private static String CONFLICT_MOD = "magellan_trajectory_analyzer";
	private static String CONFLICT_MOD_2 = "vice_adaptive_trajectory_analyzer";
	private static String UTC_MOD_ID = "vice_unified_targeting_core";
	private static String DTC_MOD_ID = "dedicated_targeting_core";
	private static String ITU_MOD_ID = "targetingunit";
	private static String CTC_MOD_ID = "tahlan_centraltargeting";
	private static String ATC_MOD_ID = "advancedcore";
	private static String ARCHAIC = "archaic_c";
	private static String DFC = "distributed_fire_control";
	private static String DFC_NAME = "Distributed Fire Control";
	
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.addListener(new CompositeRangeModifier());
	}
	
	public static class CompositeRangeModifier implements WeaponRangeModifier {
		public CompositeRangeModifier() {}
		
		public float getWeaponRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			//if (weapon.getType() != WeaponType.COMPOSITE || !weapon.getSpec().hasTag(ARCHAIC)) return 0f;
			if (!weapon.getSpec().hasTag(ARCHAIC)) return 0f;
			if (ship.getVariant().hasHullMod(DTC_MOD_ID)) {
				if (ship.getVariant().getSMods().contains(DTC_MOD_ID)) return RANGE_BONUS_GOOD;
				else return RANGE_BONUS_BASE;
			}
			else if (ship.getVariant().hasHullMod(UTC_MOD_ID)) return RANGE_BONUS_UNIF;
			else if (ship.getVariant().hasHullMod(ITU_MOD_ID)) return RANGE_BONUS_GOOD;
			else if (ship.getVariant().hasHullMod(CTC_MOD_ID)) return RANGE_BONUS_CTC;
			else if (ship.getVariant().hasHullMod(ATC_MOD_ID)) return RANGE_BONUS_BEST;
			return 0f;
		}
		
		public float getWeaponRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			float bonus = 0f;
			boolean isDFC = ship.getVariant().hasHullMod(DFC);
			boolean isSMOD = ship.getVariant().getSMods().contains(THIS_MOD);
			if (isSMOD && (weapon.getType() == WeaponType.MISSILE 
							|| weapon.getType() == WeaponType.COMPOSITE)) bonus = RANGE_BONUS_FLAT;
			if (isDFC && weapon.getSpec().hasTag(ARCHAIC)) bonus += DFC_BONUS_FLAT;
			return bonus;
		}
		
		public float getWeaponRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		return !ship.getVariant().hasHullMod(CONFLICT_MOD) && !ship.getVariant().hasHullMod(CONFLICT_MOD_2);
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(CONFLICT_MOD) || ship.getVariant().hasHullMod(CONFLICT_MOD_2)) {
			return "Comparable system already present";
		}
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "archaic";
		if (index == 1) return "" + (int) RANGE_BONUS_UNIF + "%";
		if (index == 2) return "" + (int) RANGE_BONUS_BASE + "%";
		if (index == 3) return "" + (int) RANGE_BONUS_GOOD + "%";
		if (index == 4) return "" + (int) RANGE_BONUS_BEST + "%";
		if (index == 5) return "archaic";
		if (index == 6) return "" + (int) DFC_BONUS_FLAT;
		if (index == 7) return DFC_NAME;
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) RANGE_BONUS_FLAT;
		return null;
	}
}