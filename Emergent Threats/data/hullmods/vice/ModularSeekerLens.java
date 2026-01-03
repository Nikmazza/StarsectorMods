package data.hullmods.vice;

import java.util.List;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ModularSeekerLens extends BaseHullMod {

	private static float ENERGY_WEAPON_ROF_BONUS = 10f;
	private static float ENERGY_WEAPON_FLUX_REDUCTION  = 10f;
	private static float SEEKER_FLAT_RANGE_BONUS = 300f;
	private static String SEEKER_TAG = "vice_seeker";
	private static String SEEKER = "seeker";
	private static String CONFLICT_MOD = "vice_seeker_lens";
	private static String CONFLICT_MOD_IX = "ix_compact_seeker_lens";
	private static String CONFLICT_MOD_TW = "tw_seeker_lens";
	private static boolean showText = false;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean apply = false;
		List<String> slots = stats.getVariant().getNonBuiltInWeaponSlots();
		for (String slot : slots) {
			WeaponSpecAPI spec = stats.getVariant().getWeaponSpec(slot);
			if (isSMod(stats) && spec != null 
					&& spec.hasTag(SEEKER_TAG) && spec.getSize() == WeaponSize.LARGE) apply = true;
		}
		if (apply) {
			stats.getEnergyRoFMult().modifyMult(id, 1f + ENERGY_WEAPON_ROF_BONUS * 0.01f);
			stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - ENERGY_WEAPON_FLUX_REDUCTION * 0.01f);
			showText = true;
		}
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.addListener(new ModularSeekerLensBonuses(ship));
	}
	
	public static class ModularSeekerLensBonuses implements WeaponBaseRangeModifier{
		protected ShipAPI ship;
		public ModularSeekerLensBonuses(ShipAPI ship) {
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
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return true;
	}
	
	@Override
	public void addSModEffectSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec, boolean isForBuildInList) {
		String s = "" + (int) ENERGY_WEAPON_ROF_BONUS + "%";
		tooltip.addPara("Improves the fire rate of all energy weapons by %s without increasing flux cost when at least one large %s weapon is equipped.", 10f, Misc.getHighlightColor(), s, SEEKER);
		
		if (ship == null || !isSMod(ship)) return;
		String s1 = "Seeker Lens rate of fire bonus is currently %s.";
		String s2 = showText ? "active" : "inactive";
		tooltip.addPara(s1, 10f, Misc.getHighlightColor(), s2);
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(CONFLICT_MOD)) return false;
		else if (ship.getVariant().hasHullMod(CONFLICT_MOD_IX)) return false;
		else if (ship.getVariant().hasHullMod(CONFLICT_MOD_TW)) return false;
		return true;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().hasHullMod(CONFLICT_MOD)) return "Incompatible with built-in Seeker Lens";
		if (ship.getVariant().hasHullMod(CONFLICT_MOD_TW)) return "Incompatible with built-in Seeker Lens";
		if (ship.getVariant().hasHullMod(CONFLICT_MOD_IX)) return "Incompatible with Compact Seeker Lens";
		return null;
	}
	
	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return SEEKER;
		if (index == 1) return "" + (int) SEEKER_FLAT_RANGE_BONUS;
		return null;
	}
}