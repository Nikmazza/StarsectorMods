package data.hullmods.vice;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class SeekerLens extends BaseHullMod {

	private static float ENERGY_BOLT_SPEED_BONUS = 100f;
	private static float ENERGY_WEAPON_ROF_BONUS = 25f;
	private static float ENERGY_WEAPON_FLUX_REDUCTION  = 25f;
	private static float SEEKER_FLAT_RANGE_BONUS = 800f;
	private static String SEEKER_TAG = "vice_seeker";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		WeaponSpecAPI spec = stats.getVariant().getWeaponSpec("WS 001");
		if (spec != null && spec.hasTag(SEEKER_TAG)) {
			stats.getEnergyRoFMult().modifyMult(id, 1f + ENERGY_WEAPON_ROF_BONUS * 0.01f);
			//stats.getEnergyProjectileSpeedMult().modifyMult(id, 1f + ENERGY_BOLT_SPEED_BONUS * 0.01f);
			stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - ENERGY_WEAPON_FLUX_REDUCTION * 0.01f);
		}
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.addListener(new StonefishSeekerBonuses(ship));
	}
	
	public static class StonefishSeekerBonuses implements WeaponBaseRangeModifier{
		protected ShipAPI ship;
		public StonefishSeekerBonuses(ShipAPI ship) {
			this.ship = ship;
		}
		public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}
		public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			return 1f;
		}
		public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			if (weapon.getSpec() == null) return 0f;
			if (weapon.getSpec().hasTag(SEEKER_TAG)) return SEEKER_FLAT_RANGE_BONUS;
			return 0f;
		}
	}
	
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		boolean isSeeker = false;
		WeaponSpecAPI spec = ship.getVariant().getWeaponSpec("WS 001");
		if (spec != null && spec.hasTag(SEEKER_TAG)) isSeeker = true;
		String s1 = "Seeker Lens rate of fire bonus is currently %s.";
		String s2 = isSeeker ? "active" : "inactive";
		tooltip.addPara(s1, 10f, Misc.getHighlightColor(), s2);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "seeker";
		if (index == 1) return "" + (int) SEEKER_FLAT_RANGE_BONUS;
		if (index == 2) return "" + (int) ENERGY_WEAPON_ROF_BONUS + "%";
		return null;
	}
}