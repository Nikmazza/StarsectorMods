package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.magiclib.util.MagicIncompatibleHullmods;
import static data.scripts.util.TADA_settingsData.Missile_full;

public class TADA_fullRacks extends BaseHullMod {
    
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) { 
        for (String tmp : Missile_full) {
            if(stats.getVariant().getHullMods().contains(tmp)){
                MagicIncompatibleHullmods.removeHullmodWithWarning(stats.getVariant(), tmp, "TADA_fullRacks");
            }
        }
    }
    
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        //incompatibility list
        String list = "\n";
        for(String id : Missile_full){
            if(Global.getSettings().getHullModSpec(id)==null)continue;
            list+=" - ";
            list=list+Global.getSettings().getHullModSpec(id).getDisplayName();
            list+="\n";
        }
        if (index == 0) return list;
        return null;
    }
}
