package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.Misc;

public class invictus_modernization extends BaseHullMod {

	/* A Ship of the 2nd Domain Battlegroup
	 * well-maintained survivor of the original battlegroup which founded the Hegemony
	 * Sterling example of the Domain Navy's traditional "adamant lance" doctrine
	 * focus on superior armour and firepower on ships of the line to destroy the enemy
	 * - slightly better flux handling
	 * - slightly better armour
	 * - slightly worse speed/maneuver
	 * - 
	 */
	
	//private static final float ARMOR_BONUS_MULT = 1.1f;
	private static final float ARMOR_BONUS = 800f;
	private static float MAINTENANCE_MULT = 0.5f;
	private static int BURN_LEVEL_BONUS = 1;
	private static int SPEED_BONUS = 10;
	

	
	/*private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0.0f);
		mag.put(HullSize.FRIGATE, 0.25f);
		mag.put(HullSize.DESTROYER, 0.15f);
		mag.put(HullSize.CRUISER, 0.10f);
		mag.put(HullSize.CAPITAL_SHIP, 0.05f);
	}*/	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		// just generally better armour - and structure!
		//stats.getArmorBonus().modifyMult(id, (Float) mag.get(hullSize) + 1.00f); // * ARMOR_BONUS_MULT); 
		//stats.getHullBonus().modifyPercent(id, (Float) mag.get(hullSize) * 0.5f); // some hull. 
		stats.getArmorBonus().modifyFlat(id, (Float) ARMOR_BONUS);
		stats.getMinCrewMod().modifyMult(id, MAINTENANCE_MULT);
		stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
		stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS);
		
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
//		if (index == 0) return "" + (int) ((Float) mag.get(HullSize.FRIGATE) * 100f);
//		if (index == 1) return "" + (int) ((Float) mag.get(HullSize.DESTROYER) * 100f);
//		if (index == 2) return "" + (int) ((Float) mag.get(HullSize.CRUISER) * 100f);
//		if (index == 3) return "" + (int) ((Float) mag.get(HullSize.CAPITAL_SHIP) * 100f);
		//if (index == 0) return Misc.getRoundedValue((Float) mag.get(HullSize.FRIGATE) + 1f);
		//if (index == 1) return Misc.getRoundedValue((Float) mag.get(HullSize.DESTROYER) + 1f);
		//if (index == 2) return Misc.getRoundedValue((Float) mag.get(HullSize.CRUISER) + 1f);
		//if (index == 3) return Misc.getRoundedValue((Float) mag.get(HullSize.CAPITAL_SHIP) + 1f);
		if (index == 0) return Misc.getRoundedValue(ARMOR_BONUS);
		if (index == 1) return "" + (int) Math.round((1f - MAINTENANCE_MULT) * 100f) + "%";
		if (index == 2) return "" + BURN_LEVEL_BONUS;
		if (index == 3) return "" + SPEED_BONUS;
		return null;
	}


}
