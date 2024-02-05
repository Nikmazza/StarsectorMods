package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ssp_shortgunEffect implements OnFireEffectPlugin, OnHitEffectPlugin, EveryFrameWeaponEffectPlugin{

    protected CombatEntityAPI Entity;
    protected ssp_shortgunEffect1 Plugin;
    public ssp_shortgunEffect() {
    }
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        boolean charging = weapon.getChargeLevel() > 0 && weapon.getCooldownRemaining() <= 0;
        if (charging && Entity == null) {
            Plugin = new ssp_shortgunEffect1(weapon);
            Entity = Global.getCombatEngine().addLayeredRenderingPlugin(Plugin);
        } else if (!charging && Entity != null) {
            Entity = null;
            Plugin = null;
        }
    }

    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        engine.addSmokeParticle(point,target.getVelocity(),80f,0.3f,1f, new Color(240, 170, 140,125));
        engine.addSmokeParticle(point,target.getVelocity(),55f,0.7f,1f, new Color(220, 220, 220,255));
    }

    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        if (Plugin != null) {
            Plugin.attachToProjectile(projectile);
            Plugin = null;
            Entity = null;
        }
    }
}







