package data.scripts.asm.abilities;

import java.util.Random;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorEntityToken.VisibilityLevel;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory.PatrolType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Pings;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.combat.CRPluginImpl;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.DelayedActionScript;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.TimeoutTracker;

import data.scripts.vice.RelicCorePlugin;
import data.scripts.vice.util.NameListUtil;

public class LureInfectedFleetAbility extends BaseDurationAbility { //implements RouteFleetSpawner
	
	public static class AbilityUseData {
		public long timestamp;
		public Vector2f location;
		public AbilityUseData(long timestamp, Vector2f location) {
			this.timestamp = timestamp;
			this.location = location;
		}
	}
	
	private static float DELAY_DAYS = 0.3f;
	private static float MIN_ABYSSAL_DEPTH = 0.9f;
	private static float MIN_FLEET_POINTS = 120f;
	private static String ASM_FACTION_ID = "asm_relic";
	private static String ASM_LEGION_ID = "asm_legion";
	private static String ASM_LEGION_VAR_ID = "asm_legion_drone";
	
	private static String TW_FACTION_ID = "ix_trinity_asm";
	private static String TW_AURORA_VAR_ID = "aurora_tw_encounter";
	private static String TW_RADIANT_VAR_ID = "radiant_tw_encounter";
	
	private static String DELTA_RELIC_ID = "asm_relic_delta";
	private static String GAMMA_RELIC_ID = "asm_relic_gamma";
	private static String BETA_RELIC_ID = "asm_relic_beta";
	
	protected boolean performed = false;
	
	protected TimeoutTracker<AbilityUseData> uses = new TimeoutTracker<AbilityUseData>();
	
	protected Object readResolve() {
		super.readResolve();
		if (uses == null) uses = new TimeoutTracker<AbilityUseData>();
		return this;
	}
	
	@Override
	protected void activateImpl() {
		if (entity.isInCurrentLocation()) performed = false;
		VisibilityLevel level = entity.getVisibilityLevelToPlayerFleet();
		if (level != VisibilityLevel.NONE) Global.getSector().addPing(entity, Pings.INTERDICT);
	}

	@Override
	protected void applyEffect(float amount, float level) {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;
		if (!performed) {
			performed = true;
			float delay = fleet.isInHyperspace() ? DELAY_DAYS : 0f;
			addLureScript(delay);
		}
	}
	
	@Override
	public void advance(float amount) {
		super.advance(amount);
		float days = Global.getSector().getClock().convertToDays(amount);
		uses.advance(days);
	}
		
	private void addLureScript(float delayDays) {
		CampaignFleetAPI player = getFleet();
		if (player == null) return;
		
		Global.getSector().addScript(new DelayedActionScript(delayDays) {
			@Override
			public void doAction() {
				CampaignFleetAPI fleet = getFleet();
				if (fleet == null) return;
				//special TW encounter always spawns on second use, odds increase can be tweaked if player should wait more
				if (Global.getSector().getMemoryWithoutUpdate().is("$asm_trinity_met", false)) {
					Float odds = (Float) Global.getSector().getMemoryWithoutUpdate().get("$asm_trinity_odds");
					if (Math.random() < odds) {	
						Global.getSector().getMemoryWithoutUpdate().set("$asm_trinity_met", true);
						createTrinityFleet(fleet);
					}
					else {
						odds += 1f;
						Global.getSector().getMemoryWithoutUpdate().set("$asm_trinity_odds", odds);
						createInfectedFleet(fleet);
					}
				}
				else createInfectedFleet(fleet);
			}
		});
	}
	
