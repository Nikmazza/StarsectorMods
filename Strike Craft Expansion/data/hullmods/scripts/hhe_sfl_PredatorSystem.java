package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class hhe_sfl_PredatorSystem extends BaseHullMod {

	public static final float HHE_SFL_PREDATOR_FIGHTER_FIGHTER_DAMAGE_INCREASE = 20f;
	public static final float HHE_SFL_PREDATOR_FIGHTER_FRIGATE_DAMAGE_INCREASE = 15f;
	public static final float HHE_SFL_PREDATOR_FIGHTER_DESTROYER_DAMAGE_INCREASE = 10f;
	public static final float HHE_SFL_PREDATOR_FIGHTER_CRUISER_DAMAGE_INCREASE = 5f;
	
//	private static final float HHE_SFL_PREDATOR_FIGHTER_WEAPON_DAMAGE_INCREASE = 10f;
//	private static final float HHE_SFL_PREDATOR_FIGHTER_WEAPON_PROJ_SPEED_INCREASE = 25f;
//	private static final float HHE_SFL_PREDATOR_FIGHTER_SPEED_HANDLING_INCREASE = 25f;
        public static final float HHE_SFL_PREDATOR_SPEED_BONUS = 10f;
        public static final float HHE_SFL_PREDATOR_MANEUVERABILITY_BONUS = 50f;
        public static final float HHE_SFL_PREDATOR_AUTOAIM = 1f;
	
	private static final float HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE = 2.5f;
	private static final float HHE_SFL_PENALTY_SHIP_CARGO_DECREASE = 10f;
	
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		
		fighter.getMutableStats().getDamageToFighters().modifyMult(id, 1f + HHE_SFL_PREDATOR_FIGHTER_FIGHTER_DAMAGE_INCREASE * 0.01f);
		fighter.getMutableStats().getDamageToFrigates().modifyMult(id, 1f + HHE_SFL_PREDATOR_FIGHTER_FRIGATE_DAMAGE_INCREASE * 0.01f);
		fighter.getMutableStats().getDamageToDestroyers().modifyMult(id, 1f + HHE_SFL_PREDATOR_FIGHTER_DESTROYER_DAMAGE_INCREASE * 0.01f);
		fighter.getMutableStats().getDamageToCruisers().modifyMult(id, 1f + HHE_SFL_PREDATOR_FIGHTER_CRUISER_DAMAGE_INCREASE * 0.01f);
                fighter.getMutableStats().getAutofireAimAccuracy().modifyFlat(id, HHE_SFL_PREDATOR_AUTOAIM);
		
//		fighter.getMutableStats().getBallisticWeaponDamageMult().modifyMult(id, 1f + HHE_SFL_PREDATOR_FIGHTER_WEAPON_DAMAGE_INCREASE * 0.01f);
//		fighter.getMutableStats().getEnergyWeaponDamageMult().modifyMult(id, 1f + HHE_SFL_PREDATOR_FIGHTER_WEAPON_DAMAGE_INCREASE * 0.01f);
//        fighter.getMutableStats().getProjectileSpeedMult().modifyPercent(id, HHE_SFL_PREDATOR_FIGHTER_WEAPON_PROJ_SPEED_INCREASE);
		
		fighter.getMutableStats().getMaxSpeed().modifyMult(id, 1f + HHE_SFL_PREDATOR_SPEED_BONUS * 0.01f);
		fighter.getMutableStats().getAcceleration().modifyMult(id, 1f + HHE_SFL_PREDATOR_MANEUVERABILITY_BONUS * 0.02f);
		fighter.getMutableStats().getDeceleration().modifyMult(id, 1f + HHE_SFL_PREDATOR_MANEUVERABILITY_BONUS * 0.01f);
		fighter.getMutableStats().getTurnAcceleration().modifyMult(id, 1f + HHE_SFL_PREDATOR_MANEUVERABILITY_BONUS * 0.02f);
		fighter.getMutableStats().getMaxTurnRate().modifyMult(id, 1f + HHE_SFL_PREDATOR_MANEUVERABILITY_BONUS * 0.01f);
		
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		int sfl_penalty_refit = (int) (stats.getNumFighterBays().getBaseValue() * HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE);
		stats.getFighterRefitTimeMult().modifyMult(id, 1f + sfl_penalty_refit * 0.01f);
		
		int sfl_penalty_cargo = (int) (stats.getNumFighterBays().getBaseValue() * HHE_SFL_PENALTY_SHIP_CARGO_DECREASE);
		stats.getCargoMod().modifyFlat(id, -sfl_penalty_cargo);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + HHE_SFL_PREDATOR_FIGHTER_CRUISER_DAMAGE_INCREASE + "%";
		if (index == 1) return "" + (int) HHE_SFL_PREDATOR_SPEED_BONUS + "%";
		if (index == 2) return "" + "Auxiliary Thrusters";
		
//		if (index == 3) return "" + (int) HHE_SFL_PREDATOR_FIGHTER_WEAPON_DAMAGE_INCREASE + "%";
//		if (index == 4) return "" + (int) HHE_SFL_PREDATOR_FIGHTER_WEAPON_PROJ_SPEED_INCREASE + "%";
//		if (index == 5) return "" + (int) HHE_SFL_PREDATOR_FIGHTER_SPEED_HANDLING_INCREASE + "%";
		
		if (index == 3) return "" + (int) HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE + "%" + " per fighter bay";
		if (index == 4) return "" + (int) HHE_SFL_PENALTY_SHIP_CARGO_DECREASE + " per fighter bay";
		
		if (index == 5) return "Only one SFL configuration can be taken per ship";
		
		return null;
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship.getHullSpec().getFighterBays() >= 1 &&
		
				!ship.getVariant().getHullMods().contains("hhe_sfl_AegisSystem") &&
				!ship.getVariant().getHullMods().contains("hhe_sfl_HammerSystem") &&
				!ship.getVariant().getHullMods().contains("hhe_sfl_RejuvenationSystem");
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getHullSpec().getFighterBays() < 1) {
			return "Ship does not have standard fighter bays";
		}
		if 	(ship.getVariant().getHullMods().contains("hhe_sfl_AegisSystem") ||
			ship.getVariant().getHullMods().contains("hhe_sfl_HammerSystem") ||
			ship.getVariant().getHullMods().contains("hhe_sfl_RejuvenationSystem")) {
			
			return "SFL System already installed!";
		}
		return null;
	}
	
}