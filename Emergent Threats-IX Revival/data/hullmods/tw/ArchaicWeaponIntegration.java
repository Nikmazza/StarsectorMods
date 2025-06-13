package data.hullmods.tw;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ArchaicWeaponIntegration extends BaseHullMod {

	private static float ARCHAIC_DP_DISCOUNT = 15f; //text only, accomplished by hotswapping in hullmod with negative dp
	private static float FLUX_PER_OP = 10f;
	
	private static String THIS_MOD = "tw_archaic_weapon_integration";
	private static String DISCOUNT_15 = "tw_equalizer_discount_15";
	private static String DISCOUNT_30 = "tw_equalizer_discount_30";
	private static String DISCOUNT_45 = "tw_equalizer_discount_45";
	private static String DISCOUNT_60 = "tw_equalizer_discount_60";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI var = stats.getVariant();
		int archaicCount = 0;
		float missileOP = 0f;
		WeaponSpecAPI spec001 = null;
		WeaponSpecAPI spec002 = null;
		WeaponSpecAPI spec003 = null;
		WeaponSpecAPI spec004 = null;
		WeaponType type001 = null;
		WeaponType type002 = null;
		WeaponType type003 = null;
		WeaponType type004 = null;
		
		if (var.getWeaponSpec("WS 001") != null) {
			spec001 = var.getWeaponSpec("WS 001");
			type001 = spec001.getType();
		}
		if (var.getWeaponSpec("WS 002") != null) {
			spec002 = var.getWeaponSpec("WS 002");
			type002 = spec002.getType();
		}
		if (var.getWeaponSpec("WS 003") != null) {
			spec003 = var.getWeaponSpec("WS 003");
			type003 = spec003.getType();
		}
		if (var.getWeaponSpec("WS 004") != null) {
			spec004 = var.getWeaponSpec("WS 004");
			type004 = spec004.getType();
		}
		
		MutableCharacterStatsAPI charStats = Global.getFactory().createPerson().getStats();
		
		if (spec001 != null && WeaponType.COMPOSITE.equals(type001) && spec001.hasTag("archaic_c")) archaicCount++;
		else if (spec001 != null) missileOP += spec001.getOrdnancePointCost(charStats, stats);
		if (spec002 != null && WeaponType.COMPOSITE.equals(type002) && spec002.hasTag("archaic_c")) archaicCount++;
		else if (spec002 != null) missileOP += spec002.getOrdnancePointCost(charStats, stats);
		if (spec003 != null && WeaponType.COMPOSITE.equals(type003) && spec003.hasTag("archaic_c")) archaicCount++;
		else if (spec003 != null) missileOP += spec003.getOrdnancePointCost(charStats, stats);
		if (spec004 != null && WeaponType.COMPOSITE.equals(type004) && spec004.hasTag("archaic_c")) archaicCount++;
		else if (spec004 != null) missileOP += spec004.getOrdnancePointCost(charStats, stats);
		
		float flux = FLUX_PER_OP * missileOP;
		stats.getFluxDissipation().modifyFlat(id, -flux);
		
		if (archaicCount == 1) {
			var.removeMod(DISCOUNT_30);
			var.removeMod(DISCOUNT_45);
			var.removeMod(DISCOUNT_60);
			var.addMod(DISCOUNT_15);
		}
		else if (archaicCount == 2) {
			var.removeMod(DISCOUNT_15);
			var.removeMod(DISCOUNT_45);
			var.removeMod(DISCOUNT_60);
			var.addMod(DISCOUNT_30);
		}
		else if (archaicCount == 3) {
			var.removeMod(DISCOUNT_15);
			var.removeMod(DISCOUNT_30);
			var.removeMod(DISCOUNT_60);
			var.addMod(DISCOUNT_45);
		}
		else if (archaicCount == 4) {
			var.removeMod(DISCOUNT_15);
			var.removeMod(DISCOUNT_30);
			var.removeMod(DISCOUNT_45);
			var.addMod(DISCOUNT_60);
		}
		else {
			var.removeMod(DISCOUNT_15);
			var.removeMod(DISCOUNT_30);
			var.removeMod(DISCOUNT_45);
			var.removeMod(DISCOUNT_60);
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "archaic";
		if (index == 1) return "" + (int) ARCHAIC_DP_DISCOUNT + "";
		if (index == 2) return "" + (int) FLUX_PER_OP + "";
		return null;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		if (ship == null) return;		
		String discount = "inactive";
		if (ship.getVariant().hasHullMod(DISCOUNT_15)) discount = "15";
		else if (ship.getVariant().hasHullMod(DISCOUNT_30)) discount = "30";
		else if (ship.getVariant().hasHullMod(DISCOUNT_45)) discount = "45";
		else if (ship.getVariant().hasHullMod(DISCOUNT_60)) discount = "60";
		
		MutableShipStatsAPI stats = ship.getMutableStats();
		float flux = stats.getFluxDissipation().getFlatStatMod(THIS_MOD) == null 
			? 0f : stats.getFluxDissipation().getFlatStatMod(THIS_MOD).getValue();
			
		String missileFluxPenalty = flux != 0f ? "" + (int) flux : "inactive";
		
		String s1 = "Total OP discount is %s";
		String s2 = "Total flux penalty is %s";
		tooltip.addPara(s1, 10f, Misc.getHighlightColor(), discount);
		tooltip.addPara(s2, 10f, Misc.getHighlightColor(), missileFluxPenalty);
	}
}





