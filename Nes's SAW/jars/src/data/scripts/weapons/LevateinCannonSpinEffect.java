//By Tartiflette, modified by Nes
package data.scripts.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;

import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;

public class LevateinCannonSpinEffect implements EveryFrameWeaponEffectPlugin{

    private float delay = 0.1f, timer = 0f, spinDown= 20f;//larger = longer spindown
    private int frame = 0;
    private boolean runOnce = false;
    private AnimationAPI theAnim;

    //spinny barrel stuff
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (engine.isPaused() || weapon.getSlot().isHidden()) {
            return;
        }

        if(!runOnce){
            runOnce=true;
            theAnim = weapon.getAnimation();
        }

        float mult = 1f;
        ShipAPI ship = weapon.getShip();
        if (ship != null) {
            mult *= ship.getMutableStats().getBallisticRoFMult().getModifiedValue();
        }

        float minDelay = 1f / (theAnim.getFrameRate() * mult);
        int maxFrame = theAnim.getNumFrames();

            timer+=amount;
            if (timer >= delay){

                int frames = (int)(timer/delay);

                timer-=frames*delay;
                if (weapon.getChargeLevel()==1){
                    delay = minDelay;
                }

                else {
                    delay = Math.min(delay + delay/spinDown, 0.1f);
                }

                if (delay!=0.1f){
                    frame+=frames;
                    if (frame>=maxFrame){
                        frame=Math.min(maxFrame-1,frame-maxFrame);
                    }
                }
            }

        //fix barrel twitching
        if (weapon.getChargeLevel() == 0) {
            theAnim.pause();
        }
        else {
            theAnim.setFrame(frame);
        }
    }
}
