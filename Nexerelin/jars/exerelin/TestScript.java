package exerelin;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MutableStat.StatMod;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI.ShipTypeHints;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.CoreScript;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseIntel;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.PiracyRespiteScript;
import com.fs.starfarer.api.impl.campaign.intel.events.PirateBasePirateActivityCause2;
import com.fs.starfarer.api.impl.campaign.missions.RecoverAPlanetkiller;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseHubMission;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMission;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantStationFleetManager;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.loading.PersonMissionSpec;
import com.fs.starfarer.api.util.Misc;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.PlayerFactionStore;
import exerelin.campaign.RevengeanceManager;
import exerelin.campaign.SectorManager;
import exerelin.campaign.ai.StrategicAI;
import exerelin.campaign.ai.concern.CommodityCompetitionConcern;
import exerelin.campaign.ai.concern.StrategicConcern;
import exerelin.campaign.intel.bases.NexPirateBaseIntel;
import exerelin.campaign.intel.hostileactivity.NexHostileActivityManager;
import exerelin.utilities.NexUtilsFleet;
import org.lazywizard.console.Console;
import org.lwjgl.util.vector.Vector2f;

import java.io.IOException;
import java.util.*;

import static exerelin.campaign.intel.colony.ColonyExpeditionIntel.log;

//import exerelin.utilities.ReflectionUtils;

//import exerelin.campaign.ai.MilitaryInfoHelper;

public class TestScript {

