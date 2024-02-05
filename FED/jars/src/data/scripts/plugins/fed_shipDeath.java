
// Script horribly brutalized from Blackrock with permission from Cyc/DR

package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SoundPlayerAPI;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.input.InputEventAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.CollisionUtils;
//import org.dark.shaders.distortion.DistortionShader;
//import org.dark.shaders.distortion.RippleDistortion;
//import org.dark.shaders.light.LightShader;
//import org.dark.shaders.light.StandardLight;
//import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
//import org.dark.graphics.plugins.ShipDestructionEffects;

public class fed_shipDeath extends BaseEveryFrameCombatPlugin {

    public static final Logger LOGGER = Global.getLogger(fed_shipDeath.class);
    
    private static final Set<String> APPLICABLE_SHIPS = new HashSet<>(36);

    private static final Color COLOR_EMP_CORE = new Color(155, 130, 20, 205);
    private static final Color COLOR_EMP_FRINGE = new Color(50, 45, 45, 255);
    private static final Color COLOR_PARTICLE = new Color(209, 159, 21);
    private static final Color COLOR_SUPERBRITE = new Color(255, 255, 230);

    private static final Map<String, Float> CORE_OFFSET = new HashMap<>(36);

    private static final String DATA_KEY = "fed_shipDeath";

    private static final Map<HullSize, Float> EXPLOSION_AREA_INCREASE = new HashMap<>(5);
    private static final Map<HullSize, Float> EXPLOSION_INTENSITY = new HashMap<>(5);
    private static final Map<HullSize, Float> EXPLOSION_LENGTH = new HashMap<>(5);
    private static final Map<HullSize, Float> PITCH_BEND = new HashMap<>(5);
    private static final Map<HullSize, Float> PITCH_BEND_CHARGEUP = new HashMap<>(5);

    private static final Vector2f ZERO = new Vector2f();

