package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;


public class testsystem extends BaseShipSystemScript {

	public static final float DAMAGE_BONUS_PERCENT = 25f;

	public static String RD_NO_EXTRA_CRAFT = "rd_no_extra_craft";
	public static String RD_FORCE_EXTRA_CRAFT = "rd_force_extra_craft";

	public static float EXTRA_FIGHTER_DURATION = 25;
        public boolean doOnce = false;

	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return;
		}

		float bonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
		stats.getEnergyWeaponDamageMult().modifyPercent(id, bonusPercent);
                if (state == ShipSystemStatsScript.State.OUT) {
                    stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
                    stats.getMaxTurnRate().unmodify(id);
		} else {
                    stats.getMaxSpeed().modifyFlat(id, 50f);
                    stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
                    stats.getDeceleration().modifyPercent(id, 200f * effectLevel);
                    stats.getTurnAcceleration().modifyFlat(id, 30f * effectLevel);
                    stats.getTurnAcceleration().modifyPercent(id, 200f * effectLevel);
                    stats.getMaxTurnRate().modifyFlat(id, 15f);
                    stats.getMaxTurnRate().modifyPercent(id, 100f);
                }

		if (effectLevel == 1 && !doOnce) {
                        doOnce = true;
			//need to make this more balanced
			//possibly don't count the "added" fighters to helping restore the replacement rate?
			//also: need to adjust the AI to be more conservative using this

			for (FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()) {
				if (bay.getWing() == null) continue;
				FighterWingSpecAPI spec = bay.getWing().getSpec();

				int addForWing = getAdditionalFor(spec);
				int maxTotal = spec.getNumFighters() + addForWing;
				int actualAdd = maxTotal - bay.getWing().getWingMembers().size();
				//int actualAdd = addForWing;
				//actualAdd = Math.min(spec.getNumFighters(), actualAdd);
				if (actualAdd > 0) {
					bay.setFastReplacements(bay.getFastReplacements() + addForWing);
					bay.setExtraDeployments(actualAdd);
					bay.setExtraDeploymentLimit(maxTotal);
					bay.setExtraDuration(EXTRA_FIGHTER_DURATION);
					//bay.setExtraDuration(99999999999f);
				}
			}
		}
	}

	public static int getAdditionalFor(FighterWingSpecAPI spec) {
		//if (spec.isBomber() && !spec.hasTag(RD_FORCE_EXTRA_CRAFT)) return 0;
		if (spec.hasTag(RD_NO_EXTRA_CRAFT)) return 0;

		int size = spec.getNumFighters();
//		if (size <= 3) return 1;
//		return 2;
		if (size <= 3) return 1;
		if (size <= 5) return 2;
		return 3;
	}


	public void unapply(MutableShipStatsAPI stats, String id) {
                doOnce = false;
		stats.getEnergyWeaponDamageMult().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
	}



	public StatusData getStatusData(int index, State state, float effectLevel) {
		float bonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
		if (index == 0) {
			return new StatusData("+" + (int) bonusPercent + "% energy weapon damage" , false);
		}
		if (index == 1) {
			return new StatusData("improved maneuverability", false);
		} else if (index == 2) {
			return new StatusData("+50 top speed", false);
		}
//		if (index == 0) {
//			return new StatusData("deploying additional fighters", false);
//		}
		return null;
	}


	@Override
	public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
		return true;
	}
}