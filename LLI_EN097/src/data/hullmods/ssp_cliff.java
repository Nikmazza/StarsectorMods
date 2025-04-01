package data.hullmods;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;

import java.awt.*;

public class ssp_cliff extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getCrewLossMult().modifyMult(id,0.2f);
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        MutableShipStatsAPI stats=ship.getMutableStats();
        float mult = stats.getDynamic().getMod("ssp_cliff_Mult").computeEffective(1f);
        stats.getMissileWeaponDamageMult().modifyMult("ssp_cliff",1+((1-(ship.getHitpoints()/ship.getMaxHitpoints()))*0.05f*mult));
        stats.getBallisticWeaponDamageMult().modifyMult("ssp_cliff",1+((1-(ship.getHitpoints()/ship.getMaxHitpoints()))*0.05f*mult));
        stats.getEnergyWeaponDamageMult().modifyMult("ssp_cliff",1+((1-(ship.getHitpoints()/ship.getMaxHitpoints()))*0.05f*mult));

        stats.getShieldUpkeepMult().modifyMult("ssp_cliff",1-((1-(ship.getHitpoints()/ship.getMaxHitpoints()))*0.25f*mult));
        stats.getPhaseCloakUpkeepCostBonus().modifyMult("ssp_cliff",1-((1-(ship.getHitpoints()/ship.getMaxHitpoints()))*0.25f*mult));

        stats.getZeroFluxMinimumFluxLevel().modifyFlat("ssp_cliff",(1-(ship.getHitpoints()/ship.getMaxHitpoints()))*0.50f*mult);
        stats.getZeroFluxSpeedBoost().modifyMult("ssp_cliff",1+(1-(ship.getHitpoints()/ship.getMaxHitpoints()))*0.20f*mult);
    }
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float opad = 10f;
        float mult = ship.getMutableStats().getDynamic().getMod("ssp_cliff_Mult").computeEffective(1f);
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_cliff_tooltip0"),
                opad, h, "80%",""+(int)(5*mult)+"%",""+(int)(25*mult)+"%",""+(int)(50*mult)+"%",""+(int)(20*mult)+"%","");
        label.setHighlight("80%",""+(int)(5*mult)+"%",""+(int)(25*mult)+"%",""+(int)(50*mult)+"%",""+(int)(20*mult)+"%","");
        label.setHighlightColors(h,h,h,h,h,h);
        tooltip.addPara(SSPI18nUtil.getHullModString("ssp_cliff_tooltip1"),Color.GRAY, opad);
    }
    public int getDisplaySortOrder() { return 99; }
}