    static {
        APPLICABLE_SHIPS.add("fed_boss");
        APPLICABLE_SHIPS.add("fed_superkestrel");
        APPLICABLE_SHIPS.add("fed_byte");
        APPLICABLE_SHIPS.add("fed_cormorant");
        APPLICABLE_SHIPS.add("fed_cormorant_old");
        APPLICABLE_SHIPS.add("fed_medcombtanker");
        APPLICABLE_SHIPS.add("fed_flagship");
        APPLICABLE_SHIPS.add("fed_flagship_assault");
        APPLICABLE_SHIPS.add("fed_kestral");
        APPLICABLE_SHIPS.add("fed_lightdestroyer");
        APPLICABLE_SHIPS.add("fed_osprey");
        APPLICABLE_SHIPS.add("fed_raptor");
        APPLICABLE_SHIPS.add("fed_rigger");
        APPLICABLE_SHIPS.add("fed_rigger_firesupport");
        APPLICABLE_SHIPS.add("fed_stealth");
        APPLICABLE_SHIPS.add("fed_talos");
        APPLICABLE_SHIPS.add("fed_tern");
        APPLICABLE_SHIPS.add("fed_heavyfreighter");
        APPLICABLE_SHIPS.add("fed_mantis_cruiser");
        APPLICABLE_SHIPS.add("fed_cruisercarrier");
        APPLICABLE_SHIPS.add("fed_cargocap");
        APPLICABLE_SHIPS.add("fed_colubris");
        APPLICABLE_SHIPS.add("fed_nisos");
        APPLICABLE_SHIPS.add("fed_superbuffalo");
        APPLICABLE_SHIPS.add("fed_superkite");
        APPLICABLE_SHIPS.add("fed_superdestroyer");
        APPLICABLE_SHIPS.add("fed_superdestroyer_pirate");
        APPLICABLE_SHIPS.add("fed_shivan");
        APPLICABLE_SHIPS.add("fed_stormwalker");
        APPLICABLE_SHIPS.add("fed_polyp");
        APPLICABLE_SHIPS.add("fed_cormorant_old_pirate");
        APPLICABLE_SHIPS.add("fed_lightdestroyer_p");
        APPLICABLE_SHIPS.add("fed_tern_pirate");
        APPLICABLE_SHIPS.add("fed_boss_rebel");
        APPLICABLE_SHIPS.add("fed_legion");

        CORE_OFFSET.put("fed_boss", 0f);
        CORE_OFFSET.put("fed_superkestrel", 0f);
        CORE_OFFSET.put("fed_byte", 0f);
        CORE_OFFSET.put("fed_cormorant", 0f);
        CORE_OFFSET.put("fed_cormorant_old", 0f);
        CORE_OFFSET.put("fed_medcombtanker", 0f);
        CORE_OFFSET.put("fed_flagship", 0f);
        CORE_OFFSET.put("fed_flagship_assault", 0f);
        CORE_OFFSET.put("fed_kestral", 0f);
        CORE_OFFSET.put("fed_lightdestroyer", 0f);
        CORE_OFFSET.put("fed_osprey", 0f);
        CORE_OFFSET.put("fed_raptor", 0f);
        CORE_OFFSET.put("fed_rigger", 0f);
        CORE_OFFSET.put("fed_rigger_firesupport", 0f);
        CORE_OFFSET.put("fed_stealth", 0f);
        CORE_OFFSET.put("fed_talos", 0f);
        CORE_OFFSET.put("fed_tern", 0f);
        CORE_OFFSET.put("fed_heavyfreighter", 0f);
        CORE_OFFSET.put("fed_mantis_cruiser", 0f);
        CORE_OFFSET.put("fed_cruisercarrier", 0f);
        CORE_OFFSET.put("fed_cargocap", 0f);
        CORE_OFFSET.put("fed_colubris", 0f);
        CORE_OFFSET.put("fed_nisos", 0f);
        CORE_OFFSET.put("fed_superbuffalo", 0f);
        CORE_OFFSET.put("fed_superkite", 0f);
        CORE_OFFSET.put("fed_superdestroyer", 0f);
        CORE_OFFSET.put("fed_superdestroyer_pirate", 0f);
        CORE_OFFSET.put("fed_shivan", 0f);
        CORE_OFFSET.put("fed_stormwalker", 0f);
        CORE_OFFSET.put("fed_polyp", 0f);
        CORE_OFFSET.put("fed_cormorant_old_pirate", 0f);
        CORE_OFFSET.put("fed_lightdestroyer_p", 0f);
        CORE_OFFSET.put("fed_tern_pirate", 0f);
        CORE_OFFSET.put("fed_boss_rebel", 0f);
        CORE_OFFSET.put("fed_legion", 0f);
    }

