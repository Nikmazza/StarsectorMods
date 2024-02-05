package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.magiclib.util.MagicRender;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import java.awt.Color;
import java.util.List;
import org.lazywizard.lazylib.IOUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class fed_TravelDriveStats extends BaseShipSystemScript {

    private CombatEngineAPI engine;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        ShipAPI ship = null;
        if (engine != Global.getCombatEngine()) {
            engine = Global.getCombatEngine();
        }

        Boolean validPlayerShip = false;
        // We can get ship APIs from a MutableShipStatAPI using getEntity()
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            if (ship == Global.getCombatEngine().getPlayerShip()) {
                if (effectLevel > 0f && !ship.isRetreating()) {
                    Global.getCombatEngine().getTimeMult().modifyPercent(id, -(25f * effectLevel));
                    validPlayerShip = true;
                }
            }
        } else {
            Global.getCombatEngine().getTimeMult().unmodify(id);
            return;
        }

        // Maybe saves computing power
        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        //engine.addFloatingText(ship.getLocation(), ""+ship.getFacing(), 15f, Color.WHITE, ship, 1f, 0.5f);
        if (engine.getCustomData().containsKey(ship.getFleetMemberId() + "_usedTravelDrive")) {
            if (engine.getCustomData().get(ship.getFleetMemberId() + "_usedTravelDrive").equals(true)) {
                if (ship.getFacing() == 90f) {
                    if (Global.getCombatEngine().getTotalElapsedTime(false) > 10f
                                || Global.getCombatEngine().isFleetsInContact()) {
                        
                        } else if (Global.getCombatEngine().getContext().getOtherGoal() != null && Global.getCombatEngine().getContext().getPlayerGoal() != null) {
                         if (Global.getCombatEngine().getContext().getOtherGoal().equals(FleetGoal.ESCAPE)) {
                            ship.getLocation().setY(ship.getLocation().getY() - 1300f);

                        } else if (Global.getCombatEngine().getContext().getPlayerGoal().equals(FleetGoal.ATTACK)) {
                            if (Global.getCombatEngine().getTotalElapsedTime(false) > 30f
                                    || Global.getCombatEngine().isFleetsInContact()) {

                            } else {
                                ship.getLocation().setY(ship.getLocation().getY() - 2600f);
                            }
                        } else {
                            ship.getLocation().setY(ship.getLocation().getY() - 2600f);
                        }

                    } else {
                        ship.getLocation().setY(ship.getLocation().getY() - 2600f);
                    }
                }
                if (ship.getFacing() == 270f) {
                    ship.getLocation().setY(ship.getLocation().getY() + 2600f);
                }

                engine.getCustomData().remove(ship.getFleetMemberId() + "_usedTravelDrive");
                engine.getCustomData().put(ship.getFleetMemberId() + "_usedTravelDrive", false);
            }
        } else {
            engine.getCustomData().put(ship.getFleetMemberId() + "_usedTravelDrive", true);
        }

        //engine.addFloatingText(ship.getLocation(), ""+ship.getFacing(), 15f, Color.WHITE, ship, 1f, 0.5f);
        /*
        List<ShipAPI> nearShips = CombatUtils.getShipsWithinRange(ship.getLocation(), ship.getShieldRadiusEvenIfNoShield());
        if (!nearShips.isEmpty()) {
            ship.getTravelDrive().forceState(ship.getTravelDrive().getState(), 1);
            for (ShipAPI ships : nearShips){
                ShipAIPlugin shipai = (ShipAIPlugin) ships.getAI();
                
            }
        }

        else {
            ship.getTravelDrive().getSpecAPI().setToggle(true);
            ship.getTravelDrive().getSpecAPI().setActive(0f);
        }
        else if (ship.getFullTimeDeployed() > 10f && ship.getFullTimeDeployed() < 20) {
            if (ship.getTravelDrive().getState() != ShipSystemAPI.SystemState.OUT) {
                ship.getTravelDrive().forceState(ShipSystemAPI.SystemState.OUT, 1);
            }
        }*/
        // Switch state because fun, right?
        if (null != state) {
            switch (state) {

                // SYSTEM IS SPOOLING UP
                // This even applies to when a ship enters combat
                // They kinda just sit there, no way to work around it really.
                case IN:

                    //STAT STUFF: Not sure how much is needed, but it's fine sitting here.
                    //DO NOT ALLOW SHIP TO MOVE
                    // Gradually reduce ship transparency, regular system file adds jitter copies.
                    ship.setAlphaMult(1 - 0.8f * effectLevel);
                    ship.setCollisionClass(CollisionClass.FIGHTER);
                    // If a ship has modules, we need it to fade out.disable like the rest.
                    if (ship.isShipWithModules()) {
                        List<ShipAPI> modules = ship.getChildModulesCopy();
                        for (ShipAPI childModule : modules) {
                            childModule.setCollisionClass(CollisionClass.FIGHTER);
                            // Fookin null checks
                            if (childModule.getShield() != null) {
                                if (childModule.getShield().getType() == (ShieldAPI.ShieldType.FRONT) || (childModule.getShield().getType() == ShieldAPI.ShieldType.OMNI)) {
                                    // Overkill never fails
                                    childModule.getShield().setArc(0f);
                                    childModule.getShield().setRadius(0f);
                                }
                            }
                            childModule.setAlphaMult(1 - 0.8f * effectLevel);
                        }
                    }
                    //RENDER EXPLOSION WHEN JUMPING
                    //Special logic needed to spawn particles only 0.25 seconds before jumping

                    fed_spawnTravelDriveExplosion(stats, ship, effectLevel);
                    fed_spawnTravelDriveExplosion(stats, ship, effectLevel);
                    fed_spawnTravelDriveExplosion(stats, ship, effectLevel);
                    break;

                case ACTIVE:

                    ship.setAlphaMult(1 - 0.8f * effectLevel);

                    // Make ship not bonk things when warping.
                    ship.setCollisionClass(CollisionClass.FIGHTER);
                    //ship.setHullSize(ShipAPI.HullSize.FIGHTER);

                    stats.getMaxSpeed().modifyFlat(id, 600f);
                    stats.getAcceleration().modifyFlat(id, 600f);
                    // Only way to make it seem literally FTL
                    stats.getTimeMult().modifyPercent(id, 200 * effectLevel);

                    // Some prerandomized variables to argument size in cosmetic particle calls
                    // RENDER PARTICLES FOR WARPING SHIPS
                    /*ship.addAfterimage(
                            new Color(125, 125, 1, 255), //color
                            MathUtils.getRandomNumberInRange(-5f, 5f), //X Location rel to ship center
                            MathUtils.getRandomNumberInRange(-5f, 5f), //Y Location rel to ship center
                            (stats.getEntity().getVelocity().getX()) * -1f, //X Velocity
                            (stats.getEntity().getVelocity().getY()) * -1f, //Y Velocity
                            5f, //Max Jitter, units of distance
                            0f, //Fade-in time
                            0f, //Regular duration
                            0.5f, //Fade-out time
                            true, //Is Additive (whiteness adds up)
                            true, //Combine with sprite color
                            true); //Above ship
                     */
                    // Fuckin modules need afterimages too waaaah
                    if (ship.isShipWithModules()) {
                        List<ShipAPI> modules = ship.getChildModulesCopy();
                        for (ShipAPI childModule : modules) {
                            childModule.addAfterimage(
                                    new Color(125, 100, 25, 55), //color
                                    MathUtils.getRandomNumberInRange(-5f, 5f), //X Location rel to ship center
                                    MathUtils.getRandomNumberInRange(-5f, 5f), //Y Location rel to ship center
                                    (stats.getEntity().getVelocity().getX()) * -1f, //X Velocity
                                    (stats.getEntity().getVelocity().getY()) * -1f, //Y Velocity
                                    5f, //Max Jitter, units of distance
                                    0f, //Fade-in time
                                    0f, //Regular duration
                                    0.2f, //Fade-out time
                                    true, //Is Additive (whiteness adds up)
                                    true, //Combine with sprite color
                                    true);
                            childModule.setCollisionClass(CollisionClass.FIGHTER);
                            // Fookin null checks
                            if (childModule.getShield() != null) {
                                if (childModule.getShield().getType() == (ShieldAPI.ShieldType.FRONT) || (childModule.getShield().getType() == ShieldAPI.ShieldType.OMNI)) {
                                    // Overkill never fails
                                    childModule.getShield().setArc(0f);
                                    childModule.getShield().setRadius(0f);
                                }
                            }
                            childModule.setAlphaMult(1 - 0.8f * effectLevel);
                        }
                    }

                    // We don't always want to render the warp trail, keeps it lighter
                    fed_spawnTravelDriveWake(stats, ship, effectLevel);
                    break;

                case OUT:
                    float shipExitSpeed = (stats.getDeceleration().modified) * 10 + ship.getMutableStats().getZeroFluxSpeedBoost().modified;

                    if (shipExitSpeed > 500f) {
                        shipExitSpeed = 500f;
                    }

                    if (ship.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP)) {
                        shipExitSpeed = (220f);
                    }

                    ship.getVelocity().scale(shipExitSpeed / (ship.getVelocity().length()));

                    stats.getTimeMult().modifyPercent(id, 200 * effectLevel);
                    ship.setAlphaMult(1 - 0.8f * effectLevel);

                    // Give modules back shields as we come out of warp.
                    if (ship.getVelocity().length() <= ship.getMaxSpeed()) { //&& ship.getFullTimeDeployed() > 1) {
                        if (ship.isShipWithModules()) {
                            List<ShipAPI> modules = ship.getChildModulesCopy();
                            for (ShipAPI childModule : modules) {
                                if (!childModule.getVariant().hasHullMod("fed_fleetshieldeffect")) {
                                    childModule.setCollisionClass(CollisionClass.SHIP);
                                }
                                childModule.ensureClonedStationSlotSpec();
                                if (childModule.getShield() != null) {
                                    if (childModule.getShield().getType() == (ShieldAPI.ShieldType.FRONT) || (childModule.getShield().getType() == ShieldAPI.ShieldType.OMNI)) {
                                        childModule.getShield().setType(childModule.getHullSpec().getShieldType());
                                        childModule.getShield().setArc(childModule.getHullSpec().getShieldSpec().getArc());
                                        // Warning! This works because StarFed modules have radius-updating in FederationDesign.java
                                        childModule.getShield().setRadius(0f);
                                    }
                                }
                                childModule.setAlphaMult(1f);
                            }
                        }
                        stats.getEntity().setCollisionClass(CollisionClass.SHIP);
                        ship.setAlphaMult(1f);
                        ship.getTravelDrive().deactivate();
                        fed_spawnTravelDriveExplosion(stats, ship, effectLevel);
                        fed_spawnTravelDriveExplosion(stats, ship, effectLevel);
                        fed_spawnTravelDriveExplosion(stats, ship, effectLevel);
                        Global.getSoundPlayer().playSound(ship.getTravelDrive().getSpecAPI().getOutOfUsesSound(), 1, 1, ship.getLocation(), new Vector2f(0f, 0f));
                        Global.getSoundPlayer().playUISound(ship.getTravelDrive().getSpecAPI().getOutOfUsesSound(), 1, 0.1f);
                    }

                    fed_spawnTravelDriveWake(stats, ship, effectLevel);

                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id
    ) {

        stats.getTimeMult().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        Global.getCombatEngine().getTimeMult().unmodify(id);

    }

    private void fed_spawnTravelDriveExplosion(MutableShipStatsAPI stats, ShipAPI ship, float effectLevel) {
        //Bright Flash, look at magicrender to find all of the args.
        MagicRender.battlespace(
                Global.getSettings().getSprite(
                        "fx",
                        "fed_warp_wave"
                ),
                stats.getEntity().getLocation(),
                new Vector2f(stats.getEntity().getVelocity().x * -1f, stats.getEntity().getVelocity().y * 0.25f),
                new Vector2f(stats.getEntity().getCollisionRadius() * 2f * (float) Math.random(), stats.getEntity().getCollisionRadius() * 2f * (float) Math.random()),
                new Vector2f(stats.getEntity().getCollisionRadius() * 4f, stats.getEntity().getCollisionRadius() * 4f),
                (float) Math.random() * 360,
                MathUtils.getRandomNumberInRange(-5, 5),
                new Color(1f, 1f, 1f, 1f),
                true,
                0, 0, 0, 0, 0,
                0.2f,
                0.1f,
                0.5f,
                CombatEngineLayers.ABOVE_SHIPS_LAYER
        );
        //More Smoke
        MagicRender.battlespace(
                Global.getSettings().getSprite(
                        "fx",
                        "fed_smoke_ring"
                ),
                stats.getEntity().getLocation(),
                new Vector2f(stats.getEntity().getVelocity().x * -0.5f, stats.getEntity().getVelocity().y * 0.1f),
                new Vector2f(stats.getEntity().getCollisionRadius() * (2 * effectLevel) * (float) Math.random(), stats.getEntity().getCollisionRadius() * (2 * effectLevel) * (float) Math.random()),
                new Vector2f(stats.getEntity().getCollisionRadius() * 2.5f, stats.getEntity().getCollisionRadius() * 2f),
                (float) Math.random() * 360,
                MathUtils.getRandomNumberInRange(-5, 5),
                new Color(1f, 1f, 1f, 1f),
                false,
                0, 0, 0, 0, 0,
                0f,
                0.1f,
                1.0f,
                CombatEngineLayers.BELOW_SHIPS_LAYER
        );
        MagicRender.battlespace(
                Global.getSettings().getSprite(
                        "fx",
                        "fed_smoke_ring"
                ),
                stats.getEntity().getLocation(),
                new Vector2f(stats.getEntity().getVelocity().x * 0f, stats.getEntity().getVelocity().y * 0f),
                new Vector2f(stats.getEntity().getCollisionRadius() * (2 * effectLevel) * (float) Math.random(), stats.getEntity().getCollisionRadius() * (2 * effectLevel) * (float) Math.random()),
                new Vector2f(stats.getEntity().getCollisionRadius() * 2.5f, stats.getEntity().getCollisionRadius() * 1),
                180f,
                MathUtils.getRandomNumberInRange(-5, 5),
                new Color(1f, 1f, 1f, 1f),
                true,
                0, 0, 0, 0, 0,
                0f,
                0f,
                0.5f,
                CombatEngineLayers.ABOVE_SHIPS_LAYER
        );
        ship.addAfterimage(
                new Color(175, 150, 75, 255), //color
                0f, //X Location rel to ship center
                0f, //Y Location rel to ship center
                0f, //X Velocity
                0f, //Y Velocity
                25f, //Max Jitter, units of distance
                0f, //Fade-in time
                0f, //Regular duration
                0.5f, //Fade-out time
                true, //Is Additive (whiteness adds up)
                false, //Combine with sprite color
                false);
    }

    private void fed_spawnTravelDriveWake(MutableShipStatsAPI stats, ShipAPI ship, float effectLevel) {
        //Bright Flash, look at magicrender to find all of the args.
        float randomVariableOut = (float) MathUtils.getRandomNumberInRange(0.8f, 1.0f);
        float shipSpriteWidthOut = stats.getEntity().getCollisionRadius();
        if ((float) Math.random() < 0.5f * effectLevel) {
            MagicRender.battlespace(
                    Global.getSettings().getSprite(
                            "fx",
                            "fed_warp_trail"
                    ),
                    stats.getEntity().getLocation(),
                    stats.getEntity().getVelocity(),
                    new Vector2f(shipSpriteWidthOut * randomVariableOut * 2f, shipSpriteWidthOut * randomVariableOut * 2f + 100f),
                    new Vector2f(shipSpriteWidthOut * randomVariableOut * -4f, shipSpriteWidthOut * randomVariableOut * -4 + 100f),
                    (float) Math.random() * 360,
                    MathUtils.getRandomNumberInRange(-5, 5),
                    new Color(1f, 1f, 1f, 1f),
                    true,
                    0, 0, 0, 0, 0,
                    0.2f,
                    0.1f,
                    0.2f,
                    CombatEngineLayers.ABOVE_SHIPS_LAYER
            );
            ship.addAfterimage(
                    new Color(125, 100, 25, 255), //color
                    0f, //X Location rel to ship center
                    0f, //Y Location rel to ship center
                    (stats.getEntity().getVelocity().getX()) * -1f, //X Velocity
                    (stats.getEntity().getVelocity().getY()) * -1f, //Y Velocity
                    5f, //Max Jitter, units of distance
                    0f, //Fade-in time
                    0f, //Regular duration
                    0.5f, //Fade-out time
                    true, //Is Additive (whiteness adds up)
                    false, //Combine with sprite color
                    true);
        }
    }

    @Override
    public StatusData getStatusData(int index, State state,
            float effectLevel
    ) {
        if (index == 0) {
            return new StatusData("FTL Microdrive Engaged", false);
        }
        return null;
    }
}
