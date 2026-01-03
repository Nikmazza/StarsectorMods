package data.hullmods.vice;

import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

public class AtlasController extends BaseHullMod {
	
	private static String CARGO_ID = "vice_atlas_cargo_module";
	private static String CARGO_H = "vice_atlas_cargo_handler";
	private static String FUEL_ID = "vice_atlas_fuel_scoop";
	private static String FUEL_H = "vice_atlas_fuel_handler";
	private static String COLONY_ID = "vice_atlas_colony_pod";
	private static String COLONY_H = "vice_atlas_colony_handler";
	
	private static String BAY_MOD_1 = "converted_hangar";
	private static String BAY_MOD_2 = "vice_adaptive_drone_bay";
	
	private static String NEW_SPRITE = "atlas_tt_bay";
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI variant = stats.getVariant();
		//if no mode is active, add cargo module. Order is cargo -> fuel -> colony -> cargo
		if (!variant.hasHullMod(CARGO_H) && !variant.hasHullMod(FUEL_H) && !variant.hasHullMod(COLONY_H)) {
			variant.addMod(CARGO_H);
			variant.addMod(CARGO_ID);
		}
		//if cargo handler is present but hullmod has been switched out, delete old handler, add new hullmods
		else if (variant.hasHullMod(CARGO_H) && !variant.hasHullMod(CARGO_ID)) {
			variant.getHullMods().remove(CARGO_H);
			variant.getHullMods().remove(COLONY_H);
			variant.getHullMods().remove(COLONY_ID);
			variant.addMod(FUEL_H);
			variant.addMod(FUEL_ID);
		}
		//same for fuel handler
		else if (variant.hasHullMod(FUEL_H) && !variant.hasHullMod(FUEL_ID)) {
			variant.getHullMods().remove(FUEL_H);
			variant.getHullMods().remove(CARGO_H);
			variant.getHullMods().remove(CARGO_ID);
			variant.addMod(COLONY_H);
			variant.addMod(COLONY_ID);
		}
		//same for salvage handler
		else if (variant.hasHullMod(COLONY_H) && !variant.hasHullMod(COLONY_ID)) {
			variant.getHullMods().remove(COLONY_H);
			variant.getHullMods().remove(FUEL_H);
			variant.getHullMods().remove(FUEL_ID);
			variant.addMod(CARGO_H);
			variant.addMod(CARGO_ID);
		}
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		String hullId = ship.getHullSpec().getHullId();
		if ((hullId.equals("vice_atlas_tt") || hullId.equals("vice_atlas_tt_default_d")) 
					&& (ship.getVariant().hasHullMod(BAY_MOD_1) || ship.getVariant().hasHullMod(BAY_MOD_2))) {
			float x = ship.getSpriteAPI().getCenterX();
			float y = ship.getSpriteAPI().getCenterY();
			float alpha = ship.getSpriteAPI().getAlphaMult();
			float angle = ship.getSpriteAPI().getAngle();
			Color color = ship.getSpriteAPI().getColor();
			ship.setSprite("vice_ships", NEW_SPRITE);
			ship.getSpriteAPI().setCenter(x, y);
			ship.getSpriteAPI().setAlphaMult(alpha);
			ship.getSpriteAPI().setAngle(angle);
			ship.getSpriteAPI().setColor(color);	
		}
	}
}