package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import org.lwjgl.util.vector.Vector2f;

public class FedMissileForge extends BaseHullMod {

    public static String FED_MR_DATA_KEY = "fed_reload_data_key";

    public static class FedMissileForgeData {

        IntervalUtil interval = new IntervalUtil(45f, 45f);
        IntervalUtil intervalLargeWarhead = new IntervalUtil(60f, 60f);
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) {
            return "one burst";
        }
        if (index == 1) {
            return "20% of base ammo";
        }
        if (index == 2) {
            return "500";
        }
        if (index == 3) {
            return "45 seconds";
        }
        if (index == 4) {
            return "501 to 900";
        }
        if (index == 5) {
            return "60 seconds";
        }
        return null;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        super.advanceInCombat(ship, amount);

        if (!ship.isAlive()) {
            return;
        }

        CombatEngineAPI engine = Global.getCombatEngine();

        String key = FED_MR_DATA_KEY + "_" + ship.getId();
        FedMissileForgeData data = (FedMissileForgeData) engine.getCustomData().get(key);
        if (data == null) {
            data = new FedMissileForgeData();
            engine.getCustomData().put(key, data);
        }

        boolean isSmallFull = true;
        boolean isLargeFull = true;

        for (WeaponAPI wep : ship.getAllWeapons()) {
            if (wep.getType() != WeaponType.MISSILE && wep.getDerivedStats().getDamagePerShot() > 901) {
                continue;
            }
            if (wep.getDerivedStats().getDamagePerShot() <= 900 && wep.getDerivedStats().getDamagePerShot() > 500) {
                if (wep.usesAmmo() && wep.getAmmo() < wep.getMaxAmmo()) {
                    isLargeFull = false;
                }
            }
            if (wep.getDerivedStats().getDamagePerShot() <= 500) {
                if (wep.usesAmmo() && wep.getAmmo() < wep.getMaxAmmo()) {
                    isSmallFull = false;
                }
            }

        }

        if (!isSmallFull) {
            data.interval.advance(amount);
        }

        if (!isLargeFull) {
            data.intervalLargeWarhead.advance(amount);
        }

        int smallReloadTime = 45 - (int) data.interval.getElapsed();
        int largeReloadTime = 60 - (int) data.intervalLargeWarhead.getElapsed();

        if (ship == Global.getCombatEngine().getPlayerShip()) {
            engine.maintainStatusForPlayerShip(ship.toString() + "_player_smallmissiles", "fed_icon_missilereload_small.png", "FORGING MISSILES", "0-500 dmg in " + smallReloadTime, false);
            engine.maintainStatusForPlayerShip(ship.toString() + "_player_largemissiles", "fed_icon_missilereload_large.png", "FORGING MISSILES", "500-900 dmg in " + largeReloadTime, false);
        }

        if (data.interval.intervalElapsed()) {
            for (WeaponAPI w : ship.getAllWeapons()) {
                if (w.getType() != WeaponType.MISSILE || w.getDerivedStats().getDamagePerShot() > 500) {
                    continue;
                }

                int burstSize = w.getSpec().getBurstSize();
                if (!(burstSize >= 1)) {
                    burstSize = 1;
                }

                if (burstSize <= w.getSpec().getMaxAmmo() / 5) {
                    burstSize = w.getSpec().getMaxAmmo() / 5;
                }
                if (w.usesAmmo() && w.getAmmo() + burstSize <= w.getMaxAmmo()) {
                    w.setAmmo(w.getAmmo() + burstSize);
                    Global.getSoundPlayer().playSound("fed_missilereload_verysmall", 1f, 0.75f, ship.getLocation(), ship.getVelocity());
                    data.interval.advance(amount);
                } else {
                    w.setAmmo(w.getMaxAmmo());
                    Global.getSoundPlayer().playSound("fed_missilereload_verysmall", 1f, 0.75f, ship.getLocation(), ship.getVelocity());
                    data.interval.advance(amount);
                }

            }
        }
        if (data.intervalLargeWarhead.intervalElapsed()) {
            for (WeaponAPI w : ship.getAllWeapons()) {

                if (w.getType() != WeaponType.MISSILE || w.getDerivedStats().getDamagePerShot() > 900 || w.getDerivedStats().getDamagePerShot() < 501) {
                    continue;
                }

                int burstSize = w.getSpec().getBurstSize();
                if (!(burstSize >= 1)) {
                    burstSize = 1;
                }

                if (burstSize <= w.getSpec().getMaxAmmo() / 5) {
                    burstSize = w.getSpec().getMaxAmmo() / 5;
                }
                if (w.usesAmmo() && w.getAmmo() + burstSize <= w.getMaxAmmo()) {
                    w.setAmmo(w.getAmmo() + burstSize);
                    Global.getSoundPlayer().playSound("fed_missilereload_small", 1f, 0.75f, ship.getLocation(), ship.getVelocity());
                    data.intervalLargeWarhead.advance(amount);
                } else {
                    w.setAmmo(w.getMaxAmmo());
                    Global.getSoundPlayer().playSound("fed_missilereload_small", 1f, 0.75f, ship.getLocation(), ship.getVelocity());
                    data.intervalLargeWarhead.advance(amount);
                }

            }

        }
    }

}
