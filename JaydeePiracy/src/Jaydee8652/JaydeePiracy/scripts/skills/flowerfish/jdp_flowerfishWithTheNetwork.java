package Jaydee8652.JaydeePiracy.scripts.skills.flowerfish;


import Jaydee8652.JaydeePiracy.hullmods.jdp_mercenaryrelay;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.rpg.Person;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;
import org.magiclib.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class jdp_flowerfishWithTheNetwork extends SCBaseSkillPlugin{
    public static float MANEUVER_BONUS = 25f;
    public static float SPEED_BONUS = 10f;
    public static float RANGE_BONUS = 15f;
    public static float PROJ_BONUS = 20f;

    public static float EFFECT_RANGE = 700f;
    public static float EFFECT_FADE = 500f;

    public static Object STATUS_KEY = new Object();

    private static String jdp_relayIcon = "graphics/icons/campaign/sensor_strength.png";


    public static String JDP_MERCENARY_RELAY_KEY = "jdp_mercenary_relay_key";
    public static class jdp_MercenaryRelayData {
        IntervalUtil interval = new IntervalUtil(0.9f, 1.1f);
        float mag = 0;
    }

    @Override
    public String getAffectsString() {
        return "all ships";
    }

    @Override
    public void addTooltip(SCData scData, TooltipMakerAPI tooltipMakerAPI) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color good = Misc.getPositiveHighlightColor();
        float HEIGHT = 50f;
        float PAD = 10f;

        tooltipMakerAPI.addPara("Gain access to the Mercenary Relay hullmod", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltipMakerAPI.addSpacer(10f);

        TooltipMakerAPI relay = tooltipMakerAPI.beginImageWithText(jdp_relayIcon, HEIGHT);
        relay.addPara("Mercenary Relay", 0f, h, "Mercenary Relay");
        relay.addPara("When the \"With The Network\" skill is enabled the following bonuses are applied to allied ships larger than frigates within approximately 1000 su.", 0f, h, "\"With The Network\"", "1000");
        relay.addPara(" -Increases ship maneuverability by %s", 0f, h, "+" + (int)Math.round(MANEUVER_BONUS) + "%");
        relay.addPara(" -Increases ship max speed by %s", 0f, h, "+" + (int)Math.round(SPEED_BONUS) + "%");
        relay.addPara(" -Increases weapon range by %s", 0f, h, "+" + (int)Math.round(RANGE_BONUS) + "%");
        relay.addPara(" -Increases projectile speed by %s", 0f, h, "+" + (int)Math.round(PROJ_BONUS) + "%");
        tooltipMakerAPI.addImageWithText(PAD);
        tooltipMakerAPI.addSpacer(10f);

        tooltipMakerAPI.addPara("\"Acknowledged command, telemetry received.\"", 0f, Misc.getTextColor(), Misc.getHighlightColor());
        tooltipMakerAPI.addPara("  -Flowerfish Third Fuki", 0f, jdp_AptitudeFlowerfish.JDP_FLOWERFISH_COLOUR, Misc.getHighlightColor());
        tooltipMakerAPI.addSpacer(10f);
    }

    public void applyEPEffect(ShipAPI ship, ShipAPI other, float mag) {
        String id = "jdp_mercenary_relay_bonus" + ship.getId();
        MutableShipStatsAPI stats = ship.getMutableStats();

        if (mag > 0) {

            float maneuver = MANEUVER_BONUS * mag;
            stats.getAcceleration().modifyPercent(id, maneuver);
            stats.getDeceleration().modifyPercent(id, maneuver);
            stats.getTurnAcceleration().modifyPercent(id, maneuver * 2f);
            stats.getMaxTurnRate().modifyPercent(id, maneuver);

            float speed = SPEED_BONUS * mag;
            stats.getMaxSpeed().modifyPercent(id, speed);

            float range = RANGE_BONUS * mag;
            stats.getBallisticWeaponRangeBonus().modifyPercent(id, range);
            stats.getEnergyWeaponRangeBonus().modifyPercent(id, range);

            float proj = PROJ_BONUS * mag;
            stats.getProjectileSpeedMult().modifyPercent(id, proj);

        } else {
            stats.getAcceleration().unmodify(id);
            stats.getDeceleration().unmodify(id);
            stats.getTurnAcceleration().unmodify(id);
            stats.getMaxTurnRate().unmodify(id);

            stats.getMaxSpeed().unmodify(id);

            stats.getBallisticWeaponRangeBonus().unmodify(id);
            stats.getEnergyWeaponRangeBonus().unmodify(id);

            stats.getShieldArcBonus().unmodify(id);

            stats.getProjectileSpeedMult().unmodify(id);
        }

    }

    @Override
    public void advanceInCombat(SCData data, ShipAPI ship, Float amount) {
        super.advanceInCombat(data, ship, amount);

        if(!ship.isAlive()) return;
        if(amount <= 0f) return;
        if(ship.isFrigate()) return;

        CombatEngineAPI engine = Global.getCombatEngine();

        String key = JDP_MERCENARY_RELAY_KEY + "_" + ship.getId();
        jdp_flowerfishWithTheNetwork.jdp_MercenaryRelayData relay_data = (jdp_flowerfishWithTheNetwork.jdp_MercenaryRelayData) engine.getCustomData().get(key);
        if (relay_data == null) {
            relay_data = new jdp_flowerfishWithTheNetwork.jdp_MercenaryRelayData();
            engine.getCustomData().put(key, relay_data);
        }

        boolean playerShip = ship == Global.getCombatEngine().getPlayerShip();

        relay_data.interval.advance(amount * 4f);
        if (relay_data.interval.intervalElapsed() || playerShip) {
            float checkSize = EFFECT_RANGE + EFFECT_FADE + ship.getCollisionRadius() + 300f;
            checkSize *= 2f;

            Iterator<Object> iter = Global.getCombatEngine().getShipGrid().getCheckIterator(
                    ship.getLocation(), checkSize, checkSize);

            ShipAPI best = null;
            float bestMag = 0f;
            while (iter.hasNext()) {
                Object next = iter.next();
                if (!(next instanceof ShipAPI)) continue;

                ShipAPI other = (ShipAPI) next;

                if (ship == other) continue;
                if (other.getOwner() != ship.getOwner()) continue;
                if (other.isHulk()) continue;

                if (!other.getVariant().hasHullMod("jdp_mercenaryrelay")) continue;

                float radSum = ship.getShieldRadiusEvenIfNoShield() + other.getShieldRadiusEvenIfNoShield();
                radSum *= 0.75f;
                float dist = Misc.getDistance(ship.getShieldCenterEvenIfNoShield(), other.getShieldCenterEvenIfNoShield());
                dist -= radSum;

                float mag = 0f;
                if (dist < EFFECT_RANGE) {
                    mag = 1f;
                } else if (dist < EFFECT_RANGE + EFFECT_FADE) {
                    mag = 1f - (dist - EFFECT_RANGE) / EFFECT_FADE;
                }

                if (mag > bestMag) {
                    best = other;
                    bestMag = mag;
                }
            }

            applyEPEffect(ship, best, bestMag);

            relay_data.mag = bestMag;
        }

        if (playerShip) {
            if (relay_data.mag > 0.005f) {
                String icon = Global.getSettings().getSpriteName("ui", "icon_tactical_escort_package");
                String percent = "" + (int) Math.round(relay_data.mag * 100f) + "%";
                Global.getCombatEngine().maintainStatusForPlayerShip(
                        STATUS_KEY, icon, "Mercenary relay", percent + " telemetry quality", false);
            } else {
                String icon = Global.getSettings().getSpriteName("ui", "icon_tactical_escort_package");
                Global.getCombatEngine().maintainStatusForPlayerShip(
                        STATUS_KEY, icon, "Mercenary relay", "no connection", true);
            }
        }

    }

    @Override
    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) {}

    @Override
    public void onActivation(SCData data) {
        data.getCommander().addTag("jdp_flowerfishWithTheNetwork");

        if (data.isPlayer()) {
            CharacterDataAPI player = Global.getSector().getCharacterData();
            player.addHullMod("jdp_mercenaryrelay");
        }
    }

    @Override
    public void onDeactivation(SCData data) {
        data.getCommander().removeTag("jdp_flowerfishWithTheNetwork");

    }
}
