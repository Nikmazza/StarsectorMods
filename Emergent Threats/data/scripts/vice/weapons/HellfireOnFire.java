package data.scripts.vice.weapons;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.ShapedExplosionUtil;

public class HellfireOnFire implements BeamEffectPlugin {

	private boolean applied = false;
	private Color pc = new Color(255,150,130,155);
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		if (applied) return;
		
		CombatEntityAPI target = beam.getDamageTarget();
		if (beam.getBrightness() >= 0f) {
			Vector2f origin = beam.getFrom();
			float beamAngle = Misc.getAngleInDegrees(origin, beam.getTo());
			Vector2f shipVector = beam.getSource().getVelocity();
			float shipAngle = Misc.getAngleInDegrees(shipVector);
			float angleDiff = shipAngle - beamAngle;
			if (angleDiff < 0) angleDiff += 360f;
			double magnitude = Math.sqrt((shipVector.getX() * shipVector.getX()) + (shipVector.getY() * shipVector.getY()));
			float shipSpeed = (float) (magnitude * Math.cos((double) angleDiff));
			engine.addSmoothParticle(origin, new Vector2f(), 120f, 1.0f, 1.2f, pc);
			ShapedExplosionUtil.spawnShapedExplosion(origin, beamAngle, shipSpeed, pc, false);
			applied = true; //apply once on firing
		}
	}
}