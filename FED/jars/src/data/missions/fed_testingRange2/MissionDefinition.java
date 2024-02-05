package data.missions.fed_testingRange2;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

    @Override
    public void defineMission(MissionDefinitionAPI api) {

        // Set up the fleets so we can add ships and fighter wings to them.
        // In this scenario, the fleets are attacking each other, but
        // in other scenarios, a fleet may be defending or trying to escape
        api.initFleet(FleetSide.PLAYER, "SFS", FleetGoal.ATTACK, false);
        api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true);

        // Set a small blurb for each fleet that shows up on the mission detail and
        // mission results screens to identify each side.
        api.setFleetTagline(FleetSide.PLAYER, "Star Federation Simulacra");
        api.setFleetTagline(FleetSide.ENEMY, "Targets");

        // These show up as items in the bulleted list under 
        // "Tactical Objectives" on the mission detail screen
//		api.addBriefingItem("Launch missiles for maximum PD saturation.");
//		api.addBriefingItem("Use your shield sparingly at high flux, your shielding is merely average.");
//		api.addBriefingItem("USN HeadOn must survive");
        // Set up the player's fleet.  Variant names come from the
        // files in data/variants and data/variants/fighters
        api.addToFleet(FleetSide.PLAYER, "fed_station_1_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_boss_standard", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "fed_superkestrel_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_flagship_assault_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_flagship_standard", FleetMemberType.SHIP, false);

        api.addToFleet(FleetSide.PLAYER, "fed_cruisercarrier_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_osprey_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_kestral_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_stealth_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_raptor_standard", FleetMemberType.SHIP, false);
        
        api.addToFleet(FleetSide.PLAYER, "fed_superdestroyer_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_rigger_firesupport_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_rigger_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_lightdestroyer_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_cormorant_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_cormorant_old_standard", FleetMemberType.SHIP, false);

        api.addToFleet(FleetSide.PLAYER, "fed_colubris_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_tern_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_talos_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_superkite_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_byte_standard", FleetMemberType.SHIP, false);

        api.addToFleet(FleetSide.PLAYER, "fed_cormorant_old_pirate_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_lightdestroyer_p_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_tern_pirate_standard", FleetMemberType.SHIP, false);
        
        api.addToFleet(FleetSide.PLAYER, "fed_boss_rebel_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_legion_shock", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_cargocap_standard", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "fed_superdestroyer_pirate_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_mantis_cruiser_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_nisos_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_shivan_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_stormwalker_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_polyp_standard", FleetMemberType.SHIP, false);

        api.addToFleet(FleetSide.PLAYER, "fed_heavyfreighter_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_medcombtanker_standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "fed_superbuffalo_standard", FleetMemberType.SHIP, false);
        
        //api.addToFleet(FleetSide.PLAYER, "fed_omega_capital_redeemer", FleetMemberType.SHIP, false);

        // Set up the enemy fleet.
        //api.addToFleet(FleetSide.ENEMY, "eagle_Assault", FleetMemberType.SHIP, false);		
        //api.addToFleet(FleetSide.ENEMY, "onslaught_Outdated", FleetMemberType.SHIP, false);	
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_Support", FleetMemberType.SHIP, false);

        // Set up the map.
        float width = 15000f;
        float height = 11000f;
        api.initMap((float) -width / 2f, (float) width / 2f, (float) -height / 2f, (float) height / 2f);

        float minX = -width / 2;
        float minY = -height / 2;

        // All the addXXX methods take a pair of coordinates followed by data for
        // whatever object is being added.
        // And a few random ones to spice up the playing field.
        // A similar approach can be used to randomize everything
        // else, including fleet composition.
        for (int i = 0; i < 7; i++) {
            float x = (float) Math.random() * width - width / 2;
            float y = (float) Math.random() * height - height / 2;
            float radius = 100f + (float) Math.random() * 800f;
            api.addNebula(x, y, radius);
        }

        // Add objectives. These can be captured by each side
        // and provide stat bonuses and extra command points to
        // bring in reinforcements.
        // Reinforcements only matter for large fleets - in this
        // case, assuming a 100 command point battle size,
        // both fleets will be able to deploy fully right away.
        api.addObjective(minX + width * 0.7f, minY + height * 0.25f, "sensor_array");
        api.addObjective(minX + width * 0.8f, minY + height * 0.75f, "nav_buoy");
        api.addObjective(minX + width * 0.2f, minY + height * 0.25f, "nav_buoy");

    }

}
