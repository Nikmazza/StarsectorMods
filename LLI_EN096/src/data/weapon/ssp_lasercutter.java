package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.WeaponGroupSpec;
import com.fs.starfarer.api.loading.WeaponGroupType;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class ssp_lasercutter implements OnFireEffectPlugin {
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        if (!(projectile instanceof MissileAPI)) return;

        MissileAPI missile = (MissileAPI) projectile;

        ShipAPI ship = null;
        if (weapon != null) ship = weapon.getShip();
        if (ship == null) return;

        ssp_lasercutter_effect script = new ssp_lasercutter_effect(missile, ship, weapon);
        Global.getCombatEngine().addPlugin(script);
    }
    public class ssp_lasercutter_effect extends BaseEveryFrameCombatPlugin  {
        public float laserrange=506f;
        protected  MissileAPI missile;
        protected  ShipAPI ship;
        protected  WeaponAPI weapon;
        protected ShipAPI CutterDrone_L;
        protected ShipAPI CutterDrone_R;
        protected float radius=0f;
        protected boolean runonce = true;
        public ssp_lasercutter_effect(MissileAPI missile, ShipAPI ship, WeaponAPI weapon) {
            this.missile = missile;
            this.ship = ship;
            this.weapon = weapon;
        }
        @Override
        public void advance(float amount, List<InputEventAPI> events) {
            if (Global.getCombatEngine().isPaused()) return;


            if (missile.isFizzling()) {
                if (CutterDrone_L != null && CutterDrone_R != null) {
                    Global.getCombatEngine().removeEntity(CutterDrone_L);
                    Global.getCombatEngine().removeEntity(CutterDrone_R);
                }
            }
            boolean doCleanup = missile.isExpired() || missile.didDamage() || !Global.getCombatEngine().isEntityInPlay(missile);
            if (doCleanup) {
                if (CutterDrone_L != null && CutterDrone_R != null) {
                    Global.getCombatEngine().removeEntity(CutterDrone_L);
                    Global.getCombatEngine().removeEntity(CutterDrone_R);
                }
                Global.getCombatEngine().removePlugin(this);
                return;
            }

            if ( missile.isArmed() && !missile.isFizzling() && !missile.isFading()) {
                if(runonce){
                    runonce = false;
                    ShipHullSpecAPI spec = Global.getSettings().getHullSpec("ssp_MissileShieldDrone");
                    ShipVariantAPI variant = Global.getSettings().createEmptyVariant("ssp_MissileShieldDrone", spec);
                    variant.addWeapon("WS 000", "ssp_plasmamissile_payload");
                    WeaponGroupSpec g = new WeaponGroupSpec(WeaponGroupType.ALTERNATING);
                    g.addSlot("WS 000");
                    variant.addWeaponGroup(g);

                    CutterDrone_L = Global.getCombatEngine().createFXDrone(variant);
                    CutterDrone_L.setLayer(CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);
                    CutterDrone_L.setOwner(ship.getOriginalOwner());
                    CutterDrone_L.getMutableStats().getHullDamageTakenMult().modifyMult("ssp_lasercutter_effect", 0f); // so it's non-targetable
                    CutterDrone_L.getMutableStats().getEnergyWeaponFluxCostMod().modifyMult("ssp_lasercutter_effect", 0f);
                    CutterDrone_L.getMutableStats().getBallisticWeaponFluxCostMod().modifyMult("ssp_lasercutter_effect", 0f);
                    CutterDrone_L.getMutableStats().getMissileWeaponFluxCostMod().modifyMult("ssp_lasercutter_effect", 0f);
                    CutterDrone_L.getMutableStats().getEnergyWeaponDamageMult().applyMods(ship.getMutableStats().getMissileWeaponDamageMult());
                    CutterDrone_L.getMutableStats().getMissileWeaponDamageMult().applyMods(ship.getMutableStats().getMissileWeaponDamageMult());
                    CutterDrone_L.getMutableStats().getBallisticWeaponDamageMult().applyMods(ship.getMutableStats().getMissileWeaponDamageMult());
                    CutterDrone_L.setDrone(true);
                    CutterDrone_L.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DRONE_MOTHERSHIP, 100000f, ship);
                    CutterDrone_L.setCollisionClass(CollisionClass.FIGHTER);
                    CutterDrone_L.giveCommand(ShipCommand.SELECT_GROUP, null, 0);
                    Global.getCombatEngine().addEntity(CutterDrone_L);

                    CutterDrone_R = Global.getCombatEngine().createFXDrone(variant);
                    CutterDrone_R.setLayer(CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);
                    CutterDrone_R.setOwner(ship.getOriginalOwner());
                    CutterDrone_R.getMutableStats().getHullDamageTakenMult().modifyMult("ssp_lasercutter_effect", 0f); // so it's non-targetable
                    CutterDrone_R.getMutableStats().getEnergyWeaponFluxCostMod().modifyMult("ssp_lasercutter_effect", 0f);
                    CutterDrone_R.getMutableStats().getBallisticWeaponFluxCostMod().modifyMult("ssp_lasercutter_effect", 0f);
                    CutterDrone_R.getMutableStats().getMissileWeaponFluxCostMod().modifyMult("ssp_lasercutter_effect", 0f);
                    CutterDrone_R.getMutableStats().getEnergyWeaponDamageMult().applyMods(ship.getMutableStats().getMissileWeaponDamageMult());
                    CutterDrone_R.getMutableStats().getMissileWeaponDamageMult().applyMods(ship.getMutableStats().getMissileWeaponDamageMult());
                    CutterDrone_R.getMutableStats().getBallisticWeaponDamageMult().applyMods(ship.getMutableStats().getMissileWeaponDamageMult());
                    CutterDrone_R.setDrone(true);
                    CutterDrone_R.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DRONE_MOTHERSHIP, 100000f, ship);
                    CutterDrone_R.setCollisionClass(CollisionClass.FIGHTER);
                    CutterDrone_R.giveCommand(ShipCommand.SELECT_GROUP, null, 0);
                    Global.getCombatEngine().addEntity(CutterDrone_R);
                }
            }
            Stateupdate(amount);
        }
        protected void Stateupdate(float amount) {
            if (CutterDrone_L != null && CutterDrone_R != null) {
                radius += amount*laserrange;
                if(radius>=laserrange){
                    radius=laserrange;
                    CutterDrone_R.giveCommand(ShipCommand.FIRE, missile.getLocation(), 0);
                    CutterDrone_L.giveCommand(ShipCommand.FIRE, missile.getLocation(), 0);
                }
                CutterDrone_L.setOwner(missile.getOwner());
                CutterDrone_L.getLocation().set(MathUtils.getPoint( missile.getLocation(),radius,missile.getFacing()+90));
                CutterDrone_L.setFacing(missile.getFacing());
                CutterDrone_L.getVelocity().set(missile.getVelocity());
                CutterDrone_L.setAngularVelocity(missile.getAngularVelocity());
                CutterDrone_R.setOwner(missile.getOwner());
                CutterDrone_R.getLocation().set(MathUtils.getPoint( missile.getLocation(),radius,missile.getFacing()-90));
                CutterDrone_R.setFacing(missile.getFacing()-180);
                CutterDrone_R.getVelocity().set(missile.getVelocity());
                CutterDrone_R.setAngularVelocity(missile.getAngularVelocity());


        }

    }
    }
}

