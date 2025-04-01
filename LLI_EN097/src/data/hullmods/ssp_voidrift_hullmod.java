package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.SSP_NegativeExplosionVisual;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ssp_voidrift_hullmod extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getZeroFluxSpeedBoost().modifyMult(id,0f);
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        ship.setCollisionClass(CollisionClass.FIGHTER);
        ship.getVelocity().set(0,0);
        if(ship.isHulk()){
            Global.getCombatEngine().removeEntity(ship);
        }
    }

}

