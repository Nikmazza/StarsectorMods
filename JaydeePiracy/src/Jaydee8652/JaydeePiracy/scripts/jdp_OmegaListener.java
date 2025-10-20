package Jaydee8652.JaydeePiracy.scripts;

import Jaydee8652.JaydeePiracy.utils.jdp_People;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.util.DelayedActionScript;
import org.apache.log4j.Logger;


import static Jaydee8652.JaydeePiracy.campaign.world.jdp_Dory.addPenroseFleet;

public class jdp_OmegaListener implements FleetEventListener {
	@Override
	public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {
	}
	public static Logger log = Global.getLogger(jdp_People.class);


	@Override
	public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
		if (primaryWinner.getFaction().equals(Global.getSector().getPlayerFaction())) {
			for (CampaignFleetAPI loser : battle.getNonPlayerSide()) {
				if (loser.getFaction().getId().equals(Factions.OMEGA)) {
					for (SectorEntityToken entity : Global.getSector().getPlayerFleet().getContainingLocation().getAllEntities())
						if (entity.hasTag(Entities.CORONAL_TAP)) {
							if ((!Global.getSector().getMemoryWithoutUpdate().contains("$jdp_conquered_hypershunt")) && (Global.getSector().getMemoryWithoutUpdate().getBoolean("$nex_randomSector") == false)) {

								log.info("JDP_LOG: The Penorse has been generated");

								Global.getSector().getMemoryWithoutUpdate().set("$jdp_conquered_hypershunt", true);
								SectorEntityToken hrvoje = Global.getSector().getEntityById("jdp_hrvoje");
								addPenroseFleet(hrvoje);

								Global.getSector().addScript(new DelayedActionScript(2) {
									@Override
									public void doAction() {
										CampaignFleetAPI player = Global.getSector().getPlayerFleet();
										if (player == null) return;
										log.info("JDP_LOG: The Penrose Quest has fired");
										Global.getSector().getCampaignUI().showInteractionDialog(new RuleBasedInteractionDialogPluginImpl("jdp_hypershuntConquered"), player);
									}
								});
									//In theory, if a mod adds an Omega fleet that spawns in Hypershunt Systems it would also trigger
									//this Listener, however for my purposes, the description of the quest says you killed Omega in a
									//system with a Hypershunt, so it would still be correct.
								}

						}
				}
			}
		}
	}
}