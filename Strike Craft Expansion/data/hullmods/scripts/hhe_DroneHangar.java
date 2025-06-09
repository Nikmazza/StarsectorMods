package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class hhe_DroneHangar extends BaseHullMod {

	public static final float DRONE_REPLACEMENT_RATE = 25f;
	public static final float DRONE_REFIT_TIME = 25f;
	public static final float FIGHTER_REPAIR_BONUS = 25f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyPercent(id, -DRONE_REPLACEMENT_RATE);
		stats.getFighterRefitTimeMult().modifyPercent(id, -DRONE_REFIT_TIME);
	}
	
    @Override
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		fighter.getMutableStats().getCombatEngineRepairTimeMult().modifyMult(id, 1f - FIGHTER_REPAIR_BONUS * 0.01f);
		fighter.getMutableStats().getCombatWeaponRepairTimeMult().modifyMult(id, 1f - FIGHTER_REPAIR_BONUS * 0.01f);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) DRONE_REPLACEMENT_RATE + "%";
		if (index == 1) return "" + (int) DRONE_REFIT_TIME + "%";
		if (index == 2) return "" + (int) FIGHTER_REPAIR_BONUS + "%";
		return null;
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship.getVariant().getHullMods().contains("automated") && ship.getHullSpec().getFighterBays() >= 1;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (!ship.getVariant().getHullMods().contains("automated")) {
			return "Requires an automated ship";
		}
		if (ship.getHullSpec().getFighterBays() < 1) {
			return "Ship does not have standard fighter bays";
		}
		return null;
	}
	
}
