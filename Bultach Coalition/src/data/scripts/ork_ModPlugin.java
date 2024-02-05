package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import data.scripts.world.ork_Gen;
import exerelin.campaign.SectorManager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ork_ModPlugin extends BaseModPlugin {

    @Override
    public void onNewGame() {
        SectorAPI sector = Global.getSector();

        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
            new ork_Gen().generate(sector);
        }
    }

    public static void OrkLoveSettings() {
        FactionAPI orks = Global.getSector().getFaction("orks");

        for (FactionAPI faction : Global.getSector().getAllFactions()){
            //if (faction.getRelationship("orks") > 0.2f) continue; //I guess if someone loves orks that much, they can be above that???
            //if (faction.getRelationship("orks") < 0f) continue; //I guess if someone hates orks that much, they can be below that.
            if (Objects.equals(faction.getId(), "orks")) continue;
            orks.setRelationship(faction.getId(), -0.2f); //Otherwise set all factions to -0.2.
        }

        //Vanilla factions
        orks.setRelationship(Factions.LUDDIC_CHURCH, 0.2f);
        orks.setRelationship(Factions.LUDDIC_PATH, -0.3f);
        orks.setRelationship(Factions.TRITACHYON, -1f);
        orks.setRelationship(Factions.PERSEAN, -0.3f);
        orks.setRelationship(Factions.PIRATES, -0.5f);
        orks.setRelationship(Factions.INDEPENDENT, 0.6f);
        orks.setRelationship(Factions.DIKTAT, -0.5f);
        orks.setRelationship(Factions.LIONS_GUARD, -0.5f);
        orks.setRelationship(Factions.HEGEMONY, 0.3f);
        orks.setRelationship(Factions.REMNANTS, -1f);
        //Modded one
        if(Global.getSettings().getModManager().isModEnabled("blade_breakers")) {
            orks.setRelationship("blade_breakers", -0.8f);
        }
        if(Global.getSettings().getModManager().isModEnabled("exipirated")) {
            orks.setRelationship("exipirated", -0.6f);
        }
        if(Global.getSettings().getModManager().isModEnabled("gmda")) {
            orks.setRelationship("gmda", -0.6f);
        }
        if(Global.getSettings().getModManager().isModEnabled("gmda_patrol")) {
            orks.setRelationship("gmda_patrol", -0.6f);
        }
        if(Global.getSettings().getModManager().isModEnabled("draco")) {
            orks.setRelationship("draco", -0.6f);
        }
        if(Global.getSettings().getModManager().isModEnabled("fang")) {
            orks.setRelationship("fang", -0.6f);
        }
        if(Global.getSettings().getModManager().isModEnabled("HMI")) {
            orks.setRelationship("mess", -0.8f);
        }
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        OrkLoveSettings();

        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();

        MarketAPI market1 = Global.getSector().getEconomy().getMarket("dregruk");
        if (market1 != null) {
            PersonAPI admin1 = Global.getFactory().createPerson();
            admin1.setId("orks_urlakk");
            admin1.setFaction("orks");
            admin1.setGender(FullName.Gender.MALE);
            admin1.setPostId(Ranks.POST_FACTION_LEADER);
            admin1.setRankId(Ranks.FACTION_LEADER);
            admin1.setImportance(PersonImportance.VERY_HIGH);
            admin1.getName().setFirst("Uisdean");
            admin1.getName().setLast("Knox");
            admin1.setPortraitSprite(Global.getSettings().getSpriteName("characters", "BTuisdean"));
            admin1.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            ip.addPerson(admin1);
            market1.setAdmin(admin1);
            market1.getCommDirectory().addPerson(admin1, 0);
            market1.addPerson(admin1);
        }

        MarketAPI market2 = Global.getSector().getEconomy().getMarket("orguk");
        if (market2 != null) {
            PersonAPI admin2 = Global.getFactory().createPerson();
            admin2.setId("orks_vorhgad");
            admin2.setFaction("orks");
            admin2.setGender(FullName.Gender.MALE);
            admin2.setPostId(Ranks.POST_FACTION_LEADER);
            admin2.setRankId(Ranks.FACTION_LEADER);
            admin2.setImportance(PersonImportance.VERY_HIGH);
            admin2.getName().setFirst("Liam");
            admin2.getName().setLast("Niall");
            admin2.setPortraitSprite(Global.getSettings().getSpriteName("characters", "BTliam"));
            admin2.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            ip.addPerson(admin2);
            market2.setAdmin(admin2);
            market2.getCommDirectory().addPerson(admin2, 0);
            market2.addPerson(admin2);
        }

        MarketAPI market3 = Global.getSector().getEconomy().getMarket("gathrog");
        if (market3 != null) {
            PersonAPI admin3 = Global.getFactory().createPerson();
            admin3.setId("orks_gharag");
            admin3.setFaction("orks");
            admin3.setGender(FullName.Gender.FEMALE);
            admin3.setPostId(Ranks.POST_FACTION_LEADER);
            admin3.setRankId(Ranks.FACTION_LEADER);
            admin3.setImportance(PersonImportance.VERY_HIGH);
            admin3.getName().setFirst("Ailis");
            admin3.getName().setLast("Mil");
            admin3.setPortraitSprite(Global.getSettings().getSpriteName("characters", "BTailis"));
            admin3.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
            ip.addPerson(admin3);
            market3.setAdmin(admin3);
            market3.getCommDirectory().addPerson(admin3, 0);
            market3.addPerson(admin3);
        }
    }
}