package data.scripts;

import java.awt.Color;

import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

public class rr_dtpc_shotgun implements OnFireEffectPlugin {

    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

        ShipAPI ship = weapon.getShip();
        float angle = projectile.getFacing();
    	
        // random projectile velocity thing (scales velocity from -10% to +10%)
		float velScale = projectile.getProjectileSpec().getMoveSpeed(ship.getMutableStats(), weapon);
		Vector2f vel = (Vector2f) (ship.getVelocity());
		Vector2f dtpcRandomVel = MathUtils.getPointOnCircumference(null, MathUtils.getRandomNumberInRange(velScale * -0.1f, velScale * 0.1f) , angle);
		dtpcRandomVel.x += vel.x;
		dtpcRandomVel.y += vel.y;
		engine.spawnProjectile(weapon.getShip(), weapon, "rr_d-tpc_split", projectile.getLocation(), projectile.getFacing(), dtpcRandomVel);
        engine.removeEntity(projectile);
		// having to spawn/despawn projectiles, because it's a BaB projectile, and you can't modify their velocity after spawning.
        
        // scripted muzzle vfx
		for (int i=0; i < 4; i++) {
			
            float angle1 = angle + MathUtils.getRandomNumberInRange(-13f, 13f);
            Vector2f particleVel = MathUtils.getPointOnCircumference(ship.getVelocity(), MathUtils.getRandomNumberInRange(23f, 59f), angle1);
            
            Vector2f point1 = MathUtils.getPointOnCircumference(projectile.getLocation(), MathUtils.getRandomNumberInRange(1f, 29f), angle1);
            
            Global.getCombatEngine().addSmoothParticle(point1,
            		particleVel,
					MathUtils.getRandomNumberInRange(2f, 5f), //size
					0.8f, //brightness
					MathUtils.getRandomNumberInRange(0.3f, 0.6f), //duration
					new Color(255,100,100,255));
        }
        
    }
  }