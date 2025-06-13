package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
/**
 *Made by Mayu
 */
public class csp_multi_layered_shielding extends BaseHullMod {

    public static final float EMP_RESISTANCE = 15f;
    public static final float SHIELD_KINETIC_REDUCTION = 15f;
    public static final float SHIELD_UPKEEP_BONUS = 25f;
    
    @Override
    public void applyEffectsBeforeShipCreation(final HullSize hullSize, final MutableShipStatsAPI stats, final String id) {
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - EMP_RESISTANCE * 0.01f);
        stats.getKineticShieldDamageTakenMult().modifyMult(id, 1f - SHIELD_KINETIC_REDUCTION / 100f);
        stats.getShieldUpkeepMult().modifyMult(id, 1f - SHIELD_UPKEEP_BONUS * 0.01f);
    }
	
    @Override
    public String getDescriptionParam(final int index, final HullSize hullSize) {
        return null;
    }

    @Override
    public boolean isApplicableToShip(final ShipAPI ship) {
        return true;
    }
    //Built-in
    @Override
    public String getUnapplicableReason(final ShipAPI ship) {
        return null;
    }
	
    @Override
    public void addPostDescriptionSection(final TooltipMakerAPI tooltip, final ShipAPI.HullSize hullSize, final ShipAPI ship, final float width, final boolean isForModSpec) {
        final Color green = new Color(55,245,65,255);
        final Color flavor = new Color(110,110,110,255); // quote color
        final float pad = 10f;
        final float padQuote = 6f; // quote padding
        final float padSig = 1f; // ditto
        tooltip.addSectionHeading("Details", Alignment.MID, pad);
        tooltip.addPara("- Reduces incoming EMP damage: %s \n- Reduced kinetic damage to shields: %s \n- Reduced flux upkeep for shields: %s", pad, green, new String[] { Misc.getRoundedValue(15.0f) + "%", Misc.getRoundedValue(15.0f) + "%", Misc.getRoundedValue(25.0f) + "%" });
        tooltip.addPara("%s", padQuote, flavor, new String[] { "\"The Sun’s coronas is one thing, but the sheer kinetic energy of the sea’s wraith is another.\"" });
        tooltip.addPara("%s", padSig, flavor, new String[] { "         \u2014 Lion's Guard Fleet Captain" });
    }	

}