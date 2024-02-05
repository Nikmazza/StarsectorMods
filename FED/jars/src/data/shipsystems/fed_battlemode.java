package data.shipsystems;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;

public class fed_battlemode extends BaseShipSystemScript {

    public static final Object KEY_JITTER = new Object();

    public static final float SPEED_INCREASE_PERCENT = 30;
    public static final float ROF_BONUS = 1.30f;
    public static final float BAL_FLUX_REDUCTION = 20f;

    public static final Color JITTER_UNDER_COLOR = new Color(0, 50, 255, 105);
    public static final Color JITTER_COLOR = new Color(0, 50, 255, 45);

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }

        if (effectLevel > 0) {
            float jitterLevel = effectLevel;
            float maxRangeBonus = 5f;
            float jitterRangeBonus = jitterLevel * maxRangeBonus;
            for (ShipAPI fighter : getFighters(ship)) {
                if (fighter.isHulk()) {
                    continue;
                }
                MutableShipStatsAPI fStats = fighter.getMutableStats();
//				fStats.getBallisticWeaponDamageMult().modifyPercent(id, DAMAGE_INCREASE_PERCENT * effectLevel);
//				fStats.getEnergyWeaponDamageMult().modifyPercent(id, DAMAGE_INCREASE_PERCENT * effectLevel);
//				fStats.getMissileWeaponDamageMult().modifyPercent(id, DAMAGE_INCREASE_PERCENT * effectLevel);

                fStats.getMaxSpeed().modifyMult(id, 1f + 0.01f * SPEED_INCREASE_PERCENT * effectLevel);

                if (jitterLevel > 0) {
                    //fighter.setWeaponGlow(effectLevel, new Color(255,50,0,125), EnumSet.allOf(WeaponType.class));
                    //fighter.setWeaponGlow(effectLevel, Misc.setAlpha(JITTER_UNDER_COLOR, 255), EnumSet.allOf(WeaponType.class));

                    fighter.setJitterUnder(KEY_JITTER, JITTER_COLOR, jitterLevel, 4, 0f, jitterRangeBonus);
                    fighter.setJitter(KEY_JITTER, JITTER_UNDER_COLOR, jitterLevel, 1, 0f, 0 + jitterRangeBonus * 1f);
                    
                    Global.getSoundPlayer().playLoop("system_targeting_feed_loop", ship, 1f, 1f, fighter.getLocation(), fighter.getVelocity());
                }
            }
            float mult = -1f + (1f + ROF_BONUS * effectLevel);
            stats.getEnergyRoFMult().modifyMult(id, mult);
            stats.getEnergyWeaponFluxCostMod().modifyPercent(id, -BAL_FLUX_REDUCTION * effectLevel);
            stats.getBallisticRoFMult().modifyMult(id, mult);
            stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -BAL_FLUX_REDUCTION * effectLevel);
            stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, 100f);
        }
    }

    private List<ShipAPI> getFighters(ShipAPI carrier) {
        List<ShipAPI> result = new ArrayList<ShipAPI>();

//		this didn't catch fighters returning for refit		
//		for (FighterLaunchBayAPI bay : carrier.getLaunchBaysCopy()) {
//			if (bay.getWing() == null) continue;
//			result.addAll(bay.getWing().getWingMembers());
//		}
        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (!ship.isFighter()) {
                continue;
            }
            if (ship.getWing() == null) {
                continue;
            }
            if (ship.getWing().getSourceShip() == carrier) {
                result.add(ship);
            }
        }

        return result;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        for (ShipAPI fighter : getFighters(ship)) {
            if (fighter.isHulk()) {
                continue;
            }
            MutableShipStatsAPI fStats = fighter.getMutableStats();
            fStats.getMaxSpeed().unmodify(id);
        }
        stats.getBallisticRoFMult().unmodify(id);
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
        stats.getZeroFluxMinimumFluxLevel().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        float percent = SPEED_INCREASE_PERCENT * effectLevel;
        float mult = -1f + (1f + ROF_BONUS * effectLevel);
        float bonusPercentBallistic = (int) ((mult - 1f) * 100f);
        if (index == 0) {
            //return new StatusData("+" + (int)percent + "% fighter damage", false);
            return new StatusData("" + Misc.getRoundedValueMaxOneAfterDecimal(1f + SPEED_INCREASE_PERCENT * effectLevel * 0.01f) + "x fighter speed", false);
        }

        if (index == 0) {
            return new StatusData("rate of fire +" + (int) bonusPercentBallistic + "%", false);
        }

        if (index == 1) {
            return new StatusData("flux use -" + (int) BAL_FLUX_REDUCTION + "%", false);
        }
        
        return null;
    }

}
