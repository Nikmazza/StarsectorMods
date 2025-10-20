package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class jdp_subcore extends BaseHullMod {

	public static float VISION_BONUS = 1000f;
	public static float AUTOFIRE_AIM = 0.5f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getSightRadiusMod().modifyFlat(id, VISION_BONUS);
		stats.getAutofireAimAccuracy().modifyFlat(id, AUTOFIRE_AIM);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return Math.round(AUTOFIRE_AIM) + " standard unit";
		if (index == 1) return "" + (int)Math.round(VISION_BONUS);
        return null;
    }
}
