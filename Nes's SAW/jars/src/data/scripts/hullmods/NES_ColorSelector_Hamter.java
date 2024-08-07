package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

import java.util.HashMap;
import java.util.Map;


public class NES_ColorSelector_Hamter extends BaseHullMod {

    private final Map<Integer,String> DECO_COUNTER = new HashMap<>();
    {
        DECO_COUNTER.put(0, "nes_fluorspar_deco_counter0");
        DECO_COUNTER.put(1, "nes_fluorspar_deco_counter1");
        DECO_COUNTER.put(2, "nes_fluorspar_deco_counter2");
        DECO_COUNTER.put(3, "nes_fluorspar_deco_counter3");
    }

    private final Map<String, Integer> SWITCH_TO = new HashMap<>();
    {
        SWITCH_TO.put("nes_fluorspar_deco_counter0",1);
        SWITCH_TO.put("nes_fluorspar_deco_counter1",2);
        SWITCH_TO.put("nes_fluorspar_deco_counter2",3);
        SWITCH_TO.put("nes_fluorspar_deco_counter3",0);
    }

    private final Map<Integer,String> SWITCH = new HashMap<>();
    {
        SWITCH.put(0,"NES_Austere");
        SWITCH.put(1,"NES_Cardinal");
        SWITCH.put(2,"NES_Ivory");
        SWITCH.put(3,"NES_Starling");
    }

    private final String decocounterID = "NES_DECO_COUNTER";

    //faction specific paintjob selector for Ham(p)ter
    private static int PAINTJOB_Hamter;

    @Override
    public void advanceInCampaign(FleetMemberAPI member, float amount) {

        //skip this if player
        if (member.getFleetCommander().isPlayer() || member.getFleetCommander().isDefault()) {
            return;
        }

        else if (member.getFleetData() != null && member.getFleetData().getFleet() != null && !member.getFleetCommander().isPlayer()
                && member.getFleetCommander().getFaction().getId().contains("pirates")){
            PAINTJOB_Hamter = 1;//Cardinal red
            return;
        }

        else if (member.getFleetData() != null && member.getFleetData().getFleet() != null && !member.getFleetCommander().isPlayer()
                && member.getFleetCommander().getFaction().getId().contains("independent")
                || member.getFleetCommander().getFaction().getId().contains("persean")){
            PAINTJOB_Hamter = 2;//Ivory yellow
            return;
        }

        else PAINTJOB_Hamter = 0;//Standard lowtech
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
                selected=PAINTJOB_Hamter;
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