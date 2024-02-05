package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import data.SSPI18nUtil;
import org.magiclib.util.MagicIncompatibleHullmods;
import org.lazywizard.lazylib.MathUtils;

public class ssp_PeakTimeoverride extends BaseHullMod {
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getCRLossPerSecondPercent().modifyMult(id,0.5f);
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new ssp_Listener_PeakTime(ship, id));
        if (ship.getVariant().getHullMods().contains("safetyoverrides")) {
            //if someone tries to install safetyoverrides, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "safetyoverrides",
                    "ssp_PeakTimeoverride"
            );
        }
        else if (ship.getVariant().getHullMods().contains("hardened_subsystems")) {
            //if someone tries to install safetyoverrides, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "hardened_subsystems",
                    "ssp_PeakTimeoverride"
            );
        }
    }
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff") && !ship.getVariant().hasHullMod("safetyoverrides");
    }
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("ssp_cliff")) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "90%";
        }else if (index == 1){
            return "110%";
        }else if (index == 2){
            return "50%";
        }
        return null;
    }
    public static class ssp_Listener_PeakTime implements AdvanceableListener {
        protected float modify_times=0f;
        protected float STAGE=0f;
        protected ShipAPI ship;
        protected String id;
        public ssp_Listener_PeakTime(ShipAPI ship, String id) {
            this.ship = ship;
            this.id = id;
        }
        public void advance(float amount) {
            MutableShipStatsAPI stats = ship.getMutableStats();
            float noLossTime = ship.getMutableStats().getPeakCRDuration().computeEffective(ship.getHullSpec().getNoCRLossTime());
            float LossTime = ship.getTimeDeployedForCRReduction();
            float CurrentTime = noLossTime-LossTime;
            float MODIFY=10f-20f*(CurrentTime/noLossTime);
            MODIFY= MathUtils.clamp(MODIFY,-10,10);
            if (ship.losesCRDuringCombat() && ship.getCurrentCR() > 0 ) {
                stats.getFluxDissipation().modifyPercent(id,MODIFY);
                stats.getFluxCapacity().modifyPercent(id,MODIFY);
                stats.getMaxSpeed().modifyPercent(id,MODIFY);
                stats.getAcceleration().modifyPercent(id,MODIFY);
                stats.getDeceleration().modifyPercent(id,MODIFY);
                stats.getTurnAcceleration().modifyPercent(id,MODIFY);
                stats.getMaxTurnRate().modifyPercent(id,MODIFY);
            }
            if (noLossTime <= ship.getTimeDeployedForCRReduction() && ship.getCurrentCR() > 0) {
                stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 2f); // set to two, meaning boost is always on
            }
            if(ship.getCurrentCR()==0){
                stats.getFluxDissipation().unmodify(id);
                stats.getFluxCapacity().unmodify(id);
                stats.getMaxSpeed().unmodify(id);
                stats.getAcceleration().unmodify(id);
                stats.getDeceleration().unmodify(id);
                stats.getTurnAcceleration().unmodify(id);
                stats.getMaxTurnRate().unmodify(id);
                stats.getZeroFluxMinimumFluxLevel().unmodify(id);
                }
            if (Global.getCombatEngine().getPlayerShip() == ship) {
                boolean isdebuff = false;
                if (MODIFY<=0){isdebuff =true;}else if(MODIFY>0){isdebuff =false;}

                Global.getCombatEngine().maintainStatusForPlayerShip(id, "graphics/icons/hullsys/emp_emitter.png", "元件步进改制",
                        String.format(SSPI18nUtil.getHullModString("ssp_PeakTimeoverride"), (Math.round(MODIFY*10f))/10f ), isdebuff);
            }
        }
    }
}
