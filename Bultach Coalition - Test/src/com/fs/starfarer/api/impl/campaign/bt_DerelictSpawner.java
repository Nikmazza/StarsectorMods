package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class bt_DerelictSpawner {
    private static Logger log = Global.getLogger(bt_DerelictSpawner.class);

    public bt_DerelictSpawner() {
    }

    public static void spawnDerelicts() {
        List<PlanetAPI> gudeplanets = new ArrayList<PlanetAPI>();
        Iterator stariter = Global.getSector().getStarSystems().iterator();
        while (stariter.hasNext()) {
            StarSystemAPI star = (StarSystemAPI) stariter.next();
            if (star.isProcgen()) {
                Iterator planetiter = star.getPlanets().iterator();
                while (planetiter.hasNext()) {
                    PlanetAPI planet = (PlanetAPI) planetiter.next();
                    Float prob = MathUtils.getRandomNumberInRange(0f, 100f);
                    if (prob > 90 && (planet.hasCondition(Conditions.RUINS_VAST) || planet.hasCondition(Conditions.RUINS_EXTENSIVE) || planet.hasCondition(Conditions.RUINS_WIDESPREAD) || planet.hasCondition(Conditions.RUINS_SCATTERED))) {
                        gudeplanets.add(planet);
                    }
                }
            }
        }

        List<String> variantsToSpawn = new ArrayList<String>();
        variantsToSpawn.add("ork_anathema_standard");
        variantsToSpawn.add("ork_triumph_standard");
        variantsToSpawn.add("ork_triumph_standard");
        variantsToSpawn.add("ork_triumph_standard");
        variantsToSpawn.add("ork_despot_standard");
        variantsToSpawn.add("ork_despot_standard");
        variantsToSpawn.add("ork_apsis_standard");
        variantsToSpawn.add("ork_apsis_standard");
        variantsToSpawn.add("ork_apsis_standard");

        if (gudeplanets.isEmpty()) {
            log.warn("No suitable planets found for derelict spawning.");
            return;
        }

        Collections.shuffle(gudeplanets, MathUtils.getRandom());

        int planetIndex = 0;
        for (String variantIdToSpawn : variantsToSpawn) {
            if (planetIndex >= gudeplanets.size()) {
                log.warn("Not enough suitable planets to spawn all listed derelicts. Attempted to spawn " + variantsToSpawn.size() + " ships, but only " + gudeplanets.size() + " suitable planets were found.");
                break;
            }
            PlanetAPI chosenPlanet = gudeplanets.get(planetIndex);

            ShipVariantAPI shipVariant = Global.getSettings().getVariant(variantIdToSpawn);
            if (shipVariant == null) {
                log.error("Could not find variant: " + variantIdToSpawn + ". Skipping this derelict.");
                continue;
            }
            String hullIdForTypeField = shipVariant.getHullSpec().getHullId();
            String hullForLogging = shipVariant.getHullVariantId();


            Float orbitRadius = chosenPlanet.getRadius() + MathUtils.getRandomNumberInRange(50f, 300f);
            FactionAPI faction = Global.getSector().getFaction("neutral");
            String name = faction.pickRandomShipName();

            TTBlackSite.addDerelict(
                    chosenPlanet.getStarSystem(),
                    chosenPlanet,
                    variantIdToSpawn,
                    name,
                    hullIdForTypeField,
                    ShipRecoverySpecial.ShipCondition.AVERAGE,
                    orbitRadius,
                    true
            );
            log.info("Generated a " + hullForLogging + " (" + variantIdToSpawn + ") in orbit of " + chosenPlanet.getName() + " in the " + chosenPlanet.getStarSystem().getName() + " system.");

            planetIndex++;
        }
    }
}