package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

import data.scripts.vice.util.RemoveSpecialFightersUtil;

public class VastLaunchBays extends BaseHullMod {
	
	private static String ATK_WING_ID = "vice_kite_atk_wing";
	private static String BMR_WING_ID = "vice_kite_bmr_wing";
	
	@Override	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean isAtk = false;
		boolean isBmr = false;
		ShipVariantAPI variant = stats.getVariant();
		if (variant.hasHullMod("vice_convert_shuttle_attack")) isAtk = true;
		else if (variant.hasHullMod("vice_convert_shuttle_bomber")) isBmr = true;

		if (isAtk || isBmr) stats.getNumFighterBays().setBaseValue(2f);
		else stats.getNumFighterBays().unmodify(id);
		
		RemoveSpecialFightersUtil.deleteSpecialLPCsFromHull(stats.getVariant(), id);
	}
	
	@Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		boolean isAtk = false;
		boolean isBmr = false;
		ShipVariantAPI variant = ship.getVariant();
		if (variant.hasHullMod("vice_convert_shuttle_attack")) isAtk = true;
		else if (variant.hasHullMod("vice_convert_shuttle_bomber")) isBmr = true;
		
		if (isAtk && variant.getWingId(0) == null) variant.setWingId(0, ATK_WING_ID);
		if (isAtk && variant.getWingId(1) == null) variant.setWingId(1, ATK_WING_ID);
		
		if (isBmr && variant.getWingId(0) == null) variant.setWingId(0, BMR_WING_ID);
		if (isBmr && variant.getWingId(1) == null) variant.setWingId(1, BMR_WING_ID);
		
		if (!isAtk && !isBmr) {
			if (ATK_WING_ID.equals(variant.getWingId(0)) || BMR_WING_ID.equals(variant.getWingId(0))) 
				variant.setWingId(0, null);
			if (ATK_WING_ID.equals(variant.getWingId(1)) || BMR_WING_ID.equals(variant.getWingId(1))) 
				variant.setWingId(1, null);
		}
		
		deleteLPC(ATK_WING_ID);
		deleteLPC(BMR_WING_ID);
	}

	private void deleteLPC(String wing) {
		try {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			for (CargoStackAPI s : cargo.getStacksCopy()) {
				if (s.isFighterWingStack() && s.getFighterWingSpecIfWing().getId().equals(wing)) cargo.removeStack(s);
			}
		}
		catch (Exception e) {}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Converted Shuttles (Attack)";
		if (index == 1) return "Converted Shuttles (Bomber)";
		return null;
	}
}
