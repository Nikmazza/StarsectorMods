package data.hullmods.asm;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.threat.ConstructionSwarmSystemScript;
import com.fs.starfarer.api.impl.combat.threat.ThreatCombatStrategyAI;
import com.fs.starfarer.api.impl.combat.threat.ThreatCombatStrategyForBothSidesPlugin;
import com.fs.starfarer.api.impl.combat.threat.ThreatShipReclamationScript;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.RelicCorePlugin;

public class ThreatCompromised extends BaseHullMod {

	public static String SHIP_BEING_RECLAIMED = "ship_being_reclaimed";
	
	public static String HIVE_UNIT = "hive_unit";
	public static String FABRICATOR_UNIT = "fabricator_unit";
	
	public static float AIM_BONUS = 1f;
	public static float MISSILE_GUIDANCE_BONUS = 1f;
	public static float CR_BONUS = 30f;
	public static float SENSOR_PROFILE_MULT = 0.5f;
	public static float EW_PENALTY_MULT = 0.5f;
	
	public static float MODULE_DAMAGE_TAKEN_MULT = 0.5f;
	public static float EMP_DAMAGE_TAKEN_MULT = 0.5f;
	
	public static String RELIC_FACTION_ID = "asm_relic";
	
	public static String CARRIER_MOD = "asm_relic_carrier";
	public static String MOTHERSHIP_MOD = "asm_relic_mothership";
	public static float CARRIER_FRAGMENT_BONUS = 1.5f;
	public static float MOTHERSHIP_FRAGMENT_BONUS = 2f;
	public static float CR_MULT_WRONG_CORE = -0.3f;
	
	public static String DELTA_RELIC_ID = "asm_relic_delta";
	public static String GAMMA_RELIC_ID = "asm_relic_gamma";
	public static String BETA_RELIC_ID = "asm_relic_beta";
	
	private static Map DP_INCREASE = new HashMap();
	static {
		DP_INCREASE.put(HullSize.FIGHTER, 0f);
		DP_INCREASE.put(HullSize.FRIGATE, 2f);
		DP_INCREASE.put(HullSize.DESTROYER, 4f);
		DP_INCREASE.put(HullSize.CRUISER, 7f);
		DP_INCREASE.put(HullSize.CAPITAL_SHIP, 10f);
	}
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ConstructionSwarmSystemScript.init();
		
		stats.getAutofireAimAccuracy().modifyFlat(id, AIM_BONUS);
		stats.getMissileGuidance().modifyFlat(id, MISSILE_GUIDANCE_BONUS);
		
		stats.getSensorProfile().modifyMult(id, SENSOR_PROFILE_MULT);
		
		stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_PENALTY_MOD).modifyMult(id, EW_PENALTY_MULT);
		
		stats.getEmpDamageTakenMult().modifyMult(id, EMP_DAMAGE_TAKEN_MULT);
		stats.getEngineDamageTakenMult().modifyMult(id, MODULE_DAMAGE_TAKEN_MULT);
		stats.getWeaponDamageTakenMult().modifyMult(id, MODULE_DAMAGE_TAKEN_MULT);
		stats.getDynamic().getMod(Stats.CAN_REPAIR_MODULES_UNDER_FIRE).modifyFlat(id, 1f);
		
		if (stats.getVariant() != null && stats.getVariant().hasHullMod(CARRIER_MOD)) {
			stats.getDynamic().getStat(Stats.FRAGMENT_SWARM_RESPAWN_RATE_MULT).modifyMult(id, CARRIER_FRAGMENT_BONUS);
			stats.getDynamic().getMod(Stats.FRAGMENT_SWARM_SIZE_MOD).modifyMult(id, CARRIER_FRAGMENT_BONUS);
		} 
		else if (stats.getVariant() != null && stats.getVariant().hasHullMod(MOTHERSHIP_MOD)) {
			stats.getDynamic().getMod(Stats.FRAGMENT_SWARM_RESPAWN_RATE_MULT).modifyMult(id, MOTHERSHIP_FRAGMENT_BONUS);
			stats.getDynamic().getMod(Stats.FRAGMENT_SWARM_SIZE_MOD).modifyMult(id, MOTHERSHIP_FRAGMENT_BONUS);
		}
		
		boolean isPlayerOwned = false;
		if (stats.getFleetMember() != null && Global.getSector().getPlayerFleet() != null) {
			List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
			for (FleetMemberAPI member : fleetList) {
				if (member.getVariant().getHullVariantId() == stats.getVariant().getHullVariantId()) {
					isPlayerOwned = true;
				}
			}
		}
		
		if (isPlayerOwned) {
			float dp = (Float) DP_INCREASE.get(hullSize);
			stats.getDynamic().getMod("deployment_points_mod").modifyFlat(id, dp);
			stats.getSuppliesToRecover().modifyFlat(id, dp);
			stats.getSuppliesPerMonth().modifyFlat(id, dp);
		}
		
		boolean makeDelta = false;
		boolean makeGamma = false;
		boolean makeBeta = false;
		//if ship is in non-player fleet and lacks core, capitals beta, cruisers gamma, destroyer/frig gamma
		if (!isPlayerOwned && stats.getFleetMember() != null && stats.getFleetMember().getCaptain() != null) {
			if (stats.getFleetMember().getCaptain().isDefault()) {
				String facId = "";
				try {
					facId = stats.getFleetMember().getFleetData().getFleet().getFaction().getId();
				}
				catch (Exception e) {
					facId = "";
				} 
				if (facId.equals(RELIC_FACTION_ID)) {
					ShipVariantAPI v = stats.getVariant();
					if (v.getHullSize().equals(HullSize.CAPITAL_SHIP)) makeBeta = true;
					else if (v.getHullSize().equals(HullSize.CRUISER)) makeGamma = true;
					else if (v.getHullSize().equals(HullSize.DESTROYER)
							|| v.getHullSize().equals(HullSize.FRIGATE)) makeDelta = true;
				}
			}
		}
		
		if (makeDelta) {
			PersonAPI p = new RelicCorePlugin().createPerson(DELTA_RELIC_ID, RELIC_FACTION_ID, null);
			stats.getFleetMember().setCaptain(p);
		}
		
		else if (makeGamma) {
			PersonAPI p = new RelicCorePlugin().createPerson(GAMMA_RELIC_ID, RELIC_FACTION_ID, null);
			stats.getFleetMember().setCaptain(p);
		}
		else if (makeBeta) {
			PersonAPI p = new RelicCorePlugin().createPerson(BETA_RELIC_ID, RELIC_FACTION_ID, null);
			stats.getFleetMember().setCaptain(p);
		}
		
		//-100 percent readiness penalty
		//do not apply if ship is not in fleet, ship has no captain, or ship captain AI core id is Infected Core
		boolean isPenalty = true;
		if (stats.getFleetMember() == null || stats.getFleetMember().getCaptain() == null) isPenalty = false;
		else if (stats.getFleetMember().getCaptain().isDefault()) isPenalty = false;
		else if (DELTA_RELIC_ID.equals(stats.getFleetMember().getCaptain().getAICoreId())
				|| GAMMA_RELIC_ID.equals(stats.getFleetMember().getCaptain().getAICoreId())
				|| BETA_RELIC_ID.equals(stats.getFleetMember().getCaptain().getAICoreId())
				|| "xo_synthesis_core".equals(stats.getFleetMember().getCaptain().getAICoreId())
				|| "ix_command_core".equals(stats.getFleetMember().getCaptain().getAICoreId())) isPenalty = false;
		if (isPenalty) stats.getMaxCombatReadiness().modifyFlat(id, CR_MULT_WRONG_CORE, "Hostile hardware architecture");
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		
	}
	
	@Override
	public void applyEffectsAfterShipAddedToCombatEngine(ShipAPI ship, String id) {
		CombatEngineAPI engine = Global.getCombatEngine();
		if (!engine.hasPluginOfClass(ThreatCombatStrategyForBothSidesPlugin.class)) {
			engine.addPlugin(new ThreatCombatStrategyForBothSidesPlugin());
		} 
	}
	
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);
		
		if (!ship.isHulk() || ship.hasTag(SHIP_BEING_RECLAIMED)) return;
		if (ThreatCombatStrategyAI.isFabricator(ship)) return;
		
		Float elapsedAsHulk = new Float(0);
		String key = "elapsedAsHulkKey";
		if (ship.getCustomData().containsKey(key)) {
			elapsedAsHulk = (Float) ship.getCustomData().get(key);
		}
		elapsedAsHulk += amount;
		ship.setCustomData(key, elapsedAsHulk);
		if (elapsedAsHulk > 1) {
			CombatEngineAPI engine = Global.getCombatEngine();
			int owner = ship.getOriginalOwner();
			boolean found = false;
			for (ShipAPI curr : engine.getShips()) {
				if (curr == ship || curr.getOwner() != owner) continue;
				if (curr.isHulk() || curr.getOwner() == 100) continue;
				if (!ThreatCombatStrategyAI.isFabricator(curr)) continue;
				if (curr.getCurrentCR() >= 1f) continue;
				found = true;
				break;
			}
			if (found) {
				Global.getCombatEngine().addPlugin(new ThreatShipReclamationScript(ship, 3f));
			} else {
				ship.setCustomData(key, 0f);
			}
		}
		
		String key2 = "asm_checkRetreatKey";
		if (!ship.getCustomData().containsKey(key2)) {
			boolean runOnce = true;
			ship.setCustomData(key2, runOnce);
			if (ship.getFleetMember() != null && ship.getFleetMember().getOwner() == 1) {
				CombatEngineAPI engine = Global.getCombatEngine();
				engine.getContext().aiRetreatAllowed = false;
			}
		}		
	}
	
	@Override
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
	}
	
	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return true;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
		Color t = Misc.getTextColor();
		Color g = Misc.getGrayColor();

		tooltip.addSectionHeading("Combat", Alignment.MID, opad);
		tooltip.addPara("Sensor profile reduced by %s. Target leading accuracy increased to maximum for all weapons, including missiles. Effect of enemy ECM rating reduced by %s.", opad, h,
				"" + (int) Math.round(SENSOR_PROFILE_MULT * 100f) + "%",
				"" + (int) Math.round(EW_PENALTY_MULT * 100f) + "%");
		tooltip.addPara("Weapon and engine damage taken is reduced by %s. EMP damage taken is reduced by %s. In "
				+ "addition, repairs of damaged but functional weapons and engines can continue while they are under fire.",
				opad, h,
				"" + (int) Math.round((1f - MODULE_DAMAGE_TAKEN_MULT) * 100f) + "%",
				"" + (int) Math.round((1f - EMP_DAMAGE_TAKEN_MULT) * 100f) + "%");
		tooltip.addPara("Fragment replacement rate and swarm size increased by %s for carriers and %s for motherships. The mothership will %s fragments from %s ship wrecks.", opad, h,
				"50%", "100%", "reclaim", "Threat Infected");
		tooltip.addSectionHeading("Logistics", Alignment.MID, opad);
		tooltip.addPara("Hostile hardware architecture increases deployment and supply cost under player control by %s/%s/%s/%s, based on hull size. Combat readiness reduced by %s when not operated by a %s or default command AI.", opad, h,
				"" + (int) Math.round((Float) DP_INCREASE.get(HullSize.FRIGATE)),
				"" + (int) Math.round((Float) DP_INCREASE.get(HullSize.DESTROYER)),
				"" + (int) Math.round((Float) DP_INCREASE.get(HullSize.CRUISER)),
				"" + (int) Math.round((Float) DP_INCREASE.get(HullSize.CAPITAL_SHIP)),
				"" + (int) (CR_MULT_WRONG_CORE * -100f) + "%",
				"Infected AI Core");
	}
	
	public float getTooltipWidth() {
		return super.getTooltipWidth();
	}
}