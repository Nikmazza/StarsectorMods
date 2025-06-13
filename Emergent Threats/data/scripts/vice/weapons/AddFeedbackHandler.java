package data.scripts.vice.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

public class AddFeedbackHandler implements EveryFrameWeaponEffectPlugin {
	
	private boolean runOnce = false;
	
	@Override
	public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (runOnce) return;
		else if (!weapon.getShip().getVariant().hasHullMod("vice_feedback_handler")) {
			weapon.getShip().getVariant().getHullMods().add("vice_feedback_handler");
		}
		runOnce = true;
	}
}