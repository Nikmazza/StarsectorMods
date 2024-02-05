package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class sa_automated_subsystems extends BaseHullMod {
	private static final Set<String> BLOCKED_HULLMODS = new HashSet<String>(4);


	static {
		//id of all hmods you want to block
		BLOCKED_HULLMODS.add("hardened_subsystems");
	}
	public static final float PEAK_BONUS_PERCENT = 40f;
	public static final float DEGRADE_REDUCTION_PERCENT = 20f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getPeakCRDuration().modifyPercent(id, PEAK_BONUS_PERCENT);
		stats.getCRLossPerSecondPercent().modifyMult(id, 1f - DEGRADE_REDUCTION_PERCENT / 100f);
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		super.applyEffectsAfterShipCreation(ship, id);

		for (String hullmod : BLOCKED_HULLMODS) {
			if (ship.getVariant().hasHullMod(hullmod)) {
				ship.getVariant().removeMod(hullmod);
			}
		}
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return !ship.getVariant().getHullMods().contains(HullMods.HARDENED_SUBSYSTEMS);
	}

	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains(HullMods.HARDENED_SUBSYSTEMS)) {
			return "Incompatible with Hardened Subsystems";
		}
		return super.getUnapplicableReason(ship);
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
		tooltip.addPara("- Increased Peak Performance Time: %s \n- Decreased Combat Readiness degradation: %s", pad, green, new String[] { Misc.getRoundedValue(40f) + "%", Misc.getRoundedValue(20.0f) + "%"});
		tooltip.addSectionHeading("Incompatibilities", Alignment.MID, pad);
		tooltip.addPara("- Incompatible with %s", pad, red, "Hardened Subsystems" );
	}

}