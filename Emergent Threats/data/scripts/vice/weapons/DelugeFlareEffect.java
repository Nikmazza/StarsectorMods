package data.scripts.vice.weapons;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
//import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.util.ShapedExplosionUtil;

public class DelugeFlareEffect implements OnFireEffectPlugin, OnHitEffectPlugin {
	
	private static Color CORE_COLOR = new Color(255,225,255,50);
	private static Color FLARE_COLOR = new Color(255,230,60,50);
	private static Color EXHAUST_COLOR = new Color(255,230,60,255);
	private static float MAX_ANGLE_DEFLECT = 135f;
	private static float ON_HIT_MULT = 0.25f;
	
	public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine) {
		//float angle = weapon.getCurrAngle();
		//Vector2f projLoc = MathUtils.getPointOnCircumference(proj.getLocation(), 10f, angle);
		Vector2f projLoc = proj.getLocation();
		ShipAPI ship = weapon.getShip();
		engine.addHitParticle(
				projLoc,
				ship.getVelocity(),
				15.0f, //size
				1.0f, //brightness
				0.1f, //duration
				CORE_COLOR);
        engine.addHitParticle(
				projLoc,
				ship.getVelocity(),
				30.0f, //size
				1.0f, //brightness
				0.2f, //duration
				FLARE_COLOR);
		//Vector2f loc = MathUtils.getPointOnCircumference(weapon.getLocation(), 0f, angle);
		//Vector2f shipVelocity = weapon.getShip().getVelocity();
		//float speed = (float) Math.sqrt(shipVelocity.lengthSquared());
		//ShapedExplosionUtil.spawnShapedExplosion(loc, angle, speed, EXHAUST_COLOR, "deadeye");
	}
	
	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldHit, 
						ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		
        engine.addHitParticle(
				point,
				new Vector2f(),
				200.0f, //size
				2.0f, //brightness
				0.4f, //duration
				FLARE_COLOR);
		
		if (shieldHit) return;
		
		float damageMult = 0f;
		ShipAPI ship = null;
		try {
			ship = (ShipAPI) target;
		}
		catch (Exception e) {
			return;
		}
		if (ship == null) return;
		
		ArmorGridAPI grid = ship.getArmorGrid();
		int[] cell = grid.getCellAtLocation(point);
		if (cell == null) damageMult = 1f;
		else damageMult = 1f - grid.getArmorFraction(cell[0], cell[1]);
			
		float lostHP = proj.getDamage().getDamage() * damageMult * ON_HIT_MULT;
		float newHP = target.getHitpoints() - lostHP;
		if (newHP > 1) target.setHitpoints(newHP);
		else if (!ship.isHulk()) engine.applyDamage(target, point, 10000f, proj.getDamage().getType(), 0f, true, false, proj.getSource());
		engine.addFloatingDamageText(point, lostHP, Color.MAGENTA, target, proj.getSource());
	}
}