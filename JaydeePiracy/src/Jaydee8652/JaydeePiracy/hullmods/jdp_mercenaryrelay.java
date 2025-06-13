package Jaydee8652.JaydeePiracy.hullmods;
import java.util.Iterator;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class jdp_mercenaryrelay extends BaseHullMod {

    public static float MANEUVER_BONUS = 25f;
    public static float SPEED_BONUS = 10f;
    public static float RANGE_BONUS = 20f;
    public static float SHIELD_BONUS = 10f;
    public static float FLUX_BONUS = 20f;

    public static float EFFECT_RANGE = 1700f;
    public static float EFFECT_FADE = 1500f;

    public static Object STATUS_KEY = new Object();


    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "2000";
        if (index == 1) return "" + (int)Math.round(MANEUVER_BONUS) + "%";
        if (index == 2) return "" + (int)Math.round(SPEED_BONUS) + "%";
        if (index == 3) return "" + (int)Math.round(RANGE_BONUS) + "%";
        if (index == 4) return "" + (int)Math.round(FLUX_BONUS) + "%";
        if (index == 5) return "" + (int)Math.round(SHIELD_BONUS) + "%";

        if (index == 6) return "doubled";
        return null;
    }

    public String getSModDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int)Math.round(SHIELD_BONUS) + "%";
        return null;
    }


    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return getUnapplicableReason(ship) == null;
    }

    public static String JDP_MERCENARY_RELAY_KEY = "jdp_mercenary_relay_key";
    public static class jdp_MercenaryRelayData {
        IntervalUtil interval = new IntervalUtil(0.9f, 1.1f);
        float mag = 0;
    }

    public void applyEPEffect(ShipAPI ship, ShipAPI other, float mag) {
        String id = "jdp_mercenary_relay_bonus" + ship.getId();
        MutableShipStatsAPI stats = ship.getMutableStats();

        if (mag > 0) {
            boolean sMod = isSMod(ship);

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

            float flux = FLUX_BONUS * mag;
            stats.getBallisticWeaponFluxCostMod().modifyPercent(id, 1f - flux / 100f);
            stats.getBallisticWeaponFluxCostMod().modifyPercent(id, 1f - flux / 100f);

            float shields = SHIELD_BONUS * mag;
            stats.getShieldDamageTakenMult().modifyMult(id, 1f - shields / 100f);

        } else {
            stats.getAcceleration().unmodify(id);
            stats.getDeceleration().unmodify(id);
            stats.getTurnAcceleration().unmodify(id);
            stats.getMaxTurnRate().unmodify(id);

            stats.getMaxSpeed().unmodify(id);

            stats.getBallisticWeaponRangeBonus().unmodify(id);
            stats.getEnergyWeaponRangeBonus().unmodify(id);

            stats.getShieldDamageTakenMult().unmodify(id);
        }

    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        super.advanceInCombat(ship, amount);

        if (!ship.isAlive()) return;
        if (amount <= 0f) return;

        CombatEngineAPI engine = Global.getCombatEngine();

        String key = JDP_MERCENARY_RELAY_KEY + "_" + ship.getId();
        jdp_MercenaryRelayData data = (jdp_MercenaryRelayData) engine.getCustomData().get(key);
        if (data == null) {
            data = new jdp_MercenaryRelayData();
            engine.getCustomData().put(key, data);
        }

        boolean playerShip = ship == Global.getCombatEngine().getPlayerShip();

        data.interval.advance(amount * 4f);
        if (data.interval.intervalElapsed() || playerShip) {
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

                if (other.isFrigate()) {
                    mag *= 2f;
                }

                if (ship.isFrigate()) {
                    mag *= 0f;
                }

                if (mag > bestMag) {
                    best = other;
                    bestMag = mag;
                }
            }

            //if (best != null && bestMag > 0) {
            applyEPEffect(ship, best, bestMag);
            //}

            data.mag = bestMag;
        }

        if (playerShip) {
            if (data.mag > 0.005f) {
                String icon = Global.getSettings().getSpriteName("ui", "icon_tactical_escort_package");
                String percent = "" + (int) Math.round(data.mag * 100f) + "%";
                Global.getCombatEngine().maintainStatusForPlayerShip(
                        STATUS_KEY, icon, "Mercenary relay", percent + " telemetry quality", false);
            } else {
                String icon = Global.getSettings().getSpriteName("ui", "icon_tactical_escort_package");
                Global.getCombatEngine().maintainStatusForPlayerShip(
                        STATUS_KEY, icon, "Mercenary relay", "no connection", true);
            }
        }

    }

}
