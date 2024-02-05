package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class FedSeparateShields extends BaseHullMod {
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
	
	if (index == 0) return "6000";
        if (index == 1) return  "1.0";
        if (index == 2) return  "150";
        return null;
	}
}
