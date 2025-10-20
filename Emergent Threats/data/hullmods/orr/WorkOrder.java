package data.hullmods.orr;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class WorkOrder extends BaseHullMod {
	
	private static List<String> WO_HULLMOD_LIST = new ArrayList<String>();
	static {
		WO_HULLMOD_LIST.add("orr_wo_anathema");
		WO_HULLMOD_LIST.add("orr_wo_giga");
		WO_HULLMOD_LIST.add("orr_wo_hmc");
		WO_HULLMOD_LIST.add("orr_wo_nal");
		WO_HULLMOD_LIST.add("orr_wo_tac");
		WO_HULLMOD_LIST.add("orr_wo_tpc");
		WO_HULLMOD_LIST.add("orr_wo_tpl");
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
	}
	
	private int getModCount(ShipVariantAPI variant) {
		int modCount = 0;
		for (String mod : WO_HULLMOD_LIST) {
			if (variant.hasHullMod(mod)) modCount++;
		}
		return modCount;
	}
	
	private boolean isValidShip(ShipVariantAPI variant) {
		//aggressor/xiv
		if (variant.hasHullMod("orr_aggressor")) return true;
		//aggressor asm (T)
		else if (variant.hasHullMod("orr_asmgressor")) return true;
		//Incursion (EX)
		else if (variant.hasHullMod("vice_intrepid_hull")) return true;
		//onslaught/xiv
		else if (variant.hasHullMod("orr_onslaught")) return true;
		//onslaught asm (T)
		else if (variant.hasHullMod("orr_asmslaught")) return true;
		//onslaught cgr
		else if (variant.hasHullMod("orr_damper_field")) return true;
		//onslaught dpl original
		else if (variant.getHullSpec().getHullId().equals("dpl_onslaught_alt") 
				|| variant.getHullSpec().getHullId().equals("dpl_onslaught_alt_default_D")) return true;
		//onslaught dpl ORR
		else if (variant.hasHullMod("orr_dplslaught")) return true;
		return false;
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		return getModCount(ship.getVariant()) < 2 && isValidShip(ship.getVariant());
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (!isValidShip(ship.getVariant())) return "Invalid ship type";
		return null;
	}	
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "";
		return null;
	}
}