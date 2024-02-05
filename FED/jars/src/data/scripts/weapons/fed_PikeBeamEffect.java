package data.scripts.weapons;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import java.util.HashMap;
import java.util.Map;


public class fed_PikeBeamEffect implements BeamEffectPlugin {

    private final IntervalUtil fireInterval = new IntervalUtil(0.32f, 0.34f);
    private boolean wasZero = true;
    
    private static final Map<ShipAPI.HullSize, Float> FED_HULL_SIZE_MULTIPLIER = new HashMap<>(5);
        static {
          FED_HULL_SIZE_MULTIPLIER.put(ShipAPI.HullSize.FIGHTER, 5f);
          FED_HULL_SIZE_MULTIPLIER.put(ShipAPI.HullSize.FRIGATE, 1f);  
          FED_HULL_SIZE_MULTIPLIER.put(ShipAPI.HullSize.DESTROYER, 1.5f); 
          FED_HULL_SIZE_MULTIPLIER.put(ShipAPI.HullSize.CRUISER, 2f);
          FED_HULL_SIZE_MULTIPLIER.put(ShipAPI.HullSize.CAPITAL_SHIP, 2.5f);
        }

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        CombatEntityAPI target = beam.getDamageTarget();
        if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
            float dur = beam.getDamage().getDpsDuration();
            // needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
            if (!wasZero) {
                dur = 0;
            }

            wasZero = beam.getDamage().getDpsDuration() <= 0;
            fireInterval.advance(dur);
            if (fireInterval.intervalElapsed()) {
                ShipAPI ship = (ShipAPI) target;
                boolean hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getTo());

                if (!hitShield) {
                    Vector2f point = beam.getRayEndPrevFrame();
                    float dam = beam.getDamage().getDamage() * 0.2f * FED_HULL_SIZE_MULTIPLIER.get(ship.getHullSize());

                    engine.spawnEmpArcPierceShields(
                            beam.getSource(), point, beam.getDamageTarget(), beam.getDamageTarget(),
                            DamageType.FRAGMENTATION,
                            dam, // damage
                            0, // emp 
                            10000f, // max range 
                            "tachyon_lance_emp_impact",
                            beam.getWidth() +10f,
                            beam.getFringeColor(),
                            beam.getCoreColor()
                    );
                }
            }
        }
    }
}
