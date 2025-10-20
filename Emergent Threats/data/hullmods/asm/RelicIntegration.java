package data.hullmods.asm;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class RelicIntegration extends BaseHullMod {

	public static float COST_REDUCTION  = 10;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, -COST_REDUCTION);

		boolean isPlayerOwned = false;
		if (stats.getFleetMember() != null && Global.getSector().getPlayerFleet() != null) {
			List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
			for (FleetMemberAPI member : fleetList) {
				if (member.getVariant().getHullVariantId() == stats.getVariant().getHullVariantId()) {
					isPlayerOwned = true;
				}
			}
		}
		
		if (!isPlayerOwned) return;

		boolean has18 = false;
		boolean has19 = false;
		boolean has20 = false;
		int hasCount = 0;
		if (stats.getVariant().getWeaponSpec("WS 018") != null 
			&& stats.getVariant().getWeaponSpec("WS 018").getType().equals(WeaponType.COMPOSITE)
			&& stats.getVariant().getWeaponSpec("WS 018").hasTag("archaic_c")) has18 = true;
		if (stats.getVariant().getWeaponSpec("WS 019") != null 
			&& stats.getVariant().getWeaponSpec("WS 019").getType().equals(WeaponType.COMPOSITE)
			&& stats.getVariant().getWeaponSpec("WS 019").hasTag("archaic_c")) has19 = true;
		if (stats.getVariant().getWeaponSpec("WS 020") != null 
			&& stats.getVariant().getWeaponSpec("WS 020").getType().equals(WeaponType.COMPOSITE)
			&& stats.getVariant().getWeaponSpec("WS 020").hasTag("archaic_c")) has20 = true;
		if (has18) hasCount++;
		if (has19) hasCount++;
		if (has20) hasCount++;
		if (hasCount == 1) {
			stats.getVariant().getHullMods().remove("asm_relic_discount_20");
			stats.getVariant().getHullMods().remove("asm_relic_discount_30");
			stats.getVariant().getHullMods().add("asm_relic_discount_10");
		}
		else if (hasCount == 2) {
			stats.getVariant().getHullMods().remove("asm_relic_discount_10");
			stats.getVariant().getHullMods().remove("asm_relic_discount_30");
			stats.getVariant().getHullMods().add("asm_relic_discount_20");
		}
		else if (hasCount == 3) {
			stats.getVariant().getHullMods().remove("asm_relic_discount_10");
			stats.getVariant().getHullMods().remove("asm_relic_discount_20");
			stats.getVariant().getHullMods().add("asm_relic_discount_30");
		}
		else {
			stats.getVariant().getHullMods().remove("asm_relic_discount_10");
			stats.getVariant().getHullMods().remove("asm_relic_discount_20");
			stats.getVariant().getHullMods().remove("asm_relic_discount_30");
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "ballistic";
		if (index == 1) return "archaic";
		if (index == 2) return "" + (int) COST_REDUCTION + "";
		return null;
	}

	@Override
	public boolean affectsOPCosts() {
		return true;
	}
	
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		if (ship == null) return;		
		String discount = "inactive";
		if (ship.getVariant().hasHullMod("asm_relic_discount_10")) discount = "10";
		else if (ship.getVariant().hasHullMod("asm_relic_discount_20")) discount = "20";
		else if (ship.getVariant().hasHullMod("asm_relic_discount_30")) discount = "30";
		tooltip.addPara("Archaic weapon OP discount is currently %s", 10f, Misc.getHighlightColor(), discount);
	}
}