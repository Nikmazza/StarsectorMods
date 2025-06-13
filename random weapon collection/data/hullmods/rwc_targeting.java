package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;

public class rwc_targeting extends BaseHullMod {
	private static String CONFLICT_MOD_0 = "dedicated_targeting_core";
	private static String CONFLICT_MOD_1 = "targetingunit";

	public static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0f);
		mag.put(HullSize.FRIGATE, 5f);
		mag.put(HullSize.DESTROYER, 10f);
		mag.put(HullSize.CRUISER, 20f);
		mag.put(HullSize.CAPITAL_SHIP, 30f);
	}
	public static float DAMAGE_BONUS = 10f;
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue() + "%";
		if (index == 1) return "" + ((Float) mag.get(HullSize.DESTROYER)).intValue() + "%";
		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue() + "%";
		if (index == 3) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue() + "%";
		if (index == 4) return "" + (int) DAMAGE_BONUS + "%";
		return null;
	}
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getVariant().removeMod(CONFLICT_MOD_0);
		stats.getVariant().removeMod(CONFLICT_MOD_1);
		stats.getBallisticWeaponRangeBonus().modifyPercent(id, (Float) mag.get(hullSize));
		stats.getEnergyWeaponRangeBonus().modifyPercent(id, (Float) mag.get(hullSize));
		stats.getBallisticWeaponDamageMult().modifyPercent(id, DAMAGE_BONUS);
		stats.getEnergyWeaponDamageMult().modifyPercent(id, DAMAGE_BONUS);
		stats.getMissileWeaponDamageMult().modifyPercent(id, DAMAGE_BONUS);
	}


	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return !ship.getVariant().getHullMods().contains("dedicated_targeting_core") &&
				!ship.getVariant().getHullMods().contains("targetingunit") &&
				!ship.getVariant().getHullMods().contains("advancedcore");
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains("dedicated_targeting_core")) {
			return "Incompatible with Dedicated Targeting Core";
		}
		if (ship.getVariant().getHullMods().contains("advancedcore")) {
			return "Incompatible with Advanced Targeting Core";
		}
		if (ship.getVariant().getHullMods().contains("targetingunit")) {
			return "Incompatible with Integrated Targeting Unit";
		}
		return null;
	}
	
}
