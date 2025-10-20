package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.util.HashSet;
import java.util.Set;

public class GMDA_Ftr extends BaseHullMod {


    private static final float HANDLING_MULT = 1.65f;
    private static final float SPEED_MOD = 1.10f;
    private static final int BURN_LEVEL_BONUS = 1;
    private static final float PEAK_MULT = (float) 0.7;
    private static final float CR_DEG_MULT = 2.5f;
    private static final float ARMOR_PENALTY_MULT = 0.50f;
    private static final float CAPACITY_PENALTY = 0.20f;
    private static final float DEPLOY_MULT= 1.35f;

	/*private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0.0f);
		mag.put(HullSize.FRIGATE, 0.25f);
		mag.put(HullSize.DESTROYER, 0.15f);
		mag.put(HullSize.CRUISER, 0.10f);
		mag.put(HullSize.CAPITAL_SHIP, 0.05f);
	}*/	

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    static
    {
        // These hullmods will automatically be removed
        BLOCKED_HULLMODS.add("augmentedengines");
        BLOCKED_HULLMODS.add("auxiliarythrusters");
        BLOCKED_HULLMODS.add("unstable_injector");
		BLOCKED_HULLMODS.add("safetyoverrides");
		BLOCKED_HULLMODS.add("heavyarmor");
        BLOCKED_HULLMODS.add("reinforcedhull");
    }
    private float check=0;
    private String id, ERROR="IncompatibleHullmodWarning";

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id){
        
        if (check>0) {     
            check-=1;
            if (check<1){
            ship.getVariant().removeMod(ERROR);   
            }
        }
        
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {                
                ship.getVariant().removeMod(tmp);      
                ship.getVariant().addMod(ERROR);
                check=3;
            }
        }
    }
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().modifyMult(id, SPEED_MOD);
		stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
		stats.getMaxSpeed().modifyMult(id, HANDLING_MULT);
		stats.getAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getDeceleration().modifyMult(id, HANDLING_MULT);
		stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
		stats.getTurnAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getPeakCRDuration().modifyMult(id, PEAK_MULT);
		stats.getArmorBonus().modifyMult(id, ARMOR_PENALTY_MULT);
        stats.getCargoMod().modifyMult(id, CAPACITY_PENALTY);
        stats.getFuelMod().modifyMult(id, CAPACITY_PENALTY);
        stats.getCRPerDeploymentPercent().modifyMult(id, DEPLOY_MULT);
		stats.getCRLossPerSecondPercent().modifyMult(id, CR_DEG_MULT);
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
		if (index == 1) return "" + (int) ((1f - HANDLING_MULT) * 100f); // + Strings.X;
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