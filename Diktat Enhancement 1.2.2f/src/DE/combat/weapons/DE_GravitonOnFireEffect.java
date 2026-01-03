package DE.combat.weapons;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.combat.DamageType;

public class DE_GravitonOnFireEffect implements BeamEffectPlugin {

	// public static float DAMAGE = 150;

	protected String weaponId = null;

	@Override
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		ShipAPI ship = beam.getSource();
		/*if (!ship.hasListenerOfClass(DE_GravitonOnFireEffect.class)) {
			ship.addListener(this);
			weaponId = beam.getWeapon().getId();
		}*/
		WeaponAPI w = beam.getWeapon();
		if (w != null && w.getId().equals(weaponId) && w.isFiring()) {
			// float base = damage.getBaseDamage();
			// damage.setDamage(base + DAMAGE);
			beam.getDamage().isForceHardFlux();
		}
	}

	// deprecated because it crashed + is janky asf
	/* public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
		// emp arcs may cause crash with the below code - idk how to fix this rn
			BeamAPI b = (BeamAPI) param;
			WeaponAPI w = b.getWeapon();
			if (w != null && w.getId().equals(weaponId)) {
				// float base = damage.getBaseDamage();
				// damage.setDamage(base + DAMAGE);
				damage.isForceHardFlux();
				return "de_gravitonlance";
			}
			return null;
	}*/
}
