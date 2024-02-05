package data.shipsystems.scripts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;

public class BcBehindYouStats extends BaseShipSystemScript {

	public static float MAX_TIME_MULT = 30f;
	public static float SPD_MULT = 2f;
	public static Color SMOKE_COLOR = new Color(129, 129, 129);

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;

		stats.getMaxSpeed().modifyMult(id, SPD_MULT);

		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}

		float TimeMult = 1f + (MAX_TIME_MULT-1f)*effectLevel;
		stats.getTimeMult().modifyMult(id, TimeMult);
		if (player) {
			Global.getCombatEngine().getTimeMult().modifyMult(id, (1f / TimeMult));
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
		};
		}

	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getTimeMult().unmodify(id);
	}

	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData("begin: incinerate", false);
		}
		return null;
	}
}
