package data.scripts.vice;

import java.util.List;
import lunalib.lunaRefit.LunaRefitManager;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.listeners.BountyListener;
import data.scripts.vice.listeners.EnemyEncounterListener;
import data.scripts.vice.listeners.ETReputationListener;
import data.scripts.vice.listeners.PruneBantengMarketListener;
import data.scripts.vice.luna.AutomateHVBRadiantButton;
import data.scripts.vice.luna.BiochipAICommanderButton;
import data.scripts.vice.luna.RemoveMissionDeployHullmod;
import data.scripts.vice.luna.SignalMaskerInstallButton;
import data.scripts.vice.luna.SignalMaskerRemoveButton;
import data.scripts.vice.XOCampaignPlugin;

public class ETModPlugin extends BaseModPlugin {
	
	private static XOCampaignPlugin synthesisCorePlugin = new XOCampaignPlugin();
	
	@Override
	public void onGameLoad(boolean newGame) {
		SectorAPI sector = Global.getSector();
		sector.registerPlugin(synthesisCorePlugin);
		sector.getFaction("sindrian_diktat").getKnownFighters().remove("talon_wing");
		sector.getFaction("lions_guard").getKnownFighters().remove("talon_wing");
		
		FactionAPI diamond_nexus = sector.getFaction("diamond_nexus");
		diamond_nexus.setRelationship("remnant", RepLevel.NEUTRAL);
		diamond_nexus.setRelationship("independent", RepLevel.NEUTRAL);
		diamond_nexus.setRelationship("player", RepLevel.HOSTILE);
		
		ImportantPeopleAPI people = Global.getSector().getImportantPeople();
		PersonAPI person = Global.getFactory().createPerson();
		person.setId("vice_taylor_sheasby");
		person.setFaction("persean");
		person.setGender(FullName.Gender.MALE);
		person.setRankId("specialAgent");
		person.setPostId("investigator");
		person.setImportance(PersonImportance.VERY_HIGH);
		person.getName().setFirst("Taylor");
		person.getName().setLast("Sheasby");
		person.setPortraitSprite(Global.getSettings().getSpriteName("portraits", "vice_taylor_sheasby"));
		if (!people.containsPerson(person)) people.addPerson(person);
		
		//backwards compatability, if player is continuing a pre v0.9.5 game
		if (!sector.getMemoryWithoutUpdate().is("$bounty_listener_set", true)) {
			if (sector.getMemoryWithoutUpdate().is("$vice_project_mayfly_succeeded", true)
				|| sector.getMemoryWithoutUpdate().is("$vice_project_mayfly_failed", true)) return;
			else {
				sector.getListenerManager().addListener(new BountyListener());
				sector.getMemoryWithoutUpdate().set("$bounty_listener_set", true);
			}
		}
		
		//obselete
		//sector.getMemoryWithoutUpdate().set("$mission_picker_tri_tachyon", true);
		//sector.getMemoryWithoutUpdate().set("$mission_picker_cabal", true);
		//sector.getMemoryWithoutUpdate().set("$mission_picker_tri_tachyon_expired", false);
		//sector.getMemoryWithoutUpdate().set("$mission_picker_cabal_expired", false);
	}
	
	@Override
	public void onNewGameAfterTimePass() {
		SectorAPI sector = Global.getSector();
		PruneBantengMarketListener pListener = new PruneBantengMarketListener();
		for (MarketAPI market : sector.getEconomy().getMarketsCopy()) {
			pListener.pruneMarket(market);
		}
	}
	
	@Override
	public void beforeGameSave() {
		//give Sindrian Diktat commissioned players new hullmods
		if (Global.getSector().getMemoryWithoutUpdate().is("$give_diktat_hullmods", false)) return;
		String commissionID = Misc.getCommissionFactionId();
		if (commissionID == null) commissionID = "";
		if (commissionID.equals("sindrian_diktat")) {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			cargo.addSpecial(new SpecialItemData("LG_bp_package", ""), 1);
			cargo.addHullmods("vice_attuned_emitter_diodes", 1);
			cargo.addHullmods("vice_convert_shuttle", 1);
			cargo.addHullmods("vice_modular_bolt_coherer", 1);
			cargo.addHullmods("vice_modular_fleet_override", 1);
		}
		Global.getSector().getMemoryWithoutUpdate().set("$give_diktat_hullmods", false);
	}
	
	@Override
    public void onNewGame() {
		SectorAPI sector = Global.getSector();
		sector.registerPlugin(synthesisCorePlugin);
		setRelationships(sector);
		sector.getMemoryWithoutUpdate().set("$give_diktat_hullmods", true);
		sector.getListenerManager().addListener(new EnemyEncounterListener());
		sector.getListenerManager().addListener(new ETReputationListener());
		sector.getListenerManager().addListener(new PruneBantengMarketListener());
		//sector.getListenerManager().addListener(new BountyListener());
		//sector.getMemoryWithoutUpdate().set("$bounty_listener_set", true);
		sector.getFaction("sindrian_diktat").getKnownFighters().remove("talon_wing");
		sector.getFaction("lions_guard").getKnownFighters().remove("talon_wing");
	}
	
	private static void setRelationships(SectorAPI sector) {
		FactionAPI diamond_nexus = sector.getFaction("diamond_nexus");
		FactionAPI vantage_group = sector.getFaction("vantage_group");
		FactionAPI vice_diktat_navy = sector.getFaction("vice_diktat_navy");
		FactionAPI vice_lions_guard = sector.getFaction("vice_lions_guard"); //LG 1st Division
		
		List<FactionAPI> factions = sector.getAllFactions();
		
		for (FactionAPI faction : factions) {
            if (faction.isNeutralFaction()) continue;
            diamond_nexus.setRelationship(faction.getId(), RepLevel.VENGEFUL);
			vantage_group.setRelationship(faction.getId(), RepLevel.HOSTILE);
			vice_diktat_navy.setRelationship(faction.getId(), RepLevel.HOSTILE);
			vice_lions_guard.setRelationship(faction.getId(), RepLevel.HOSTILE);
        }
		diamond_nexus.setRelationship("remnant", RepLevel.NEUTRAL);
		diamond_nexus.setRelationship("independent", RepLevel.NEUTRAL);
		diamond_nexus.setRelationship("diamond_nexus", 1f);
		vantage_group.setRelationship("vantage_group", 1f);
		vice_diktat_navy.setRelationship("vice_diktat_navy", 1f);
		vice_lions_guard.setRelationship("vice_lions_guard", 1f);
		vice_lions_guard.setRelationship("vantage_group", RepLevel.NEUTRAL); //prevents both bounties killing each other
	}
	
	@Override
	public void onApplicationLoad() {
		LunaRefitManager.addRefitButton(new AutomateHVBRadiantButton());
		LunaRefitManager.addRefitButton(new BiochipAICommanderButton());
		LunaRefitManager.addRefitButton(new RemoveMissionDeployHullmod());
		LunaRefitManager.addRefitButton(new SignalMaskerInstallButton());
		LunaRefitManager.addRefitButton(new SignalMaskerRemoveButton());
	}
}