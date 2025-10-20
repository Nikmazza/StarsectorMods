package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.util.HashMap;
import java.util.Map;

import static com.fs.starfarer.api.combat.ShipAPI.HullSize.*;

public class GMDA extends BaseHullMod {

	private static final float PEAK_MULT = 0.65f;
	private static final float CR_DEG_MULT = 2.0f;
	private static final float CAPACITY_PENALTY = 0.20f;
    private static final float OVERDRIVE_BOOST = 0.5f;
    private static final float OVERDRIVE_DMG_BOOST = 0.25f;
    private static final float FRIGATE_BOOST = 0.15f;
    private static final float DESTROYER_BOOST = 0.20f;
    private static final float CRUISER_BOOST = 0.25f;
    private static final float CAPITAL_BOOST = 0.3f;
    private static final float THRESHOLD = 450f;

    protected Object SPEEDKEY = new Object();

    @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getPeakCRDuration().modifyMult(id, PEAK_MULT);
        stats.getCargoMod().modifyMult(id, CAPACITY_PENALTY);
        stats.getFuelMod().modifyMult(id, CAPACITY_PENALTY);
		stats.getCRLossPerSecondPercent().modifyMult(id, CR_DEG_MULT);
	}

    public void advanceInCombat(ShipAPI ship, float amount) {
        ShipAPI playerShip = Global.getCombatEngine().getPlayerShip();
        String id = "GMDA_BOSA";
        float effect_level = (ship.getFluxTracker().getFluxLevel());

        if (!ship.getFluxTracker().isOverloaded());
        {
            ship.getMutableStats().getWeaponRangeThreshold().modifyFlat(id, (THRESHOLD));
            ship.getMutableStats().getWeaponRangeMultPastThreshold().modifyMult(id, (1 - effect_level));
            ship.getMutableStats().getBallisticWeaponDamageMult().modifyMult(id, (1 + (OVERDRIVE_DMG_BOOST * effect_level)));
            ship.getMutableStats().getEnergyWeaponDamageMult().modifyMult(id, (1 + (OVERDRIVE_DMG_BOOST * effect_level)));
            ship.getMutableStats().getBallisticRoFMult().modifyMult(id, (1 + (OVERDRIVE_BOOST * effect_level)));
            ship.getMutableStats().getEnergyRoFMult().modifyMult(id, (1 + (OVERDRIVE_BOOST * effect_level)));
            ship.getMutableStats().getFluxDissipation().modifyMult(id, (1 + (1.4f * OVERDRIVE_BOOST * effect_level)));
            ship.getMutableStats().getAcceleration().modifyMult(id, (1 + (1.4f * OVERDRIVE_BOOST * effect_level)));
            ship.getMutableStats().getDeceleration().modifyMult(id, (1 - (1.4f * OVERDRIVE_BOOST * effect_level)));
            ship.getMutableStats().getMaxTurnRate().modifyMult(id, (1 + (1.4f * OVERDRIVE_BOOST * effect_level)));
            ship.getMutableStats().getTurnAcceleration().modifyMult(id, (1 + (1.4f * OVERDRIVE_BOOST * effect_level)));
        }

        if (!ship.getFluxTracker().isOverloaded() && ship.getHullSize() == FRIGATE);
        {
            ship.getMutableStats().getMaxSpeed().modifyMult(id, (1 + (FRIGATE_BOOST * effect_level)));
        }

        if (!ship.getFluxTracker().isOverloaded() && ship.getHullSize() == DESTROYER);
        {
            ship.getMutableStats().getMaxSpeed().modifyMult(id, (1 + (DESTROYER_BOOST * effect_level)));
        }

        if (!ship.getFluxTracker().isOverloaded() && ship.getHullSize() == CRUISER);
        {
            ship.getMutableStats().getMaxSpeed().modifyMult(id, (1 + (CRUISER_BOOST * effect_level)));
        }

        if (!ship.getFluxTracker().isOverloaded() && ship.getHullSize() == CAPITAL_SHIP);
        {
            ship.getMutableStats().getMaxSpeed().modifyMult(id, (1 + (CAPITAL_BOOST * effect_level)));
        }



        if(ship==playerShip){
            Global.getCombatEngine().maintainStatusForPlayerShip(
                    "SPEEDKEY",
                    "graphics/icons/hullsys/temporal_shell.png",
                    "Bludknock Overdrive System Active",
                    "Overdrive at " + Math.round(effect_level*100) + "%",
                    false);
        }

        if (ship.getFluxTracker().isOverloaded()){
            ship.getMutableStats().getWeaponRangeThreshold().unmodifyMult(id);
            ship.getMutableStats().getWeaponRangeMultPastThreshold().unmodifyMult(id);
            ship.getMutableStats().getBallisticWeaponDamageMult().unmodifyMult(id);
            ship.getMutableStats().getEnergyWeaponDamageMult().unmodifyMult(id);
            ship.getMutableStats().getBallisticRoFMult().unmodifyMult(id);
            ship.getMutableStats().getEnergyRoFMult().unmodifyMult(id);
            ship.getMutableStats().getFluxDissipation().unmodifyMult(id);
            ship.getMutableStats().getAcceleration().unmodifyMult(id);
            ship.getMutableStats().getDeceleration().unmodifyMult(id);
            ship.getMutableStats().getMaxTurnRate().unmodifyMult(id);
            ship.getMutableStats().getTurnAcceleration().unmodifyMult(id);
            ship.getMutableStats().getMaxSpeed().unmodifyMult(id);
        }
    }


    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + "With increasing level of flux"; // + Strings.X;
        if (index == 1) return "" + "15%, 20%, 25% and 30%"; // + Strings.X;
        if (index == 2) return "" + "70%"; //
        if (index == 3) return "" + "50%"; //
        if (index == 4) return "" + "25%"; //
        if (index == 5) return "" + "with increasing flux, range decreases down to 450 s.u., and deceleration decreases down to 70% at maximum flux"; // + Strings.X;
        if (index == 6) return "" + (int) ((1f - PEAK_MULT)* 100f)+ "%"; // + Strings.X;
        if (index == 7) return "" + (int) ((CR_DEG_MULT)); // + Strings.X;
        if (index == 8) return "" + (int) ((1f - CAPACITY_PENALTY) * 100f)+ "%"; // + Strings.X;
        return null;
    }

}