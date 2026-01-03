package data.scripts.xo.synthesis;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

public class DroneTactics extends SCBaseSkillPlugin {
	
	private static String ADB_MOD_ID = "vice_adaptive_drone_bay";
	private static String AFC_MOD_ID = "vice_adaptive_flight_command";
	private static float DAM_BONUS = 10f;
	private static float AFC_BONUS = 20f;
	private static float DRONE_REPLACEMENT_BONUS = 25f;

	@Override
    public String getAffectsString() {
        return "all drone fighters";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
        tooltip.addPara("+10%% damage dealt, or +20%% when Adaptive Flight Command is enabled", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addPara("25%% reduction to drone replacement time when Adaptive Drone Bay is enabled", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("AI Subsystem Integration converts equipped strike craft to drones. The Abomination Interface, Autonomous Bays (RAT), and Drone Conversion (SEEKER) hullmods also grants all drone bonuses to equipped strike craft", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("Acquire the Drone Bay and Flight Command adaptive hullmods", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Drone Bay", "Flight Command");
    }
	
	//technically the refit time bonus applies to all fighters and not just drones, but can't be bothered to fix
    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
		if (stats.getVariant().hasHullMod(ADB_MOD_ID)) {
			stats.getFighterRefitTimeMult().modifyMult(id, 1f - DRONE_REPLACEMENT_BONUS * 0.01f);
		}
    }

	@Override
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		float bonus = ship.getVariant().hasHullMod(AFC_MOD_ID) ? AFC_BONUS : DAM_BONUS;		
		boolean isAlwaysValid = isValid(ship.getVariant());
		if (isAlwaysValid || fighter.getHullSpec().getMinCrew() == 0) {
			MutableShipStatsAPI stats = fighter.getMutableStats();
			stats.getBallisticWeaponDamageMult().modifyPercent(id, bonus);
			stats.getEnergyWeaponDamageMult().modifyPercent(id, bonus);
			stats.getMissileWeaponDamageMult().modifyPercent(id, bonus);
		}
	}
	
	private boolean isValid(ShipVariantAPI variant) {
		boolean isAlwaysValid = variant.hasHullMod("vice_abomination_interface") 
							|| variant.hasHullMod("SKR_remote") 
							|| variant.hasHullMod("rat_autonomous_bays");
		for (String mod : variant.getSMods()) {
			if (mod.equals("vice_ai_subsystem_integration")) isAlwaysValid = true;
		}
		return isAlwaysValid;
	}
	
	@Override
	public void onActivation(SCData data) {
		if (data.isPlayer() && !Global.getSector().getMemoryWithoutUpdate().is("$gave_DT_hullmods", true)) {
			CharacterDataAPI player = Global.getSector().getCharacterData();
			player.addHullMod("vice_adaptive_drone_bay");
			player.addHullMod("vice_adaptive_flight_command");
			Global.getSector().getMemoryWithoutUpdate().set("$gave_DT_hullmods", true);
		}
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_drone_tactics_is_active", true);
	}
	
	@Override
	public void onDeactivation(SCData data) {
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_drone_tactics_is_active", false);
	}
}
