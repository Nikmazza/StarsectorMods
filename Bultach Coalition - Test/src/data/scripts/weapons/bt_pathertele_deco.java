package data.scripts.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import java.awt.Color;

/**
 * @author celestis
 */
public class bt_pathertele_deco implements EveryFrameWeaponEffectPlugin {
    
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        SpriteAPI sprite = weapon.getSprite();
        ShipAPI ship = weapon.getShip();
        if (!engine.isEntityInPlay(ship)) {
            sprite.setColor(new Color(159, 0, 255, 244));
        }
        Color color = sprite.getColor();
        
        float alpha = color.getAlpha() / 255f;
        
        if (ship.getSystem().isActive()) {
            alpha += amount * 1f;
        } else {
            alpha -= amount * 0.2f;
        }
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 1) {
            alpha = 1;
        }
        sprite.setColor(new Color(1, 1, 1, alpha));
    }
    
}
