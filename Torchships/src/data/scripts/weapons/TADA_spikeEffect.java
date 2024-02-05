package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
//import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;

public class TADA_spikeEffect implements OnHitEffectPlugin {
    
    private final String ID="TADA_pikeSecondary";
    
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
          
        if(!projectile.isFading()){
            if(!shieldHit){
                ((TADA_spikeThrowerEffect) projectile.getWeapon().getEffectPlugin()).putHIT(target);
                engine.spawnProjectile(
                                projectile.getSource(),
                                projectile.getWeapon(),
                                ID,
                                point,
                                projectile.getFacing(),
                                target.getVelocity()
                );
            }
        }
    }
}
