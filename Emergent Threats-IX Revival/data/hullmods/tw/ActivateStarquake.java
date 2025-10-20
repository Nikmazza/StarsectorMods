package data.hullmods.tw;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class ActivateStarquake extends BaseHullMod {
	
	private static String RADIANT_MOD = "ix_converted_hull";
	private static String TW_MOD = "tw_trinity_retrofit";
		
    public boolean isApplicableToShip(ShipAPI ship) {
		return ship.getVariant().hasHullMod(RADIANT_MOD) && ship.getVariant().hasHullMod(TW_MOD);
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (!ship.getVariant().hasHullMod(RADIANT_MOD) || !ship.getVariant().hasHullMod(TW_MOD)) {
			return "Can only be fitted to the Radiant (TW)";
		}
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Starfall (TW)";
		if (index == 1) return "25 OP";
		return null;
	}
}