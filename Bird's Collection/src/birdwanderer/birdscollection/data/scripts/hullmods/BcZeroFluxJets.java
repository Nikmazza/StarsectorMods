package birdwanderer.birdscollection.data.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class BcZeroFluxJets extends BaseHullMod {
    public static float BOOST_BONUS = 30f;

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        stats.getZeroFluxSpeedBoost().modifyFlat(id, BOOST_BONUS);


    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "30";
        return null;
    }


}
