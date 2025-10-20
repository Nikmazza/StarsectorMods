package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.util.HashSet;
import java.util.Set;

public class PoorMissileIntegration extends BaseHullMod {

	public static final float LOAD_REDUCTION = 0.75f;
	
    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
    static{
        // These hullmods will automatically be removed
        // This prevents unexplained hullmod blocking
        BLOCKED_HULLMODS.add("missleracks");
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

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "Expanded Missile Racks"; // + Strings.X;
        return null;
    }
}
