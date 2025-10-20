package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class BluebottlerOnHitEffect implements OnHitEffectPlugin {


    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (target instanceof ShipAPI){
            float size = (100f / target.getMass());

            float dirX = target.getVelocity().getX();
            float dirY = target.getVelocity().getY();
            float totalvel = dirX + dirY;
            float velsplitX = MathUtils.getRandomNumberInRange(0f, totalvel);
            float velsplitY = totalvel - velsplitX;

            target.getVelocity().set(velsplitX, velsplitY);
            target.getVelocity().scale(MathUtils.getRandomNumberInRange(0.95f, 0.98f) * size);
        }
    }
}