	// runcode exerelin.TestScript.run()
	public static void run() {
		HostileActivityEventIntel ha = HostileActivityEventIntel.get();
		if (ha != null) {
			int prog = ha.getProgress();
			ha.endImmediately();
			new HostileActivityEventIntel();
			NexHostileActivityManager.purgeOldListeners();
			NexHostileActivityManager.replaceVanillaOverrideActivities(ha);
			ha.setProgress(prog);
		}
		Console.showMessage("wololo");

		if (true) return;

		//purgeNonPlayerMarkets();

        for (CampaignFleetAPI fleet : Global.getSector().getCurrentLocation().getFleets()) {
            if (fleet.getBattle() == null) continue;
            for (CampaignFleetAPI bf : fleet.getBattle().getBothSides()) {
                if (bf.getFaction().getId().equals(Factions.LUDDIC_CHURCH) && bf.getName().equals("Unknown Attack Fleet")) {
                    Console.showMessage("Found Eugel fleet, attempting to purge");
                    fleet.getBattle().finish(BattleAPI.BattleSide.TWO, false);
                    for (FleetMemberAPI member : new ArrayList<>(bf.getFleetData().getMembersListCopy()))
                    {
						//Console.showMessage("Removing member " + member.getShipName());
                        fleet.getFleetData().removeFleetMember(member);
                    }
                    fleet.despawn();
                    return;
                }
            }
        }

        // runcode import com.fs.starfarer.api.impl.campaign.shared.SharedData;
		Global.getSector().getPlayerFleet().getCargo().getCredits().set(0);
		SharedData.getData().getCurrentReport().setDebt(0);
		SharedData.getData().getCurrentReport().setPreviousDebt(0);

        // runcode import exerelin.campaign.PlayerFactionStore; import exerelin.campaign.AllianceManager; import exerelin.campaign.SectorManager;
		String playerFactionId = PlayerFactionStore.getPlayerFactionId();
        boolean hegAlive = SectorManager.isFactionAlive(Factions.HEGEMONY);
		Console.showMessage("Hegemony alive: " + hegAlive);
		boolean blockInspect = (!playerFactionId.equals(Factions.HEGEMONY) && AllianceManager.areFactionsAllied(playerFactionId, Factions.HEGEMONY));
		Console.showMessage("Inspections blocked due to alliance: " + blockInspect);

		if (true) return;

		StarSystemAPI system = (StarSystemAPI) Global.getSector().getCurrentLocation();
		for (EveryFrameScript script : system.getScripts()) {
			if (script instanceof RemnantStationFleetManager) {
				RemnantStationFleetManager rsfm = (RemnantStationFleetManager)script;
				//Console.showMessage("baaaaa " + ReflectionUtils.getIncludingSuperclasses("maxFleets", rsfm, rsfm.getClass()));
				break;
			}
		}


		/*
		runcode
		MarketAPI market = Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget().getMarket();
		for (com.fs.starfarer.api.campaign.CommDirectoryEntryAPI entry : market.getCommDirectory().getEntriesCopy()) {
			if (entry.getEntryData() instanceof PersonAPI && ((PersonAPI)entry.getEntryData()).isAICore()) {
				market.getCommDirectory().removeEntry(entry.getId());
				break;
			}
		}
		*/

		/*
		// runcode import java.net.URL;
		Class klass = String.class;
		URL location = klass.getResource('/' + klass.getName().replace('.', '/') + ".class");
		Console.showMessage(location);

		// runcode
		PersonAPI pers = Global.getSector().getImportantPeople().getPerson("sunrider_ava");
		Console.showMessage(pers == null);
		Global.getSector().getPlayerFleet().getFleetData().addOfficer(pers);
		*/

		/*
		for (PlayerSpecialForcesIntel psf : Global.getSector().getListenerManager().getListeners(PlayerSpecialForcesIntel.class)) {
			Console.showMessage("Found listener for PSF " + psf.getName());
		}

		for (MarketAPI market : Misc.getFactionMarkets(Factions.PLAYER)) {
			ShipQuality.QualityData qd = ShipQuality.getInstance().getQualityData(market);
			Console.showMessage("Quality for market " + market.getName() + " is " + NexUtils.statBonusToString(qd.quality, 0));
		}
		*/

		//new CoreLifecyclePluginImpl().tagLuddicShrines();

		// runcode import com.fs.starfarer.api.impl.campaign.missions.academy.GAProjectZiggurat;
		//GAProjectZiggurat zig = (GAProjectZiggurat)Global.getSector().getMemoryWithoutUpdate().get("$gaPZ_ref");
		//zig.setCurrentStage(GAProjectZiggurat.Stage.TALK_TO_CALLISTO, null, null);

		// spam some raids
		/*
		for (String factionId : SectorManager.getLiveFactionIdsCopy()) {
			if (StrategicAI.getAI(factionId) == null) continue;
			FactionAPI faction = Global.getSector().getFaction(factionId);
			List<String> enemies = DiplomacyManager.getFactionsAtWarWithFaction(faction, false, false, true);
			if (enemies.isEmpty()) continue;
			FactionAPI target = Global.getSector().getFaction(enemies.get(0));

			InvasionFleetManager.getManager().generateInvasionOrRaidFleet(faction, target,
					InvasionFleetManager.EventType.RAID, new FleetPoolManager.RequisitionParams());
		}
		 */

		/*
		for (IntelInfoPlugin intel : Global.getSector().getIntelManager().getIntel(DelayedFleetEncounter.class)) {
			DelayedFleetEncounter dfe = (DelayedFleetEncounter)intel;
			String str = String.format("DFE %s, stage %s", dfe.getName(), dfe.getCurrentStage());
			Console.showMessage(str);
		}
		*/
		
		/*
		SensorGhostManager sgm = SensorGhostManager.getGhostManager();
		for (SensorGhostCreator creator : SensorGhostManager.CREATORS) {
			if (creator instanceof GuideGhostCreator) {
				List<SensorGhost> list = creator.createGhost(sgm);
				if (list == null || list.isEmpty()) Console.showMessage("Failed to create ghost");
				break;
			}
		}
		*/
		/*
		for (FactionAPI faction : Global.getSector().getAllFactions()) {
			float maxPointsForFaction = faction.getApproximateMaxFPPerFleet(FactionAPI.ShipPickMode.PRIORITY_THEN_ALL);
			Console.showMessage(String.format("%s has max FP: %s", faction.getDisplayName(), maxPointsForFaction));
		}
		*/
		
		//checkUpkeepModifiers();
		
		//dumpShieldUpkeepStats();
	}

