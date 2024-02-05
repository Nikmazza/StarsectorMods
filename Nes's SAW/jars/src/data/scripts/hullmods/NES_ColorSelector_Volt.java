package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.lazywizard.lazylib.MathUtils;
import java.util.HashMap;
import java.util.Map;


public class NES_ColorSelector_Volt extends BaseHullMod {

    private final Map<Integer,String> DECO_COUNTER = new HashMap<>();
    {
        DECO_COUNTER.put(0, "nes_fluorspar_deco_counter0");
        DECO_COUNTER.put(1, "nes_fluorspar_deco_counter1");
        DECO_COUNTER.put(2, "nes_fluorspar_deco_counter2");
        DECO_COUNTER.put(3, "nes_fluorspar_deco_counter3");
        DECO_COUNTER.put(4, "nes_fluorspar_deco_counter4");
        DECO_COUNTER.put(5, "nes_fluorspar_deco_counter5");
    }

    private final Map<String, Integer> SWITCH_TO = new HashMap<>();
    {
        SWITCH_TO.put("nes_fluorspar_deco_counter0",1);
        SWITCH_TO.put("nes_fluorspar_deco_counter1",2);
        SWITCH_TO.put("nes_fluorspar_deco_counter2",3);
        SWITCH_TO.put("nes_fluorspar_deco_counter3",4);
        SWITCH_TO.put("nes_fluorspar_deco_counter4",5);
        SWITCH_TO.put("nes_fluorspar_deco_counter5",0);
    }

    private final Map<Integer,String> SWITCH = new HashMap<>();
    {
        SWITCH.put(0,"NES_Default");
        SWITCH.put(1,"NES_Hazard");
        SWITCH.put(2,"NES_Verdant");
        SWITCH.put(3,"NES_Crimson");
        SWITCH.put(4,"NES_Sparkle");
        SWITCH.put(5,"NES_Commando");
    }

    private final String decocounterID = "NES_DECO_COUNTER";

    //faction specific paintjob selector for Volt
    //this is dumb and silly, too bad!
    private static int PAINTJOB_VOLT;

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {

        //skip this if player
        if (member.getFleetCommander().isPlayer() || member.getFleetCommander().isDefault()) {
            return;
        }
        //incase volt bp is sold to the pl or diktat
        else if (member.getFleetData() != null && member.getFleetData().getFleet() != null && !member.getFleetCommander().isPlayer()
                && member.getFleetCommander().getFaction().getId().contains("persean")
                || member.getFleetCommander().getFaction().getId().contains("sindrian_diktat")){
            PAINTJOB_VOLT = 1;//Hazard yellow
            return;
        }
        //incase volt bp is sold to the church or path
        else if (member.getFleetData() != null && member.getFleetData().getFleet() != null && !member.getFleetCommander().isPlayer()
                && member.getFleetCommander().getFaction().getId().contains("luddic_church")
                || member.getFleetCommander().getFaction().getId().contains("luddic_path")){
            PAINTJOB_VOLT = 2;//Verdant green
            return;
        }

        else if (member.getFleetData() != null && member.getFleetData().getFleet() != null && !member.getFleetCommander().isPlayer()
                && member.getFleetCommander().getFaction().getId().contains("pirates")){
            PAINTJOB_VOLT = 3;//Crimson red, yarr harr~
            return;
        }

        else if (member.getFleetData() != null && member.getFleetData().getFleet() != null && !member.getFleetCommander().isPlayer()
                && member.getFleetCommander().getFaction().getId().contains("mercenary")
                || member.getFleetCommander().getFaction().getId().contains("independent")){
            PAINTJOB_VOLT = 5;//Commando urban
            return;
        }

        else PAINTJOB_VOLT = 0;//Default bloo
        return;
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        //swap livery if hullmod is removed / not present
        boolean toSwitch=true;
        for(int i=0; i<SWITCH.size(); i++){
            if(stats.getVariant().getHullMods().contains(SWITCH.get(i))){
                toSwitch=false;
            }
        }

        if(toSwitch){
            //select new livery
            int selected;
            if(stats.getVariant().getWeaponSpec(decocounterID)!=null){
                selected=SWITCH_TO.get(stats.getVariant().getWeaponSpec(decocounterID).getWeaponId());
            }
            else{
                selected=PAINTJOB_VOLT;
            }

            //add the proper hullmod
            stats.getVariant().addMod(SWITCH.get(selected));

            //clear the deco_counter slot that is to be refilled
            stats.getVariant().clearSlot(decocounterID);
            String toInstallDeco=DECO_COUNTER.get(selected);

            //refill deco_counter slot with the selected deco
            stats.getVariant().addWeapon(decocounterID, toInstallDeco);

        }
    }

    //fix for deco_counter appearing in cargo
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        if (ship.getOriginalOwner() < 0) {
            if (
                    Global.getSector() != null &&
                            Global.getSector().getPlayerFleet() != null &&
                            Global.getSector().getPlayerFleet().getCargo() != null &&
                            Global.getSector().getPlayerFleet().getCargo().getStacksCopy() != null &&
                            !Global.getSector().getPlayerFleet().getCargo().getStacksCopy().isEmpty()
            ) {
                for (CargoStackAPI s : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()) {
                    if (
                            s.isWeaponStack() && (
                                    DECO_COUNTER.containsValue(s.getWeaponSpecIfWeapon().getWeaponId())
                            )
                    ) {
                        Global.getSector().getPlayerFleet().getCargo().removeStack(s);
                    }
                }
            }
        }
    }
}
