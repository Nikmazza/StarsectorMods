package DE.combat.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

// modified from uaf
public class de_spinny implements EveryFrameWeaponEffectPlugin {
    public de_spinny() {
    }

    // should make weapon spin and always fire
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (!engine.isPaused()) {
            ShipAPI ship = weapon.getShip();
            if (ship.isAlive()) {
                float angle = weapon.getCurrAngle();
                angle += 1f;
                angle %= 360.0F;
                weapon.setCurrAngle(angle);
                weapon.isAlwaysFire();
            }
        }
    }
}