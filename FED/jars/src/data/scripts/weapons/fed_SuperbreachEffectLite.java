/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.scripts.weapons;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class fed_SuperbreachEffectLite implements OnHitEffectPlugin {

	public static float DAMAGE = 500;
        public static float HALFDAMAGE = 500;
        public static Color FRINGECOLOR = new Color(255,165,50,255);
        public static Color CORECOLOR = new Color(255,245,245,255);
        public static Color FRINGECOLOR_PIERCE = new Color(205,165,50,255);
        public static Color CORECOLOR_PIERCE = new Color(205,245,245,255);
        
        

        @Override
	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		if (target instanceof ShipAPI && !shieldHit) {
                   engine.spawnEmpArcPierceShields(
                            projectile.getSource(), point, target, target,
                            DamageType.HIGH_EXPLOSIVE,
                            DAMAGE, // damage
                            0, // emp 
                            10000f, // max range 
                            "tachyon_lance_emp_impact",
                            15f,
                            FRINGECOLOR,
                            CORECOLOR
                    ); 
		}
                if (target instanceof ShipAPI && shieldHit) {
                   engine.spawnEmpArcPierceShields(
                            projectile.getSource(), point, target, target,
                            DamageType.FRAGMENTATION,
                            DAMAGE, // damage
                            0, // emp 
                            10000f, // max range 
                            "tachyon_lance_emp_impact",
                            12f,
                            FRINGECOLOR_PIERCE,
                            CORECOLOR_PIERCE
                    ); 
		}
                
	}
}
