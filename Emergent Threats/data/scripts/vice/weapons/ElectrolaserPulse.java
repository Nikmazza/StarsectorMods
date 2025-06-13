package data.scripts.vice.weapons;

import java.awt.Color;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import data.scripts.vice.util.DistanceUtil;

public class ElectrolaserPulse implements BeamEffectPlugin {

	private static float HIT_DAMAGE = 250f;
	private static float EMP_DAMAGE = 500f;
	private static float EXPLOSION_RANGE = 300f;
	private static Color EMP_COLOR = new Color(200,75,200,200);
	private static Color CORE_COLOR = new Color(200,200,255,200);
	private boolean applied = false;
	
	private IntervalUtil tracker = new IntervalUtil(1.99f, 1.99f);
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		if (applied) return;
		tracker.advance(amount);
		
		//on hit emp burst to all targets in range
		if (tracker.intervalElapsed()) {
			Vector2f point = beam.getRayEndPrevFrame();
			Vector2f weaponLoc = beam.getWeapon().getLocation();
			ShipAPI thisShip = beam.getSource();
			renderExplosion(point, weaponLoc, thisShip, engine);
			applied = true;
		}
	}
	
	private void renderExplosion(Vector2f point, Vector2f weaponLoc, ShipAPI thisShip, CombatEngineAPI engine) {
		for (int i = 0; i < 2; i++) {
			engine.spawnEmpArcVisual(
					weaponLoc, //from
					thisShip, //from anchor
					point, //to
					null, //to anchor
					15f, //thickness
					EMP_COLOR, //fringe color
					CORE_COLOR //core color
			);
		}
		
		Global.getSoundPlayer().playSound("vice_electrolaser_pulse", 1f, 1f, point, new Vector2f());
		
		//if player is equipped with more than 1 feedback weapon, each weapon causes feedback on pulse firing
		if (thisShip.getVariant().hasHullMod("vice_feedback_error")) {
			engine.spawnEmpArcPierceShields(
					thisShip, //damage source
					weaponLoc, //to
					thisShip, //to anchor 
					thisShip, //target
					DamageType.ENERGY, 
					HIT_DAMAGE, // damage
					EMP_DAMAGE, // emp 
					100000f, // max range 
					"", //sound
					15f, //thickness
					EMP_COLOR, //fringe color
					CORE_COLOR //core color
			);
		}
		
		//spawnDecoSphere(point, weaponLoc, thisShip, engine);
		spawnDecoSphere(engine, thisShip, point);
		List<CombatEntityAPI> enemies = DistanceUtil.getAllEnemiesInRange(point, EXPLOSION_RANGE, thisShip);
		for (CombatEntityAPI enemy : enemies) {
			engine.spawnEmpArc(
					thisShip, //damage source
					point, //to
					null, //to anchor 
					enemy, //target
					DamageType.ENERGY, 
					HIT_DAMAGE, // damage
					EMP_DAMAGE, // emp 
					100000f, // max range 
					"", //sound
					15f, //thickness
					EMP_COLOR, //fringe color
					CORE_COLOR //core color
			);
		}
	}
	
	private static void spawnDecoSphere(CombatEngineAPI engine, ShipAPI thisShip, Vector2f point) {
		MagicLensFlare.createSharpFlare(
				engine,
				thisShip,
				point,
				6,		//thickness
				400,	//length
				0,		//angle
				EMP_COLOR,	//fringe
				CORE_COLOR	//core
		);
		MagicLensFlare.createSharpFlare(
				engine,
				thisShip,
				point,
				4,		//thickness
				300,	//length
				60,		//angle
				EMP_COLOR,	//fringe
				CORE_COLOR	//core
		);
		MagicLensFlare.createSharpFlare(
				engine,
				thisShip,
				point,
				4,		//thickness
				300,	//length
				300,		//angle
				EMP_COLOR,	//fringe
				CORE_COLOR	//core
		);
	}
	
	//renders six lightning bolts in a star pattern
	/**
	private static void spawnDecoSphere(Vector2f point, Vector2f weaponLoc, ShipAPI thisShip, CombatEngineAPI engine) {
		float pointX = point.getX();
		float pointY = point.getY();
		float weaponX = weaponLoc.getX();
		float weaponY = weaponLoc.getY();
		float angle = (float) Math.atan2(weaponY - pointY, weaponX - pointX);
		float angleDiff = 1.05f; //*6 = 2pi
		float distance = 150f;
		for (int i = 0; i < 6; i++) {
			float x = pointX + (float) (distance * Math.cos(angle));
			float y = pointY + (float) (distance * Math.sin(angle));
			Vector2f endPoint = new Vector2f(x, y);
			engine.spawnEmpArcVisual(
					endPoint, 
					thisShip, //from anchor
					point, 
					thisShip, //to anchor 
					15f, // thickness
					EMP_COLOR, //fringe color
					CORE_COLOR //core color
			);
		angle += angleDiff; //radians
		}
	}
	**/
}