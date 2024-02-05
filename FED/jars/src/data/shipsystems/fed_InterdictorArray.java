package data.shipsystems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.combat.WeaponGroupAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.FindShipFilter;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class fed_InterdictorArray extends BaseShipSystemScript {

    public static final Object SHIP_KEY = new Object();
    public static final Object TARGET_KEY = new Object();

    public static final float WING_EFFECT_RANGE = 400f;

    public static final float RANGE = 1200f;
    public static final Color EFFECT_COLOR = new Color(100, 255, 165, 75);

    public static class TargetData {

        public ShipAPI target;
        public float sinceLastAfterimage = 0f;
        public boolean lastAbove = false;

        public TargetData(ShipAPI target) {
            this.target = target;
        }
    }

    public void apply(MutableShipStatsAPI stats, final String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        
        Vector2f accordant_l = VectorUtils.rotateAroundPivot(new Vector2f(ship.getLocation().x - 35, ship.getLocation().y + 140), ship.getLocation(), ship.getFacing()-90f);
        Vector2f accordant_r = VectorUtils.rotateAroundPivot(new Vector2f(ship.getLocation().x + 35, ship.getLocation().y + 140), ship.getLocation(), ship.getFacing()-90f);

        final String targetDataKey = ship.getId() + "_interdictor_target_data";

        Object targetDataObj = Global.getCombatEngine().getCustomData().get(targetDataKey);
        if (state == State.IN && targetDataObj == null) {
            ShipAPI target = findTarget(ship);
            Global.getCombatEngine().getCustomData().put(targetDataKey, new TargetData(target));
        } else if (state == State.IDLE && targetDataObj != null) {
            Global.getCombatEngine().getCustomData().remove(targetDataKey);
        }
        if (targetDataObj == null || ((TargetData) targetDataObj).target == null) {
            return;
        }

        final TargetData targetData = (TargetData) targetDataObj;
        CombatEngineAPI gameEngine = Global.getCombatEngine();

        //ShipAPI target = targetData.target;
        List<ShipAPI> targets = new ArrayList<ShipAPI>();
        if (targetData.target.isFighter() || targetData.target.isDrone()) {
            CombatEngineAPI engine = Global.getCombatEngine();
            List<ShipAPI> ships = engine.getShips();
            for (ShipAPI other : ships) {
                if (other.isShuttlePod()) {
                    continue;
                }
                if (other.isHulk()) {
                    continue;
                }
                if (!other.isDrone() && !other.isFighter()) {
                    continue;
                }
                if (other.getOriginalOwner() != targetData.target.getOriginalOwner()) {
                    continue;
                }

                float dist = Misc.getDistance(other.getLocation(), targetData.target.getLocation());
                if (dist > WING_EFFECT_RANGE) {
                    continue;
                }

                targets.add(other);
            }
        } else {
            targets.add(targetData.target);
        }

        boolean first = true;
        for (ShipAPI target : targets) {
            if (effectLevel >= 1) {
                Color color = getEffectColor(target);
                color = Misc.setAlpha(color, 255);

                

                ShipEngineControllerAPI ec = target.getEngineController();
                float limit = ec.getFlameoutFraction() * 0.95f;
                if (target.isDrone() || target.isFighter()) {
                    limit = 1f;
                }

                float disabledSoFar = 0f;
                float activeHealth = 0f;
                boolean disabledAnEngine = false;
                List<ShipEngineAPI> engines = new ArrayList<ShipEngineAPI>(ec.getShipEngines());
                Collections.shuffle(engines);

                //Disable engines by fractional power, at random
                //Potentially leaves large engines alone, disables tiny ones
                //   for minimal effect - which is lame
                for (ShipEngineAPI engine : engines) {
                    if (engine.isDisabled()) {
                        continue;
                    }

                    float contrib = engine.getContribution();
                    activeHealth += engine.getHitpoints();

                    if (disabledSoFar + contrib <= limit) {

                        //gameEngine.addFloatingText(engine.getLocation().translate(0, 100 + (float) (100f * Math.random())), "CNT: " + (contrib), 15f, Color.WHITE, target, 1f, 0.5f);
                        activeHealth -= engine.getHitpoints();
                        engine.disable();
                        gameEngine.spawnEmpArcVisual(accordant_l, ship, engine.getLocation(), target, (contrib * 50f), EFFECT_COLOR, Color.white);
                        gameEngine.spawnEmpArcVisual(accordant_r, ship, engine.getLocation(), target,  (contrib * 50f), EFFECT_COLOR, Color.white);
                        disabledSoFar += contrib;
                        //gameEngine.addFloatingText(engine.getLocation().translate(0, 100 + (float) (100f * Math.random())), "DSF: " + (disabledSoFar), 15f, Color.WHITE, target, 1f, 0.5f);
                        disabledAnEngine = true;
                    }
                }

                //Apply EMP damage to passed over engines if there is remaining
                //fraction left, based on remaiing engine health
                if (disabledSoFar < (limit * 0.8)) {
                    float healthToDamage = activeHealth * (limit - disabledSoFar);
                    //gameEngine.addFloatingText(target.getLocation(), "HTD: " + (healthToDamage), 15f, Color.blue, target, 1f, 0.5f);
                    for (ShipEngineAPI engine : engines) {
                        if (engine.isDisabled()) {
                            //gameEngine.addFloatingText(engine.getLocation().translate(0, 10 + (float) (100f * Math.random())), "dis_skip", 15f, Color.yellow, target, 1f, 0.5f);
                            continue;
                        }

                        if (healthToDamage > 0 && healthToDamage < engine.getHitpoints()) {
                            float dam = (engine.getHitpoints() - healthToDamage);
                            //gameEngine.addFloatingText(engine.getLocation(), "DAM " + (engine.getHitpoints() - healthToDamage), 15f, Color.CYAN, target, 1f, 0.5f);
                            engine.setHitpoints(engine.getHitpoints() - healthToDamage);
                            healthToDamage -= dam;
                        }
                    }
                }

                if (!disabledAnEngine) {
                    if (engines.size() == 0 || target.getEngineController().isFlamedOut()) {
                        //gameEngine.addFloatingText(target.getLocation(), "DSBL WPN", 15f, Color.red, target, 1f, 0.5f);
                        //VectorUtils.rotateAroundPivot(toRotate, pivotPoint, )
                        
                        
                            gameEngine.spawnEmpArcPierceShields(ship, accordant_l, ship, target, DamageType.ENERGY, 0, 2000, 10000, "tachyon_lance_emp_impact", 20, EFFECT_COLOR, Color.white);
                            gameEngine.spawnEmpArcPierceShields(ship, accordant_r, ship, target, DamageType.ENERGY, 0, 2000, 10000, "tachyon_lance_emp_impact", 20, EFFECT_COLOR, Color.white);
                            gameEngine.spawnEmpArcPierceShields(ship, accordant_l, ship, target, DamageType.ENERGY, 0, 1000, 10000, "tachyon_lance_emp_impact", 10, EFFECT_COLOR, Color.white);
                            gameEngine.spawnEmpArcPierceShields(ship, accordant_r, ship, target, DamageType.ENERGY, 0, 1000, 10000, "tachyon_lance_emp_impact", 10, EFFECT_COLOR, Color.white);
                            
                            
                        //Global.getCombatEngine().applyDamage(target, randomPoint, 0, DamageType.ENERGY, 10000, true, false, false);

                    } else {
                        for (ShipEngineAPI engine : engines) {
                            //gameEngine.addFloatingText(target.getLocation(), "DSBL ONE ENG", 15f, Color.red, target, 1f, 0.5f);

                            if (engine.isDisabled()) {
                                continue;
                            }
                            engine.disable();
                            gameEngine.spawnEmpArcVisual(accordant_l, ship, engine.getLocation(), target, 50f, EFFECT_COLOR, Color.white);
                            gameEngine.spawnEmpArcVisual(accordant_r, ship, engine.getLocation(), target,  50f, EFFECT_COLOR, Color.white);
                            break;
                        }
                    }
                } else {
                    if (first) {
                    if (target.getFluxTracker().showFloaty()
                            || ship == Global.getCombatEngine().getPlayerShip()
                            || target == Global.getCombatEngine().getPlayerShip()) {
                        target.getFluxTracker().showOverloadFloatyIfNeeded("Drive Interdicted!", color, 4f, true);
                    }
                    first = false;
                }
                }
                ec.computeEffectiveStats(ship == Global.getCombatEngine().getPlayerShip());
            }

            if (effectLevel > 0) {
                float jitterLevel = effectLevel;
                float maxRangeBonus = 20f + target.getCollisionRadius() * 0.25f;
                float jitterRangeBonus = jitterLevel * maxRangeBonus;
                if (state == State.OUT) {
                    jitterRangeBonus = maxRangeBonus + (1f - jitterLevel) * maxRangeBonus;
                }

                target.setJitter(this,
                        //target.getSpriteAPI().getAverageColor(),
                        getEffectColor(target),
                        jitterLevel, 6, 0f, 0 + jitterRangeBonus);

                if (first) {
                    ship.setJitter(this,
                            //target.getSpriteAPI().getAverageColor(),
                            getEffectColor(targetData.target),
                            jitterLevel, 6, 0f, 0 + jitterRangeBonus);
                }
            }
        }
    }

    protected Color getEffectColor(ShipAPI ship) {
        if (ship.getEngineController().getShipEngines().isEmpty()) {
            return EFFECT_COLOR;
        }
        return Misc.setAlpha(ship.getEngineController().getShipEngines().get(0).getEngineColor(), EFFECT_COLOR.getAlpha());
    }

    public void unapply(MutableShipStatsAPI stats, String id) {

    }

    protected ShipAPI findTarget(ShipAPI ship) {
        FindShipFilter filter = new FindShipFilter() {
            public boolean matches(ShipAPI ship) {
                return !(ship.getEngineController().isFlamedOut()
                        || ship.getHullSpec().hasTag("module_hull_bar_only")
                        || ship.getVariant().hasHullMod("shield_always_on")
                        || ship.getVariant().hasHullMod("vast_bulk"));
            }
        };

        float range = getMaxRange(ship);
        boolean player = ship == Global.getCombatEngine().getPlayerShip();
        ShipAPI target = ship.getShipTarget();
        if (target != null) {
            float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
            float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
            if (dist > range + radSum) {
                target = null;
            }
        } else {
            if (target == null || target.getOwner() == ship.getOwner()) {
                if (player) {
                    target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), HullSize.FRIGATE, range, true, filter);
                } else {
                    Object test = ship.getAIFlags().getCustom(AIFlags.MANEUVER_TARGET);
                    if (test instanceof ShipAPI) {
                        target = (ShipAPI) test;
                        float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
                        float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
                        if (dist > range + radSum) {
                            target = null;
                        }
                    }
                }
            }
            if (target == null) {
                target = Misc.findClosestShipEnemyOf(ship, ship.getLocation(), HullSize.FRIGATE, range, true, filter);
            }
        }

        return target;
    }

    protected float getMaxRange(ShipAPI ship) {
        return RANGE;
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
//		if (effectLevel > 0) {
//			if (index == 0) {
//				float damMult = 1f + (DAM_MULT - 1f) * effectLevel;
//				return new StatusData("" + (int)((damMult - 1f) * 100f) + "% more damage to target", false);
//			}
//		}
        return null;
    }

    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (system.isOutOfAmmo()) {
            return null;
        }
        if (system.getState() != SystemState.IDLE) {
            return null;
        }

        ShipAPI target = findTarget(ship);
        if (target != null && target != ship) {
            return "READY";
        }
        if (target == null && ship.getShipTarget() != null) {
            return "OUT OF RANGE";
        }
        return "NO TARGET";
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        if (system.isActive()) {
            return true;
        }
        ShipAPI target = findTarget(ship);

        return target != null && target != ship;
    }

}
