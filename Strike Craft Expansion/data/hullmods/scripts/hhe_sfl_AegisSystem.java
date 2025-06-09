package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import java.util.Iterator;
import java.util.List;

public class hhe_sfl_AegisSystem extends BaseHullMod {

//	private static final float HHE_SFL_AEGIS_FIGHTER_DMGTAKEN_DECREASE = 20f;
//	private static final float HHE_SFL_AEGIS_FIGHTER_WEAPON_RECOIL_DECREASE = 20f;
//	private static final float HHE_SFL_AEGIS_FIGHTER_COMBAT_REPAIR_DECREASE = 25f;
        public static final float HHE_SFL_ANTIMISSILE = 50f;
	
	public static final float HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE = 2.5f;
	public static final float HHE_SFL_PENALTY_SHIP_CARGO_DECREASE = 10f;
        public static final float HHE_SFL_PDRANGE = 100f;
	
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		
//		fighter.getMutableStats().getEnergyDamageTakenMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_DMGTAKEN_DECREASE * 0.01f);
//		fighter.getMutableStats().getFragmentationDamageTakenMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_DMGTAKEN_DECREASE * 0.01f);
//		fighter.getMutableStats().getHighExplosiveDamageTakenMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_DMGTAKEN_DECREASE * 0.01f);
//		fighter.getMutableStats().getKineticDamageTakenMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_DMGTAKEN_DECREASE * 0.01f);
//		
//		fighter.getMutableStats().getEnergyShieldDamageTakenMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_DMGTAKEN_DECREASE * 0.01f);
//		fighter.getMutableStats().getFragmentationShieldDamageTakenMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_DMGTAKEN_DECREASE * 0.01f);
//		fighter.getMutableStats().getHighExplosiveShieldDamageTakenMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_DMGTAKEN_DECREASE * 0.01f);
//		fighter.getMutableStats().getKineticShieldDamageTakenMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_DMGTAKEN_DECREASE * 0.01f);
//		
//		fighter.getMutableStats().getMaxRecoilMult().modifyMult(id, 1f - (HHE_SFL_AEGIS_FIGHTER_WEAPON_RECOIL_DECREASE * 0.01f));
//		fighter.getMutableStats().getRecoilPerShotMult().modifyMult(id, 1f - (HHE_SFL_AEGIS_FIGHTER_WEAPON_RECOIL_DECREASE * 0.01f));
//		fighter.getMutableStats().getRecoilDecayMult().modifyMult(id, 1f - (HHE_SFL_AEGIS_FIGHTER_WEAPON_RECOIL_DECREASE * 0.01f));
//		
//		fighter.getMutableStats().getCombatEngineRepairTimeMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_COMBAT_REPAIR_DECREASE * 0.01f);
//		fighter.getMutableStats().getCombatWeaponRepairTimeMult().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_COMBAT_REPAIR_DECREASE * 0.01f);
//		fighter.getMutableStats().getOverloadTimeMod().modifyMult(id, 1f - HHE_SFL_AEGIS_FIGHTER_COMBAT_REPAIR_DECREASE * 0.01f);
                fighter.getMutableStats().getDamageToMissiles().modifyMult(id, 1f - HHE_SFL_ANTIMISSILE * 0.01f);
                fighter.getMutableStats().getDynamic().getMod(Stats.PD_IGNORES_FLARES).modifyFlat(id, 1f);
                fighter.getMutableStats().getDynamic().getMod(Stats.PD_BEST_TARGET_LEADING).modifyFlat(id, 1f);
                fighter.getMutableStats().getBeamPDWeaponRangeBonus().modifyFlat(id, HHE_SFL_PDRANGE);
                fighter.getMutableStats().getNonBeamPDWeaponRangeBonus().modifyFlat(id, HHE_SFL_PDRANGE);
                
		List weapons = ship.getAllWeapons();
		Iterator iter = weapons.iterator();
		while (iter.hasNext()) {
			WeaponAPI weapon = (WeaponAPI)iter.next();
			boolean sizeMatches = weapon.getSize() == WeaponAPI.WeaponSize.SMALL;
			boolean notPD = !weapon.hasAIHint(WeaponAPI.AIHints.PD);
			if (sizeMatches && notPD && weapon.getType() != WeaponAPI.WeaponType.MISSILE) {
				weapon.setPD(true);
                                weapon.setPDAlso(true);
			}
	}
    }
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		int sfl_penalty_refit = (int) (stats.getNumFighterBays().getBaseValue() * HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE);
		stats.getFighterRefitTimeMult().modifyMult(id, 1f + sfl_penalty_refit * 0.01f);
		
		int sfl_penalty_cargo = (int) (stats.getNumFighterBays().getBaseValue() * HHE_SFL_PENALTY_SHIP_CARGO_DECREASE);
		stats.getCargoMod().modifyFlat(id, -sfl_penalty_cargo);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + "Integrated Point-defense AI";
                if (index == 1) return "" + HHE_SFL_PDRANGE + " units";
//		if (index == 1) return "" + (int) HHE_SFL_AEGIS_FIGHTER_WEAPON_RECOIL_DECREASE + "%";
		
		if (index == 2) return "" + (int) HHE_SFL_PENALTY_FIGHTER_REFIT_INCREASE + "%" + " per fighter bay";
		if (index == 3) return "" + (int) HHE_SFL_PENALTY_SHIP_CARGO_DECREASE + " per fighter bay";
		
		if (index == 4) return "Only one SFL configuration can be taken per ship";
		
		return null;
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship.getHullSpec().getFighterBays() >= 1 &&
		
				!ship.getVariant().getHullMods().contains("hhe_sfl_PredatorSystem") &&
				!ship.getVariant().getHullMods().contains("hhe_sfl_HammerSystem") &&
				!ship.getVariant().getHullMods().contains("hhe_sfl_RejuvenationSystem");
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getHullSpec().getFighterBays() < 1) {
			return "Ship does not have standard fighter bays";
		}
		if 	(ship.getVariant().getHullMods().contains("hhe_sfl_PredatorSystem") ||
			ship.getVariant().getHullMods().contains("hhe_sfl_HammerSystem") ||
			ship.getVariant().getHullMods().contains("hhe_sfl_RejuvenationSystem")) {
			
			return "SFL System already installed!";
		}
		return null;
	}
	
}