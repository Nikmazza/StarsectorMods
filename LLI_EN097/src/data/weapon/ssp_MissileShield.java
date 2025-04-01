package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;

import java.util.List;

public class ssp_MissileShield implements OnFireEffectPlugin {
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        if (!(projectile instanceof MissileAPI)) return;

        MissileAPI missile = (MissileAPI) projectile;

        ShipAPI ship = null;
        if (weapon != null) ship = weapon.getShip();
        if (ship == null) return;

        MissileShieldScript script = new MissileShieldScript(missile, ship, weapon);
        Global.getCombatEngine().addPlugin(script);
    }
    public class MissileShieldScript extends BaseEveryFrameCombatPlugin implements MissileAIPlugin {
        protected  MissileAPI missile;
        protected  ShipAPI ship;
        protected  WeaponAPI weapon;
        protected ShipAPI MissileShieldDrone;
        protected boolean runonce = true;
        public MissileShieldScript(MissileAPI missile, ShipAPI ship, WeaponAPI weapon) {
            this.missile = missile;
            this.ship = ship;
            this.weapon = weapon;
        }
        @Override
        public void advance(float amount, List<InputEventAPI> events) {
            if (Global.getCombatEngine().isPaused()) return;

            if (missile.isFizzling()) {
                if (MissileShieldDrone != null) {
                    Global.getCombatEngine().removeEntity(MissileShieldDrone);
                }
            }
            boolean doCleanup = missile.isExpired() || missile.didDamage() || !Global.getCombatEngine().isEntityInPlay(missile);
            if (doCleanup) {
                if (MissileShieldDrone != null) {
                    Global.getCombatEngine().removeEntity(MissileShieldDrone);
                    missile.setCollisionClass(CollisionClass.MISSILE_NO_FF);
                }
                Global.getCombatEngine().removePlugin(this);
                return;
            }

            if ( missile.isArmed() && !missile.isFizzling() && !missile.isFading()) {
                boolean SpawnMissileShieldDrone = false;
                if (missile.getAI() instanceof GuidedMissileAI) {
                    GuidedMissileAI ai = (GuidedMissileAI) missile.getAI();
                    SpawnMissileShieldDrone = ai.getTarget()!=null;
                }
                if(SpawnMissileShieldDrone && runonce){
                    runonce = false;
                    ShipHullSpecAPI spec = Global.getSettings().getHullSpec("ssp_MissileShieldDrone");
                    ShipVariantAPI variant = Global.getSettings().createEmptyVariant("ssp_MissileShieldDrone", spec);
                    variant.addMod("ssp_Temporaryshield");
                    MissileShieldDrone = Global.getCombatEngine().createFXDrone(variant);
                    MissileShieldDrone.setLayer(CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);
                    MissileShieldDrone.setOwner(ship.getOriginalOwner());
                    MissileShieldDrone.getMutableStats().getHullDamageTakenMult().modifyMult("ssp_MissileShield", 0f); // so it's non-targetable
                    MissileShieldDrone.setDrone(true);
                    MissileShieldDrone.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DRONE_MOTHERSHIP, 100000f, ship);
                    MissileShieldDrone.setCollisionClass(CollisionClass.FIGHTER);
                    MissileShieldDrone.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK,null,0);
                    Global.getCombatEngine().addEntity(MissileShieldDrone);
                }
            }
            Stateupdate(amount);
        }
        protected void Stateupdate(float amount) {
            if (MissileShieldDrone != null) {
                MissileShieldDrone.setOwner(missile.getOwner());
                MissileShieldDrone.getLocation().set(missile.getLocation());
                MissileShieldDrone.setFacing(missile.getFacing());
                MissileShieldDrone.getVelocity().set(missile.getVelocity());
                MissileShieldDrone.setAngularVelocity(missile.getAngularVelocity());
                if(MissileShieldDrone.getShield()!=null && MissileShieldDrone.getShield().isOn()){
                    missile.setCollisionClass(CollisionClass.PROJECTILE_NO_FF);
                }else if(MissileShieldDrone.getShield()!=null && MissileShieldDrone.getShield().isOff()){
                    missile.setCollisionClass(CollisionClass.MISSILE_NO_FF);}
            }
        }
        public void advance(float amount) {
            // MissileAIPlugin.advance()
            // unused, but just want the missile to have a non-null AI
        }
    }
}
