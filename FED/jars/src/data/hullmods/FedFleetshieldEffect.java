package data.hullmods;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.BoundsAPI.SegmentAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatUIAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import java.awt.Color;
import java.util.List;
import org.lazywizard.lazylib.combat.CombatUtils;

public class FedFleetshieldEffect extends BaseHullMod {

    private final float HE_DAMAGE_TAKEN_PERCENT = 100f;
    //private final float HARDFLUX_DISSIPATION_PERCENT = 50;
    private final float OVERLOAD_ADDITION = 15f;

    private CombatEngineAPI engine;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        //stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_BONUS);
        stats.getHighExplosiveShieldDamageTakenMult().modifyPercent(id, HE_DAMAGE_TAKEN_PERCENT);
        //stats.getHardFluxDissipationFraction().modifyPercent(id, (float) HARDFLUX_DISSIPATION_PERCENT);
        stats.getOverloadTimeMod().modifyFlat(id, OVERLOAD_ADDITION);

    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.setCollisionClass(CollisionClass.FIGHTER);
        ship.getShield().setRadius(ship.getShieldRadiusEvenIfNoShield() * 0.1f);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        engine = Global.getCombatEngine();

        if (engine == null) {
            return;
        }
        if (engine.isPaused()) {
            /*if (Global.getCurrentState().equals(GameState.COMBAT)) {
                if (engine.getCombatUI().isShowingCommandUI()) {
                    ship.setCollisionRadius(100f);
                } else {
                    ship.setCollisionRadius(ship.getShieldRadiusEvenIfNoShield() + 200f);
                }
                return;
            }*/
            return;
        }

        //ship.setCollisionRadius(ship.getShieldRadiusEvenIfNoShield() + 200f);

        if (ship.isAlive()) {

            List<ShipAPI> allShips = engine.getShips();
            List<ShipAPI> nearShips = CombatUtils.getShipsWithinRange(ship.getLocation(), ship.getShieldRadiusEvenIfNoShield() * 0.8f);
            for (ShipAPI randomShip : allShips) {
                if (nearShips.contains(randomShip)) {
                    if (ship.getOwner() != randomShip.getOwner()) {
                        randomShip.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BIGGEST_THREAT, 5f, ship);
                        randomShip.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.NEEDS_HELP, 5f, ship);
                    } else if (!ship.getFluxTracker().isOverloadedOrVenting()) {
                        randomShip.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.SAFE_VENT, 10f);
                        randomShip.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS, 1f);
                        randomShip.getMutableStats().getHighExplosiveDamageTakenMult().modifyFlat(randomShip.getId() + "_fsprotection", -0.95f);
                        float chanceOfUnderlay = 0.1f + 0.15f * (1f - ship.getFluxLevel());
                        if (Math.random() > chanceOfUnderlay) {
                            randomShip.addAfterimage(
                                    new Color(25, 105, 45, 25), //color
                                    0f, //X Location rel to ship center
                                    0f, //Y Location rel to ship center
                                    0, //X Velocity
                                    0, //Y Velocity
                                    15f, //Max Jitter, units of distance
                                    0.5f, //Fade-in time
                                    0.5f, //Regular duration
                                    0.5f, //Fade-out time
                                    true, //Is Additive (whiteness adds up)
                                    false, //Combine with sprite color
                                    false);
                        } 
                    } else {
                        randomShip.getMutableStats().getHighExplosiveDamageTakenMult().unmodify(randomShip.getId() + "_fsprotection");
                    }
                } else {
                    randomShip.getMutableStats().getHighExplosiveDamageTakenMult().unmodify(randomShip.getId() + "_fsprotection");
                }
            }

            /*List<DamagingProjectileAPI> projectiles = CombatUtils.getProjectilesWithinRange(ship.getShield().getLocation(), ship.getShield().getRadius() - 50f);
            //engine.addFloatingText(ship.getLocation(), "Radius:" + ship.getShield().getRadius(), 15f, Color.WHITE, ship, 1f, 0.5f);
            //engine.addFloatingText(ship.getLocation(), "Location:" + ship.getShield().getLocation(), 15f, Color.WHITE, ship, 1f, 0.5f);
            for (DamagingProjectileAPI proj : projectiles) {
                if (!(proj.getSource().isAlly() || proj.getSource().getOwner() == (ship.getOwner())) && proj.getElapsed() > 0.25) {
                    engine.removeEntity(proj);
                }
            }*/
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        return null;
    }
}
