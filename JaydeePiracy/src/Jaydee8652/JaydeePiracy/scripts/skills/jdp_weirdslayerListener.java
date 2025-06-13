package Jaydee8652.JaydeePiracy.scripts.skills;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import org.lwjgl.util.vector.Vector2f;

import static Jaydee8652.JaydeePiracy.scripts.skills.jdp_weirdslayer.DAMAGE_BONUS_PERCENT;

//Made for NEON! Go download NSP!
class jdp_weirdslayerListener implements DamageDealtModifier {

	public String modifyDamageDealt(Object param,
									CombatEntityAPI target, DamageAPI damage,
									Vector2f point, boolean shieldHit) {

		ShipAPI enemy = null;

		if (target instanceof ShipAPI) {
			enemy = (ShipAPI) target;
		}

		if (enemy == null || enemy.getVariant() == null) return null;
		if (enemy.getVariant() == null) return null;
		if (enemy.getVariant().hasHullMod("dweller_hullmod")){
			String id = "jdp_weirdslayer_damagemod";
			damage.getModifier().modifyPercent(id, 25f); //
			return id;
		}
		else return null;
	}
}