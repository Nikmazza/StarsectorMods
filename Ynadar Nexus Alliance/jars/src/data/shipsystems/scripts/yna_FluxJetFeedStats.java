package data.shipsystems.scripts;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import static com.fs.starfarer.api.impl.combat.RecallDeviceStats.getFighters;

public class yna_FluxJetFeedStats extends BaseShipSystemScript {
	public static final Object KEY_JITTER = new Object();
        
	public static final Color JITTER_COLOR = new Color(90,165,255,55);
	public static final Color JITTER_FINAL_COLOR = new Color(255,255,0,55);
	
	public static final float ROF_BONUS = 0.33f;
	public static final float WEAPON_FLUX_REDUCTION = 0.33f;
	
        @Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}
		
		float jitterLevel = effectLevel;
		float jitterRangeBonus = 0;
		float maxRangeBonus = 2f;
		if (state == State.IN) {
			jitterLevel = effectLevel / (1f / ship.getSystem().getChargeUpDur());
			if (jitterLevel > 1) {
				jitterLevel = 1f;
			}
			jitterRangeBonus = jitterLevel * maxRangeBonus;
		} else if (state == State.ACTIVE) {
			jitterLevel = 1f;
			jitterRangeBonus = maxRangeBonus;
		} else if (state == State.OUT) {
			jitterRangeBonus = jitterLevel * maxRangeBonus;
		}
		jitterLevel = (float) Math.sqrt(jitterLevel);
		effectLevel *= effectLevel;
		
		
                float mult = 1f + ROF_BONUS * effectLevel;
                float beam_mult = 1f + (ROF_BONUS * 0.5f) * effectLevel;
                
		if (state == State.IN) {
                    //stats.getTimeMult().modifyMult(id, 1f);
			stats.getMaxSpeed().modifyFlat(id, 50f);
			stats.getAcceleration().modifyPercent(id, 200f);
			stats.getDeceleration().modifyPercent(id, 200f);
			stats.getMaxTurnRate().modifyFlat(id, 15f);
			stats.getMaxTurnRate().modifyPercent(id, 100f);
			stats.getTurnAcceleration().modifyFlat(id, 30f);
			stats.getTurnAcceleration().modifyPercent(id, 200f);
                    
                    ship.setJitter(this, JITTER_COLOR, jitterLevel, 2, 0, 0 + jitterRangeBonus);
                } else if (state == State.ACTIVE) {
                    stats.getMaxSpeed().unmodify(id);
                    stats.getAcceleration().unmodify(id);
                    stats.getDeceleration().unmodify(id);
                    stats.getMaxTurnRate().unmodify(id);
                    stats.getTurnAcceleration().unmodify(id);
                    
                    stats.getBallisticRoFMult().modifyMult(id, mult);
                    stats.getEnergyRoFMult().modifyMult(id, mult);
                    stats.getBeamWeaponDamageMult().modifyMult(id, beam_mult);
                    stats.getBallisticWeaponFluxCostMod().modifyMult(id, WEAPON_FLUX_REDUCTION);
                    stats.getEnergyWeaponFluxCostMod().modifyMult(id, WEAPON_FLUX_REDUCTION);
                    
                    ship.setJitter(this, JITTER_FINAL_COLOR, jitterLevel, 2, 0, 0 + jitterRangeBonus);
                } else if (state == State.OUT) {
                    stats.getBallisticRoFMult().unmodify(id);
                    stats.getEnergyRoFMult().unmodify(id);
                    stats.getBeamWeaponDamageMult().unmodify(id);
                    stats.getBallisticWeaponFluxCostMod().unmodify(id);
                    stats.getEnergyWeaponFluxCostMod().unmodify(id);
                }
                
		if (stats.getEntity() instanceof ShipAPI && false) {
			String key = ship.getId() + "_" + id;
			Object test = Global.getCombatEngine().getCustomData().get(key);
			if (state == State.IN) {
                            ship.getEngineController().extendFlame(this, -0.25f, -0.25f, -0.25f);
                            if (test == null && effectLevel > 0.1f && effectLevel < 0.5f) {
				Global.getCombatEngine().getCustomData().put(key, new Object());
				ship.getEngineController().getExtendLengthFraction().advance(1f);
				for (ShipEngineAPI engine : ship.getEngineController().getShipEngines()) {
					if (engine.isSystemActivated()) {
						ship.getEngineController().setFlameLevel(engine.getEngineSlot(), 1f);
					}
				}
                            }
			} else {
				Global.getCombatEngine().getCustomData().remove(key);
			}
		}
	}
	
	
        @Override
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}

                stats.getMaxSpeed().unmodify(id);
                stats.getAcceleration().unmodify(id);
                stats.getDeceleration().unmodify(id);
                stats.getMaxTurnRate().unmodify(id);
                stats.getTurnAcceleration().unmodify(id);
                
                stats.getBallisticRoFMult().unmodify(id);
                stats.getEnergyRoFMult().unmodify(id);
                stats.getBeamWeaponDamageMult().unmodify(id);
                stats.getBallisticWeaponFluxCostMod().unmodify(id);
                stats.getEnergyWeaponFluxCostMod().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float mult = 1f + ROF_BONUS * effectLevel;
		float bonusPercent = (float) (mult - 1f) * 100f;
		float fluxPercent = 100 - (WEAPON_FLUX_REDUCTION * 100);
		if ((index == 0) && (state == State.IN)) {
			return new StatusData("improved maneuverability", false);
		}
		if ((index == 1) && (state == State.IN)) {
			return new StatusData("+" + (int) 50f + " top speed", false);
		}
		if ((index == 0) && (state == State.ACTIVE)) {
			return new StatusData("-" + (int) fluxPercent + "% non-missile flux generation" , false);
		}
		if ((index == 1) && (state == State.ACTIVE)) {
			return new StatusData("non-missile rate of fire +" + (int) bonusPercent + "%", false);
		}
		return null;
	}
}








