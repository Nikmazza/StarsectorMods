package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import static data.scripts.hullmods.LTA_SampleTimeAndFluxConditionalHullmod.REFRESH_DELAY_MIN;
import static data.scripts.hullmods.LTA_SampleTimeAndFluxConditionalHullmod.REFRESH_DELAY_MAX;

public class LTA_SampleTimeAndFluxConditionalHullmod extends BaseHullMod {

	/* A Ship of the 14th Domain Battlegroup
	 * well-maintained survivor of the original battlegroup which founded the Hegemony
	 * Sterling example of the Domain Navy's traditional "decisive battle" doctrine
	 * focus on superior armour and firepower on ships of the line to destroy the enemy
	 * - slightly better flux handling
	 * - slightly better armour
	 * - slightly worse speed/maneuver
	 * - 
	 */
	
	//private static final float ARMOR_BONUS_MULT = 1.1f;
	private static final float ARMOR_BONUS = 100f;
	private static final float CAPACITY_MULT = 1.05f;
	private static final float DISSIPATION_MULT = 1.05f;
	private static final float HANDLING_MULT = 0.92f;
        
        public static final float REFRESH_DELAY_MIN = 0.1f;
        public static final float REFRESH_DELAY_MAX = 0.1f;
        public static final float MAX_TIME_MULT = 1.5f;
	public static final float MIN_TIME_MULT = 1.0f;
        
        public static final float MIN_SPEED_LOSS = 1.0f;
        public static final float MAX_SPEED_LOSS = 0.135f;
        
        public static final float SPEED_IN_PHASE_MULT = 1.0f;
        
        public static final String STAT_ID_PREFIX = "LTA_SampleTimeAndFluxConditionalHullmodID"; // This needs to be unique for each hullmod and represents the ID sections used below in AdvanceInCombat as it doesnt make one automatically of its own
        
    	//public static class PeriodicTimeflowCheckerData {
	//	IntervalUtil interval = new IntervalUtil(REFRESH_DELAY_MIN, REFRESH_DELAY_MAX);
	//}
        
        
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
		
		// 5% better flux stats
		stats.getFluxCapacity().modifyMult(id, CAPACITY_MULT);
		stats.getFluxDissipation().modifyMult(id, DISSIPATION_MULT);
		
		// 8% worse handling
		stats.getMaxSpeed().modifyMult(id, HANDLING_MULT);
		stats.getAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getDeceleration().modifyMult(id, HANDLING_MULT);
		stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
		stats.getTurnAcceleration().modifyMult(id, HANDLING_MULT);
	}
        
        @Override //After advanceInCombat you must call mutableShipStats manually rather than putting them in the void. ||YuiNote||
	public void advanceInCombat(ShipAPI ship, float amount) {
            
            
            //int second = (int)Math.floor(Global.getCombatEngine().getTotalElapsedTime(false));

                
            float Current_Ship_Flux = ship.getFluxLevel();

            float CurrentSpeedLoss = Misc.interpolate(MIN_SPEED_LOSS, MAX_SPEED_LOSS, Current_Ship_Flux);
            
                String id = STAT_ID_PREFIX + ship.getId();
                float shipTimeMult = 1f;
                        
                if (ship.isPhased())//IF PHASED USE BELOW
                    
                                    {
                                            shipTimeMult = Misc.interpolate (MIN_TIME_MULT, MAX_TIME_MULT, Current_Ship_Flux); //ALT CODE TO RIGHT// + (MAX_TIME_MULT - 1f) * Current_Ship_Flux;
                                            ship.getMutableStats().getMaxSpeed().modifyMult (id, 1 / CurrentSpeedLoss);
                                            Global.getCombatEngine().maintainStatusForPlayerShip(id, "graphics/LTA/icons/hullsys/hopper_drive", "SPEEDY RE-DOEY", "Top Speed Boosted By " + (1/CurrentSpeedLoss) * 100 + "%", true);
                                    } 
                                        else
                                            {
                                                ship.getMutableStats().getMaxSpeed().unmodify (id);
                                            }
                
                    //stats.getTimeMult().modifyMult(id, shipTimeMult);
                    if (ship==Global.getCombatEngine().getPlayerShip()) {
                    	Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
                            ship.getMutableStats().getTimeMult().modifyMult (id, shipTimeMult);
                    }
                    else
                    {
                            Global.getCombatEngine().getTimeMult().unmodify(id);
                            ship.getMutableStats().getTimeMult().modifyMult (id, shipTimeMult);
                    }
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
		if (index == 1) return "" + (int) Math.round((1f - HANDLING_MULT) * 100f) + "%"; // + Strings.X;
		if (index == 2) return "" + (int) Math.round((CAPACITY_MULT - 1f) * 100f) + "%"; 
		return null;
	}

	/*public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue();
		if (index == 1) return "" + ((Float) mag.get(Hullize.DESTROYER)).intValue();
		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue();
		if (index == 3) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue();
		if (index == 4) return "" + (int) ACCELERATION_BONUS;
		//if (index == 5) return "four times";
		if (index == 5) return "4" + Strings.X;
		if (index == 6) return "" + BURN_LEVEL_BONUS;
		return null;
	}*/

}
