package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import org.magiclib.util.MagicIncompatibleHullmods;
import static data.scripts.util.TADA_settingsData.ReactiveArmor_noncompatible;
import static data.scripts.util.TADA_txt.txt;

public class TADA_reactiveArmor extends BaseHullMod {

    private final float explosive = -33f;    
    private final float kinetic = 25f;
    private final float no_smod_energy = 10f;

    private final float smod_energy = -10f;

//    private final Set<String> BLOCKED_HULLMODS = new HashSet<>();
//    {
//        // These hullmods will automatically be removed
////        BLOCKED_HULLMODS.add("heavyarmor");
//        BLOCKED_HULLMODS.add("ii_armor_package");
//        BLOCKED_HULLMODS.add("SCY_lightArmor");
//        BLOCKED_HULLMODS.add("SKR_ancientArmor");
//    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        
        stats.getHighExplosiveDamageTakenMult().modifyMult(id, 1 + (explosive / 100));
        
        stats.getKineticDamageTakenMult().modifyPercent(id, kinetic);

        boolean sMod = isSMod(stats);
        float energy = no_smod_energy;
        if (sMod) energy = smod_energy;
        stats.getEnergyDamageTakenMult().modifyPercent(id, energy);
        //stats.getFragmentationDamageTakenMult().modifyMult(id, 1+(frag/100));
        //stats.getArmorDamageTakenMult().modifyMult(id, frag);
    }
    
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id){
        for (String tmp : ReactiveArmor_noncompatible) {
            if (ship.getVariant().getHullMods().contains(tmp)) {   
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TADA_reactiveArmor");
            }
        }
    }
    
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        
        if (index == 0) return "" + Math.round(explosive) + "%";
        if (index == 1) return "" + "+" + Math.round(kinetic) + "%";
        if (index == 2) return "" + "+" + Math.round(no_smod_energy ) + "%"; 
        
        //incompatibility list
        String list = "\n";
        for(String id : ReactiveArmor_noncompatible){
            if(Global.getSettings().getHullModSpec(id)==null)continue;
            list+=" - ";
            list=list+Global.getSettings().getHullModSpec(id).getDisplayName();
            list+="\n";
        }
        if (index == 3) return list;
        return null;
    }

    @Override
    public String getSModDescriptionParam(int index, ShipAPI.HullSize hullSize) {

        if (index == 0) return "" + Math.round(smod_energy) + "%";
        return null;
    }
    
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        for (String tmp : ReactiveArmor_noncompatible) {
            if (ship.getVariant().getHullMods().contains(tmp)) {                
                return false;
            }
        }
        return true;
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        for (String tmp : ReactiveArmor_noncompatible) {
            if (ship.getVariant().getHullMods().contains(tmp)) {                
                return txt("hm_incompatible")+Global.getSettings().getHullModSpec(tmp).getDisplayName();
            }
        }
        return null;
    }
}
