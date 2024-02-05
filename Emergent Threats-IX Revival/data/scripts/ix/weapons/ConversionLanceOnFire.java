package data.scripts.ix.weapons;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;

import data.scripts.ix.ShapedExplosionUtil;

public class ConversionLanceOnFire implements BeamEffectPlugin {

	private static float EXPLOSION_DAMAGE = 1500f;
	private boolean applied = false;
	private static Color pc = new Color(170,50,50,255);
	private static Color explosionColor = new Color(255,200,0,255);
	private IntervalUtil tracker = new IntervalUtil(1.8f, 1.8f);
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		if (applied) return;
		tracker.advance(amount);
		
		//on hit explosion effect
		if (tracker.intervalElapsed()) {
			Vector2f point = beam.getRayEndPrevFrame();
			engine.spawnDamagingExplosion(createExplosionSpec(), beam.getSource(), point);
			engine.spawnExplosion(point, new Vector2f(), explosionColor, 250f, 1f);
			applied = true;
		}
	}
	
	private DamagingExplosionSpec createExplosionSpec() {
		float damage = EXPLOSION_DAMAGE;
		DamagingExplosionSpec spec = new DamagingExplosionSpec(
				0.1f, // duration
				150f, // radius
				150f, // coreRadius
				damage, // maxDamage
				damage, // minDamage
				CollisionClass.PROJECTILE_FF, // collisionClass
				CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
				0, // particleSizeMin
				0, // particleSizeRange
				0, // particleDuration
				0, // particleCount
				pc, // particleColor
				pc  // explosionColor
		);

		spec.setDamageType(DamageType.HIGH_EXPLOSIVE);
		spec.setUseDetailedExplosion(false);
		spec.setSoundSetId("conversion_lance_ix_hit");
		return spec;		
	}
}