package data.hullmods.vice;

import java.awt.Color;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import data.scripts.vice.util.RemnantSubsystemsUtil;

public class AdaptiveGravityDrive extends BaseHullMod {
	
	//dummy hullmod, system switch handled by built-in hullmods/handler
	private static String LAMPETIA_HULLMOD = "vice_lampetia_remnant";
	private static String RADIANT_IX_TW_MOD = "ix_converted_hull";
	private static String RESPLENDENT_HULLMOD = "vice_resplendent_remnant";
	private static String RESPLENDENT_PROTOTYPE_MOD = "vice_resplendent_prototype";
	private static String HANDLER_MOD = "vice_gravity_drive_handler";
	private static String GRAPHICS_OVERRIDE_MOD = "vice_converted_bridge";
	private static String NEW_SPRITE = "resplendent_adaptive_gravity_drive";
	private static String BASE_SYSTEM = "displacer";
	private static String THIS_SYSTEM = "vice_fleetjump";
	
	//Utility variables
	private RemnantSubsystemsUtil util = new RemnantSubsystemsUtil();
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		//valid ships have their own hullmods handle system swap
		if (!isValidShip(ship)) ship.getVariant().addPermaMod(HANDLER_MOD); 
		if (ship.getHullSpec().getHullId().equals("vice_resplendent") && !ship.getVariant().hasHullMod(GRAPHICS_OVERRIDE_MOD)) {
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
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		return ((isValidShip(ship) || hasSystem(ship)) && util.isApplicable(ship) && util.isOnlyRemnantMod(ship));
	}
	
	private boolean hasSystem(ShipAPI ship) {
		return BASE_SYSTEM.equals(ship.getHullSpec().getShipSystemId()) 
				|| THIS_SYSTEM.equals(ship.getHullSpec().getShipSystemId());
	}
	
	private boolean isValidShip(ShipAPI ship) {
		return (ship.getVariant().getHullMods().contains(LAMPETIA_HULLMOD) 
				|| ship.getVariant().getHullMods().contains(RESPLENDENT_HULLMOD)
				|| ship.getVariant().getHullMods().contains(RESPLENDENT_PROTOTYPE_MOD)
				|| ship.getVariant().getHullMods().contains(RADIANT_IX_TW_MOD));
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (!isValidShip(ship) || !hasSystem(ship)) return "Incompatible ship system";
		if (!util.isApplicable(ship)) return util.getIncompatibleCauseString("manufacturer");
		if (!util.isOnlyRemnantMod(ship)) return util.getIncompatibleCauseString("modcount");
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Fleet Jump";
		return null;
	}
}