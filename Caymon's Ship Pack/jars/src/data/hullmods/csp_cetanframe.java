package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.HashSet;
import java.util.Set;
import org.magiclib.util.MagicIncompatibleHullmods;

public class csp_cetanframe extends BaseHullMod {

    private static Set<String> BLOCKED_HULLMODS = new HashSet();
    static {
        BLOCKED_HULLMODS.add("safetyoverrides");
    }
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                ship.getVariant().removeMod(tmp);
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "csp_cetanframe");
            }
        }
    }
}
