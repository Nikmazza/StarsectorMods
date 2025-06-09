package data.scripts.weapons;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;

public class yna_pd_las1_beam_effect implements BeamEffectPlugin {

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
            //if ((beam.getBrightness() >= 0.2f)  && (((ShipAPI) target).isFighter())) {
            if ((beam.getBrightness() >= 0.2f)  && (target instanceof MissileAPI)) {
                float damageScaler = 1.0f; //1.0f;
                engine.applyDamage(target, beam.getTo(), beam.getDamage().computeDamageDealt(dur) * damageScaler, DamageType.FRAGMENTATION, 0f, false, true, beam.getSource(), false);
            }
        }
    }
}
