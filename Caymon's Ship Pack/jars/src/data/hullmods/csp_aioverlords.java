package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import org.lazywizard.lazylib.MathUtils;

import java.util.HashMap;
import java.util.Map;

//Original code by Tartiflette, modified to enable swapping individual weapons
public class csp_aioverlords extends BaseHullMod {
        
    public  Map<Integer,String> LEFT_SELECTOR = new HashMap<>();
    {
        LEFT_SELECTOR.put(0, "csp_remnant_plasma");
        LEFT_SELECTOR.put(1, "csp_remnant_dualamb");
        LEFT_SELECTOR.put(2, "csp_remnant_pulseL");
    }
    
    public Map<Integer,String> RIGHT_SELECTOR = new HashMap<>();
    {
        RIGHT_SELECTOR.put(0, "csp_remnant_pulsar");
        RIGHT_SELECTOR.put(1, "csp_remnant_pulseR");
        RIGHT_SELECTOR.put(2, "csp_remnant_locust");
    }
    
    private final Map<String, Integer> SWITCH_TO_LEFT = new HashMap<>();
    {
        SWITCH_TO_LEFT.put("csp_remnant_plasma",1);
        SWITCH_TO_LEFT.put("csp_remnant_dualamb",2);
        SWITCH_TO_LEFT.put("csp_remnant_pulseL",0);
    }
	
    private final Map<String, Integer> SWITCH_TO_RIGHT = new HashMap<>();
    {
		SWITCH_TO_RIGHT.put("csp_remnant_pulsar",1);
        SWITCH_TO_RIGHT.put("csp_remnant_pulseR",2);
        SWITCH_TO_RIGHT.put("csp_remnant_locust",0);
    }
    
    private final Map<Integer,String> LEFTSWITCH = new HashMap<>();
    {
        LEFTSWITCH.put(0,"csp_selector_plasma");
        LEFTSWITCH.put(1,"csp_selector_antimatter");
        LEFTSWITCH.put(2,"csp_selector_pulseL");
    }
    private final Map<Integer,String> RIGHTSWITCH = new HashMap<>();
    {
        RIGHTSWITCH.put(0,"csp_selector_pulsar");
        RIGHTSWITCH.put(1,"csp_selector_pulseR");
        RIGHTSWITCH.put(2,"csp_selector_locust");
    }    
    
    private final String leftslotID = "WS0007";
    private final String rightslotID = "WS0008";
    
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) 
	{

        //trigger a weapon switch if none of the selector hullmods are present
        boolean toSwitchLeft=true;
		boolean toSwitchRight = true;
		int numMods = 0;
        for(int i=0; i<SWITCH_TO_LEFT.size(); i++){
            if(stats.getVariant().getHullMods().contains(LEFTSWITCH.get(i))){
                toSwitchLeft=false;
				numMods++;
            }
        }
        for(int i=0; i<SWITCH_TO_RIGHT.size(); i++){
            if(stats.getVariant().getHullMods().contains(RIGHTSWITCH.get(i))){
                toSwitchRight=false;
				numMods++;
            }
        }
		
		//If there isn't both a hullmod for the left AND right arm, we need to switch something

        //remove the weapons to change and swap the hullmod for the next fire mode
        if(toSwitchLeft){        
            //select new fire mode
            int selected;       
            boolean random=false;
            if(stats.getVariant().getWeaponSpec(leftslotID)!=null){
                selected=SWITCH_TO_LEFT.get(stats.getVariant().getWeaponSpec(leftslotID).getWeaponId());
				//stake
                
            } else {
                selected=MathUtils.getRandomNumberInRange(0, SWITCH_TO_LEFT.size()-1);
                random=true;
            }
            
            //add the proper hullmod
            stats.getVariant().addMod(LEFTSWITCH.get(selected));

            //clear the weapons to replace
			//if(ship.getHullmods.contains(SWITCH))
			//{
				stats.getVariant().clearSlot(leftslotID);
				String toInstallLeft=LEFT_SELECTOR.get(selected);  
				stats.getVariant().addWeapon(leftslotID, toInstallLeft);
		//	}            
            if(random){
                stats.getVariant().autoGenerateWeaponGroups();
            }
        }
		
        if(toSwitchRight){        
            //select new fire mode
            int selected;       
            boolean random=false;
            if(stats.getVariant().getWeaponSpec(rightslotID)!=null){
                selected=SWITCH_TO_RIGHT.get(stats.getVariant().getWeaponSpec(rightslotID).getWeaponId());
                
            } else {
                selected=MathUtils.getRandomNumberInRange(0, SWITCH_TO_RIGHT.size()-1);
                random=true;
            }
            
            //add the proper hullmod
            stats.getVariant().addMod(RIGHTSWITCH.get(selected));

            //clear the weapons to replace			
			//if(ship.getHullmods.contains(SWITCH)
			//{
				stats.getVariant().clearSlot(rightslotID);
				//select and place the proper weapon              
				String toInstallRight=RIGHT_SELECTOR.get(selected);
				stats.getVariant().addWeapon(rightslotID, toInstallRight);
			//}
            
            if(random){
                stats.getVariant().autoGenerateWeaponGroups();
            }
        }
		
    }
    
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id){
        
        if(ship.getOriginalOwner()<0){
            //this emoves the weapons if you strip, but basically useless if you scuttle...
			//TODO: Make script that removes these weapons if they ever appear in cargo
            if(
                    Global.getSector()!=null && 
                    Global.getSector().getPlayerFleet()!=null && 
                    Global.getSector().getPlayerFleet().getCargo()!=null && 
                    Global.getSector().getPlayerFleet().getCargo().getStacksCopy()!=null &&
                    !Global.getSector().getPlayerFleet().getCargo().getStacksCopy().isEmpty()
                    ){
                for (CargoStackAPI s : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()){
                    if(
                            s.isWeaponStack() && (
                                LEFT_SELECTOR.containsValue(s.getWeaponSpecIfWeapon().getWeaponId()) || 
                                RIGHT_SELECTOR.containsValue(s.getWeaponSpecIfWeapon().getWeaponId())
                                ) 
                            ){
                        Global.getSector().getPlayerFleet().getCargo().removeStack(s);
                    }
                }
            }
        }
    }
    
    @Override
    public String getDescriptionParam(int index, HullSize hullSize) { 
        if (index == 0) return "A";
        if (index == 1) return "B";
        if (index == 2) return "C";
        return null;
    }
    
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ( ship.getHullSpec().getHullId().startsWith("csp_"));
    }
}
