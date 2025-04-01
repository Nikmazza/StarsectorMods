package data.weapon;

import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;

public class ssp_ChaosArmory_EveryFrameEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

    }
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
//        ShipAPI target = null;
//        if(projectile instanceof GuidedMissileAI){target= (ShipAPI) ((GuidedMissileAI) projectile).getTarget(); }
//        if(target != null){Random = Automatic_SwitchMissileType(target);}else{Random =0;}
        int Random = MathUtils.getRandomNumberInRange(1,4);
        switch(Random){
            case 1 :
                engine.removeEntity(projectile);
                engine.spawnProjectile(weapon.getShip(), weapon, "ssp_ChaosArmory_HIGH_EXPLOSIVE", projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getShip().getVelocity());
                break;
            case 2 :
                engine.removeEntity(projectile);
                engine.spawnProjectile(weapon.getShip(), weapon, "ssp_ChaosArmory_ENERGY", projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getShip().getVelocity());
                engine.spawnProjectile(weapon.getShip(), weapon, "ssp_ChaosArmory_ENERGY", projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getShip().getVelocity());
                engine.spawnProjectile(weapon.getShip(), weapon, "ssp_ChaosArmory_ENERGY", projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getShip().getVelocity());
                break;
            case 3 :
                engine.removeEntity(projectile);
                engine.spawnProjectile(weapon.getShip(), weapon, "ssp_ChaosArmory_KINETIC", projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getShip().getVelocity());
                break;
            case 4 :
                engine.removeEntity(projectile);
                engine.spawnProjectile(weapon.getShip(), weapon, "ssp_ChaosArmory_FRAGMENTATION", projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getShip().getVelocity());
                break;
        }
    }
    public int Automatic_SwitchMissileType(ShipAPI target){
        boolean isOverloadedOrVenting = target.getFluxTracker().isOverloadedOrVenting();
        float CurrFluxLevel = target.getFluxTracker().getFluxLevel();
        float MaxSpeed = target.getMaxSpeed();
        float ArmorRating = target.getHullSpec().getArmorRating();
        boolean cannotmove = target.getEngineController().isFlamedOut();
        boolean IsInjured;
        if(target.getHitpoints()<target.getMaxHitpoints()){IsInjured=true;}else{IsInjured=false;}
        if(isOverloadedOrVenting){
           if(!IsInjured ){return 1;}
           else{return 4;}
        }else{
            if(target.getHullSize() == ShipAPI.HullSize.FRIGATE || target.getHullSize() == ShipAPI.HullSize.DESTROYER){
                if(cannotmove){return 3;}else{return 2;}
            }
            else if(target.getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP || target.getHullSize() == ShipAPI.HullSize.CRUISER){
                if(CurrFluxLevel>=0.95){return 4;}else{return 3;}
            }
            else return MathUtils.getRandomNumberInRange(1,4);
        }
    }
   }
