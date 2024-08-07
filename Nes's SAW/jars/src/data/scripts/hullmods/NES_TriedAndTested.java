package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import static data.scripts.utils.NES_Util.txt;

public class NES_TriedAndTested extends BaseHullMod {

	public static int SMOD_BONUS = 1;


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat(id, SMOD_BONUS);
	}


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) SMOD_BONUS + "";
		return null;
	}
}