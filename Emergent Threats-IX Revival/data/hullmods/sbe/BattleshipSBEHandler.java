package data.hullmods.sbe;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

//Swap weapons and ship sprite, NO_GUN_MOD is added by lunalib extract button, OP discount is added by NO_GUN_MOD
public class BattleshipSBEHandler extends BaseHullMod {

	private static String NO_GUN_MOD = "ix_dedicated_ewar_suite";
	private static String SBE_MOD = "ix_sbe";
	private static String ECM_MOD = "ecm";
	private static String INTREPID_MOD = "ix_intrepid_sbe_handler";
	private static String RADIANT_MOD = "ix_radiant_sbe_handler";
	private static String RADIANT_BASE_MOD = "ix_converted_hull";
	
	private static String INTREPID_SLOT = "WS DEM";
	private static String RADIANT_SLOT = "WS SBE";
	private static String SBE_WEAPON_ID = "sbe_ix";
	private static String EWAR_DECO_ID = "ewar_suite_ix";
	private static String EWAR_DECO_HG = "ewar_suite_hg";
	
	boolean runOnce = false;
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		ShipVariantAPI variant = stats.getVariant();
		if (variant.hasHullMod(NO_GUN_MOD)) {
			if (id.equals(INTREPID_MOD)) variant.addWeapon(INTREPID_SLOT, EWAR_DECO_ID);
			if (id.equals(RADIANT_MOD)) variant.addWeapon(RADIANT_SLOT, EWAR_DECO_HG);
			stats.getVariant().getPermaMods().remove(SBE_MOD);
			stats.getVariant().getPermaMods().remove(ECM_MOD);
			stats.getVariant().getHullMods().remove(SBE_MOD);
			stats.getVariant().getHullMods().remove(ECM_MOD);
		}
		else {
			if (id.equals(INTREPID_MOD)) variant.addWeapon(INTREPID_SLOT, SBE_WEAPON_ID);
			if (id.equals(RADIANT_MOD)) variant.addWeapon(RADIANT_SLOT, SBE_WEAPON_ID);
			stats.getVariant().getHullMods().add(SBE_MOD);
			stats.getVariant().getPermaMods().add(SBE_MOD);
		}
		deleteBuiltInWeapons();
	}
	
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		String sprite = "";
		ShipVariantAPI variant = ship.getVariant();
		if (!variant.hasHullMod(NO_GUN_MOD)) return;
		if (id.equals(INTREPID_MOD)) sprite = "intrepid_ix_no_sbe";
		else if (id.equals(RADIANT_MOD) && variant.hasHullMod(RADIANT_BASE_MOD)) sprite = "radiant_ix_no_sbe";
		else if (id.equals(RADIANT_MOD)) sprite = "radiant_hvb_no_sbe";
		
		float x = ship.getSpriteAPI().getCenterX();
		float y = ship.getSpriteAPI().getCenterY();
		float alpha = ship.getSpriteAPI().getAlphaMult();
		float angle = ship.getSpriteAPI().getAngle();
		Color color = ship.getSpriteAPI().getColor();
		ship.setSprite("ix_ships", sprite);
		ship.getSpriteAPI().setCenter(x, y);
		ship.getSpriteAPI().setAlphaMult(alpha);
		ship.getSpriteAPI().setAngle(angle);
		ship.getSpriteAPI().setColor(color);
	}
	
	private void deleteBuiltInWeapons() {
		try {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			for (CargoStackAPI s : cargo.getStacksCopy()) {
				if (s.isWeaponStack() 
					&& (s.getWeaponSpecIfWeapon().getWeaponId().equals(SBE_WEAPON_ID)
						|| s.getWeaponSpecIfWeapon().getWeaponId().equals(EWAR_DECO_ID)
						|| s.getWeaponSpecIfWeapon().getWeaponId().equals(EWAR_DECO_HG))) {
					cargo.removeStack(s);
				}
			}
		}
		catch (Exception e) {}
	}
}
