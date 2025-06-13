package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class test_tachyon_impact implements OnHitEffectPlugin {

    private static final Color COLOR1 = new Color(240, 174, 252, 200);
    private static final Color COLOR2 = new Color(155, 41, 217, 175);
    private static final Vector2f ZERO = new Vector2f();

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult,
            CombatEngineAPI engine) {
        if (point == null) {
            return;
        }

        if (target instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) target;

            float hitLevel = 0f;
            float emp = projectile.getEmpAmount() / 4f;
            float dam = projectile.getDamageAmount() / 36f;
            for (int x = 0; x < 12; x++) {               
                if (!shieldHit) {
                    hitLevel += 1f / 12f;
                    ShipAPI empTarget = ship;
                    engine.spawnEmpArcPierceShields(projectile.getSource(), point, empTarget, empTarget,
                            DamageType.ENERGY, dam, emp, 100000f, null, 20f, COLOR1, COLOR2);
                }
            }

            if (hitLevel > 0f) {
                engine.addSmoothParticle(point, ZERO, 300f * hitLevel, hitLevel, 0.75f, COLOR1);
            }
        }
    }
}
