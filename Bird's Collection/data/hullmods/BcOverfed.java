package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.util.Misc;

public class BcOverfed extends BaseHullMod {
	
	private static final float WEAPON_MALFUNCTION_PROB = 0.005f;
	public static final float ROF_BONUS = 100f;
	public static final float AMMO_BONUS = 50f;
	private static final float PEAK_MULT = 0.5f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getWeaponMalfunctionChance().modifyFlat(id, 0.005f);
		stats.getBallisticRoFMult().modifyPercent(id, ROF_BONUS);
		stats.getBallisticAmmoBonus().modifyPercent(id, AMMO_BONUS);
		stats.getPeakCRDuration().modifyMult(id, PEAK_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) AMMO_BONUS + "%";
		if (index == 1) return "" + (int) ROF_BONUS + "%";
		return null;
	}
	public boolean isApplicableToShip(ShipAPI ship) {
//		return !ship.getVariant().getHullMods().contains("unstable_injector") &&
//			   !ship.getVariant().getHullMods().contains("augmented_engines");
		if (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) return false;
		if (ship.getVariant().hasHullMod(HullMods.SAFETYOVERRIDES)) return false;
		
		return true;
	}
	public String getUnapplicableReason(ShipAPI ship) {

		if (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) {
			return "Can not be installed on civilian ships";
		}
		
		return null;
	}
}
