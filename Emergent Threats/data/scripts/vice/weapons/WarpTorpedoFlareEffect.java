package data.scripts.vice.weapons;

import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

import data.scripts.vice.util.ShapedExplosionUtil;

public class WarpTorpedoFlareEffect implements OnFireEffectPlugin {

	private static Color FLARE_COLOR = new Color(70,200,255,200);
	
	public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine) {
		float angle = proj.getAngularVelocity() - 90f;
		Vector2f loc = proj.getLocation();
		Vector2f velocity = proj.getVelocity();
		float speed = (float) Math.sqrt(velocity.lengthSquared());
		ShapedExplosionUtil.spawnShapedExplosion(loc, angle, speed, FLARE_COLOR, "warptorp");
		
	}
}