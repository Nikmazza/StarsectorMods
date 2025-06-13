package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class csp_selector_miningblaster extends BaseHullMod {
    
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
 		if (index == 0) return "Armored Mining Blaster";
		if (index == 1) return "Remove this hullmod to cycle between weapons.";
        return null;    
    }
}
