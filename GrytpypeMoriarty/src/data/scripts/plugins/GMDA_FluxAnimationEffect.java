package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.awt.Color;

public class GMDA_FluxAnimationEffect implements EveryFrameWeaponEffectPlugin {

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine.isPaused()) {
            return;
        }
        ShipAPI ship = weapon.getShip();

        float effect_level = (ship.getFluxTracker().getFluxLevel());

        weapon.getSprite().setAdditiveBlend();
        // Refit screen check
        if (ship.getOriginalOwner() == -1)
        {
            weapon.getSprite().setColor(new Color(0, 0, 0, 0)); // Blinker is off
            return;
        }

        if (weapon.getShip() != null && weapon.getShip().isAlive())
        {
            weapon.getAnimation().setAlphaMult(1f); // Blinker is on
        }
        else
        {
            weapon.getAnimation().setAlphaMult(0f); // Ship is dead, hide the blinker ;)
        }
        weapon.getAnimation().setFrameRate(1f + (effect_level * 2f));
    }
}
