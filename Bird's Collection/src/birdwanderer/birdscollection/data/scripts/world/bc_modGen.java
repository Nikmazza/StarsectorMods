package birdwanderer.birdscollection.data.scripts.world;

import birdwanderer.birdscollection.data.scripts.world.systems.bc_zagan;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import birdwanderer.birdscollection.data.scripts.world.systems.bc_deixis;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import lunalib.lunaSettings.LunaSettings;

public class bc_modGen {

    public static void initFactionRelationships(SectorAPI sector) {

        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI persean = sector.getFaction(Factions.PERSEAN);
        FactionAPI diktat = sector.getFaction(Factions.DIKTAT);
        FactionAPI faysco = sector.getFaction("faysco");


        faysco.setRelationship(path.getId(), RepLevel.VENGEFUL);
        faysco.setRelationship(hegemony.getId(), RepLevel.INHOSPITABLE);
        faysco.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        faysco.setRelationship(tritachyon.getId(), RepLevel.HOSTILE);
        faysco.setRelationship(church.getId(), RepLevel.INHOSPITABLE);
        faysco.setRelationship(persean.getId(), RepLevel.WELCOMING);
        faysco.setRelationship(diktat.getId(), RepLevel.FAVORABLE);

    }

    public void generate(SectorAPI sector) {

        boolean hasFaysco = Boolean.TRUE.equals(LunaSettings.getBoolean("bc", "bc_enable_faysco"));
        FactionAPI faysco = sector.getFaction("faysco");

        if (hasFaysco) {
            faysco.setShowInIntelTab(true);
            SharedData.getData().getPersonBountyEventData().addParticipatingFaction("faysco");

            initFactionRelationships(sector);

            new bc_deixis().generate(sector);
            new bc_zagan().generate(sector);

        }
    }
}