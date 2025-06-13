package data.campaign.fleets;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.fleets.BaseRouteFleetManager;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.OptionalFleetData;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteData;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteSegment;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.tutorial.TutorialMissionIntel;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class BRVScavFleetRouteManager extends BaseRouteFleetManager {

	public static String BRV_SCAV_FLEET = "$brv_scav_fleet";



	protected StarSystemAPI system;
	
	public BRVScavFleetRouteManager(StarSystemAPI system) {
		super(1f, 14f);
		this.system = system;
	}

	protected String getRouteSourceId() {
		return "salvage_" + system.getId();
	}

	protected int getMaxFleets() {
		//if (true) return 0;
		int numBRVMarkets = 0;
		for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
			if (market.getFactionId().contentEquals("brighton")) {
				numBRVMarkets++;
			}
		}

		float salvage = getVeryApproximateSalvageValue(system);
		return (int) (1 + Math.min(salvage*numBRVMarkets * 3, 7));
	}
	
	protected void addRouteFleetIfPossible() {
		if (TutorialMissionIntel.isTutorialInProgress()) {
			return;
		}
		
		MarketAPI market = pickSourceMarket();
		if (market == null) return;
		
		Long seed = new Random().nextLong();
		String id = getRouteSourceId();
		
		OptionalFleetData extra = new OptionalFleetData(market);
		
		RouteData route = RouteManager.getInstance().addRoute(id, market, seed, extra, this);
		
		float distLY = Misc.getDistanceLY(market.getLocationInHyperspace(), system.getLocation());
		float travelDays = distLY * 1.5f;
		
		float prepDays = 2f + (float) Math.random() * 3f;
		float endDays = 8f + (float) Math.random() * 3f; // longer since includes time from jump-point to source

		float totalTravelTime = prepDays + endDays + travelDays * 2f;
		float stayDays = Math.max(20f, totalTravelTime);
		
		route.addSegment(new RouteSegment(prepDays, market.getPrimaryEntity()));
		route.addSegment(new RouteSegment(travelDays, market.getPrimaryEntity(), system.getCenter()));
		route.addSegment(new RouteSegment(stayDays, system.getCenter()));
		route.addSegment(new RouteSegment(travelDays, system.getCenter(), market.getPrimaryEntity()));
		route.addSegment(new RouteSegment(endDays, market.getPrimaryEntity()));
	}
	
	
	public static float getVeryApproximateSalvageValue(StarSystemAPI system) {
		return system.getEntitiesWithTag(Tags.SALVAGEABLE).size();
	}
	
	public MarketAPI pickSourceMarket() {
//		if (true) {
//			return Global.getSector().getEconomy().getMarket("jangala");
//		}
		
		WeightedRandomPicker<MarketAPI> markets = new WeightedRandomPicker<MarketAPI>();
		
		for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
			if (market.getFaction().isHostileTo("brighton")) continue;
			if (market.getContainingLocation() == null) continue;
			if (market.getContainingLocation().isHyperspace()) continue;
			if (market.isHidden()) continue;
			
			float distLY = Misc.getDistanceLY(system.getLocation(), market.getLocationInHyperspace());
			float weight = market.getSize();
			
			float f = Math.max(0.1f, 1f - Math.min(1f, distLY / 20f));
			f *= f;
			weight *= f;
			
			markets.add(market, weight);
		}
		
		return markets.pick();
	}
	

	public CampaignFleetAPI spawnFleet(RouteData route) {
		Random random = route.getRandom();
		
		WeightedRandomPicker<String> picker = new WeightedRandomPicker<String>(random);
		picker.add(FleetTypes.SCAVENGER_SMALL, 10f);
		picker.add(FleetTypes.SCAVENGER_MEDIUM, 15f);
		picker.add(FleetTypes.SCAVENGER_LARGE, 5f);
		
		String type = picker.pick();

		boolean pirate = random.nextBoolean();
		CampaignFleetAPI fleet = createBRVScavenger(type, system.getLocation(), route, route.getMarket(), random);
		if (fleet == null) return null;;
		
		fleet.addScript(new BRVScavengerFleetAssignmentAI(fleet, route, pirate));
		
		return fleet;
	}
	
	public static CampaignFleetAPI createBRVScavenger(String type, Vector2f locInHyper, MarketAPI source, Random random) {
		return createBRVScavenger(type, locInHyper, null, source,  random);
	}
	public static CampaignFleetAPI createBRVScavenger(String type, Vector2f locInHyper, RouteData route, MarketAPI source, Random random) {
		if (random == null) random = new Random();

		
		if (type == null) {
			WeightedRandomPicker<String> picker = new WeightedRandomPicker<String>(random);
			picker.add(FleetTypes.SCAVENGER_SMALL, 10f);
			picker.add(FleetTypes.SCAVENGER_MEDIUM, 15f);
			picker.add(FleetTypes.SCAVENGER_LARGE, 5f);
			type = picker.pick();
		}
		
		
		int combat = 0;
		int freighter = 0;
		int tanker = 0;
		int transport = 0;
		int utility = 0;
		
		
		if (type.equals(FleetTypes.SCAVENGER_SMALL)) {
			combat = random.nextInt(2) + 1;
			tanker = random.nextInt(2) + 1;
			utility = random.nextInt(2) + 1;
		} else if (type.equals(FleetTypes.SCAVENGER_MEDIUM)) {
			combat = 4 + random.nextInt(5);
			freighter = 4 + random.nextInt(5);
			tanker = 3 + random.nextInt(4);
			transport = random.nextInt(2);
			utility = 2 + random.nextInt(3);
		} else if (type.equals(FleetTypes.SCAVENGER_LARGE)) {
			combat = 7 + random.nextInt(8);
			freighter = 6 + random.nextInt(7);
			tanker = 5 + random.nextInt(6);
			transport = 3 + random.nextInt(8);
			utility = 4 + random.nextInt(5);
		}

		combat *= 5f;
		freighter *= 3f;
		tanker *= 3f;
		transport *= 1.5f;
		
		FleetParamsV3 params = new FleetParamsV3(
				route != null ? route.getMarket() : source, 
				locInHyper,
				"brighton", // quality will always be reduced by non-market-faction penalty, which is what we want
				route == null ? null : route.getQualityOverride(),
				type,
				combat, // combatPts
				freighter, // freighterPts 
				tanker, // tankerPts
				transport, // transportPts
				0f, // linerPts
				utility, // utilityPts
				0f // qualityMod
				);
		if (route != null) {
			params.timestamp = route.getTimestamp();
		}
		params.random = random;
		CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
		
		if (fleet == null || fleet.isEmpty()) return null;

		fleet.setFaction("brighton", true);
		
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_SCAVENGER, true);
		fleet.getMemoryWithoutUpdate().set(BRV_SCAV_FLEET, true);


		return fleet;
	}

	public boolean shouldCancelRouteAfterDelayCheck(RouteData data) {
		return false;
	}

	public boolean shouldRepeat(RouteData route) {
		return false;
	}
	
	public void reportAboutToBeDespawnedByRouteManager(RouteData route) {
		
	}
}







