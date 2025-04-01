package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ProximityExplosionEffect;
import com.fs.starfarer.api.impl.combat.RiftCascadeEffect;
import data.SSP_NegativeExplosionVisual;
import data.SSP_NegativeExplosionVisual.SSP_NEParams;
import com.fs.starfarer.api.loading.MissileSpecAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;


public class ssp_zhurong_system_minelayerExplosion implements ProximityExplosionEffect {
    public void onExplosion(DamagingProjectileAPI explosion, DamagingProjectileAPI originalProjectile) {
        SSP_NEParams p = createStandardRiftParams("ssp_zhurong_system_minelayer", 20f);
        spawnStandardRift(explosion, p);
    }
//定义裂隙属性:根据proj定义颜色并传递半径至下一步
    public static SSP_NEParams createStandardRiftParams(String minelayerId, float baseRadius) {
        Color color = new Color(100,100,255,255);
        Object o = Global.getSettings().getWeaponSpec(minelayerId).getProjectileSpec();
        if (o instanceof MissileSpecAPI) {
            MissileSpecAPI spec = (MissileSpecAPI) o;
            color = spec.getGlowColor();
        }
        SSP_NEParams p = createStandardRiftParams(color, baseRadius);
        return p;
    }
//定义裂隙的其他属性，此处的颜色均为实际效果颜色
    public static SSP_NEParams createStandardRiftParams(Color borderColor, float radius) {
        SSP_NEParams p = new SSP_NEParams();
        p.hitGlowSizeMult = 0.75f;
        p.spawnHitGlowAt = 0f;
        p.noiseMag = 1f;
        p.fadeIn = 0.1f;
        p.underglow = new Color(100, 0, 25, 100);
        p.withHitGlow = true;
        p.radius = radius;
        p.color = borderColor;
        return p;
    }
//使用定义过的裂隙的属性，生成裂隙
    public static void spawnStandardRift(DamagingProjectileAPI explosion, SSP_NEParams params) {
        CombatEngineAPI engine = Global.getCombatEngine();
        explosion.addDamagedAlready(explosion.getSource());

        CombatEntityAPI prev = null;
        for (int i = 0; i < 2; i++) {
            SSP_NEParams p = params.clone();
            p.radius *= 0.75f + 0.5f * (float) Math.random();

            p.withHitGlow = prev == null;

            Vector2f loc = new Vector2f(explosion.getLocation());
            //loc = Misc.getPointWithinRadius(loc, p.radius * 1f);
            loc = Misc.getPointAtRadius(loc, p.radius * 0.4f);

            CombatEntityAPI e = engine.addLayeredRenderingPlugin(new SSP_NegativeExplosionVisual(p));
            e.getLocation().set(loc);

            if (prev != null) {
                float dist = Misc.getDistance(prev.getLocation(), loc);
                Vector2f vel = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(loc, prev.getLocation()));
                vel.scale(dist / (p.fadeIn + p.fadeOut) * 0.7f);
                e.getVelocity().set(vel);
            }

            prev = e;
        }

    }
}
