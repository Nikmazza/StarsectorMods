package birdwanderer.birdscollection.data.scripts.shipsystems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class BcRemoteDamperStats extends BaseShipSystemScript {
	public static final Object KEY_JITTER = new Object();
	
	public static final float DAMAGE_TAKEN_MULT = .333f;
	
	public static final Color JITTER_UNDER_COLOR = new Color(255,165,90,155);
	public static final Color JITTER_COLOR = new Color(255,165,90,55);

	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
            return;
		}
		
		
		if (effectLevel > 0) {
			float jitterLevel = effectLevel;
			float maxRangeBonus = 5f;
			float jitterRangeBonus = jitterLevel * maxRangeBonus;
			for (ShipAPI fighter : getFighters(ship)) {
				if (fighter.isHulk()) continue;
				MutableShipStatsAPI fStats = fighter.getMutableStats();
				
				fStats.getEmpDamageTakenMult().modifyMult(id, DAMAGE_TAKEN_MULT);
				fStats.getHullDamageTakenMult().modifyMult(id, DAMAGE_TAKEN_MULT);
				fStats.getArmorDamageTakenMult().modifyMult(id, DAMAGE_TAKEN_MULT);

				ShieldAPI fShields = fighter.getShield();
				if (fShields != null) {
					fShields.toggleOff();
				}

				
				if (jitterLevel > 0) {

					fighter.setJitterUnder(KEY_JITTER, JITTER_COLOR, jitterLevel, 5, 0f, jitterRangeBonus);
					fighter.setJitter(KEY_JITTER, JITTER_UNDER_COLOR, jitterLevel, 2, 0f, 0 + jitterRangeBonus * 1f);
					Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
				}
			}
		}
	}
	
	private List<ShipAPI> getFighters(ShipAPI carrier) {
		List<ShipAPI> result = new ArrayList<ShipAPI>();
		
//		this didn't catch fighters returning for refit		
//		for (FighterLaunchBayAPI bay : carrier.getLaunchBaysCopy()) {
//			if (bay.getWing() == null) continue;
//			result.addAll(bay.getWing().getWingMembers());
//		}
		
		for (ShipAPI ship : Global.getCombatEngine().getShips()) {
			if (!ship.isFighter()) continue;
			if (ship.getWing() == null) continue;
			if (ship.getWing().getSourceShip() == carrier) {
				result.add(ship);
			}
		}
		
		return result;
	}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}
		for (ShipAPI fighter : getFighters(ship)) {
			if (fighter.isHulk()) continue;
			MutableShipStatsAPI fStats = fighter.getMutableStats();
			fStats.getEmpDamageTakenMult().unmodify(id);
			fStats.getHullDamageTakenMult().unmodify(id);
			fStats.getArmorDamageTakenMult().unmodify(id);


		}
	}
	
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float percent = DAMAGE_TAKEN_MULT * effectLevel;
		if (index == 0) {
			//return new StatusData("+" + (int)percent + "% fighter damage taken", false);
			return new StatusData("" + Misc.getRoundedValueMaxOneAfterDecimal(1f * DAMAGE_TAKEN_MULT) + "x fighter damage taken", false);
		}
		return null;
	}

	
}








