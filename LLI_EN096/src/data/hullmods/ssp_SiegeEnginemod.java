package data.hullmods;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.IntervalUtil;

public class ssp_SiegeEnginemod extends BaseHullMod {
    public static float Fleet_Support=5f;
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        float log=0f;
        float nagetive=0f;
        if(stats.getVariant().hasHullMod("additional_berthing")) {log+=1;}
        if(stats.getVariant().hasHullMod("auxiliary_fuel_tanks")){log+=1;}
        if(stats.getVariant().hasHullMod("expanded_cargo_holds")){log+=1;}
        for (String Smods:stats.getVariant().getSMods()){
            if(Smods.equals("additional_berthing")){nagetive+=1f;}
            if(Smods.equals("auxiliary_fuel_tanks")){nagetive+=1f;}
            if(Smods.equals("expanded_cargo_holds")){nagetive+=1f;}
        }
        if(log-nagetive>=2){stats.getVariant().addMod("ssp_logistics");}
        if(log-nagetive<2 && stats.getVariant().hasHullMod("ssp_logistics")){stats.getVariant().removeMod("ssp_logistics");}
        stats.getDynamic().getMod(Stats.FLEET_GROUND_SUPPORT).modifyFlat(id, 300);
        stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, Fleet_Support);
        stats.getDynamic().getMod(Stats.COORDINATED_MANEUVERS_FLAT).modifyFlat(id, Fleet_Support);
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return (int)Fleet_Support+"%";
        if (index == 1) return (int)Fleet_Support+"%";
        if (index == 2) return "300";
        if (index == 3) return Global.getSettings().getHullModSpec("additional_berthing").getDisplayName();
        if (index == 4) return Global.getSettings().getHullModSpec("auxiliary_fuel_tanks").getDisplayName();
        if (index == 5) return Global.getSettings().getHullModSpec("expanded_cargo_holds").getDisplayName();
        return null;
    }

//    @Override
//    public void advanceInCombat(ShipAPI ship, float amount) {
//        IntervalUtil Interval = new IntervalUtil(0.5f, 0.5f);
//        Interval.advance(amount);
//        if(Interval.intervalElapsed()){
//            ship.addListener(ssp_SiegeEnginemod_listener.class);
//        }
//    }
//    public static class ssp_SiegeEnginemod_listener implements AdvanceableListener {
//        protected ShipAPI ship;
//        protected String id;
//        protected float t=0f;
//        public ssp_SiegeEnginemod_listener(ShipAPI ship, String id) {
//            this.ship = ship;
//            this.id = id;
//        }
//
//        @Override
//        public void advance(float amount) {
//        //if(Global.getSector()==null && Global.getSector().getPlayerStats()==null && Global.getSector().getPlayerStats().getDynamic()==null)return;
//        t+=amount;
//        Global.getSector().getPlayerStats().getDynamic().getMod(Stats.ELECTRONIC_WARFARE_MAX).modifyFlat("ssp_SiegeEnginemod_effectid", 5f);
//        Global.getSector().getPlayerStats().getDynamic().getMod(Stats.COORDINATED_MANEUVERS_MAX).modifyFlat("ssp_SiegeEnginemod_effectid", 5f);
//        ship.getMutableStats().getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat("ssp_SiegeEnginemod_effectid", Fleet_Support);
//        if(t==5f) {
//            Global.getSector().getPlayerStats().getDynamic().getMod(Stats.ELECTRONIC_WARFARE_MAX).unmodify("ssp_SiegeEnginemod_effectid");
//            Global.getSector().getPlayerStats().getDynamic().getMod(Stats.COORDINATED_MANEUVERS_MAX).unmodify("ssp_SiegeEnginemod_effectid");
//            ship.getMutableStats().getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).unmodify("ssp_SiegeEnginemod_effectid");
//            ship.removeListenerOfClass(ssp_SiegeEnginemod_listener.class);
//        }
//    }
//}
}
