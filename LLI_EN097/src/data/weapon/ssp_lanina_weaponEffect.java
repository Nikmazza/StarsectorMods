package data.weapon;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ssp_lanina_weaponEffect implements BeamEffectPlugin {

    private IntervalUtil fireInterval = new IntervalUtil(0.2f, 0.3f);
    protected IntervalUtil Interval = new IntervalUtil(0.2f, 0.2f);
    private boolean wasZero = true;

    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        CombatEntityAPI target = beam.getDamageTarget();
        ShipAPI SorceShip = beam.getSource();
        float pierceChance = 0.50f;//电弧概率
//        //光束变色
//        if(SorceShip.getSystem().isOn()){
//        beam.setCoreColor(new Color(250, 80, 40,255));
//        beam.setFringeColor(new Color(250, 80, 40, 255));
//        }
        if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
            ShipAPI ship = (ShipAPI) target;
            //定身
            Interval.advance(amount);
            if(Interval.intervalElapsed()){ target.getVelocity().scale(0.2f); }
            //生成电弧
            float dur = beam.getDamage().getDpsDuration();
            // needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
            if (!wasZero) dur = 0;
            wasZero = beam.getDamage().getDpsDuration() <= 0;
            fireInterval.advance(dur);
            if (fireInterval.intervalElapsed()) {

                pierceChance *= ship.getMutableStats().getDynamic().getValue(Stats.SHIELD_PIERCED_MULT);
                boolean piercedShield = (float) Math.random() < pierceChance;
                //piercedShield = true;
                if (piercedShield) {
                    Vector2f point = beam.getRayEndPrevFrame();
                    float emp = beam.getDamage().getFluxComponent() * 0.5f;
                    float dam = beam.getDamage().getDamage() * 0.25f;
                    engine.spawnEmpArcPierceShields(
                            beam.getSource(), point, beam.getDamageTarget(), beam.getDamageTarget(),
                            DamageType.ENERGY,
                            dam, // damage
                            emp, // emp
                            100000f, // max range
                            "tachyon_lance_emp_impact",
                            15f,
                            beam.getFringeColor(),
                            beam.getCoreColor()
                    );
                }
            }
        }
    }
}
