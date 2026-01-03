package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class UndershieldIris extends BaseHullMod {
	
	//display texts only
	private static int DEGREES_FLEET = 360;
	private static int DEGREES_STOCK = 160;
	private static int SHIELD_TOTAL = 4000;
	private static int SHIELD_COOLDOWN = 30;

	//actual effect
	private static float SHIELD_RADIUS = 120f;
	
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.getShield().setRadius(SHIELD_RADIUS);
	}	
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + DEGREES_FLEET;
		if (index == 1) return "" + SHIELD_TOTAL;
		if (index == 2) return "" + SHIELD_COOLDOWN;
		return null;
	}
}