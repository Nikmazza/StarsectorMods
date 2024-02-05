package data.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;

import java.util.HashSet;
import java.util.Set;

//Modified ParallelTargetingUnit from tahlan. Thamks Nia!

public class NES_StabilizedTurrets extends BaseHullMod {

	static final float RANGE_BONUS = 200f; // flat bonus for small energy weapons
	static final float BEAM_MALUS = 200f; // flat malus for small energy beams

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return true;
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		if (!ship.hasListenerOfClass(NES_STListener.class)) {
			ship.addListener(new NES_STListener());
		}
	}

	@Override
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
		if (index == 0) return "" + Math.round(RANGE_BONUS);
		return null;
	}

	// Our range listener
	private static class NES_STListener implements WeaponRangeModifier {

		@Override
		public float getWeaponRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0f;
		}

		@Override
		public float getWeaponRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}

		@Override
		public float getWeaponRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			if (weapon.getSize() != WeaponAPI.WeaponSize.SMALL || weapon.getType() == WeaponAPI.WeaponType.MISSILE || weapon.getType() == WeaponAPI.WeaponType.BALLISTIC) {
				return 0f;
			}

			//Stolen from Nicke. Thx buddy
			float percentRangeIncreases = ship.getMutableStats().getEnergyWeaponRangeBonus().getPercentMod();
			if (ship.hasListenerOfClass(WeaponRangeModifier.class)) {
				for (WeaponRangeModifier listener : ship.getListeners(WeaponRangeModifier.class)) {
					//Should not be needed, but good practice: no infinite loops allowed here, no
					if (listener == this) {
						continue;
					}
					percentRangeIncreases += listener.getWeaponRangePercentMod(ship, weapon);
				}
			}

			float baseRangeMod = RANGE_BONUS;
			if (weapon.getSpec().isBeam()) {
				baseRangeMod -= BEAM_MALUS;
			}

			return baseRangeMod * (1f + (percentRangeIncreases/100f));
		}
	}
}
