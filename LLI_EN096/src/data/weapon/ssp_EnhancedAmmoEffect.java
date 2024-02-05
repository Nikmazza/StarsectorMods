package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.shipsystems.ssp_missleswarm;

public class ssp_EnhancedAmmoEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
    }
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ShipAPI ship=projectile.getWeapon().getShip();
        if(ship!=null && getmode(ship)!=null && getmode(ship) == 1) {
            engine.spawnProjectile(weapon.getShip(), weapon,weapon.getId()+"_KINETIC",projectile.getLocation(),projectile.getFacing(),weapon.getShip().getVelocity());
            engine.removeEntity(projectile);
        }else if(ship!=null && getmode(ship)!=null && getmode(ship) == 2) {
            engine.spawnProjectile(weapon.getShip(), weapon,weapon.getId()+"_HE",projectile.getLocation(),projectile.getFacing(),weapon.getShip().getVelocity());
            engine.removeEntity(projectile);
        }
    }
    public Float getmode(ShipAPI ship){
        ssp_missleswarm.ssp_missleswarm_customdata CustomData = (ssp_missleswarm.ssp_missleswarm_customdata) Global.getCombatEngine().getCustomData().get("ssp_missleswarm_mode");
        if(CustomData!=null && CustomData.HaHaHashmap!=null && CustomData.HaHaHashmap.get(ship)!=null){return CustomData.HaHaHashmap.get(ship);}
        else return null;
    }
}
