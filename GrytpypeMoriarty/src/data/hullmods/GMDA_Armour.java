package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.util.HashSet;
import java.util.Set;

public class GMDA_Armour extends BaseHullMod {


    public static final float ARMOR_STRENGTH_MULT = 0.5f;
    public static final float ARMOR_MAX_REDUCTION_PENALTY = 0.15f;


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getEffectiveArmorBonus().modifyMult(id, ARMOR_STRENGTH_MULT);
        stats.getMaxArmorDamageReduction().modifyFlat(id, -ARMOR_MAX_REDUCTION_PENALTY);
	}


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((1f - ARMOR_STRENGTH_MULT) * 100f)+ "%"; // + Strings.X;
        if (index == 1) return "" + (int) ((ARMOR_MAX_REDUCTION_PENALTY) * 100f)+ "%"; // + Strings.X;
		return null;
	}

}