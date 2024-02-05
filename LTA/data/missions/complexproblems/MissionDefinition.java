package data.missions.complexproblems;

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
		api.setFleetTagline(FleetSide.PLAYER, "'Complex Solution'");
		api.setFleetTagline(FleetSide.ENEMY, "'Complex Problem'");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Complex Problems require Complex Solutions");
		api.addBriefingItem("Destroy the opposing forces");
		//api.addBriefingItem("");
		
		boolean testMode = false;
		// Set up the player's fleet.  Variant names come from the
		// files in data/variants and data/variants/fighters
		//api.addToFleet(FleetSide.PLAYER, "station_small_Standard", FleetMemberType.SHIP, "Test Station", false);
		if (!testMode) {
			api.addToFleet(FleetSide.PLAYER, "LTA_Hammerhead_Thunderbirds_Hunterkiller", FleetMemberType.SHIP, "Gravitas 2", true);
			api.addToFleet(FleetSide.PLAYER, "LTA_Brawler_Thunderbirds_Hunterkiller", FleetMemberType.SHIP, "Delwit", false);
			api.addToFleet(FleetSide.PLAYER, "LTA_Brawler_Thunderbirds_Assault", FleetMemberType.SHIP, "Daemos", false);
			api.addToFleet(FleetSide.PLAYER, "LTA_Brawler_Thunderbirds_Escort", FleetMemberType.SHIP, "Dillweed", false);
			api.addToFleet(FleetSide.PLAYER, "LTA_Brawler_Thunderbirds_HeavilyModified", FleetMemberType.SHIP, "Dubbs", false);
			api.addToFleet(FleetSide.PLAYER, "LTA_Epattcudxi_Thunderbirds_Elite", FleetMemberType.SHIP, "'Lé Brainlét'", false);
			//api.addToFleet(FleetSide.PLAYER, "wolf_PD", FleetMemberType.SHIP, "TTS Tescuetti", false);
			//api.addToFleet(FleetSide.PLAYER, "brawler_Elite", FleetMemberType.SHIP, "HSS Mothballer", false);
			//api.addToFleet(FleetSide.PLAYER, "LTA_Consul_Standard", FleetMemberType.SHIP, "HSS Meatball", false);
			//api.addToFleet(FleetSide.PLAYER, "lasher_Strike", FleetMemberType.SHIP, "HSS Dills", false);
			//api.addToFleet(FleetSide.PLAYER, "lasher_Standard", FleetMemberType.SHIP, "HSS Frills", false);
			
			// Set up the enemy fleet.
			api.addToFleet(FleetSide.ENEMY, "prometheus2_Standard", FleetMemberType.SHIP, "Ludd's Right Ear", false);
			api.addToFleet(FleetSide.ENEMY, "colossus2_Pather", FleetMemberType.SHIP, "Soils of Grenellen", false);
			api.addToFleet(FleetSide.ENEMY, "gremlin_luddic_path_Strike", FleetMemberType.SHIP, "Marie's Testament", false);
			api.addToFleet(FleetSide.ENEMY, "brawler_pather_Raider", FleetMemberType.SHIP, "Kumari of Woternese", false);
			api.addToFleet(FleetSide.ENEMY, "lasher_luddic_path_Raider", FleetMemberType.SHIP, "Velwarden", false);
			api.addToFleet(FleetSide.ENEMY, "kite_luddic_path_Raider", FleetMemberType.SHIP, "Little Retribution", false);
			api.addToFleet(FleetSide.ENEMY, "kite_luddic_path_Strike", FleetMemberType.SHIP, "Talenti Manuscript", false);
			//api.addToFleet(FleetSide.ENEMY, "LTA_Epattcudxi_Thunderbirds_Unlicensed", FleetMemberType.SHIP, "||UNREGISTERED FRIGATE||", false);
			
			//api.defeatOnShipLoss("Stranger II");
		}
		
		if (testMode) {
//			FleetMemberAPI member = api.addToFleet(FleetSide.PLAYER, "omen_PD", FleetMemberType.SHIP, "Milk Run", true);
//			member.getCaptain().getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
//			member.getCaptain().getStats().setSkillLevel(Skills.SHIELD_MODULATION, 2);
//			member.getCaptain().getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
			
			//api.addToFleet(FleetSide.PLAYER, "falcon_Attack", FleetMemberType.SHIP, "Stranger II", true);
	//		PersonAPI person = new AICoreOfficerPluginImpl().createPerson(Commodities.ALPHA_CORE, null, null);
	//		member.setCaptain(person);
			
			//api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, "Cherenkov Bloom", false);
			//api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, null, false);
			//api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, null, false);
			
			api.addObjective(0, 4000, BattleObjectives.SENSOR_JAMMER);
			api.addObjective(4000, 0, BattleObjectives.COMM_RELAY);
			api.addObjective(-3000, -2000, BattleObjectives.NAV_BUOY);
		}
		
		// Set up the map.
		float width = 12000f;
		float height = 12000f;
		
		if (testMode) {
			width += 4000;
			height += 8000;
		}
		
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		// Add an asteroid field
		api.addAsteroidField(minX, minY + height / 2, 0, 8000f,
							 20f, 70f, 100);
		
		//api.addPlanet(0, 0, 50f, StarTypes.RED_GIANT, 250f, true);
		
	}

}
