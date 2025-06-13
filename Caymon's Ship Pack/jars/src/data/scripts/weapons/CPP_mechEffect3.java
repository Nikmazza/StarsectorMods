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

public class CPP_mechEffect3 implements EveryFrameWeaponEffectPlugin{

    private boolean runOnce=false, lockNloaded=false;
    private ShipSystemAPI system;
    private ShipAPI ship;
    private WeaponAPI cpp_tarobo_arm_left,cpp_tarobo_shoulderL_deco,cpp_tarobo_shoulderR_deco,cpp_tarobo_arm_right;

    private float overlap=0, heat=0;
    private final float LEFT_ARM_OFFSET=-75, RIGHT_ARM_OFFSET = -25,MAX_OVERLAP=10;


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if(!runOnce){
            runOnce=true;
            ship=weapon.getShip();
            system = ship.getSystem();
            for(WeaponAPI w : ship.getAllWeapons()){
				// get the decos, change the ID(s) to whatever their ID is
                switch (w.getSlot().getId()){
                    case "WS0003":
                        cpp_tarobo_arm_left = w;
                        break;
                    case "WS0004":
                        cpp_tarobo_arm_right = w;
                        break;
                    case "WS0001":
                        if(cpp_tarobo_shoulderL_deco == null) {
                            cpp_tarobo_shoulderL_deco = w;
                        }
                        break;
                    case "WS0002":
                        if(cpp_tarobo_shoulderR_deco == null) {
                            cpp_tarobo_shoulderR_deco = w;
                        }
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
        ship.syncWeaponDecalsWithArmorDamage();

		//this stuff determines how the arm/pauldron move while accelerating/decelerating or moving backwards
        if(ship.getEngineController().isAccelerating()){
            if(overlap>(MAX_OVERLAP-0.1f)){
                overlap=MAX_OVERLAP;
            } else {
                overlap=Math.min(MAX_OVERLAP, overlap +((MAX_OVERLAP-overlap)*amount*5));
            }
        } else if(ship.getEngineController().isDecelerating()|| ship.getEngineController().isAcceleratingBackwards()){         
            if(overlap<-(MAX_OVERLAP-0.1f)){
                overlap=-MAX_OVERLAP;
            } else {   
                overlap=Math.max(-MAX_OVERLAP, overlap +((-MAX_OVERLAP+overlap)*amount*5));
            }
        } else {
            if(Math.abs(overlap)<0.1f){
                overlap=0;   
            }else{
                overlap-=(overlap/2)*amount*3;   
            }
        }
        
        float sineA=0, sinceB=0;           
        float global=ship.getFacing();
        float distance = (global -  weapon.getCurrAngle()) + 180f;
        distance = (distance / 360.0f);
        distance = ((distance - (float) Math.floor(distance)) * 360f) - 180f;
        float aim=distance;
		
		if(cpp_tarobo_arm_left != null)
		{
            cpp_tarobo_arm_left.setCurrAngle(
							global
							+   
							((aim+LEFT_ARM_OFFSET)*sinceB)
							+
							((overlap+aim*0.25f)*(1-sinceB))
			);
			
			weapon.setCurrAngle(global+MathUtils.getShortestRotation(global,cpp_tarobo_arm_left.getCurrAngle())*0.6f);
		}
        if(cpp_tarobo_arm_right != null)
        {
            cpp_tarobo_arm_right.setCurrAngle(
                    global
                            +
                            ((aim+RIGHT_ARM_OFFSET)*sinceB)
                            +
                            ((overlap+aim*0.25f)*(1-sinceB))
            );

            weapon.setCurrAngle(global+MathUtils.getShortestRotation(global,cpp_tarobo_arm_right.getCurrAngle())*0.6f);
        }
    }
}
