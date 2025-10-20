package data.scripts.ix.weapons;

import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

public class StormwallSystemOnFireEffect implements OnFireEffectPlugin {
	
	private static Color CORE_COLOR = new Color(200,200,255,175);
	private static Color FRINGE_COLOR = new Color(0,100,255,175);
	
    public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine) {
		ShipAPI ship = proj.getSource();
		if (ship != null) proj.setFacing(ship.getFacing());
		Vector2f projLoc = MathUtils.getPointOnCircumference(proj.getLocation(), 14f, weapon.getCurrAngle());
		
        engine.addHitParticle(
				projLoc,
				ship.getVelocity(),
				40.0f, //size
				1.0f, //brightness
				0.25f, //duration
				FRINGE_COLOR);
		
		projLoc = MathUtils.getPointOnCircumference(proj.getLocation(), 8f, weapon.getCurrAngle());
		
		engine.addHitParticle(
				projLoc,
				ship.getVelocity(),
				20.0f, //size
				2.5f, //brightness
				0.15f, //duration
				CORE_COLOR);
    }
}