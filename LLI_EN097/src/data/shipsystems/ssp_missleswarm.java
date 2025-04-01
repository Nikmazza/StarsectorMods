package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ssp_missleswarm extends BaseShipSystemScript {

    public static final Color JITTER_UNDER_COLOR = new Color(255,90,90,220);
    public class ssp_missleswarm_customdata{
        public Map<ShipAPI, Float> HaHaHashmap = new HashMap();
        public ssp_missleswarm_customdata(){}
    }
    public void apply(MutableShipStatsAPI stats, String id, ShipSystemStatsScript.State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return; }
        ssp_missleswarm_customdata CustomData = (ssp_missleswarm_customdata) Global.getCombatEngine().getCustomData().get("ssp_missleswarm_mode");
        if (CustomData == null){
            CustomData = new ssp_missleswarm_customdata();
            Global.getCombatEngine().getCustomData().put("ssp_missleswarm_mode",CustomData);
        }
        float Mode;
        if (CustomData.HaHaHashmap.containsKey(ship)) {
            Mode = CustomData.HaHaHashmap.get(ship);
        } else {
            Mode = 0F;
        }
        if(effectLevel==1){
            Mode++;
        }
        if(Mode>2){Mode=0;}
        CustomData.HaHaHashmap.put(ship,Mode);

    }
    public void unapply(MutableShipStatsAPI stats, String id) {
    }
    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        ssp_missleswarm_customdata CustomData = (ssp_missleswarm_customdata) Global.getCombatEngine().getCustomData().get("ssp_missleswarm_mode");
        if(CustomData!=null && CustomData.HaHaHashmap.get(ship)!=null && CustomData.HaHaHashmap.get(ship)==0){return DamageType.FRAGMENTATION.getDisplayName(); }
        else if(CustomData!=null && CustomData.HaHaHashmap.get(ship)!=null && CustomData.HaHaHashmap.get(ship)==1){return DamageType.KINETIC.getDisplayName();}
        else if(CustomData!=null && CustomData.HaHaHashmap.get(ship)!=null && CustomData.HaHaHashmap.get(ship)==2){return DamageType.HIGH_EXPLOSIVE.getDisplayName();}
        else return DamageType.FRAGMENTATION.getDisplayName();
    }
}
