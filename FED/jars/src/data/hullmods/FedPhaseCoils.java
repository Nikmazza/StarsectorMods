package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class FedPhaseCoils extends BaseHullMod {
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
	
	if (index == 0) return "50%";
        if (index == 1) return  "66%";
        if (index == 2) return  "Adaptive Phase Coils";
        if (index == 3) return  "50%";
        if (index == 4) return  "100%";
        if (index == 5) return  "2x";
        return null;
	}
}
