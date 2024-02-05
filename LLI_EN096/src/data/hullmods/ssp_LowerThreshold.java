package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;


public class ssp_LowerThreshold  extends BaseHullMod {
    public static float SMOD_CR_COMPENSATE = 0.15f;
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        boolean sMod = isSMod(stats);
        if(sMod && !stats.getVariant().hasHullMod("automated")){
            stats.getVariant().addPermaMod("automated",false);
        }else if(!sMod && stats.getVariant().hasHullMod("automated")){
            stats.getVariant().removePermaMod("automated");
        }
        if(sMod){stats.getMaxCombatReadiness().modifyFlat(id,SMOD_CR_COMPENSATE,Global.getSettings().getHullModSpec("ssp_LowerThreshold").getDisplayName());}
        stats.getDynamic().getMod("ssp_cliff_Mult").modifyMult(id,1.25f);
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff") && !ship.getVariant().hasHullMod("ssp_ShortRange")&& !ship.getVariant().hasHullMod("ssp_LongerRange");
    }
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("ssp_cliff") ) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "25%";
        }
        return null;
    }
    public String getSModDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return ""+ Global.getSettings().getHullModSpec("automated").getDisplayName();
        if (index == 1) return ""+(int)(SMOD_CR_COMPENSATE*100)+"%";
        return null;
    }
    public boolean hasSModEffect() { return true; }
}
