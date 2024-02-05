package data.missions.ssp_LLI2;

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
		api.initFleet(FleetSide.PLAYER, "TTS", FleetGoal.ATTACK, false, 10);
		api.initFleet(FleetSide.ENEMY, "LLS", FleetGoal.ATTACK, true, 10);

		// Set a small blurb for each fleet that shows up on the mission detail and
		// mission results screens to identify each side.
		api.setFleetTagline(FleetSide.PLAYER, "Tri-Tachyon Corporation Fleet");
		api.setFleetTagline(FleetSide.ENEMY, "Loulan Armada");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Defeat all enemy forces.");
		
		
		// Friendly ships

		api.addToFleet(FleetSide.PLAYER, "doom_Strike", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.PLAYER, "astral_Attack", FleetMemberType.SHIP,false);
		api.addToFleet(FleetSide.PLAYER, "hyperion_Attack", FleetMemberType.SHIP,false);					
		api.addToFleet(FleetSide.PLAYER, "afflictor_Strike", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "afflictor_Strike", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "omen_PD", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "omen_PD", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "shade_Assault", FleetMemberType.SHIP,false);					
		api.addToFleet(FleetSide.PLAYER, "shade_Assault", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "shade_Assault", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.PLAYER, "shade_Assault", FleetMemberType.SHIP,false);	

		// Enemies ships

		api.addToFleet(FleetSide.ENEMY, "ssp_hanba_standard", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.ENEMY, "ssp_hanba_standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "ssp_wave_M_standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "ssp_wave_M_standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "ssp_wave_M_standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "ssp_wave_standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "ssp_wave_standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "ssp_vortexing_standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "ssp_vortexing_standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "ssp_vortexing_standard", FleetMemberType.SHIP,false);	
		api.addToFleet(FleetSide.ENEMY, "ssp_vortexing_standard", FleetMemberType.SHIP,false);	
		
	

		
		// Set up the map.
		float width = 24000f;
		float height = 18000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
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
