package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class fed_SensorBarEffect implements EveryFrameWeaponEffectPlugin {
	
	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		if (engine.isPaused()) return;
                if (!weapon.getShip().isAlive()) return;
		
		float curr = weapon.getCurrAngle();
		
		curr += 1f;

		weapon.setCurrAngle(curr);
	}
}
