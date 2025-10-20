package data.scripts.asm.shipsystems;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatFleetManagerAPI;
import com.fs.starfarer.api.combat.EmpArcEntityAPI;
import com.fs.starfarer.api.combat.EmpArcEntityAPI.EmpArcParams;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.FighterWingAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.impl.combat.threat.RoilingSwarmEffect;

import org.lazywizard.lazylib.MathUtils;
import org.magiclib.util.MagicLensFlare;

public class DismantlerSwarms extends BaseShipSystemScript {
	
	public static int MIN_SWARM_COUNT = 1;
	public static int SWARM_PER_DRONE = 50;
	public static float CR_PER_DRONE = 0.02f;
	public static float MIN_CR = 0.1f;
	
	public static String SWARM_WING = "asm_dismantler_wing";
	public static Color JITTER_COLOR = new Color(200,200,150,255);
	private boolean hasSpawned = false;
	private boolean hasConsumedFragments = false;
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		if (hasSpawned == true) return;
		
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) ship = (ShipAPI) stats.getEntity();
		else return;
		
		if (effectLevel > 0f && !hasConsumedFragments) {
			RoilingSwarmEffect swarm = RoilingSwarmEffect.getSwarmFor(ship);
			int swarmCount = swarm == null ? 0 : swarm.getNumActiveMembers();
			if (swarmCount >= SWARM_PER_DRONE * 2) swarm.despawnMembers(SWARM_PER_DRONE * 2);
			else if (swarmCount >= SWARM_PER_DRONE) {
				swarm.despawnMembers(SWARM_PER_DRONE);
				ship.setCurrentCR(ship.getCurrentCR() - CR_PER_DRONE);
			}
			else ship.setCurrentCR(ship.getCurrentCR() - CR_PER_DRONE * 2f);
			hasConsumedFragments = true;
			return;
		}
		else if (effectLevel != 1f) return;
		
		Vector2f loc1 = MathUtils.getPoint(ship.getLocation(), 400f, ship.getFacing() - 200f);
		Vector2f loc2 = MathUtils.getPoint(ship.getLocation(), 400f, ship.getFacing() + 200f);
			
		Vector2f systemLoc = MathUtils.getPoint(ship.getLocation(), 53f, ship.getFacing() - 0f);
		CombatEngineAPI engine = Global.getCombatEngine();
			
		MagicLensFlare.createSharpFlare(
				engine,
				ship,
				systemLoc,
				10,		//thickness
				300,	//length
				0,		//angle
				JITTER_COLOR,	//fringe
				Color.white	//core
		);
			
		EmpArcParams params = new EmpArcParams();
		params.segmentLengthMult = 2f;
		params.zigZagReductionFactor = 0.15f;
		params.fadeOutDist = 400f;
		params.minFadeOutMult = 2f;
		params.flickerRateMult = 0.7f;
			
		EmpArcEntityAPI arc = (EmpArcEntityAPI) engine.spawnEmpArcVisual(
				systemLoc, 
				ship, 
				loc1, 
				ship, 
				30f, 
				JITTER_COLOR,
				Color.white,
				params
		);
		arc.setCoreWidthOverride(50f);
		arc.setSingleFlickerMode(true);
			
		EmpArcEntityAPI arc2 = (EmpArcEntityAPI) engine.spawnEmpArcVisual(
				systemLoc, 
				ship, 
				loc2,
				ship, 
				30f, 
				JITTER_COLOR,
				Color.white,
				params
		);
		arc2.setCoreWidthOverride(50f);
		arc2.setSingleFlickerMode(true);
		
		Global.getSoundPlayer().playSound("unstable_fragment_fire", 1f, 1f, ship.getLocation(), new Vector2f());
		
		CombatFleetManagerAPI manager = Global.getCombatEngine().getFleetManager(ship.getOwner());
		manager.setSuppressDeploymentMessages(true);
		
		ShipAPI swarmer1 = manager.spawnShipOrWing(SWARM_WING, loc1, ship.getFacing(), 0f, null);
		ShipAPI swarmer2 = manager.spawnShipOrWing(SWARM_WING, loc2, ship.getFacing(), 0f, null);
		FighterWingAPI swarmWing1 = swarmer1.getWing();
		FighterWingAPI swarmWing2 = swarmer2.getWing();
		
		/**
		swarmWing1.setSourceShip(ship);
		swarmWing2.setSourceShip(ship);
		
		FighterLaunchBayAPI bay = null;
		for (FighterLaunchBayAPI b : ship.getLaunchBaysCopy()) {
			if (b.getWeaponSlot().getId().equals("LB 001")) swarmWing1.setSourceBay(b);
			else if (b.getWeaponSlot().getId().equals("LB 002")) swarmWing2.setSourceBay(b);
		}
		**/
		
		manager.setSuppressDeploymentMessages(false);
		hasSpawned = true;
	}
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		hasSpawned = false;
		hasConsumedFragments = false;
	}
	
	@Override
	public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
		return enoughFragments(ship) && enoughCR(ship);
		
	}
	
	private boolean enoughFragments(ShipAPI ship) {
		RoilingSwarmEffect swarm = RoilingSwarmEffect.getSwarmFor(ship);
		int swarmCount = swarm == null ? 0 : swarm.getNumActiveMembers();
		return (swarmCount >= MIN_SWARM_COUNT);
	}

	private boolean enoughCR(ShipAPI ship) {
		return ship.getCurrentCR() >= MIN_CR;
	}
	
	@Override
	public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
		if (system.isOutOfAmmo()) return null;
		if (system.getState() != SystemState.IDLE) return null;
		if (!enoughFragments(ship)) return "LOW FRAGMENTS";
		if (!enoughCR(ship)) return "LOW CR";
		return "READY";
	}
}