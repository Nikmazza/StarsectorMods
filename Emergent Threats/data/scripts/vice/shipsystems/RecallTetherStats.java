package data.scripts.vice.shipsystems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class RecallTetherStats extends BaseShipSystemScript {

	public static Object KEY_JITTER = new Object();
	public static Color JITTER_COLOR = new Color(100,165,255,155);
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI fighter = null;
		ShipAPI carrier = null;
		if (stats.getEntity() instanceof ShipAPI) {
			fighter = (ShipAPI) stats.getEntity();
			if (fighter.getWing() != null && fighter.getWing().getSourceShip() instanceof ShipAPI) {
				carrier = fighter.getWing().getSourceShip();
			}
		}
		if (fighter == null || carrier == null) return;
		
		if (effectLevel > 0) {

			if (fighter.isHulk() || carrier.isHulk()) return;
				
			float maxRangeBonus = fighter.getCollisionRadius() * 1f;
			float jitterRangeBonus = 5f + effectLevel * maxRangeBonus;
			fighter.setJitter(KEY_JITTER, JITTER_COLOR, effectLevel, 10, 0f, jitterRangeBonus);
			
			//if (fighter.isAlive()) fighter.setPhased(true);
				
			if (state == State.IN) {
				float alpha = 1f - effectLevel * 0.5f;
				fighter.setExtraAlphaMult(alpha);
			}
	
			if (effectLevel == 1) {
				if (carrier != null && fighter != null && fighter.getWing() != null) {
					fighter.getWing().getSource().makeCurrentIntervalFast();
					fighter.getWing().getSource().land(fighter);
					//fighter.setExtraAlphaMult(1);
				}
			}
		}
	}
		
	public void unapply(MutableShipStatsAPI stats, String id) {

	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		return null;
	}
}