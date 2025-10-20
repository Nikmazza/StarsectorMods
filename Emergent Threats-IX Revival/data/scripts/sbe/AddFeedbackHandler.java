package data.scripts.sbe;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class AddFeedbackHandler implements EveryFrameWeaponEffectPlugin {
	
	private boolean runOnce = false;
	
	@Override
	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (runOnce) return;
		if (Global.getSettings().getModManager().isModEnabled("EmergentThreats_Vice")) runOnce = true;
		else if (!weapon.getShip().getVariant().hasHullMod("ix_feedback_handler")) {
			weapon.getShip().getVariant().getHullMods().add("ix_feedback_handler");
		}
		runOnce = true;
	}
}