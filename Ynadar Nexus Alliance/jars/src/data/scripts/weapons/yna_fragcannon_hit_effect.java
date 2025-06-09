package data.scripts.weapons;

import com.fs.starfarer.api.combat.CollisionClass;
import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;

public class yna_fragcannon_hit_effect implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult,
            CombatEngineAPI engine) {
            float proj_dam = projectile.getDamageAmount();
            DamageType dam_type = projectile.getDamageType();
            
            if (projectile.isFading()) {
                DamagingProjectileAPI e = engine.spawnDamagingExplosion(createExplosionSpec(proj_dam, dam_type), projectile.getSource(), point);
                e.addDamagedAlready(target);
            }
	}
    
    public DamagingExplosionSpec createExplosionSpec(float damex, DamageType damet) {
	DamagingExplosionSpec spec = new DamagingExplosionSpec(
		0.1f, // duration
		75f, // radius
		25f, // coreRadius
		damex * 0.5f, // maxDamage
		damex * 0.45f, // minDamage
		CollisionClass.PROJECTILE_FF, // collisionClass
		CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
		3f, // particleSizeMin
		3f, // particleSizeRange
		0.5f, // particleDuration
		25, // particleCount
		new Color(255,165,125,255), // particleColor
		new Color(255,165,125,125)  // explosionColor
	);
	spec.setDamageType(damet);
	spec.setUseDetailedExplosion(false);
	spec.setSoundSetId("");
	return spec;		
    }
}