	public static void purgeKOLListeners() {
		/* runcode int count = 0;

		while (Global.getSector().getListenerManager().hasListenerOfClass(org.selkie.kol.listeners.UpdateRelationships.class)) {
			Global.getSector().getListenerManager().removeListenerOfClass(org.selkie.kol.listeners.UpdateRelationships.class);
			count++;
		}
		Console.showMessage("Removed " + count + " listeners");
		*/

		// runcode com.fs.starfarer.api.impl.campaign.intel.events.PirateHostileActivityFactor.setDefeatedLargePirateRaid(false)

		// runcode Console.showMessage(com.fs.starfarer.api.impl.campaign.intel.events.getBaseIntel(star));
	}

	// runcode exerelin.TestScript.createNearbyPirateBase()
	public static void createNearbyPirateBase() {
		PiracyRespiteScript.get().cleanup();
		StarSystemAPI star = Global.getSector().getStarSystem("Penelope's Star");
		if (true) {
			Console.showMessage(PirateBasePirateActivityCause2.getBaseIntel(star));
			return;
		}
		for (StarSystemAPI system : Misc.getNearbyStarSystems(star.getStar(), 10)) {
			if (!Misc.getMarketsInLocation(system).isEmpty()) {
				continue;
			}
			PirateBaseIntel pb = new NexPirateBaseIntel(system, Factions.PIRATES, PirateBaseIntel.PirateBaseTier.TIER_5_3MODULE);
			if (pb.isEnding() || pb.isEnded()) continue;
			pb.setTargetPlayerColoniesOnly(true);
			pb.setForceTarget(star);
			Console.showMessage("Pirate base created in " + system.getNameWithLowercaseType());
			break;
		}
	}

	public static void printStatModBonuses(StatBonus bonus) {
		Console.showMessage("  Flat bonuses:");
		for (StatMod mod : bonus.getFlatBonuses().values()) {
			Console.showMessage(String.format("  - %s : %s", mod.desc, mod.value));
		}
		Console.showMessage("  Mult bonuses:");
		for (StatMod mod : bonus.getMultBonuses().values()) {
			Console.showMessage(String.format("  - %s : %s", mod.desc, mod.value));
		}
		Console.showMessage("  Percent bonuses:");
		for (StatMod mod : bonus.getPercentBonuses().values()) {
			Console.showMessage(String.format("  - %s : %s", mod.desc, mod.value));
		}
	}

	public static void upsizeAllPlayerMarkets() {
		for (MarketAPI market : Misc.getFactionMarkets(Factions.PLAYER)) {
			ImmigrationPlugin plugin = Misc.getImmigrationPlugin(market);
			float weight = plugin.getWeightForMarketSize(market.getSize() + 1);
			market.getPopulation().setWeight(weight);
			market.getPopulation().normalize();
		}
	}

	// runcode exerelin.TestScript.rerollHAEvent();
	public static void rerollHAEvent() {
		setHALevel(0);
		setHALevel(598);
	}

	//runcode exerelin.TestScript.setHALevel(598)
	public static void setHALevel(int amount) {
		// runcode import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;	int amount = 598;
		HostileActivityEventIntel intel = HostileActivityEventIntel.get();
		if (intel != null) {
			intel.setProgress(amount);
		}
	}

	public static void validateVariants() {
		// runcode import Global.*;
		for (ShipHullSpecAPI spec : Global.getSettings().getAllShipHullSpecs()) {
			String hullId = spec.getHullId();
			//Console.showMessage("Checking hull ID " + hullId);
			List<ShipVariantAPI> targets = Global.getSector().getAutofitVariants().getTargetVariants(hullId);
			for (ShipVariantAPI curr : targets) {
				if (curr == null) {
					Console.showMessage("Null variant found for hull ID " + hullId);
				}
			}
		}

	}

	public static void purgeMonthlyReport() {
		// runcode import com.fs.starfarer.api.campaign.econ.MonthlyReport; import com.fs.starfarer.api.impl.campaign.shared.SharedData;
		MonthlyReport report = SharedData.getData().getPreviousReport();
		for (MonthlyReport.FDNode node : report.getColoniesNode().getChildren().values()) {
			if (node.getChildren() != null) {
				for (MonthlyReport.FDNode subnode : node.getChildren().values()) {
					subnode.tooltipCreator = null;
				}
			}
		}
		report = SharedData.getData().getCurrentReport();
		for (MonthlyReport.FDNode node : report.getColoniesNode().getChildren().values()) {
			if (node.getChildren() != null) {
				for (MonthlyReport.FDNode subnode : node.getChildren().values()) {
					subnode.tooltipCreator = null;
				}
			}
		}
	}

