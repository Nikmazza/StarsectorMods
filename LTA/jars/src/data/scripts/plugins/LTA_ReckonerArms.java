package data.scripts.plugins;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class LTA_ReckonerArms implements EveryFrameWeaponEffectPlugin {

    
    // Yui The Modder here:                                                                // 
    // I annotated and simplified the majority of the following code for ease of use and   // 
    // implementation and wrote these comments as a form of guide.                         //
    // As far as I am aware I have implemented the code in the simplest possible format,   //
    // but I am inexperienced comparative to many of the other members of the community.   //
                                                                                            
    // Please feel free to share this guide to moving modules as weapons, and if you have  // 
    // any feedback or I wrote something not clearly/concisely enough please feel free to  //
    // contact me on the forums or on Discord in the unofficial Starsector Discord server. // 
    
    // Bananao provided some sample code I used for reference when making this.            //
    // They are available on the forums.                                                   //
    
    
    // YOU WILL NEED A FEW THINGS TO GET STARTED:
    // 2 Weapons (to sync up with some modules) (*Can look like anything or be invisible we just want the weapon itself*).          //
    // 2 Ship Modules (to control using the weapons) (*These are our arms that we're going to wiggle around and aim at enemies*)    //
    // A java file laid out like the following, located in a scripts capable folder, (You will likely want to "compile" this later. //
    // If you're new to compiling, ask another modder about "IDE" programs and compiling with them):                                //
    
    // Things to remember //
// - Weapon slot ids on the ship you're doing this on are recommended to be unique (for the     //
//   module and weapon slots we're using in this file) due to potential crashes and issues      //
//   with other vessels.                                                                        //
// - Crashes/Bugs caused by this script should be traceable in the starsector log, located in   //
//   "Starsector\starsector-core". Look out for file directories and recognisable IDs.          //


   
    private float delay = 0f;
    private boolean runOnce = false;
    private ShipAPI LTA_Reckoner_Upcycled_LeftArm, LTA_Reckoner_Upcycled_RightArm; //(Where you put the IDs for ships(modules) you're using with this script).
    private WeaponAPI LTA_reckonertargetingbeamleft, LTA_reckonertargetingbeamright; //(Where you put the IDs for weapons you're using for this script) Can be used to rotate deco weapons and builtins too, just remember to make cases for them later!
    
    //Don't touch this unless you know what you're doing, it sets up something we'll be using later in the script.
    //                  \|/
    private boolean wpn_found = false, prt_found = false;
    //                  /|\
    
    //Don't touch these unless you know what you're doing, they make sure the script keeps time correctly.
    //                             \|/
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {


        if (!weapon.getShip().isAlive()) return;
        ShipAPI ship = weapon.getShip();

        ///black magick wytchkraft (note from Yui - needed for reasons, please don't delete)
        if (!runOnce) {
            runOnce = true;

            if (ship.getOriginalOwner() == -1 || ship.getOwner() == -1) {
                return;
            }
            if (ship.getOriginalOwner() != 0) {
                delay = 2.1f;
            }
        }
	//                             /|\



//The following finds the modular and assigns their IDs, along with their mounting points on ships. Please make sure the IDs here match (for each case) what you've put at the top. Each case governs a seperate weapon and module. (You can do lots of different ships here and it will all run on this same script).
        if (delay >= 0) {
            delay -= amount;
            if (delay <= 0) {
                if (!ship.getChildModulesCopy().isEmpty()) {
                    for (ShipAPI s : ship.getChildModulesCopy()) {
                        switch (s.getStationSlot().getId()) {
                            case "WS_ReckonerLeftArm": //In this case this is the weapon ID for the MODULE on the vessel, please don't confuse these IDs with the module hull IDs. ("WS" always refers to "Weapon Slot"). PLEASE NAME THE WEAPON SLOT SOMETHING UNIQUE IT'S SUPER IMPORTANT TO DO SO TO PREVENT BUGS. (Don't leave it as WS00NUMBER, name it something).
                                LTA_Reckoner_Upcycled_LeftArm = s;
                                break;
                            case "WS_ReckonerRightArm":
                                LTA_Reckoner_Upcycled_RightArm = s;
                                break;
                        }
                    }
                }
            }
            //log.info("ANIM TEST: PARTS FOUND");
            return;
        }
        
        //The following needs to be expanded if you want to add more modules, just arrange them as: && MYMOD_SHIPHULLID != null && MYMOD_SHIPHULLID != null ect...
        if (LTA_Reckoner_Upcycled_LeftArm != null && LTA_Reckoner_Upcycled_RightArm != null) {
            prt_found = true;
        }



        //This part tells the game to check for the guiding weapons we'll be using to aim the modules (And their slots on the vessel you're after). Do the same edits as with the previous segment.
        if(!wpn_found){
            for (WeaponAPI w : ship.getAllWeapons()) {
                switch (w.getSlot().getId()) {
                    case "WS_ReckonerLeftArmTargeting":
                        LTA_reckonertargetingbeamleft = w;
                        break;
                    case "WS_ReckonerRightArmTargeting":
                        LTA_reckonertargetingbeamright = w;
                        break;

                }
            }
			wpn_found = true;
        }
       //This checks if the weapon is alive and exists, so that you know, we don't get some weird zombified module that turns on its own like a spooky haunted barbecue spit.
        //if(!wpn_found || !prt_found)return;

        if(!LTA_Reckoner_Upcycled_LeftArm.isAlive()){
            LTA_reckonertargetingbeamleft.disable(true);
            LTA_reckonertargetingbeamleft.getSprite().setSize(0,0);
        }
        if(!LTA_Reckoner_Upcycled_RightArm.isAlive()){
            LTA_reckonertargetingbeamright.disable(true);
            LTA_reckonertargetingbeamright.getSprite().setSize(0,0);
        }


        //The following code manages the rotation of the parts assigned IDs to previously.
        //                            \|/
        LTA_Reckoner_Upcycled_LeftArm.setFacing(LTA_reckonertargetingbeamleft.getCurrAngle());
        LTA_Reckoner_Upcycled_RightArm.setFacing(LTA_reckonertargetingbeamright.getCurrAngle());
        //                            /|\
    
	}	
}