	private void createInfectedFleet(CampaignFleetAPI fleet) {

		Global.getSector().getFaction(ASM_FACTION_ID).setRelationship("player", RepLevel.HOSTILE);
		
		float points = fleet.getFleetPoints();
		//inflate/deflate fleet size by -35% + 65% of player fp total 
		points = (float) (Math.random() - 0.35f) * points + points;
		if (points < MIN_FLEET_POINTS) points = MIN_FLEET_POINTS;
		
		FleetParamsV3 params = new FleetParamsV3(
				fleet.getLocationInHyperspace(), //locInHyper
				ASM_FACTION_ID,// factionId
				null, //Float qualityOverride, new Float(1f)
				FleetTypes.PATROL_LARGE, //String fleetType
				points, //float combatPts
				0f, //float freighterPts
				0f, //float tankerPts
				0f, //float transportPts
				0f, //float linerPts
				0f, //float utilityPts
				1f //float qualityMod
		);
				
		CampaignFleetAPI enemyFleet = FleetFactoryV3.createFleet(params);
		enemyFleet.inflateIfNeeded();
		enemyFleet.getMemoryWithoutUpdate().set("$ignorePlayerCommRequests", false);
		FleetMemberAPI flagship = enemyFleet.getFlagship();
		//if enemyFleet flagship is not Legion and has Legion, swap to that, otherwise turn flagship into Legion
		if (flagship.getHullId() != ASM_LEGION_ID) {
			boolean hasMothership = false;
			for (FleetMemberAPI m : enemyFleet.getMembersWithFightersCopy()) {
				if (hasMothership) continue;
				if (m.getHullId() == ASM_LEGION_ID) {
					hasMothership = true;
					m.setFlagship(true);
					flagship = m;
				}
			}
			
			if (!hasMothership) {
				FleetMemberAPI ship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, ASM_LEGION_VAR_ID);
				flagship.setVariant(ship.getVariant(), false, true);
				ship.setFlagship(true);
				flagship = ship;
			}
			
			PersonAPI infectedCore = new RelicCorePlugin().createPerson(BETA_RELIC_ID, ASM_FACTION_ID, null);
			flagship.setCaptain(infectedCore);
			enemyFleet.setCommander(infectedCore);
		}
		
		for (FleetMemberAPI m : enemyFleet.getMembersWithFightersCopy()) {
			if (!m.isFlagship() && m.getType().equals(FleetMemberType.SHIP)) {
				m.setCaptain(null); //lets Threat Infected Hull hullmod set the correct Infected Core captain
			}
			m.getRepairTracker().setCR(1f);
			m.setShipName(NameListUtil.ASM_INFESTED_HUSK);
		}
		
		enemyFleet.getFleetData().sort();
		Global.getSector().getHyperspace().addEntity(enemyFleet);
		float x = fleet.isInHyperspace() ? fleet.getLocationInHyperspace().getX() : fleet.getLocation().getX();
		float y = fleet.isInHyperspace() ? fleet.getLocationInHyperspace().getY() : fleet.getLocation().getY();
		
