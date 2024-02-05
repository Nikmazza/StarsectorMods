package data.missions.ssp_LLI1;

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
		api.initFleet(FleetSide.ENEMY, "TTS", FleetGoal.ATTACK, true, 10);

		// Set a small blurb for each fleet that shows up on the mission detail and
		// mission results screens to identify each side.
		api.setFleetTagline(FleetSide.PLAYER, "Loulan Navy");
		api.setFleetTagline(FleetSide.ENEMY, "Noobs");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("LLI Southwind must survive.");
		api.addBriefingItem("Defeat all enemy forces.");
		
		api.defeatOnShipLoss("LLI SouthWind");
		
		// Friendly ships

		api.addToFleet(FleetSide.PLAYER, "ssp_elnino_standard", FleetMemberType.SHIP, "LLS SouthWind",true);
		api.addToFleet(FleetSide.PLAYER, "ssp_wasteland_standard", FleetMemberType.SHIP,false);
		api.addToFleet(FleetSide.PLAYER, "ssp_wasteland_standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "ssp_combustion_standard", FleetMemberType.SHIP,false);					
		api.addToFleet(FleetSide.PLAYER, "ssp_combustion_standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "ssp_combustion_standard", FleetMemberType.SHIP,false);	
		// Enemies ships

		api.addToFleet(FleetSide.ENEMY, "paragon_Elite", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.ENEMY, "falcon_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "falcon_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_Assault", FleetMemberType.SHIP, false);
		
	

		
		// Set up the map.
		float width = 18000f;
		float height = 18000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;

	}

}
