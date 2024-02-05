package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static com.fs.starfarer.api.util.Misc.ZERO;

public class LTA_OtamayOnHitEffect implements OnHitEffectPlugin {
    
    private static final float MIN_DAMAGE = 500f;
    private static final float MAX_DAMAGE = 500f;
    private static final float DISRUPTION_DUR = 0.75f;


    private static final Color OTAMAY_EXPLOSION_CORE_COLOR = new Color(255, 40, 25);
    private static final Color OTAMAY_EXPLOSION_COLOUR = new Color(255, 40, 25, 0);
    private static final Color OTAMAY_EXPLOSION_FLASH_COLOR = new Color(255, 100, 0);
    private static final Color OTAMAY_PARTICLE_COLOUR = new Color(255, 40, 25, 80);
    private static final int OTAMAY_PARTICLES_AMOUNT = 200;

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        
        ShipAPI targeted = null;
        if (target instanceof ShipAPI) {
          targeted = (ShipAPI) target;
        }
        if (shieldHit) { 
            if (targeted != null) {
            targeted.getFluxTracker().beginOverloadWithTotalBaseDuration(DISRUPTION_DUR);
            }
        }
        //MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 8, 400, 0, new Color(186, 240, 255), new Color(255, 255, 255));

        engine.spawnExplosion(point, ZERO, OTAMAY_PARTICLE_COLOUR, 200f, 1f);
        engine.spawnExplosion(point, ZERO, OTAMAY_EXPLOSION_CORE_COLOR, 100f, 1f);

        DamagingExplosionSpec blast = new DamagingExplosionSpec(0.5f, 250f, 150f, MAX_DAMAGE, MIN_DAMAGE, CollisionClass.PROJECTILE_FF, CollisionClass.PROJECTILE_FIGHTER, 10f, 10f, 0.5f, 200, OTAMAY_EXPLOSION_COLOUR, null);
        blast.setDamageType(DamageType.ENERGY);
        blast.setShowGraphic(false);
        engine.spawnDamagingExplosion(blast,projectile.getSource(),point,false);

        engine.addSmoothParticle(point, ZERO, 750f, 0.4f, 0.2f, OTAMAY_PARTICLE_COLOUR);
        engine.addHitParticle(point, ZERO, 750f, 1.0f, 0.50f, OTAMAY_EXPLOSION_FLASH_COLOR);
        for (int x = 0; x < OTAMAY_PARTICLES_AMOUNT; x++) 
        {
            engine.addHitParticle(point, MathUtils.getPointOnCircumference(null, MathUtils.getRandomNumberInRange(10f, 50f), (float) Math.random() * 360f), 10f, 1f, MathUtils.getRandomNumberInRange(0.6f, 1.2f), OTAMAY_PARTICLE_COLOUR);
        }
    }
}