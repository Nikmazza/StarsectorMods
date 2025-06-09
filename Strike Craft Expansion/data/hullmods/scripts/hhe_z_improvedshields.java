package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class hhe_z_improvedshields extends BaseHullMod {

	public static final float HHE_SHIELD_BONUS_TURN = 66f;
	public static final float HHE_SHIELD_BONUS_UNFOLD = 66f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getShieldTurnRateMult().modifyPercent(id, HHE_SHIELD_BONUS_TURN);
		stats.getShieldUnfoldRateMult().modifyPercent(id, HHE_SHIELD_BONUS_UNFOLD);
	}
}