		if (fleet.isInHyperspace()) enemyFleet.setLocation(x, y);
		else {
			SectorEntityToken token = fleet.getContainingLocation().createToken(x, y);
			JumpDestination dest = new JumpDestination(token, null);
			Global.getSector().doHyperspaceTransition(enemyFleet, null, dest, 0f);
		}
		enemyFleet.updateFleetView();
		enemyFleet.addAssignment(FleetAssignment.INTERCEPT, fleet, 30f);
	}
	
	private void createTrinityFleet(CampaignFleetAPI fleet) {
		if (!Global.getSettings().getModManager().isModEnabled("EmergentThreats_IX_Revival")) {
			createInfectedFleet(fleet);
			return;
		}
		
		//Global.getSector().getFaction(TW_FACTION_ID).setRelationship("player", RepLevel.HOSTILE);
		
		float points = fleet.getFleetPoints(); 
		points *= 1.5f;
		if (points < MIN_FLEET_POINTS - 40f) points = MIN_FLEET_POINTS - 40f;
		
		FleetParamsV3 params = new FleetParamsV3(
				fleet.getLocationInHyperspace(), //locInHyper
				TW_FACTION_ID,// factionId
				null, //Float qualityOverride, new Float(1f)
				FleetTypes.PATROL_LARGE, //String fleetType
				points, //float combatPts
				points * 0.2f, //float freighterPts
				0f, //float tankerPts
				0f, //float transportPts
				0f, //float linerPts
				0f, //float utilityPts
				1f //float qualityMod
		);
				
		CampaignFleetAPI enemyFleet = FleetFactoryV3.createFleet(params);
		enemyFleet.inflateIfNeeded();
		enemyFleet.getMemoryWithoutUpdate().set("$ignorePlayerCommRequests", false);
		FleetMemberAPI ship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, TW_AURORA_VAR_ID);
		FleetMemberAPI ship2 = Global.getFactory().createFleetMember(FleetMemberType.SHIP, TW_RADIANT_VAR_ID);
		enemyFleet.getFleetData().addFleetMember(ship);
		enemyFleet.getFleetData().addFleetMember(ship2);
		ship2.setFlagship(true);		
		FleetMemberAPI oldFlagShip = enemyFleet.getFlagship();
		oldFlagShip.setFlagship(false);
		enemyFleet.getFleetData().removeFleetMember(oldFlagShip);		
		ship.getRepairTracker().setCR(0.81f);
		ship2.getRepairTracker().setCR(0.91f);
		//ship.getVariant().addTag(Tags.UNRECOVERABLE); 	done in hullmod
		ship.setShipName(NameListUtil.TWC_MIDIR);
		enemyFleet.getCommander().setRankId("tw_asm_admiral");	
		
		ship2.getVariant().addTag(Tags.VARIANT_ALWAYS_RECOVERABLE);
		ship2.setShipName(NameListUtil.TWC_INTERCESSION);
		
		enemyFleet.getFleetData().addFleetMember("stonefish_tw_strike");
		enemyFleet.getFleetData().addFleetMember("stonefish_tw_strike");
		enemyFleet.getFleetData().addFleetMember("iconoclast_tw_attack");
		enemyFleet.getFleetData().addFleetMember("iconoclast_tw_attack");
		enemyFleet.getFleetData().addFleetMember("howler_tw_escort");
		enemyFleet.getFleetData().addFleetMember("howler_tw_escort");
		enemyFleet.getFleetData().addFleetMember("omen_tw_pd");
		enemyFleet.getFleetData().addFleetMember("omen_tw_pd");
		enemyFleet.getFleetData().addFleetMember("omen_tw_pd");
		
		for (FleetMemberAPI m : enemyFleet.getMembersWithFightersCopy()) {
			if (m.getRepairTracker().getCR() <= 0.6f) m.getRepairTracker().setCR(0.8f);
		}

		enemyFleet.getFleetData().sort();
		Global.getSector().getHyperspace().addEntity(enemyFleet);
		float x = fleet.isInHyperspace() ? fleet.getLocationInHyperspace().getX() : fleet.getLocation().getX();
		float y = fleet.isInHyperspace() ? fleet.getLocationInHyperspace().getY() : fleet.getLocation().getY();
		
		if (fleet.isInHyperspace()) enemyFleet.setLocation(x, y);
		else {
			SectorEntityToken token = fleet.getContainingLocation().createToken(x, y);
			JumpDestination dest = new JumpDestination(token, null);
			Global.getSector().doHyperspaceTransition(enemyFleet, null, dest, 0f);
		}
		enemyFleet.updateFleetView();
		enemyFleet.addAssignment(FleetAssignment.INTERCEPT, fleet, 30f);
	}
	
	public boolean isUsable() {
		if (!super.isUsable()) return false;
		if (getFleet() == null) return false;
		
		CampaignFleetAPI fleet = getFleet();		
		boolean isFirstTime = Global.getSector().getMemoryWithoutUpdate().is("$asm_is_at_circumfix", true);
		if (isFirstTime) return true;
		boolean isValidLocation = Misc.getAbyssalDepthOfPlayer() >= MIN_ABYSSAL_DEPTH 
				|| (fleet.getStarSystem() != null && fleet.getStarSystem().hasTag(Tags.SYSTEM_ABYSSAL));
		//No populated systems
		if (fleet.getStarSystem() != null) {
			StarSystemAPI system = fleet.getStarSystem();
			for (PlanetAPI planet : system.getPlanets()) {
				if (planet.getMarket() != null && planet.getMarket().getSize() >= 3) isValidLocation = false;
			}
		}
		return isValidLocation;
	}
	
	@Override
	protected void deactivateImpl() {
		cleanupImpl();
	}
	
	@Override
	protected void cleanupImpl() {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;
	}
	
	@Override
	public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
		
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null) return;
		
		Color highlight = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
		
		if (!Global.CODEX_TOOLTIP_MODE) {
			LabelAPI title = tooltip.addTitle(spec.getName());
		} 
		else tooltip.addSpacer(-10f);

		float pad = 10f;
		
		tooltip.addPara("Broadcast a Domain Armada recall signal into the deep abyss to lure a nearby Threat Infected fleet to your current location. The fleet will be hostile and attempt to attack you immediately. Usable once every 30 days.", pad, highlight, "Threat Infected", "30");
		
		if (!Global.CODEX_TOOLTIP_MODE) tooltip.addPara("Can only be used within abyssal hyperspace and inside unpopulated abyssal systems.", bad, pad);
		
		//addIncompatibleToTooltip(tooltip, expanded);
	}

	public boolean hasTooltip() {
		return true;
	}
}