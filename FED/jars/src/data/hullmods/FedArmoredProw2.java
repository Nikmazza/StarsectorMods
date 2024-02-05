package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import org.magiclib.util.MagicUI;
import java.util.List;

public class FedArmoredProw2 extends BaseHullMod {
    
    
    public void applyEffectsAFterShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        ship.ensureClonedStationSlotSpec();
    }

    String fed_armoredprow_moduleStatus;

    /* DONT NEED BECAUSE AUXSHIELD TAKES PRIORITY
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        float ap_childHull = 0;
        float ap_childBaseHullpoints = 0;
        boolean ap_kestralArmorAlive = false;
        //boolean ap_superkestrelArmorAlive = false;

        if (ship.isShipWithModules()) {
            ship.ensureClonedStationSlotSpec();
            List<ShipAPI> prow_modules = ship.getChildModulesCopy();
            for (ShipAPI childModule : prow_modules) {
                if (childModule.getVariant().getHullSpec().getHullId().equals("fed_kestral_armor")) {
                    ap_childBaseHullpoints = childModule.getVariant().getHullSpec().getHitpoints();
                    ap_childHull = childModule.getHullLevel();
                    ap_kestralArmorAlive = !childModule.isHulk(); 
                }
               
            }
        }

        fed_armoredprow_moduleStatus = "PROW";
        //Color ui = new Color(255, 128, 0);
        if (ap_kestralArmorAlive){
           MagicUI.drawInterfaceStatusBar(ship, (ap_childHull), null, null, ap_childHull, fed_armoredprow_moduleStatus, (int) (ap_childBaseHullpoints * ap_childHull)); 
        }
      
        
    } */
	
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
	
	if (index == 0) return "1000";
        if (index == 1) return  "6000";
        if (index == 2) return  "will not protect the hull from large explosions";
        return null;
	}
}
