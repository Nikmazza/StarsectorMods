package data.shipsystems.ai;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lwjgl.util.vector.Vector2f;

public class ssp_AdvanceVentingAI implements ShipSystemAIScript {

    private ShipAPI ship;
    private CombatEngineAPI engine;
    private ShipwideAIFlags flags;
    private ShipSystemAPI system;

    private IntervalUtil tracker = new IntervalUtil(0.5f, 1f);

    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.flags = flags;
        this.engine = engine;
        this.system = system;
    }

    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        tracker.advance(amount);
        boolean SHOULD_ON=false;//是否激活
        boolean HAS_ON=ship.getSystem().isOn();
        float SOFT=(ship.getFluxTracker().getCurrFlux()-ship.getFluxTracker().getHardFlux())/ship.getMaxFlux();
        float HARD=ship.getFluxTracker().getHardFlux()/ship.getMaxFlux();
        float TOTAL=ship.getFluxTracker().getCurrFlux()/ship.getMaxFlux();
        //开关盾情况下
        if(ship.getShield()!=null && ship.getShield().isOn()) {
            //开盾时
            //如果硬在50%下，无论总量，则软幅少开f
            if(HARD<0.5f){SHOULD_ON=(SOFT<0.1f);}
            //如果硬在50%上，且总幅能高，则有软关f保命，无软开f散硬
            if(HARD>=0.5f && TOTAL>=0.8f){SHOULD_ON= (SOFT>=0.1f);}
            //如果硬在50%上，且总幅能不高，则优先降低硬幅能
            if(HARD>=0.5f && TOTAL<0.8f){SHOULD_ON= true;}
        } else if(ship.getShield()==null||(ship.getShield() != null && ship.getShield().isOff())){
            //关盾时
            //如果硬在50%下，无论总量，则软幅少开f
            if(HARD<0.5f){SHOULD_ON=(SOFT<0.1f);}
            //如果硬在50%上，且总幅能高，优先关f降低总量
            if(HARD>=0.5f && TOTAL>=0.8f){SHOULD_ON= false;}
            //如果硬在50%上，且总幅能不高，还是优先降低总量
            if(HARD>=0.5f && TOTAL<0.8f){SHOULD_ON= false;}
        }


        //最终决定是否开启
        if(SHOULD_ON!=HAS_ON){ship.useSystem();}
    }
}
