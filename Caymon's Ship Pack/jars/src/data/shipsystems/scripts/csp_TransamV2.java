package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import org.magiclib.util.MagicRender;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class csp_TransamV2 extends BaseShipSystemScript {
	public static final float MAX_TIME_MULT = 1.5f;
	public static final float MIN_TIME_MULT = 0.1f;
	public static final float DAM_MULT = 0.1f;

	public static final float DAMAGE_BONUS_PERCENT = 25f;
	public static final int HARD_FLUX_DISSIPATION_PERCENT = 25;
	public static final float EXTRA_DAMAGE_TAKEN_PERCENT = 100f;
	public static final float FLUX_REDUCTION = 50f;

	private static final Map mag = new HashMap();
	static {
		mag.put(ShipAPI.HullSize.FIGHTER, 0.33f);
		mag.put(ShipAPI.HullSize.FRIGATE, 0.33f);
		mag.put(ShipAPI.HullSize.DESTROYER, 0.33f);
		mag.put(ShipAPI.HullSize.CRUISER, 0.5f);
		mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 0.5f);
	}
	protected Object STATUSKEY1 = new Object();

	//public static final float INCOMING_DAMAGE_MULT = 0.25f;
	public static final float INCOMING_DAMAGE_CAPITAL = 0.5f;
	public static final Color JITTER_COLOR = new Color(219,74,101,35);
	public static final Color JITTER_UNDER_COLOR = new Color(219,74,101,105);
    private final Color AFTER_IMAGE_COLOR = new Color(219,74,101,105);
	private final IntervalUtil interval = new IntervalUtil(0.2f, 0.2f);

    @Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
            if (!Global.getCombatEngine().isPaused()) {
                interval.advance(Global.getCombatEngine().getElapsedInLastFrame());
				if (interval.intervalElapsed()) {				
					final SpriteAPI sprite = ship.getSpriteAPI();
					final float offsetX = sprite.getWidth()/2 - sprite.getCenterX();
					final float offsetY = sprite.getHeight()/2 - sprite.getCenterY();
					final float trueOffsetX = (float) FastTrig.cos(Math.toRadians(ship.getFacing()-90f))*offsetX - (float)FastTrig.sin(Math.toRadians(ship.getFacing()-90f))*offsetY;
					final float trueOffsetY = (float)FastTrig.sin(Math.toRadians(ship.getFacing()-90f))*offsetX + (float)FastTrig.cos(Math.toRadians(ship.getFacing()-90f))*offsetY;
					MagicRender.battlespace(
						Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
							new Vector2f(ship.getLocation().getX()+trueOffsetX,ship.getLocation().getY()+trueOffsetY),
							new Vector2f(0, 0),
							new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
							new Vector2f(0, 0),
							ship.getFacing() - 90f,
							0f,
							AFTER_IMAGE_COLOR,
							true,
							0f,
							0f,
							0f,
							0f,
							0f,
							0.1f,
							0.1f,
							1f,
							CombatEngineLayers.BELOW_SHIPS_LAYER
					);
				}
			}
		} else {
			return;
		}
		float bonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
		stats.getEnergyWeaponDamageMult().modifyPercent(id, bonusPercent);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - (FLUX_REDUCTION * 0.01f));
		stats.getHardFluxDissipationFraction().modifyFlat(id, (float)HARD_FLUX_DISSIPATION_PERCENT * 0.01f);

		effectLevel = 1f;

		float mult = (Float) mag.get(ShipAPI.HullSize.CRUISER);
		if (stats.getVariant() != null) {
			mult = (Float) mag.get(stats.getVariant().getHullSize());
		}
		stats.getHullDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
		stats.getArmorDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
		stats.getEmpDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);

		if (player) {
			ShipSystemAPI system = ship.getSystem();
			if (system != null) {
				float percent = (1f - mult) * effectLevel * 100;
				Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
						system.getSpecAPI().getIconSpriteName(), system.getDisplayName(),
						(int) Math.round(percent) + "% less damage taken", false);
			}
		}

		float jitterLevel = effectLevel;
		float jitterRangeBonus = 0;
		float maxRangeBonus = 10f;
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

		if (state == State.OUT) {
			stats.getMaxSpeed().unmodify(id); // have
			stats.getMaxTurnRate().unmodify(id);
		} else {
			stats.getMaxSpeed().modifyFlat(id, 25f);
			stats.getAcceleration().modifyPercent(id, 100f * effectLevel);
			stats.getDeceleration().modifyPercent(id, 100f * effectLevel);
			stats.getTurnAcceleration().modifyFlat(id, 15f * effectLevel);
			stats.getTurnAcceleration().modifyPercent(id, 100f * effectLevel);
			stats.getMaxTurnRate().modifyFlat(id, 15f);
			stats.getMaxTurnRate().modifyPercent(id, 50f);
		}
                
		jitterLevel = (float) Math.sqrt(jitterLevel);
		effectLevel *= effectLevel;
		
		ship.setJitter(this, JITTER_COLOR, jitterLevel, 3, 0, 0 + jitterRangeBonus);
		ship.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel, 25, 0f, 7f + jitterRangeBonus);
		
	
		float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
		stats.getTimeMult().modifyMult(id, shipTimeMult);
		if (player) {
			Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
//			if (ship.areAnyEnemiesInRange()) {
//				Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
//			} else {
//				Global.getCombatEngine().getTimeMult().modifyMult(id, 2f / shipTimeMult);
//			}
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
		}

		ship.getEngineController().fadeToOtherColor(this, JITTER_COLOR, new Color(0,0,0,0), effectLevel, 0.5f);
		ship.getEngineController().extendFlame(this, -0.25f, -0.25f, -0.25f);
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

		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getTimeMult().unmodify(id);
		stats.getEnergyWeaponDamageMult().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getEmpDamageTakenMult().unmodify(id);
		stats.getEnergyWeaponFluxCostMod().unmodify(id);
		stats.getHardFluxDissipationFraction().unmodify(id);
	}

    @Override
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
		if (index == 0) {
			return new StatusData("Increased energy weapon damage, speed and time flow altered", false);
		}
//		if (index == ) {
//			return new StatusData("increased speed", false);
//		}
//		if (index == 1) {
//			return new StatusData("increased acceleration", false);
//		}
		return null;
	}
}








