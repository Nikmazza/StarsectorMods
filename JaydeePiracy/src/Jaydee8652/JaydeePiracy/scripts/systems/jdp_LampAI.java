package Jaydee8652.JaydeePiracy.scripts.systems;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public class jdp_LampAI implements ShipSystemAIScript {
  private CombatEngineAPI engine;
  private ShipAPI ship;
  private ShipSystemAPI system;
  private boolean runOnce = false;
  private final IntervalUtil timer = new IntervalUtil(1f, 2f);

  //Thanks Tart! I would have never figured this out.

  @Override
  public void init(
          ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
    this.ship = ship;
    this.system = system;
    this.engine = engine;
    timer.randomize();
  }

  @Override
  public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {

    if (engine.isPaused() || ship.getShipAI() == null) {
      return;
    }

    //No thoughts, Lamp empty
    timer.advance(amount);
    if (timer.intervalElapsed() && AIUtils.canUseSystemThisFrame(ship)) {
      ship.useSystem();
    }
  }
}
