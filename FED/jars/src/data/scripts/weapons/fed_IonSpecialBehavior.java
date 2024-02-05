package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class fed_IonSpecialBehavior implements EveryFrameWeaponEffectPlugin, OnFireEffectPlugin {
    
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {


                Vector2f loc = projectile.getLocation();
                Vector2f vel = projectile.getVelocity();
                vel.x +=  projectile.getSource().getVelocity().x;
                vel.y +=   projectile.getSource().getVelocity().y;
                int shotCount = (20);
                for (int j = 0; j < shotCount; j++) {
                    //spec + "_clone" means is, if its got the same name in its name (except the "_clone" part) then it must be that weapon.
                    Vector2f randomVel = MathUtils.getRandomPointInCircle(vel, 0f);
                    float angleVariance = MathUtils.getRandomNumberInRange(0f, 0.05f);
                    randomVel.x -= vel.x;
                    randomVel.y -= vel.y;
                    
                    engine.spawnProjectile(projectile.getSource(), projectile.getWeapon(), projectile.getProjectileSpecId() + "_clone", loc, projectile.getFacing()+angleVariance, randomVel);
                }
                engine.removeEntity(projectile);
        
    }
    
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        //cricket
    }
}
