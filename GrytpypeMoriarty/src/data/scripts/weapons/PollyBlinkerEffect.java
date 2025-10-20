package data.scripts.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import java.awt.Color;

public class PollyBlinkerEffect implements EveryFrameWeaponEffectPlugin
{
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon)
    {
        ShipAPI ship = weapon.getShip();
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
    }
}