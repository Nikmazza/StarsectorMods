//emp arcs, dmg resist, and very fast speed for short time
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


public class BcRamBurnStats1 extends BaseShipSystemScript {


	public static float ARC_DAMAGE = 80f;
	public static float ARC_EMP_DAMAGE = 80f;
	public static float ARC_RANGE = 500f;
	public static float ACCEL_MULT = 40f;
	public static float ARC_CHANCE = 0.9f;

	public static float SPEED_MULT = 12f;
	public static final float DAMAGE_MULT = 0.5f;

	public static Color ARC_COLOR = new Color(255, 93, 35);
	public static boolean ZAPPY_ZAPPY = false;

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
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
					MathUtils.getRandomNumberInRange(15f, 18f), // thickness
					new Color(255, 93, 35), //central clr
					ARC_COLOR //fringe clr
			);
		}

		float level = effectLevel;

		//Burndrive-related code
		if (state == State.OUT) {
			stats.getMaxSpeed().unmodify(id);
		} else {
			stats.getMaxSpeed().modifyMult(id, SPEED_MULT);
			stats.getAcceleration().modifyMult(id, ACCEL_MULT);
			stats.getHullDamageTakenMult().modifyMult(id, DAMAGE_MULT);
			stats.getArmorDamageTakenMult().modifyMult(id, DAMAGE_MULT);
		}
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

		stats.getMaxSpeed().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
	}
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData("thrust maxed", false);
		}
		if (index == 1) {
			return new StatusData("reactor overfill, engaging energy emitters", false);
		}
		if (index == 2) {
			return new StatusData("hull/armor damage reduced by 50%", false);
		}
		return null;
	}
}