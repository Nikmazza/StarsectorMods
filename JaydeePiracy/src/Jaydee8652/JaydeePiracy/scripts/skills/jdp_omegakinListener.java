package Jaydee8652.JaydeePiracy.scripts.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;
import java.util.Map;

import static Jaydee8652.JaydeePiracy.scripts.skills.jdp_omegakin.DAMAGE_BONUS_PERCENT;

class jdp_omegakinListener implements DamageDealtModifier {

	public String modifyDamageDealt(Object param,
									CombatEntityAPI target, DamageAPI damage,
									Vector2f point, boolean shieldHit) {
		WeaponAPI weapon = null;
		if (param instanceof DamagingProjectileAPI) {
			weapon = ((DamagingProjectileAPI) param).getWeapon();
		} else if (param instanceof BeamAPI) {
			weapon = ((BeamAPI) param).getWeapon();
		} else if (param instanceof MissileAPI) {
			weapon = ((MissileAPI) param).getWeapon();
		}

		if (weapon == null) return null;
		if (!weapon.getSpec().hasTag("omega")) return null;

		String id = "jdp_omegakin_damagemod";
		damage.getModifier().modifyPercent(id, DAMAGE_BONUS_PERCENT);

		return id;
	}
}