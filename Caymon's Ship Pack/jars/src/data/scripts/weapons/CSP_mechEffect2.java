package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.magiclib.util.MagicAnim;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;

/**
 *
 * @author Tartiflette
 */
public class CSP_mechEffect2 implements EveryFrameWeaponEffectPlugin{

    private boolean runOnce=false, lockNloaded=false;
    private ShipSystemAPI system;
    private ShipAPI ship;
    private WeaponAPI csp_LG_arm_left,csp_LG_shoulderLdeco;
    
    private float overlap=0, heat=0;
    private final float LEFT_ARM_OFFSET=-75,MAX_OVERLAP=10;
    
    
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (!runOnce) {
            runOnce = true;
            ship = weapon.getShip();
            system = ship.getSystem();
            for (WeaponAPI w : ship.getAllWeapons()) {
                // get the decos, change the ID(s) to whatever their ID is
                switch (w.getSlot().getId()) {
                    case "WS0004":
                        csp_LG_arm_left = w;
                        break;
                    // case "D_PAULDRONL" :
                    //    pauldronL=w;
                    //   break;
                }
            }
        }

        if (engine.isPaused()) {
            return;
        }

        //this stuff determines how the arm/pauldron move while accelerating/decelerating or moving backwards
        if (ship.getEngineController().isAccelerating()) {
            if (overlap > (MAX_OVERLAP - 0.1f)) {
                overlap = MAX_OVERLAP;
            } else {
                overlap = Math.min(MAX_OVERLAP, overlap + ((MAX_OVERLAP - overlap) * amount * 5));
            }
        } else if (ship.getEngineController().isDecelerating() || ship.getEngineController().isAcceleratingBackwards()) {
            if (overlap < -(MAX_OVERLAP - 0.1f)) {
                overlap = -MAX_OVERLAP;
            } else {
                overlap = Math.max(-MAX_OVERLAP, overlap + ((-MAX_OVERLAP + overlap) * amount * 5));
            }
        } else {
            if (Math.abs(overlap) < 0.1f) {
                overlap = 0;
            } else {
                overlap -= (overlap / 2) * amount * 3;
            }
        }

        float sineA = 0, sinceB = 0;
        float global = ship.getFacing();
        float distance = (global -  weapon.getCurrAngle()) + 180f;
        distance = (distance / 360.0f);
        distance = ((distance - (float) Math.floor(distance)) * 360f) - 180f;
        float aim=distance;


            if (csp_LG_arm_left != null)
            {

                //move the decos
                csp_LG_arm_left.setCurrAngle(
                        global
                                +
                                ((aim + LEFT_ARM_OFFSET) * sinceB)
                                +
                                ((overlap + aim * 0.25f) * (1 - sinceB))
                );

            weapon.setCurrAngle(global + MathUtils.getShortestRotation(global, csp_LG_arm_left.getCurrAngle()) * 0.6f);
        }
    }
}
