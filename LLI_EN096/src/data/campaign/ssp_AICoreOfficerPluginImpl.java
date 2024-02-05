package data.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.BaseAICoreOfficerPluginImpl;

import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;

import java.util.Random;

public class ssp_AICoreOfficerPluginImpl extends BaseAICoreOfficerPluginImpl implements AICoreOfficerPlugin {
    public PersonAPI createPerson(String aiCoreId, String factionId, Random random) {
        int points = 15;
        float mult = 1f;
        PersonAPI person = Global.getFactory().createPerson();
        person.setFaction(factionId);
        person.setAICoreId(aiCoreId);
        CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(aiCoreId);
        person.getStats().setSkipRefresh(true);
        person.setName(new FullName(spec.getName(), "", FullName.Gender.ANY));
        person.setPortraitSprite("graphics/portraits/portrait_ai1b.png");
        person.getStats().setLevel(2);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
        person.getMemoryWithoutUpdate().set(AUTOMATED_POINTS_VALUE, points);
        person.getMemoryWithoutUpdate().set(AUTOMATED_POINTS_MULT, mult);
        person.setPersonality(Personalities.RECKLESS);
        person.setRankId(Ranks.UNKNOWN);
        person.setPostId(null);
        person.getStats().setSkipRefresh(false);
        return person;
    }
}
