package birdwanderer.birdscollection.data.scripts.shipsystems;

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

public class BcHyperboost2Stats extends BaseShipSystemScript {

	public static float ROF_MULT = 2f;
	public static float MAX_TIME_MULT = 30f;
	public static float SPD_MULT = 0f;
	public static float ARC_DAMAGE = 10f;
	public static float ARC_EMP_DAMAGE = 80f;
	public static float ARC_RANGE = 500f;
	public static float ARC_CHANCE = 0.9f;
	public static Color ARC_COLOR = new Color(243, 222, 87);
	public static boolean ZAPPY_ZAPPY = true;
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;

		stats.getBallisticRoFMult().modifyMult(id, ROF_MULT);
		stats.getEnergyRoFMult().modifyMult(id, ROF_MULT);
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
		}

		for (WeaponSlotAPI weaponSlotAPI : ship.getHullSpec().getAllWeaponSlotsCopy()) {
			//wpn slots
			if (!weaponSlotAPI.isSystemSlot()) {
				continue;
			}

			//arc p/frame
			if (Math.pow((1f - ARC_CHANCE), Global.getCombatEngine().getElapsedInLastFrame()) >= (Math.random() * effectLevel)) {
				continue;
			}

			//position of slot
			Vector2f point = weaponSlotAPI.computePosition(ship);

			CombatEntityAPI targetEntity = null;
			List<ShipAPI> shipsWithinRange = CombatUtils.getShipsWithinRange(point, ARC_RANGE);
			List<CombatEntityAPI> removeList = new ArrayList<CombatEntityAPI>();
			for (ShipAPI testShip : shipsWithinRange) {
				if (testShip.getOwner() == ship.getOwner() || testShip.isPhased() || testShip.getCollisionClass().equals(CollisionClass.NONE)) {
					removeList.add(testShip);
				}
			}
			List<MissileAPI> missilesWithinRange = CombatUtils.getMissilesWithinRange(point, ARC_RANGE);
			for (MissileAPI testMissile : missilesWithinRange) {
				if (testMissile.getOwner() == ship.getOwner() || testMissile.getCollisionClass().equals(CollisionClass.NONE)) {
					removeList.add(testMissile);
				}
			}

			for (CombatEntityAPI removeThing : removeList) {
				if (shipsWithinRange.contains(removeThing)) {
					shipsWithinRange.remove(removeThing);
				}
				if (missilesWithinRange.contains(removeThing)) {
					missilesWithinRange.remove(removeThing);
				}
			}


			//targeting
			float damageNuller = 1f;
			if (missilesWithinRange.isEmpty() && shipsWithinRange.isEmpty()) {
				damageNuller = 0f;

				if (ZAPPY_ZAPPY) {
					targetEntity = new SimpleEntity(MathUtils.getRandomPointOnCircumference(point, ARC_RANGE));
				} else {
					targetEntity = ship;
				}
			} else if (!missilesWithinRange.isEmpty() && (shipsWithinRange.isEmpty() || MathUtils.getRandomNumberInRange(0, missilesWithinRange.size() + shipsWithinRange.size()) < missilesWithinRange.size())) {
				targetEntity = (CombatEntityAPI)missilesWithinRange.get(MathUtils.getRandomNumberInRange(0, missilesWithinRange.size()-1));
			} else {
				targetEntity = (CombatEntityAPI)shipsWithinRange.get(MathUtils.getRandomNumberInRange(0, shipsWithinRange.size()-1));
			}

			Global.getCombatEngine().spawnEmpArc(ship, point, ship, targetEntity,
					DamageType.ENERGY, //dmg type
					ARC_DAMAGE * damageNuller, //dmg
					ARC_EMP_DAMAGE * damageNuller, //emp
					100000f, //range
					"tachyon_lance_emp_impact", //sound
					MathUtils.getRandomNumberInRange(5f, 8f), // thickness
					new Color(243, 222, 87), //central clr
					ARC_COLOR //fringe clr
			);
		}
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getTimeMult().unmodify(id);
		stats.getEnergyRoFMult().unmodify(id);
		stats.getBallisticRoFMult().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData("begin: incinerate", false);
		}
		return null;
	}
}
