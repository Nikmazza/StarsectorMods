package Jaydee8652.JaydeePiracy.plugins;

import java.util.Random;

import Jaydee8652.JaydeePiracy.utils.jdp_Ranks;
import Jaydee8652.JaydeePiracy.utils.jdp_Skills;
import Jaydee8652.JaydeePiracy.utils.JaydeePiracyIDs;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.BaseAICoreOfficerPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;

public class OmegaCoreOfficerPluginImpl extends BaseAICoreOfficerPluginImpl {//Applies the Omega officer stats to my clone of the item

	public static int JDP_OMEGA_POINTS = 0;
	public static float JDP_OMEGA_MULT = 4;

	public PersonAPI createPerson(String aiCoreId, String factionId, Random random) {
		if (random == null) random = new Random();
		
		PersonAPI person = Global.getFactory().createPerson();
		person.setFaction(factionId);
		person.setAICoreId(aiCoreId);
		
		CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(aiCoreId);
		boolean jdp_omega = JaydeePiracyIDs.JDP_CORE_OMEGA.equals(aiCoreId);
		
		person.getStats().setSkipRefresh(true);
		
		person.setName(new FullName(spec.getName(), "", Gender.ANY));
		int points = 0;
		float mult = 1f;
		if (jdp_omega) {
			person.setPortraitSprite("graphics/portraits/characters/omega.png");
			person.getStats().setLevel(9);
			person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
			person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
			person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
			person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
			person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
			person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
			person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
			person.getStats().setSkillLevel(Skills.POINT_DEFENSE, 2);
			person.getStats().setSkillLevel(jdp_Skills.JDP_OMEGA_ECM, 2);//Uses my copy of the skill which has a description and icon
		}
		points = JDP_OMEGA_POINTS;
		mult = JDP_OMEGA_MULT;

		if (points != 0) {
			person.getMemoryWithoutUpdate().set(AUTOMATED_POINTS_VALUE, points);
		}
		person.getMemoryWithoutUpdate().set(AUTOMATED_POINTS_MULT, mult);
		
		person.setPersonality(Personalities.RECKLESS);
		person.setRankId(jdp_Ranks.JDP_DANCER);

		person.setPostId(null);
		
		person.getStats().setSkipRefresh(false);
		
		return person;
	}

}




