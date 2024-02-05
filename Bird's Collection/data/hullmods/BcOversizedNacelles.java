package data.hullmods;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.util.Misc;

public class BcOversizedNacelles extends BaseHullMod {

	private static Map speed = new HashMap();
	static {
		speed.put(HullSize.FRIGATE, 50f);
		speed.put(HullSize.DESTROYER, 30f);
		speed.put(HullSize.CRUISER, 20f);
		speed.put(HullSize.CAPITAL_SHIP, 10f);
	}
	
	private static final float FLUX_DISSIPATION_MULT = 2f;
	private static final float ENGINE_DAMAGE_TAKEN_MULT = 200f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 2f); // set to two, meaning boost is always on
		stats.getEngineDamageTakenMult().modifyPercent(id, ENGINE_DAMAGE_TAKEN_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round((ENGINE_DAMAGE_TAKEN_MULT)) + "%";	
		if (index == 2) return "" + ((Float) speed.get(HullSize.CRUISER)).intValue();
		//if (index == 3) return "" + ((Float) speed.get(HullSize.)).intValue();
		if (index == 3) return Misc.getRoundedValue(FLUX_DISSIPATION_MULT);
		//if (index == 4) return Strings.X + Misc.getRoundedValue(PEAK_MULT);
		if (index == 4) return "3";
		//if (index == 3) return Misc.getRoundedValue(CR_DEG_MULT);
//		if (index == 4) return Misc.getRoundedValue(RECOIL_MULT);
		//if (index == 3) return (int)OVERLOAD_DUR + "%";
		
//		if (index == 0) return "" + ((Float) speed.get(hullSize)).intValue();
//		if (index == 1) return "" + (int)((FLUX_DISSIPATION_MULT - 1f) * 100f) + "%";
//		if (index == 2) return "" + (int)((1f - PEAK_MULT) * 100f) + "%";
		
//		if (index == 0) return "" + ((Float) speed.get(HullSize.FRIGATE)).intValue();
//		if (index == 1) return "" + ((Float) speed.get(HullSize.DESTROYER)).intValue();
//		if (index == 2) return "" + ((Float) speed.get(HullSize.CRUISER)).intValue();
//		if (index == 3) return "" + ((Float) speed.get(HullSize.CAPITAL_SHIP)).intValue();
//		
//		if (index == 4) return "" + (int)((FLUX_DISSIPATION_MULT - 1f) * 100f);
//		if (index == 5) return "" + (int)((1f - PEAK_MULT) * 100f);
		
		return null;
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
//		return !ship.getVariant().getHullMods().contains("unstable_injector") &&
//			   !ship.getVariant().getHullMods().contains("augmented_engines");
		if (ship.getVariant().getHullSize() == HullSize.CAPITAL_SHIP) return false;
		if (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) return false;
		
		
		return true;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullSize() == HullSize.CAPITAL_SHIP) {
			return "Can not be installed on capital ships";
		}
		if (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) {
			return "Can not be installed on civilian ships";
		}
		
		return null;
	}
	

}
