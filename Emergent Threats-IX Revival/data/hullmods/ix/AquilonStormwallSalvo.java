package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AquilonStormwallSalvo extends BaseHullMod {
	
	//dummy hullmod, system switch handled by Aquilon Nanoforge
	private static String MISSILE_COUNT = "24";
	private static String MISSILE_DAMAGE = "60 kinetic";
	private static String MISSILE_EMP = "300 EMP";
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return MISSILE_COUNT;
		if (index == 1) return MISSILE_DAMAGE;
		if (index == 2) return MISSILE_EMP;
		return null;
	}
}