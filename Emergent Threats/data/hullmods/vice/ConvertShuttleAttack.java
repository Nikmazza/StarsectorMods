package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.util.BasicUtil;

public class ConvertShuttleAttack extends BaseHullMod {
	
	//Adding/removing shuttles, adjusting launch bay count, and clearing LPCs handled by Vast Launch Bays
	private static int MOD_COST = 40;
	private static int MIN_BAY_COUNT = 2;
	private static String WING_ID = "vice_kite_atk_wing";
	private static String THIS_MOD = "vice_convert_shuttle_attack";
	private static String CONFLICT_MOD_ID = "vice_convert_shuttle_bomber";
	private static String REQUIRED_MOD_ID = "vice_vast_launch_bays";
	
	@Override	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getVariant().getHullMods().remove("vice_adaptive_flight_command");
	}
	
	@Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		if (!hasEmptyBays(ship)) ship.getVariant().getHullMods().remove(id);
	}
	
	private boolean isCorrectHull(ShipAPI ship) {
		return (ship.getVariant().getHullSpec().isBuiltInMod(REQUIRED_MOD_ID));
	}
	
	//if already fitted then always true
	private boolean hasEnoughOP(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(THIS_MOD) || BasicUtil.getRemainingOP(ship) >= MOD_COST) return true;
		return false;
	}
	
	//ignores the shuttles already fitted by this mod
	private boolean hasEmptyBays(ShipAPI ship) {
		boolean isEmpty = true;
		for (int i = 0; i < 2; i++) {
			if (ship.getVariant().getWingId(i) != null && !WING_ID.equals(ship.getVariant().getWingId(i))) isEmpty = false;
		}
		for (int i = 2; i < 6; i++) {
			if (ship.getVariant().getWingId(i) != null) isEmpty = false;
		}
		return isEmpty;
	}
	
	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return true;
	}
	
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		if (isForModSpec || ship == null || ship.getVariant().hasHullMod(THIS_MOD)) return;
		String s = "To install this hullmod, remove all equipped fighters and have %s ordnance points available.";
		tooltip.addPara(s, 10f, Misc.getHighlightColor(), "" + MOD_COST);
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		if (!isCorrectHull(ship)) return false;
		if (!hasEnoughOP(ship)) return false;
		if (!hasEmptyBays(ship)) return false;
		if (ship.getHullSpec().getFighterBays() < MIN_BAY_COUNT) return false;
		return (!ship.getVariant().hasHullMod(CONFLICT_MOD_ID));
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(CONFLICT_MOD_ID)) return "Shuttle conversion already installed";
		if (!isCorrectHull(ship)) return "Incompatible hull";
		if (!hasEnoughOP(ship)) return "Insufficient ordnance points remaining";
		if (!hasEmptyBays(ship)) return "Fighter bays are occupied";
		if (ship.getHullSpec().getFighterBays() < MIN_BAY_COUNT) return "Insufficient number of fighter bays";
		return null;
	}
}