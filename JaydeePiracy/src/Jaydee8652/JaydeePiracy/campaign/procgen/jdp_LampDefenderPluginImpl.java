package Jaydee8652.JaydeePiracy.campaign.procgen;

import java.util.Random;

import Jaydee8652.JaydeePiracy.utils.jdp_Factions;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BaseGenericPlugin;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SDMParams;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SalvageDefenderModificationPlugin;
import com.fs.starfarer.api.loading.VariantSource;
import Jaydee8652.JaydeePiracy.campaign.world.jdp_Hiroc;

import javax.swing.text.html.HTML;

//Remember - you need to activate the script in the modplugin for it to work

public class jdp_LampDefenderPluginImpl extends BaseGenericPlugin implements SalvageDefenderModificationPlugin {
	
	public float getStrength(SDMParams p, float strength, Random random, boolean withOverride) {
		return strength;
	}
	public float getMinSize(SDMParams p, float minSize, Random random, boolean withOverride) {
		return minSize;
	}
	
	public float getMaxSize(SDMParams p, float maxSize, Random random, boolean withOverride) {
		return maxSize; 
	}
	
	public float getProbability(SDMParams p, float probability, Random random, boolean withOverride) {
		return probability;
	}
	
	public void reportDefeated(SDMParams p, SectorEntityToken entity, CampaignFleetAPI fleet) {
	}

	public void modifyFleet(SDMParams p, CampaignFleetAPI fleet, Random random, boolean withOverride) {
		fleet.getFleetData().clear();

		fleet.setNoFactionInName(true);
		fleet.setName("Erratic Fusion Lamp");

		fleet.getFleetData().setShipNameRandom(random);
		fleet.addTag("sc_do_not_generate_skills");
		fleet.setFaction(jdp_Factions.JDP_FABRIQUEORBITALE);

		//Lamp
		FleetMemberAPI lamp = fleet.getFleetData().addFleetMember("jdp_lamp_Erratic");
		lamp.setShipName("TEST");
		lamp.updateStats();
		lamp.getRepairTracker().setCR(lamp.getRepairTracker().getMaxCR());

		ShipVariantAPI variant = lamp.getVariant().clone();
		variant.setSource(VariantSource.REFIT);
		variant.addTag(Tags.TAG_NO_AUTOFIT);
		lamp.setVariant(variant, false, true);

		if (fleet.getInflater() instanceof DefaultFleetInflater) {
			DefaultFleetInflater dfi = (DefaultFleetInflater) fleet.getInflater();
			DefaultFleetInflaterParams dfip = (DefaultFleetInflaterParams)dfi.getParams();
			dfip.allWeapons = true;
			dfip.averageSMods = 3;
			dfip.quality = 0.4f;

			DModManager.assumeAllShipsAreAutomated = true;
			fleet.inflateIfNeeded();
			fleet.setInflater(null);
			DModManager.assumeAllShipsAreAutomated = false;
		}
	}

	@Override
	public int getHandlingPriority(Object params) {
		if (!(params instanceof SDMParams)) return 0;
		SDMParams p = (SDMParams) params;
		
		if (p.entity != null && p.entity.hasTag("jdp_erratic_lamp_tag")) {
			return 2;
		}
		return -1;
	}
	public float getQuality(SDMParams p, float quality, Random random, boolean withOverride) {
		return quality;
	}
}