    static {
        EXPLOSION_LENGTH.put(HullSize.FIGHTER, 1.5f);
        EXPLOSION_INTENSITY.put(HullSize.FIGHTER, 0.5f);
        EXPLOSION_AREA_INCREASE.put(HullSize.FIGHTER, 0f);
        PITCH_BEND.put(HullSize.FIGHTER, 1.2f);
        PITCH_BEND_CHARGEUP.put(HullSize.FIGHTER, 1.2f);

        EXPLOSION_LENGTH.put(HullSize.FRIGATE, 3.5f);
        EXPLOSION_INTENSITY.put(HullSize.FRIGATE, 0.7f);
        EXPLOSION_AREA_INCREASE.put(HullSize.FRIGATE, 1.5f);
        PITCH_BEND.put(HullSize.FRIGATE, 0.9f);
        PITCH_BEND_CHARGEUP.put(HullSize.FRIGATE, 0.6f);

        EXPLOSION_LENGTH.put(HullSize.DESTROYER, 5.75f);
        EXPLOSION_INTENSITY.put(HullSize.DESTROYER, 1.05f);
        EXPLOSION_AREA_INCREASE.put(HullSize.DESTROYER, 2f);
        PITCH_BEND.put(HullSize.DESTROYER, 1f);
        PITCH_BEND_CHARGEUP.put(HullSize.DESTROYER, .37f);

        EXPLOSION_LENGTH.put(HullSize.CRUISER, 7.5f);
        EXPLOSION_INTENSITY.put(HullSize.CRUISER, 1.3f);
        EXPLOSION_AREA_INCREASE.put(HullSize.CRUISER, 2.5f);
        PITCH_BEND.put(HullSize.CRUISER, 0.92f);
        PITCH_BEND_CHARGEUP.put(HullSize.CRUISER, 0.29f);

        EXPLOSION_LENGTH.put(HullSize.CAPITAL_SHIP, 8.6f);
        EXPLOSION_INTENSITY.put(HullSize.CAPITAL_SHIP, 1.4f);
        EXPLOSION_AREA_INCREASE.put(HullSize.CAPITAL_SHIP, 3f);
        PITCH_BEND.put(HullSize.CAPITAL_SHIP, 0.85f);
        PITCH_BEND_CHARGEUP.put(HullSize.CAPITAL_SHIP, 0.26f);

        EXPLOSION_LENGTH.put(HullSize.DEFAULT, 7.5f);
        EXPLOSION_INTENSITY.put(HullSize.DEFAULT, 1f);
        EXPLOSION_AREA_INCREASE.put(HullSize.DEFAULT, 1.5f);
        PITCH_BEND.put(HullSize.DEFAULT, 1f);
        PITCH_BEND_CHARGEUP.put(HullSize.DEFAULT, 0.6f);
    }

