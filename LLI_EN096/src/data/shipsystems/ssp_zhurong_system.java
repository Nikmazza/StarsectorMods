package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ssp_zhurong_system extends BaseShipSystemScript {
    protected boolean RunOnce=false;
    protected Vector2f loc1 = new Vector2f();
    protected Vector2f loc2 = new Vector2f();
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship=(ShipAPI)stats.getEntity();
        if(!RunOnce){
            loc1 = new Vector2f(ship.getLocation());
            RunOnce=true;
            if(ship.getVariant().hasHullMod("ssp_LongerRange")){ship.getFluxTracker().increaseFlux(ship.getSystem().getFluxPerUse()*2,true);}
        }
    }
    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship=(ShipAPI)stats.getEntity();
        if(RunOnce){
            for(int c=0 ; c<=12 ;c++){
                loc2 = new Vector2f(ship.getLocation());
                Vector2f RandomPoint1=MathUtils.getRandomPointInCircle(loc1,ship.getCollisionRadius()*2f);
                Vector2f RandomPoint2=MathUtils.getRandomPointInCircle(loc2,ship.getCollisionRadius());
                Global.getCombatEngine().spawnProjectile(
                        ship,
                        null,
                        "ssp_zhurong_system_minelayer",
                        RandomPoint1,
                       // VectorUtils.getAngle(RandomPoint1,RandomPoint2),
                        (float) Math.random() * 360f,
                        null);
                Global.getCombatEngine().spawnEmpArcVisual(
                        RandomPoint2,
                        null,
                        RandomPoint1,
                        null,
                        1f,
                        new Color(240, 25, 100,150),
                        new Color(255, 45, 95, 255));
            }
        }
        RunOnce=false;
    }
    @Override
    public int getUsesOverride(ShipAPI ship) {
        if(ship.getVariant().hasHullMod("ssp_ShortRange")){return 9;}
        else return -1;
    }
    @Override
    public float getRegenOverride(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("ssp_LongerRange")){return 0.15f;}
        else return -1;
    }
}
