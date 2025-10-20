package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import java.util.HashMap;
import java.util.Map;
import Jaydee8652.JaydeePiracy.utils.JaydeePiracyIDs;

import static Jaydee8652.JaydeePiracy.utils.rulecmd.jdp_InitialiseFrictionless.getYear;

public class jdp_emulatedfrictionlesshull extends BaseHullMod {

	public static float ARMOR_MULT = 0.6f;
	private static final float SUPPLY_USE_MULT = 2f;
	private static final float PEAK_MULT = 0.5f;
	public static float CORONA_EFFECT_MULT = 0f;
	public static Map TIME = new HashMap();
	static {
		TIME.put(HullSize.FIGHTER, 0f);
		TIME.put(HullSize.FRIGATE, 100f);
		TIME.put(HullSize.DESTROYER, 80f);
		TIME.put(HullSize.CRUISER, 60f);
		TIME.put(HullSize.CAPITAL_SHIP, 40f);
	}

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//That's good!
		stats.getTimeMult().modifyPercent(id, (Float) TIME.get(hullSize));
		float mult = CORONA_EFFECT_MULT;
		stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, mult);

		//That's bad!
		stats.getEffectiveArmorBonus().modifyMult(id, ARMOR_MULT);
		stats.getMinArmorFraction().modifyMult(id, ARMOR_MULT);
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);
		stats.getPeakCRDuration().modifyMult(id, PEAK_MULT);
	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		boolean player = false;
		boolean modified = false;
		String id = "jdp_emulatedfrictionlesshull_" + ship.getId();

		player = ship == Global.getCombatEngine().getPlayerShip();

		if (player) {
			Global.getCombatEngine().getTimeMult().modifyPercent(id, 100f / (100f + (Float) TIME.get(ship.getHullSize())));
			modified = true;
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
			modified = false;
		}
	}

	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		for (ShipAPI module : ship.getChildModulesCopy()) {
			//That's good!
			module.getMutableStats().getTimeMult().modifyPercent(id, (Float) TIME.get(module.getHullSize()));
			float mult = CORONA_EFFECT_MULT;
			module.getMutableStats().getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, mult);

			//That's bad!
			module.getMutableStats().getEffectiveArmorBonus().modifyMult(id, ARMOR_MULT);
			module.getMutableStats().getMinArmorFraction().modifyMult(id, ARMOR_MULT);
			module.getMutableStats().getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);
			module.getMutableStats().getPeakCRDuration().modifyMult(id, PEAK_MULT);
		}
	}


	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Frictionless NMC-" + getYear();
		if (index == 1) return "" + ((Float) TIME.get(HullSize.FRIGATE)).intValue() + "%";
		if (index == 2) return "" + ((Float) TIME.get(HullSize.DESTROYER)).intValue() + "%";
		if (index == 3) return "" + ((Float) TIME.get(HullSize.CRUISER)).intValue() + "%";
		if (index == 4) return "" + ((Float) TIME.get(HullSize.CAPITAL_SHIP)).intValue() + "%";
		if (index == 5) return "" + (int) Math.round((1f - CORONA_EFFECT_MULT) * 100f) + "%";
		if (index == 6) return "" + (int) Math.round(ARMOR_MULT * 100f) + "%";
		if (index == 7) return "" + (int)((SUPPLY_USE_MULT - 1f) * 100f) + "%";
		if (index == 8) return "2";
		return null;
	}

	public CargoStackAPI getRequiredItem() {
		return Global.getSettings().createCargoStack(CargoAPI.CargoItemType.SPECIAL,
				new SpecialItemData(JaydeePiracyIDs.JDP_FRICTIONLESSMETAL, null), null);
	}



	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (ship != null && ship.getHullSpec().isPhase()) {
			return false;
		}
		if (ship != null && ship.getVariant().getHullMods().contains("jdp_frictionlesshull")) {
			return false;
		}
		return true;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains("jdp_frictionlesshull")) {
			return "Incompatible, the hull of this vessel is already frictionless.";
		}
		if (ship.getHullSpec().isPhase()) {
			return "The material is incompatible with phase ships.";
		}
		return null;
	}

}




