package data.scripts.vice.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class FluxShunt extends BaseShipSystemScript {

	public static float FLUX_SHUNT_AMOUNT = 1500f;
	public static Object KEY_SHIP = new Object();
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		if (stats.getEntity() instanceof ShipAPI) {
			ShipAPI ship = (ShipAPI) stats.getEntity();
			if (effectLevel == 1f) ship.getFluxTracker().decreaseFlux(FLUX_SHUNT_AMOUNT);			
			else if (effectLevel > 0f && effectLevel < 1f) {
				for (WeaponAPI w : ship.getAllWeapons()) {
					if (w.getSlot().isSystemSlot()) continue;
					if (!w.isDecorative()) w.setForceNoFireOneFrame(true);
				}
			}
			
			if (stats.getVariant().hasHullMod("safetyoverrides") && effectLevel > 0f && effectLevel < 0.8f) {
				ship.getFluxTracker().forceOverload(0.1f);
			}
		}
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			String bonus = "Venting " + (int) FLUX_SHUNT_AMOUNT + " hard flux";
			return new StatusData(bonus, false);	
		}
		return null;
	}
}