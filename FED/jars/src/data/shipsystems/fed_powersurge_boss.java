package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class fed_powersurge_boss extends BaseShipSystemScript {

    public static final float ACCEL_BONUS = 150f;
    public static final float SPEED_BONUS_PERCENT = 300f;
    public static final float TURN_BONUS = 50f;
    public static final float TURN_ACCEL = 50f;
    public static final float TURN_BONUS_FLAT = 20f;
    public static final float TURN_ACCEL_FLAT = 30f;
    public static final float ROF_BONUS = 50f;
    //public static final float BAL_FLUX_REDUCTION = 50f;
    public static final float REGEN_RATE = 50f;
    
    private static final Color FLASH_COLOR = new Color(165, 255, 185, 250);
    private static final float FLASH_SIZE = 300f; //explosion size
    private static final float FLASH_DUR = 0.75f;
    
    private static final Color COLOR_COLOR = new Color(85, 255, 90, 230);
    private static final float COLOR_SIZE = 1100f; //explosion size
    private static final float COLOR_DUR = 1.5f;
    
    private static final Color SMOKE_COLOR = new Color(50, 60, 50, 100);
    private static final float SMOKE_SIZE = 1200f; //explosion size
    private static final float SMOKE_DUR = 4f;
    
    
    private static final float SPREAD_RANDOM = 175f; // random velocity added to the spread projectiles

    private CombatEngineAPI engine;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        ShipAPI ship = null;
        if (engine != Global.getCombatEngine()) {
            engine = Global.getCombatEngine();
        }

        // We can get ship APIs from a MutableShipStatAPI using getEntity()
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        
        WeaponAPI wep = null;
        for (WeaponAPI shipWeapon : ship.getAllWeapons()) {
            if (shipWeapon.getId().startsWith("fed_boss_surge") && !shipWeapon.isPermanentlyDisabled()) {
                wep = shipWeapon;
            }
        }

        if (null != state && wep != null) {
            switch (state) {
                case IN:
                    wep.repair();
                    break;
                case OUT:
                    if(!wep.isDisabled()) {

                        Vector2f loc = wep.getLocation();

                        // set up for explosions    
                        Vector2f ship_velocity = ship.getVelocity();

                        // do visual fx
                        engine.spawnExplosion(loc, ship_velocity, FLASH_COLOR, FLASH_SIZE, FLASH_DUR);
                        engine.spawnExplosion(loc, ship_velocity, COLOR_COLOR, COLOR_SIZE, COLOR_DUR);
                        engine.spawnExplosion(loc, ship_velocity, SMOKE_COLOR, SMOKE_SIZE, SMOKE_DUR);
                        
                        float baseFlux = ship.getFluxTracker().getMaxFlux();
                        float fluxPerShot = baseFlux / 360; //should be about 70 flux per shot added
                        
                        float currFlux = ship.getFluxTracker().getCurrFlux();
                        float damageAmount = currFlux/fluxPerShot;
                        
                        if (damageAmount > 400f) { damageAmount = 400f; } 
                        if (damageAmount < 200f) { damageAmount = 200f; } 
                        
                        float shotVariance = (50 + 60*ship.getFluxLevel());

                        for (int j = 0; j < damageAmount; j++) {
                            
                            float randomX = ship.getVelocity().getX() + ( shotVariance * MathUtils.getRandomNumberInRange(-1f, 1f));
                            float randomY = ship.getVelocity().getY() + ( shotVariance * MathUtils.getRandomNumberInRange(-1f, 1f));
                            engine.spawnProjectile(ship,
                                    wep,
                                    "fed_boss_surge_shot_clone",
                                    loc,
                                    MathUtils.getRandomNumberInRange(0f, 360f),
                                    MathUtils.getRandomPointOnLine(ship_velocity, new Vector2f(randomX, randomY)));
                        }
                        wep.disable();
                        ship.getFluxTracker().beginOverloadWithTotalBaseDuration(8 * ship.getFluxTracker().getFluxLevel() + 4f);
                        ship.getFluxTracker().decreaseFlux(damageAmount*10);
                        
                    }
                    break;
                    
            }
        }

        float enginePower = ACCEL_BONUS * effectLevel;
        float speedBonusPercent = (SPEED_BONUS_PERCENT * effectLevel * 0.5f) + (SPEED_BONUS_PERCENT * 0.5f);

        float turnBonus = TURN_BONUS * effectLevel;
        float turnAccelBonus = TURN_ACCEL * effectLevel;

        float turnBonusFlat = TURN_BONUS_FLAT * effectLevel;
        float turnAccelerationFlat = TURN_ACCEL_FLAT * effectLevel;

        float rof = (ROF_BONUS * effectLevel);
        stats.getEnergyRoFMult().modifyPercent(id, rof);
        stats.getBallisticRoFMult().modifyPercent(id, rof);

        stats.getAcceleration().modifyFlat(id, enginePower);
        stats.getMaxSpeed().modifyPercent(id, speedBonusPercent);

        stats.getMaxTurnRate().modifyPercent(id, turnBonus);
        stats.getTurnAcceleration().modifyPercent(id, turnAccelBonus);

        stats.getMaxTurnRate().modifyFlat(id, turnBonusFlat);
        stats.getTurnAcceleration().modifyFlat(id, turnAccelerationFlat);
        stats.getDeceleration().modifyFlat(id, enginePower / 2f);
        stats.getEnergyAmmoRegenMult().modifyPercent(id, rof);
        stats.getBallisticAmmoRegenMult().modifyPercent(id, rof);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id
    ) {
        stats.getAutofireAimAccuracy().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getDeceleration().unmodify(id);
        stats.getBallisticRoFMult().unmodify(id);
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
    }

    
    @Override
    public StatusData getStatusData(int index, State state,
            float effectLevel
    ) {
        float speedBonusFlat = 100 * effectLevel;
        float mult = (ROF_BONUS * effectLevel);

        if (index == 0) {
            return new StatusData("engine power increased +" + (int) speedBonusFlat + "%", false);
        }
        if (index == 1) {
            return new StatusData("rate of fire +" + (int) mult + "%", false);
        }
        return null;
    }
}
