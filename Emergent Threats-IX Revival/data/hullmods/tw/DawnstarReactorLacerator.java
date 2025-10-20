package data.hullmods.tw;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;

public class DawnstarReactorLacerator extends BaseHullMod {
	
	//extra damage handled by Lacerator on hit weapon script
	private static String LACERATOR_ID = "lacerator_tw";
	private static String APB_M_ID = "daythorn_mapb_ix";
	private static String APB_T_ID = "daythorn_tapb_ix";
	private static float RANGE_BONUS = 200f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.addListener(new LaceratorBonuses(ship));
	}
	
	public static class LaceratorBonuses implements DamageDealtModifier, WeaponBaseRangeModifier{
		protected ShipAPI ship;
		public LaceratorBonuses(ShipAPI ship) {
			this.ship = ship;
		}
		
		public String modifyDamageDealt(Object param,
								   		CombatEntityAPI target, DamageAPI damage,
								   		Vector2f point, boolean shieldHit) {
			if (param instanceof BeamAPI) {
				BeamAPI beam = (BeamAPI) param;
				String weaponId = beam.getWeapon().getSpec().getWeaponId();
				if (weaponId.equals(LACERATOR_ID) 
						|| weaponId.equals(APB_M_ID)
						|| weaponId.equals(APB_T_ID)) damage.setForceHardFlux(true);
			}
			return null;
		}
		public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}
		public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}
		public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			float bonus = 0f;
			String weaponId = weapon.getSpec().getWeaponId();
			if (weaponId.equals(LACERATOR_ID) 
						|| weaponId.equals(APB_M_ID)
						|| weaponId.equals(APB_T_ID)) bonus = RANGE_BONUS;
			return bonus;
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Heavy Lacerator";
		if (index == 1) return "100";
		if (index == 2) return "Daythorn";
		if (index == 3) return "" + (int) RANGE_BONUS;
		return null;
	}
}