package data.hullmods.ix;

import java.util.Collection;
import java.util.Random;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class ChargeRegulatorController extends BaseHullMod {
	
	private static String AMP_ID = "ix_charge_amplifier";
	private static String AMP_H_ID = "ix_charge_amplifier_handler";
	private static String INHB_ID = "ix_charge_inhibitor";
	private static String INHB_H_NH = "ix_charge_inhibitor_handler";
	private static String OVR_ID = "ix_charge_overcharger";
	private static String OVR_H_NH = "ix_charge_overcharger_handler";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI variant = stats.getVariant();
		//if neither mode is active, add amplifier handler and hullmod, order is amplify -> overcharge -> inhibit
		if (!variant.hasHullMod(AMP_H_ID) && !variant.hasHullMod(INHB_H_NH) && !variant.hasHullMod(OVR_H_NH)) {
			variant.addMod(AMP_H_ID);
			variant.addMod(AMP_ID);
		}
		//if amplifier handler is present but hullmod has been switched out, delete old handler, add new hullmods
		else if (variant.hasHullMod(AMP_H_ID) && !variant.hasHullMod(AMP_ID)) {
			variant.getHullMods().remove(AMP_H_ID);
			variant.getHullMods().remove(INHB_ID);
			variant.getHullMods().remove(INHB_H_NH);
			variant.addMod(OVR_H_NH);
			variant.addMod(OVR_ID);
		}
		//same for overcharger handler
		else if (variant.hasHullMod(OVR_H_NH) && !variant.hasHullMod(OVR_ID)) {
			variant.getHullMods().remove(OVR_H_NH);
			variant.getHullMods().remove(AMP_ID);
			variant.getHullMods().remove(AMP_H_ID);
			variant.addMod(INHB_H_NH);
			variant.addMod(INHB_ID);
		}
		//same for inhibitor handler
		else if (variant.hasHullMod(INHB_H_NH) && !variant.hasHullMod(INHB_ID)) {
			variant.getHullMods().remove(INHB_H_NH);
			variant.getHullMods().remove(OVR_ID);
			variant.getHullMods().remove(OVR_H_NH);
			variant.addMod(AMP_H_ID);
			variant.addMod(AMP_ID);
		}
	}
}