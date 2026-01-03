package Jaydee8652.JaydeePiracy.scripts.systems;

import java.awt.Color;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.combat.entities.Ship;

public class jdp_Radiators extends BaseShipSystemScript {
	public static final float MAX_TIME_MULT = 3f;
	public static final float MIN_TIME_MULT = 0.1f;
	public static final float DAM_MULT = 0.1f;
	public static float extendedAmount = 0;

	public static final Color JITTER_COLOR = new Color(125,125,200,55);
	public static final Color JITTER_UNDER_COLOR = new Color(125,125,200,155);

	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}

		if (state == State.IN) {
			extendedAmount = effectLevel / (ship.getSystem().getChargeUpDur());
			if (extendedAmount > 1) {
				extendedAmount = 1f;
			}
		} else if (state == State.OUT) {
			extendedAmount = -effectLevel / (ship.getSystem().getChargeUpDur());
			if (extendedAmount < 0) {
				extendedAmount = 0f;
			}
		}

		List<ShipAPI> modules = ship.getChildModulesCopy();
		for (ShipAPI module :modules) {
			WeaponSlotAPI slot = module.getStationSlot();
			slot.setAngle(slot.getArc() * extendedAmount);}
	}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}

		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getTimeMult().unmodify(id);
		
//		stats.getHullDamageTakenMult().unmodify(id);
//		stats.getArmorDamageTakenMult().unmodify(id);
//		stats.getEmpDamageTakenMult().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
		if (index == 0) {
			return new StatusData("time flow altered", false);
		}
//		if (index == ) {
//			return new StatusData("increased speed", false);
//		}
//		if (index == 1) {
//			return new StatusData("increased acceleration", false);
//		}
		return null;
	}
}








