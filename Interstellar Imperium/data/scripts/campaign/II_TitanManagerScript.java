package data.scripts.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.SharedUnlockData;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.II_Util;
import java.util.List;

public class II_TitanManagerScript implements EveryFrameScript {

    private final IntervalUtil interval = new IntervalUtil(1f, 1f);

    @Override
    public void advance(float amount) {
        interval.advance(amount);

        if (interval.intervalElapsed()) {
            List<CampaignFleetAPI> fleets = Global.getSector().getCurrentLocation().getFleets();
            for (CampaignFleetAPI fleet : fleets) {
                List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
                for (FleetMemberAPI member : members) {
                    String specId = II_Util.getNonDHullId(member.getHullSpec());
                    if (specId.contentEquals("ii_titan") || specId.contentEquals("ii_titan_armor")
                            || specId.contentEquals("ii_titan_targeting") || specId.contentEquals("ii_titan_elite")) {
                        fleet.removeFleetMemberWithDestructionFlash(member);
                    }
                }
            }

            boolean save = false;
            if (!SharedUnlockData.get().isPlayerAwareOfShip("ii_titan") && SharedUnlockData.get().isPlayerAwareOfShip("ii_olympus")) {
                SharedUnlockData.get().reportPlayerAwareOfShip("ii_titan", false);
                save = true;
            }
            if (!SharedUnlockData.get().isPlayerAwareOfShip("ii_titan_armor") && SharedUnlockData.get().isPlayerAwareOfShip("ii_olympus")) {
                SharedUnlockData.get().reportPlayerAwareOfShip("ii_titan_armor", false);
                save = true;
            }
            if (!SharedUnlockData.get().isPlayerAwareOfShip("ii_titan_targeting") && SharedUnlockData.get().isPlayerAwareOfShip("ii_olympus")) {
                SharedUnlockData.get().reportPlayerAwareOfShip("ii_titan_targeting", false);
                save = true;
            }
            if (!SharedUnlockData.get().isPlayerAwareOfShip("ii_titan_elite") && SharedUnlockData.get().isPlayerAwareOfShip("ii_olympus")) {
                SharedUnlockData.get().reportPlayerAwareOfShip("ii_titan_elite", false);
                save = true;
            }
            if (!SharedUnlockData.get().isPlayerAwareOfWeapon("ii_apocalypse_mirv") && SharedUnlockData.get().isPlayerAwareOfShip("ii_olympus")) {
                SharedUnlockData.get().reportPlayerAwareOfWeapon("ii_apocalypse_mirv", false);
                save = true;
            }
            if (!SharedUnlockData.get().isPlayerAwareOfWeapon("ii_fundae_mirv") && SharedUnlockData.get().isPlayerAwareOfShip("ii_olympus")) {
                SharedUnlockData.get().reportPlayerAwareOfWeapon("ii_fundae_mirv", false);
                save = true;
            }
            if (save) {
                SharedUnlockData.get().saveIfNeeded();
            }
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }
}
