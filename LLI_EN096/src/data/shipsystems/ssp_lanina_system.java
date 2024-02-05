package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.SSPI18nUtil;

public class ssp_lanina_system extends BaseShipSystemScript {

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        float bonus=0f;
        if(stats.getVariant().hasHullMod("ssp_LongerRange")){bonus=75f;}
    if (state == ShipSystemStatsScript.State.OUT) {
        stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
        stats.getMaxTurnRate().unmodify(id);
    } else {
        stats.getMaxSpeed().modifyFlat(id, 125f+bonus);
        stats.getAcceleration().modifyPercent(id, 800f * effectLevel);
        stats.getDeceleration().modifyPercent(id, 800f * effectLevel);
        stats.getTurnAcceleration().modifyFlat(id, 60f * effectLevel);
        stats.getTurnAcceleration().modifyPercent(id, 400f * effectLevel);
        stats.getMaxTurnRate().modifyFlat(id, 30f);
        stats.getMaxTurnRate().modifyPercent(id, 200f);
    }
}
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(SSPI18nUtil.getShipSystemString("ssp_lanina_system"), false);
        }
        return null;
    }
    @Override
    public float getActiveOverride(ShipAPI ship) {
        if (ship != null) {
            if (ship.getVariant().hasHullMod("ssp_LongerRange")){return 2f;}}
        return -1;
    }

}
