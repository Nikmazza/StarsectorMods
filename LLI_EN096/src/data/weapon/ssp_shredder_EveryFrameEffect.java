package data.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import data.SSPMisc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class ssp_shredder_EveryFrameEffect implements EveryFrameWeaponEffectPlugin {

    protected IntervalUtil interval = new IntervalUtil(0.1f, 0.1f);
    protected float T=0f;

    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if(Global.getCombatEngine().isPaused())return;
        List<BeamAPI> beams = weapon.getBeams();
        interval.advance(amount*2);

        if (beams.isEmpty()) return;
        BeamAPI beam = beams.get(0);
        if (beam.getBrightness() >= 1f) {T+=amount;}else if(beam.getBrightness() < 1f){T=0f;}
        Vector2f FP = beam.getFrom();
        float FluxLevel = weapon.getShip().getFluxLevel();
        if(interval.intervalElapsed()){
            for(float beam_length=0;beam_length<beam.getLengthPrevFrame();beam_length+=20f+MathUtils.getRandomNumberInRange(-4,4)){
                Vector2f Loc =MathUtils.getPoint(FP,beam_length,beam.getWeapon().getCurrAngle());
                Global.getCombatEngine().addNebulaParticle(Loc,beam.getSource().getVelocity(),45f,0.1f,0.25f,1f,0.125f, beam.getFringeColor());
                Global.getCombatEngine().addNegativeNebulaParticle(Loc,beam.getSource().getVelocity(),30f,0.1f,0.25f,1f,0.125f, SSPMisc.Anti_Color(beam.getFringeColor()));
                //Global.getCombatEngine().spawnExplosion(Loc,beam.getSource().getVelocity(),beam.getFringeColor(),10f,0.125f);
            }
            Global.getCombatEngine().addSwirlyNebulaParticle(beam.getTo(),beam.getSource().getVelocity(),50f,1f,1f,1f,0.3f, beam.getFringeColor(),false);
            Global.getCombatEngine().addNegativeSwirlyNebulaParticle(beam.getTo(),beam.getSource().getVelocity(),45f,1f,1f,1f,0.3f, SSPMisc.Anti_Color(beam.getFringeColor()));
        }
      //产生电弧
        if (T>(beam.getWeapon().getSpec().getBurstDuration())*0.99f) {
            //命中
            if (beam.getDamageTarget() != null) {
                for(int i=1;i<=10;i++) {
                engine.spawnEmpArc(
                        weapon.getShip(),
                        FP,
                       null,
                        beam.getDamageTarget(),
                        beam.getWeapon().getDamageType(),
                        100 * (1+FluxLevel),
                        100 *(1+FluxLevel),
                        1000000,
                        null,
                        5f,
                        beam.getFringeColor(), beam.getCoreColor());}
            } else {//空枪
                for(int i=1;i<=10;i++) {
                    Vector2f RandomPoint = MathUtils.getRandomPointInCircle(FP, 100f);
                    engine.spawnEmpArcVisual(FP, weapon.getShip(), RandomPoint, weapon.getShip(), 15f,  beam.getFringeColor(), beam.getCoreColor());
                }
            }
            T=0;
        }
    }

}

