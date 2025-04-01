package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.SSPI18nUtil;

import java.awt.*;

public class ssp_CLIFF_morehp extends ssp_CLIFF_basehullmod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getMod("ssp_CLIFF_morehp").modifyMult(id,1.20f);
        stats.getDynamic().getMod("ssp_cliff_Mult").modifyMult(id,2f);
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "x0.8";
        }else if(index ==1){
            return SSPI18nUtil.getHullModString("ssp_hpmessager");
        }else if(index ==2){
            return "x1.2";
        }else if(index ==3){
            return "x2";
        }
        return null;
    }
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float opad = 10f;
        tooltip.addPara(SSPI18nUtil.getHullModString("ssp_CLIFF_basehullmod"), Color.GRAY, opad);
    }
}
