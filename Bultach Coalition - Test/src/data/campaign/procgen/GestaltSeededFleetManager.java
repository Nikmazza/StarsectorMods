package data.campaign.procgen;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.bt_GestaltCore;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.fleets.SourceBasedFleetManager;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.lazylib.MathUtils;

import java.util.*;

public class GestaltSeededFleetManager extends SourceBasedFleetManager {

	protected int minPts;
	protected int maxPts;
	protected int totalLost;

	public GestaltSeededFleetManager(SectorEntityToken source, float thresholdLY, int minFleets, int maxFleets, float respawnDelay,
									 int minPts, int maxPts) {
		super(source, thresholdLY, minFleets, maxFleets, respawnDelay);
		this.minPts = minPts;
		this.maxPts = maxPts;
		if (this.maxPts < this.minPts) {
			this.maxPts = this.minPts;
		}
	}

	public static SectorEntityToken pickEntityToGuard(Random random, StarSystemAPI homeSystem, CampaignFleetAPI fleet) {
		return null;
	}

	@Override
	protected CampaignFleetAPI spawnFleet() {
		SectorEntityToken spawnAtEntity = this.source;

		if (spawnAtEntity == null) {
			return null;
		}

		Random random = new Random();

		int combatPoints;
		if (this.maxPts <= this.minPts) {
			combatPoints = this.minPts;
		} else {
			combatPoints = this.minPts + random.nextInt(this.maxPts - this.minPts + 1);
		}

		int bonus = totalLost * 2;
		if (this.maxPts > 0 && bonus > this.maxPts) {
			bonus = this.maxPts;
		} else if (this.maxPts <= 0) {
			bonus = 0;
		}
		combatPoints += bonus;

		String type = FleetTypes.PATROL_SMALL;
		if (combatPoints > 12) type = FleetTypes.PATROL_MEDIUM;
		if (combatPoints > 24) type = FleetTypes.PATROL_LARGE;

		combatPoints *= 6f;

		FleetParamsV3 params = new FleetParamsV3(
				null,
				spawnAtEntity.getLocationInHyperspace(),
				"gestalt",
				1.5f,
				type,
				combatPoints,
				0f, 0f, 0f, 0f, 0f,
				0f
		);
		params.random = random;
		params.withOfficers = false;

		CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

		if (fleet == null || fleet.isEmpty()) {
			return null;
		}

		LocationAPI location = spawnAtEntity.getContainingLocation();
		if (location != null) {
			location.addEntity(fleet);
			fleet.setLocation(spawnAtEntity.getLocation().x, spawnAtEntity.getLocation().y);
			fleet.setFacing(random.nextFloat() * 360f);
		} else {
			return null;
		}
		addGestaltInteractionConfig(fleet);
		fleet.removeAbility(Abilities.EMERGENCY_BURN);
		fleet.removeAbility(Abilities.SENSOR_BURST);
		fleet.removeAbility(Abilities.GO_DARK);

		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_SAW_PLAYER_WITH_TRANSPONDER_ON, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_FLEET, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE, false);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOLD_VS_STRONGER, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_FIGHT_TO_THE_LAST, true);

		addGestaltInteractionConfig(fleet);

		if (location instanceof StarSystemAPI) {
			fleet.addScript(new GestaltAssignmentAI(fleet, (StarSystemAPI) location, spawnAtEntity));
		} else {
			fleet.addScript(new GestaltAssignmentAI(fleet, null, spawnAtEntity));
		}

		addGestaltCoresToFleet(fleet);

		return fleet;
	}

	public static void initGestaltFleetProperties(Random random, CampaignFleetAPI fleet, boolean dormant) {
		if (random == null) {
			random = new Random();
		}

		fleet.removeAbility(Abilities.EMERGENCY_BURN);
		fleet.removeAbility(Abilities.SENSOR_BURST);
		fleet.removeAbility(Abilities.GO_DARK);

		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_SAW_PLAYER_WITH_TRANSPONDER_ON, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_FLEET, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOLD_VS_STRONGER, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true);

		if (dormant) {
			fleet.setTransponderOn(false);
			fleet.getMemoryWithoutUpdate().set("$cfai_makeAllowDisengage", true);
			fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
			fleet.setAI(null);
			fleet.setNullAIActionText("dormant");
		} else {
			fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE, false);
			fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_FIGHT_TO_THE_LAST, true);
		}

		addGestaltInteractionConfig(fleet);
		long salvageSeed = random.nextLong();
		fleet.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SEED, salvageSeed);
	}

	public static void addGestaltInteractionConfig(CampaignFleetAPI fleet) {
		fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_INTERACTION_DIALOG_CONFIG_OVERRIDE_GEN, new GestaltFleetInteractionConfigGen());
	}

	public static class GestaltFleetInteractionConfigGen implements FleetInteractionDialogPluginImpl.FIDConfigGen {
		public GestaltFleetInteractionConfigGen() {
		}

		public FleetInteractionDialogPluginImpl.FIDConfig createConfig() {
			FleetInteractionDialogPluginImpl.FIDConfig config = new FleetInteractionDialogPluginImpl.FIDConfig();
			config.showTransponderStatus = false;
			config.alwaysAttackVsAttack = true;
			config.alwaysHarry = true;
			config.delegate = new FleetInteractionDialogPluginImpl.BaseFIDDelegate() {
				@Override
				public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
					bcc.aiRetreatAllowed = false;
					bcc.fightToTheLast = true;
				}
			};
			return config;
		}
	}

	public static void addGestaltCoresToFleet(CampaignFleetAPI fleet){
		Random officerRandom = new Random();

		fleet.setCommander(new bt_GestaltCore().createPerson("bt_gestalt_core", "gestalt", officerRandom));
		for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()){
			float prob = MathUtils.getRandomNumberInRange(0f,100f);
			if ((prob >= 60f) || member.isCapital()) {
				member.setCaptain(new bt_GestaltCore().createPerson("bt_gestalt_core", "gestalt", new Random()));
			}
		}
	}

	@Override
	public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
		super.reportFleetDespawnedToListener(fleet, reason, param);
		if (reason == CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE) {
			totalLost++;
		}
	}
}