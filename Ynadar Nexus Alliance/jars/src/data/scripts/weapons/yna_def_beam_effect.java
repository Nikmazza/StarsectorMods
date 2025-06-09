package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class yna_def_beam_effect implements BeamEffectPlugin {

    //private final float level = 0.9f;
    //private final IntervalUtil fireInterval = new IntervalUtil(0.1f, 0.1f);
    //private static final Vector2f point = new Vector2f();
    private boolean wasZero = true;
	
	
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        
        float dur = beam.getDamage().getDpsDuration();
        if (!wasZero) {
            dur = 0;
        }
        wasZero = beam.getDamage().getDpsDuration() <= 0;
        if (dur > 0f) {
            CombatEntityAPI target = beam.getDamageTarget();
            if ((beam.getBrightness() >= 0.2f)  && (!((ShipAPI) target).isFighter())) {
                    float damageScaler = 1f;
                
                    engine.applyDamage(target, beam.getTo(), beam.getDamage().computeDamageDealt(dur) * damageScaler, DamageType.ENERGY, 0f, false, true, beam.getSource(), false);  
            }
        }
    }
}
