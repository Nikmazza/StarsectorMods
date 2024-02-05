package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
//import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import org.magiclib.util.MagicUI;
import java.awt.Color;
import java.util.List;

public class FedFleetShield extends BaseHullMod {

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
    }

    String fleetshield_moduleStatus;

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        float fs_childflux = 0;
        float fs_childHardFlux = 1;
        float fs_childOverloadTime = 0;
        float fs_childNumStatus = 0;
        boolean fs_childAlive = false;
        Color fs_barColor = new Color(0, 0, 0, 0);
        Color fs_pipColor = new Color(0, 0, 0, 0);

        if (ship.isShipWithModules()) {
            ship.ensureClonedStationSlotSpec();
            List<ShipAPI> shield_modules = ship.getChildModulesCopy();
            for (ShipAPI childModule : shield_modules) {
                if (childModule.getVariant().hasHullMod("fed_fleetshieldeffect")) {
                    fs_childHardFlux = childModule.getFluxTracker().getHardFlux();
                    fs_childflux = childModule.getFluxTracker().getFluxLevel();
                    fs_childOverloadTime = childModule.getFluxTracker().getOverloadTimeRemaining();
                    //Global.getCombatEngine().addFloatingText(ship.getLocation(), "" + childModule.getFluxLevel(), 15f, Color.WHITE, ship, 1f, 0.5f);
                    fs_childNumStatus = fs_childflux * childModule.getHullSpec().getFluxCapacity();
                    fs_childAlive = !childModule.isHulk();
                    if (childModule.isHulk() || childModule.getFluxTracker().isOverloadedOrVenting()) {
                        //ship.getMutableStats().getHullDamageTakenMult().unmodify(ship.getId() + "_auxshield");
                        //ship.getMutableStats().getArmorDamageTakenMult().unmodify(ship.getId() + "_auxshield");
                        fs_barColor = Color.RED;
                        fs_pipColor = Color.RED;
                    } else {
                        //ship.getMutableStats().getHighExplosiveDamageTakenMult().modifyFlat(ship.getId() + "_auxshield", -10f);
                        fs_barColor = childModule.getShield().getInnerColor().brighter().brighter().brighter().brighter();
                        fs_pipColor = childModule.getShield().getRingColor().brighter().brighter().brighter().brighter();
                        float chanceOfUnderlay = 0.1f + 0.15f * (1f - ship.getFluxLevel());
                        if (Math.random() > chanceOfUnderlay) {
                            ship.addAfterimage(
                                    new Color(25, 105, 45, 55), //color
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
                    }
                }
            }
        }

        fleetshield_moduleStatus = "SHLD";
        //Color ui = new Color(255, 128, 0);
        if (fs_childAlive) {
            if (fs_childOverloadTime > 0f) {
                fs_childNumStatus = fs_childOverloadTime;
                fs_childflux = fs_childOverloadTime / 40f;
                if (fs_childflux < 0f) {
                    fs_childflux = 0f;
                }
                if (fs_childflux > 1f) {
                    fs_childflux = 1f;
                }
                fs_childHardFlux = fs_childflux;
                fleetshield_moduleStatus = "OVLD";

            } else {
                if (!(ship.getShield() == null)) {
                    if (!(ship.getShield().getType().equals(ShieldAPI.ShieldType.NONE))) {
                        ship.getShield().toggleOff();
                        ship.blockCommandForOneFrame(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK);
                    }   
                }
            }

            MagicUI.drawInterfaceStatusBar(ship, (fs_childflux), null, null, (fs_childHardFlux), fleetshield_moduleStatus, (int) fs_childNumStatus);

        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) {
            return "360-degree bubble shield";
        }
        if (index == 1) {
            return "protect allies";
        }
        if (index == 2) {
            return "50000";
        }
        if (index == 3) {
            return "1.0";
        }
        if (index == 4) {
            return "500";
        }
        if (index == 5) {
            return "50%";
        }
        if (index == 6) {
            return "50%";
        }
        if (index == 7) {
            return "35";
        }
        return null;
    }
}
