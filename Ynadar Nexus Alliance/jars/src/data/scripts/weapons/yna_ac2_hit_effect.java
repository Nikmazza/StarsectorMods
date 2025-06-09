package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.impl.combat.BreachOnHitEffect;

public class yna_ac2_hit_effect implements OnHitEffectPlugin {
        @Override
	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		float dam = projectile.getDamageAmount() * 0.25f;
		DamageType dam_type = projectile.getDamageType();
                
		if (shieldHit && target instanceof ShipAPI) {
			Global.getCombatEngine().applyDamage(target, point, dam , dam_type, 0f, false, true,
				projectile.getSource());
		}
	}
}
