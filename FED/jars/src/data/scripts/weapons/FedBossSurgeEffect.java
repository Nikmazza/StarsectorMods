/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class FedBossSurgeEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {

    //Shotgun projectile for replacement
    private static final String PROJ_ID = "fed_boss_surge_shot";
    //Explosion flash
    private static final Color FLASH_COLOR = new Color(255, 240, 165, 200);
    private static final float FLASH_SIZE = 80f; //explosion size
    private static final float FLASH_DUR = 0.2f;
    
    private static final float SPREAD_RANDOM = 100f; // random velocity added to the spread projectiles

    @Override
    public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine) {
        Vector2f loc = proj.getLocation();
        
        // set up for explosions    
        ShipAPI ship = weapon.getShip();
        Vector2f ship_velocity = ship.getVelocity();
        
        // do visual fx
        engine.spawnExplosion(loc, ship_velocity, FLASH_COLOR, FLASH_SIZE, FLASH_DUR);
        
        //int shotFocusedCount = 50;
        for (int j = 0; j < 150; j++) {
            
            engine.spawnProjectile(ship,
                    weapon,
                    PROJ_ID + "_clone",
                    loc,
                    proj.getFacing() + MathUtils.getRandomNumberInRange(0f, 360f),
                    MathUtils.getRandomPointInCircle(ship_velocity, SPREAD_RANDOM));
        }

        engine.removeEntity(proj);
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

    }
}