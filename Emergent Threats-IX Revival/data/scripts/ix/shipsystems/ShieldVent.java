package data.scripts.ix.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class ShieldVent extends BaseShipSystemScript {
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		if (effectLevel == 1f && stats.getEntity() instanceof ShipAPI) {
			ShipAPI ship = (ShipAPI) stats.getEntity();
			ship.getFluxTracker().setHardFlux(0f);
			ship.getFluxTracker().setCurrFlux(0f);
		}
	}
}