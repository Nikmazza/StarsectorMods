package data.hullmods.orr;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;

public class RevisedArmaments extends BaseHullMod {
	
	public static float CASUALTIES_PERCENT = 50f;
	public static float CR_PENALTY_HMC = 15f;
	public static float CR_PENALTY_GIGA = 1f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if (stats.getVariant().getHullSpec().hasTag("orr_hmc")) {
			if (!stats.getVariant().hasHullMod("automated") && !stats.getVariant().hasHullMod("vice_ai_subsystem_integration")) {
				stats.getCrewLossMult().modifyPercent(id, CASUALTIES_PERCENT);
				stats.getMaxCombatReadiness().modifyFlat(id, -CR_PENALTY_HMC * 0.01f, "Extreme Hazard");
			}
		}
		else if (stats.getVariant().getHullSpec().hasTag("orr_giga")) {
			if (!stats.getVariant().hasHullMod("automated") && !stats.getVariant().hasHullMod("vice_ai_subsystem_integration")) {
				stats.getCrewLossMult().modifyPercent(id, CASUALTIES_PERCENT);
				stats.getMaxCombatReadiness().modifyFlat(id, -CR_PENALTY_GIGA * 0.01f, "Hideous");
			}
		}
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ShipVariantAPI variant = ship.getVariant();
		if (!variant.getHullSpec().hasTag("orr_nal")) return;
		if (variant.hasHullMod("high_scatter_amp")
				|| variant.hasHullMod("vice_adaptive_emitter_diodes")
				|| variant.hasHullMod("vice_attuned_emitter_diodes")) return;
		ship.addListener(new NALHardFlux(ship));
	}
	
	public static class NALHardFlux implements DamageDealtModifier {
		protected ShipAPI ship;
		public NALHardFlux(ShipAPI ship) {
			this.ship = ship;
		}
		
		public String modifyDamageDealt(Object param,
								   		CombatEntityAPI target, DamageAPI damage,
								   		Vector2f point, boolean shieldHit) {
			
			if (!(param instanceof DamagingProjectileAPI) && param instanceof BeamAPI) {
				if (((BeamAPI) param).getWeapon().getId().equals("orr_nal")) {
					damage.setForceHardFlux(true);
				}
			}
			return null;
		}
	}
}