package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;

public class bt_bloodflow_decoscript implements EveryFrameWeaponEffectPlugin {

    private AnimationAPI animation;
    private SpriteAPI weaponSprite;
    private boolean initialized = false;
    private float frameTimer = 0f;
    private static final float FRAME_RATE = 12.0f;
    private static final float FRAME_DURATION = 1.0f / FRAME_RATE;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null) {
            if (weapon != null && weapon.getSprite() != null) {
                SpriteAPI sprite = weapon.getSprite();
                sprite.setNormalBlend();
                AnimationAPI anim = weapon.getAnimation();
                if (anim != null) {
                    anim.setAlphaMult(0f);
                }
            }
            return;
        }

        if (engine.isPaused()) {
            return;
        }

        if (!initialized) {
            if (weapon == null || weapon.getAnimation() == null || weapon.getSprite() == null) {
                initialized = true;
                return;
            }
            this.animation = weapon.getAnimation();
            this.weaponSprite = weapon.getSprite();
            initialized = true;
        }

        if (this.animation == null || this.weaponSprite == null) {
            return;
        }

        ShipAPI ship = weapon.getShip();

        boolean effectShouldBeActive = ship != null &&
                ship.isAlive() &&
                !engine.isCombatOver() &&
                engine.isEntityInPlay(ship) &&
                ship.getSystem() != null &&
                ship.getSystem().isActive();

        if (effectShouldBeActive) {
            this.weaponSprite.setAdditiveBlend();
            this.animation.setAlphaMult(4f);

            frameTimer += amount;
            while (frameTimer >= FRAME_DURATION) {
                int nextFrame = this.animation.getFrame() + 1;
                int totalFrames = this.animation.getNumFrames();

                if (totalFrames > 0) {
                    if (nextFrame >= totalFrames) {
                        nextFrame = 0;
                    }
                    this.animation.setFrame(nextFrame);
                    if (nextFrame == 2) {
                        Global.getSoundPlayer().playSound(
                                "bt_bloodflow_single",
                                1.0f,
                                1.0f,
                                weapon.getLocation(),
                                ship.getVelocity()
                        );
                    }
                }
                frameTimer -= FRAME_DURATION;
            }
        } else {
            this.weaponSprite.setNormalBlend();
            this.animation.setAlphaMult(0f);
            if (this.animation.getNumFrames() > 0) {
                this.animation.setFrame(0);
            }
            frameTimer = 0f;
        }
    }
}