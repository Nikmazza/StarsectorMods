package Jaydee8652.JaydeePiracy.scripts.skills.flowerfish;


import Jaydee8652.JaydeePiracy.scripts.systems.jdp_ModularJitterStats;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.fleet.FleetMember;
import com.fs.starfarer.rpg.Person;
import org.lazywizard.lazylib.MathUtils;
import org.magiclib.subsystems.MagicSubsystem;
import org.magiclib.subsystems.MagicSubsystemsManager;
import org.magiclib.util.MagicUI;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;
import org.magiclib.*;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.subsystems.MagicSubsystem;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class jdp_flowerfishUneasyMovements extends SCBaseSkillPlugin {
    private static String jdp_skimmerIcon = "graphics/icons/hullsys/displacer.png";


    @Override
    public String getAffectsString() {
        return "all ships with human officers";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color good = Misc.getPositiveHighlightColor();
        float HEIGHT = 50f;
        float PAD = 10f;


        tooltip.addPara("All ships with officers in your fleet receive the \"Maerulan Phase Skimmer\" subsystem", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltip.addSpacer(10f);

        TooltipMakerAPI relay = tooltip.beginImageWithText(jdp_skimmerIcon, HEIGHT);
        relay.addPara("Maerulan Phase Skimmer", 0f, h, "Maerulan Phase Skimmer");
        relay.addPara("Teleports the ship a short distance in the direction it's traveling and centers facing on its target.", 0f, h);
        relay.addPara(" *Has one charge by default, but is effected by bonuses that increase system charges", 0f, Misc.getGrayColor(), h);
        relay.addPara(" Maximum range scales inversely with ship mass (including modules) and is capped at 500", 0f, Misc.getGrayColor(), h);
        relay.addPara(" Recharge rate also scales inversely with ship mass (including modules) but has no cap", 0f, Misc.getGrayColor(), h);
        tooltip.addImageWithText(PAD);
        tooltip.addSpacer(10f);

        tooltip.addPara("\"Its- Its horrible! Whoever invented this, they're a monster.\"", 0f, Misc.getTextColor(), Misc.getHighlightColor());
        tooltip.addPara("  -Proving Grounds Transcript, Undisclosed Location", 0f, jdp_AptitudeFlowerfish.JDP_FLOWERFISH_COLOUR, Misc.getHighlightColor());
        tooltip.addSpacer(10f);
    }

    @Override
    public void applyEffectsToFighterSpawnedByShip(SCData data, ShipAPI fighter, ShipAPI ship, String id) {
        // No implementation needed
    }

    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
        // No implementation needed
    }

    @Override
    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) {
        if (jdp_AptitudeFlowerfish.isHumanOfficer(ship)) {
            //MagicSubsystemsManager.addSubsystemToShip(ship, new jdp_maerulanphaseskimmer(ship));
        }
    }

    @Override
    public void advance(SCData data, Float amount) {
        // No implementation needed
    }

    @Override
    public void onActivation(SCData data) {
        // No implementation needed
    }

    @Override
    public void onDeactivation(SCData data) {
        // No implementation needed
    }
}
/*
    public class jdp_maerulanphaseskimmer extends MagicSubsystem {

        public  final Color JITTER_COLOR = new Color(187,36,55,255);
        public  final Color JITTER_UNDER_COLOR = new Color(181, 54, 70,255);

        public jdp_maerulanphaseskimmer(ShipAPI ship) {
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
            return (160000 * ((1/ship.getMassWithModules())));
        }

        @Override
        public boolean shouldActivateAI(float amount) {
            return false;
        }

        @Override
        public void onActivate() {
            ShipAPI ship = null;
            boolean player = false;

            if (stats.getEntity() instanceof ShipAPI) {
                ship = (ShipAPI) stats.getEntity();
                player = ship == Global.getCombatEngine().getPlayerShip();
            } else {
                return;
            }

            sinceSwarmTargeted += Global.getCombatEngine().getElapsedInLastFrame();

            if ((state == State.COOLDOWN) && cooldownToSet >= 0f) {
                ship.getSystem().setCooldown(cooldownToSet);
                ship.getSystem().setCooldownRemaining(cooldownToSet);
                cooldownToSet = -1f;

            }

            if (state == State.IDLE || state == State.COOLDOWN || effectLevel <= 0f) {
                readyToFire = true;
            }

            if (state == State.IN || state == State.OUT) {
                float jitterLevel = effectLevel;

                float maxRangeBonus = 150f;
                float jitterRangeBonus = (1f - effectLevel * effectLevel) * maxRangeBonus;

                float brightness = 0f;
                float threshold = 0.1f;
                if (effectLevel < threshold) {
                    brightness = effectLevel / threshold;
                } else {
                    brightness = 1f - (effectLevel - threshold) / (1f - threshold);
                }
                if (brightness < 0) brightness = 0;
                if (brightness > 1) brightness = 1;
                if (state == State.OUT) {
                    jitterRangeBonus = 0f;
                    brightness = effectLevel * effectLevel;
                }
                Color color = VoltaicDischargeOnFireEffect.EMP_FRINGE_COLOR;

                ship.setJitter(this, color, jitterLevel, 5, 0f, 3f + jitterRangeBonus);
            }

            if (effectLevel == 1 && readyToFire) {
                ShipAPI target = findTarget(ship);
                readyToFire = false;
                if (target != null) {
                    CombatEngineAPI engine = Global.getCombatEngine();
                    findSlots(ship);

                    Vector2f slotLoc = mainSlot.computePosition(ship);

                    EmpArcParams params = new EmpArcParams();
                    params.segmentLengthMult = 8f;
                    params.zigZagReductionFactor = 0.15f;
                    params.fadeOutDist = 500f;
                    params.minFadeOutMult = 2f;
                    params.flickerRateMult = 0.7f;

                    if (ship.getOwner() == target.getOwner()) {
                        params.flickerRateMult = 0.3f;

                        Color color = VoltaicDischargeOnFireEffect.EMP_FRINGE_COLOR;
                        if (ThreatSwarmAI.isAttackSwarm(target)) {
                            color = VoltaicDischargeOnFireEffect.PHASE_FRINGE_COLOR;
                        }
                        float emp = 0;
                        float dam = 0;
                        EmpArcEntityAPI arc = (EmpArcEntityAPI)engine.spawnEmpArcPierceShields(ship, slotLoc, ship, target,
                                DamageType.ENERGY,
                                dam,
                                emp, // emp
                                100000f, // max range
                                "energy_lash_friendly_impact",
                                100f, // thickness
                                //new Color(100,165,255,255),
                                color,
                                new Color(255,255,255,255),
                                params
                        );
                        arc.setTargetToShipCenter(slotLoc, target);
                        arc.setCoreWidthOverride(50f);

                        arc.setSingleFlickerMode(true);
                        Global.getSoundPlayer().playSound("energy_lash_fire", 1f, 1f, ship.getLocation(), ship.getVelocity());
                    } else {
                        params.flickerRateMult = 0.4f;

                        int numArcs = slots.size();
                        //numArcs = 1;

                        float emp = EMP_DAMAGE;
                        float dam = DAMAGE;

                        for (int i = 0; i < numArcs; i++) {
                            float delay = 0.03f * i;

                            int index = i;
                            ShipAPI ship2 = ship;
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    Vector2f slotLoc = slots.get(index).computePosition(ship2);
                                    Color color = VoltaicDischargeOnFireEffect.EMP_FRINGE_COLOR;
                                    Color core = new Color(255,255,255,255);
                                    if (target.isPhased()) {
                                        color = VoltaicDischargeOnFireEffect.PHASE_FRINGE_COLOR;
                                        core = VoltaicDischargeOnFireEffect.PHASE_CORE_COLOR;
                                    }
                                    //color = Misc.interpolateColor(color, new Color(255,0,255), 0.25f);
                                    EmpArcEntityAPI arc = (EmpArcEntityAPI)engine.spawnEmpArc(ship2, slotLoc, ship2, target,
                                            DamageType.ENERGY,
                                            dam,
                                            emp, // emp
                                            100000f, // max range
                                            "energy_lash_enemy_impact",
                                            60f, // thickness
                                            //new Color(100,165,255,255),
                                            color,
                                            core,
                                            params
                                    );
                                    arc.setCoreWidthOverride(40f);
                                    arc.setSingleFlickerMode(true);
                                }
                            };
                            if (delay <= 0f) {
                                r.run();
                            } else {
                                Global.getCombatEngine().addPlugin(new DelayedCombatActionPlugin(delay, r));
                            }

                            Global.getSoundPlayer().playSound("energy_lash_fire_at_enemy", 1f, 1f, ship.getLocation(), ship.getVelocity());

//						arc.setFadedOutAtStart(true);
//						arc.setRenderGlowAtStart(false);
                        }
                    }

                    applyEffectToTarget(ship, target);
                }
            }
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
            };

            float jitterLevel = getEffectLevel();
            if (state == State.ACTIVE) {
                if (player == true) {
                    Global.getSoundPlayer().playSound("system_phase_skimmer", 1f, 0.15f, ship.getLocation(), ship.getVelocity());
                    Global.getSoundPlayer().playSound("mote_attractor_system_activated", 2f, 0.3f, ship.getLocation(), ship.getVelocity());
                } else {
                    Global.getSoundPlayer().playSound("system_phase_skimmer", 5f, 0.8f, ship.getLocation(), ship.getVelocity());
                    Global.getSoundPlayer().playSound("mote_attractor_system_activated", 6f, 1f, ship.getLocation(), ship.getVelocity());
                }

                Vector2f jumpVector = calculateJumpVector(ship);
                Vector2f targetLocation = Vector2f.add(ship.getLocation(), jumpVector, null);

                // Execute Transfer
                performTeleport(ship, targetLocation);

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
        }

        @Override
        public void onFinished() {
        }

        @Override
        public String getDisplayText() {
            return "Maerulan Phase Skimmer";
        }

        //Calculate the jump direction vector
        private Vector2f calculateJumpVector(ShipAPI ship) {
            Vector2f direction = new Vector2f();

            //Forwards/Backwards
            if (ship.getEngineController().isAccelerating()) {
                direction.y += 1f;
            } else if (ship.getEngineController().isAcceleratingBackwards() || ship.getEngineController().isDecelerating()) {
                direction.y -= 1f;
            }
            //Strafing
            if (ship.getEngineController().isStrafingLeft()) {
                direction.x -= 0.8f;
            } else if (ship.getEngineController().isStrafingRight()) {
                direction.x += 0.8f;
            }

            float tele_distance = 500000 * ((1/(ship.getMassWithModules()+1500)));

            float angle = ship.getFacing();
            direction.x = (float) (direction.x * tele_distance * Math.cos(Math.toRadians(angle)));
            direction.y = (float) (direction.y * tele_distance * Math.sin(Math.toRadians(angle)));

            return direction;
        }

        //Execute the transfer logic
        private void performTeleport(ShipAPI ship, Vector2f target) {
            //Temporarily disable collision detection
            ship.setCollisionClass(CollisionClass.NONE);

            //Calculate Facing
            ShipAPI ship_target = ship.getShipTarget();
            Vector2f mouse_target = ship.getMouseTarget();

            float angle = ship.getFacing();

            //Mouse Direction
            if (mouse_target != null) {
                float xchange = mouse_target.x - ship.getLocation().x;
                float ychange = mouse_target.y - ship.getLocation().y;
                angle = (float) Math.toDegrees(Math.atan2(ychange, xchange));
            }

            //Target Direction (Higher Priority)
            if (ship_target != null) {
                float xchange = ship_target.getLocation().x - ship.getLocation().x;
                float ychange = ship_target.getLocation().y - ship.getLocation().y;
                angle = (float) Math.toDegrees(Math.atan2(ychange, xchange));
            }

            //Set New Location
            ship.getLocation().set(target);
            ship.setFacing(angle);

            //Restore collision detection
            ship.setCollisionClass(CollisionClass.SHIP);
        }
    }
}
*/