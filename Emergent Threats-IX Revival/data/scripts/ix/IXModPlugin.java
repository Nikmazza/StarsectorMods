package data.scripts.ix;

import java.util.List;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;


import data.scripts.ix.IXSystemCreation;

public class IXModPlugin extends BaseModPlugin implements SectorGeneratorPlugin {

	private static String IX_SKILL_ID = "ix_sword_of_the_fleet";
	
	@Override
    public void onNewGame() {
        initializeFaction(Global.getSector());
    }
	
	@Override
	public void beforeGameSave() {
		//hack to see if the player is starting in IX Battlegroup faction from nex
		if (Global.getSector().getMemoryWithoutUpdate().is("$give_IX_hullmods", false)) return;

		List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getMembersWithFightersCopy();
		boolean isIX = true;
		boolean isHonorGuard = false;
		FactionAPI ix =  Global.getSector().getFaction("ix_battlegroup");
		if (ix.getRelationship("player") < 0) isIX = false;
		for (FleetMemberAPI member : fleetList) {
			if (!isIX) continue;
			if (!member.getVariant().isFighter() 
					&& (!member.getShipName().startsWith("DSS") 
					&& !member.getShipName().startsWith("HGS"))) isIX = false;
			String id = member.getVariant().getHullVariantId();
			if (id.equals("hyperion_ix_custom") 
					|| id.equals("temblor_ix_custom") 
					|| id.equals("tigershark_ix_custom")
					|| id.equals("odyssey_ix_custom")) isHonorGuard = true;
		}
		if (isIX) {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			cargo.addHullmods("ix_ground_invasion_conversion", 1);
			cargo.addHullmods("ix_laser_collimator", 1);
			cargo.addHullmods("ix_reactive_combat_shields", 1);
			cargo.addHullmods("ix_terminus_relay", 1);
			if (Global.getSettings().getModManager().isModEnabled("EmergentThreats_Vice")){
				cargo.addHullmods("vice_interdiction_array", 1);
				cargo.addHullmods("vice_adaptive_flux_dissipator", 1);
			}
			if (isHonorGuard) {
				cargo.addHullmods("ix_antecedent", 1);
				cargo.addSpecial(new SpecialItemData("ix_core_bp_package", ""), 1);
			}
			MutableCharacterStatsAPI stats = Global.getSector().getPlayerPerson().getStats();
			if (!stats.hasSkill(IX_SKILL_ID)) stats.setSkillLevel(IX_SKILL_ID, 2f);
		}
		Global.getSector().getMemoryWithoutUpdate().set("$give_IX_hullmods", false);
	}
	
	private void initializeFaction(SectorAPI sector) {
		generate(sector);
		Global.getSector().getMemoryWithoutUpdate().set("$give_IX_hullmods", true);
        Global.getSector().getFaction("ix_battlegroup").setShowInIntelTab(true);
        FactionAPI ix = sector.getFaction("ix_battlegroup");
		
        List<FactionAPI> factionList = sector.getAllFactions();
        factionList.remove(ix);
        for (FactionAPI faction : factionList) {
            ix.setRelationship(faction.getId(), -0.50f);
        }
        ix.setRelationship("player", -0.50f);
		ix.setRelationship("ix_core", 1f);
		ix.setRelationship(Factions.INDEPENDENT, 0f);
		ix.setRelationship(Factions.PIRATES, 0f);
	}
	
	@Override
	public void onGameLoad(boolean newGame) {
		FactionAPI pirates = Global.getSector().getFaction(Factions.PIRATES);
		FactionAPI ix_battlegroup = Global.getSector().getFaction("ix_battlegroup");
		FactionAPI ix_honor_guard = Global.getSector().getFaction("ix_core");
		pirates.removeKnownWeapon("stormlight_graser_ix");
		ix_battlegroup.getKnownFighters().remove("talon_wing");
		ix_honor_guard.getKnownFighters().remove("talon_wing");
	}
	
	@Override
	public void generate(SectorAPI sector) {
		IXSystemCreation.generate(sector);
	}
}