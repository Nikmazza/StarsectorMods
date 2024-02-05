package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ssp_nuke_onHitEffect implements OnHitEffectPlugin {
    Color COLOR1 = new Color(245, 255, 235,200);
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
                      Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (target instanceof ShipAPI) {
            for(int o=0;o<36;o++){
                Vector2f Vel1 = new Vector2f (0,0);
                Global.getCombatEngine().addNebulaParticle(point,Vel1.set(VectorUtils.rotate(new Vector2f(100f, 0f), o*10f)),30f,2.5f,0.5f,0.8f,1.2f,COLOR1);
            }

        }
    }
}
