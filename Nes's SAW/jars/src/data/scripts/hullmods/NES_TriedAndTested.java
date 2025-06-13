package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import static data.scripts.utils.NES_Util.txt;

public class NES_TriedAndTested extends BaseHullMod {

	//public static float DEPLOYMENT_COST_MULT = 0.5f;
	public static boolean AlLOW_CONVERTED_HANGAR = true;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//stats.getSuppliesToRecover().modifyMult(id, DEPLOYMENT_COST_MULT);

		if (AlLOW_CONVERTED_HANGAR) {
			stats.getDynamic().getMod(Stats.FORCE_ALLOW_CONVERTED_HANGAR).modifyFlat(id, 1f);
			stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_CREW_INCREASE).modifyFlat(id, 1f);
			stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_REARM_INCREASE).modifyFlat(id, 1f);
			stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_REFIT_PENALTY).modifyFlat(id, 1f);
			//stats.getDynamic().getMod(Stats.CONVERTED_HANGAR_NO_DP_INCREASE).modifyFlat(id, 1f);
		}
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		//if (index == 0) return "" + (int) Math.round((1f - DEPLOYMENT_COST_MULT) * 100f) + "%";
		if (index == 0) return "" + txt("hullmod_triedandtested");
		if (index == 1) return "" + (int)Math.round(2);

		return null;
	}
}