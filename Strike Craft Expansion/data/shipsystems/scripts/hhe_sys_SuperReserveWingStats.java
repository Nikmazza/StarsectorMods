package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;

public class hhe_sys_SuperReserveWingStats extends BaseShipSystemScript {
	
	public static String RD_NO_EXTRA_CRAFT = "rd_no_extra_craft";
	public static String RD_FORCE_EXTRA_CRAFT = "rd_force_extra_craft";
	
	public static float EXTRA_FIGHTER_DURATION = 90;
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
		
		if (effectLevel == 1) {
			for (FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()) {
				if (bay.getWing() == null) continue;
				
				bay.makeCurrentIntervalFast();
				FighterWingSpecAPI spec = bay.getWing().getSpec();
				
				int addForWing = getAdditionalFor(spec);
				int maxTotal = spec.getNumFighters() + addForWing;
				int actualAdd = maxTotal - bay.getWing().getWingMembers().size();
				actualAdd = Math.min(spec.getNumFighters(), actualAdd);
				if (actualAdd > 0) {
					bay.setFastReplacements(bay.getFastReplacements() + addForWing);
					bay.setExtraDeployments(actualAdd);
					bay.setExtraDeploymentLimit(maxTotal);
					bay.setExtraDuration(EXTRA_FIGHTER_DURATION);
				}
			}
		}
	}
	
	public static int getAdditionalFor(FighterWingSpecAPI spec) {
		if (spec.isBomber() && !spec.hasTag(RD_FORCE_EXTRA_CRAFT)) return 0;
		if (spec.hasTag(RD_NO_EXTRA_CRAFT)) return 0;
		
		int size = spec.getNumFighters();
		if (size <= 1) return 1;
		if (size <= 2) return 2;
		if (size <= 3) return 3;
		if (size <= 4) return 4;
		if (size <= 5) return 5;
		return 6;
	}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
	}
	

	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		return null;
	}


	@Override
	public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
		return true;
	}
	

	
}








