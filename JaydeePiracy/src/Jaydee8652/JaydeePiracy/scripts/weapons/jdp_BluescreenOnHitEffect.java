package Jaydee8652.JaydeePiracy.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class jdp_BluescreenOnHitEffect implements OnHitEffectPlugin {

    private static final Color EXPLOSION_COLOR = new Color(46, 91, 255);

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        if (!shieldHit && target instanceof ShipAPI) {
            //((jdp_BluescreenScript) projectile.getWeapon().getEffectPlugin()).putHIT(target);

            //Visual

            engine.spawnProjectile(
                    projectile.getSource(),
                    projectile.getWeapon(),
                    "jdp_marlingun_bluescreen_sub",
                    point,
                    projectile.getFacing(),
                    target.getVelocity()
            );
            Global.getSoundPlayer().playSound("ui_transponder_on", 1.0f, 0.66f, point, target.getVelocity());
        }
        engine.addSmoothParticle(point, target.getVelocity(), 100f, 1.5f, 0.25f, EXPLOSION_COLOR);
    }
}
