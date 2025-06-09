package data.scripts.vice.weapons;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.util.ShapedExplosionUtil;

public class BalefireOnFire implements BeamEffectPlugin {

	private boolean applied = false;
	private Color BEAM_COLOR_BALEFIRE = new Color(255,150,130,155);
	private Color BEAM_COLOR_MAYFLY = new Color(255,100,0,75);
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		if (applied) return;
		
		boolean isMayfly = false;
		Color pc = BEAM_COLOR_BALEFIRE;
		if (beam.getWeapon().getSize().equals(WeaponSize.MEDIUM)) isMayfly = true;
		if (isMayfly) pc = BEAM_COLOR_BALEFIRE;
		
		String type = isMayfly ? "deadeye" : "small";
		
		CombatEntityAPI target = beam.getDamageTarget();
		if (beam.getBrightness() >= 0f) {
			Vector2f origin = beam.getFrom();
			float beamAngle = Misc.getAngleInDegrees(origin, beam.getTo());
			float shipSpeed = isMayfly ? 150f : 0f;
			engine.addSmoothParticle(origin, new Vector2f(), 220, 1.0f, 1.3f, pc);
			ShapedExplosionUtil.spawnShapedExplosion(origin, beamAngle, shipSpeed, pc, type);
			applied = true; //apply once on firing
		}
	}
}
