package data.scripts.orr.weapons;

import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

import data.scripts.vice.util.ShapedExplosionUtil;

public class HeavyAdjudicatorOnFire implements OnFireEffectPlugin {

	private static Color GLOW_COLOR = new Color(255,100,65,200);
	private static Color GLOW_COLOR_MISFIRE = new Color(255,100,65,200); //same color for now
	private static float MISFIRE_ODDS = 0.02f;
	private static float STRUCTURE_LOSS = 500f;
	
	public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine) {
		ShipAPI ship = weapon.getShip();
		if (ship == null) return;
		
		float speed = 0f;
		float angle = weapon.getCurrAngle(); // - 90f / + 90f
		Vector2f loc0 = MathUtils.getPointOnCircumference(weapon.getLocation(), 22f, angle);
		Vector2f loc = MathUtils.getPointOnCircumference(weapon.getLocation(), -5f, angle);
		
		float angle1 = weapon.getCurrAngle() - 90f;
		float angle2 = weapon.getCurrAngle() + 90f;
		Vector2f loc1 = MathUtils.getPointOnCircumference(loc, 12f, angle1);
		Vector2f loc2 = MathUtils.getPointOnCircumference(loc, 12f, angle2);
		Color plumeColor = GLOW_COLOR;
		
		if ((float) Math.random() <= MISFIRE_ODDS) {
			float newHP = ship.getHitpoints() - STRUCTURE_LOSS;
			if (newHP > 1) ship.setHitpoints(newHP);
			else engine.applyDamage(ship, ship.getLocation(), 10000f, proj.getDamageType(), 0f, true, false, ship);
			engine.addFloatingDamageText(loc, STRUCTURE_LOSS, Color.red, ship, ship);
			speed = 50f; //not actual ship speed but used for special effects on misfire
			plumeColor = GLOW_COLOR_MISFIRE;
		}
		
		ShapedExplosionUtil.spawnShapedExplosion(loc0, angle, speed, plumeColor, "har");
		ShapedExplosionUtil.spawnShapedExplosion(loc1, angle1, speed, plumeColor, "hars");
		ShapedExplosionUtil.spawnShapedExplosion(loc2, angle2, speed, plumeColor, "hars");
	}
}