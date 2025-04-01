package data.missions.LLI_M1_DumperAndPhase;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.BattleObjectives;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {

		// Set up the fleets so we can add ships and fighter wings to them.
		// In this scenario, the fleets are attacking each other, but
		// in other scenarios, a fleet may be defending or trying to escape
		api.initFleet(FleetSide.PLAYER, "", FleetGoal.ATTACK, false);
		api.initFleet(FleetSide.ENEMY, "", FleetGoal.ATTACK, true);

//		api.getDefaultCommander(FleetSide.PLAYER).getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 3);
//		api.getDefaultCommander(FleetSide.PLAYER).getStats().setSkillLevel(Skills.ELECTRONIC_WARFARE, 3);
		
		// Set a small blurb for each fleet that shows up on the mission detail and
		// mission results screens to identify each side.
		api.setFleetTagline(FleetSide.PLAYER, "");
		api.setFleetTagline(FleetSide.ENEMY, "");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("");
		
		boolean testMode = false;
		// Set up the player's fleet.  Variant names come from the
		// files in data/variants and data/variants/fighters
		//api.addToFleet(FleetSide.PLAYER, "station_small_Standard", FleetMemberType.SHIP, "Test Station", false);
		
			api.addToFleet(FleetSide.PLAYER, "atlas2_Standard", FleetMemberType.SHIP,  true);
			api.addToFleet(FleetSide.PLAYER, "atlas2_Standard", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "colossus3_Pirate", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "colossus3_Pirate", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "colossus3_Pirate", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "colossus3_Pirate", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "manticore_pirates_Assault", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "manticore_pirates_Assault", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "vanguard_pirates_Strike", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "vanguard_pirates_Strike", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "vanguard_pirates_Strike", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "shade_d_pirates_Assault", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "shade_d_pirates_Assault", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "hound_d_pirates_Overdriven", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "hound_d_pirates_Overdriven", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "hound_d_pirates_Overdriven", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "hound_d_pirates_Overdriven", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.PLAYER, "hound_d_pirates_Overdriven", FleetMemberType.SHIP, false);
			

			
			// Set up the enemy fleet.
			api.addToFleet(FleetSide.ENEMY, "ssp_karni_M1", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "ssp_thunder_M1", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "ssp_thunder_M1", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "ssp_midkarni_M1", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "ssp_midkarni_M1", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "ssp_conduit_M1", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "ssp_Estatics_M1", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "ssp_Estatics_M1", FleetMemberType.SHIP, false);
			api.addToFleet(FleetSide.ENEMY, "ssp_Estatics_M1", FleetMemberType.SHIP, false);
		
		
		
		// Set up the map.
		float width = 20000f;
		float height = 20000f;
		
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		api.addObjective(minX + width * 0.8f - 1000, minY-1000+height*0.4f, "sensor_array");
		api.addObjective(minX + width * 0.8f - 1000, minY+1000+height*0.6f, "sensor_array");
		api.addObjective(minX + width * 0.3f - 1000, minY + height * 0.5f, "comm_relay");
		api.addObjective(minX + width * 0.5f + 1000, minY + height * 0.5f, "nav_buoy");
		
		// Add an asteroid field
		api.addAsteroidField(minX, minY + height / 2, 0, 8000f,
							 20f, 70f, 100);
		
		api.addPlanet(0, 0, 50f, StarTypes.RED_GIANT, 250f, true);
		
	}

}
