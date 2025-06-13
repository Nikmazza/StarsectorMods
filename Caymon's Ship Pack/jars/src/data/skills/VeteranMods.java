package data.skills;

import com.fs.starfarer.api.characters.CharacterStatsSkillEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

public class VeteranMods extends SCBaseSkillPlugin {
	
	public static float MANEUVERABILITY_BONUS = 20;
	public static float SPEED_BONUS = 10f;
	public static float SHIELD_DAMAGE_REDUCTION = 10f;
	
	//public static float DAMAGE_BONUS = 100f;

	public String getAffectsString() {
		return "all ships";
	}

	public void addTooltip(SCData scData, TooltipMakerAPI tooltipMakerAPI) {
		tooltipMakerAPI.addPara("+10%% top speed", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltipMakerAPI.addPara("-10%% damage taken by shields", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltipMakerAPI.addPara("+20%% maneuverability", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
	}


	public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id)
		{
			stats.getAcceleration().modifyPercent(id, MANEUVERABILITY_BONUS);
			stats.getDeceleration().modifyPercent(id, MANEUVERABILITY_BONUS);
			stats.getTurnAcceleration().modifyPercent(id, MANEUVERABILITY_BONUS * 2f);
			stats.getMaxTurnRate().modifyPercent(id, MANEUVERABILITY_BONUS);
			stats.getMaxSpeed().modifyPercent(id, SPEED_BONUS);
			stats.getShieldDamageTakenMult().modifyMult(id, 1f - SHIELD_DAMAGE_REDUCTION / 100f);
			
//			stats.getBallisticWeaponDamageMult().modifyPercent(id, DAMAGE_BONUS);
//			stats.getEnergyWeaponDamageMult().modifyPercent(id, DAMAGE_BONUS);
//			stats.getMissileWeaponDamageMult().modifyPercent(id, DAMAGE_BONUS);
		}
		
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			
//			stats.getBallisticWeaponDamageMult().unmodify(id);
//			stats.getEnergyWeaponDamageMult().unmodify(id);
//			stats.getMissileWeaponDamageMult().unmodify(id);
		}

	}

