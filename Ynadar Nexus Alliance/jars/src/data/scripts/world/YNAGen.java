package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;

import data.scripts.world.systems.yna_Home;

public class YNAGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
        initFactionRelationships(sector);

        new yna_Home().generate(sector);
        
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("ynadar");   
    }

	public static void initFactionRelationships(SectorAPI sector) {
		FactionAPI YNA = sector.getFaction("ynadar");
		FactionAPI player = sector.getFaction(Factions.PLAYER);

		FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
		FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
		FactionAPI pirates = sector.getFaction(Factions.PIRATES);
		FactionAPI independent = sector.getFaction(Factions.INDEPENDENT);
		FactionAPI kol = sector.getFaction(Factions.KOL);
		FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
		FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
		FactionAPI diktat = sector.getFaction(Factions.DIKTAT);
		FactionAPI persean = sector.getFaction(Factions.PERSEAN);
		FactionAPI derelict = sector.getFaction(Factions.DERELICT);
		FactionAPI remnant = sector.getFaction(Factions.REMNANTS);
		FactionAPI omega = sector.getFaction(Factions.OMEGA);
		
        /*for (FactionAPI faction : sector.getAllFactions()) {
            if (faction != YNA) {
                YNA.setRelationship(faction.getId(), RepLevel.SUSPICIOUS);
            }
        }*/

	YNA.setRelationship(Factions.PLAYER, RepLevel.INHOSPITABLE);
	YNA.setRelationship(Factions.INDEPENDENT, RepLevel.FRIENDLY);
	YNA.setRelationship(Factions.HEGEMONY, RepLevel.NEUTRAL);
	YNA.setRelationship(Factions.TRITACHYON, RepLevel.INHOSPITABLE);
	YNA.setRelationship(Factions.KOL, RepLevel.WELCOMING);
	YNA.setRelationship(Factions.DIKTAT, RepLevel.INHOSPITABLE);
	YNA.setRelationship(Factions.PERSEAN, RepLevel.HOSTILE);
	YNA.setRelationship(Factions.LUDDIC_CHURCH, RepLevel.WELCOMING);
	YNA.setRelationship(Factions.LUDDIC_PATH, RepLevel.NEUTRAL);
        YNA.setRelationship(Factions.PIRATES, RepLevel.HOSTILE);
	YNA.setRelationship(Factions.DERELICT, RepLevel.HOSTILE);
	YNA.setRelationship(Factions.REMNANTS, RepLevel.HOSTILE);
	YNA.setRelationship(Factions.OMEGA, RepLevel.HOSTILE);
        
	YNA.setRelationship("ocua", RepLevel.FAVORABLE);
	YNA.setRelationship("xlu", RepLevel.NEUTRAL);
        
	player.setRelationship("yna", RepLevel.INHOSPITABLE);
	independent.setRelationship("yna", RepLevel.FRIENDLY);
	hegemony.setRelationship("yna", RepLevel.NEUTRAL);
	diktat.setRelationship("yna", RepLevel.INHOSPITABLE);
	tritachyon.setRelationship("yna", RepLevel.INHOSPITABLE);
	kol.setRelationship("yna", RepLevel.WELCOMING);
	persean.setRelationship("yna", RepLevel.HOSTILE);
	church.setRelationship("yna", RepLevel.WELCOMING);
	path.setRelationship("yna", RepLevel.NEUTRAL);
	pirates.setRelationship("yna", RepLevel.HOSTILE);
	derelict.setRelationship("yna", RepLevel.HOSTILE);
	remnant.setRelationship("yna", RepLevel.HOSTILE);
	omega.setRelationship("yna", RepLevel.VENGEFUL);
        
    }
}