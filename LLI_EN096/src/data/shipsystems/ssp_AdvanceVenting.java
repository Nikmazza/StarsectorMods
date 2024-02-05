package data.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.SSPI18nUtil;

import java.awt.*;

public class ssp_AdvanceVenting extends BaseShipSystemScript {
    public static final Color JITTER_UNDER_COLOR = new Color(220, 75, 75, 255);

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship=null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return; }
        float Depression=ship.getMutableStats().getFluxDissipation().base;
        float CD_Min=6f;
        float Timemult=1f;
        float HardFluxDissipationflat=0f;
        if (ship.getVariant().hasHullMod("ssp_LongerRange")) {CD_Min=9f; Timemult=4f;}
        if (ship.getVariant().hasHullMod("ssp_ShortRange") && ship.getFluxTracker().getHardFlux()==ship.getFluxTracker().getCurrFlux()) {HardFluxDissipationflat=200f;}
        stats.getFluxDissipation().modifyFlat(id,7*Depression+HardFluxDissipationflat);
        stats.getTimeMult().modifyMult(id,Timemult);


        //刷新冷却
        float HARD=ship.getFluxTracker().getHardFlux();
        float ALL=ship.getFluxTracker().getMaxFlux();
        float caculate= (HARD/ALL)*ship.getSystem().getCooldown();
        if (caculate<=CD_Min ) caculate=CD_Min;
        if(state==State.OUT){
            ship.getSystem().setCooldownRemaining(caculate);//面板冷却时间
        }
        //视觉效果
        float jitterLevel = effectLevel;
        float jitterRangeBonus = 0;
        float maxRangeBonus = 10f;
        if (state == State.IN) {
            jitterLevel = effectLevel / (1f / ship.getSystem().getChargeUpDur());
            if (jitterLevel > 1) {
                jitterLevel = 1f;
            }
            jitterRangeBonus = jitterLevel * maxRangeBonus;
        } else if (state == State.ACTIVE) {
            jitterLevel = 1f;
            jitterRangeBonus = maxRangeBonus;
        } else if (state == State.OUT) {
            jitterRangeBonus = jitterLevel * maxRangeBonus;
        }
        jitterLevel = (float) Math.sqrt(jitterLevel);
        effectLevel *= effectLevel;
        ship.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel, 3, 0f, 4f + jitterRangeBonus);
    }
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getFluxDissipation().unmodify(id);
        stats.getTimeMult().unmodify(id);
    }
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(SSPI18nUtil.getShipSystemString("ssp_AdvanceVenting"), false);
        }
        return null;
    }
//    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
//        float Curr_Flux=ship.getFluxTracker().getCurrFlux();
//        return Curr_Flux > 0;
//    }
}
