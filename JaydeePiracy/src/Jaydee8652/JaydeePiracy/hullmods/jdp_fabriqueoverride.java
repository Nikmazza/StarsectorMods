package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.subsystems.MagicSubsystem;
import org.magiclib.subsystems.MagicSubsystemsManager;
import org.magiclib.util.MagicUI;

import java.awt.*;

public class jdp_fabriqueoverride extends BaseHullMod {

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        super.applyEffectsAfterShipCreation(ship, id);
        if (ship != null) {
            MagicSubsystemsManager.addSubsystemToShip(ship, new jdp_fabriqueoverride.jdp_lampoverride(ship));
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        return "Can only be prebuilt into specific hulls.";
    }

    @Override
    public boolean affectsOPCosts() {
        return true;
    }

    public class jdp_lampoverride extends MagicSubsystem {

        public final Color JITTER_COLOR = new Color(187, 36, 55, 255);
        public final Color JITTER_UNDER_COLOR = new Color(181, 54, 70, 255);

        public jdp_lampoverride(ShipAPI ship) {
            super(ship);
        }

        @Override
        public float getBaseInDuration() {
            return 1f;
        }

        @Override
        public float getBaseActiveDuration() {
            return 0f;
        }

        @Override
        public float getBaseOutDuration() {
            return 0.5f;
        }

        @Override
        public float getBaseCooldownDuration() {
            return 2f;
        }

        @Override
        public int getMaxCharges() {
            return 1;
        }

        @Override
        public float getBaseChargeRechargeDuration() {
            return (160000 * ((1 / ship.getMassWithModules())));
        }

        @Override
        public boolean shouldActivateAI(float amount) {
            ShipAPI target = ship.getShipTarget();
            if (target != null && (ship.getEngineController().isAccelerating() || ship.getEngineController().isDecelerating() || ship.getEngineController().isStrafingLeft() || ship.getEngineController().isStrafingRight())) {
                float score = 0f;

                float xchange = target.getLocation().x - ship.getLocation().x;
                float ychange = target.getLocation().y - ship.getLocation().y;
                float target_angle = (float) Math.toDegrees(Math.atan2(ychange, xchange));

                if ((target_angle - ship.getFacing()) > 0.20f) {
                    score += 12f;
                }

                score += ship.getFluxLevel() * 4f;

                float engagementRange = aiData.getEngagementRange() + (500000 * ((1 / (ship.getMassWithModules() + 1500))));
                float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
                if (dist > engagementRange) {
                    score += 12f;
                }

                return score > 10f;
            }

            return false;
        }

        @Override
        public void onActivate() {
        }


        public void advance(float amount, boolean isPaused) {
            ShipAPI ship = null;
            boolean player = false;

            if (stats.getEntity() instanceof ShipAPI) {
                ship = (ShipAPI) stats.getEntity();
                player = ship == Global.getCombatEngine().getPlayerShip();
            } else {
                return;
            }

            if (ship.getCaptain().isPlayer()) {
                player = true;
            }
            ;

            float jitterLevel = getEffectLevel();
            if (state == State.ACTIVE) {
                if (player == true) {
                    Global.getSoundPlayer().playSound("system_phase_skimmer", 1f, 0.15f, ship.getLocation(), ship.getVelocity());
                    Global.getSoundPlayer().playSound("mote_attractor_system_activated", 2f, 0.3f, ship.getLocation(), ship.getVelocity());
                } else {
                    Global.getSoundPlayer().playSound("system_phase_skimmer", 5f, 0.8f, ship.getLocation(), ship.getVelocity());
                    Global.getSoundPlayer().playSound("mote_attractor_system_activated", 6f, 1f, ship.getLocation(), ship.getVelocity());
                }
            } else if (state == State.OUT) {
                jitterLevel *= jitterLevel;
            }


            float maxRangeBonus = 25f;
            float jitterRangeBonus = jitterLevel * maxRangeBonus;

            //Ship Jitter
            ship.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel, 5, 0f, 3f + jitterRangeBonus);
            ship.setJitter(this, JITTER_COLOR, jitterLevel, 4, 0f, 0 + jitterRangeBonus);

            //Module Jitter
            for (ShipAPI module : ship.getChildModulesCopy()) {
                module.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel, 5, 0f, 3f + jitterRangeBonus);
                module.setJitter(this, JITTER_COLOR, jitterLevel, 4, 0f, 0 + jitterRangeBonus);
            }

     //       MagicUI.drawInterfaceStatusBar(ship, 1-shieldTLevel, barColor, null, 0, "SHIELD", (int) (100-100*shieldTLevel));

        }

        @Override
        public void onFinished() {
        }

        @Override
        public String getDisplayText() {
            return "Fabrique Override";
        }
    }
}