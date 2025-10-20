package data.scripts.vice.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.util.NameListUtil;
import org.magiclib.bounty.ActiveBounty;
import org.magiclib.bounty.MagicBountyCoordinator;

public class BountyListener extends BaseCampaignEventListener {
	
	public BountyListener() {
		super(true);
	}
	
	//order is backwards from mission chain so (bounty == null) does not kill method for later missions
	public void reportFleetDespawned(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {
		MemoryAPI mem = Global.getSector().getMemoryWithoutUpdate();
		//succeeded check for preventing double dipping for talking then fighting the fleet anyway
		if (mem.is("$vice_project_mayfly_despawn", true) 
					&& !mem.is("$vice_project_mayfly_succeeded", true)) {
			ActiveBounty b = MagicBountyCoordinator.getInstance().getActiveBounty("vice_project_mayfly");
			if (b == null) return;
			b.endBounty(new ActiveBounty.BountyResult.FailedSalvagedFlagship());
			//agreed to let Persean League handle it internally
			if (mem.is("$vice_mayfly_fis_left_alone", true) 
						&& !mem.is("$vice_project_mayfly_gave_item", true)) {
				Global.getSector().getPlayerFleet().getCargo().getCredits().add(100000f);
				Global.getSector().getCampaignUI().addMessage(NameListUtil.FLS_CREDIT_PAYMENT_SMALL, Misc.getPositiveHighlightColor());
				mem.set("$vice_project_mayfly_gave_item", true);
			}
			else if (mem.is("$vice_mayfly_fis_took_bribe", true)
						&& !mem.is("$vice_project_mayfly_gave_item", true)) {
				Global.getSector().getPlayerFleet().getCargo().getCredits().add(500000f);
				Global.getSector().getCampaignUI().addMessage(NameListUtil.FLS_CREDIT_PAYMENT_LARGE, Misc.getPositiveHighlightColor());
				mem.set("$vice_project_mayfly_gave_item", true);
			}
			else if (mem.is("$vice_project_mayfly_believed_kato", true)
						&& !mem.is("$vice_project_mayfly_gave_item", true)) {
				if (Global.getSector().getMemoryWithoutUpdate().is("$vice_ai_commander_bg_start", true)) {
					CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
					cargo.addSpecial(new SpecialItemData(Items.SHIP_BP, "vice_champion_auto"), 1);
					cargo.addSpecial(new SpecialItemData(Items.SHIP_BP, "vice_heron_auto"), 1);
					Global.getSector().getCampaignUI().addMessage(NameListUtil.KATO_BLUEPRINT, Misc.getHighlightColor());
				}
				else {
					Global.getSector().getPlayerFleet().getCargo().addSpecial(new SpecialItemData("vice_biochip_ai_commander", ""), 1);
					Global.getSector().getCampaignUI().addMessage(NameListUtil.KATO_BIOCHIP, Misc.getHighlightColor());
				}
				mem.set("$vice_project_mayfly_gave_item", true);
				
				//needs to be reset every load game
				Global.getSector().getFaction("remnant").addKnownShip("vice_chevalier_rem", false);
				Global.getSector().getFaction("remnant").addKnownShip("vice_hemlock_rem", false);
				mem.set("$vice_project_mayfly_remnant_knows_ships", true);
				
				//not yet implemented, final version will be item that leads to dialog at Danu
				//Global.getSector().getCampaignUI().addMessage(NameListUtil.INVITATION_POEM, Misc.getStoryBrightColor());
			}
		}
		else if (mem.is("$vice_faith_fury_despawn", true)) {
			ActiveBounty b = MagicBountyCoordinator.getInstance().getActiveBounty("vice_faith_fury");
			if (b == null) return;
			b.endBounty(new ActiveBounty.BountyResult.FailedSalvagedFlagship());
		}
	}
	
	@Override
	public void reportPlayerOpenedMarket(MarketAPI market) {
		if (market == null) return;
		MemoryAPI mem = Global.getSector().getMemoryWithoutUpdate();
		//if ship was already given for allying with Johan, do nothing
		if (mem.is("$vice_faith_fury_gaveship", true)) return;
		//if Faith and Fury was not failed, do nothing
		if (!mem.is("$vice_faith_fury_failed", true)) return;
		//if Project Mayfly has not started, do nothing
		if (!mem.is("$vice_project_mayfly", false)) return;
		
		//if allied with Johan, check for Chalcedon then add proteus to storage and set flag
		if (mem.is("$vice_faith_fury_ally", true)) {
			MarketAPI chal = null;
			if (Global.getSector().getEconomy().getMarket("chalcedon") == null) return;
			else chal = Global.getSector().getEconomy().getMarket("chalcedon");
			if (chal.getSubmarket(Submarkets.SUBMARKET_STORAGE) == null) return;
			
			SubmarketAPI chal_cargo = chal.getSubmarket(Submarkets.SUBMARKET_STORAGE);
			ShipVariantAPI v = Global.getSettings().getVariant("vice_proteus_captured");
			FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, v);
			member.setShipName(NameListUtil.PLS_PRIDE_OF_KAZERON);
			chal_cargo.getCargo().getMothballedShips().addFleetMember(member);
			mem.set("$vice_faith_fury_gaveship", true);
		}
		
		/**
		//self delete after final mission
		if (mem.is("$vice_project_mayfly_succeeded", true)
					|| mem.is("$vice_project_mayfly_failed", true)) {
			Global.getSector().getListenerManager().removeListener(this);
		}
		**/
	}
	
	@Override
	public void reportPlayerReputationChange(PersonAPI person, float delta) {
		MemoryAPI mem = Global.getSector().getMemoryWithoutUpdate();
		if (mem.is("$vice_give_tt_banteng", true)) {
			ShipVariantAPI v = Global.getSettings().getVariant("vice_banteng_tt_mission");
			FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, v);
			member.setShipName(NameListUtil.TTS_CONTANGO);
			Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);
			person.getRelToPlayer().setRel(0f);
			mem.set("$vice_give_tt_banteng", false);
		}
	}
}