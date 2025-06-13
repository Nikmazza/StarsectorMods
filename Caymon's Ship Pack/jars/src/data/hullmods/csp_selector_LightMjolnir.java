package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class csp_selector_LightMjolnir extends BaseHullMod {
    
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
 		if (index == 0) return "Light Mjolnir Arm";
		if (index == 1) return "Remove this hullmod to cycle between weapons.";
        return null;    
    }
}
