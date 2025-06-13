package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class DomRes_Info extends BaseHullMod {


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "once in a more restored state,";
		return null;
	}
}
