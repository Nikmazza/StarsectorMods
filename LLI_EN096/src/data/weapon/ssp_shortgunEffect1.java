package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.CombatEntityPluginWithParticles;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicFakeBeam;

import java.awt.*;

public class ssp_shortgunEffect1 extends CombatEntityPluginWithParticles {


    protected WeaponAPI weapon;
    protected DamagingProjectileAPI proj;
    protected IntervalUtil interval = new IntervalUtil(0.1f, 0.1f);
    protected IntervalUtil ExpInterval = new IntervalUtil(0.17f, 0.23f);
    protected float delay = 1f;

    public ssp_shortgunEffect1(WeaponAPI weapon) {
        super();
        this.weapon = weapon;
        ExpInterval = new IntervalUtil(0.20f, 0.30f);
        delay = 0.1f;
        //setSpriteSheetKey("fx_particles2");
    }

    public void attachToProjectile(DamagingProjectileAPI proj) {
        this.proj = proj;
    }

    public void advance(float amount) {
        if (Global.getCombatEngine().isPaused()) return;
        if (proj != null) {
            entity.getLocation().set(proj.getLocation());
        } else {
            entity.getLocation().set(weapon.getFirePoint(0));
        }
        super.advance(amount);

        boolean keepSpawningParticles = isWeaponCharging(weapon) || (proj != null && !isProjectileExpired(proj) && !proj.isFading());

        if (proj != null && !isProjectileExpired(proj) && !proj.isFading()) {
            delay -= amount;
            if (delay <= 0) {
                ExpInterval.advance(amount);
                if (ExpInterval.intervalElapsed()) {


                    Vector2f point= MathUtils.getRandomPointInCircle(proj.getLocation(),80f);
                    Vector2f proj_point=proj.getLocation();
                    //Global.getCombatEngine().spawnProjectile(weapon.getShip(), weapon,"flak",proj_point,MathUtils.getRandomNumberInRange(0f,360f),proj.getVelocity());
                    //Global.getCombatEngine().spawnDamagingExplosion(ssp_shortgun_shot_explosion(proj), proj.getSource(),proj_point);
                    Global.getCombatEngine().spawnDamagingExplosion(ssp_shortgun_shot_explosion(proj), proj.getSource(),point);
                    MagicFakeBeam.spawnFakeBeam(
                            Global.getCombatEngine(),
                            proj_point,
                            MathUtils.getDistance(proj_point,point),
                            VectorUtils.getAngle(proj_point,point),
                            10f,
                            0f,
                            0.1f,
                            0f,
                            new Color (225, 99, 15, 255),
                            new Color(255, 255, 255, 155),
                            0f,
                            DamageType.ENERGY,
                            0f,
                            proj.getSource()
                            );

                }
            }
        }

    }
    public DamagingExplosionSpec ssp_shortgun_shot_explosion(DamagingProjectileAPI proj) {
        float damage = proj.getDamageAmount()*0.25f;
        DamagingExplosionSpec Explosion=new DamagingExplosionSpec(
                0.05f, // duration
                100f, // radius
                50f, // coreRadius
                damage, // maxDamage
                damage/2, // minDamage
                CollisionClass.PROJECTILE_NO_FF, // collisionClass
                CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
                0.5f, // particleSizeMin
                0.5f, // particleSizeRange
                0.05f, // particleDuration
                100, // particleCount
                new Color(225, 99, 15, 200), // particleColor
                new Color(225, 99, 15, 150)  // explosionColor
        );
        Explosion.setDamageType(DamageType.HIGH_EXPLOSIVE);
        Explosion.setUseDetailedExplosion(false);
        Explosion.setSoundSetId(null);
        return Explosion;
    }
    @Override
    public void render(CombatEngineLayers layer, ViewportAPI viewport) {
        // pass in proj as last argument to have particles rotate
        super.render(layer, viewport, null);
    }

    public boolean isExpired() {
        boolean keepSpawningParticles = isWeaponCharging(weapon) ||
                (proj != null && !isProjectileExpired(proj) && !proj.isFading());
        return super.isExpired() && (!keepSpawningParticles || (!weapon.getShip().isAlive() && proj == null));
    }

    public float getRenderRadius() {
        return 500f;
    }

    @Override
    protected float getGlobalAlphaMult() {
        if (proj != null && proj.isFading()) {
            return proj.getBrightness();
        }
        return super.getGlobalAlphaMult();
    }

    public static boolean isProjectileExpired(DamagingProjectileAPI proj) {
        return proj.isExpired() || proj.didDamage() || !Global.getCombatEngine().isEntityInPlay(proj);
    }

    public static boolean isWeaponCharging(WeaponAPI weapon) {
        return weapon.getChargeLevel() > 0 && weapon.getCooldownRemaining() <= 0;
    }

}







