package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

@SuppressWarnings("unchecked")
public class BrightonCommissionedCrews extends BaseHullMod {

	public static float AIM_BONUS = 20f;
	public static float HIDE_BONUS = 10f;
	private final String CREW="CHM_commission";

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getAutofireAimAccuracy().modifyMult(id, 1f + (AIM_BONUS * 0.01f));
		stats.getWeaponTurnRateBonus().modifyMult(id, 1f + (AIM_BONUS * 0.01f));
		stats.getSensorProfile().modifyMult(id, 1f - (HIDE_BONUS * 0.01f));
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id){
		if (ship.getVariant().getHullMods().contains(CREW)) {
			ship.getVariant().removeMod(CREW);
		}
	}
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) HIDE_BONUS + "%";
		if (index == 1) return "" + (int) AIM_BONUS + "%";
		return null;
	}
}
