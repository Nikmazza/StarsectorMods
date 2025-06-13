package data.scripts.vice.shipsystems;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.util.Misc;

public class DEMStrike extends BaseShipSystemScript {
	
	private static String DEM_WING_ID = "vice_terminator_dem_wing";

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) ship = (ShipAPI) stats.getEntity();
		else return;
		
		if (effectLevel > 0) {
			for (ShipAPI fighter : getFighters(ship)) {
				for (WeaponAPI w : fighter.getAllWeapons()) {
					if (w.getSize().equals(WeaponSize.MEDIUM)) w.setAmmo(1);
				}
			}
		}
	}
	
	private List<ShipAPI> getFighters(ShipAPI carrier) {
		List<ShipAPI> result = new ArrayList<ShipAPI>();	
		for (ShipAPI ship : Global.getCombatEngine().getShips()) {
			if (!ship.isFighter() || ship.getWing() == null || ship.isHulk()) continue;
			if (!ship.getWing().getWingId().equals(DEM_WING_ID)) continue;
			if (ship.getWing().getSourceShip() == carrier) result.add(ship);
		}
		return result;
	}
	
	public void unapply(MutableShipStatsAPI stats, String id) {

	}
		
	public StatusData getStatusData(int index, State state, float effectLevel) {
		return null;
	}
}