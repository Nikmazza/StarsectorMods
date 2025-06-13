package data.campaign.procgen;

import java.util.Random;

import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BaseGenericPlugin;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SDMParams;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SalvageDefenderModificationPlugin;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import data.scripts.world.systems.HMI_manchester;

public class DomResSurprise2PluginImpl extends BaseGenericPlugin implements SalvageDefenderModificationPlugin {
	
	public float getStrength(SDMParams p, float strength, Random random, boolean withOverride) {
		// doesn't matter, just something non-zero so we end up with a fleet
		// the auto-generated fleet will get replaced by this anyway
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

		AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(Commodities.ALPHA_CORE);
		
		fleet.getFleetData().clear();
		fleet.getFleetData().setShipNameRandom(random);
		
		
		FleetMemberAPI member = fleet.getFleetData().addFleetMember("domres_hammerhead_std");
		member.setId("drf_" + random.nextLong());
		PersonAPI person = plugin.createPerson(Commodities.ALPHA_CORE, fleet.getFaction().getId(), random);
		person.getStats().setSkipRefresh(true);
		person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
		person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
		person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
		person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
		person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
		person.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);
		person.getStats().setSkipRefresh(false);
		
		member.setCaptain(person);
		ShipVariantAPI v = member.getVariant().clone();
		v.setSource(VariantSource.REFIT);
		v.addTag(Tags.TAG_NO_AUTOFIT);
		v.addTag(Tags.VARIANT_ALWAYS_RECOVERABLE);
		member.setVariant(v, false, true);
		fleet.setCommander(person);

		addAutomated(fleet, "domres_rampart_bombardment", null, Commodities.BETA_CORE, random);
		addAutomated(fleet, "domres_rampart_bombardment", null, Commodities.BETA_CORE, random);

		addAutomated(fleet, "domres_berserker_cs", null, Commodities.GAMMA_CORE, random);
		addAutomated(fleet, "domres_berserker_cs", null, Commodities.GAMMA_CORE, random);
		addAutomated(fleet, "domres_berserker_cs", null, Commodities.GAMMA_CORE, random);
		addAutomated(fleet, "domres_berserker_cs", null, Commodities.GAMMA_CORE, random);

		addAutomated(fleet, "domres_warden_cs", null, null, random);
		addAutomated(fleet, "domres_warden_attack", null, null, random);
		addAutomated(fleet, "domres_warden_cs", null, null, random);
		addAutomated(fleet, "domres_warden_attack", null, null, random);
		addAutomated(fleet, "domres_warden_cs", null, null, random);
		addAutomated(fleet, "domres_warden_attack", null, null, random);


		for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
			makeAICoreSkillsGoodForLowTech(curr, true);
			curr.getRepairTracker().setCR(curr.getRepairTracker().getMaxCR());
		}
		
		for (FleetMemberAPI curr : fleet.getFleetData().getMembersListCopy()) {
			v = curr.getVariant().clone();
			v.setSource(VariantSource.REFIT);
			curr.setVariant(v, false, false);
		}
		
		if (fleet.getInflater() instanceof DefaultFleetInflater) {
			DefaultFleetInflater dfi = (DefaultFleetInflater) fleet.getInflater();
			DefaultFleetInflaterParams dfip = (DefaultFleetInflaterParams)dfi.getParams();
			dfip.allWeapons = true;
			dfip.averageSMods = 3;
			dfip.quality = 0.8f;
			
			// what a HACK
			DModManager.assumeAllShipsAreAutomated = true;
			fleet.inflateIfNeeded();
			fleet.setInflater(null);
			DModManager.assumeAllShipsAreAutomated = false;
		}
	}
	
	public static void addAutomated(CampaignFleetAPI fleet, String variantId, String shipName, String aiCore, Random random) {
		AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(Commodities.ALPHA_CORE);
		
		FleetMemberAPI member = fleet.getFleetData().addFleetMember(variantId);
		member.setId("drf_" + random.nextLong());
		
		//System.out.println("ID for " + variantId + ": " + member.getId());
		
		//member.setId("xivtf_" + random.nextLong());
		if (shipName != null) {
			member.setShipName(shipName);
		}
		if (aiCore != null) {
			PersonAPI person = plugin.createPerson(aiCore, fleet.getFaction().getId(), random);
			member.setCaptain(person);
		}
	}
	
	public static void makeAICoreSkillsGoodForLowTech(FleetMemberAPI member, boolean integrate) {
		if (member.getCaptain() == null || !member.getCaptain().isAICore()) return;
		
		PersonAPI person = member.getCaptain();
		person.getStats().setSkipRefresh(true);
		String aiCoreId = person.getAICoreId(); 
		
		if (integrate) {
			person.getStats().setLevel(person.getStats().getLevel() + 1);
			person.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 2);
		}
		
		boolean alpha = Commodities.ALPHA_CORE.equals(aiCoreId);
		boolean beta = Commodities.BETA_CORE.equals(aiCoreId);
		boolean gamma = Commodities.GAMMA_CORE.equals(aiCoreId);
		
		if (member.isCapital() || member.isCruiser()) {
			person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 0);
			person.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
		}
		
		// actually, the base skills are pretty much fine, so just add ballistic mastery if integrating, and
		// that's all
		
		// Base skills, for reference (pulled from AICoreOfficerPluginImpl)
		// ALPHA CORE
//		person.getStats().setLevel(7);
//		person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
//		person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
//		person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
//		person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 2);
//		person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
//		person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
//		person.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
		
		// BETA CORE
//		person.getStats().setLevel(5);
//		person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
//		person.getStats().setSkillLevel(Skills.TARGET_ANALYSIS, 2);
//		person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
//		person.getStats().setSkillLevel(Skills.GUNNERY_IMPLANTS, 2);
//		person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);

		// GAMMA CORE
//		person.getStats().setLevel(3);
//		person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
//		person.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 2);
//		person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
		
		person.getStats().setSkipRefresh(false);
	}
	
	@Override
	public int getHandlingPriority(Object params) {
		if (!(params instanceof SDMParams)) return 0;
		SDMParams p = (SDMParams) params;
		
		if (p.entity != null && p.entity.getMemoryWithoutUpdate().contains(
				HMI_manchester.DOMRES_SURPRISE2_KEY)) {
			return 2;
		}
		return -1;
	}
	public float getQuality(SDMParams p, float quality, Random random, boolean withOverride) {
		return quality;
	}
}



