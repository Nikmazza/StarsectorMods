package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.ArrayList;
import java.util.List;

public class ssp_combatcarrierdeck extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getFighterWingRange().modifyMult(id,0.5f);
        stats.getDynamic().getStat(Stats.FIGHTER_CREW_LOSS_MULT).modifyMult(id, 0f);
        if (stats.getVariant().hasHullMod("malfunctioning_comms")){stats.getVariant().removePermaMod("malfunctioning_comms");}
        if (stats.getVariant().hasHullMod("defective_manufactory")) {stats.getVariant().removePermaMod("defective_manufactory");}
        if (stats.getVariant().hasHullMod("damaged_deck")) {stats.getVariant().removePermaMod("damaged_deck");}
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
            for (ShipAPI fighter : getFighters(ship)) {
            fighter.getMutableStats().getMaxSpeed().modifyMult(this.spec.getId(), 1.5f);
            fighter.getMutableStats().getAcceleration().modifyMult(this.spec.getId(), 1.5f);
            fighter.getMutableStats().getDeceleration().modifyMult(this.spec.getId(), 1.5f);
            fighter.getMutableStats().getTurnAcceleration().modifyMult(this.spec.getId(), 1.5f);
            fighter.getMutableStats().getMaxTurnRate().modifyMult(this.spec.getId(), 1.5f);
            }
    }
    private List<ShipAPI> getFighters(ShipAPI carrier) {
        List<ShipAPI> result = new ArrayList<ShipAPI>();
        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (!ship.isFighter()) continue;
            if (ship.getWing() == null) continue;
            if (ship.getWing().getSourceShip() == carrier) {
                result.add(ship);
            }
        }
        return result;
    }
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "50%";
        }else if(index ==1){
            return "50%";
        }else if(index ==2){
            return "100%";
        }
        return null;
    }
}
