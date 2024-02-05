package data.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class engineBatteryStats extends BaseShipSystemScript {

    @Override
    public void apply(MutableShipStatsAPI stats, String id, ShipSystemStatsScript.State state, float effectLevel) {
        
        if (null != state) switch (state) {
            case IN:
                stats.getMaxSpeed().modifyPercent(id, 0f);
                stats.getAcceleration().modifyPercent(id, 0f);
                stats.getDeceleration().modifyPercent(id, 0f);
                stats.getMaxTurnRate().modifyFlat(id, 20f);
                stats.getMaxTurnRate().modifyPercent(id, 120f);
                stats.getTurnAcceleration().modifyFlat(id, 20f);
                stats.getTurnAcceleration().modifyPercent(id, 125f);
                stats.getEntity().getVelocity().scale(0.8f);
                break;
            case ACTIVE:
                stats.getMaxSpeed().modifyPercent(id, 200f);
                //stats.getMaxSpeed().modifyFlat(id, 20f);
                stats.getAcceleration().modifyPercent(id, 500f);
                //stats.getAcceleration().modifyFlat(id, 50f);
                stats.getDeceleration().modifyPercent(id, 0f);
                stats.getMaxTurnRate().modifyFlat(id, 0f);
                stats.getMaxTurnRate().modifyPercent(id, 25f);
                stats.getTurnAcceleration().modifyPercent(id, 50f);
                stats.getTurnAcceleration().modifyFlat(id, 0f);
                break;
            case OUT:
                stats.getAcceleration().modifyPercent(id, 20f + effectLevel * 80f);
                stats.getDeceleration().modifyPercent(id, 50f);
                stats.getMaxTurnRate().unmodifyFlat(id);
                stats.getMaxTurnRate().modifyPercent(id, 20f + effectLevel * 80f);
                stats.getTurnAcceleration().modifyPercent(id, 20f + effectLevel * 80f);
                stats.getTurnAcceleration().unmodifyFlat(id);
                break;
            default:
                break;
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }

    @Override
    public ShipSystemStatsScript.StatusData getStatusData(int index, ShipSystemStatsScript.State state, float effectLevel) {
        if (index == 0) {
            return new ShipSystemStatsScript.StatusData("increased engine power", false);
        }
        return null;
    }

    
	public float getActiveOverride(ShipAPI ship) {
		if (ship.getHullSize() == HullSize.FRIGATE) {
			return 1f;
		}
		if (ship.getHullSize() == HullSize.DESTROYER) {
			return 1.5f;
		}
		if (ship.getHullSize() == HullSize.CRUISER) {
			return 2f;
		}
                if (ship.getHullSize() == HullSize.CAPITAL_SHIP) {
			return 3f;
		}
		return -1;
	}
	/*public float getInOverride(ShipAPI ship) {
		if (ship.getHullSize() == HullSize.FRIGATE) {
			return 0.7f;
		}
		if (ship.getHullSize() == HullSize.DESTROYER) {
			return 0.3f;
		}
		if (ship.getHullSize() == HullSize.CRUISER) {
			return 0.4f;
		}
		return -1;
	}*/
	public float getOutOverride(ShipAPI ship) {
		if (ship.getHullSize() == HullSize.FRIGATE) {
			return 2f;
		}
		if (ship.getHullSize() == HullSize.DESTROYER) {
			return 1.5f;
		}
		if (ship.getHullSize() == HullSize.CRUISER) {
			return 1.5f;
		}
		return -1;
        }
	
	public float getRegenOverride(ShipAPI ship) {
		if (ship.getHullSize() == HullSize.FRIGATE) {
			return 0.07f;
		}
		if (ship.getHullSize() == HullSize.DESTROYER) {
			return 0.06f;
		}
		if (ship.getHullSize() == HullSize.CRUISER) {
			return 0.05f;
		}
		return -1;
	}

	public int getUsesOverride(ShipAPI ship) {
		if (ship.getHullSize() == HullSize.FRIGATE) {
			return 1;
		}
		if (ship.getHullSize() == HullSize.DESTROYER) {
			return 2;
		}
		if (ship.getHullSize() == HullSize.CAPITAL_SHIP) {
			return 2;
		}
		return -1;
	}
     
}
