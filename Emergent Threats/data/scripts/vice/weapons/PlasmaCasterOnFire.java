package data.scripts.vice.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.WeaponAPI;

public class PlasmaCasterOnFire implements OnFireEffectPlugin {
	
	public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
		ShipAPI ship = weapon.getShip();
		if (ship == null) return;		
		if (weapon.getAmmo() >= 5) ship.getSystem().setAmmo(0);
		else if (weapon.getAmmo() == 1) ship.getSystem().setAmmo(1);
	}
}