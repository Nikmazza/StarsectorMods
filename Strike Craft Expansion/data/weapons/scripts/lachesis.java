/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
/**
 *
 * @author bevit
 */
public class lachesis implements OnHitEffectPlugin{
  	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

		if (shieldHit && ((ShipAPI)target).getHardFluxLevel() > .1 && target instanceof ShipAPI) {
			
			float emp = projectile.getEmpAmount()*((ShipAPI)target).getHardFluxLevel();
			float dam = projectile.getDamageAmount()*((ShipAPI)target).getHardFluxLevel()/3; //Max Bleedthrough damage of 250 for 750 damage torpedo
			
			engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
							   DamageType.ENERGY, 
							   dam,
							   emp, // emp 
							   100000f, // max range 
							   "tachyon_lance_emp_impact",
							   40f * ((ShipAPI)target).getHardFluxLevel(), // thickness grows as hardflux increases
							   new Color(75,25,125,105),
							   new Color(75,25,125,105)
							   );
			
			//engine.spawnProjectile(null, null, "plasma", point, 0, new Vector2f(0, 0));
		}
                else if (!shieldHit && target instanceof ShipAPI) {
                    float emp = projectile.getEmpAmount();
                    float dam = projectile.getDamageAmount()/30; // Good EMP arc on direct hit, but trivial "extra" damage due to lack of "bleed-through"
                    
			engine.spawnEmpArc(projectile.getSource(), point, target, target,
							   DamageType.ENERGY, 
							   dam,
							   emp, // emp 
							   100000f, // max range 
							   "tachyon_lance_emp_impact",
							   40f, // thickness
							   new Color(75,25,125,105),
							   new Color(75,25,125,105)
							   );
	}  
}
}