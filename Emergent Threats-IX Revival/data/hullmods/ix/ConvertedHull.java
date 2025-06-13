package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class ConvertedHull extends BaseHullMod {

	private static float MIN_CREW = 100f;
	private static float MAX_CREW = 150f;
	private static float SHIELD_PENALTY = 33.3333f;
	private static int SYSTEM_CHARGES_PENALTY = 1; //text only, new value set by entry in ship_systems.csv
	
	private static String FTR_HULLMOD = "vice_adaptive_flight_command";
	private static String FTR_SYSTEM = "vice_targetingsweep";
	private static String GRAV_HULLMOD = "vice_adaptive_gravity_drive";
	private static String GRAV_SYSTEM = "vice_fleetjump";
	private static String BASE_SYSTEM = "displacer_degraded";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMinCrewMod().modifyFlat(id, MIN_CREW);
		stats.getMaxCrewMod().modifyFlat(id, MAX_CREW);
		stats.getShieldDamageTakenMult().modifyMult(id, 1f + SHIELD_PENALTY * 0.01f);
		
		//system swap for Radiant IX/TW adaptive flight command or adaptive gravity drive
		ShipVariantAPI variant = stats.getVariant();
		if (variant.getNonBuiltInHullmods().contains(FTR_HULLMOD)) {
			variant.getHullSpec().setShipSystemId(FTR_SYSTEM);
		}
		else if (variant.getNonBuiltInHullmods().contains(GRAV_HULLMOD)) {
			variant.getHullSpec().setShipSystemId(GRAV_SYSTEM);
		}
		else variant.getHullSpec().setShipSystemId(BASE_SYSTEM);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) MIN_CREW;
		if (index == 1) return "" + (int) MAX_CREW;
		if (index == 2) return "" + (int) Math.ceil(SHIELD_PENALTY) + "%";
		if (index == 3) return "" + SYSTEM_CHARGES_PENALTY;
		return null;
	}
}