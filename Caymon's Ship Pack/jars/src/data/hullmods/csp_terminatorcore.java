package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
/**
 * Modified by Mayu
 */
public class csp_terminatorcore extends BaseHullMod {

    public static final float DAMAGE_MISSILES_PERCENT = 100f;
    public static final float DAMAGE_FIGHTERS_PERCENT = 50f;

    @Override
    public void applyEffectsBeforeShipCreation(final HullSize hullSize, final MutableShipStatsAPI stats, final String id) {
        stats.getDamageToMissiles().modifyPercent(id, DAMAGE_MISSILES_PERCENT);
        stats.getDamageToFighters().modifyPercent(id, DAMAGE_FIGHTERS_PERCENT);
        stats.getBeamWeaponTurnRateBonus().modifyMult(id, 2f);
        stats.getBeamWeaponRangeBonus().modifyFlat(id, 300f);
        stats.getAutofireAimAccuracy().modifyFlat(id, 1f);		
        stats.getEngineDamageTakenMult().modifyMult(id, 0f);		
        stats.getDynamic().getMod(Stats.PD_IGNORES_FLARES).modifyFlat(id, 1f);   
    }
	
    @Override
    public String getDescriptionParam(final int index, final HullSize hullSize) {
        return null;
    }

    @Override
    public void addPostDescriptionSection(final TooltipMakerAPI tooltip, final ShipAPI.HullSize hullSize, final ShipAPI ship, final float width, final boolean isForModSpec) {
        final Color green = new Color(55,245,65,255);  
        final float pad = 10f;
        final float padS = 0f;
        tooltip.addSectionHeading("Details", Alignment.MID, pad);
        tooltip.addPara("- Increased damage to fighters: %s"
                    + "\n- Increased damage to missiles: %s"
                    + "\n- Increased turret turn rate: %s"
                    + "\n- Increased beam weapon range: %s"
                    + "\n- Increased autofire accuracy: %s", pad, green,
                new String[] {
                    Misc.getRoundedValue(50.0f) + "%", // damage to fighters
                    Misc.getRoundedValue(100.0f) + "%", // damage to missiles
                    Misc.getRoundedValue(200.0f) + "%", // turret turn rate
                    Misc.getRoundedValue(300.0f) + "", // beam weapon range
                    Misc.getRoundedValue(100.0f) + "%" // autofire accuracy
                });
        tooltip.addPara("- The engines take %s damage. \n- PD weapons will %s.", padS, Misc.getHighlightColor(), new String[] { "zero", "ignore flares" });
    }
    
}







