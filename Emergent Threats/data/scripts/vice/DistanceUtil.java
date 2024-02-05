package data.scripts.vice;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class DistanceUtil {
 
	private static String ABYSSAL_HULLMOD = "rat_abyssal_grid";
	
	public static float getDistance(CombatEntityAPI self, CombatEntityAPI target) {
		if (self == null || target == null) return 100000f; //out of range by default
		
		Vector2f selfLoc = self.getLocation();
		Vector2f targetLoc = target.getLocation();
		
		float x1 = selfLoc.getX();
		float y1 = selfLoc.getY();
		float x2 = targetLoc.getX();
		float y2 = targetLoc.getY();
		
		return (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));		
	}
	
	public static ShipAPI getNearestEnemy(ShipAPI ship, float range) {
		List<ShipAPI> enemies = getAllShipsInRange(ship, range, "enemies");		
		ShipAPI result = null;
		for (ShipAPI e : enemies) {
			if (!e.isDrone() && !e.isFighter() && !e.isHulk() && !e.isPhased()) {
				if (result == null) result = e;
				else if (getDistance(ship, e) < getDistance(ship, result)) result = e;
			}
		}
        return result;
    }

	public static ShipAPI getNearestEnemy(ShipAPI ship, float range, boolean isTargetingFighters) {
		List<ShipAPI> enemies = getAllShipsInRange(ship, range, "enemies");		
		ShipAPI result = null;
		for (ShipAPI e : enemies) {
			if (!e.isHulk() && !e.isPhased()) {
				if (result == null) result = e;
				else if (getDistance(ship, e) < getDistance(ship, result)) result = e;
			}
		}
        return result;
	}

	public static ShipAPI getNearestEnemyNotAbyssal(ShipAPI ship, float range) {
		List<ShipAPI> enemies = getAllShipsInRange(ship, range, "enemies");		
		ShipAPI result = null;
		for (ShipAPI e : enemies) {
			if (!e.isDrone() && !e.isFighter() && !e.isHulk() && !e.isPhased()) {
				if (ship.getVariant().hasHullMod(ABYSSAL_HULLMOD)) continue;
				if (result == null) result = e;
				else if (getDistance(ship, e) < getDistance(ship, result)) result = e;
			}
		}
        return result;
    }
	
	//includes fighters but not phased ships
	public static List<ShipAPI> getAllShipsInRange(ShipAPI ship, float range, String iff) {
		List<ShipAPI> shipList = new ArrayList<ShipAPI>();
		List<ShipAPI> ships = Global.getCombatEngine().getShips();
		if (iff.equals("enemies")) {
			for (ShipAPI s : ships) {
				if (getDistance(ship, s) < (range + s.getCollisionRadius()) 
						&& (s.getOwner() != ship.getOwner()) 
						&& !s.isHulk() && !s.isShuttlePod() && !s.isPhased()) shipList.add(s);
			}
		}
		else if (iff.equals("friends")) {
			for (ShipAPI s : ships) {
				if (getDistance(ship, s) < (range + s.getCollisionRadius()) 
						&& (s.getOwner() == ship.getOwner()) 
						&& !s.isHulk() && !s.isShuttlePod() && !s.isPhased()) shipList.add(s);
			}
		}
		else {
			for (ShipAPI s : ships) {
				if (getDistance(ship, s) < (range + s.getCollisionRadius()) 
						&& !s.isHulk() && !s.isShuttlePod() && !s.isPhased()) shipList.add(s);
			}
		}
		return shipList;
	}
}