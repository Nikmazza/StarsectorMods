package data.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.BaseHullMod;

public class csp_selector_mark9 extends BaseHullMod {
    
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
 		if (index == 0) return "Mark 9 Cannon Arm";
		if (index == 1) return "Remove this hullmod to cycle between weapons.";
        return null;    
    }
}
