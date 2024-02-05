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
        float highflux_mustusesystem=0.75f;
        float fluxLevel = ship.getFluxTracker().getFluxLevel();
        float HARD=ship.getFluxTracker().getHardFlux();
        float ALL=ship.getFluxTracker().getCurrFlux();
        float Soft_caculate=ALL-HARD;//软幅量
        float OneUseVent=ship.getMutableStats().getFluxDissipation().base*7f*0.5f;

        if (system.getCooldownRemaining() > 0) return;
        if (system.isOutOfAmmo()) return;
        if (system.isActive()) return;

        if(ship.getShield()!=null) {
            if(ship.getShield().isOff() && ALL > OneUseVent){
                ship.useSystem();
            }//关盾而且有幅，用
            if (Soft_caculate > OneUseVent) {
                ship.useSystem();
            }//软幅多，用
            if (ship.getShield().isOff() && fluxLevel > highflux_mustusesystem) {
                ship.useSystem();
            }//盾关且幅能水平过高，用
        }
        if(ship.getShield()==null){
            ship.useSystem();
        }
    }
}
