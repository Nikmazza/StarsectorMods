package data.missions.testbed;

import java.util.List;

import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {

		
		// Set up the fleets
		api.initFleet(FleetSide.PLAYER, "SA", FleetGoal.ATTACK, false, 2);
		api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true, 10);

		// Set a blurb for each fleet
		api.setFleetTagline(FleetSide.PLAYER, "The SA Tax Evasion");
		api.setFleetTagline(FleetSide.ENEMY, "Trailing elements of the Hegemony Tax Collector Fleet");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		
		// Set up the player's fleet
		api.addToFleet(FleetSide.PLAYER, "sa_ageis_assault", FleetMemberType.SHIP, "SA Tax Evasion", true);
		//api.addToFleet(FleetSide.PLAYER, "onslaught_Standard", FleetMemberType.SHIP, "TTS Invincible", true, CrewXPLevel.ELITE);
		api.addToFleet(FleetSide.PLAYER, "sa_almas_standard", FleetMemberType.SHIP, "SA Crisp White Sheets", true);
		api.addToFleet(FleetSide.PLAYER, "sa_box_attack", FleetMemberType.SHIP, "SA Borders", true);
		api.addToFleet(FleetSide.PLAYER, "sa_brace_combat", FleetMemberType.SHIP, "SA Space Wizard", true);
		api.addToFleet(FleetSide.PLAYER, "sa_candela_assault", FleetMemberType.SHIP, "SA Thunderdome", true);
		api.addToFleet(FleetSide.PLAYER, "sa_cyclops_mining", FleetMemberType.SHIP, "SA Bad Apple", true);
		api.addToFleet(FleetSide.PLAYER, "sa_eclipse_assault", FleetMemberType.SHIP, "SA Have", true);
		api.addToFleet(FleetSide.PLAYER, "sa_elanus_standard", FleetMemberType.SHIP, "SA Illiteracy", true);
		api.addToFleet(FleetSide.PLAYER, "sa_gaia_standard", FleetMemberType.SHIP, "SA Airlock Stuck", true);
		api.addToFleet(FleetSide.PLAYER, "sa_gangster_strike", FleetMemberType.SHIP, "SA Avarice", true);
		api.addToFleet(FleetSide.PLAYER, "sa_gorget_attack", FleetMemberType.SHIP, "SA Nature's Sacrifice", true);
		api.addToFleet(FleetSide.PLAYER, "sa_kyresh_escort", FleetMemberType.SHIP, "SA Robust", true);
		api.addToFleet(FleetSide.PLAYER, "sa_lobster_royal", FleetMemberType.SHIP, "SA Greytide", true);
		api.addToFleet(FleetSide.PLAYER, "sa_mobster_assault", FleetMemberType.SHIP, "SA What Manual", true);
		api.addToFleet(FleetSide.PLAYER, "sa_pacemaker_strike", FleetMemberType.SHIP, "SA Weapons Sold Separately", true);
		api.addToFleet(FleetSide.PLAYER, "sa_splint_assault", FleetMemberType.SHIP, "SA Onslaught Not Included", true);


		// Set up the enemy fleet

		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		
		//api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);

		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		//api.addToFleet(FleetSide.ENEMY, "lasher_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		//api.addToFleet(FleetSide.ENEMY, "lasher_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		//api.addToFleet(FleetSide.ENEMY, "hound_hegemony_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		//api.addToFleet(FleetSide.ENEMY, "hound_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "atlas_Standard", FleetMemberType.SHIP, false);
		
		
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
		
		
		api.addPlugin(new BaseEveryFrameCombatPlugin() {
			public void advance(float amount, List events) {
			}
			public void init(CombatEngineAPI engine) {
				engine.getContext().setStandoffRange(10000f);
			}
		});
	}

}






