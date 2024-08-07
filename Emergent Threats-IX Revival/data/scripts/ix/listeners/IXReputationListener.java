package data.scripts.ix.listeners;

import java.util.List;
import lunalib.lunaSettings.LunaSettings;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.util.Misc;

import data.scripts.ix.NameListUtil;

public class IXReputationListener extends BaseCampaignEventListener {
	
	public IXReputationListener() {
		super(true);
	}
	
	@Override
	public void reportPlayerOpenedMarket(MarketAPI market) {
		SectorAPI sector = Global.getSector();
		/*
		if (sector.getMemoryWithoutUpdate().is("$gavePKtoIX", true)) {
			FactionAPI f = sector.getFaction(faction);
			if (f == null) return;
			Map<String, MutableStat> maxTable = (Map<String, MutableStat>) f.getMemoryWithoutUpdate().get("$nex_max_relations");
			if (maxTable == null) return;
			if (maxTable <= 0) return;
			//do reputation stuff
		}
		*/
		
		//String factionId = sector.getPlayerFaction().getId();
		//if (Misc.getCommissionFactionId() != null) factionId = Misc.getCommissionFactionId();
		
		if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
			if (!LunaSettings.getBoolean("EmergentThreats_IX_Revival", "ix_brighton_neutral")) {
				FactionAPI ix = sector.getFaction("ix_battlegroup");
				if (ix.getRelationship("brighton") > -0.50f) ix.setRelationship("brighton", -0.50f);
			}
		}
		if (market.hasIndustry("ix_marzanna_base")) {
			if (market.getFactionId().equals("ix_battlegroup")) market.getPlanetEntity().setFaction("ix_marzanna");
			for (int i = 0; i < market.getPeopleCopy().size(); i++) {
				PersonAPI p = (PersonAPI) market.getPeopleCopy().get(i);
				if (p.getFaction().getId().equals("ix_battlegroup")) p.setFaction("ix_marzanna");
			}
		}
	}
	
	@Override
	public void reportPlayerReputationChange(String faction, float delta) {
		SectorAPI sector = Global.getSector();
		
		if (faction.equals("ix_battlegroup") && sector.getFaction("ix_marzanna") != null) {
			float ixRep = sector.getFaction("ix_battlegroup").getRelToPlayer().getRel();
			sector.getFaction("ix_marzanna").getRelToPlayer().setRel(ixRep);
		}
		else if (faction.equals("ix_marzanna") && sector.getFaction("ix_battlegroup") != null) {
			float marRep = sector.getFaction("ix_marzanna").getRelToPlayer().getRel();
			sector.getFaction("ix_battlegroup").getRelToPlayer().setRel(marRep);
		}
		
		//Give ship for turning in pk
		if (faction.equals("ix_battlegroup")) {
			if (sector.getPlayerMemoryWithoutUpdate().is("$receivedHyperionIX", true)) {
				giveHyperionIX();
				sector.getPlayerMemoryWithoutUpdate().set("$receivedHyperionIX", false);
			}
			if (sector.getPlayerMemoryWithoutUpdate().is("$receivedRadiantIX", true)) {
				giveRadiantIX();
				sector.getPlayerMemoryWithoutUpdate().set("$receivedRadiantIX", false);
			}
		}
	}
	
	private void giveHyperionIX() {
		ShipVariantAPI v = Global.getSettings().getVariant("hyperion_ix_special").clone();
		FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, v);
		member.setShipName(NameListUtil.HGS_Judicator);
		Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);
	}
	
	private void giveRadiantIX() {
		ShipVariantAPI v = Global.getSettings().getVariant("radiant_ix_custom_2").clone();
		FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, v);
		member.setShipName(NameListUtil.HGS_Judicator);
		Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);
	}
	
	public static void setIXHostileToAll() {
		//runs once
		if (Global.getSector().getPlayerMemoryWithoutUpdate().is("$reputationIsSetIX", true)) return;
		SectorAPI sector = Global.getSector();
		FactionAPI ix = sector.getFaction("ix_battlegroup");
		FactionAPI hg = sector.getFaction("ix_core");
		FactionAPI marzanna = sector.getFaction("ix_marzanna");
		FactionAPI hvb = sector.getFaction("ix_remnant_hvb");
		FactionAPI exo = sector.getFaction("rat_exotech");
		FactionAPI bw = sector.getFaction("tahlan_legioelite");
		FactionAPI player = sector.getFaction("player");
		FactionAPI pirates = sector.getFaction(Factions.PIRATES);
		FactionAPI indep = sector.getFaction(Factions.INDEPENDENT);
		FactionAPI brighton = sector.getFaction("brighton");
		
		List<FactionAPI> factionList = sector.getAllFactions();
		factionList.remove(ix);
		factionList.remove(hg);
		factionList.remove(marzanna);
		factionList.remove(hvb);
		factionList.remove(player);
		factionList.remove(exo);
		factionList.remove(bw);
		factionList.remove(pirates);
		factionList.remove(indep);
		
		if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
			if (LunaSettings.getBoolean("EmergentThreats_IX_Revival", "ix_brighton_neutral")) {
				ix.setRelationship("brighton", 0f);
				factionList.remove(brighton);
			}
			else ix.setRelationship("brighton", -0.50f);
		}
		
		for (FactionAPI faction : factionList) {
			if (faction != null && ix.getRelationship(faction.getId()) > -0.50f) {
				ix.setRelationship(faction.getId(), -0.50f);
				if (ix.getRelationship("player") > 0.51) {
					player.setRelationship(faction.getId(), -0.50f);
				}
			}
			marzanna.setRelationship(faction.getId(), ix.getRelationship(faction.getId()));
			hvb.setRelationship(faction.getId(), -1f);
		}
		
		hvb.setRelationship(ix.getId(), -1f);
		hvb.setRelationship(player.getId(), -1f);
		Global.getSector().getPlayerMemoryWithoutUpdate().set("$reputationIsSetIX", true);
	}
}