package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BT_Sathar_Swapper extends BaseHullMod {

    public static final String WEAPON_SLOT = "WS0001";
    public static final String WEAPON_SLOT_2 = "WS0002";
    public static final String WEAPON_PREFIX = "ork_sathar_";

    // points to the next weapon/hullmod suffix
    public static final Map<String, String> LOADOUT_CYCLE = new HashMap<>();

    static {
        LOADOUT_CYCLE.put("siege_laser", "gigashotgun");
        LOADOUT_CYCLE.put("gigashotgun", "emp_nuke");
        LOADOUT_CYCLE.put("emp_nuke", "doom_laser");
        LOADOUT_CYCLE.put("doom_laser", "siege_laser");
    }

    // Array for random weapon selection
    public static final String[] WEAPON_POOL = { "siege_laser", "gigashotgun", "emp_nuke", "doom_laser" };

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

        if (stats.getEntity() == null)
            return;

        // trigger a weapon switch if none of the selector hullmods are present
        boolean switchLoadout = true;
        for (String hullmod : LOADOUT_CYCLE.values()) {
            if (stats.getVariant().getHullMods().contains("bt_sathar_mode_" + hullmod)) {
                switchLoadout = false;
                break;
            }
        }

        if (switchLoadout) {
            // Randomly choose a new weapon from the pool
            String newWeapon = getRandomWeapon();

            // Add corresponding hullmod to match the new weapon
            stats.getVariant().addMod("bt_sathar_mode_" + newWeapon);

            // Clear slots and assign the new weapon
            stats.getVariant().clearSlot(WEAPON_SLOT);
            stats.getVariant().clearSlot(WEAPON_SLOT_2);
            stats.getVariant().addWeapon(WEAPON_SLOT, WEAPON_PREFIX + newWeapon);
            stats.getVariant().addWeapon(WEAPON_SLOT_2, WEAPON_PREFIX + newWeapon);

        } else if (stats.getVariant().getWeaponId(WEAPON_SLOT) == null) {
            // If no weapon is assigned, choose a random weapon
            String newWeapon = getRandomWeapon();

            // Assign random weapon
            stats.getVariant().addWeapon(WEAPON_SLOT, WEAPON_PREFIX + newWeapon);
            stats.getVariant().addWeapon(WEAPON_SLOT_2, WEAPON_PREFIX + newWeapon);
        }
    }

    // Method to randomly select a weapon from the pool
    private String getRandomWeapon() {
        Random rand = new Random();
        return WEAPON_POOL[rand.nextInt(WEAPON_POOL.length)];
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getOriginalOwner() < 0) {
            // Undo fix for weapons put in cargo
            if (Global.getSector() != null &&
                    Global.getSector().getPlayerFleet() != null &&
                    Global.getSector().getPlayerFleet().getCargo() != null &&
                    Global.getSector().getPlayerFleet().getCargo().getStacksCopy() != null &&
                    !Global.getSector().getPlayerFleet().getCargo().getStacksCopy().isEmpty()) {

                for (CargoStackAPI s : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
                    if (s.isWeaponStack() && s.getWeaponSpecIfWeapon().getWeaponId().startsWith(WEAPON_PREFIX)) {
                        Global.getSector().getPlayerFleet().getCargo().removeStack(s);
                    }
                }
            }
        }
    }

    @Override
    public int getDisplayCategoryIndex() {
        return 2;
    }
}
