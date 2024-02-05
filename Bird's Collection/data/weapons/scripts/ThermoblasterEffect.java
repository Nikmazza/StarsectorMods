package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.DamageType;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.loading.WeaponSlotAPI;

public class ThermoblasterEffect implements OnFireEffectPlugin {

	public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
		ShipAPI ship = weapon.getShip();
		WeaponSlotAPI weaponSlotAPI = weapon.getSlot();
		Vector2f point = weaponSlotAPI.computePosition(ship);
		engine.applyDamage(ship, point, 10000f, DamageType.HIGH_EXPLOSIVE, 0, true, false, ship);
	}
}