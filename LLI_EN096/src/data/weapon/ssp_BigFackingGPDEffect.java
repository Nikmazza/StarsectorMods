package data.weapon;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class ssp_BigFackingGPDEffect implements OnHitEffectPlugin {
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
                      Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (target instanceof ShipAPI) {
            if(((ShipAPI) target).hasListenerOfClass(ssp_fluxdeliversystem_beameffect.ssp_fluxdeliversystem_listener.class)){
                List<ssp_fluxdeliversystem_beameffect.ssp_fluxdeliversystem_listener> listeners = ((ShipAPI) target).getListeners(ssp_fluxdeliversystem_beameffect.ssp_fluxdeliversystem_listener.class);
                if (listeners.isEmpty()) return; // ???

                ssp_fluxdeliversystem_beameffect.ssp_fluxdeliversystem_listener listener = listeners.get(0);
                listener.ArcSpawner(projectile.getWeapon());
            }
           if(((ShipAPI) target).getFluxTracker().isOverloaded()){
                // projectile.getWeapon().getShip().getFluxTracker().decreaseFlux(projectile.getDamageAmount());
               engine.spawnDamagingExplosion(ssp_BFGPD_spec1(projectile),projectile.getSource(),point);
            }else{
               engine.spawnDamagingExplosion(ssp_BFGPD_spec2(),projectile.getSource(),point);
           }
        }
    }
    public DamagingExplosionSpec ssp_BFGPD_spec1(DamagingProjectileAPI projectile) {
        float damage = projectile.getDamageAmount();
        DamagingExplosionSpec spec = new DamagingExplosionSpec(
                0.1f, // duration
                100f, // radius
                50f, // coreRadius
                damage, // maxDamage
                damage*0.25f, // minDamage
                CollisionClass.PROJECTILE_FF, // collisionClass
                CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
                2f, // particleSizeMin
                2f, // particleSizeRange
                0.3f, // particleDuration
                100, // particleCount
                new Color(215,155,255,125), // particleColor
                new Color(125, 60, 200, 200)  // explosionColor
        );

        spec.setDamageType(DamageType.FRAGMENTATION);
        spec.setUseDetailedExplosion(false);
        spec.setSoundSetId(null);
        return spec;
    }
    public DamagingExplosionSpec ssp_BFGPD_spec2() {

        DamagingExplosionSpec spec = new DamagingExplosionSpec(
                0.1f, // duration
                80f, // radius
                40f, // coreRadius
                0, // maxDamage
                0, // minDamage
                CollisionClass.PROJECTILE_FF, // collisionClass
                CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
                2f, // particleSizeMin
                2f, // particleSizeRange
                0.3f, // particleDuration
                100, // particleCount
                new Color(215,155,255,125), // particleColor
                new Color(125, 60, 200, 200)  // explosionColor
        );

        spec.setDamageType(DamageType.FRAGMENTATION);
        spec.setUseDetailedExplosion(false);
        spec.setSoundSetId(null);
        return spec;
    }
}
