package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.Misc;

public class StarFederationCommissionedCrews extends BaseHullMod
{
    public static final String HULLMOD_ID = "StarFederationCommissionedCrews";
    public static final float REPAIR_TIME = 25f;
    public static final float CREW_INCREASE = 5f;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id)
    {
        stats.getCombatEngineRepairTimeMult().modifyPercent(id, -REPAIR_TIME);
        stats.getCombatWeaponRepairTimeMult().modifyPercent(id, -REPAIR_TIME);
        stats.getMinCrewMod().modifyPercent(id, CREW_INCREASE);
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize)
    {
        if (index == 0)
        {
            return "" + (int) (REPAIR_TIME) + "%";
        }
        if (index == 1)
        {
            return "" + (int) (CREW_INCREASE) + "%";
        }
        return null;
    }
}