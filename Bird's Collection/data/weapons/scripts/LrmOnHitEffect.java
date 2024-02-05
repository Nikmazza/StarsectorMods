package data.weapons.scripts;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class LrmOnHitEffect implements OnHitEffectPlugin {


	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

		engine.spawnEmpArc(
				projectile.getSource(), point, target, target,
				DamageType.ENERGY,
				5f, // damage
				50f, // emp
				100000f, // max range
				"tachyon_lance_emp_impact",
				3f, // thickness
				//new Color(25,100,155,255),
				//new Color(255,255,255,255)
				new Color(35,237,255, 255),
				new Color(255, 255, 255, 255)
		);
	}
}