    private static void explode(CombatEngineAPI engine, ExplodingShip exploder) {
        ShipAPI ship = exploder.ship;
        Vector2f shipLoc = MathUtils.getPointOnCircumference(ship.getLocation(), CORE_OFFSET.get(
                ship.getHullSpec().getHullId()), ship.getFacing());
        Vector2f shipVel = ship.getVelocity();
        ship.setOwner(ship.getOriginalOwner());

        float explosionTime = EXPLOSION_LENGTH.get(ship.getHullSize());
        float area = EXPLOSION_AREA_INCREASE.get(ship.getHullSize()) + ship.getCollisionRadius();

        engine.spawnExplosion(shipLoc, shipVel, COLOR_EMP_CORE, area * 5f, explosionTime * 0.3f);
        engine.spawnExplosion(shipLoc, shipVel, COLOR_EMP_FRINGE, area * 1.2f, explosionTime * 1.25f);
        engine.addHitParticle(shipLoc, shipVel, area * 2.5f, 1f, 0.05f * explosionTime, COLOR_EMP_CORE);
        engine.addHitParticle(shipLoc, shipVel, area * 0.125f, 1f, explosionTime * 0.75f, COLOR_EMP_FRINGE);
        engine.addHitParticle(shipLoc, shipVel, area * 0.25f, 1f, explosionTime, COLOR_EMP_CORE);
        engine.addHitParticle(shipLoc, shipVel, area * 0.50f, 1f, explosionTime * 1.25f, COLOR_EMP_FRINGE);
        engine.addSmoothParticle(shipLoc, shipVel, area * 1.5f, 0.1f, explosionTime * 1.5f, COLOR_EMP_FRINGE);
        engine.spawnExplosion(shipLoc, shipVel, COLOR_SUPERBRITE, area * 3f, explosionTime * 0.1f);
        //AnamorphicFlare.createFlare(ship, new Vector2f(shipLoc), engine, 1f, 0.04f / EXPLOSION_INTENSITY.get(
        //       ship.getHullSize()), 0f, 0f, 2f,
        //       COLOR_PARTICLE, COLOR_ATTACHED_LIGHT);
        for (int i = 0; i <= (int) ship.getCollisionRadius() * EXPLOSION_INTENSITY.get(ship.getHullSize()); i++) {
            float radius = ship.getCollisionRadius() * (float) Math.random() * 0.25f;
            Vector2f direction = MathUtils.getRandomPointOnCircumference(null, ship.getCollisionRadius()
                    * ((float) Math.random() * 0.75f + 0.25f)
                    * EXPLOSION_INTENSITY.get(ship.getHullSize()));
            Vector2f point = MathUtils.getPointOnCircumference(shipLoc, radius, VectorUtils.getFacing(direction));
            engine.addHitParticle(point, direction, 1f, 1f, (1f + (float) Math.random()) * (float) Math.sqrt(
                    EXPLOSION_LENGTH.get(ship.getHullSize())),
                    COLOR_PARTICLE);
        }

        //StandardLight light = new StandardLight(shipLoc, ZERO, ZERO, null);
        //light.setColor(COLOR_ATTACHED_LIGHT);
        //light.setSize(area * 1.5f);
        //light.setIntensity(1f * EXPLOSION_INTENSITY.get(ship.getHullSize()));
        //light.fadeOut(explosionTime);
        //LightShader.addLight(light);
        //float time = EXPLOSION_INTENSITY.get(ship.getHullSize());
        //RippleDistortion ripple = new RippleDistortion(shipLoc, ZERO);
        //ripple.setSize(area);
        //ripple.setIntensity(100f * EXPLOSION_INTENSITY.get(ship.getHullSize()));
        //ripple.setFrameRate(60f / (time));
        //ripple.fadeInSize(time);
        //ripple.fadeOutIntensity(time);
        //DistortionShader.addDistortion(ripple);
        switch (ship.getHullSize()) {
            case FRIGATE:
                Global.getSoundPlayer().playSound("fed_shatter_small", PITCH_BEND.get(ship.getHullSize()),
                        EXPLOSION_INTENSITY.get(ship.getHullSize()),
                        shipLoc, ZERO);
                break;
            case DESTROYER:
                Global.getSoundPlayer().playSound("fed_shatter_large", PITCH_BEND.get(ship.getHullSize()),
                        EXPLOSION_INTENSITY.get(ship.getHullSize()),
                        shipLoc, ZERO);
                break;
            case CRUISER:
                Global.getSoundPlayer().playSound("fed_shatter_large", PITCH_BEND.get(ship.getHullSize()),
                        EXPLOSION_INTENSITY.get(ship.getHullSize()),
                        shipLoc, ZERO);
                break;
            case CAPITAL_SHIP:
                Global.getSoundPlayer().playSound("fed_shatter_capital", PITCH_BEND.get(ship.getHullSize()),
                        EXPLOSION_INTENSITY.get(ship.getHullSize()),
                        shipLoc, ZERO);
            default:
                break;
        }

        ship.setOwner(100);
        //for(int i = 0; i < ship.getHullSpec().getMinPieces(); i++){
        //    org.dark.graphics.plugins.ShipDestructionEffects.suppressEffects(ship, true, true);
        //    ship.splitShip();
        //}
        Set<ShipAPI> pieces = new HashSet<>(15);
        Set<ShipAPI> newPieces = new HashSet<>(15);
        pieces.add(ship);
        float count = EXPLOSION_LENGTH.get(ship.getHullSize()) * 2f;
        while (!pieces.isEmpty() && (count > 0)) {
            for (ShipAPI piece : pieces) {
                ShipAPI newPiece = piece.splitShip();
                //ShipDestructionEffects.suppressEffects(newPiece, true, true);
                //if (newPiece != null) {
                //    newPieces.add(newPiece);
                //   ShipDestructionEffects.suppressEffects(newPiece, true, true);
                //}
                count = count - 1;
                if (count <= 0) {
                    break;
                }
            }

            pieces.addAll(newPieces);
            newPieces.clear();
            count--;
        }

    }

