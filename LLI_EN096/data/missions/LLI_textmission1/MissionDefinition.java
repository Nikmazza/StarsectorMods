package data.missions.LLI_textmission1;

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
		api.initFleet(FleetSide.PLAYER, "", FleetGoal.ATTACK, false, 10);
		api.initFleet(FleetSide.ENEMY, "", FleetGoal.ATTACK, true, 10);

		// Set a small blurb for each fleet that shows up on the mission detail and
		// mission results screens to identify each side.
		api.setFleetTagline(FleetSide.PLAYER, "");
		api.setFleetTagline(FleetSide.ENEMY, "");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("");
		api.addBriefingItem("");
		
		// Friendly ships
		api.addToFleet(FleetSide.PLAYER, "ssp_elnino_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_tronado_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_karni_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_hanba_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_flood_standard", FleetMemberType.SHIP,true);
		
		
		api.addToFleet(FleetSide.PLAYER, "ssp_undergroundfire_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_seaquake_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_tide_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_tide_T_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_wasteland_standard", FleetMemberType.SHIP,true);		
		api.addToFleet(FleetSide.PLAYER, "ssp_lanina_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_thunder_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_TDS_standard", FleetMemberType.SHIP,true);
		
		
		api.addToFleet(FleetSide.PLAYER, "ssp_conduit_standard", FleetMemberType.SHIP,true);		
		api.addToFleet(FleetSide.PLAYER, "ssp_yu_lian_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_flame_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_turbulence_standard", FleetMemberType.SHIP,true);		
		api.addToFleet(FleetSide.PLAYER, "ssp_wave_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_wave_M_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_vocaino_standard", FleetMemberType.SHIP,true);

		api.addToFleet(FleetSide.PLAYER, "ssp_vortexing_standard", FleetMemberType.SHIP,true);		
		api.addToFleet(FleetSide.PLAYER, "ssp_combustion_standard", FleetMemberType.SHIP,true);	
		api.addToFleet(FleetSide.PLAYER, "ssp_river_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_dew_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_gonggong_standard", FleetMemberType.SHIP,true);
		api.addToFleet(FleetSide.PLAYER, "ssp_zhurong_standard", FleetMemberType.SHIP,true);

		// Enemies ships
		api.addToFleet(FleetSide.ENEMY, "hammerhead_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hammerhead_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "wolf_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "falcon_Attack", FleetMemberType.SHIP, false);
		
	

		
		// Set up the map.
		float width = 10000f;
		float height = 10000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;

		
	}

}
