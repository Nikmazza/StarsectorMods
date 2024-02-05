package data.world.fed;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import exerelin.campaign.SectorManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import static com.fs.starfarer.api.impl.campaign.CoreLifecyclePluginImpl.dedupePortraits;

public class FedGen implements SectorGeneratorPlugin {

    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI star_federation = sector.getFaction("star_federation");
        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI diktat = sector.getFaction(Factions.DIKTAT);
        FactionAPI persean = sector.getFaction(Factions.PERSEAN);
        FactionAPI guard = sector.getFaction(Factions.LIONS_GUARD);
        FactionAPI remnants = sector.getFaction(Factions.REMNANTS);
        FactionAPI derelicts = sector.getFaction(Factions.DERELICT);

        star_federation.setRelationship(player.getId(), 0.1f);
        star_federation.setRelationship(hegemony.getId(), -0.1f);
        star_federation.setRelationship(tritachyon.getId(), 0.15f);
        star_federation.setRelationship(pirates.getId(), -0.5f);
        star_federation.setRelationship(independent.getId(), 0.5f);
        star_federation.setRelationship(persean.getId(), 0.2f);
        star_federation.setRelationship(church.getId(), -0.2f);
        star_federation.setRelationship(path.getId(), -0.5f);
        star_federation.setRelationship(kol.getId(), -0.25f);
        star_federation.setRelationship(diktat.getId(), -0.2f);
        star_federation.setRelationship(guard.getId(), -0.3f);
        star_federation.setRelationship(remnants.getId(), -0.5f);
        star_federation.setRelationship(derelicts.getId(), -0.5f);

        //modded factions
        star_federation.setRelationship("SCY", RepLevel.SUSPICIOUS);
        star_federation.setRelationship("shadow_industry", RepLevel.WELCOMING);
        star_federation.setRelationship("syndicate_asp", RepLevel.WELCOMING);

        star_federation.setRelationship("citadeldefenders", RepLevel.FAVORABLE);
        star_federation.setRelationship("tiandong", RepLevel.NEUTRAL);
        star_federation.setRelationship("metelson", RepLevel.FAVORABLE);
        star_federation.setRelationship("Coalition", RepLevel.FAVORABLE);

        star_federation.setRelationship("sun_ice", RepLevel.NEUTRAL);
        star_federation.setRelationship("pn_colony", RepLevel.NEUTRAL);
        star_federation.setRelationship("neutrinocorp", RepLevel.NEUTRAL);
        star_federation.setRelationship("blackrock_driveyards", RepLevel.NEUTRAL);

        star_federation.setRelationship("dassault_mikoyan", RepLevel.SUSPICIOUS);
        star_federation.setRelationship("interstellarimperium", RepLevel.INHOSPITABLE);
        star_federation.setRelationship("apex_design", RepLevel.NEUTRAL);

        star_federation.setRelationship("pack", RepLevel.INHOSPITABLE);
        star_federation.setRelationship("6eme_bureau", RepLevel.INHOSPITABLE);

        star_federation.setRelationship("diableavionics", RepLevel.HOSTILE);
        star_federation.setRelationship("maystar_federationte", RepLevel.HOSTILE);
        star_federation.setRelationship("pirateAnar", RepLevel.HOSTILE);
        star_federation.setRelationship("sun_ici", RepLevel.HOSTILE);
        star_federation.setRelationship("junk_pirates", RepLevel.HOSTILE);
        star_federation.setRelationship("exigency", RepLevel.HOSTILE);
        star_federation.setRelationship("exipirated", RepLevel.HOSTILE);
        star_federation.setRelationship("cabal", RepLevel.HOSTILE);
        star_federation.setRelationship("the_deserter", RepLevel.HOSTILE);
        star_federation.setRelationship("blade_breakers", RepLevel.HOSTILE);

        star_federation.setRelationship("crystanite", RepLevel.VENGEFUL);
        star_federation.setRelationship("new_galactic_order", RepLevel.VENGEFUL);
        star_federation.setRelationship("explorer_society", RepLevel.VENGEFUL);

