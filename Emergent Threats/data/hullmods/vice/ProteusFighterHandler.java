package data.hullmods.vice;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import data.scripts.vice.util.RemoveSpecialFightersUtil;

public class ProteusFighterHandler extends BaseHullMod {

	private static int SHUTTLE_INDEX = 3;
	private static String WING_ID = "vice_kite_dem_wing";
	private static String WING_ID_BACKUP = "gladius_wing";
	private static String WING_VARIANT = "vice_kite_twindem";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		List<String> wings = stats.getVariant().getWings();
		for (int i = 0; i < wings.size(); i++) {
			if (i == SHUTTLE_INDEX) continue;
			if (wings.get(i).equals(WING_ID)) stats.getVariant().setWingId(i, WING_ID_BACKUP);
		}
		RemoveSpecialFightersUtil.deleteSpecialLPCsFromHull(stats.getVariant(), id);
	}
	
	@Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		try {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			for (CargoStackAPI s : cargo.getStacksCopy()) {
				if (s.isFighterWingStack() && s.getFighterWingSpecIfWing().getVariantId().equals(WING_VARIANT)) cargo.removeStack(s);
			}
		}
		catch (Exception e) {}
	}
}