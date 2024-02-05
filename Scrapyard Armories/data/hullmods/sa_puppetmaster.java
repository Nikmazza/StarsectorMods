package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
/**
 *Made by Mayu
 */
public class sa_puppetmaster extends BaseHullMod {

    public static final float DAMAGE_BONUS = 70f; // bonuses
    public static final float AUTOFIRE_BONUS = 20f; // ditto
	private static final float SUPPLY_PENALTY = 1.10f; // malus or debuff
	
    @Override
    public void applyEffectsBeforeShipCreation(final HullSize hullSize, final MutableShipStatsAPI stats, final String id) {
		stats.getAutofireAimAccuracy().modifyFlat(id, AUTOFIRE_BONUS * 0.01f); // Autofire aim bonus modifier

		stats.getDamageToMissiles().modifyPercent(id, DAMAGE_BONUS); // Damage to missile modifier
		stats.getDamageToFighters().modifyPercent(id, DAMAGE_BONUS); // Damage to fighters modifier
		
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_PENALTY); // malus, note: modifyMult means it is multiplied by 1.10 which is equals to 10%
    }
	//Below is where you put incompatible hullmods or any conditions you like when this hullmod exist
	//Since we don't need it, I'll just put it here for pointers
	@Override
	public boolean isApplicableToShip(final ShipAPI ship) {
		return true;
	}
	//Built-in
	public String getUnapplicableReason(final ShipAPI ship) {
		return null;
	}	

    @Override
    public String getDescriptionParam(final int index, final HullSize hullSize) {
		//Since we are going to use a much better looking tooltip, all you need to do is to put the
		//Descriptions in the hullmod.csv
        return null;
    }	
	//The fancy hullmod tooltips goes here
    @Override
    public void addPostDescriptionSection(final TooltipMakerAPI tooltip, final ShipAPI.HullSize hullSize, final ShipAPI ship, final float width, final boolean isForModSpec) {
		final Color green = Misc.getPositiveHighlightColor();
		final Color red = Misc.getNegativeHighlightColor();
        final Color flavor = new Color(110,110,110,255);
        float pad = 10f;
		float padNeg = 0f;
		float padQuote = 6f;
		float padSig = 1f;
		//float padList = 2f;
		tooltip.addSectionHeading("Details", Alignment.MID, pad);
		tooltip.addPara("- Increased autofire accuracy: %s \n- Increased damage to missiles and fighters: %s", pad, green, new String[] { Misc.getRoundedValue(20.0f) + "%", Misc.getRoundedValue(20.0f) + "%"});
		tooltip.addPara("- Supply cost is increased: %s", padNeg, red, new String[] { Misc.getRoundedValue(10.0f) + "%" });
        tooltip.addPara("%s", padQuote, flavor, new String[] { "\"You think you pull the strings, when you're but the puppet.\"" });
        tooltip.addPara("%s", padSig, flavor, new String[] { "         \u2014 Unknown message received upon activation" });  	
	}	
}