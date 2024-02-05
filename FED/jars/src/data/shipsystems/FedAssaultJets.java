package data.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class FedAssaultJets extends BaseShipSystemScript {

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        stats.getMaxSpeed().modifyPercent(id, 125f * effectLevel);
        stats.getAcceleration().modifyPercent(id, 50f * effectLevel);
        stats.getTurnAcceleration().modifyFlat(id, 5f * effectLevel);
        stats.getMaxTurnRate().modifyFlat(id, 3f * effectLevel);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("all thrusters burning", false);
        }
        return null;
    }

    public float getActiveOverride(ShipAPI ship) {
        if (ship.getHullSize() == HullSize.FRIGATE) {
            return 1f;
        }
        if (ship.getHullSize() == HullSize.DESTROYER) {
            return 1.1f;
        }
        if (ship.getHullSize() == HullSize.CRUISER) {
            return 1.25f;
        }
        return -1;
    }

    public float getInOverride(ShipAPI ship) {
        if (ship.getHullSize() == HullSize.FRIGATE) {
            return 0.3f;
        }
        if (ship.getHullSize() == HullSize.DESTROYER) {
            return 0.3f;
        }
        if (ship.getHullSize() == HullSize.CRUISER) {
            return 0.4f;
        }
        return -1;
    }

    public float getOutOverride(ShipAPI ship) {
        if (ship.getHullSize() == HullSize.FRIGATE) {
            return 1.25f;
        }
        if (ship.getHullSize() == HullSize.DESTROYER) {
            return 1.5f;
        }
        if (ship.getHullSize() == HullSize.CRUISER) {
            return 2f;
        }
        return -1;
    }

    public float getRegenOverride(ShipAPI ship) {
        if (ship.getHullSize() == HullSize.FRIGATE) {
            return 0.1f;
        }
        if (ship.getHullSize() == HullSize.DESTROYER) {
            return 0.9f;
        }
        if (ship.getHullSize() == HullSize.CRUISER) {
            return 0.08f;
        }
        return -1;
    }

    public int getUsesOverride(ShipAPI ship) {
        if (ship.getHullSize() == HullSize.FRIGATE) {
            return 2;
        }
        if (ship.getHullSize() == HullSize.DESTROYER) {
            return 2;
        }
        if (ship.getHullSize() == HullSize.CRUISER) {
            return 2;
        }
        return -1;
    }
}
