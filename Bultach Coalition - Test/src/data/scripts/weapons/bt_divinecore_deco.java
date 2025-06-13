package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class bt_divinecore_deco implements EveryFrameWeaponEffectPlugin, OnFireEffectPlugin {

    private final List<DamagingProjectileAPI> projectiles = new ArrayList<>();

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || weapon == null || engine.isPaused()) return;

        SpriteAPI flare1 = Global.getSettings().getSprite("fx", "bt_holy_explosion");
        SpriteAPI flare2 = Global.getSettings().getSprite("fx", "bt_flare1");
        SpriteAPI flare3 = Global.getSettings().getSprite("fx", "bt_flare1");

        Vector2f point = new Vector2f(0f, 0f);
        VectorUtils.rotate(point, weapon.getShip().getFacing());
        Vector2f.add(point, weapon.getLocation(), point);

        MagicRender.singleframe(
                flare1,
                MathUtils.getRandomPointInCircle(point, MathUtils.getRandomNumberInRange(0f, 3f)),
                new Vector2f(90f, 90f),
                0f,
                new Color(255, 229, 200, 115),
                false
        );

        MagicRender.singleframe(
                flare2,
                MathUtils.getRandomPointInCircle(point, MathUtils.getRandomNumberInRange(0f, 3f)),
                new Vector2f(250f, 30f),
                0f,
                new Color(255, 223, 175, 125),
                true
        );

        MagicRender.singleframe(
                flare3,
                MathUtils.getRandomPointInCircle(point, MathUtils.getRandomNumberInRange(0f, 3f)),
                new Vector2f(250f, 30f),
                90f,
                new Color(255, 223, 175, 125),
                true
        );

        for (int i = 0; i < 3; i++) {
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "bt_cleave_aura"),
                    MathUtils.getRandomPointInCircle(weapon.getLocation(), MathUtils.getRandomNumberInRange(0f, 3f)),
                    new Vector2f(),
                    new Vector2f(18, 18),
                    new Vector2f(84 + 136 * i, 84 + 136 * i),
                    MathUtils.getRandomNumberInRange(0, 360),
                    MathUtils.getRandomNumberInRange(-15, 15),
                    new Color(255, 248, 238, 15),
                    true,
                    0.08f * i,
                    0.0f,
                    0.4f - i / 8f
            );
        }
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        if (projectile != null) {
            projectiles.add(projectile);
        }
    }
}