    private CombatEngineAPI engine;
    private SoundPlayerAPI sound;

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine == null) {
            return;
        }

        if (engine.isPaused()) {
            return;
        }

        final LocalData localData = (LocalData) engine.getCustomData().get(DATA_KEY);
        final Set<ShipAPI> deadShips = localData.deadShips;
        final List<ExplodingShip> explodingShips = localData.explodingShips;
        final Set<ShipAPI> nonExplodingHulks = localData.nonExplodingHulks;

        List<ShipAPI> ships = engine.getShips();
        int shipsSize = ships.size();
        for (int i = 0; i < shipsSize; i++) {
            ShipAPI ship = ships.get(i);
            if (ship == null) {
                continue;
            }

            if (ship.isHulk() && !ship.isPiece()) {
                if (!APPLICABLE_SHIPS.contains(ship.getHullSpec().getHullId()) || nonExplodingHulks.contains(ship)) {
                    continue;
                }

                if (!deadShips.contains(ship) && !nonExplodingHulks.contains(ship)) {
                    if (Math.random() > ship.getHullSpec().getBreakProb()) {
                        nonExplodingHulks.add(ship);
                        LOGGER.info("fed_shipDeath: Non-exploding hull " + ship.getId() + ", " + ship.getName() + "added.");
                    } else {
                        deadShips.add(ship);
                        Vector2f shipLoc = ship.getLocation();
                        float chargingTime = EXPLOSION_LENGTH.get(ship.getHullSize());
                        LOGGER.info("fed_shipDeath: Critical-load on " + ship.getId() + ", " + ship.getName() + ", exploding in:" + chargingTime);

                        switch (ship.getHullSize()) {
                            case FRIGATE:
                                Global.getSoundPlayer().playSound("fed_artbeam_charge", PITCH_BEND_CHARGEUP.get(ship.getHullSize()),
                                        EXPLOSION_INTENSITY.get(ship.getHullSize())*1.25f,
                                        shipLoc, ship.getVelocity());
                                break;
                            case DESTROYER:
                                Global.getSoundPlayer().playSound("fed_artbeam_charge", PITCH_BEND_CHARGEUP.get(ship.getHullSize()),
                                        EXPLOSION_INTENSITY.get(ship.getHullSize())*1.5f,
                                        shipLoc, ship.getVelocity());
                                break;
                            case CRUISER:
                                Global.getSoundPlayer().playSound("fed_artbeam_charge", PITCH_BEND_CHARGEUP.get(ship.getHullSize()),
                                        EXPLOSION_INTENSITY.get(ship.getHullSize())*1.75f,
                                        shipLoc, ship.getVelocity());
                                break;
                            case CAPITAL_SHIP:
                                Global.getSoundPlayer().playSound("fed_artbeam_charge", PITCH_BEND_CHARGEUP.get(ship.getHullSize()),
                                        EXPLOSION_INTENSITY.get(ship.getHullSize())*2f,
                                        shipLoc, ship.getVelocity());
                            default:
                                break;
                        }
                        ExplodingShip exploder = new ExplodingShip(ship, chargingTime + (org.lazywizard.lazylib.MathUtils.getRandomNumberInRange(-0.4f, 0.1f)));
                        explodingShips.add(exploder);
                    }

                    //Vector2f shipLoc = MathUtils.getPointOnCircumference(ship.getLocation(), CORE_OFFSET.get(
                    //        ship.getHullSpec().getHullId()),
                    //       ship.getFacing());
                    //StandardLight light = new StandardLight(ZERO, ZERO, ZERO, ship);
                    //light.setColor(COLOR_ATTACHED_LIGHT);
                    //light.setSize(ship.getCollisionRadius() * 2f);
                    //light.setIntensity(EXPLOSION_INTENSITY.get(ship.getHullSize()));
                    //light.fadeIn(chargingTime);
                    //light.setLifetime(0f);
                    //LightShader.addLight(light);
                }
            }
        }

        Iterator<ShipAPI> iter = deadShips.iterator();
        while (iter.hasNext()) {
            ShipAPI ship = iter.next();

            if (ship != null && !ships.contains(ship)) {
                iter.remove();
            }
        }

        Iterator<ExplodingShip> iter2 = explodingShips.iterator();
        while (iter2.hasNext()) {
            ExplodingShip exploder = iter2.next();
            ShipAPI ship = exploder.ship;

            if (ship == null || !engine.getShips().contains(exploder.ship) || ship.isPiece()) {
                explode(engine, exploder);
                iter2.remove();
                continue;

            } else {

                exploder.chargeLevel += amount * ship.getMutableStats().getTimeMult().getModifiedValue()
                        / exploder.chargingTime;
                Vector2f shipLoc = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius() * 0.8f);

                if (MathUtils.getRandomNumberInRange(0.25f, exploder.chargingTime) < exploder.chargeLevel) {
                    float explosion_size = 25 + 125f * (exploder.chargeLevel);
                    float explosion_duration = org.lazywizard.lazylib.MathUtils.getRandomNumberInRange(0.6f, 0.8f);
                    if (CollisionUtils.isPointWithinBounds(shipLoc, ship)) {
                        engine.spawnExplosion(shipLoc, ZERO, COLOR_EMP_FRINGE, explosion_size + 10f, explosion_duration * 2f);
                        engine.spawnExplosion(shipLoc, ZERO, COLOR_EMP_CORE, explosion_size, explosion_duration);
                        engine.spawnExplosion(shipLoc, ZERO, COLOR_SUPERBRITE, explosion_size / 2, explosion_duration - 0.2f);
                    }

                    //sound.playSound("explosion_from_damage", 1f, 1f, shipLoc, new Vector2f(0,0));
                }
                int new_alpha = 1 + (int) (253 * exploder.chargeLevel);
                if (new_alpha >= 254) {
                    new_alpha = 255;
                }
                Color shipDetColor = new Color(255, 188, 31, new_alpha);
                float scalingEffect = 8f + (25f * exploder.chargeLevel);

                ship.addAfterimage(
                        shipDetColor, //color
                        0f, //X Location rel to ship center
                        0f, //Y Location rel to ship center
                        ship.getVelocity().getX(), //X Velocity
                        ship.getVelocity().getY(), //Y Velocity
                        scalingEffect, //Max Jitter, units of distance
                        0f, //Fade-in time
                        0f, //Regular duration
                        0.1f, //Fade-out time
                        true, //Is Additive (whiteness adds up)
                        false, //Combine with sprite color
                        true);
            }

            if (exploder.chargeLevel >= 1f) {
                ship.setAngularVelocity(ship.getAngularVelocity() * (org.lazywizard.lazylib.MathUtils.getRandomNumberInRange(90f, 120f) / 100f) + org.lazywizard.lazylib.MathUtils.getRandomNumberInRange(-60f, 60f));
                ship.getVelocity().scale((org.lazywizard.lazylib.MathUtils.getRandomNumberInRange(90f, 120f) / 100f));
                explode(engine, exploder);
                iter2.remove();
            }

        }
    }

    @Override
    public void init(CombatEngineAPI engine) {
        
        this.engine = engine;
        Global.getCombatEngine().getCustomData().put(DATA_KEY, new LocalData());
        LOGGER.info("Star Federation fed_shipDeath: init successful");
    }

    private static final class ExplodingShip{

        float chargeLevel;
        float chargingTime;
        ShipAPI ship;

        private ExplodingShip(ShipAPI ship, float chargingTime) {
            this.ship = ship;
            this.chargingTime = chargingTime;
            this.chargeLevel = 0f;
        }
    }

    private static final class LocalData {

        final Set<ShipAPI> deadShips = new LinkedHashSet<>(50);
        final Set<ShipAPI> nonExplodingHulks = new LinkedHashSet<>(50);
        final List<ExplodingShip> explodingShips = new ArrayList<>(50);

    }
}

