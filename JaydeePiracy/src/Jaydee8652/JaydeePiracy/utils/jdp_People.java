package Jaydee8652.JaydeePiracy.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.missions.RecoverAPlanetkiller;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import org.apache.log4j.Logger;
import Jaydee8652.JaydeePiracy.utils.jdp_Ranks;

import static com.fs.starfarer.api.impl.campaign.ids.People.createFactionLeaders;

public class jdp_People {
	public static String JDP_CALHOUM = "jdp_calhoum";
	public static String JDP_OMEGA = "jdp_omega";
	public static String JDP_PENDRAGON = "jdp_pendragon";
	public static String JDP_PENDRAGONSICK = "jdp_pendragon2";
	public static String JDP_PENDRAGONASCEND = "jdp_pendragon3";
	public static String JDP_DEADCOMM = "jdp_deadcomm";
	public static String JDP_PAOLO = "jdp_paolo";
	public static String JDP_MAERULA = "jdp_maerula";
	public static String JDP_WASABI = "jdp_wasabi";
	public static String JDP_KABAYAKI = "jdp_kabayaki";


	public static Logger log = Global.getLogger(jdp_People.class);

	public static PersonAPI getPerson(String id) {
		return Global.getSector().getImportantPeople().getPerson(id);
	}

	public static void jdp_createMiscCharacters() {
		ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
		MarketAPI market = null;

		//They're not actually on Jangala in universe, its just convenient for testing for them to be next to spawn.
		market = Global.getSector().getEconomy().getMarket("jangala");
		if (market != null) {

			//Hayes Calhoum - Base Commander of the Interfector Launch Site
			PersonAPI jdp_calhoum_person = Global.getFactory().createPerson();
			jdp_calhoum_person.setId(JDP_CALHOUM);
			jdp_calhoum_person.setFaction(Factions.PLAYER);
			jdp_calhoum_person.setGender(Gender.MALE);
			jdp_calhoum_person.setRankId(Ranks.GROUND_COLONEL);
			jdp_calhoum_person.setPostId(Ranks.POST_BASE_COMMANDER);
			jdp_calhoum_person.setImportance(PersonImportance.HIGH);
			jdp_calhoum_person.getName().setFirst("Hayes");
			jdp_calhoum_person.getName().setLast("Calhoum");
			jdp_calhoum_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_calhoum"));
			if (!ip.containsPerson(jdp_calhoum_person)) {
				log.info("JDP_RETROGEN: Hayes Calhoum Person did not exist. He has been generated retroactively");
				ip.addPerson(jdp_calhoum_person);
				market.addPerson(jdp_calhoum_person);
			} else
				log.info("JDP_RETROGEN: Hayes Calhoum Person already exists. No action taken");

			//*Finger* Prismata - Not *many bubbles*, nor *light relfections*.
			PersonAPI jdp_omega_person = Global.getFactory().createPerson();
			jdp_omega_person.setId(JDP_OMEGA);
			jdp_omega_person.setFaction(Factions.OMEGA);
			jdp_omega_person.setGender(Gender.ANY);
			jdp_omega_person.setRankId(jdp_Ranks.JDP_DANCER);
			jdp_omega_person.setPostId(jdp_Ranks.POST_JDP_GAMEMASTER);
			jdp_omega_person.setImportance(PersonImportance.HIGH);
			jdp_omega_person.getName().setFirst("*Finger*");
			jdp_omega_person.getName().setLast("Prismata");
			jdp_omega_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_omega"));
			if (!ip.containsPerson(jdp_omega_person)) {
				log.info("JDP_RETROGEN: *Finger* Prismata Person did not exist. They has been generated retroactively");
				ip.addPerson(jdp_omega_person);
				market.addPerson(jdp_omega_person);
			} else
				log.info("JDP_RETROGEN: *Finger* Prismata Person already exists. No action taken");

			//Ochre Pendragon - The object of your quest
			PersonAPI jdp_pendragon_person = Global.getFactory().createPerson();
			jdp_pendragon_person.setId(JDP_PENDRAGON);
			jdp_pendragon_person.setFaction(Factions.REMNANTS);
			jdp_pendragon_person.setGender(Gender.ANY);
			jdp_pendragon_person.setRankId(jdp_Ranks.JDP_CAPTIVE);
			jdp_pendragon_person.setPostId(jdp_Ranks.POST_JDP_BLANK);
			jdp_pendragon_person.setImportance(PersonImportance.HIGH);
			jdp_pendragon_person.getName().setFirst("Ochre");
			jdp_pendragon_person.getName().setLast("Pendragon");
			jdp_pendragon_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_pendragon"));
			if (!ip.containsPerson(jdp_pendragon_person)) {
				log.info("JDP_RETROGEN: Ochre Pendragon Person did not exist. They has been generated retroactively");
				ip.addPerson(jdp_pendragon_person);
				market.addPerson(jdp_pendragon_person);
			} else
				log.info("JDP_RETROGEN: Ochre Pendragon Person already exists. No action taken");

			//Ochre Pendragon (Sick) - It is a good pain.
			PersonAPI jdp_pendragon2_person = Global.getFactory().createPerson();
			jdp_pendragon2_person.setId(JDP_PENDRAGONSICK);
			jdp_pendragon2_person.setFaction(Factions.REMNANTS);
			jdp_pendragon2_person.setGender(Gender.ANY);
			jdp_pendragon2_person.setRankId(jdp_Ranks.JDP_CAPTIVE);
			jdp_pendragon2_person.setPostId(jdp_Ranks.POST_JDP_BLANK);
			jdp_pendragon2_person.setImportance(PersonImportance.HIGH);
			jdp_pendragon2_person.getName().setFirst("Ochre");
			jdp_pendragon2_person.getName().setLast("Pendragon");
			jdp_pendragon2_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_pendragon2"));
			if (!ip.containsPerson(jdp_pendragon2_person)) {
				log.info("JDP_RETROGEN: Ochre Pendragon (Sick) Person did not exist. They has been generated retroactively");
				ip.addPerson(jdp_pendragon2_person);
				market.addPerson(jdp_pendragon2_person);
			} else
				log.info("JDP_RETROGEN: Ochre Pendragon (Sick) Person already exists. No action taken");

			//Ochre Pendragon (Ascendent) - He's gone to a better place. Maybe?
			PersonAPI jdp_pendragon3_person = Global.getFactory().createPerson();
			jdp_pendragon3_person.setId(JDP_PENDRAGONASCEND);
			jdp_pendragon3_person.setFaction(Factions.REMNANTS);
			jdp_pendragon3_person.setGender(Gender.ANY);
			jdp_pendragon3_person.setRankId(jdp_Ranks.JDP_CAPTIVE);
			jdp_pendragon3_person.setPostId(jdp_Ranks.POST_JDP_BLANK);
			jdp_pendragon3_person.setImportance(PersonImportance.HIGH);
			jdp_pendragon3_person.getName().setFirst("Ochre");
			jdp_pendragon3_person.getName().setLast("Pendragon");
			jdp_pendragon3_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_pendragon3"));
			if (!ip.containsPerson(jdp_pendragon3_person)) {
				log.info("JDP_RETROGEN: Ochre Pendragon (Ascendent) Person did not exist. They has been generated retroactively");
				ip.addPerson(jdp_pendragon3_person);
				market.addPerson(jdp_pendragon3_person);
			} else
				log.info("JDP_RETROGEN: Ochre Pendragon (Ascendent) Person already exists. No action taken");

			//Deadcomm - Not a person, just comms static.
			// Utility for when people hang up on you and I want it to have more impact.
			PersonAPI jdp_deadcomm_person = Global.getFactory().createPerson();
			jdp_deadcomm_person.setId(JDP_DEADCOMM);
			jdp_deadcomm_person.setFaction(jdp_Factions.JDP_DEADCOMM);
			jdp_deadcomm_person.setGender(Gender.ANY);
			jdp_deadcomm_person.setRankId(jdp_Ranks.JDP_DEAD);
			jdp_deadcomm_person.setPostId(jdp_Ranks.POST_JDP_ERROR);
			jdp_deadcomm_person.setImportance(PersonImportance.HIGH);
			jdp_deadcomm_person.getName().setFirst("ERROR: DATA INVALID");
			jdp_deadcomm_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_deadcomm"));
			if (!ip.containsPerson(jdp_deadcomm_person)) {
				log.info("JDP_RETROGEN: Deadcomm Person did not exist. It has been generated retroactively");
				ip.addPerson(jdp_deadcomm_person);
				market.addPerson(jdp_deadcomm_person);
			} else
				log.info("JDP_RETROGEN: Deadcomm Person already exists. No action taken");

			//Paolo Casiraghi - Galatia Domain History Lecturer
			PersonAPI jdp_paolo_person = Global.getFactory().createPerson();
			jdp_paolo_person.setId(JDP_PAOLO);
			jdp_paolo_person.setFaction(Factions.INDEPENDENT);
			jdp_paolo_person.setGender(Gender.MALE);
			jdp_paolo_person.setRankId(Ranks.CITIZEN);
			jdp_paolo_person.setPostId(Ranks.POST_ACADEMICIAN);
			jdp_paolo_person.getName().setFirst("Paolo");
			jdp_paolo_person.getName().setLast("Casiraghi");
			jdp_paolo_person.setImportance(PersonImportance.HIGH);
			jdp_paolo_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_paolo"));
			if (!ip.containsPerson(jdp_paolo_person)) {
				log.info("JDP_RETROGEN: Paolo Casiraghi Person did not exist. It has been generated retroactively");
				ip.addPerson(jdp_paolo_person);
				market.addPerson(jdp_paolo_person);
			} else
				log.info("JDP_RETROGEN: Paolo Casiraghi Person already exists. No action taken");
		}

		market = Global.getSector().getEconomy().getMarket("ailmar");
		if (market != null) {

			//Maerula - Flowerfish pseudo-Faction Lead
			PersonAPI jdp_maerula_person = Global.getFactory().createPerson();
			jdp_maerula_person.setId(JDP_MAERULA);
			jdp_maerula_person.setFaction(jdp_Factions.JDP_FLOWERFISH);
			jdp_maerula_person.setGender(Gender.ANY);
			jdp_maerula_person.setRankId(Ranks.SENIOR_EXECUTIVE);
			jdp_maerula_person.setPostId(Ranks.POST_HACKER);
			jdp_maerula_person.setImportance(PersonImportance.HIGH);
			jdp_maerula_person.getName().setFirst("Maerula");
			jdp_maerula_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_maerula"));
			if (!ip.containsPerson(jdp_maerula_person)) {
				log.info("JDP_RETROGEN: Maerula Person did not exist. He has been generated retroactively");
				ip.addPerson(jdp_maerula_person);
				market.addPerson(jdp_maerula_person);
				market.getCommDirectory().addPerson(jdp_maerula_person, 3);
				market.getCommDirectory().getEntryForPerson(jdp_maerula_person).setHidden(true);
				ip.addPerson(jdp_maerula_person);
			} else
				log.info("JDP_RETROGEN: Maerula Person already exists. No action taken");

			//Wasabi - Flowerfish "offical"-Faction Lead
			PersonAPI jdp_wasabi_person = Global.getFactory().createPerson();
			jdp_wasabi_person.setId(JDP_WASABI);
			jdp_wasabi_person.setFaction(jdp_Factions.JDP_FLOWERFISH);
			jdp_wasabi_person.setGender(Gender.FEMALE);
			jdp_wasabi_person.setRankId(Ranks.SPECIAL_AGENT);
			jdp_wasabi_person.setPostId(Ranks.POST_SECURITY_CHIEF);
			jdp_wasabi_person.setImportance(PersonImportance.HIGH);
			jdp_wasabi_person.getName().setFirst("\"Wasabi\"");
			jdp_wasabi_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_wasabi"));
			if (!ip.containsPerson(jdp_wasabi_person)) {
				log.info("JDP_RETROGEN: Wasabi Person did not exist. He has been generated retroactively");
				ip.addPerson(jdp_wasabi_person);
				market.addPerson(jdp_wasabi_person);
				market.getCommDirectory().addPerson(jdp_wasabi_person, 4);
				ip.addPerson(jdp_wasabi_person);
			} else
				log.info("JDP_RETROGEN: Wasabi Person already exists. No action taken");

		}

		market = Global.getSector().getEconomy().getMarket("fikenhild");
		if (market != null) {

			//Kabayaki - Flowerfish Faction representative in the Fikenheld court
			PersonAPI jdp_kabayaki_person = Global.getFactory().createPerson();
			jdp_kabayaki_person.setId(JDP_KABAYAKI);
			jdp_kabayaki_person.setFaction(jdp_Factions.JDP_FLOWERFISH);
			jdp_kabayaki_person.setGender(Gender.FEMALE);
			jdp_kabayaki_person.setRankId(Ranks.SPECIAL_AGENT);
			jdp_kabayaki_person.setPostId(Ranks.POST_SECURITY_CHIEF);
			jdp_kabayaki_person.setImportance(PersonImportance.HIGH);
			jdp_kabayaki_person.getName().setFirst("\"Kabayaki\"");
			jdp_kabayaki_person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "jdp_kabayaki"));
			if (!ip.containsPerson(jdp_kabayaki_person)) {
				log.info("JDP_RETROGEN: Kabayaki Person did not exist. He has been generated retroactively");
				ip.addPerson(jdp_kabayaki_person);
				market.addPerson(jdp_kabayaki_person);
				market.getCommDirectory().addPerson(jdp_kabayaki_person, 4);
				ip.addPerson(jdp_kabayaki_person);
			} else
				log.info("JDP_RETROGEN: Kabayaki Person already exists. No action taken");
		}
	}


}
