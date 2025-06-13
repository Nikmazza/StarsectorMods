package data.hullmods.tw;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class PlasmaRamjet extends BaseHullMod {

	//improved ship system is new system assigned directly to the hull
	private static float DURATION_INCREASE = 33f;
	private static float SPEED_INCREASE = 20f;
	private static String ABOLISHER_MOD = "tw_tigershark_abolisher";
	private static String ABOLISHER_HANDLER = "tw_tigershark_ab_handler";
	private static String STORMWALL_MOD = "tw_tigershark_stormwall";
	private static String STORMWALL_HANDLER = "tw_tigershark_sw_handler";

	//also handles swapping the main gun on the Tigershark (TW)
	private static String MAIN_SLOT = "WS 004";
	private static String ABOLISHER_ID = "abolisher_built_in_tw";
	private static String STORMWALL_ID = "stormwall_built_in_tw";
	
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI variant = stats.getVariant();
		boolean ab = variant.hasHullMod(ABOLISHER_MOD);
		boolean abh = variant.hasHullMod(ABOLISHER_HANDLER);
		boolean sw = variant.hasHullMod(STORMWALL_MOD);
		boolean swh = variant.hasHullMod(STORMWALL_HANDLER);
		
		//if no mods at all, give ship the Abolisher and handler
		if (!ab && !abh && !sw && !swh) {
			variant.addMod(ABOLISHER_MOD);
			variant.addMod(ABOLISHER_HANDLER);
		}
		
		//if abolisher removed but handler still remains, swap to next weapon set
		else if (!ab && abh) {
			variant.removeMod(ABOLISHER_HANDLER);
			variant.addMod(STORMWALL_MOD);
			variant.addMod(STORMWALL_HANDLER);
		}
		
		//if stormwall removed but handler still remains, swap to first weapon set
		else if (!sw && swh) {
			variant.removeMod(STORMWALL_HANDLER);
			variant.addMod(ABOLISHER_MOD);
			variant.addMod(ABOLISHER_HANDLER);
		}
    }
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		deleteWeaponInCargo(ABOLISHER_ID);
		deleteWeaponInCargo(STORMWALL_ID);
	}
	
	private void deleteWeaponInCargo(String weapon) {
		try {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			for (CargoStackAPI s : cargo.getStacksCopy()) {
				if (s.isWeaponStack() && s.getWeaponSpecIfWeapon().getWeaponId().equals(weapon)) {
					cargo.removeStack(s);					
				}
			}
		}
		catch (Exception e) {}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) DURATION_INCREASE + "%";
		if (index == 1) return "" + (int) SPEED_INCREASE + "%";
		return null;
	}
}