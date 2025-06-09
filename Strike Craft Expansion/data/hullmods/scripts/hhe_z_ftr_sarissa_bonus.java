package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class hhe_z_ftr_sarissa_bonus extends BaseHullMod {

	public static final float HHE_SARISSA_WEAPON_BOOST = 100f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getBallisticWeaponRangeBonus().modifyPercent(id, HHE_SARISSA_WEAPON_BOOST);
	}
}
