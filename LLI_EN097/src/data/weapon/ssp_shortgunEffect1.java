package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.CombatEntityPluginWithParticles;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

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

        if (proj != null && !isProjectileExpired(proj) && !proj.isFading()) {
            delay -= amount;
            if (delay <= 0) {
                ExpInterval.advance(amount);
                if (ExpInterval.intervalElapsed()) {
                    boolean active=false;
                    Vector2f point=MathUtils.getPoint(proj.getLocation(),500f,VectorUtils.getFacing(proj.getVelocity()));
                    for (ShipAPI s:Global.getCombatEngine().getShips()){
                        if(MathUtils.isPointWithinCircle(point,s.getLocation(),200) && s.getOriginalOwner()+proj.getOwner()==1){
                            active=true;
                            break;
                        }
                    }
                    if(active){
                        int HEspawn=MathUtils.getRandomNumberInRange(4,6);
                        for(int h=0;h<HEspawn;h++){
                            float spawnangle=VectorUtils.getFacing(proj.getVelocity())+MathUtils.getRandomNumberInRange(-5,5);
                            CombatEntityAPI he=Global.getCombatEngine().spawnProjectile(proj.getSource(),proj.getWeapon(),"ssp_shortgun_HIGH_EXPLOSIVE",proj.getLocation(),spawnangle,null);
                            float randomvelocity=MathUtils.getRandomNumberInRange(0.6f,1f);
                            he.getVelocity().set(he.getVelocity().x*randomvelocity,he.getVelocity().y*randomvelocity);
                        }
                        for(int f=0;f<(8-HEspawn)*4;f++){
                            float spawnangle=VectorUtils.getFacing(proj.getVelocity())+MathUtils.getRandomNumberInRange(-8,8);
                            CombatEntityAPI frag=Global.getCombatEngine().spawnProjectile(proj.getSource(),proj.getWeapon(),"ssp_shortgun_FRAGMENTATION",proj.getLocation(),spawnangle,null);
                            float randomvelocity2=MathUtils.getRandomNumberInRange(0.6f,1f);
                            frag.getVelocity().set(frag.getVelocity().x*randomvelocity2,frag.getVelocity().y*randomvelocity2);
                        }
                        Global.getCombatEngine().spawnDamagingExplosion(ssp_shortgun_shot_explosion(proj), proj.getSource(),proj.getLocation());
                        Global.getCombatEngine().removeEntity(proj);
                    }
//                    Vector2f point= MathUtils.getRandomPointInCircle(proj.getLocation(),80f);
//                    Vector2f proj_point=proj.getLocation();
//                    Global.getCombatEngine().spawnDamagingExplosion(ssp_shortgun_shot_explosion(proj), proj.getSource(),point);
//                    MagicFakeBeam.spawnFakeBeam(
//                            Global.getCombatEngine(),
//                            proj_point,
//                            MathUtils.getDistance(proj_point,point),
//                            VectorUtils.getAngle(proj_point,point),
//                            10f,
//                            0f,
//                            0.1f,
//                            0f,
//                            new Color (125, 40, 15, 255),
//                            new Color(155, 155, 155, 155),
//                            0f,
//                            DamageType.ENERGY,
//                            0f,
//                            proj.getSource()
//                            );
                }
            }
        }

    }
    public DamagingExplosionSpec ssp_shortgun_shot_explosion(DamagingProjectileAPI proj) {
        //float damage = proj.getDamageAmount()*0.25f;
        float damage = 2f;
        DamagingExplosionSpec Explosion=new DamagingExplosionSpec(
                0.4f, // duration
                50f, // radius
                50f, // coreRadius
                damage, // maxDamage
                damage/2, // minDamage
                CollisionClass.PROJECTILE_NO_FF, // collisionClass
                CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
                4f, // particleSizeMin
                10f, // particleSizeRange
                0.6f, // particleDuration
                15, // particleCount
                new Color(175, 110, 75, 255), // particleColor
                new Color(125, 40, 15,  150)  // explosionColor
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







