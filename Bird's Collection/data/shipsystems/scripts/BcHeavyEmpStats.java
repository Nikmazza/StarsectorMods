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
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;

public class BcHeavyEmpStats extends BaseShipSystemScript {

	public static float ARC_DAMAGE = 200f;
	public static float ARC_EMP_DAMAGE = 5000f;
	public static float ARC_RANGE = 700f;
	public static Color ARC_COLOR = new Color(221, 88, 0);
	public static boolean ZAPPY_ZAPPY = true;
	boolean firedOnce = false;

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
			//is slot a system?
			if (!weaponSlotAPI.isSystemSlot()) {
				continue;
			}

			//position of slot
			Vector2f point = weaponSlotAPI.computePosition(ship);

			CombatEntityAPI targetEntity = null;
			List<ShipAPI> shipsWithinRange = CombatUtils.getShipsWithinRange(point, ARC_RANGE);
			List<CombatEntityAPI> removeList = new ArrayList<CombatEntityAPI>();
			for (ShipAPI testShip : shipsWithinRange) {
				if (testShip.getOwner() == ship.getOwner() || testShip.isHulk() || testShip.isFighter() || testShip.isPiece() || testShip.isPhased() || testShip.getCollisionClass().equals(CollisionClass.NONE)) {
					removeList.add(testShip);
				}
			}


			for (CombatEntityAPI removeThing : removeList) {
				if (shipsWithinRange.contains(removeThing)) {
					shipsWithinRange.remove(removeThing);
				}
			}


			//targeting
			float damageNuller = 1f;
			if (shipsWithinRange.isEmpty()) {
				damageNuller = 0f;

				if (ZAPPY_ZAPPY) {
					targetEntity = new SimpleEntity(MathUtils.getRandomPointOnLine(point, point));
				}

			} else {
				targetEntity = (CombatEntityAPI)shipsWithinRange.get(MathUtils.getRandomNumberInRange(0, shipsWithinRange.size()-1));
			}

			if (firedOnce == false) {
				Global.getCombatEngine().spawnEmpArc(ship, point, ship, targetEntity,
						DamageType.ENERGY, //dmg type
						ARC_DAMAGE * damageNuller, //dmg
						ARC_EMP_DAMAGE * damageNuller, //emp
						100000f, //range
						"tachyon_lance_emp_impact", //sound
						50f, // thickness
						new Color(255, 195, 181), //central clr
						ARC_COLOR //fringe clr
				);
				firedOnce = true;
			}
		}
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		firedOnce = false;
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData("emp emitter engaged", false);
		}
		return null;
	}
}
