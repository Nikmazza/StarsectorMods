package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.util.HashSet;
import java.util.Set;

public class GMDA_Ambulance_Base extends BaseHullMod {


    public static final float CREW_SAVE_BONUS_SML = 0.10f;
	public static final float CASUALTY_REDUCTION_SML = 0.05f;
	
	public static final float CREW_SAVE_BONUS_MED = 0.25f;
	public static final float CASUALTY_REDUCTION_MED = 0.10f;
	
	public static final float CREW_SAVE_BONUS_LRG = 0.50f;
	public static final float CASUALTY_REDUCTION_LRG = 0.20f;


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) (CREW_SAVE_BONUS_SML * 100f)+ "%"; // + Strings.X;
        if (index == 1) return "" + (int) (CREW_SAVE_BONUS_MED * 100f)+ "%"; // + Strings.X;
        if (index == 2) return "" + (int) (CREW_SAVE_BONUS_LRG * 100f)+ "%"; // + Strings.X;
        if (index == 3) return "" + (int) (CREW_SAVE_BONUS_LRG * 100f)+ "%"; // + Strings.X;
		if (index == 4) return "" + (int) (CASUALTY_REDUCTION_SML * 100f)+ "%"; // + Strings.X;
		if (index == 5) return "" + (int) (CASUALTY_REDUCTION_MED * 100f)+ "%"; // + Strings.X;
		if (index == 6) return "" + (int) (CASUALTY_REDUCTION_LRG * 100f)+ "%"; // + Strings.X;
		if (index == 7) return "" + (int) (CASUALTY_REDUCTION_LRG * 100f)+ "%"; // + Strings.X;
		return null;
	}

}