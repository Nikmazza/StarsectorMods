package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
//import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import org.magiclib.util.MagicUI;
import java.awt.Color;
import java.util.List;

public class FedAuxShields extends BaseHullMod {

    String moduleStatus;

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        float childFlux = 0;
        float childHardFlux = 1;
        float childOverloadTime = 0;
        float childNumStatus = 0;
        boolean isAuxShieldModuleAlive = false;
        boolean addModuleGlows = false;
        Color barColor = new Color(0, 0, 0, 0);
        Color pipColor = new Color(0, 0, 0, 0);

        if (ship.isShipWithModules()) {
            ship.ensureClonedStationSlotSpec();
            List<ShipAPI> modules = ship.getChildModulesCopy();
            for (ShipAPI childModule : modules) {
                if (childModule.getVariant().hasHullMod("fed_auxshieldeffect")) {
                    isAuxShieldModuleAlive = !childModule.isHulk();
                    childHardFlux = childModule.getFluxTracker().getHardFlux();
                    childFlux = childModule.getFluxTracker().getFluxLevel();
                    childOverloadTime = childModule.getFluxTracker().getOverloadTimeRemaining();
                    //Global.getCombatEngine().addFloatingText(ship.getLocation(), "" + childModule.getFluxLevel(), 15f, Color.WHITE, ship, 1f, 0.5f);
                    childNumStatus = childFlux * childModule.getHullSpec().getFluxCapacity();
                    if (childModule.isHulk() || childModule.getFluxTracker().isOverloadedOrVenting()) {
                        ship.getMutableStats().getHighExplosiveDamageTakenMult().unmodify(ship.getId() + "_auxshield");
                        barColor = Color.RED;
                        pipColor = Color.RED;
                    } else {
                        ship.getMutableStats().getHighExplosiveDamageTakenMult().modifyFlat(ship.getId() + "_auxshield", -0.95f);
                        barColor = childModule.getShield().getInnerColor();
                        pipColor = childModule.getShield().getRingColor();
                        float chanceOfUnderlay = 0.1f + 0.15f * (1f - ship.getFluxLevel());
                        if (Math.random() > chanceOfUnderlay) {
                            addModuleGlows = true;
                            ship.addAfterimage(
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
                    }
                } else if (addModuleGlows){
                    childModule.addAfterimage(
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

        moduleStatus = "SHLD";
        //Color ui = new Color(255, 128, 0);
        if (childOverloadTime > 0f) {
            childNumStatus = childOverloadTime;
            childFlux = childOverloadTime / 40f;
            if (childFlux < 0f) {
                childFlux = 0f;
            }
            if (childFlux > 1f) {
                childFlux = 1f;
            }
            childHardFlux = childFlux;
            moduleStatus = "OVLD";
        }
        if (isAuxShieldModuleAlive) {
            MagicUI.drawInterfaceStatusBar(ship, (childFlux), null, null, (childHardFlux), moduleStatus, (int) childNumStatus);
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {

        if (index == 0) {
            return "5000";
        }
        if (index == 1) {
            return "1.0";
        }
        if (index == 2) {
            return "100";
        }
        return null;
    }
}
