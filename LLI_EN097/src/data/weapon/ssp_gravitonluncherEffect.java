package data.weapon;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ssp_gravitonluncherEffect implements OnHitEffectPlugin {
    public static final Color COLOR = new Color(40, 220, 255,255);

    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
                      Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (shieldHit && target instanceof ShipAPI) {
            engine.applyDamage(target,point,projectile.getDamage().getDamage(),DamageType.KINETIC,0f,false,false,projectile.getSource());
        }
        //SpawnWD(point,target);
    }
//    public void SpawnWD(Vector2f point,CombatEntityAPI target){
//        //WaveDistortion WD = new WaveDistortion();
//        RippleDistortion WD = new RippleDistortion();
//        WD.setLocation(point);
//        WD.setSize(20f);
//        WD.setVelocity(target.getVelocity());
//        WD.setArc(0,360);
//        WD.setArcAttenuationWidth(0f);
//        WD.setLifetime(0.2f);
//        WD.setAutoFadeSizeTime(0.2f);
//        WD.setAutoFadeIntensityTime(0.2f);
//        DistortionShader.addDistortion(WD);
//    }
}


