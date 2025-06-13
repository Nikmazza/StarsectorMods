package data.scripts.shipsystems;

import java.util.List;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import org.lazywizard.lazylib.VectorUtils;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.WaveDistortion;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.util.vector.ReadableVector2f;
import org.lwjgl.util.vector.Vector2f;
import org.lazywizard.lazylib.MathUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.DamageType;
import java.awt.Color;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class BrutePhaseStatsAlt extends BaseShipSystemScript
{
    private static final Color JITTER_COLOR;
    private static final Color JITTER_UNDER_COLOR;
    private static final Color AFTERIMAGE_COLOR;
    private static final Color EXPLOSION_COLOR;
    private static final Color EMP_COLOR;
    private static final Color LENS_FLARE_OUTER_COLOR;
    private static final Color LENS_FLARE_CORE_COLOR;
    private static final DamageType EXPLOSION_DAMAGE_TYPE;
    private boolean Explosions;
    
    
    public BrutePhaseStatsAlt() {
        this.Explosions = true;
    }
    
    public static float getMaxTimeMult(final MutableShipStatsAPI stats) {
        return 1.0f + 4.0f * stats.getDynamic().getValue("phase_time_mult");
    }
    
    public void apply(final MutableShipStatsAPI stats, String id, final State state, final float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }
        final ShipAPI ship = (ShipAPI)stats.getEntity();
        final boolean player = ship == Global.getCombatEngine().getPlayerShip();
        final CombatEngineAPI engine = Global.getCombatEngine();
        id = id + "_" + ship.getId();
        if (Global.getCombatEngine().isPaused() || !ship.isAlive()) {
            return;
        }
        float levelForAlpha = effectLevel;
        ShipSystemAPI cloak = ship.getPhaseCloak();
        if (cloak == null) {
            cloak = ship.getSystem();
        }
        //System
        if (state == State.IN || state == State.ACTIVE) {
            ship.setPhased(true);
            levelForAlpha = effectLevel;

        }
        if (state == State.OUT) {
            levelForAlpha = effectLevel;
            
        }

        //Alpha stuff
        ship.setExtraAlphaMult(1.0f - 0.75f * levelForAlpha);
        ship.setApplyExtraAlphaToEngines(true);

        final float shipTimeMult = 1.0f + (getMaxTimeMult(stats) - 1.0f) * levelForAlpha;
        stats.getTimeMult().modifyMult(id, shipTimeMult);

        if (player) {
            Global.getCombatEngine().getTimeMult().modifyMult(id, 1.0f / shipTimeMult);

        }
        else {
            Global.getCombatEngine().getTimeMult().unmodify(id);
        }

        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getTimeMult().unmodify(id);
            Global.getCombatEngine().getTimeMult().unmodify(id);
            stats.getMaxSpeed().unmodify(id);
            stats.getAcceleration().unmodify(id);
            ship.setPhased(false);
            ship.setExtraAlphaMult(1.0f);
            Global.getSoundPlayer().playSound("bt_exit_phase", 1.0f, 1.0f, ship.getLocation(), ship.getVelocity());



            if (this.Explosions) {
                engine.spawnExplosion(ship.getLocation(), ship.getVelocity(), BrutePhaseStatsAlt.EXPLOSION_COLOR, ship.getCollisionRadius() * 1.25f, 0.3f);

                final Vector2f vector2f;
                final Vector2f loc = vector2f = new Vector2f((ReadableVector2f)ship.getLocation());
                vector2f.x -= (float)(8.0 * FastTrig.cos(ship.getFacing() * Math.PI / 180.0));
                final Vector2f vector2f2 = loc;
                vector2f2.y -= (float)(8.0 * FastTrig.sin(ship.getFacing() * Math.PI / 180.0));

                final StandardLight light = new StandardLight();
                light.setLocation(loc);
                light.setIntensity(0.35f);
                light.setSize(950.0f);
                light.setColor(BrutePhaseStatsAlt.EXPLOSION_COLOR);
                light.fadeOut(1.0f);
                LightShader.addLight(light);

                final WaveDistortion wave = new WaveDistortion();
                wave.setLocation(loc);
                wave.setSize(950.0f);
                wave.setIntensity(85.0f);
                wave.fadeInSize(1.2f);
                wave.fadeOutIntensity(0.9f);
                wave.setSize(262.5f);
                DistortionShader.addDistortion(wave);

                final List<CombatEntityAPI> entities = (List<CombatEntityAPI>)CombatUtils.getEntitiesWithinRange(ship.getLocation(), 700.0f);
                for (int size = entities.size(), i = 0; i < size; ++i) {
                    final CombatEntityAPI tmp = entities.get(i);
                    final float mod = 1.0f - MathUtils.getDistance((CombatEntityAPI)ship, tmp) / 600.0f;
                    float force = 50.0f * mod;
                    float damage = 800.0f * mod;
                    float emp = 500.0f * mod;
                    if (tmp instanceof MissileAPI) {
                        force = 50.0f * mod;
                        engine.applyDamage(tmp, loc, 800.0f, DamageType.ENERGY, 0.8f, false, true, (Object)ship);
                    }
                    if (tmp instanceof ShipAPI) {
                        final ShipAPI victim = (ShipAPI)tmp;
                        if (victim.getHullSize() == ShipAPI.HullSize.FIGHTER) {
                            force = 60.0f * mod;
                            damage /= 0.25f;
                        }
                        else if (victim.getHullSize() == ShipAPI.HullSize.FRIGATE) {
                            force = 40.0f * mod;
                            damage /= 1.0f;
                        }
                        else if (victim.getHullSize() == ShipAPI.HullSize.DESTROYER) {
                            force = 30.0f * mod;
                            damage /= 1.0f;
                        }
                        else if (victim.getHullSize() == ShipAPI.HullSize.CRUISER) {
                            force = 20.0f * mod;
                            damage /= 1.0f;
                        }
                        else if (victim.getHullSize() == ShipAPI.HullSize.CAPITAL_SHIP) {
                            force = 12.0f * mod;
                            damage /= 1.0f;
                        }
                        if (victim.getOwner() == ship.getOwner()) {
                            damage *= 0.01f;
                            emp *= 0.02f;
                            force *= 0.2f;
                        }
                        if (victim.getShield() != null && victim.getShield().isOn() && victim.getShield().isWithinArc(ship.getLocation())) {
                            victim.getFluxTracker().increaseFlux(damage * 1.0f, true);
                        }
                        else {
                            for (int x = 0; x < 5; ++x) {
                                engine.spawnEmpArc(ship, MathUtils.getRandomPointInCircle(victim.getLocation(), victim.getCollisionRadius()), (CombatEntityAPI)victim, (CombatEntityAPI)victim, BrutePhaseStatsAlt.EXPLOSION_DAMAGE_TYPE, damage / 10.0f, emp / 5.0f, 900.0f, (String)null, 3.0f, BrutePhaseStatsAlt.EMP_COLOR, BrutePhaseStatsAlt.EMP_COLOR);
                            }
                        }
                    }
                    final Vector2f dir = VectorUtils.getDirectionalVector(ship.getLocation(), tmp.getLocation());
                    dir.scale(force);
                    Vector2f.add(tmp.getVelocity(), dir, tmp.getVelocity());
                }
                this.Explosions = true;
            }
        }
        else {
            stats.getMaxSpeed().modifyPercent(id, 200.0f * effectLevel);
            stats.getAcceleration().modifyFlat(id, 1500.0f * effectLevel);
            stats.getDeceleration().modifyPercent(id, 1000f * effectLevel);
            stats.getMaxTurnRate().modifyPercent(id, 185f * effectLevel);
        }
    }
    
    public void unapply(final MutableShipStatsAPI stats, final String id) {
        if (stats.getEntity() instanceof ShipAPI) {
            final ShipAPI ship = (ShipAPI)stats.getEntity();
            stats.getMaxSpeed().unmodify("BrutePhaseStatsAlt");
            stats.getAcceleration().unmodify("BrutePhaseStatsAlt");
            stats.getDeceleration().unmodify("BrutePhaseStatsAlt");
            stats.getMaxTurnRate().unmodify("BrutePhaseStatsAlt");
            stats.getTimeMult().unmodify("BrutePhaseStatsAlt");
            Global.getCombatEngine().getTimeMult().unmodify("BrutePhaseStatsAlt");
        }
    }
    
    public ShipSystemStatsScript.StatusData getStatusData(final int index, final ShipSystemStatsScript.State state, final float effectLevel) {
        return null;
    }

    static {
        JITTER_COLOR = new Color(179, 59, 59, 255);
        JITTER_UNDER_COLOR = new Color(179, 59, 95, 255);
        AFTERIMAGE_COLOR = new Color(179, 59, 59, 50);
        EXPLOSION_COLOR = new Color(160, 55, 64);
        EMP_COLOR = new Color(112, 31, 204);
        LENS_FLARE_OUTER_COLOR = new Color(150, 37, 25, 255);
        LENS_FLARE_CORE_COLOR = new Color(255, 122, 122, 250);
        EXPLOSION_DAMAGE_TYPE = DamageType.HIGH_EXPLOSIVE;
    }


}
