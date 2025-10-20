package data.scripts.asm.weapons;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class FermionDecouplerEffect implements BeamEffectPlugin {

	private IntervalUtil fireInterval = new IntervalUtil(0.20f, 0.20f);
	private IntervalUtil feedbackInterval = new IntervalUtil(2.00f, 2.00f);
	private static float STRUCTURE_LOSS = 20; // x5 per second
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		ShipAPI thisShip = beam.getSource();	

		//separate timer as otherwise feedback will only tick on hitting enemy ship instead of every 2s
		if (beam.getBrightness() >= 1f && thisShip.getOwner() == 0 && thisShip.getVariant().hasHullMod("vice_feedback_error")) {
			feedbackInterval.advance(amount);
			if (feedbackInterval.intervalElapsed()) activateFeedback(beam.getWeapon().getLocation(), thisShip, engine);
		}
		
		CombatEntityAPI target = beam.getDamageTarget();
		if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
			float dur = beam.getDamage().getDpsDuration();
			// needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
			fireInterval.advance(dur);
			if (fireInterval.intervalElapsed()) {
				ShipAPI ship = (ShipAPI) target;
				boolean hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getRayEndPrevFrame());
				if (!hitShield && ship.isAlive()) {
					Vector2f point = beam.getRayEndPrevFrame();
					float newHP = target.getHitpoints() - STRUCTURE_LOSS;
					if (newHP > 1) target.setHitpoints(newHP);
					else engine.applyDamage(target, point, 10000f, beam.getDamage().getType(), 0f, true, false, beam.getSource());
					engine.addFloatingDamageText(point, STRUCTURE_LOSS, Misc.FLOATY_HULL_DAMAGE_COLOR, target, beam.getSource());
				}
			}
		}
	}
	
	private static float HIT_DAMAGE = 250f;
	private static float EMP_DAMAGE = 500f;
	private static Color EMP_COLOR = new Color(255,255,150,175);

	//if player is equipped with more than 1 feedback weapon, each weapon causes feedback on pulse firing
	private void activateFeedback(Vector2f weaponLoc, ShipAPI thisShip, CombatEngineAPI engine) {
		engine.spawnEmpArcPierceShields(
				thisShip, //damage source
				weaponLoc, //to
				thisShip, //to anchor 
				thisShip, //target
				DamageType.ENERGY, 
				HIT_DAMAGE, // damage
				EMP_DAMAGE, // emp 
				100000f, // max range 
				"energy_lash_fire", //sound
				30f, //thickness
				EMP_COLOR, //fringe color
				Color.white //core color
		);
	}
}