	public static void purgeNonPlayerMarkets() {
		for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
			if (market.getFaction().isPlayerFaction()) continue;
			DecivTracker.decivilize(market, true);
		}
	}

	public static void rerollActions() {
		for (String factionId : SectorManager.getLiveFactionIdsCopy()) {
			StrategicAI sai = StrategicAI.getAI(factionId);
			if (sai == null) continue;
			sai.forceMeeting();
		}
	}

	public static void purgeAIConcerns() {
        for (String factionId : SectorManager.getLiveFactionIdsCopy()) {
            StrategicAI sai = StrategicAI.getAI(factionId);
            if (sai == null) continue;
            for (StrategicConcern concern : sai.getEconModule().getCurrentConcerns()) {
                if (!(concern instanceof CommodityCompetitionConcern)) continue;
                CommodityCompetitionConcern c3 = (CommodityCompetitionConcern)concern;
                String compFacId = c3.getFaction().getId();
                //Console.showMessage("Concern has faction ID " + concernFacId);
                if (c3.getCommodityId().equals(Commodities.ORE)) {
                    Console.showMessage("Purging concern " + c3.getName() + " for faction " + factionId);
                    sai.getEconModule().removeConcern(c3);
                    break;
                }
            }
        }
    }

	// runcode exerelin.TestScript.printPlayerPower()
	public static void printPlayerPower() {
		float playerPower = NexUtilsFleet.calculatePowerLevel(Global.getSector().getPlayerFleet());
		Console.showMessage("Player power level is " + playerPower);
	}

	// runcode exerelin.TestScript.testAllyFleetGen()
	public static void testAllyFleetGen() {
		FactionAPI heg = Global.getSector().getFaction(Factions.HEGEMONY);
		FactionAPI tt = Global.getSector().getFaction(Factions.TRITACHYON);

		tt.setRelationship(heg.getId(), 0.51f);
		AllianceManager.createAlliance(heg.getId(), tt.getId());

		MarketAPI market = Global.getSector().getEconomy().getMarket("jangala");
		FleetParamsV3 params = new FleetParamsV3(market, FleetTypes.PATROL_LARGE,
				100,
				10,
				10,
				10,
				10,
				5,
				0);
		//params.factionId = heg.getId();

		CampaignFleetAPI standard = FleetFactoryV3.createFleet(params);
		CampaignFleetAPI ally = NexUtilsFleet.nexCreateFleet(params);
		addFleetToPlayerLocation(standard, -300, 0);
		addFleetToPlayerLocation(ally, 300, 0);
	}

	public static void addFleetToPlayerLocation(CampaignFleetAPI fleet, float xOffset, float yOffset) {
		CampaignFleetAPI player = Global.getSector().getPlayerFleet();
		LocationAPI loc = player.getContainingLocation();
		loc.addEntity(fleet);
		fleet.setLocation(player.getLocation().x + xOffset, player.getLocation().y + yOffset);
	}

	// runcode exerelin.TestScript.forceCustomProduction()
	public static void forceCustomProduction() {
		CoreScript cs = null;
		for (EveryFrameScript script : Global.getSector().getScripts()) {
			if (script instanceof com.fs.starfarer.api.impl.campaign.CoreScript) {
				Console.showMessage("Running core script " + script);
			}
		}
		if (cs == null) return;

		cs.doCustomProduction();
		Console.showMessage("Executing custom production");

		FactionAPI pf = Global.getSector().getPlayerFaction();
		FactionProductionAPI prod = pf.getProduction();

		MarketAPI gatheringPoint = prod.getGatheringPoint();
		SubmarketAPI storage = gatheringPoint.getSubmarket(Submarkets.SUBMARKET_STORAGE);
		for (FleetMemberAPI member : storage.getCargo().getMothballedShips().getMembersListCopy()) {
			Console.showMessage(String.format(" Found ship %s (%s), variant source %s", member.getShipName(),
					member.getHullSpec().getHullNameWithDashClass(), member.getVariant().getSource()));
		}
	}

	public static void printVengeanceLevel(String factionId) {
		// runcode import exerelin.campaign.RevengeanceManager;
		RevengeanceManager man = RevengeanceManager.getManager();
		int currStage = man.getCurrentVengeanceStage(factionId);
		float points = man.getFactionPoints(factionId);
		float req = 0;
		if (currStage < RevengeanceManager.FLEET_STAGES.size() - 1) {
			req = ((Integer[])(RevengeanceManager.FLEET_STAGES.get(currStage + 1)))[0];
		} else {
			float surp = points % RevengeanceManager.ADDITIONAL_STAGE_INTERVAL;
			req = points - surp + RevengeanceManager.ADDITIONAL_STAGE_INTERVAL;
		}
		Console.showMessage(String.format("Faction %s has %.1f points, needed %.1f", factionId, points, req));
	}

	// runcode exerelin.TestScript.createMission("nex_conquest", false);
	// runcode exerelin.TestScript.createMission("nex_supReb", false);
	// runcode exerelin.TestScript.createMission("gaRR", false);
	public static HubMission createMission(String missionId, boolean startNow) {
		PersonMissionSpec spec = Global.getSettings().getMissionSpec(missionId);
		if (spec == null) {
			throw new RuntimeException("Mission with spec [" + missionId + "] not found");
		}
		InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();

		HubMission mission = spec.createMission();

		SectorEntityToken entity = dialog.getInteractionTarget();
		PersonAPI person = entity.getActivePerson();

		if (person == null) {
			String extra = "";
			long seed = BarEventManager.getInstance().getSeed(null, person, extra);
//			person.getMemoryWithoutUpdate().set(key, "" + seed); // so it's not the same seed for multiple missions
			mission.setGenRandom(new Random(seed));
		} else {
			mission.setPersonOverride(person);
			//mission.setGenRandom(new Random(Misc.getSalvageSeed(entity)));
			String key = "$beginMission_seedExtra";
			String extra = person.getMemoryWithoutUpdate().getString(key);
			long seed = BarEventManager.getInstance().getSeed(null, person, extra);
			person.getMemoryWithoutUpdate().set(key, "" + seed); // so it's not the same seed for multiple missions
			mission.setGenRandom(new Random(seed));
		}

		mission.createAndAbortIfFailed(entity.getMarket(), false);

		if (mission.isMissionCreationAborted()) {
			return null;
		}
		return mission;
	}

	public static void printInvalidEntityLocations() {
		// runcode import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
		List<SectorEntityToken> tokens = new ArrayList<SectorEntityToken>();
		for (StarSystemAPI system : Global.getSector().getStarSystems()) {
			tokens.addAll(system.getAllEntities());
		}
		tokens.addAll(Global.getSector().getHyperspace().getAllEntities());
		for (SectorEntityToken token : tokens) {
			float x = token.getLocation().x;
			float y = token.getLocation().y;
			if (Float.isNaN(x) || Float.isInfinite(x) || Float.isNaN(y) || Float.isInfinite(y)) {
				Console.showMessage(String.format("Entity %s (%s) in %s has non-finite location", token.getFullName(), token.getId(), token.getContainingLocation().getNameWithLowercaseType()));
			}
		}
	}

	public static void findTraum() {
		// runcode import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
		List<SectorEntityToken> tokens = new ArrayList<SectorEntityToken>();
		for (StarSystemAPI system : Global.getSector().getStarSystems()) {
			tokens.addAll(system.getAllEntities());
		}
		tokens.addAll(Global.getSector().getHyperspace().getAllEntities());
		for (SectorEntityToken token : tokens) {
			String specId = token.getCustomEntityType();
			if (specId == null || token.getMemoryWithoutUpdate().contains(MemFlags.SALVAGE_SPEC_ID_OVERRIDE)) {
				specId = token.getMemoryWithoutUpdate().getString(MemFlags.SALVAGE_SPEC_ID_OVERRIDE);
			}
			if (!"wreck".equals(specId)) continue;
			CustomCampaignEntityPlugin plugin = token.getCustomPlugin();
			if (plugin instanceof DerelictShipEntityPlugin) {
				DerelictShipEntityPlugin dsep = (DerelictShipEntityPlugin)plugin;
				String variantId = dsep.getData().ship.variantId;
				if (variantId.startsWith("tahlan_schnee")) {
					Console.showMessage("Traum found in " + token.getContainingLocation().getName() + " at " + token.getLocation());
				}
			}
		}
	}
	
	public static void dumpShieldUpkeepStats() {
		HullModSpecAPI stab = Global.getSettings().getHullModSpec(HullMods.STABILIZEDSHIELDEMITTER);
		StringBuilder sb = new StringBuilder();
		
		for (ShipHullSpecAPI spec : Global.getSettings().getAllShipHullSpecs()) {
			if (!spec.getHullId().startsWith("uaf")) continue;
			if (spec.isDHull()) continue;
			if (spec.getHullSize() == HullSize.FIGHTER) continue;
			if (spec.getHints().contains(ShipTypeHints.UNBOARDABLE)) continue;
			String name = spec.getHullName();
			float upkeep = spec.getShieldSpec().getUpkeepCost();
			float upkeepMult = upkeep/spec.getFluxDissipation();
			int stabCost = stab.getCostFor(spec.getHullSize());
			float stabSavings = upkeep * 0.5f;
			float equivalentVents = stabSavings/10;
			
			sb.append(String.format("%s,%.0f,%.1f,%s,%.0f,%.0f", name, upkeep, upkeepMult, stabCost, stabSavings, equivalentVents));
			sb.append("\r\n");
			
			//Console.showMessage(String.format("%s has upkeep %.0f", name, upkeep));
		}
		
		try {
			Global.getSettings().writeTextFileToCommon("shield_data", sb.toString());
		} catch (IOException ex) {
			Console.showMessage("Dump failed qq: " + ex.getMessage());
		}
	}

	/*
	public static void dumpMarketDefenseStats() {
		StringBuilder sb = new StringBuilder();

		for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
			String name = market.getName();
			float value = getMarketValue(market);
			float sd = getSpaceDefenseValue(market);
			float gd = getGroundDefenseValue(market);
			sb.append(String.format("%s\t%s\t%s\t%.1f\t%.1f", name, market.getFaction().getDisplayName(), market.getSize(), sd, gd));
			sb.append("\r\n");
		}

		try {
			Global.getSettings().writeTextFileToCommon("defense_data", sb.toString());
		} catch (IOException ex) {
			Console.showMessage("Dump failed qq: " + ex.getMessage());
		}
	}

	public static float getMarketValue(MarketAPI market) {
		float value = NexUtilsMarket.getMarketIndustryValue(market);
		for (Industry ind : market.getIndustries()) {
			if (ind.getSpec().hasTag(Industries.TAG_HEAVYINDUSTRY)) {
				value += ind.getBuildCost();
			}
		}
		value += NexUtilsMarket.getIncomeNetPresentValue(market, 6, 0.02f);

		return value;
	}

	/*
	public static float getSpaceDefenseValue(MarketAPI market) {
		MilitaryInfoHelper helper = MilitaryInfoHelper.getInstance();
		MilitaryInfoHelper.PatrolStrengthEntry patrolStr = helper.getPatrolStrength(market.getContainingLocation());
		Float space = 0f;
		if (patrolStr != null && patrolStr.strByFaction.containsKey(market.getFaction().getId())) {
			space += patrolStr.strByFaction.get(market.getFaction().getId());
		}
		CampaignFleetAPI station = Misc.getStationFleet(market);
		float stationStr = 0;
		if (station != null) {
			stationStr = NexUtilsFleet.getFleetStrength(station, true, true, true);
		}
		space += stationStr;
		return space;
	}
	 */

	public static float getGroundDefenseValue(MarketAPI market) {
		return market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).computeEffective(0);
	}
	
	public static void checkUpkeepModifiers() {
		// runcode import com.fs.starfarer.api.combat.MutableStat.StatMod;
		MarketAPI market = Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget().getMarket();
		Map<String, StatMod> incomeMods = market.getIncomeMult().getMultMods();
		for (String key : new ArrayList<String>(incomeMods.keySet())) {
			StatMod mod = incomeMods.get(key);
			String str = String.format("Mult %s (%s): %s", mod.source, mod.desc, mod.value);
			Console.showMessage(str);
		}
		Map<String, StatMod> upkeepMods = market.getUpkeepMult().getMultMods();
		for (String key : new ArrayList<String>(upkeepMods.keySet())) {
			StatMod mod = upkeepMods.get(key);
			String str = String.format("Mult %s (%s): %s", mod.source, mod.desc, mod.value);
			Console.showMessage(str);
		}
	}
	
	public static void printShipsAndPricesSorted() {
		
		List<ShipHullSpecAPI> shipsSorted = new ArrayList<>(Global.getSettings().getAllShipHullSpecs());
		Collections.sort(shipsSorted, new Comparator<ShipHullSpecAPI>() {
			@Override
			public int compare(ShipHullSpecAPI one, ShipHullSpecAPI two) {
				int compare = one.getHullSize().compareTo(two.getHullSize());
				if (compare != 0) return compare;
				return Float.compare(one.getBaseValue(), two.getBaseValue());
			}
		});
		
		StringBuilder txt = new StringBuilder();
		for (ShipHullSpecAPI spec : shipsSorted) {
			if (spec.getHullName() == null || spec.getHullName().isEmpty()) continue;
			if (spec.getHints().contains(ShipTypeHints.HIDE_IN_CODEX)) continue;
			if (spec.isDHull()) continue;
			String line = String.format("%s,%s,%s", spec.getHullName(), spec.getHullSize(), spec.getBaseValue());
			txt.append(line);
			txt.append("\r\n");
		}
		try {
			Global.getSettings().writeTextFileToCommon("ship cost dump", txt.toString());
		} catch (IOException ex) {
			log.error("wtf failed to write file", ex);
		}
	}

	public static void checkRepair() {
		for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy())
		{
			float crToRecover = member.getRepairTracker().getMaxCR() - member.getRepairTracker().getCR();
			
			if (crToRecover <= 0) continue;
			Console.showMessage("Checking fleet member " + member.getShipName() + ", " + member.getHullSpec().getHullNameWithDashClass());
			
			float suppliesPerCRPoint = member.getDeploymentCostSupplies()/member.getDeployCost();
			float repairPerDay = member.getRepairTracker().getRepairRatePerDay();
			float suppliesPerDay = suppliesPerCRPoint * member.getRepairTracker().getRecoveryRate();
			float recoverCost = suppliesPerCRPoint * crToRecover;
			String str = String.format("CR recovery cost: %.2f, supplies per day: %.2f", recoverCost, suppliesPerDay);
			Console.showMessage(str);
			
			
			float hullDam = member.getStatus().getHullDamageTaken();
			float armorDam = member.getStatus().getArmorDamageTaken();
			float daysToRepair = (hullDam + armorDam)/repairPerDay;
			float daysToRepair2 = member.getRepairTracker().getRemainingRepairTime();
			str = String.format("Armor dam: %.2f; hull dam: %.2f", armorDam, hullDam);
			Console.showMessage(str);
			str = String.format("Days to repair: %.2f, will cost %.2f", daysToRepair, daysToRepair * suppliesPerDay);
			Console.showMessage(str);
			str = String.format("Days to repair alt: %.2f, will cost %.2f", daysToRepair2, daysToRepair2 * suppliesPerDay);
			Console.showMessage(str);
			//break;
		}
	}
	
	// runcode exerelin.TestScript.debugMission();
	public static void debugMission() {
		HubMissionWithTriggers mission = (HubMissionWithTriggers)Global.getSector().getCampaignUI().
				getCurrentInteractionDialog().getInteractionTarget().getActivePerson().getMemoryWithoutUpdate().get("$extr_ref");
		debugMission(mission);
	}

	public static void lookForPK() {
		// runcode import com.fs.starfarer.api.impl.campaign.missions.RecoverAPlanetkiller; import com.fs.starfarer.api.campaign.rules.MemoryAPI;

		MemoryAPI mem = Global.getSector().getMemoryWithoutUpdate();
		Console.showMessage(String.format("$pk_ref is %s, $pk_inProgress is %s", mem.get("$pk_ref"), mem.get("$pk_inProgress")));

		CampaignFleetAPI nexus = new RecoverAPlanetkiller().getNexus();
		if (nexus != null) {
			Console.showMessage(String.format("Nexus %s found in %s", nexus.getName(), nexus.getContainingLocation().getNameWithLowercaseType()));
			Console.showMessage("Nexus is alive: " + nexus.isAlive());
		} else {
			Console.showMessage("Nexus not found");
		}
		Console.showMessage("Started at nexus: " + RecoverAPlanetkiller.startedAtNexus());
	}

	public static void pickNewNexus() {
		// runcode import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator; import com.fs.starfarer.api.impl.campaign.missions.RecoverAPlanetkiller;
		Console.showMessage(new RecoverAPlanetkiller().getNexusSystem().getNameWithLowercaseTypeShort());

		if (true) return;
		List<CampaignFleetAPI> stations = MiscellaneousThemeGenerator.getRemnantStations(true, false);
		float minDist = Float.MAX_VALUE;
		CampaignFleetAPI nexus = null;
		for (CampaignFleetAPI curr : stations) {
			float dist = Misc.getDistanceLY(RecoverAPlanetkiller.getTundra(), curr);
			if (dist < minDist) {
				minDist = dist;
				nexus = curr;
			}
		}
		if (nexus != null) {
			Console.showMessage("Picking new nexus in [" + nexus.getContainingLocation().getName() + "]");
			nexus.getMemoryWithoutUpdate().set(MiscellaneousThemeGenerator.PK_NEXUS_KEY, true);
			Global.getSector().getPersistentData().put(MiscellaneousThemeGenerator.PK_NEXUS_KEY, nexus);
			Global.getSector().getMemoryWithoutUpdate().set(MiscellaneousThemeGenerator.PK_NEXUS_KEY, nexus.getContainingLocation().getId());

			Misc.addDefeatTrigger(nexus, "PKNexusDefeated");
		} else {
			Console.showMessage("No replacement nexus found");
		}
	}
	
	public static void debugMission(HubMissionWithTriggers mission) {
		BaseHubMission.LocData loc = new BaseHubMission.LocData(BaseHubMission.EntityLocationType.HIDDEN_NOT_NEAR_STAR, null, Global.getSector().getCurrentLocation());
		loc.loc = new BaseThemeGenerator.EntityLocation();
		loc.loc.type = BaseThemeGenerator.LocationType.OUTER_SYSTEM;
		loc.loc.location = new Vector2f(10000, 10000);
		loc.loc.orbit = null;
		SectorEntityToken token = mission.spawnMissionNode(loc);
		Misc.makeImportant(token, "test");
	}
	
	/*
		runcode MarketAPI market = Global.getSector().getEntityById("system_185c:planet_1").getMarket();
		exerelin.TestScript.createColonyStatic(market);
	*/
	public static void createColonyStatic(MarketAPI market) 
	{
		log.info("Colonizing market " + market.getName() + ", " + market.getId());
		String factionId = Factions.PLAYER;
		
		market.setSize(3);
		market.addCondition("population_3");
		market.setFactionId(factionId);
		market.setPlanetConditionMarketOnly(false);
		
		if (market.hasCondition(Conditions.DECIVILIZED))
		{
			market.removeCondition(Conditions.DECIVILIZED);
			market.addCondition(Conditions.DECIVILIZED_SUBPOP);
		}
		market.addIndustry(Industries.POPULATION);
					
		// submarkets
		market.addSubmarket(Submarkets.LOCAL_RESOURCES);
		market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
		
		market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
		for (MarketConditionAPI cond : market.getConditions())
		{
			cond.setSurveyed(true);
		}
		
		Global.getSector().getEconomy().addMarket(market, true);
		market.getPrimaryEntity().setFaction(factionId);	// http://fractalsoftworks.com/forum/index.php?topic=8581.0
		
		market.setPlayerOwned(true);
		market.addIndustry(Industries.SPACEPORT);
		SubmarketAPI storage = market.getSubmarket(Submarkets.SUBMARKET_STORAGE);
		if (storage != null)
			((StoragePlugin)storage.getPlugin()).setPlayerPaidToUnlock(true);
	}
}
