package data.shipsystems.scripts.ai;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

/**
 * An example system AI
 * @author Nicke535
 * heavly modified by Arthr
 */
public class bc_flarePrimaryAI implements ShipSystemAIScript {
    //Static constants: used to tweak behaviour while keeping the code readable
    private static final float MISSILE_DETECTION_RANGE = 750f; //In SU
    private static final float HIGH_FLUX_THRESHHOLD = 0.80f; //As a multiplier (0.25 = 25%)

    //Some variables to keep track of for later, makes subsequent code calls easier
    private ShipSystemAPI system;
    private ShipAPI ship;

    //Creates a tracker that runs once every 0.17-0.24 seconds; we'll use this for the AI to not trigger every single frame
    private final IntervalUtil tracker = new IntervalUtil(0.17f, 0.24f);


    /**
     * Set up our initial state/variables
     */
    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
    }

    /**
     * Main advance method: most code should be in here
     */
    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        //Advance our tracker
        tracker.advance(amount);

        //Once the interval has elapsed...
        if (tracker.intervalElapsed()) {
            boolean shouldUseSystem = false;

            //Get all missiles nearby, and iterate over them. If we find any enemy ones within range, we should use our system
            for (MissileAPI missile : CombatUtils.getMissilesWithinRange(ship.getLocation(), MISSILE_DETECTION_RANGE)){
                if (missile.getOwner() != ship.getOwner()) {
                    shouldUseSystem = true;
                    //And break the loop, no reason to continue checking more missiles
                    break;
                }
            }


            //If we have too much flux, we *shouldn't* be using our system
            if (ship.getFluxLevel() > HIGH_FLUX_THRESHHOLD) {
                shouldUseSystem = false;
            }

            //Finally, activate if we should, deactivate if not
            if (shouldUseSystem) {
                activateSystem();
            } else {
                deactivateSystem();
            }
        }
    }

    /**
     * Function for activating a right-click system
     */
    private void activateSystem() {
        //Only run when already on: activating again de-activates it
        if (ship.getPhaseCloak().isActive() == false) {
                ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);
		}
         else {
                return;
            }      
    }

    /**
     * Function for deactivating a right-click system
     */
    private void deactivateSystem() {
        //Only run when already on: activating again de-activates it
        if (ship.getPhaseCloak().isActive() == true) {
                ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);
		}
         else {
                return;
            }
    }
}
