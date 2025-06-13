package data.scripts.vice.util;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;

public class BasicUtil {

	public static int getRemainingOP(ShipAPI ship) {
		int unusedOP = 0;
		try {
			unusedOP = ship.getVariant().getUnusedOP(Global.getSector().getCharacterData().getPerson().getFleetCommanderStats()); 
		}
		catch (Exception e) {
			unusedOP = ship.getVariant().getUnusedOP(Global.getFactory().createPerson().getFleetCommanderStats());
		}
		return unusedOP;	
	}
}