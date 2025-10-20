package data.scripts.vice;

import java.awt.Color;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BaseAICoreOfficerPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class RelicCorePlugin extends BaseAICoreOfficerPluginImpl implements AICoreOfficerPlugin {
	
	private static String DELTA_RELIC_ID = "asm_relic_delta";
	private static String GAMMA_RELIC_ID = "asm_relic_gamma";
	private static String BETA_RELIC_ID = "asm_relic_beta";
	private static String ASM_SKILL = "asm_devouring_swarm";
	
	public PersonAPI createPerson(String aiCoreId, String factionId, Random random) {
		if (random == null) random = new Random();
		PersonAPI person = Global.getFactory().createPerson();
		person.setFaction(factionId);
		person.setAICoreId(aiCoreId);
		CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(aiCoreId);
		boolean isRelicDelta = DELTA_RELIC_ID.equals(aiCoreId);
		boolean isRelicGamma = GAMMA_RELIC_ID.equals(aiCoreId);
		boolean isRelicBeta = BETA_RELIC_ID.equals(aiCoreId);
		person.getStats().setSkipRefresh(true);
		float mult = 1f;
		if (isRelicDelta) {
			person.setName(new FullName("Infector Fragment", "", Gender.ANY));
			person.setPortraitSprite("graphics/asm/portraits/asm_relic_delta.png");
			person.getStats().setLevel(2);
			person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 1);
			person.getStats().setSkillLevel(ASM_SKILL, 1);
			person.setRankId(Ranks.SPACE_LIEUTENANT);
			mult = 1f;
		}
		if (isRelicGamma) {
			person.setName(new FullName("Infected Gamma Core", "", Gender.ANY));
			person.setPortraitSprite("graphics/asm/portraits/asm_relic_gamma.png");
			person.getStats().setLevel(3);
			person.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);
			person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
			person.getStats().setSkillLevel(ASM_SKILL, 1);
			person.setRankId(Ranks.SPACE_LIEUTENANT);
			mult = 2f;
		}
		else if (isRelicBeta) {
			person.setName(new FullName("Infected Beta Core", "", Gender.ANY));
			person.setPortraitSprite("graphics/asm/portraits/asm_relic_beta.png");
			person.getStats().setLevel(4);
			person.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);
			person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
			person.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
			person.getStats().setSkillLevel(ASM_SKILL, 2);
			person.setRankId(Ranks.SPACE_CAPTAIN);
			mult = 3f;
		}
		person.getMemoryWithoutUpdate().set("$autoPointsMult", mult);
		person.setPersonality(Personalities.RECKLESS);
		person.setPostId(null);
		person.getStats().setSkipRefresh(false);
        return person;
	}
}