package data.weapons.scripts;

// Written by AxleMC131 (thanks Axle!)
// Tag searching by ruddygreat

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

import org.lazywizard.lazylib.combat.CombatUtils;

public class BcShotgunEffect implements OnFireEffectPlugin {
    
    public BcShotgunEffect() {}
    
    public static final float BASE_SPREAD_VEL = 8f; // Factor to vary shrapnel velocity by.
    private static final String SGTAG_MIN_SHOTS = "bc_sgmins_"; // Tag to search for the MINIMUM number of shrapnel shots
    private static final String SGTAG_MAX_SHOTS = "bc_sgmaxs_"; // Tag to search for the MAXIMUM number of shrapnel shots
    
    /* The search tags correspond to arbitrary tags setup in weapon_data.csv, and are paired with a number which provides
    * the value this script reads. To adapt this script for your own purposes, simply change the search tags to whatever 
    * you want to use for specifying the min/max number of shrapnel shots, and add the corresponding tags into the base
    * weapon's tags in the CSV file.
    * The spread of the shrapnel is determined by the weapon's "max spread" value, also in weapon_data.csv.
    */

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        /* First instantiate our split values both to 1. If a weapon is using this script, but for whatever reason
        * doesn't have the relevant shotgun tags, it will default to only a single shrapnel shot. The randomised
        * velocity and spread still apply.
        */
        int splitIntoMin = 1;
        int splitIntoMax = 1;
        for (String tag : weapon.getSpec().getTags()) {
            // Search for our "min shots" tag. If we find it, set our min shots to the attached value.
            if (tag.contains(SGTAG_MIN_SHOTS)) { 
                splitIntoMin = Integer.parseInt(tag.substring(SGTAG_MIN_SHOTS.length()));
            }
            // Search for our "max shots" tag. If we find it, set our max shots to the attached value.
            if (tag.contains(SGTAG_MAX_SHOTS)) { 
                splitIntoMax = Integer.parseInt(tag.substring(SGTAG_MAX_SHOTS.length()));
            }
        }
        // A little math to generate a random integer between our min and max shots (inclusive).
        int numShrapnelShots = splitIntoMin;
        numShrapnelShots += (int) (Math.random() * (splitIntoMax - splitIntoMin + 1));
        
        /* Retrieve the maximum spread value from the weapon's "max spread" stat. This could also be done with a
        * tag search, but I like using the value that's already there to read from. I also don't see a need for
        * shotgun weapons to have a shrapnel spread AND a recoil spread, hence my shotgun weapons have a non-zero
        * "max spread" value but a zero "spread/shot" value. You're free to change this to also use a tag if
        * that fits your purpose better.
        */
        float splitSpreadArc = 0f;
        if (weapon.getSpec().getMaxSpread() > 0f) {
            splitSpreadArc = weapon.getSpec().getMaxSpread();
        }
        
        /* Iterate once for every shrapnel shot we are generating (as determined by the math earlier).
        * Each new projectile is briefly stored and has applied to it a velocity offset and a damage variation.
        * The damage is set up such that the per-shot damage specified in weapon_data.csv is divided evenly between
        * all shrapnel shots, regardless of how many there are. For the stat-savvy folks out there, I judge the
        * armour-penetration based on the min, max and average per-shrapnel damage values and use that for balancing.
        */
        int i = 0;
        while (i < numShrapnelShots) {
            float arcOffset = (splitSpreadArc / 2f) - ((float) Math.random() * splitSpreadArc);
            DamagingProjectileAPI shrapnelproj = (DamagingProjectileAPI) engine.spawnProjectile(projectile.getSource(), 
                    weapon, 
                    weapon.getId()+"_shrapnel", 
                    projectile.getLocation(), 
                    projectile.getFacing() + arcOffset, 
                    null);
            float velocityOffset = (BcShotgunEffect.BASE_SPREAD_VEL / 2f) - ((float) Math.random() * BcShotgunEffect.BASE_SPREAD_VEL);
            CombatUtils.applyForce(shrapnelproj, shrapnelproj.getVelocity(), velocityOffset);
            shrapnelproj.setDamageAmount(projectile.getDamageAmount() / numShrapnelShots);
            i++;
        }
        
        // Oh, and make sure to remove the original (non-shrapnel) projectile once we're done!
        engine.removeEntity(projectile);
    }
    
    
}




