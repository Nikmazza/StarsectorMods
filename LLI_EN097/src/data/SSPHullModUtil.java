package data;

import com.fs.starfarer.api.combat.ShipAPI;

import java.util.Map;

public class SSPHullModUtil {

    public static String getHullSizeFlatString(Map<ShipAPI.HullSize, Float> map) {
        return "" + map.get(ShipAPI.HullSize.FRIGATE).intValue() + "/" + map.get(ShipAPI.HullSize.DESTROYER).intValue() + "/" + map.get(ShipAPI.HullSize.CRUISER).intValue() + "/"
                + map.get(ShipAPI.HullSize.CAPITAL_SHIP).intValue() + "";
    }


    public static String getHullSizePercentString(Map<ShipAPI.HullSize, Float> map) {
        return SSPMisc.getDigitValue(map.get(ShipAPI.HullSize.FRIGATE)) + "%/" + SSPMisc.getDigitValue(map.get(ShipAPI.HullSize.DESTROYER)) + "%/" + SSPMisc.getDigitValue(map.get(ShipAPI.HullSize.CRUISER)) + "%/"
                + SSPMisc.getDigitValue(map.get(ShipAPI.HullSize.CAPITAL_SHIP)) + "%";
    }
}
