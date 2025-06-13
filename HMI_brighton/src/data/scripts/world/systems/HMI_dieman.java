package data.scripts.world.systems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams;
import com.fs.starfarer.api.util.Misc;

public class HMI_dieman {

	public void generate(SectorAPI sector) {
		
		
		StarSystemAPI system = sector.createStarSystem("Dieman");
		LocationAPI hyper = Global.getSector().getHyperspace();
		
		system.setBackgroundTextureFilename("graphics/backgrounds/background3.jpg");
		
		// create the star and generate the hyperspace anchor for this system
		PlanetAPI star = system.initStar("dieman", // unique id for this star
										    "star_orange",  // id in planets.json
										    575f, 		  // radius (in pixels at default zoom)
										    460, // corona
										    10f, // solar wind burn level
											0.75f, // flare probability
											3.2f); // CR loss multiplier, good values are in the range of 1-5
		
		system.setLightColor(new Color(245, 230, 235)); // light color in entire system, affects all entities
		system.getLocation().set(16000, -2000);

		PlanetAPI dieman1 = system.addPlanet("dieman1", star, "Botany", "rocky_unstable", 200, 50, 1100, 60);
		Misc.initConditionMarket(dieman1);
		dieman1.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
		dieman1.getMarket().addCondition(Conditions.LOW_GRAVITY);
		dieman1.getMarket().addCondition(Conditions.EXTREME_TECTONIC_ACTIVITY);
		dieman1.getMarket().addCondition(Conditions.IRRADIATED);
		dieman1.getMarket().addCondition(Conditions.VERY_HOT);
		dieman1.getMarket().addCondition(Conditions.ORE_SPARSE);

		PlanetAPI dieman2 = system.addPlanet("dieman2", star, "Moreton", "barren3", 120, 65, 2000, 185);
		Misc.initConditionMarket(dieman2);
		dieman2.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
		dieman2.getMarket().addCondition(Conditions.TECTONIC_ACTIVITY);
		dieman2.getMarket().addCondition(Conditions.IRRADIATED);
		dieman2.getMarket().addCondition(Conditions.VERY_HOT);


		PlanetAPI dieman3 = system.addPlanet("dieman3", star, "Brighton", "desert1", 20, 185, 6100, 210);
		dieman3.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "volturn"));
		dieman3.getSpec().setGlowColor(new Color(255, 255, 255, 255));
		dieman3.getSpec().setUseReverseLightForGlow(true);
		dieman3.applySpecChanges();
		dieman3.setCustomDescriptionId("planet_brighton");

		PlanetAPI dieman3a = system.addPlanet("dieman3a", dieman3, "Piers", "barren_castiron", 120, 45, 700, 32);
		Misc.initConditionMarket(dieman3a);
		dieman3a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
		dieman3a.getMarket().addCondition(Conditions.HOT);
		dieman3a.getMarket().addCondition(Conditions.LOW_GRAVITY);

		system.addAsteroidBelt(star, 400, 7800, 512, 550, 600, Terrain.ASTEROID_BELT, "The Belt");
		system.addRingBand(star, "misc", "rings_ice0", 256f, 2, Color.white, 256f, 7800, 550f);

		PlanetAPI dieman4 = system.addPlanet("dieman4", star, "Sullivan", "cryovolcanic", 320, 55, 7800, 550);
		dieman4.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "volturn"));
		dieman4.getSpec().setGlowColor(new Color(100, 100, 255, 255));
		dieman4.getSpec().setUseReverseLightForGlow(true);
		dieman4.applySpecChanges();
		dieman4.setCustomDescriptionId("planet_sullivan");

		SectorEntityToken dieman5_roids = system.addTerrain(Terrain.ASTEROID_FIELD,
				new AsteroidFieldTerrainPlugin.AsteroidFieldParams(
						400f, // min radius
						600f, // max radius
						20, // min asteroid count
						40, // max asteroid count
						4f, // min asteroid radius
						8f, // max asteroid radius
						"Sullivan Cloud")); // null for default name
		dieman5_roids.setCircularOrbit(star, 320, 7800, 550);

		SectorEntityToken dieman5 = system.addCustomEntity("dieman5", "Endeavour Station", "station_side02", "brighton");
		dieman5.setCircularOrbitPointingDown(star, 100, 7600, 550);
		dieman5.setCustomDescriptionId("hmi_endeavour_station_desc");
		dieman5.setInteractionImage("illustrations", "orbital");


		PlanetAPI dieman6 = system.addPlanet("dieman6", star, "Griffith", "barren", 20, 125, 12050, 210);
		dieman6.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "volturn"));
		dieman6.getSpec().setGlowColor(new Color(255, 255, 255, 255));
		dieman6.getSpec().setUseReverseLightForGlow(true);
		dieman6.applySpecChanges();
		dieman6.setCustomDescriptionId("planet_griffith");
		dieman6.setInteractionImage("illustrations", "pirate_station");

		// Relays
		SectorEntityToken dieman_relay = system.addCustomEntity("hazard_relay", // unique id
				"Dieman Relay", // name - if null, defaultName from custom_entities.json will be used
				"comm_relay_makeshift", // type of object, defined in custom_entities.json
				"brighton"); // faction
		dieman_relay.setCircularOrbitPointingDown(star, 300, 4500, 700);

		SectorEntityToken dieman_buoy = system.addCustomEntity("hazard_buoy", // unique id
				"Dieman Nav Buoy", // name - if null, defaultName from custom_entities.json will be used
				"nav_buoy_makeshift", // type of object, defined in custom_entities.json
				"brighton"); // faction
		dieman_buoy.setCircularOrbitPointingDown(star, 120, 4500, 700);

		SectorEntityToken dieman_sensor = system.addCustomEntity("hazard_sensor", // unique id
				"Dieman Sensor Array", // name - if null, defaultName from custom_entities.json will be used
				"sensor_array_makeshift", // type of object, defined in custom_entities.json
				"brighton"); // faction
		dieman_sensor.setCircularOrbitPointingDown(star, 60, 8000, 1000);

		JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("dieman_jump_point1", "Endeavour Jump-point");
		OrbitAPI orbit = Global.getFactory().createCircularOrbit(star, 140, 8200, 550);
		jumpPoint.setOrbit(orbit);
		orbit.setEntity(jumpPoint);
		jumpPoint.setStandardWormholeToHyperspaceVisual();
		jumpPoint.setRelatedPlanet(dieman6);
		system.addEntity(jumpPoint);

		PlanetAPI dieman7 = system.addPlanet("dieman7", star, "Redcliffe", "gas_giant", 110, 700, 18000, 650);
		Misc.initConditionMarket(dieman7);
		dieman7.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
		dieman7.getMarket().addCondition(Conditions.HIGH_GRAVITY);
		dieman7.getMarket().addCondition(Conditions.EXTREME_WEATHER);
		dieman7.getMarket().addCondition(Conditions.VERY_COLD);
		dieman7.getMarket().addCondition(Conditions.POOR_LIGHT);
		dieman7.getMarket().addCondition(Conditions.VOLATILES_DIFFUSE);


		PlanetAPI dieman8 = system.addPlanet("dieman8", star, "Risdon", "ice_giant", 220, 550, 21000, 725);
		Misc.initConditionMarket(dieman8);
		dieman8.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
		dieman8.getMarket().addCondition(Conditions.HIGH_GRAVITY);
		dieman8.getMarket().addCondition(Conditions.EXTREME_WEATHER);
		dieman8.getMarket().addCondition(Conditions.VERY_COLD);
		dieman8.getMarket().addCondition(Conditions.POOR_LIGHT);
		dieman8.getMarket().addCondition(Conditions.VOLATILES_TRACE);

		system.addRingBand(dieman8, "misc", "rings_special0", 256f, 1, new Color(225,215,255,200), 128f, 750, 60f, Terrain.RING, "The Rings of Risdon");

		system.autogenerateHyperspaceJumpPoints(true, true);
	}
}
