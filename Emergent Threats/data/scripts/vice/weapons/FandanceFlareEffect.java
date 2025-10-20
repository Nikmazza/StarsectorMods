package data.scripts.vice.weapons;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import data.scripts.vice.util.ShapedExplosionUtil;

public class FandanceFlareEffect implements OnFireEffectPlugin, OnHitEffectPlugin {
	
	private static Color CORE_COLOR = new Color(200,225,255,50);
	private static Color FLARE_COLOR = new Color(5,135,175,50);
	private static Color EXHAUST_COLOR = new Color(5,100,125,255);
	private static float MAX_ANGLE_DEFLECT = 135f;
	
	public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine) {
		float angle = weapon.getCurrAngle() - 180f;
		Vector2f projLoc = MathUtils.getPointOnCircumference(proj.getLocation(), 4f, angle);
		Vector2f projLoc2 = proj.getLocation();
		ShipAPI ship = weapon.getShip();
		
        engine.addHitParticle(
				projLoc2,
				ship.getVelocity(),
				30.0f, //size
				1.0f, //brightness
				0.1f, //duration
				FLARE_COLOR);
		
		engine.addHitParticle(
				projLoc,
				ship.getVelocity(),
				15.0f, //size
				1.0f, //brightness
				0.05f, //duration
				CORE_COLOR);
		
		if (weapon.getBurstFireTimeRemaining() < 0.4f && weapon.getBurstFireTimeRemaining() > 0.1f) {
			Vector2f loc = MathUtils.getPointOnCircumference(weapon.getLocation(), 8f, angle);
			Vector2f shipVelocity = weapon.getShip().getVelocity();
			float speed = (float) Math.sqrt(shipVelocity.lengthSquared());
			ShapedExplosionUtil.spawnShapedExplosion(loc, angle, speed, EXHAUST_COLOR, "deadeye");
		}
	}
	
	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldHit, 
						ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		if (target instanceof ShipAPI) {
			ShipAPI ship = (ShipAPI) target;
			if (ship.getHullSize().equals(HullSize.FIGHTER)) {
				float facing = ship.getFacing();
				float rand = ((float) Math.random());
				boolean isFlameOut = rand < 0.15f || rand > 0.85f; //30%, from both extremes
				rand -= 0.5f;
				ship.setFacing(facing + (rand * MAX_ANGLE_DEFLECT));
				if (isFlameOut) {
					ShipEngineControllerAPI ec = ship.getEngineController();
					List<ShipEngineAPI> engines = new ArrayList<ShipEngineAPI>(ec.getShipEngines());
					for (ShipEngineAPI engine : engines) {
						engine.disable();
					}
				}
			}
		}
	}
}