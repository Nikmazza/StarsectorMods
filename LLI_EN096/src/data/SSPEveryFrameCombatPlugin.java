package data;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import java.awt.*;
import java.util.List;

public class SSPEveryFrameCombatPlugin extends BaseEveryFrameCombatPlugin {
    IntervalUtil Interval02 = new IntervalUtil(0.2f, 0.4f);
    IntervalUtil Interval01 = new IntervalUtil(0.1f, 0.2f);
    IntervalUtil Interval2x005 = new IntervalUtil(0.05f, 0.1f);
    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        Interval02.advance(amount);
        Interval01.advance(amount);
        Interval2x005.advance(amount*2);
        if (Global.getCombatEngine() == null){return;}
        if (Global.getCombatEngine().isPaused()){return;}
        CombatEngineAPI engine=Global.getCombatEngine();
        for (DamagingProjectileAPI proj : engine.getProjectiles()) {
            if (proj == null || proj.getProjectileSpecId() == null) break;
            if (Interval02.intervalElapsed()) {
                if (proj.getProjectileSpecId().equals("ssp_siege_shot")) {
                    engine.spawnEmpArcVisual(
                            proj.getLocation(),
                            proj,
                            MathUtils.getRandomPointInCircle(proj.getLocation(), MathUtils.getRandomNumberInRange(20f, 40f)),
                            null,
                            2f, new Color(200, 10, 240, 155),
                            new Color(255, 100, 180, 155));
                }
            }
            if (proj.getProjectileSpecId().equals("ssp_blaster_shot") || proj.getProjectileSpecId().equals("ssp_BigFackingGPD_shot")) {
                if (Interval2x005.intervalElapsed()) {
                    engine.addHitParticle(MathUtils.getRandomPointInCircle(proj.getLocation(), 10f), new Vector2f(0, 0), 7, 1, 0.2f,1f,  new Color(175,195,255));
                }
                if (Interval01.intervalElapsed()) {
                    engine.addHitParticle(MathUtils.getRandomPointInCircle(proj.getLocation(), 20f), new Vector2f(0, 0), 7, 1, 0.2f, 1f, new Color(175, 195, 255));
                }
                if (Interval02.intervalElapsed()) {
                    engine.addHitParticle(MathUtils.getRandomPointInCircle(proj.getLocation(), 30f), new Vector2f(0, 0), 7, 1, 0.2f, 1f, new Color(175, 195, 255));
                }
            }

        }
    }


}
