package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.SSPI18nUtil;

public class ssp_microburn extends BaseShipSystemScript {

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship=(ShipAPI)stats.getEntity();
        float TurnRate_MULT=0.05f;
        float TurnAcceleration_MULT=10f;
        if(stats.getVariant().hasHullMod("ssp_LongerRange")){ TurnRate_MULT=5f;  TurnAcceleration_MULT=1f; }
        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
        }  else {
            stats.getMaxSpeed().modifyFlat(id, 350f  * effectLevel);
            stats.getAcceleration().modifyFlat(id, 1200f * effectLevel);
            stats.getTurnAcceleration().modifyMult(id,TurnAcceleration_MULT);
            stats.getMaxTurnRate().modifyMult(id,TurnRate_MULT);
        }
        if (Global.getCombatEngine().getPlayerShip() == ship) {
            boolean isdebuff = true;
            String TurnRateString=SSPI18nUtil.getShipSystemString("ssp_microburn_ZeroTurnRate");
            if (stats.getVariant().hasHullMod("ssp_LongerRange")){isdebuff =false; TurnRateString=SSPI18nUtil.getShipSystemString("ssp_microburn_FastTurnRate");}
            Global.getCombatEngine().maintainStatusForPlayerShip(
                    id,
                    ship.getSystem().getSpecAPI().getIconSpriteName(),
                    ship.getSystem().getDisplayName(),
                    TurnRateString,
                    isdebuff
            );
        }
    }
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(SSPI18nUtil.getShipSystemString("ssp_microburn"), false);
        }
        return null;
    }
    @Override
    public int getUsesOverride(ShipAPI ship) {
        if (ship != null) {
            if (ship.getVariant().hasHullMod("ssp_LongerRange")) { return 2;}
            if (ship.getVariant().hasHullMod("ssp_ShortRange")){return 4;}
        }
        return -1;
    }
    @Override
    public float getRegenOverride(ShipAPI ship) {
        if (ship != null) {
            if (ship.getVariant().hasHullMod("ssp_LongerRange")){return 0.10f;}}
        return -1;
    }

    @Override
    public float getInOverride(ShipAPI ship) {
        if (ship != null) {
            if (ship.getVariant().hasHullMod("ssp_ShortRange")){return 0.6f;}}
        return -1;
    }
    @Override
    public float getActiveOverride(ShipAPI ship) {
        if (ship != null) {
            if (ship.getVariant().hasHullMod("ssp_ShortRange")){return 0.6f;}}
        return -1;
    }
    @Override
    public float getOutOverride(ShipAPI ship) {
        if (ship != null) {
            if (ship.getVariant().hasHullMod("ssp_ShortRange")){return 0.6f;}}
        return -1;
    }
}


