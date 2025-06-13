package data.scripts.vice.weapons;

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
	private boolean wasZero = true;
	private static float STRUCTURE_LOSS = 100; // x5 per second
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		CombatEntityAPI target = beam.getDamageTarget();
		if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
			float dur = beam.getDamage().getDpsDuration();
			// needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
			if (!wasZero) dur = 0;
			wasZero = beam.getDamage().getDpsDuration() <= 0;
			fireInterval.advance(dur);
			
			if (fireInterval.intervalElapsed()) {
				ShipAPI ship = (ShipAPI) target;
				boolean hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getTo());
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
}
