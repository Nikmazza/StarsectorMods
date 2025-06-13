package data.hullmods.vice;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class MissionDeploy extends BaseHullMod {
	
	private static float DISCOUNT_L = 60f;
	private static float DISCOUNT_S = 30f;
	private static float DISCOUNT_CA = 24f;
	private static float HALF_COST = 50f;
	private static String ALPHA_ID = "alpha_core";
	private static String BETA_ID = "beta_core";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean apply = true; //can toggle self delete at end of method
		
		//do not apply if ship is in player fleet
		if (stats.getFleetMember() != null && Global.getSector().getPlayerFleet() != null) {
			List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
			for (FleetMemberAPI member : fleetList) {
				if (member.getVariant().getHullVariantId() == stats.getVariant().getHullVariantId()) {
					apply = false;
				}
			}
		}
		
		if (stats.getVariant().hasHullMod("vice_experimental_hull_hvb") 
				&& stats.getFleetMember() != null && stats.getFleetMember().getOwner() != 0
				&& !ALPHA_ID.equals(stats.getFleetMember().getCaptain().getAICoreId())) {
			PersonAPI person = Global.getFactory().createPerson();
			person.setFaction("diamond_nexus");
			person.setAICoreId(ALPHA_ID);
			person.setName(new FullName(Global.getSettings().getCommoditySpec(ALPHA_ID).getName(), "", Gender.ANY));
			person.setPortraitSprite("graphics/portraits/portrait_ai2b.png");
			person.getStats().setLevel(7);
			person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
			person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
			person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
			person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
			person.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2);
			person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
			person.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
			person.setRankId(Ranks.SPACE_CAPTAIN);
			person.setPersonality(Personalities.RECKLESS);
			person.setPostId(null);
			person.getStats().setSkipRefresh(false);
			stats.getFleetMember().setCaptain(person);
		}
		else if (stats.getVariant().hasHullMod("automated") 
				&& stats.getFleetMember() != null && stats.getFleetMember().getOwner() != 0
				&& !BETA_ID.equals(stats.getFleetMember().getCaptain().getAICoreId())) {
			PersonAPI person = Global.getFactory().createPerson();
			person.setFaction("persean");
			person.setAICoreId(BETA_ID);
			person.setName(new FullName(Global.getSettings().getCommoditySpec(BETA_ID).getName(), "", Gender.ANY));
			person.setPortraitSprite("graphics/portraits/portrait_ai3.png");
			person.getStats().setLevel(5);
			person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
			person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
			person.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
			person.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 2);
			person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
			person.setRankId(Ranks.SPACE_CAPTAIN);
			person.setPersonality(Personalities.RECKLESS);
			person.setPostId(null);
			person.getStats().setSkipRefresh(false);
			stats.getFleetMember().setCaptain(person);
		}
		
		boolean applyCarrier = stats.getVariant().hasHullMod("vice_modular_fleet_override") ? true : false;
		boolean applySmall = stats.getVariant().hasHullMod("automated") ? true : false;
		boolean isPirate = !stats.getVariant().hasHullMod("advancedcore") ? true : false;
		if (stats.getVariant().hasHullMod("vice_experimental_hull_hvb")) isPirate = false;
		
		if (applyCarrier) stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, -DISCOUNT_CA);
		else if (applySmall) stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, -DISCOUNT_S);
		else if (isPirate) stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyPercent(id, -HALF_COST);
		else if (apply) stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, -DISCOUNT_L);
		
		//if (!apply) stats.getVariant().getHullMods().remove(id);
	}
}