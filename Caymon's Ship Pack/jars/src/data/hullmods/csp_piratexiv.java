package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class csp_piratexiv extends BaseHullMod {

	/* A Ship of the 14th Domain Battlegroup
	 * well-maintained survivor of the original battlegroup which founded the Hegemony
	 * Sterling example of the Domain Navy's traditional "decisive battle" doctrine
	 * focus on superior armour and firepower on ships of the line to destroy the enemy
	 * - slightly better flux handling
	 * - slightly better armour
	 * - slightly worse speed/maneuver
	 * -
	 */

	//private static final float ARMOR_BONUS_MULT = 1.1f;
	private static final float ARMOR_BONUS = 100f;
	private static final float HANDLING_MULT = 0.85f;
	public static final float ARMOR_DAMAGE_MULT = 95f;

	/*private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0.0f);
		mag.put(HullSize.FRIGATE, 0.25f);
		mag.put(HullSize.DESTROYER, 0.15f);
		mag.put(HullSize.CRUISER, 0.10f);
		mag.put(HullSize.CAPITAL_SHIP, 0.05f);
	}*/

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		// just generally better armour - and structure!
		//stats.getArmorBonus().modifyMult(id, (Float) mag.get(hullSize) + 1.00f); // * ARMOR_BONUS_MULT);
		//stats.getHullBonus().modifyPercent(id, (Float) mag.get(hullSize) * 0.5f); // some hull.
		stats.getArmorBonus().modifyFlat(id, (Float) ARMOR_BONUS);

		// 5% better flux stats

		// 8% worse handling
		stats.getAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getDeceleration().modifyMult(id, HANDLING_MULT);
		stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
		stats. getTurnAcceleration().modifyMult(id, HANDLING_MULT);

		stats.getArmorDamageTakenMult().modifyMult(id, (ARMOR_DAMAGE_MULT / 100));
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}

	public void addPostDescriptionSection(final TooltipMakerAPI tooltip, final ShipAPI.HullSize hullSize, final ShipAPI ship, final float width, final boolean isForModSpec) {
		final Color green = new Color(55,245,65,255);
		final Color red = new Color(255,0,0,255);
		final float pad = 10f;
		final float padS = 0f;
		tooltip.addSectionHeading("Details", Alignment.MID, pad);
		tooltip.addPara("- Increased Armor Bonus: %s"
						+ "\n- Reduced Armor Damage: %s", pad, green,
				new String[] {
						Misc.getRoundedValue(100.0f) + "", // Armor Bonus
						Misc.getRoundedValue(5.0f) + "%", // Reduced Armor Damage
				});
		tooltip.addPara("- Maneuverability Decreased: %s",0.0F, red,
				new String[]{
						Misc.getRoundedValue(15.0F) + "%",
});
	}

}

	/*public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue();
		if (index == 1) return "" + ((Float) mag.get(Hullize.DESTROYER)).intValue();
		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue();
		if (index == 3) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue();
		if (index == 4) return "" + (int) ACCELERATION_BONUS;
		//if (index == 5) return "four times";
		if (index == 5) return "4" + Strings.X;
		if (index == 6) return "" + BURN_LEVEL_BONUS;
		return null;
	}*/

