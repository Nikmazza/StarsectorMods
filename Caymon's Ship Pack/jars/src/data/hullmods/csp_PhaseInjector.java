package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI; // this import is needed for the fancy hullmod tooltips
import com.fs.starfarer.api.ui.Alignment; // same goes here
//import com.fs.starfarer.api.util.Misc; // and here if you want to use something like Misc.getHighlightColor()
import java.awt.Color; // and lastly, this one too
/**
* please take your meds techpriest
* regards,
* -Mayu
*/
public class csp_PhaseInjector extends BaseHullMod {
	
    private static final String CSP_PHASE_INJECTOR = "csp_PhaseInjector";
    public static final float PHASE_SPEED_BONUS = 3f; //triples via mult
	
    @Override
    public void applyEffectsBeforeShipCreation(final HullSize hullSize, final MutableShipStatsAPI stats, final String id) {
        //sneed
    }
	
    @Override
    public void advanceInCombat(final ShipAPI ship, final float amount) {
        final MutableShipStatsAPI stats = ship.getMutableStats();
        if (!ship.isAlive()) return;
        if (ship.isPhased()) {
            stats.getMaxSpeed().modifyMult(CSP_PHASE_INJECTOR, PHASE_SPEED_BONUS);
        }
        else if (ship.isPhased()) {
            stats.getMaxSpeed().modifyMult(CSP_PHASE_INJECTOR, PHASE_SPEED_BONUS);
        }
        else {
            stats.getMaxSpeed().unmodify(CSP_PHASE_INJECTOR);
        }
    }	
    // Since the detailed stats goes into the fancy tooltip, you don't need to put anything here; just the basic descriptions
    @Override
    public String getDescriptionParam(final int index, final HullSize hullSize) {
        //if (index == 0) return "triples";
        return null;
    }
    // The fancy hullmod tooltip, embrace renaissance
    @Override
    public void addPostDescriptionSection(final TooltipMakerAPI tooltip, final ShipAPI.HullSize hullSize, final ShipAPI ship, final float width, final boolean isForModSpec) {
        final Color green = new Color(55,245,65,255); // Color of the positive value that this hullmod adds which is green colored
        //final Color red = new Color(255,0,0,255); // Here's another example of color, I use this for maluses or negative value     
        final float pad = 10f; // The required padding for the paragraphs so it won't look cramped; 10f is the standard
        tooltip.addSectionHeading("Details", Alignment.MID, pad); // Header or the title of the section. Another example is "Incompatibilities"
        tooltip.addPara("- %s the max top speed of the ship during phase.", pad, green, new String[] { "Triples" });
        // %s means it has to call the value from 'newString[] { "have sex" };' and if you want to call a string with numbers then it should be-
        // Misc.getRoundedValue(69.0f) + "%" or if it is just a flat value just make it Misc.getRoundedValue(69.0f) + "" without the percent sign within the -> ""
    }
    
}