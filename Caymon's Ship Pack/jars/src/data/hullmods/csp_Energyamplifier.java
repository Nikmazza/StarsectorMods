package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.AIHints;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class csp_Energyamplifier extends BaseHullMod {

	public static float BONUS_MAX_1 = 800;
	public static float BONUS_MAX_2 = 800;
	public static float BONUS_MAX_3 = 900;
	public static float BONUS_SMALL_1 = 100;
	public static float BONUS_SMALL_2 = 100;
	public static float BONUS_SMALL_3 = 200;
	public static float BONUS_MEDIUM_3 = 100;
	
	public static float HYBRID_MULT = 2f;
	public static float HYBRID_BONUS_MIN = 100f;
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		WeaponSize largest = null;
		for (WeaponSlotAPI slot : ship.getHullSpec().getAllWeaponSlotsCopy()) {
			if (slot.isDecorative() ) continue;
			if (slot.getWeaponType() == WeaponType.ENERGY) {
				if (largest == null || largest.ordinal() < slot.getSlotSize().ordinal()) {
					largest = slot.getSlotSize();
				}
			}
		}
		if (largest == null) return;
		float small = 0f;
		float medium = 0f;
		float max = 0f;
		switch (largest) {
		case LARGE:
			small = BONUS_SMALL_3;
			medium = BONUS_MEDIUM_3;
			max = BONUS_MAX_3;
			break;
		case MEDIUM:
			small = BONUS_SMALL_2;
			max = BONUS_MAX_2;
			break;
		case SMALL:
			small = BONUS_SMALL_1;
			max = BONUS_MAX_1;
			break;
		}
		
		ship.addListener(new RangefinderRangeModifier(small, medium, max));
	}

	public static class RangefinderRangeModifier implements WeaponBaseRangeModifier {
		public float small, medium, max;
		public RangefinderRangeModifier(float small, float medium, float max) {
			this.small = small;
			this.medium = medium;
			this.max = max;
		}

		public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}
		public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}
		public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			if (weapon.getSlot() == null || weapon.getSlot().getWeaponType() != WeaponType.ENERGY) {
				return 0f;
			}
			if (!weapon.hasAIHint(AIHints.PD)) {
				return 0f;
			}
			return 300f;
		}
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return getUnapplicableReason(ship) == null;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship != null && 
				ship.getHullSize() != HullSize.CAPITAL_SHIP && 
				ship.getHullSize() != HullSize.DESTROYER && 
				ship.getHullSize() != HullSize.CRUISER) {
			return "Can only be installed on destroyer-class hulls and larger";
		}
		return null;
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
		tooltip.addPara("- Increases the base range of all Energy Point defense weaponry by %s ", pad, green, new String[] { "300" });
		// %s means it has to call the value from 'newString[] { "have sex" };' and if you want to call a string with numbers then it should be-
		// Misc.getRoundedValue(69.0f) + "%" or if it is just a flat value just make it Misc.getRoundedValue(69.0f) + "" without the percent sign within the -> ""
	}

}









