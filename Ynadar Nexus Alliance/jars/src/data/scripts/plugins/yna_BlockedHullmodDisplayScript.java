package data.scripts.plugins;

import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ShipAPI;

public class yna_BlockedHullmodDisplayScript extends BaseEveryFrameCombatPlugin {

    public static void showBlocked(ShipAPI blocked) {
        data.scripts.everyframe.yna_BlockedHullmodDisplayScript.showBlocked(blocked);
    }

    public static void stopDisplaying() {
        data.scripts.everyframe.yna_BlockedHullmodDisplayScript.stopDisplaying();
    }
}
