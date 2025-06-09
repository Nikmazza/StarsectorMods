package data.missions.yna_test;

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
		api.initFleet(FleetSide.PLAYER, "YNA", FleetGoal.ATTACK, false);
		api.initFleet(FleetSide.ENEMY, "TTS", FleetGoal.ATTACK, true, 5);

		// Set a blurb for each fleet
		api.setFleetTagline(FleetSide.PLAYER, "Ynadar Testing Fleet");
		api.setFleetTagline(FleetSide.ENEMY, "Tri-Tachyon armada");
		
		// These show up as items in the bulleted list under 
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("Defeat the enemy forces");
		
		// Set up the player's fleet
		api.addToFleet(FleetSide.PLAYER, "yna_cinderbull_FS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_nitroknife_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_pummeler_Strike", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_maulsling_Balanced", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_glarestruck_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_clamcan_Standard", FleetMemberType.SHIP, false);
		
		api.addToFleet(FleetSide.PLAYER, "yna_ballpeinhead_CS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_spitroast_CS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_quillpress_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_rockblaster_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_flamedial_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_kindlebow_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_buffalo_Standard", FleetMemberType.SHIP, false);
		
		api.addToFleet(FleetSide.PLAYER, "yna_starspot_Recon", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_grouse_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_hailslammer_LS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_weighbell_Support", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_arsonguard_Balanced", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_lumbergiant_Standard", FleetMemberType.SHIP, false);
		
		api.addToFleet(FleetSide.PLAYER, "yna_sunblade_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_torchmace_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_winterthorn_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.PLAYER, "yna_urgrand_Standard", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.PLAYER, "yna_flarebringer_Soaring", FleetMemberType.SHIP, false);
		
		api.addToFleet(FleetSide.PLAYER, "yna_perilstar_Ambitious", FleetMemberType.SHIP, false);
		
		// Set up the enemy fleet
		api.addToFleet(FleetSide.ENEMY, "paragon_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "paragon_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "astral_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "astral_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "astral_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "astral_Elite", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "odyssey_Balanced", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "odyssey_Balanced", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "odyssey_Balanced", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "medusa_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "medusa_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "medusa_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "medusa_CS", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hyperion_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hyperion_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hyperion_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hyperion_Strike", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "hyperion_Strike", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, false);
		
		api.addToFleet(FleetSide.ENEMY, "wasp_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "wasp_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "wasp_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "wasp_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "wasp_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "wasp_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "wasp_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "wasp_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "wasp_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "xyphos_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "xyphos_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "xyphos_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "xyphos_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "trident_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "trident_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "trident_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "trident_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "dagger_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "dagger_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "dagger_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "dagger_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "dagger_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "longbow_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "longbow_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "longbow_wing", FleetMemberType.FIGHTER_WING, false);
		api.addToFleet(FleetSide.ENEMY, "longbow_wing", FleetMemberType.FIGHTER_WING, false);
		
		
		// Set up the map.
		float width = 24000f;
		float height = 18000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		for (int i = 0; i < 300; i++) {
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height - height/4;
			
			if (x > -1000 && x < 1500 && y < -1000) continue;
			float radius = 200f + (float) Math.random() * 900f; 
			api.addNebula(x, y, radius);
		}
		
		api.addPlugin(new BaseEveryFrameCombatPlugin() {
			public void init(CombatEngineAPI engine) {
				engine.getContext().setStandoffRange(12000f);
			}
			public void advance(float amount, List events) {
			}
		});
			
	}

}






