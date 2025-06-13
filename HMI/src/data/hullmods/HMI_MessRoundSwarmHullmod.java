package data.hullmods;

import java.util.ArrayList;
import java.util.List;

import java.awt.Color;
import java.util.Random;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.combat.threat.FragmentSwarmHullmod;
import com.fs.starfarer.api.impl.combat.threat.RoilingSwarmEffect;
import com.fs.starfarer.api.impl.combat.threat.RoilingSwarmEffect.RoilingSwarmParams;
import com.fs.starfarer.api.impl.combat.threat.SwarmLauncherEffect;
import com.fs.starfarer.api.impl.combat.threat.VoltaicDischargeOnFireEffect;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.util.ColorShifterUtil;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import static com.fs.starfarer.api.impl.combat.threat.FragmentSwarmHullmod.CONSTRUCTION_SWARM_FLOCKING_CLASS;

/**
 * Hullmod that creates a fragment swarm around the ship. This swarm is required to power "fragment" weapons.
 * 
 * @author Alex
 *
 */
public class HMI_MessRoundSwarmHullmod extends BaseHullMod {
	
	public static String STANDARD_SWARM_EXCHANGE_CLASS = "standard_swarm_exchange_class";
	public static float IMPACT_VOLUME_MULT = 0.2f;


	public void advanceInCombat(ShipAPI ship, float amount) {
		CombatEngineAPI engine = Global.getCombatEngine();
		if (engine == null) {
			return;
		}
		if (ship == null) return;
		if (ship.isLiftingOff()){
			ship.setDoNotRender(true);
		}

		RoilingSwarmEffect swarm = RoilingSwarmEffect.getSwarmFor(ship);
		if (swarm == null) {
			swarm = createSwarmFor(ship);
		}

		if (ship.isFighter()) {
			ship.setDoNotRender(true);
			ship.setExplosionScale(0f);
			ship.setHulkChanceOverride(0f);
			ship.setImpactVolumeMult(IMPACT_VOLUME_MULT);
			ship.getArmorGrid().clearComponentMap(); // no damage to weapons/engines
		}
	}
	
	
	public static RoilingSwarmEffect createSwarmFor(ShipAPI ship) {
		RoilingSwarmEffect existing = RoilingSwarmEffect.getSwarmFor(ship);
		if (existing != null) return existing;
		RoilingSwarmParams params = new RoilingSwarmParams();

		float radius = 20f;
		int numMembers = 20;

		params.spriteCat = "misc";
		params.spriteKey = "mess_swarm_round_pieces";

		if (ship.isFighter()) {


		String wingId = ship.getWing() == null ? null : ship.getWing().getWingId();
		if (SwarmLauncherEffect.SWARM_RADIUS.containsKey(wingId)) {
			radius = SwarmLauncherEffect.SWARM_RADIUS.get(wingId);
		}
		if (SwarmLauncherEffect.FRAGMENT_NUM.containsKey(wingId)) {
			numMembers = SwarmLauncherEffect.FRAGMENT_NUM.get(wingId);
		}

		params.despawnSound = "hit_hull_solid_energy";

		params.memberExchangeClass = STANDARD_SWARM_EXCHANGE_CLASS;
		params.flockingClass = FragmentSwarmHullmod.STANDARD_SWARM_FLOCKING_CLASS;
		params.maxSpeed = ship.getMaxSpeedWithoutBoost() +
				Math.max(ship.getMaxSpeedWithoutBoost() * 0.25f + 50f, 100f);

		params.flashRateMult = 0.25f;
		params.flashCoreRadiusMult = 0f;
		params.flashRadius = 120f;
		params.flashFringeColor = new Color(34,0, 255,40);
		params.flashCoreColor = new Color(255,255,255,127);

		// if this is set to true and the swarm is glowing, missile-fragments pop over the glow and it looks bad
		//params.renderFlashOnSameLayer = true;

		params.maxOffset = radius;
		params.initialMembers = numMembers;
		params.baseMembersToMaintain = params.initialMembers;


		} else {

			//params.memberExchangeClass = STANDARD_SWARM_EXCHANGE_CLASS;
			params.memberExchangeClass = CONSTRUCTION_SWARM_FLOCKING_CLASS;
			params.flockingClass = FragmentSwarmHullmod.STANDARD_SWARM_FLOCKING_CLASS;




			params.flashRateMult = 0.25f;
			params.flashCoreRadiusMult = 0f;
			params.flashRadius = 120f;
			params.flashFringeColor = new Color(25, 0, 255,40);
			params.flashCoreColor = new Color(255,255,255,127);

			params.baseSpringFreeLength = 5f;
			params.springFreeLengthRange = 5f;
			params.minSpeedForFriction = 20f;
			//params.maxSpeed = ship.getMaxSpeedWithoutBoost() + Math.max(ship.getMaxSpeedWithoutBoost() * 0.25f + 50f, 100f);

			params.maxSpeed = 2000f;

			params.minOffset = 0f;
			params.maxOffset = Math.min(100f, ship.getCollisionRadius() * 0.75f);

			params.offsetRotationDegreesPerSecond = 4f;
			params.despawnSound = null; // ship explosion does the job instead

			params.initialMembers = getBaseSwarmSize(ship.getHullSize());

			params.baseMembersToMaintain = params.initialMembers;
		}

		List<WeaponAPI> glowWeapons = new ArrayList<>();
		for (WeaponAPI w : ship.getAllWeapons()) {
			if (w.usesAmmo() && w.getSpec().hasTag(Tags.FRAGMENT_GLOW)) {
				glowWeapons.add(w);
			}
			if (w.getSpec().hasTag(Tags.OVERSEER_CHARGE) ||
					(ship.isFighter() && w.getSpec().hasTag(Tags.OVERSEER_CHARGE_FIGHTER))) {
				w.setAmmo(0);
			}
		}



		return new RoilingSwarmEffect(ship, params) {
			protected ColorShifterUtil glowColorShifter = new ColorShifterUtil(new Color(0, 0, 0, 0));
			protected boolean resetFlash = false;
			
			@Override
			public int getNumMembersToMaintain() {
					return (int)Math.round(((0.2f + 0.8f * ship.getHullLevel()) * super.getNumMembersToMaintain()));
			}

			@Override
			public void advance(float amount) {
				super.advance(amount);
				
				glowColorShifter.advance(amount);
				
				// this is actually QUITE performance-intensive on the rendering, at least doubles the cost per swarm
				// (comment was from when flashFrequency was *10 with a shorter flashRateMult; *2 is pretty ok -am
				if (VoltaicDischargeOnFireEffect.isSwarmPhaseMode(ship)) {
					params.flashFrequency = 4f;
					params.flashProbability = 0.5f;
					resetFlash = true;
				} else {
					if (!glowWeapons.isEmpty()) {
						float ammoFractionTotal = 0f;
						float totalOP = 0f;
						for (WeaponAPI w : glowWeapons) {
							float f = w.getAmmo() / Math.max(1f, w.getMaxAmmo());
							Color glowColor = w.getSpec().getGlowColor();
	//						if (f > 0) {
	//							glowColorShifter.shift(w, glowColor, 0.5f, 0.5f, 1f);
	//						}
							glowColorShifter.shift(w, glowColor, 0.5f, 0.5f, 1f);
							float weight = w.getSpec().getOrdnancePointCost(null);
							ammoFractionTotal += f * weight;
							totalOP += weight;
						}
						
						float ammoFraction = ammoFractionTotal / Math.max(1f, totalOP);
	 					params.flashFrequency = (1f + ammoFraction) * 2f;
	 					params.flashFrequency *= Math.max(1f, Math.min(2f, params.baseMembersToMaintain / 50f));
						params.flashProbability = 1f;
						if (ammoFraction <= 0f) {
							params.flashProbability = 0f;
						}
						//params.flashFringeColor = new Color(255,0,0,(int)(30f + 30f * ammoFraction));
						//float glowAlphaBase = 50f;
						float glowAlphaBase = 30f;
						if (ship.isFighter()) {
							glowAlphaBase = 18f;
						}
						
						float extraGlow = (totalOP - 10f) / 90f;
						if (extraGlow < 0) extraGlow = 0;
						if (extraGlow > 1f) extraGlow = 1f;
						
						int glowAlpha = (int)(glowAlphaBase + glowAlphaBase * (ammoFraction + extraGlow * 0.5f));
						if (glowAlpha > 255) glowAlpha = 255;
						//params.flashFringeColor = Misc.setAlpha(glowColorShifter.getCurr(), glowAlpha);
						params.flashFringeColor = Misc.setBrightness(glowColorShifter.getCurr(), 255);
						params.flashFringeColor = Misc.setAlpha(params.flashFringeColor, glowAlpha);
						
						resetFlash = true;
					} else {
						//if (ThreatSwarmAI.isAttackSwarm(ship)) {
						if (resetFlash) {
							params.flashProbability = 0f;
							resetFlash = false;
						}
					}
				}
				
//				int flashing = 0;
//				for (SwarmMember p : members) {
//					if (p.flash != null) {
//						flashing++;
//					}
//				}
//				System.out.println("Flashing: " + flashing + ", total: " + members.size());
			}
			
		};
	}

	public static int getBaseSwarmSize(HullSize size) {
		switch (size) {
			case CAPITAL_SHIP: return 100;
			case CRUISER: return 80;
			case DESTROYER: return 60;
			case FRIGATE: return 50;
			case FIGHTER: return 20;
			case DEFAULT: return 20;
			default: return 20;
		}
	}


}











