package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.SSPI18nUtil;
import data.scripts.util.MagicIncompatibleHullmods;

import java.awt.*;

public class ssp_overloadprotection extends BaseHullMod {

    public static final Color JITTER_COLOR = new Color(75,255,95,200);
    public static float OverLoad_Protection=25f;
    public static float Colddown=60f;


    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getVariant().getHullMods().contains("reinforcedhull")) {
            //if someone tries to install sussy hullmodsus, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "reinforcedhull",
                    "ssp_overloadprotection"
            );
        }
    }
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getOverloadTimeMod().modifyMult(id,1-(OverLoad_Protection*0.01f) );
        stats.getDynamic().getMod(Stats.INDIVIDUAL_SHIP_RECOVERY_MOD).modifyFlat(id, 1000f);
        stats.getBreakProb().modifyMult(id, 0f);
    }
    public void advanceInCombat(ShipAPI ship, float amount) {
        if(ship.getFluxTracker().isOverloaded()){
            ship.getMutableStats().getHullDamageTakenMult().modifyMult(this.spec.getId(), 1-(OverLoad_Protection*0.01f));
            ship.getMutableStats().getArmorDamageTakenMult().modifyMult(this.spec.getId(), 1-(OverLoad_Protection*0.01f));
            ship.setJitterUnder(ship,JITTER_COLOR,2,8,10f);
            Global.getCombatEngine().maintainStatusForPlayerShip(this.spec.getId(), "graphics/icons/hullsys/damper_field.png", SSPI18nUtil.getHullModString("ssp_overloadprotection"),
                    SSPI18nUtil.getHullModString("ssp_overloadprotection_Activeing"), false);
        }else{
            ship.getMutableStats().getHullDamageTakenMult().unmodify(this.spec.getId());
            ship.getMutableStats().getArmorDamageTakenMult().unmodify(this.spec.getId());
        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "" + (int)OverLoad_Protection+"%";
      //  if (index == 1) return "" +(int)Colddown+"";
        if (index == 1) return Global.getSettings().getHullModSpec("reinforcedhull").getDisplayName();
        return null;
    }
    //    public static class ssp_overloadprotectionEffect implements AdvanceableListener {
//        protected ShipAPI ship;
//        protected String id;
//        protected float sinceProc = Colddown+1f;
//        boolean active = false;
//
//       public ssp_overloadprotectionEffect(ShipAPI ship) { this.ship = ship; }
//        public void advance(float amount) {
//           sinceProc += amount;
//           MutableShipStatsAPI stats = ship.getMutableStats();
//                if(ship.getFluxTracker().isOverloaded() && sinceProc > Colddown ){
//                    stats.getHullDamageTakenMult().modifyMult(id, 1-(OverLoad_Protection*0.01f));
//                    stats.getArmorDamageTakenMult().modifyMult(id, 1-(OverLoad_Protection*0.01f));
//                    ship.setJitterUnder(ship,JITTER_COLOR,2,8,10f);
//                    active = true;
//                }else if(active && sinceProc > Colddown && !ship.getFluxTracker().isOverloaded()){
//                    stats.getHullDamageTakenMult().unmodify(id);
//                    stats.getArmorDamageTakenMult().unmodify(id);
//                    active = false;
//                    sinceProc = 0f;
//                }
//            if (Global.getCombatEngine().getPlayerShip() == ship) {
//                if(sinceProc > Colddown && !active){
//                Global.getCombatEngine().maintainStatusForPlayerShip(id, "graphics/icons/hullsys/damper_field.png", SSPI18nUtil.getHullModString("ssp_overloadprotection"),
//                        String.format(SSPI18nUtil.getHullModString("ssp_overloadprotection_ready"), ""), false);
//                }else if(sinceProc < Colddown && !active){
//                 Global.getCombatEngine().maintainStatusForPlayerShip(id, "graphics/icons/hullsys/damper_field.png", SSPI18nUtil.getHullModString("ssp_overloadprotection"),
//                        String.format(SSPI18nUtil.getHullModString("ssp_overloadprotection_CD"), (int)(Colddown-sinceProc)+""), true);
//                } else if(active){
//                Global.getCombatEngine().maintainStatusForPlayerShip(id, "graphics/icons/hullsys/damper_field.png", SSPI18nUtil.getHullModString("ssp_overloadprotection"),
//                        String.format(SSPI18nUtil.getHullModString("ssp_overloadprotection_Activeing"), ""), false);
//                }
//            }
//       }
//    }
}
