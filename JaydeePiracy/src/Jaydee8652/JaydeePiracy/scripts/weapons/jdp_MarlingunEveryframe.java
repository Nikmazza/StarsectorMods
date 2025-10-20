package Jaydee8652.JaydeePiracy.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.magiclib.util.MagicAnim;
import org.magiclib.util.MagicLensFlare;
import org.magiclib.util.MagicRender;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class jdp_MarlingunEveryframe implements EveryFrameWeaponEffectPlugin, OnFireEffectPlugin {

  private WeaponAPI CHARGE;
  private WeaponAPI HEAT;
  private ShipAPI SHIP;

  private float FLheight, FLwidth, RLheight, RLwidth;
  private float doors = 0, directionCheck = 0;
  private int direction = 1;
  private final IntervalUtil timer = new IntervalUtil(0.0333f, 0.0333f);
  private final float tic = 0.0333f;
  private boolean runOnce = false,
      hasFired = false,
      closed = true,
      open = false,
      refundCheck = false;

  @Override
  public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

    if (weapon.getShip().getSystem().isOn()) {
      weapon.getShip().getSystem().deactivate();

      engine.spawnProjectile(
          weapon.getShip(),
          weapon,
          "jdp_marlingun_bluescreen",
          projectile.getLocation(),
          projectile.getFacing(),
          weapon.getShip().getVelocity());
      engine.removeEntity(projectile);
    }
  }

  @Override
  public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

    if (engine.isPaused()) return;

    if (!runOnce || SHIP == null) {
      runOnce = true;
      SHIP = weapon.getShip();
      refundCheck = false;
      return;
    }

    // system toggle off refund
    if (SHIP.getSystem().isActive()) {
      refundCheck = true;
    } else if (refundCheck) {
      refundCheck = false;
      if (weapon.getChargeLevel() < 0.99f) {
        // system was turned off without firing, refund system ammo and halve cooldown
        SHIP.getSystem()
            .setAmmo(Math.min(SHIP.getSystem().getAmmo() + 1, SHIP.getSystem().getMaxAmmo()));
        SHIP.getSystem().setCooldownRemaining(SHIP.getSystem().getCooldownRemaining() / 10f);
      }
    }

    float charge = weapon.getChargeLevel();
    if (charge == 1) {
      hasFired = true;
    } else if (charge == 0) {
      hasFired = false;
    }
  }
}
