package data.missions.ssp_LLI3;

import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.combat.EscapeRevealPlugin;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {

		// Set up the fleets so we can add ships and fighter wings to them.
		// In this scenario, the fleets are attacking each other, but
		// in other scenarios, a fleet may be defending or trying to escape
		api.initFleet(FleetSide.PLAYER, "LLS", FleetGoal.ATTACK, false, 10);
		api.initFleet(FleetSide.ENEMY, "", FleetGoal.ATTACK, true, 10);

		// Set a small blurb for each fleet that shows up on the mission detail and
		// mission results screens to identify each side.
		api.setFleetTagline(FleetSide.PLAYER, "LouLan Navy");
		api.setFleetTagline(FleetSide.ENEMY, "Pirate");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Defeat all enemy forces.");
		api.addBriefingItem("LLS Blue Fox must survive.");
		api.defeatOnShipLoss("LLS Blue Fox");
		
		
		// Friendly ships

		api.addToFleet(FleetSide.PLAYER, "ssp_lanina_standard", FleetMemberType.SHIP,"LLS Blue Fox", true);
		api.addToFleet(FleetSide.PLAYER, "ssp_wave_standard", FleetMemberType.SHIP,false);
		api.addToFleet(FleetSide.PLAYER, "ssp_wave_standard", FleetMemberType.SHIP,false);		
		api.addToFleet(FleetSide.PLAYER, "ssp_wave_standard", FleetMemberType.SHIP,false);		
		api.addToFleet(FleetSide.PLAYER, "ssp_flame_beam", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "ssp_flame_beam", FleetMemberType.SHIP,false);
		api.addToFleet(FleetSide.PLAYER, "ssp_flame_beam", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "ssp_vortexing_standard", FleetMemberType.SHIP,false);					
		api.addToFleet(FleetSide.PLAYER, "ssp_vortexing_standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "ssp_vortexing_standard", FleetMemberType.SHIP,false);	


		// Enemies ships

		api.addToFleet(FleetSide.ENEMY, "atlas2_Standard", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.ENEMY, "atlas2_Standard", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "colossus3_Pirate", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "manticore_pirates_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "manticore_pirates_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "cerberus_d_pirates_Standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "cerberus_d_pirates_Standard", FleetMemberType.SHIP,false);
		api.addToFleet(FleetSide.ENEMY, "cerberus_d_pirates_Standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "cerberus_d_pirates_Standard", FleetMemberType.SHIP,false);
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Overdriven", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Overdriven", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Overdriven", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Overdriven", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Shielded", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Shielded", FleetMemberType.SHIP,false);
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Shielded", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "hound_d_pirates_Shielded", FleetMemberType.SHIP,false);		
	
		
	

		
				// Set up the map.
		float width = 24000f;
		float height = 18000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		// All the addXXX methods take a pair of coordinates followed by data for
		// whatever object is being added.
		
		// Add two big nebula clouds
		api.addNebula(minX + width * 0.66f, minY + height * 0.5f, 2000);
		api.addNebula(minX + width * 0.25f, minY + height * 0.6f, 1000);
		api.addNebula(minX + width * 0.25f, minY + height * 0.4f, 1000);
		
		// And a few random ones to spice up the playing field.
		for (int i = 0; i < 5; i++) {
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height - height/2;
			float radius = 100f + (float) Math.random() * 400f; 
			api.addNebula(x, y, radius);
		}
		
		// add objectives
		api.addObjective(minX + width * 0.25f + 2000f, minY + height * 0.5f, 
						 "sensor_array");
		api.addObjective(minX + width * 0.75f - 2000f, minY + height * 0.5f,
						 "comm_relay");
		api.addObjective(minX + width * 0.33f + 2000f, minY + height * 0.4f, 
						 "nav_buoy");
		api.addObjective(minX + width * 0.66f - 2000f, minY + height * 0.6f, 
						 "nav_buoy");
		

		api.addAsteroidField(-(minY + height), minY + height, -45, 2000f,
								20f, 70f, 100);
		
		api.addPlanet(0, 0, 400f, "barren", 200f, true);
		api.addRingAsteroids(0,0, 30, 32, 32, 48, 200);
		
		for (int i = 0; i < 15; i++) {
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height - height/2;
			float radius = 100f + (float) Math.random() * 900f; 
			api.addNebula(x, y, radius);
		}
		api.setBackgroundSpriteName("graphics/backgrounds/hyperspace1.jpg");
		//api.setBackgroundSpriteName("graphics/backgrounds/background2.jpg");
		
		//system.setBackgroundTextureFilename("graphics/backgrounds/background2.jpg");
		//api.setBackgroundSpriteName();
		
		// Add an asteroid field going diagonally across the
		// battlefield, 2000 pixels wide, with a maximum of 
		// 100 asteroids in it.
		// 20-70 is the range of asteroid speeds.
		api.addAsteroidField(0f, 0f, (float) Math.random() * 360f, width,
									20f, 70f, 100);
		
		

	}

}
