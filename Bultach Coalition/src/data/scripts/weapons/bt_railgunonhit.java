package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class bt_railgunonhit implements OnHitEffectPlugin {

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(shieldHit) {
            //remove projectile damage
            projectile.setDamageAmount(0f);
            //apply projectile damage manually to flux bar
            ((ShipAPI) target).getFluxTracker().increaseFlux(projectile.getDamageAmount(), true);
            //if it increase by the amount of damage your weapon have, it is correct
        }
    }
}