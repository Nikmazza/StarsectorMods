package data.scripts.ix.util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class DistanceUtil {
 
	public static float getDistance(CombatEntityAPI self, CombatEntityAPI target) {
		if (self == null || target == null) return 100000f; //out of range by default
		Vector2f selfLoc = self.getLocation();
		Vector2f targetLoc = target.getLocation();
		return getDistance(selfLoc, targetLoc);
	}
	
	public static float getDistance(CombatEntityAPI from, Vector2f to) {
		Vector2f fromLoc = from.getLocation();
		return getDistance(fromLoc, to);
	}
	
	public static float getDistance(Vector2f from, CombatEntityAPI to) {
		Vector2f toLoc = to.getLocation();
		return getDistance(from, toLoc);
	}
	
	public static float getDistance(Vector2f from, Vector2f to) {
		float x1 = from.getX();
		float y1 = from.getY();
		float x2 = to.getX();
		float y2 = to.getY();
		return (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));		
	}
	
	public static ShipAPI getNearestEnemy(CombatEntityAPI ship, float range) {
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
	
	public static ShipAPI getNearestEnemy(CombatEntityAPI ship, float range, boolean isTargetingFighters) {
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
	
	public static ShipAPI getNearestPhaseEnemy(CombatEntityAPI ship, float range) {
		List<ShipAPI> enemies = getAllShipsInRange(ship, range, "p_enemies");		
		ShipAPI result = null;
		for (ShipAPI e : enemies) {
			if (e.isPhased()) {
				if (result == null) result = e;
				else if (getDistance(ship, e) < getDistance(ship, result)) result = e;
			}
		}
        return result;
    }
	
	//includes fighters but not phased ships except for p_enemies iff
	public static List<ShipAPI> getAllShipsInRange(CombatEntityAPI e, float range, String iff) {
		List<ShipAPI> shipList = new ArrayList<ShipAPI>();
		List<ShipAPI> ships = Global.getCombatEngine().getShips();
		if (iff.equals("enemies")) {
			for (ShipAPI s : ships) {
				if (getDistance(e, s) < (range + s.getCollisionRadius()) 
						&& (s.getOwner() != e.getOwner()) && !s.isPhased()) shipList.add(s);
			}
		}
		else if (iff.equals("p_enemies")) {
			for (ShipAPI s : ships) {
				if (getDistance(e, s) < (range + s.getCollisionRadius()) 
						&& (s.getOwner() != e.getOwner()) && s.isPhased()) shipList.add(s);
			}
		}
		else if (iff.equals("friends")) {
			for (ShipAPI s : ships) {
				if (getDistance(e, s) < (range + s.getCollisionRadius()) 
						&& (s.getOwner() == e.getOwner()) && !s.isPhased()) shipList.add(s);
			}
		}
		else if (iff.equals("all")) {
			for (ShipAPI s : ships) {
				if (getDistance(e, s) < (range + s.getCollisionRadius()) && !s.isPhased()) shipList.add(s);
			}
		}
		return shipList;
	}
	
	public static MissileAPI getNearestMissile(CombatEntityAPI ship, float range) {
		List<MissileAPI> missiles = getAllMissilesInRange(ship, range);		
		MissileAPI result = null;
		for (MissileAPI m : missiles) {
			if (getDistance(ship, m) < getDistance(ship, result)) result = m;
		}
        return result;
    }
	
	public static MissileAPI getNearestMissile(CombatEntityAPI ship, float range, boolean isEnemyOnly) {
		if (!isEnemyOnly) return getNearestMissile(ship, range);
		List<MissileAPI> missiles = getAllMissilesInRange(ship, range);		
		MissileAPI result = null;
		for (MissileAPI m : missiles) {
			if (m.getSourceAPI().getOwner() != ship.getOwner() 
					&& getDistance(ship, m) < getDistance(ship, result)) result = m;
		}
        return result;
    }
	
	public static CombatEntityAPI getNearestEnemyMissileOrShip(CombatEntityAPI ship, float range) {
		CombatEntityAPI a = (CombatEntityAPI) getNearestMissile(ship, range, true);
		CombatEntityAPI b = (CombatEntityAPI) getNearestEnemy(ship, range, true);
		if (a == null && b == null) return null;
		else if (a != null && b == null) return a;
		else if (a == null && b != null) return b;
		else if (getDistance(ship, a) < getDistance(ship, b)) return a;
		else return b;
    }
	
	//includes hostile missiles and fighters
	public static List<CombatEntityAPI> getAllEnemiesNearPoint(Vector2f p, float range, ShipAPI source) {
		List<CombatEntityAPI> enemies = new ArrayList<CombatEntityAPI>();
		List<MissileAPI> missiles = Global.getCombatEngine().getMissiles();
		for (MissileAPI m : missiles) {
			if (getDistance(p, m) < range && m.getSourceAPI().getOwner() != source.getOwner()) enemies.add(m);
		}
		List<ShipAPI> ships = Global.getCombatEngine().getShips();
		for (ShipAPI s : ships) {
			if (getDistance(p, s) < range && s.getOwner() != source.getOwner()) enemies.add(s);
		}
		return enemies;
	}
	
	public static List<MissileAPI> getAllMissilesInRange(CombatEntityAPI e, float range) {
		List<MissileAPI> missileList = new ArrayList<MissileAPI>();
		List<MissileAPI> missiles = Global.getCombatEngine().getMissiles();
		for (MissileAPI m : missiles) {
			if (getDistance(e, m) < range) missileList.add(m);
		}
		missileList.remove(e);
		return missileList;
	}
	
	public static List<MissileAPI> getAllMissilesInRange(CombatEntityAPI e, float range, boolean isEnemyOnly) {
		List<MissileAPI> missileList = new ArrayList<MissileAPI>();
		List<MissileAPI> missiles = Global.getCombatEngine().getMissiles();
		for (MissileAPI m : missiles) {
			if (getDistance(e, m) < range && m.getSourceAPI().getOwner() != e.getOwner()) missileList.add(m);
		}
		missileList.remove(e);
		return missileList;
	}
	
	public static List<CombatEntityAPI> getAllAsteroidsInRange(CombatEntityAPI e, float range) {
		List<CombatEntityAPI> asteroidList = new ArrayList<CombatEntityAPI>();
		List<CombatEntityAPI> asteroids = Global.getCombatEngine().getAsteroids();
		for (CombatEntityAPI a : asteroids) {
			if (getDistance(e, a) < range) asteroidList.add(a);
		}
		return asteroidList;
	}
}