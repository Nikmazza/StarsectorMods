package Jaydee8652.JaydeePiracy.scripts.skills;

import java.awt.Color;

import Jaydee8652.JaydeePiracy.utils.ReflectionUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.DescriptionSkillEffect;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.population.CoreImmigrationPluginImpl;
import com.fs.starfarer.api.impl.campaign.skills.*;
import com.fs.starfarer.api.util.Misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class jdp_hullmodapplicator {
    public static class Level1 implements ShipSkillEffect {
        public void checkForSupport(MutableShipStatsAPI stats) {
            List<String> wings = stats.getVariant().getWings();
            ShipVariantAPI ship = stats.getVariant();
            boolean hasGroundSupportShips = false;

            for (String wing : wings) {
                if (!wing.isEmpty()) {
                    if (Global.getSettings().getFighterWingSpec(wing).hasTag("jdp_groundSupportFighter")) {
                        hasGroundSupportShips = true;
                        if (!ship.hasHullMod("jdp_supportfighter")) {
                            ship.addMod("jdp_supportfighter");
                        }
                    }
                }
            }
            if (!hasGroundSupportShips) {
                ship.removeMod("jdp_supportfighter");
            }
        }

        public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
            checkForSupport(stats);
        }

        public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
            checkForSupport(stats);
        }

        public String getEffectDescription(float level) {
            return "null";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
}
