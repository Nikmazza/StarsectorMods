package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

import data.scripts.vice.util.BasicUtil;

public class ConvertShuttleDEM extends BaseHullMod {
	
	private static int MIN_OP_REMAINING = 10;
	private static int SHUTTLE_BAY_ID = 3;
	
	private static String DEM_WING_ID = "vice_kite_dem_wing";
	private static String DEM_HULL_ID = "vice_proteus";
	private static String DEM_HULL_ID_DMOD = "vice_proteus_default_D";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI variant = stats.getVariant();
		variant.setWingId(SHUTTLE_BAY_ID, DEM_WING_ID);
		variant.getHullMods().remove(id);
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

	}
	
	private boolean isCorrectHull(ShipAPI ship) {
		return (ship.getVariant().getHullSpec().getHullId().equals(DEM_HULL_ID) 
				||ship.getVariant().getHullSpec().getHullId().equals(DEM_HULL_ID_DMOD));
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		if (BasicUtil.getRemainingOP(ship) < MIN_OP_REMAINING) return false;
		if (ship.getVariant().getWingId(SHUTTLE_BAY_ID) != null) return false;
		if (ship.getHullSpec().getFighterBays() < SHUTTLE_BAY_ID + 1) return false;
		return isCorrectHull(ship);
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (!isCorrectHull(ship)) return "Incompatible hull";
		if (BasicUtil.getRemainingOP(ship) < MIN_OP_REMAINING) return "Insufficient ordnance points remaining";
		if (ship.getVariant().getWingId(SHUTTLE_BAY_ID) != null) return "Bay 4 is occupied";
		if (ship.getHullSpec().getFighterBays() < SHUTTLE_BAY_ID + 1) return "Bay 4 is not present";
		return null;
	}	
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}
}