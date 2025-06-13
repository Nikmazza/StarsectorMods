package data.hullmods;

import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.AIHints;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class rwc_rangefinder extends BaseHullMod {


	public static float BONUS_MAX_3 = 900;

	public static float BONUS_SMALL_3 = 200;
	public static float BONUS_MEDIUM_3 = 200;
	
	public static float HYBRID_MULT = 1f;
	public static float HYBRID_BONUS_MIN = 200f;
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
	}

	
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		
			float small = BONUS_SMALL_3;
			float medium = BONUS_MEDIUM_3;
			float max = BONUS_MAX_3;
			

		
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
//			if (weapon.getSlot() == null || weapon.getSlot().getWeaponType() != WeaponType.BALLISTIC) {
//				return 0f;
//			}
			if (weapon.getSpec() == null) {
				return 0f;
			}
			if (weapon.getSpec().getMountType() != WeaponType.BALLISTIC && 
					weapon.getSpec().getMountType() != WeaponType.HYBRID) {
				return 0f;
			}
			if (weapon.hasAIHint(AIHints.PD)) {
				return 0f;
			}
			
			float bonus = 0;
			if (weapon.getSize() == WeaponSize.SMALL) {
				bonus = small;
			} else if (weapon.getSize() == WeaponSize.MEDIUM) {
				bonus = medium;
			}
			if (weapon.getSpec().getMountType() == WeaponType.HYBRID) {
				bonus *= HYBRID_MULT;
				if (bonus < HYBRID_BONUS_MIN) {
					bonus = HYBRID_BONUS_MIN;
				}
			}
			if (bonus == 0f) return 0f;
			
			float base = weapon.getSpec().getMaxRange();
			if (base + bonus > max) {
				bonus = max - base;
			}
			if (bonus < 0) bonus = 0;
			return bonus;
		}
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		//if (index == 0) return "" + (int)RANGE_PENALTY_PERCENT + "%";
		return null;
	}
	
	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {

		float opad = 10f;
		Color h = Misc.getHighlightColor();
		
		
		
		

		tooltip.addPara("Utilizes targeting data from the ship's built-in heavy weapons "
				+ "to benefit certain weapons, extending the base range of "
				+ "typical ballistic weapons by 200 units. "
				+ "Also benefits hybrid weapons. Point-defense weapons are unaffected.",
				opad, h,  "base range", "200 units");
		
		
		tooltip.addSectionHeading("Interactions with other modifiers", Alignment.MID, opad + 7f);
		tooltip.addPara("Since the base range is increased, this modifier"
				+ " - unlike most other flat modifiers - "
				+ "is increased by percentage modifiers from other hullmods and skills.", opad);
	}
	
	public float getTooltipWidth() {
		return 412f;
	}
	
	
	
}









