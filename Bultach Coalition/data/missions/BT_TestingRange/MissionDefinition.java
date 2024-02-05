package data.missions.bt_testingrange;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        api.initFleet(FleetSide.PLAYER, "BT", FleetGoal.ATTACK, false);
        api.initFleet(FleetSide.ENEMY, "SIM", FleetGoal.ATTACK, true);

        api.setFleetTagline(FleetSide.PLAYER, "Simulated Bultach Forces");
        api.setFleetTagline(FleetSide.ENEMY, "Simulated Enemy Forces");

        api.addBriefingItem("Test Bultach Vessels");

		api.addToFleet(FleetSide.PLAYER, "ork_sgr_main_standard", FleetMemberType.SHIP, "BT Over Heaven", true);
		api.addToFleet(FleetSide.PLAYER, "ork_dreadnotv2_standard", FleetMemberType.SHIP, "BT More Guns", false);
        api.addToFleet(FleetSide.PLAYER, "ork_battlecruiser_standard", FleetMemberType.SHIP, "BT How Do I Stop", false);
        api.addToFleet(FleetSide.PLAYER, "ork_bigscrap_standard", FleetMemberType.SHIP, "BT Get Over Here", false);

        api.addToFleet(FleetSide.PLAYER, "ork_crusher_standard", FleetMemberType.SHIP, "BT Beats Walking", false);
        api.addToFleet(FleetSide.PLAYER, "ork_tail_standard", FleetMemberType.SHIP, "BT Rock And Stone", false);
        api.addToFleet(FleetSide.PLAYER, "ork_bigracer_standard", FleetMemberType.SHIP, "BT Brick", false);
        api.addToFleet(FleetSide.PLAYER, "ork_faster_standard", FleetMemberType.SHIP, "BT This Ship Kills Executives", false);
        api.addToFleet(FleetSide.PLAYER, "ork_vencha_standard", FleetMemberType.SHIP, "BT Soup Stand", false);
        api.addToFleet(FleetSide.PLAYER, "ork_thrasher_standard", FleetMemberType.SHIP, "BT Front Towards Enemy", false);
        api.addToFleet(FleetSide.PLAYER, "ork_interdictor_standard", FleetMemberType.SHIP, "BT No Phase", false);

        api.addToFleet(FleetSide.PLAYER, "ork_hammah_standard", FleetMemberType.SHIP, "BT Void Sailing", false);
        api.addToFleet(FleetSide.PLAYER, "ork_smasha_standard", FleetMemberType.SHIP, "BT Crab", false);
        api.addToFleet(FleetSide.PLAYER, "ork_prong_standard", FleetMemberType.SHIP, "BT Chow Hall", false);
        api.addToFleet(FleetSide.PLAYER, "ork_bees_standard", FleetMemberType.SHIP, "BT Box Of Bees", false);
        api.addToFleet(FleetSide.PLAYER, "ork_speeder_standard", FleetMemberType.SHIP, "BT Need For Speed", false);
        api.addToFleet(FleetSide.PLAYER, "ork_old_standard", FleetMemberType.SHIP, "BT Rust Bucket", false);

        api.addToFleet(FleetSide.PLAYER, "ork_grot_standard", FleetMemberType.SHIP, "BT Need More Guns", false);
        api.addToFleet(FleetSide.PLAYER, "ork_scrimb_standard", FleetMemberType.SHIP, "BT Repressor", false);
        api.addToFleet(FleetSide.PLAYER, "ork_bastard_standard", FleetMemberType.SHIP, "BT War Enthusiast", false);
        api.addToFleet(FleetSide.PLAYER, "ork_racer_standard", FleetMemberType.SHIP, "BT G Forces", false);
        api.addToFleet(FleetSide.PLAYER, "ork_skrunkly_standard", FleetMemberType.SHIP, "BT Ol Reliable", false);



        FactionAPI tritachyon = Global.getSettings().createBaseFaction(Factions.TRITACHYON);
        FleetMemberAPI member;
        member = api.addToFleet(FleetSide.ENEMY, "fury_Attack", FleetMemberType.SHIP, false);
        member.setShipName(tritachyon.pickRandomShipName());

        float width = 16000f;
        float height = 16000f;
        api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);

        for (int i = 0; i < 6; i++) {
            float x = (float) Math.random() * width - width / 2;
            float y = (float) Math.random() * height - height / 2;
            float radius = 100f + (float) Math.random() * 400f;
            api.addNebula(x, y, radius);
        }

        api.addObjective(width * 0.35f, -height * 0.1f, "nav_buoy");
        api.addObjective(-width * 0.35f, -height * 0.1f, "nav_buoy");
        api.addObjective(0f, -height * 0.3f, "sensor_array");
        api.addObjective(width * 0.2f, height * 0.35f, "comm_relay");
        api.addObjective(-width * 0.2f, height * 0.35f, "comm_relay");

        api.addNebula(0f, -height * 0.3f, 1000f);
        api.addNebula(width * 0.15f, -height * 0.05f, 2000f);
        api.addNebula(-width * 0.15f, -height * 0.05f, 2000f);

        api.addRingAsteroids(0f, 0f, 40f, width, 30f, 40f, 400);

        api.addPlanet(0, 0, 350f, "ice_giant", 0f, true);
    }
}
