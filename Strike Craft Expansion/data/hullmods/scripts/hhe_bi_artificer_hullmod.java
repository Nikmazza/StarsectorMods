package data.hullmods.scripts;

import java.util.Iterator;
import java.util.List;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class hhe_bi_artificer_hullmod extends BaseHullMod {
	
//	private static final float HHE_ARTIFICER_WEAPON_TURRET_TURN_INCREASE = 30f;
	//private static final float HHE_ARTIFICER_WEAPON_FLUX_DECREASE = 15f;
        private static final float AUTOFIRE_ACCURACY = 1f;
        private static final float ANTIFIGHTER_BONUS = 30f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
	//	stats.getDynamic().getMod(Stats.PD_IGNORES_FLARES).modifyFlat(id, 1f);
                stats.getAutofireAimAccuracy().modifyFlat(id, AUTOFIRE_ACCURACY);
                stats.getDamageToFighters().modifyPercent(id, ANTIFIGHTER_BONUS);
	//	stats.getProjectileSpeedMult().modifyMult(id, 1f + HHE_ARTIFICER_WEAPON_TURRET_TURN_INCREASE * 0.01f);
		
	//	stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - HHE_ARTIFICER_WEAPON_FLUX_DECREASE * 0.01f);
	//	stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - HHE_ARTIFICER_WEAPON_FLUX_DECREASE * 0.01f);
	//	stats.getMissileWeaponFluxCostMod().modifyMult(id, 1f - HHE_ARTIFICER_WEAPON_FLUX_DECREASE * 0.01f);
		
	}

	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		List weapons = ship.getAllWeapons();
		Iterator iter = weapons.iterator();
		while (iter.hasNext()) {
			WeaponAPI weapon = (WeaponAPI)iter.next();
			boolean sizeMatches = weapon.getSize() == WeaponSize.SMALL;
			boolean notPD = !weapon.hasAIHint(WeaponAPI.AIHints.PD);
			if (sizeMatches && notPD && weapon.getType() != WeaponType.MISSILE) {
				weapon.setPD(true);
                                weapon.setPDAlso(true);
			}
		}
	}
        
	public boolean isApplicableToShip(ShipAPI ship) {
		return !ship.getVariant().getHullMods().contains("pointdefenseai");
	}	
        
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains("pointdefenseai")) {
			return "SPDAI is incompatible with IPDAI";
		}
		return null;
	}        
        
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return (int)Math.round(ANTIFIGHTER_BONUS) + "%";
//		if (index == 0) return "" + (int) HHE_ARTIFICER_WEAPON_TURRET_TURN_INCREASE + "%";
//		if (index == 1) return "" + (int) HHE_ARTIFICER_WEAPON_FLUX_DECREASE + "%";
		return null;
	}
}
