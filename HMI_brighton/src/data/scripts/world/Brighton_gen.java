package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.scripts.world.systems.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("unchecked")
    public class Brighton_gen implements SectorGeneratorPlugin {
    @Override
    public void generate(SectorAPI sector) {

        new HMI_dieman().generate(sector);

        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("brighton");

        FactionAPI brighton = sector.getFaction("brighton");
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
        FactionAPI domres = sector.getFaction("domres");

        brighton.setRelationship("player_npc", RepLevel.FRIENDLY);
        brighton.setRelationship(Factions.PLAYER, RepLevel.FRIENDLY);
        brighton.setRelationship(hegemony.getId(), 0f);
        brighton.setRelationship(tritachyon.getId(), -0.1f);
        brighton.setRelationship(pirates.getId(), -0.7f);
        brighton.setRelationship(independent.getId(), 0.6f);
        brighton.setRelationship(persean.getId(), 0.3f);
        brighton.setRelationship(church.getId(), 0.3f);
        brighton.setRelationship(path.getId(), -0.7f);
        brighton.setRelationship(kol.getId(), 0.3f);
        brighton.setRelationship(diktat.getId(), -0.2f);
        brighton.setRelationship("exigency", -0.50f);
        brighton.setRelationship("shadow_industry", 0.5f);
        brighton.setRelationship("mayorate", -0.4f);
        brighton.setRelationship("blackrock", 0f);
        brighton.setRelationship("tiandong", 0f);
        brighton.setRelationship("SCY", 0f);
        brighton.setRelationship("neutrinocorp", 0f);
        brighton.setRelationship("interstellarimperium", 0f);
        brighton.setRelationship("diableavionics", -0.4f);
        brighton.setRelationship("ora", 0.6f);
		
		
		List<FactionAPI> allFactions = sector.getAllFactions();
        for (FactionAPI curFaction : allFactions) {

            if (curFaction == domres || curFaction.isNeutralFaction()) {
                continue;
            }
            domres.setRelationship(curFaction.getId(), RepLevel.VENGEFUL);
		}
	}
}
