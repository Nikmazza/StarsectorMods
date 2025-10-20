package DE.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class AdaptiveFlux extends BaseHullMod {
	public static boolean overloadswitch = false;

	public static final float MAX_RATE = 1.5f;

	public static final float MAX_DAMP = 0.75f;

	public static final float OVERLOAD_FLUX = 0.5f;

	public static final float OVERLOAD_HULL = 1.5f;

	public static final Color JITTER_COLOR = new Color(240,50,240,55);

	public static final Color JITTER_UNDER_COLOR = new Color(240,50,240,155);

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		CombatEngineAPI engine = Global.getCombatEngine();
		float flux = ship.getCurrFlux();
		float maxflux = ship.getMaxFlux();
		MutableShipStatsAPI stats = ship.getMutableStats();
		String id = ship.getId();

		// should be a percentage
		float percbuff = (flux / maxflux) + 0.5f;

		// add a jitter effect as a visual indicator + personal ref of whether it works
		float jitterRangeBonus = 0;
		float maxRangeBonus = 10f;
		int fluxCopyAdd = 0;
		float fluxRangeBonus = 0f;

		// benefits
		if (flux >= maxflux / 2 && !ship.getFluxTracker().isOverloaded()) {
			fluxCopyAdd = (int) (flux / maxflux) * 10;
			fluxRangeBonus = (flux / maxflux) * 10;

			stats.getShieldDamageTakenMult().modifyMult(id, MAX_DAMP / percbuff);
			stats.getShieldUpkeepMult().modifyMult(id, MAX_DAMP / percbuff);
			stats.getFluxDissipation().modifyMult(id, percbuff * MAX_RATE);
			stats.getEnergyRoFMult().modifyMult(id, percbuff * MAX_RATE);
			stats.getBallisticRoFMult().modifyMult(id, percbuff * MAX_RATE);

			// jitter
			ship.setJitter(this, JITTER_COLOR, (flux / maxflux) * 0.5f, fluxCopyAdd, 0, 0 + jitterRangeBonus + fluxRangeBonus);
			ship.setJitterUnder(this, JITTER_UNDER_COLOR, (flux / maxflux) * 0.5f, 5 + (fluxCopyAdd * 3), 0f, 4f + jitterRangeBonus + (fluxRangeBonus * 2));
		}

		// punishment
		if (ship.getFluxTracker().isOverloaded()) {
			stats.getShieldDamageTakenMult().unmodify(id);
			stats.getShieldUpkeepMult().unmodify(id);
			stats.getFluxDissipation().unmodify(id);
			stats.getEnergyRoFMult().unmodify(id);
			stats.getBallisticRoFMult().unmodify(id);
			stats.getFluxDissipation().modifyPercent(id, OVERLOAD_FLUX);
			stats.getHullDamageTakenMult().modifyPercent(id, OVERLOAD_HULL);
			stats.getArmorDamageTakenMult().modifyPercent(id, OVERLOAD_HULL);
			overloadswitch = true;
		}

		// remove the punishment after overloading
		if (!ship.getFluxTracker().isOverloaded() && overloadswitch) {
			stats.getFluxDissipation().unmodify(id);
			stats.getHullDamageTakenMult().unmodify(id);
			stats.getArmorDamageTakenMult().unmodify(id);
			overloadswitch = false;
		}
	}

	// probably unapply after combat is over? idk tbh
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getShieldDamageTakenMult().unmodify(id);
		stats.getShieldUpkeepMult().unmodify(id);
		stats.getFluxDissipation().unmodify(id);
		stats.getEnergyRoFMult().unmodify(id);
		stats.getBallisticRoFMult().unmodify(id);
		stats.getHullDamageTakenMult().unmodify(id);
	}

	public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();

		LabelAPI label = tooltip.addPara("At above %s flux, increase flux dissipation and weapons firerate up to %s. Decrease shield upkeep and damage taken up to a max of %s", opad, h,
				"" + (int) 50f + "%", "" + (int) (MAX_RATE * 100f) + "%", "" + (int) (MAX_DAMP * 100f) + "%");
//		label.setHighlight("base", "Ballistic", "" + (int)BONUS_SMALL_1, "" + (int)BONUS_MAX_1);
//		label.setHighlightColors(h, Misc.MOUNT_BALLISTIC, h, h);
		label.setHighlight("" + (int) (MAX_RATE * 100f) + "%", "" + (int) (MAX_DAMP * 100f) + "%");
		label.setHighlightColors(h, h);

		label = tooltip.addPara("Upon overloading, decrease flux dissipation by %s and increase hull damage by %s", opad, h,
				"" + (int) (OVERLOAD_FLUX * 100f) + "%", "" + (int) (OVERLOAD_HULL * 100f) + "%");
		label.setHighlight("" + (int) (OVERLOAD_FLUX * 100f) + "%", "" + (int) (OVERLOAD_HULL * 100f) + "%");
		label.setHighlightColors(bad, bad);
	}

	/*
		public ShipSystemStatsScript.StatusData getStatusData(int index, ShipSystemStatsScript.State state, float effectLevel) {
			float mult = HE_DAM * effectLevel;
			float bonusPercent = (int) ((mult) * 100f);
			float mult2 = K_DAM * effectLevel;
			float bonusPercent2 = (int) ((mult2) * 100f);
			float mult3 = FRAG_DAM * effectLevel;
			float bonusPercent3 = (int) (((mult3) * 100f) - 100f);
			float mult5 = ENERGY_DAM * effectLevel;
			float bonusPercent4 = (int) (((mult5) * 100f) - 100f);
			float mult6 = EMP_DAM * effectLevel;
			float bonusPercent6 = (int) (((mult5) * 100f) - 100f);
			float bonusPercent7 = (int) ((((5-count)/20) * 100f) - 100f);
			if (index == 0) {
				return new ShipSystemStatsScript.StatusData("high explosive damage taken -" + (int) bonusPercent + "%", false);
			} else if (index == 1) {
				return new ShipSystemStatsScript.StatusData("kinetic damage taken -" + (int) bonusPercent2 + "%", false);
			} else if (index == 2) {
				return new ShipSystemStatsScript.StatusData("fragmentation damage taken +" + (int) bonusPercent3 + "%", true);
			} else if (index == 3) {
				return new ShipSystemStatsScript.StatusData("energy damage taken +" + (int) bonusPercent4 + "%", true);
			} else if (index == 4) {
				return new ShipSystemStatsScript.StatusData("EMP damage taken +" + (int) bonusPercent6 + "%", true);
			} else if (index == 5) {
				return new ShipSystemStatsScript.StatusData("flux reduced by" + (int) bonusPercent7 + "%", false);
			}
			return null;
		}*/
}
