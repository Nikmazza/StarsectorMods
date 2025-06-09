package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class hhe_sfl_HammerSystem extends BaseHullMod {

//	private static final float HHE_SFL_HAMMER_FIGHTER_WEAPON_ROF_INCREASE = 10f;
//	private static final float HHE_SFL_HAMMER_FIGHTER_WEAPON_DAMAGE_INCREASE = 7.5f;
//	private static final float HHE_SFL_HAMMER_FIGHTER_MISSILE_DAMAGE_INCREASE = 15f;
	
//	private static final float HHE_SFL_HAMMER_FIGHTER_MISSILE_HEALTH_INCREASE = 25f;
	public static final float HHE_SFL_HAMMER_FIGHTER_MISSILE_RANGE_INCREASE = 10f;
	public static final float HHE_SFL_HAMMER_FIGHTER_MISSILE_SPEED_INCREASE = 25f;
	public static final float HHE_SFL_HAMMER_FIGHTER_MISSILE_HANDLING_INCREASE = 50f;
        public static final float HHE_SFL_HAMMER_FIGHTER_MISSILE_ACCELERATION_INCREASE = 150f;
	public static final float HHE_SFL_HAMMER_FIGHTER_MISSILE_GUIDANCE_INCREASE = 100f;
	
	public static final float HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE = 2.5f;
	public static final float HHE_SFL_PENALTY_SHIP_CARGO_DECREASE = 10f;
        public static final float HHE_ECCM_CHANCE = 0.5f;
        public static final float HHE_HAMMER_CAPDAMAGE=1.2f;
        public static final float HHE_HAMMER_CRUISERDAMAGE=1.15f;
        public static final float HHE_HAMMER_DESTROYERDAMAGE=1.10f;
        public static final float HHE_HAMMER_FRIGATEDAMAGE=1.05f;
	
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		
//		fighter.getMutableStats().getBallisticRoFMult().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_WEAPON_ROF_INCREASE * 0.01f);
//		fighter.getMutableStats().getMissileRoFMult().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_WEAPON_ROF_INCREASE * 0.01f);
//		fighter.getMutableStats().getEnergyRoFMult().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_WEAPON_ROF_INCREASE * 0.01f);
//		
//		fighter.getMutableStats().getBallisticWeaponDamageMult().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_WEAPON_DAMAGE_INCREASE * 0.01f);
//		fighter.getMutableStats().getEnergyWeaponDamageMult().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_WEAPON_DAMAGE_INCREASE * 0.01f);
//		fighter.getMutableStats().getMissileWeaponDamageMult().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_MISSILE_DAMAGE_INCREASE * 0.01f);
		
//		fighter.getMutableStats().getMissileHealthBonus().modifyPercent(id, HHE_SFL_HAMMER_FIGHTER_MISSILE_HEALTH_INCREASE);
		fighter.getMutableStats().getMissileWeaponRangeBonus().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_MISSILE_RANGE_INCREASE * 0.01f);
		fighter.getMutableStats().getMissileMaxSpeedBonus().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_MISSILE_SPEED_INCREASE * 0.01f);
		fighter.getMutableStats().getMissileAccelerationBonus().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_MISSILE_ACCELERATION_INCREASE * 0.01f);
		fighter.getMutableStats().getMissileTurnAccelerationBonus().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_MISSILE_ACCELERATION_INCREASE * 0.01f);
		fighter.getMutableStats().getMissileMaxTurnRateBonus().modifyMult(id, 1f + HHE_SFL_HAMMER_FIGHTER_MISSILE_HANDLING_INCREASE * 0.01f);
		fighter.getMutableStats().getMissileGuidance().modifyFlat(id, 0f + HHE_SFL_HAMMER_FIGHTER_MISSILE_GUIDANCE_INCREASE * 0.01f);
		fighter.getMutableStats().getEccmChance().modifyFlat(id, HHE_ECCM_CHANCE);
                fighter.getMutableStats().getDamageToCapital().modifyMult(id, HHE_HAMMER_CAPDAMAGE);
                fighter.getMutableStats().getDamageToCruisers().modifyMult(id, HHE_HAMMER_CRUISERDAMAGE);
                fighter.getMutableStats().getDamageToDestroyers().modifyMult(id, HHE_HAMMER_DESTROYERDAMAGE);
                fighter.getMutableStats().getDamageToFrigates().modifyMult(id, HHE_HAMMER_FRIGATEDAMAGE);
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		int sfl_penalty_refit = (int) (stats.getNumFighterBays().getBaseValue() * HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE);
		stats.getFighterRefitTimeMult().modifyMult(id, 1f + sfl_penalty_refit * 0.01f);
		
		int sfl_penalty_cargo = (int) (stats.getNumFighterBays().getBaseValue() * HHE_SFL_PENALTY_SHIP_CARGO_DECREASE);
		stats.getCargoMod().modifyFlat(id, -sfl_penalty_cargo);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + Math.round((HHE_HAMMER_FRIGATEDAMAGE - 1f) * 100f) + "%";
		if (index == 1) return "" + "ECCM Package";
//		
//		if (index == 2) return "" + (int) HHE_SFL_HAMMER_FIGHTER_MISSILE_HEALTH_INCREASE + "%";
//		if (index == 3) return "" + (int) HHE_SFL_HAMMER_FIGHTER_MISSILE_RANGE_INCREASE + "%";
//		if (index == 4) return "" + (int) HHE_SFL_HAMMER_FIGHTER_MISSILE_SPEED_INCREASE + "%";
//		if (index == 5) return "" + (int) HHE_SFL_HAMMER_FIGHTER_MISSILE_HANDLING_INCREASE + "%";
		
		if (index == 2) return "" + (int) HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE + "%" + " per fighter bay";
		if (index == 3) return "" + (int) HHE_SFL_PENALTY_SHIP_CARGO_DECREASE + " per fighter bay";
		
		if (index == 4) return "Only one SFL configuration can be taken per ship";
		
		return null;
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship.getHullSpec().getFighterBays() >= 1 &&
		
				!ship.getVariant().getHullMods().contains("hhe_sfl_AegisSystem") &&
				!ship.getVariant().getHullMods().contains("hhe_sfl_PredatorSystem") &&
				!ship.getVariant().getHullMods().contains("hhe_sfl_RejuvenationSystem");
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getHullSpec().getFighterBays() < 1) {
			return "Ship does not have standard fighter bays";
		}
		if 	(ship.getVariant().getHullMods().contains("hhe_sfl_AegisSystem") ||
			ship.getVariant().getHullMods().contains("hhe_sfl_PredatorSystem") ||
			ship.getVariant().getHullMods().contains("hhe_sfl_RejuvenationSystem")) {
			
			return "SFL System already installed!";
		}
		return null;
	}
	
}