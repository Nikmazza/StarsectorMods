package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class BcHunterPackage extends BaseHullMod {

	public static final float DAMAGE_TO_CAPITAL = 1.25f;

	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDamageToCapital().modifyMult(id, DAMAGE_TO_CAPITAL);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round((1f - DAMAGE_TO_CAPITAL) * 100f) + "%";
		return null;
	}


}
