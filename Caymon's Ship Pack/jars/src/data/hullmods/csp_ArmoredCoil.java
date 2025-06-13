package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.PhaseCloakStats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class csp_ArmoredCoil extends BaseHullMod {

	public static float FLUX_THRESHOLD_DECREASE_PERCENT = 0.75f;
	public static float MAX_TIME_MULT = 2f;
	public static float ARMOR_BONUS = 30f;
	public static float HEALTH_BONUS = 100f;
	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FRIGATE, 30f);
		mag.put(HullSize.DESTROYER, 20f);
		mag.put(HullSize.CRUISER, 15f);
		mag.put(HullSize.CAPITAL_SHIP, 0f);
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(
				Stats.PHASE_CLOAK_FLUX_LEVEL_FOR_MIN_SPEED_MOD).modifyMult(id, FLUX_THRESHOLD_DECREASE_PERCENT);
				stats.getArmorBonus().modifyPercent(id, ARMOR_BONUS);
				stats.getWeaponHealthBonus().modifyPercent(id, HEALTH_BONUS);
				stats.getMaxSpeed().modifyFlat(id, -(Float)mag.get(hullSize));
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round(FLUX_THRESHOLD_DECREASE_PERCENT) + "%";
		if (index == 1) return "" + (int) Math.round(PhaseCloakStats.BASE_FLUX_LEVEL_FOR_MIN_SPEED * 100f) + "%";
		if (index == 2) return "" + (int)Math.round(
				PhaseCloakStats.BASE_FLUX_LEVEL_FOR_MIN_SPEED * 100f *
				(1f + FLUX_THRESHOLD_DECREASE_PERCENT/100f)) + "%";
		return null;
	}

	public void addPostDescriptionSection(final TooltipMakerAPI tooltip, final ShipAPI.HullSize hullSize, final ShipAPI ship, final float width, final boolean isForModSpec) {
		final Color green = new Color(55,245,65,255);
		final Color red = new Color(255,0,0,255);
		final float pad = 10f;
		final float padS = 0f;
		tooltip.addSectionHeading("Details", Alignment.MID, pad);
		tooltip.addPara("- Increased Armor Bonus: %s"
						+ "\n- Weapon Health Increase: %s", pad, green,
				new String[] {
						Misc.getRoundedValue(30.0f) + "%", // Armor Bonus
						Misc.getRoundedValue(100.0f) + "%", // Increased Flux Cap and Diss
				});
		tooltip.addPara("- Max Speed Decreased: %s/%s/%s"
						+ "\n- Flux Threshold While Phased Decreased By: %s",0.0F, red,
				new String[]{
						Misc.getRoundedValue(30.0F) + "",
						Misc.getRoundedValue(20.0F) + "",
						Misc.getRoundedValue(15.0F) + "",
						Misc.getRoundedValue(75.0F) + "%",});
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(HullMods.PHASE_ANCHOR)) return false;
		return ship.getHullSpec().isPhase();
	}

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(HullMods.PHASE_ANCHOR)) {
			return "Incompatible with Phase Anchor";
		}
		if (!ship.getHullSpec().isPhase()) {
			return "Can only be installed on phase ships";
		}
		return super.getUnapplicableReason(ship);
	}
}

