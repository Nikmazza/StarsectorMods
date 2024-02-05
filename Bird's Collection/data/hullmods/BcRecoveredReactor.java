package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

@SuppressWarnings("unchecked")
public class BcRecoveredReactor extends BaseHullMod {

	public static float REPAIR_MULT = 1.2f;
	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 15f);
		mag.put(HullSize.DESTROYER, 20f);
		mag.put(HullSize.CRUISER, 25f);
		mag.put(HullSize.CAPITAL_SHIP, 30f);
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxDissipation().modifyPercent(id, (Float) mag.get(hullSize));
		stats.getCombatEngineRepairTimeMult().modifyMult(id, REPAIR_MULT);
		stats.getCombatWeaponRepairTimeMult().modifyMult(id, REPAIR_MULT);


	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue();
		if (index == 1) return "" + ((Float) mag.get(HullSize.DESTROYER)).intValue();
		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue();
		if (index == 3) return ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue() + "%";
		if (index == 4) return "20%";
		return null;
	}


}
