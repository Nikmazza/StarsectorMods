package data.hullmods;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class HMI_MiningHSA extends BaseHullMod {

	public static float GENERAL_DAMAGE_BONUS_PERCENT = 15f;
	//public static float MINING_DAMAGE_BONUS_PERCENT = 1.5f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean sMod = isSMod(stats);
		if (sMod) {
		stats.getBeamWeaponDamageMult().modifyPercent(id, GENERAL_DAMAGE_BONUS_PERCENT);
		}
	}

	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.addListener(new HMI_MiningDamageDealtMod(ship));
	}

	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		fighter.addListener(new HMI_MiningDamageDealtMod(fighter));
	}


	public static class HMI_MiningDamageDealtMod implements DamageDealtModifier {
		protected ShipAPI ship;

		public HMI_MiningDamageDealtMod(ShipAPI ship) {
			this.ship = ship;
		}

		public String modifyDamageDealt(Object param,
										CombatEntityAPI target, DamageAPI damage,
										Vector2f point, boolean shieldHit) {

			if ((param instanceof BeamAPI)) {
				WeaponAPI weapon = ((BeamAPI) param).getWeapon();
				if (weapon == null)
					return null;

				if (weapon.getId().equals("mininglaser")) {
					damage.setForceHardFlux(true);
					//damage.getModifier().modifyMult("mininglaser", MINING_DAMAGE_BONUS_PERCENT);
					return "mininglaser";
				}

				if (weapon.getId().equals("burst_ml")) {
					damage.setForceHardFlux(true);
					//damage.getModifier().modifyMult("burst_ml", MINING_DAMAGE_BONUS_PERCENT);
					return "burst_ml";
				}

				if (weapon.getId().equals("hmi_mininglaser_array")) {
					damage.setForceHardFlux(true);
					//damage.getModifier().modifyMult("hmi_mininglaser_array", MINING_DAMAGE_BONUS_PERCENT);
					return "hmi_mininglaser_array";
				}
			}

			//For unknown reasons, the code below stopped working from 0.97->0.98
			//for(WeaponAPI w:ship.getAllWeapons())
				////if (w.getSpec().getMountType() == WeaponAPI.WeaponType.HYBRID) {
				//if (w.getSpec().getType() == WeaponAPI.WeaponType.HYBRID) {
					//if (!(param instanceof DamagingProjectileAPI) && param instanceof BeamAPI) {
					//w.getDamage().setForceHardFlux(true);
					//}

			return null;
			}
	}


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Mining Lasers, Chimobuko Mining Lasers and Mining Laser Arrays";
		if (index == 1) return "hard flux";
		if (index == 2) return "fighters launched by the ship";
		return null;
	}
	
	public String getSModDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) GENERAL_DAMAGE_BONUS_PERCENT + "%";
		return null;
	}
}


