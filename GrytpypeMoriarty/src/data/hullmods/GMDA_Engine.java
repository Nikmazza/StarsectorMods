package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.util.HashSet;
import java.util.Set;

public class GMDA_Engine extends BaseHullMod {


	private static final float LOW_SPEED_MOD = 1.50f;
    private static final float SENSOR_MULT= 1.50f;

    protected Object SPEEDKEY = new Object();

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    static
    {
        // These hullmods will automatically be removed
        BLOCKED_HULLMODS.add("augmentedengines");
        BLOCKED_HULLMODS.add("auxiliarythrusters");
        BLOCKED_HULLMODS.add("unstable_injector");
        BLOCKED_HULLMODS.add("safetyoverrides");
        BLOCKED_HULLMODS.add("heavyarmor");
        BLOCKED_HULLMODS.add("SCY_lightArmor");
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
		stats.getZeroFluxSpeedBoost().modifyMult(id, LOW_SPEED_MOD);
        stats.getSensorProfile().modifyMult(id, SENSOR_MULT);
	}


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((LOW_SPEED_MOD - 1f) * 100f)+ "%"; // + Strings.X;
        if (index == 1) return "" + (int) ((SENSOR_MULT - 1f) * 100f)+ "%"; // + Strings.X;
        if (index == 2) return "" + "Safety Overrides"; // + Strings.X;
        if (index == 3) return "" + "Heavy Armour"; // + Strings.X;
        if (index == 4) return "" + "Unstable Injector"; // + Strings.X;

		return null;
	}

}