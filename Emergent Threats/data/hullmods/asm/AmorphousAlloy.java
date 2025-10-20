package data.hullmods.asm;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class AmorphousAlloy extends BaseHullMod {
	
	private static float ARMOR_BONUS = 25f;
	private static float MANEUVER_BONUS = 25f;
	private static String CONFLICT_MOD_1 = "armoredweapons";
	private static String CONFLICT_MOD_2 = "heavyarmor";
	private static String CONFLICT_MOD_3 = "tahlan_daemonplating";
	private static String CONFLICT_MOD_4 = "frontshield";
	private static Map THURSTER_BONUS = new HashMap();
	static {
		THURSTER_BONUS.put(HullSize.FIGHTER, 20f);
		THURSTER_BONUS.put(HullSize.FRIGATE, 20f);
		THURSTER_BONUS.put(HullSize.DESTROYER, 15f);
		THURSTER_BONUS.put(HullSize.CRUISER, 10f);
		THURSTER_BONUS.put(HullSize.CAPITAL_SHIP, 10f);
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI variant = stats.getVariant();
		variant.getHullSpec().addTag("threat"); //removes overlay from fragment swarm
		stats.getArmorBonus().modifyPercent(id, ARMOR_BONUS);
		stats.getAcceleration().modifyMult(id, 1f + MANEUVER_BONUS * 0.02f);
		stats.getDeceleration().modifyMult(id, 1f + MANEUVER_BONUS * 0.01f);
		stats.getTurnAcceleration().modifyMult(id, 1f + MANEUVER_BONUS * 0.02f);
		stats.getMaxTurnRate().modifyMult(id, 1f + MANEUVER_BONUS * 0.01f);
		float bonus = (Float) THURSTER_BONUS.get(hullSize);
		stats.getMaxSpeed().modifyFlat(id, bonus);
		variant.getHullMods().remove(CONFLICT_MOD_1);
		variant.getHullMods().remove(CONFLICT_MOD_2);
		variant.getHullMods().remove(CONFLICT_MOD_3);
		variant.getHullMods().remove(CONFLICT_MOD_4);
		variant.addPermaMod(id, false);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ARMOR_BONUS + "%";
		if (index == 1) return "" + (int) MANEUVER_BONUS + "%";
		if (index == 2) return "" + ((Float) THURSTER_BONUS.get(HullSize.FRIGATE)).intValue() + "%";
		if (index == 3) return "" + ((Float) THURSTER_BONUS.get(HullSize.DESTROYER)).intValue() + "%";
		if (index == 4) return "" + ((Float) THURSTER_BONUS.get(HullSize.CRUISER)).intValue() + "%";
		if (index == 5) return "" + ((Float) THURSTER_BONUS.get(HullSize.CAPITAL_SHIP)).intValue() + "%";
		if (index == 6) return "5";
		if (index == 7) return "10";
		return null;
	}
}