package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class hhe_sfl_RejuvenationSystem extends BaseHullMod {

	private static final float HHE_SFL_REJUVENATION_FIGHTER_SUPPLY_PER_DECK = 3f;
	private static final float HHE_SFL_REJUVENATION_COMBAT_HULL_REPAIR_INCREASE = 2.5f;
	
	private static final float HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE = 2.5f;
	private static final float HHE_SFL_PENALTY_SHIP_CARGO_DECREASE = 10f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		int sfl_rejuv_supply = (int) (stats.getNumFighterBays().getBaseValue() * HHE_SFL_REJUVENATION_FIGHTER_SUPPLY_PER_DECK);
		stats.getSuppliesPerMonth().modifyMult(id, 1f - sfl_rejuv_supply * 0.01f);
		stats.getSuppliesToRecover().modifyMult(id, 1f - sfl_rejuv_supply * 0.01f);
		
		int sfl_penalty_refit = (int) (stats.getNumFighterBays().getBaseValue() * HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE);
		stats.getFighterRefitTimeMult().modifyMult(id, 1f + sfl_penalty_refit * 0.01f);
		
		int sfl_penalty_cargo = (int) (stats.getNumFighterBays().getBaseValue() * HHE_SFL_PENALTY_SHIP_CARGO_DECREASE);
		stats.getCargoMod().modifyFlat(id, -sfl_penalty_cargo);
	}
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		
		fighter.getMutableStats().getHullCombatRepairRatePercentPerSecond().modifyFlat(id, HHE_SFL_REJUVENATION_COMBAT_HULL_REPAIR_INCREASE);
		
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) HHE_SFL_REJUVENATION_FIGHTER_SUPPLY_PER_DECK + "%";
		if (index == 1) return "" + (int) HHE_SFL_REJUVENATION_COMBAT_HULL_REPAIR_INCREASE + "%";
		
		if (index == 2) return "" + (int) HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE + "%" + " per fighter bay";
		if (index == 3) return "" + (int) HHE_SFL_PENALTY_SHIP_CARGO_DECREASE + " per fighter bay";
		
		if (index == 4) return "Only one SFL configuration can be taken per ship";
		
		return null;
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return (ship.getHullSize() != HullSize.FRIGATE) && ship.getHullSpec().getFighterBays() >= 1 &&
		
				!ship.getVariant().getHullMods().contains("hhe_sfl_AegisSystem") &&
				!ship.getVariant().getHullMods().contains("hhe_sfl_PredatorSystem") &&
				!ship.getVariant().getHullMods().contains("hhe_sfl_HammerSystem");
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship != null && ship.getHullSize() == HullSize.FRIGATE) {
			return "Cannot be installed on Frigates";
		}
		if (ship.getHullSpec().getFighterBays() < 1) {
			return "Ship does not have standard fighter bays";
		}
		if 	(ship.getVariant().getHullMods().contains("hhe_sfl_AegisSystem") ||
			ship.getVariant().getHullMods().contains("hhe_sfl_PredatorSystem") ||
			ship.getVariant().getHullMods().contains("hhe_sfl_HammerSystem")) {
			
			return "SFL System already installed!";
		}
		return null;
	}
	
}