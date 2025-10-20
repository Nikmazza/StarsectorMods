package data.hullmods;  
  
import com.fs.starfarer.api.combat.ShipAPI;  
  
public class IncompatibleHullmodWarning extends GMDA {
      
    @Override  
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {  
        if (index == 0) return "WARNING";  
          
        return null;  
    }  
} 