        star_federation.setRelationship("noir", RepLevel.NEUTRAL);
        star_federation.setRelationship("Lte", RepLevel.NEUTRAL);
        star_federation.setRelationship("GKSec", RepLevel.NEUTRAL);
        star_federation.setRelationship("gmda", RepLevel.SUSPICIOUS);
        star_federation.setRelationship("oculus", RepLevel.NEUTRAL);
        star_federation.setRelationship("nomads", RepLevel.NEUTRAL);
        star_federation.setRelationship("thulelegacy", RepLevel.NEUTRAL);
        star_federation.setRelationship("infected", RepLevel.NEUTRAL);
    }

    @Override
    public void generate(SectorAPI sector) {
        new Fed_Octavia().generate(sector);
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("star_federation");
        initFactionRelationships(sector);
    }

    public static void createInitialPeople() {
        boolean isNexRandomMode = false;
        boolean hasNex = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (hasNex) {
            isNexRandomMode = !SectorManager.getManager().isCorvusMode();
        }
        if (isNexRandomMode) {
            return;
        }
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();

        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (!market.getFactionId().equals("star_federation")) {
                continue;
            }
            if (market.getMemoryWithoutUpdate().getBoolean(MemFlags.MARKET_DO_NOT_INIT_COMM_LISTINGS)) {
                continue;
            }
            boolean addedPerson = false;

            PersonAPI admin = null;

            LinkedHashSet<PersonAPI> randomPeople = new LinkedHashSet<>();

            if (market.hasIndustry(Industries.MILITARYBASE) || market.hasIndustry(Industries.HIGHCOMMAND)) {
                PersonAPI person = market.getFaction().createRandomPerson(StarSystemGenerator.random);
                String rankId = Ranks.GROUND_MAJOR;
                if (market.getSize() >= 6) {
                    rankId = Ranks.GROUND_GENERAL;
                } else if (market.getSize() >= 4) {
                    rankId = Ranks.GROUND_COLONEL;
                }
                person.setRankId(rankId);
                person.setPostId(Ranks.POST_BASE_COMMANDER);
                if (market.getSize() >= 8) {
                    person.setImportanceAndVoice(PersonImportance.VERY_HIGH, StarSystemGenerator.random);
                } else if (market.getSize() >= 6) {
                    person.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);
                } else {
                    person.setImportanceAndVoice(PersonImportance.MEDIUM, StarSystemGenerator.random);
                }

                market.getCommDirectory().addPerson(person);
                market.addPerson(person);
                ip.addPerson(person);
                ip.getData(person).getLocation().setMarket(market);
                ip.checkOutPerson(person, "permanent_staff");
                addedPerson = true;
                randomPeople.add(person);
            }

            boolean hasStation = false;
            for (Industry curr : market.getIndustries()) {
                if (curr.getSpec().hasTag(Industries.TAG_STATION)) {
                    hasStation = true;
                    break;
                }
            }
            if (hasStation) {
                PersonAPI person = market.getFaction().createRandomPerson(StarSystemGenerator.random);
                String rankId = Ranks.SPACE_COMMANDER;
                if (market.getSize() >= 6) {
                    rankId = Ranks.SPACE_ADMIRAL;
                } else if (market.getSize() >= 4) {
                    rankId = Ranks.SPACE_CAPTAIN;
                }
                person.setRankId(rankId);
                person.setPostId(Ranks.POST_STATION_COMMANDER);

                if (market.getSize() >= 8) {
                    person.setImportanceAndVoice(PersonImportance.VERY_HIGH, StarSystemGenerator.random);
                } else if (market.getSize() >= 6) {
                    person.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);
                } else {
                    person.setImportanceAndVoice(PersonImportance.MEDIUM, StarSystemGenerator.random);
                }

                market.getCommDirectory().addPerson(person);
                market.addPerson(person);
                ip.addPerson(person);
                ip.getData(person).getLocation().setMarket(market);
                ip.checkOutPerson(person, "permanent_staff");
                addedPerson = true;
                randomPeople.add(person);

                if (market.getPrimaryEntity().hasTag(Tags.STATION)) {
                    admin = person;
                }
            }

            if (market.hasSpaceport()) {
                PersonAPI person = market.getFaction().createRandomPerson(StarSystemGenerator.random);
                //person.setRankId(Ranks.SPACE_CAPTAIN);
                person.setPostId(Ranks.POST_PORTMASTER);

                if (market.getSize() >= 8) {
                    person.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);
                } else if (market.getSize() >= 6) {
                    person.setImportanceAndVoice(PersonImportance.MEDIUM, StarSystemGenerator.random);
                } else if (market.getSize() >= 4) {
                    person.setImportanceAndVoice(PersonImportance.LOW, StarSystemGenerator.random);
                } else {
                    person.setImportanceAndVoice(PersonImportance.VERY_LOW, StarSystemGenerator.random);
                }

                market.getCommDirectory().addPerson(person);
                market.addPerson(person);
                ip.addPerson(person);
                ip.getData(person).getLocation().setMarket(market);
                ip.checkOutPerson(person, "permanent_staff");
                addedPerson = true;
                randomPeople.add(person);
            }

            if (addedPerson) {
                PersonAPI person = market.getFaction().createRandomPerson(StarSystemGenerator.random);
                person.setRankId(Ranks.SPACE_COMMANDER);
                person.setPostId(Ranks.POST_SUPPLY_OFFICER);

                if (market.getSize() >= 6) {
                    person.setImportanceAndVoice(PersonImportance.MEDIUM, StarSystemGenerator.random);
                } else if (market.getSize() >= 4) {
                    person.setImportanceAndVoice(PersonImportance.LOW, StarSystemGenerator.random);
                } else {
                    person.setImportanceAndVoice(PersonImportance.VERY_LOW, StarSystemGenerator.random);
                }

                market.getCommDirectory().addPerson(person);
                market.addPerson(person);
                ip.addPerson(person);
                ip.getData(person).getLocation().setMarket(market);
                ip.checkOutPerson(person, "permanent_staff");
                addedPerson = true;
                randomPeople.add(person);
            }

            if (!addedPerson || admin == null) {
                PersonAPI person = market.getFaction().createRandomPerson(StarSystemGenerator.random);
                person.setRankId(Ranks.CITIZEN);
                person.setPostId(Ranks.POST_ADMINISTRATOR);

                if (market.getSize() >= 8) {
                    person.setImportanceAndVoice(PersonImportance.VERY_HIGH, StarSystemGenerator.random);
                } else if (market.getSize() >= 6) {
                    person.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);
                } else {
                    person.setImportanceAndVoice(PersonImportance.MEDIUM, StarSystemGenerator.random);
                }

                market.getCommDirectory().addPerson(person);
                market.addPerson(person);
                ip.addPerson(person);
                ip.getData(person).getLocation().setMarket(market);
                ip.checkOutPerson(person, "permanent_staff");
                admin = person;
                randomPeople.add(person);
            }
            market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            for(MarketConditionAPI conds : market.getConditions()){
                conds.setSurveyed(true);
            }

            if (admin != null) {
                addSkillsAndAssignAdmin(market, admin);
            }

            List<PersonAPI> people = new ArrayList<>(randomPeople);
            Iterator<PersonAPI> iter = people.iterator();
            while (iter.hasNext()) {
                PersonAPI curr = iter.next();
                if (curr == null || curr.getFaction() == null) {
                    iter.remove();
                    continue;
                }
                if (curr.isDefault() || curr.isAICore() || curr.isPlayer()) {
                    iter.remove();
                    continue;
                }
            }
            dedupePortraits(people);
        }
    }

    private static void addSkillsAndAssignAdmin(MarketAPI market, PersonAPI admin) {
        List<String> skills = Global.getSettings().getSortedSkillIds();

        if (!skills.contains(Skills.INDUSTRIAL_PLANNING)) {
            return;
        }

        int size = market.getSize();
        if (size <= 4) {
            return;
        }

        int industries = 0;

        for (Industry curr : market.getIndustries()) {
            if (curr.isIndustry()) {
                industries++;
            }
        }

        admin.getStats().setSkipRefresh(true);

        if (industries >= 2 || size >= 6) {
            admin.getStats().setSkillLevel(Skills.INDUSTRIAL_PLANNING, 1);
        }

        admin.getStats().setSkipRefresh(false);
        admin.getStats().refreshCharacterStatsEffects();

        market.setAdmin(admin);
    }

}
