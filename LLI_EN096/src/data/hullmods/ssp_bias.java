package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.*;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;

import java.awt.*;

public class ssp_bias extends BaseHullMod {
    public static final float RANGE_BUFF = 100f;
    public static final float RANGE_DEBUFF = -200f;
    Color COLOR =  new Color(255,140,140, 255);
    Color COLOR_HAS_HIGH_SCATTER_AMP =  new Color(160,213,225,255);
    protected boolean HAS_HIGH_SCATTER_AMP=false;

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if(ship.getVariant().hasHullMod("ssp_modetransform")){HAS_HIGH_SCATTER_AMP=true;}else{HAS_HIGH_SCATTER_AMP=false;}
        ship.addListener(new ssp_bias_EffectMod(ship, id));
    }

    public static class ssp_bias_EffectMod implements WeaponRangeModifier {
        protected ShipAPI ship;
        protected String id;
        public ssp_bias_EffectMod(ShipAPI ship, String id) {
            this.ship = ship;
            this.id = id;
        }
        public float getWeaponRangePercentMod(ShipAPI ship, WeaponAPI weapon) {return 0f;}
        public float getWeaponRangeMultMod(ShipAPI ship, WeaponAPI weapon){return 1f;}
        public float getWeaponRangeFlatMod(ShipAPI ship, WeaponAPI weapon){
            float RANGE=0f;
            if(!ship.getVariant().hasHullMod("ssp_modetransform")){
                if(weapon.hasAIHint(WeaponAPI.AIHints.PD)){RANGE=0f;}
                if(weapon.isBeam() && !weapon.hasAIHint(WeaponAPI.AIHints.PD)){RANGE=RANGE_DEBUFF;}
                if(!weapon.isBeam() && !weapon.hasAIHint(WeaponAPI.AIHints.PD)){RANGE=RANGE_BUFF;}
            }else {
                if(weapon.isBeam()){RANGE=RANGE_BUFF;}
            }
            return RANGE;
        }
    }
    //新描述哦
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_bias_tooltip0"),
                opad, h, Global.getSettings().getHullModSpec("ssp_modetransform").getDisplayName());
        label.setHighlight(Global.getSettings().getHullModSpec("ssp_modetransform").getDisplayName());
        label.setHighlightColors(h);
        if(ship.getVariant().hasHullMod("ssp_modetransform")) {
            tooltip.addSectionHeading(SSPI18nUtil.getHullModString("ssp_beammode"), Alignment.MID, opad);
            label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_bias_beammode_tooltip"), opad, h,
                    "100");
            label.setHighlight("100");
            label.setHighlightColors(h);
        }else {
            tooltip.addSectionHeading(SSPI18nUtil.getHullModString("ssp_basemode"), Alignment.MID, opad);
            label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_bias_basemode_tooltip"), opad, h,
                    "100", "200");
            label.setHighlight("100", "200");
            label.setHighlightColors(h, b);
        }
    }
    public Color getNameColor() {
        if(HAS_HIGH_SCATTER_AMP)return COLOR_HAS_HIGH_SCATTER_AMP;
        else return COLOR;
    }
    public Color getBorderColor() {
        if(HAS_HIGH_SCATTER_AMP)return COLOR_HAS_HIGH_SCATTER_AMP;
        else return COLOR;
    }
}
