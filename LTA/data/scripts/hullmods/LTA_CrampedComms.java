package data.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class LTA_CrampedComms extends BaseHullMod {
	public static final float ENGAGEMENT_REDUCTION = 0.5f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
		
		stats.getFighterWingRange().modifyMult(id, 1f - ENGAGEMENT_REDUCTION * effect);
	}
		
	public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		float effect = 1f;
		
		if (index == 0) return "" + (int) Math.round(ENGAGEMENT_REDUCTION * 100f * effect) + "%";
		return null;
	}
}




