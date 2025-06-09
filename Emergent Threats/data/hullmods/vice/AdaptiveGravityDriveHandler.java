package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class AdaptiveGravityDriveHandler extends BaseHullMod {
	
	private static String GRAV_HULLMOD = "vice_adaptive_gravity_drive";
	private static String GRAV_SYSTEM = "vice_fleetjump";
	private static String BASE_SYSTEM = "displacer";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI variant = stats.getVariant();
		if (variant.getNonBuiltInHullmods().contains(GRAV_HULLMOD)) {
			variant.getHullSpec().setShipSystemId(GRAV_SYSTEM);
		}
		else variant.getHullSpec().setShipSystemId(BASE_SYSTEM);
	}
}