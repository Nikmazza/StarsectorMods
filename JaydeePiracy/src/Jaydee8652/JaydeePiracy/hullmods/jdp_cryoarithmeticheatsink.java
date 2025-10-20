package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class jdp_cryoarithmeticheatsink extends BaseHullMod {

	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		for (ShipAPI module : ship.getChildModulesCopy()) {
			module.getMutableStats().getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, 0);
		}
	}

	@Override
	public String getSModDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		if (index == 0) return "" + (int) 0 + "%";
		return null;
	}

}



