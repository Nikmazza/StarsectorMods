package data.scripts.xo.andradanism;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

public class DoctrinalPurity extends SCBaseSkillPlugin {
	
	//1.1 * 1.1363 = 1.2
	private static float FIGHTER_DAMAGE_BONUS = 13.63f;
	private static float ENERGY_RANGE_BONUS = 100f;
	
	@Override
    public String getAffectsString() {
        return "all carriers with a Fleet Override";
    }
    
	@Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
		tooltip.addPara("Discipline is life.", 0f, Misc.getTextColor(), Misc.getHighlightColor());
		tooltip.addPara("  -Quotations from the Supreme Executor", 0f, Misc.getTextColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("Built-in and Modular Fleet Override damage bonus increased to 15%%", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("Strike craft energy weapon range increased by 100 su", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("Gain access to the Modular Fleet Override hullmod", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
	}
    
	@Override
	public void applyEffectsToFighterSpawnedByShip(SCData data, ShipAPI fighter, ShipAPI ship, String id) {
		MutableShipStatsAPI stats = fighter.getMutableStats();
		ShipVariantAPI v = ship.getVariant();
		if (v.hasHullMod("vice_fleet_override") || v.hasHullMod("vice_modular_fleet_override")) {
			stats.getBallisticWeaponDamageMult().modifyMult(id, 1f + FIGHTER_DAMAGE_BONUS * 0.01f);
			stats.getEnergyWeaponDamageMult().modifyMult(id, 1f + FIGHTER_DAMAGE_BONUS * 0.01f);
			stats.getMissileWeaponDamageMult().modifyMult(id, 1f + FIGHTER_DAMAGE_BONUS * 0.01f);
			stats.getEnergyWeaponRangeBonus().modifyFlat(id, ENERGY_RANGE_BONUS);
		}
	}
		
	@Override
	public void onActivation(SCData data) {
		if (data.isPlayer()) {
			CharacterDataAPI player = Global.getSector().getCharacterData();
			player.addHullMod("vice_modular_fleet_override");
			Global.getSector().getMemoryWithoutUpdate().set("$xo_doctrinal_purity_is_active", true);
		}
	}
	
	@Override
	public void onDeactivation(SCData data) {
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_doctrinal_purity_is_active", false);
	}
}