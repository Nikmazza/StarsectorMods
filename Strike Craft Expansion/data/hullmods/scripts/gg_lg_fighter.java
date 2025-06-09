package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class gg_lg_fighter extends BaseHullMod {

	public static final float ENERGY_DAMAGE_REDUCTION = 0.8f;
	private static final float CAPACITY_MULT = 1.10f;
	private static final float DISSIPATION_MULT = 1.10f;	
        
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getEnergyDamageTakenMult().modifyMult(id, ENERGY_DAMAGE_REDUCTION);
		stats.getFluxCapacity().modifyMult(id, CAPACITY_MULT);
		stats.getFluxDissipation().modifyMult(id, DISSIPATION_MULT);
		//stats.getBeamDamageTakenMult().modifyMult(id, BEAM_DAMAGE_REDUCTION);
	}
//	
//	public String getDescriptionParam(int index, HullSize hullSize) {
//		if (index == 0) return "" + (int) Math.round((1f - CORONA_EFFECT_REDUCTION) * 100f) + "%";
//		if (index == 1) return "" + (int) Math.round((1f - ENERGY_DAMAGE_REDUCTION) * 100f) + "%";
//		return null;
//	}


}
