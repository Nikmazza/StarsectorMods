package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.util.HashSet;
import java.util.Set;

public class GMDA_RepairGantry_Base extends BaseHullMod {


    public static final float REPAIR_RATE_BONUS_SML = 5f;
	public static final float REPAIR_RATE_BONUS_MED = 10f;
	public static final float REPAIR_RATE_BONUS_LRG = 25f;


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) (REPAIR_RATE_BONUS_SML * 100f)+ "%"; // + Strings.X;
        if (index == 1) return "" + (int) (REPAIR_RATE_BONUS_MED * 100f)+ "%"; // + Strings.X;
        if (index == 2) return "" + (int) (REPAIR_RATE_BONUS_LRG * 100f)+ "%"; // + Strings.X;
        if (index == 3) return "" + (int) (REPAIR_RATE_BONUS_LRG * 100f)+ "%"; // + Strings.X;
		return null;
	}

}