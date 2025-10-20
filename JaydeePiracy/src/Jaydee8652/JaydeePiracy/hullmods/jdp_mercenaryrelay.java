package Jaydee8652.JaydeePiracy.hullmods;
import java.awt.*;
import java.util.List;

import Jaydee8652.JaydeePiracy.utils.ReflectionUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import static Jaydee8652.JaydeePiracy.scripts.skills.flowerfish.jdp_flowerfishWithTheNetwork.*;

public class jdp_mercenaryrelay extends BaseHullMod {
    public static float VISION_BONUS = 2000f;


    private static String jdp_relayIcon = "graphics/icons/campaign/sensor_strength.png";


    public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        if (index == 0) return "" + (int)Math.round(VISION_BONUS);
        return null;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color good = Misc.getPositiveHighlightColor();
        float HEIGHT = 50f;
        float PAD = 10f;

        if (Global.getSettings().getModManager().isModEnabled("second_in_command")) {
            TooltipMakerAPI relay = tooltip.beginImageWithText(jdp_relayIcon, HEIGHT);
            relay.addPara("Relay Network", 0f, h, "Relay Network");
            relay.addPara("When the \"With The Network\" skill is enabled the following bonuses are applied to allied ships larger than frigates within approximately 1000 su.", 0f, h, "\"With The Network\"", "1000");
            relay.addPara(" -Increases ship maneuverability by %s", 0f, h, "+" + (int)Math.round(MANEUVER_BONUS) + "%");
            relay.addPara(" -Increases ship max speed by %s", 0f, h, "+" + (int)Math.round(SPEED_BONUS) + "%");
            relay.addPara(" -Increases weapon range by %s", 0f, h, "+" + (int)Math.round(RANGE_BONUS) + "%");
            relay.addPara(" -Increases projectile speed by %s", 0f, h, "+" + (int)Math.round(PROJ_BONUS) + "%");
            tooltip.addImageWithText(PAD);

            if (ship == null) return;
            if (ship.getFleetCommander() == null) return;

            if (!ship.getFleetCommander().hasTag("jdp_flowerfishWithTheNetwork")) {
                tooltip.addSectionHeading("Bonuses Inactive", Alignment.MID, opad);
                tooltip.addPara("\"With The Network\" is not assigned to the Fleet Commander of this ship, Relay Network bonuses are inactive.", opad, bad, bad, "\"With The Network\" is not assigned to the Fleet Commander of this ship, Relay Network bonuses are inactive.");
            } else {
                tooltip.addSectionHeading("Bonuses Active", Alignment.MID, opad);
                tooltip.addPara("\"With The Network\" is assigned to the Fleet Commander of this ship, Relay Network bonuses are active.", opad, good, good, "\"With The Network\" is assigned to the Fleet Commander of this ship, Relay Network bonuses are active.");
            }
        }
    }


    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        /*if (ship != null && ship.isStationModule()) {
            return false;
        }
        if (ship != null && ship.getVariant().getHullMods().contains("jdp_modularship")) {
            return false;
        }
        if (ship != null && !ship.isFrigate()){
            return false;
        }*/
        return true;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getVariant().getHullMods().contains("jdp_modularship")) {
            return "Incompatible, cannot be applied to modular ships.";
        }
        if (ship.isStationModule()){
            return "Incompatible, cannot be applied to modular ships.";
        }
        if (ship != null && ship.isCapital()) {
            return "Can not be installed on capital ships";
        }
        if (ship != null && ship.isCruiser()) {
            return "Can not be installed on cruisers";
        }
        if (ship != null && ship.isDestroyer()) {
            return "Can not be installed on destroyers";
        }
        return null;
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //Generic bonus to hide its purpose without SiC
        stats.getSightRadiusMod().modifyFlat(id, VISION_BONUS);

        //Adds a helper hullmod who actually does all the heavy lifting
        if (Global.getSettings().getModManager().isModEnabled("second_in_command")) {
            if (!stats.getVariant().hasHullMod("jdp_mercenaryrelayhelper")) {
                stats.getVariant().addMod("jdp_mercenaryrelayhelper"); // don't add as a perma mod
            }
        }
    }
}
