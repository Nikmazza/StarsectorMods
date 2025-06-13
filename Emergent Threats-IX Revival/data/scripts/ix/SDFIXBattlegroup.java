package data.scripts.ix;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent.SkillPickPreference;
import com.fs.starfarer.api.impl.campaign.fleets.SDFBase;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.missions.FleetCreatorMission;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers.FleetQuality;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers.FleetSize;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers.OfficerNum;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers.OfficerQuality;
import com.fs.starfarer.api.impl.campaign.missions.hub.MissionFleetAutoDespawn;

//not implemented
public class SDFIXBattlegroup extends SDFBase {

    public SDFIXBattlegroup() {
    }

   @Override
    protected String getFactionId() {
        return "ix_battlegroup";
    }

    protected SkillPickPreference getCommanderShipSkillPreference() {
        return SkillPickPreference.YES_ENERGY_NO_BALLISTIC_YES_MISSILE_YES_DEFENSE;
    }

    @Override
    protected MarketAPI getSourceMarket() {
        return Global.getSector().getEconomy().getMarket("ix_piorun_market");
    }

    @Override
    protected String getDefeatTriggerToUse() {
        return "SDFIXBattlegroupDefeated";
    }


    @Override
    public CampaignFleetAPI spawnFleet() {

        MarketAPI ix_piorun_market = getSourceMarket();

        FleetCreatorMission m = new FleetCreatorMission(random);
        m.beginFleet();

        Vector2f loc = ix_piorun_market.getLocationInHyperspace();

        m.triggerCreateFleet(FleetSize.MAXIMUM, FleetQuality.SMOD_1, "SDFIXBattlegroup", FleetTypes.PATROL_LARGE, loc);

        m.triggerSetFleetSizeFraction(1.25f);

        m.triggerSetFleetOfficers( OfficerNum.ALL_SHIPS, OfficerQuality.HIGHER);
        m.triggerSetFleetDoctrineComp(3, 2, 0);
        m.triggerSetFleetCommander(getPerson());

        m.triggerFleetAddCommanderSkill(Skills.COORDINATED_MANEUVERS, 1);
        m.triggerFleetAddCommanderSkill(Skills.TACTICAL_DRILLS, 1);
        m.triggerFleetAddCommanderSkill(Skills.CREW_TRAINING, 1);
        m.triggerFleetAddCommanderSkill(Skills.CARRIER_GROUP, 1);
        m.triggerFleetAddCommanderSkill(Skills.OFFICER_TRAINING, 1);

        m.triggerSetPatrol();
        m.triggerSetFleetMemoryValue(MemFlags.MEMORY_KEY_SOURCE_MARKET, ix_piorun_market);
        //m.triggerFleetSetNoFactionInName();
        m.triggerFleetSetName("System Defense Fleet");
        m.triggerPatrolAllowTransponderOff();
        //m.triggerFleetSetPatrolActionText("patrolling");
        m.triggerOrderFleetPatrol(ix_piorun_market.getStarSystem());

        CampaignFleetAPI fleet = m.createFleet();
        fleet.removeScriptsOfClass(MissionFleetAutoDespawn.class);
        ix_piorun_market.getContainingLocation().addEntity(fleet);
        fleet.setLocation(ix_piorun_market.getPlanetEntity().getLocation().x, ix_piorun_market.getPlanetEntity().getLocation().y);
        fleet.setFacing((float) random.nextFloat() * 360f);
		fleet.setFaction("ix_battlegroup", true);

        /*for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            if (member.getHullId().equals("legion")) {
                member.setVariant(getVariant("legion_xiv_Elite"), false, false);
            } else if (member.getHullId().equals("onslaught")) {
                member.setVariant(getVariant("onslaught_xiv_Elite"), false, false);
            } else if (member.getHullId().equals("eagle")) {
                member.setVariant(getVariant("eagle_xiv_Elite"), false, false);
            } else if (member.getHullId().equals("falcon")) {
                if (random.nextFloat() < 0.5f) {
                    member.setVariant(getVariant("falcon_xiv_Elite"), false, false);
                } else {
                    member.setVariant(getVariant("falcon_xiv_Escort"), false, false);
                }
            } else if (member.getHullId().equals("dominator")) {
                member.setVariant(getVariant("dominator_XIV_Elite"), false, false);
            } else if (member.getHullId().equals("enforcer")) {
                member.setVariant(getVariant("enforcer_XIV_Elite"), false, false);
            }*/

//				member.setVariant(member.getVariant().clone(), false, false);
//				member.getVariant().setSource(VariantSource.REFIT);
//				member.getVariant().addTag(Tags.TAG_NO_AUTOFIT);
//				member.getVariant().addTag(Tags.VARIANT_CONSISTENT_WEAPON_DROPS);
//        }

        return fleet;
    }
}