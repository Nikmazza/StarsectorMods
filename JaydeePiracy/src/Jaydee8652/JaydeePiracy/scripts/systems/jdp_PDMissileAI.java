package Jaydee8652.JaydeePiracy.scripts.systems;

import Jaydee8652.JaydeePiracy.scripts.ai.jdp_defensetorpedoAI;
import Jaydee8652.JaydeePiracy.scripts.skills.flowerfish.jdp_AptitudeFlowerfish;
import Jaydee8652.JaydeePiracy.scripts.skills.flowerfish.jdp_flowerfishAccountingOfBurdens;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.List;
import java.util.Set;

import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class jdp_PDMissileAI implements ShipSystemAIScript {
  private CombatEngineAPI engine;
  private ShipAPI ship;
  private ShipSystemAPI system;
  private final IntervalUtil timer = new IntervalUtil(2f, 2f);
  private static boolean used = false;

  //Thanks Tart! I would have never figured this out.

  @Override
  public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
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

    timer.advance(amount);
    if (timer.intervalElapsed()) used = false;

    List<MissileAPI> near = CombatUtils.getMissilesWithinRange(ship.getLocation(), 1000f);
    if (!near.isEmpty() && ship.getPhaseCloak().canBeActivated()) {
      for (MissileAPI m : near) {
        Set<String> tags = m.getTags();
        if ((m.getOwner() != ship.getOwner()) && !used && (m.getWeaponSpec().getTags().stream().anyMatch(t -> t.startsWith("strike") && !t.equals("strike0")))) {
          ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);
          used = true;
          return;
        }
      }
    }
  }
}