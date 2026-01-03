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
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.RelicSystemCreation;
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

import data.scripts.orr.luna.CopyOldslaughtButton;
import data.scripts.orr.luna.InstallOnslaughtButton;
import data.scripts.orr.luna.SalvageOnslaughtButton;

public class ETModPlugin extends BaseModPlugin {
	
	private static XOCampaignPlugin synthesisCorePlugin = new XOCampaignPlugin();

	@Override
	public void onGameLoad(boolean newGame) {
		SectorAPI sector = Global.getSector();
		
		//remove after v1.2.0
		if (!sector.getMemoryWithoutUpdate().is("$asm_trinity_set", true)) {
			sector.getMemoryWithoutUpdate().set("$asm_trinity_set", true);
			sector.getMemoryWithoutUpdate().set("$asm_trinity_met", false);
			sector.getMemoryWithoutUpdate().set("$asm_trinity_odds", 0f);
		}		
		
		sector.registerPlugin(synthesisCorePlugin);
		sector.getFaction("sindrian_diktat").getKnownFighters().remove("talon_wing");
		sector.getFaction("lions_guard").getKnownFighters().remove("talon_wing");
		sector.getFaction("threat").getKnownWeapons().remove("asm_flechette");
		sector.getFaction("threat").getKnownWeapons().remove("asm_voltaic_multipulser");
		sector.getFaction("tritachyon").getKnownShips().remove("atlas");
		
		if (sector.getMemoryWithoutUpdate().is("$vice_project_mayfly_remnant_knows_ships", true)) {
			Global.getSector().getFaction("remnant").addKnownShip("vice_chevalier_rem", false);
			Global.getSector().getFaction("remnant").addKnownShip("vice_hemlock_rem", false);
		}
		
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
		
		PruneBantengMarketListener pListener = new PruneBantengMarketListener();
		for (MarketAPI market : sector.getEconomy().getMarketsCopy()) {
			pListener.pruneMarket(market);
		}
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
		generate(sector);
		sector.registerPlugin(synthesisCorePlugin);
		setRelationships(sector);
		sector.getMemoryWithoutUpdate().set("$give_diktat_hullmods", true);
		sector.getListenerManager().addListener(new EnemyEncounterListener());
		sector.getListenerManager().addListener(new ETReputationListener());
		sector.getListenerManager().addListener(new PruneBantengMarketListener());
		sector.getListenerManager().addListener(new BountyListener());
		sector.getMemoryWithoutUpdate().set("$bounty_listener_set", true);
		sector.getFaction("sindrian_diktat").getKnownFighters().remove("talon_wing");
		sector.getFaction("lions_guard").getKnownFighters().remove("talon_wing");
		
		sector.getMemoryWithoutUpdate().set("$asm_trinity_set", true); //remove after v1.2.0
		sector.getMemoryWithoutUpdate().set("$asm_trinity_met", false);
		sector.getMemoryWithoutUpdate().set("$asm_trinity_odds", 0f);
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
	public void generate(SectorAPI sector) {
		//if (sector.getMemoryWithoutUpdate().is("$asm_circumfix_is_spawned", true)) return;
		RelicSystemCreation.generate(sector);
		//sector.getMemoryWithoutUpdate().set("$asm_circumfix_is_spawned", true);
	}
	
	@Override
	public void onApplicationLoad() {
		/**
		if (Global.getSettings().getModManager().isModEnabled("vice_orr")) {
			throw new RuntimeException("Onslaught Radical Rearming has been integrated into Emergent Threats. Please disable ORR from your mod list and restart the game.");
		}
		**/
		LunaRefitManager.addRefitButton(new AutomateHVBRadiantButton());
		LunaRefitManager.addRefitButton(new BiochipAICommanderButton());
		LunaRefitManager.addRefitButton(new RemoveMissionDeployHullmod());
		LunaRefitManager.addRefitButton(new SignalMaskerInstallButton());
		LunaRefitManager.addRefitButton(new SignalMaskerRemoveButton());
		
		//ORR
		LunaRefitManager.addRefitButton(new CopyOldslaughtButton());
		LunaRefitManager.addRefitButton(new InstallOnslaughtButton());
		LunaRefitManager.addRefitButton(new SalvageOnslaughtButton());
	}
}