package data.hullmods.vice;

import java.util.Collection;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class UnifiedTargetingCore extends BaseHullMod {
		
	private static float RANGE_BONUS_S = 30f;
	private static float RANGE_BONUS_L = 40f;
	private static float RANGE_BONUS_SMOD = 5f;
	private static String DTC = "dedicated_targeting_core";
	private static String ITU = "targetingunit";
	private static String CTC = "tahlan_centraltargeting";
	private static String ATC = "advancedcore";
	private static String DFC = "distributed_fire_control";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		float bonus = hullSize == HullSize.CAPITAL_SHIP ? RANGE_BONUS_L : RANGE_BONUS_S;
		if (isSMod(stats)) bonus += RANGE_BONUS_SMOD;
		stats.getBallisticWeaponRangeBonus().modifyPercent(id, bonus);
		stats.getEnergyWeaponRangeBonus().modifyPercent(id, bonus);
		stats.getVariant().getHullMods().remove(DTC);
		stats.getVariant().getHullMods().remove(ITU);
	}

	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		HullSize shipSize = ship.getVariant().getHullSize();
		float bonus = shipSize == HullSize.CAPITAL_SHIP ? RANGE_BONUS_L : RANGE_BONUS_S;
		if (isSMod(ship)) bonus += RANGE_BONUS_SMOD;
		fighter.getMutableStats().getBallisticWeaponRangeBonus().modifyPercent(id, bonus);
		fighter.getMutableStats().getEnergyWeaponRangeBonus().modifyPercent(id, bonus);
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		Collection<String> m = ship.getVariant().getHullMods();
		return ((ship.getHullSize() == HullSize.CAPITAL_SHIP || ship.getHullSize() == HullSize.CRUISER) && 
				!m.contains(DTC) && 
				!m.contains(ITU) && 
				!m.contains(CTC) && 
				!m.contains(ATC) && 
				!m.contains(DFC));
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		Collection<String> m = ship.getVariant().getHullMods();
		if (ship.getHullSize() != HullSize.CAPITAL_SHIP && ship.getHullSize() != HullSize.CRUISER) {
			return "Can only be installed on a cruiser or capital ship";
		}
		if (m.contains(DTC)) return "Incompatible with Dedicated Targeting Core";
		if (m.contains(ITU)) return "Incompatible with Integrated Targeting Unit";
		if (m.contains(CTC)) return "Incompatible with Centralized Targeting Core";
		if (m.contains(ATC)) return "Incompatible with Advanced Targeting Core";
		if (m.contains(DFC)) return "Incompatible with Distributed Fire Control";
		
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) RANGE_BONUS_S + "%";
		if (index == 1) return "" + (int) RANGE_BONUS_L + "%";
		return null;
	}

	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) RANGE_BONUS_SMOD + "%";
		return null;
	}	
}
