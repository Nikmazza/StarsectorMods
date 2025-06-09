package data.scripts.ix.weapons;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class ArresterOnHit implements OnHitEffectPlugin {
	
	private static float FLUX_THRESHOLD_SHIELD = 0.75f;
	private static float FLUX_THRESHOLD_ARMOR = 0.90f;
	private static float OVERLOAD_ODDS = 0.3f;
	
	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		Global.getSoundPlayer().playSound("arrester_tw_hit", 1f, 1f, point, new Vector2f());
		if (target instanceof ShipAPI) {
			boolean isValidHit = false;
			ShipAPI enemy = (ShipAPI) target;
			if (shieldHit && enemy.getFluxLevel() > FLUX_THRESHOLD_SHIELD) isValidHit = true;
			else if (!shieldHit && enemy.getFluxLevel() > FLUX_THRESHOLD_ARMOR) isValidHit = true;
			if (isValidHit && Math.random() <= OVERLOAD_ODDS) {
				enemy.getFluxTracker().beginOverloadWithTotalBaseDuration(1f);
			}
		}
	}
}