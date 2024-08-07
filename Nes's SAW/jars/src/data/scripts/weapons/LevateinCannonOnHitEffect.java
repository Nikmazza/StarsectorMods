package data.scripts.weapons;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class LevateinCannonOnHitEffect implements OnHitEffectPlugin {

    //bless tantananan for this readable code
	private static final float arcChancePercent = 66f;				//Chance for the arc to trigger. default is 25.
	private static final float arcEMPPercent = 100f;				//Percentage of weapon EMP damage that will be used for arc EMP. the default value of 100 will give a final arc emp that's the same as weapon emp.
	private static final float arcDamagePercent = 0f;				//Percentage of weapon damage that will be used for arc damage. the default value of 100 will give a final arc damage that's the same as weapon damage.

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

		if ((float) Math.random() < (arcChancePercent * 0.01f) && !shieldHit && target instanceof ShipAPI) {

			float emp = projectile.getEmpAmount() * (arcEMPPercent * 0.01f);				//multiplies the weapon's emp damage with the arc EMP percent to get the final arc emp damage
			//float emp = projectile.getDamageAmount() * arcEMPPercent;						//use this, instead of the one above, if you want the EMP arcs to be based on the weapon's damage instead of weapon emp damage
			float dam = projectile.getDamageAmount() * (arcDamagePercent * 0.01f);			//multiplies the weapon's damage with the arc damage percent to get the final arc damage

			engine.spawnEmpArc(projectile.getSource(), point, target, target,
					DamageType.ENERGY, 								//Determines the damage type of the arc.
					dam,												// final arc damage
					emp, 											// final arc EMP
					100000f,
					"tachyon_lance_emp_impact",
					20f,
					//new Color(25,100,155,255),
					//new Color(255,255,255,255)

					//arc colors matching weapon projectile
					projectile.getProjectileSpec().getFringeColor(),
					projectile.getProjectileSpec().getCoreColor()
			);
		}
	}
}
