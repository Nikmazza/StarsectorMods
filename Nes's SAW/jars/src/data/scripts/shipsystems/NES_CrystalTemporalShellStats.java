package data.scripts.shipsystems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI; //afterimages stuff
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil; //afterimages stuff
import org.lazywizard.lazylib.FastTrig; //afterimages stuff
import org.lwjgl.util.vector.Vector2f; //afterimages stuff

import static data.scripts.utils.NES_Util.txt;

//With Nia's gracious permission, added custom afterimage effect. Amazing stuff <3

public class NES_CrystalTemporalShellStats extends BaseShipSystemScript {

	private final Integer TURN_BONUS = 200;

	private final Integer ACCEL_BONUS = 200;
	private final Integer SPEED_BONUS = 200;

	public static final float MAX_TIME_MULT = 5f;
	public static final float BEAM_DAMAGE_BONUS = 0.5f; //extra damage for beams (0.5f is 50% bonus)

	public static final Color JITTER_COLOR = new Color(90,165,255,55);
	public static final Color JITTER_UNDER_COLOR = new Color(90,165,255,155);

	private final IntervalUtil AfterimageInterval = new IntervalUtil(0.25f, 0.25f); //delay between afterimages, default (0.05f, 0.05f)

	//adding stats
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

		//STATS STUFF
		stats.getTurnAcceleration().modifyPercent(id, TURN_BONUS * effectLevel);
		stats.getMaxTurnRate().modifyPercent(id, TURN_BONUS * effectLevel);

		stats.getAcceleration().modifyPercent(id, ACCEL_BONUS * effectLevel);
		stats.getDeceleration().modifyPercent(id, ACCEL_BONUS * effectLevel);
		stats.getMaxSpeed().modifyPercent(id, SPEED_BONUS * effectLevel);

		float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
		stats.getTimeMult().modifyMult(id, shipTimeMult);

		//bullet time effect
		if (player) {
			float playerTimeMult = 1f / (shipTimeMult / 2f);
			Global.getCombatEngine().getTimeMult().modifyMult(id, playerTimeMult);
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
		}

		float beambonus = 1f + BEAM_DAMAGE_BONUS * effectLevel;
		stats.getBeamWeaponDamageMult().modifyMult(id, beambonus);


		//VISUAL STUFF

		//custom jitter, subtler than vanilla
		ship.setJitterShields(false);

		ship.setJitter(id,JITTER_COLOR,0.5f*effectLevel, 3, 10f);
		ship.setJitterUnder(id, JITTER_UNDER_COLOR, 0.5f*effectLevel, 10, 10f);


		//AFTERIMAGES
		AfterimageInterval.advance(Global.getCombatEngine().getElapsedInLastFrame());
		if (AfterimageInterval.intervalElapsed()) {

			// Sprite offset fuckery - Don't you love trigonometry?
			SpriteAPI sprite = ship.getSpriteAPI();
			float offsetX = sprite.getWidth() / 2 - sprite.getCenterX();
			float offsetY = sprite.getHeight() / 2 - sprite.getCenterY();

			float trueOffsetX = (float) FastTrig.cos(Math.toRadians(ship.getFacing() - 90f)) * offsetX - (float) FastTrig.sin(Math.toRadians(ship.getFacing() - 90f)) * offsetY;
			float trueOffsetY = (float) FastTrig.sin(Math.toRadians(ship.getFacing() - 90f)) * offsetX + (float) FastTrig.cos(Math.toRadians(ship.getFacing() - 90f)) * offsetY;

			org.magiclib.util.MagicRender.battlespace(
					Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
					new Vector2f(ship.getLocation().getX() + trueOffsetX, ship.getLocation().getY() + trueOffsetY),
					new Vector2f(0, 0),
					new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
					new Vector2f(0, 0),
					ship.getFacing() - 90f,
					0f,
					new Color(90,165,255,125), //afterimage color
					true, //default true
					0f, //default 0f
					0f, //default 0f
					0f, //default 0f
					0f, //default 0f
					0f, //default 0f
					0.1f, //default 0.1f
					0.1f, //default 0.1f
					0.75f, //trail length, default 1f
					CombatEngineLayers.BELOW_SHIPS_LAYER);

		}


		//vanilla temporal shell engine visuals

		//removing this for proper engines with different hull colors
		//ship.getEngineController().fadeToOtherColor(this, JITTER_COLOR, new Color(0,0,0,0), effectLevel, 0.5f);
		ship.getEngineController().extendFlame(this, -0.25f, -0.25f, -0.25f);

	}

	//removing stats
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

		stats.getTurnAcceleration().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);

		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getMaxSpeed().unmodify(id);

		stats.getTimeMult().unmodify(id);
		Global.getCombatEngine().getTimeMult().unmodify(id);

		stats.getBeamWeaponDamageMult().unmodify(id);
	}

	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData(txt("system_crystalresonance"), false); //when system active show this message
		}
		return null;
	}
}








