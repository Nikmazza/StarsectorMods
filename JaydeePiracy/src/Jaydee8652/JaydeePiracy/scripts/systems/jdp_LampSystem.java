package Jaydee8652.JaydeePiracy.scripts.systems;

import Jaydee8652.JaydeePiracy.campaign.entities.jdp_FusionLampEntityPlugin;
import Jaydee8652.JaydeePiracy.campaign.entities.jdp_MissileEntityPlugin;
import Jaydee8652.JaydeePiracy.scripts.systems.combat_lamp_render.*;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.Description;
import com.fs.starfarer.api.loading.TerrainSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class jdp_LampSystem extends BaseShipSystemScript implements jdp_CombatPulsarRenderer.CombatPulsarRendererDelegate {
    public static final Color JITTER_COLOR = new Color(255,165,90,55);
    public static final Color JITTER_UNDER_COLOR = new Color(255,165,90,155);

    float maxWindBurn = 1f;


    //apply, getStatusData, unapply
    public boolean inited = false;
    public ShipAPI ship = null;
    protected State state = null;

    //BaseTerrain
    public static final float EXTRA_SOUND_RADIUS = 100f;

    protected CombatEntityAPI entity;
    protected String terrainId = "Pulsar Wave";
    protected String name = "Pulsar Wave";

    //Terrain plugin
    public float PULSAR_ARC = 90f; //1 / ((float) Math.PI * 2f) * 360f;
    public static final float PULSAR_LENGTH = 8000f; //1 / ((float) Math.PI * 2f) * 360f;
    public float fxMult = 1f;
    public boolean single = false;
    public String nameTooltip = "Pulsar Wave";

    protected SpriteAPI flareTexture = Global.getSettings().getSprite("terrain", "pulsar");
    protected SpriteAPI starTexture = Global.getSettings().getSprite("glows", "jdp_lampstar");

    protected final SpriteAPI auroraTexture = null;
    protected BaseCombatLayeredRenderingPlugin CombatLayer;
    Color color = null;

    protected jdp_CombatPulsarRenderer flare1, flare2;
    protected jdp_CombatStarRenderer star;


    protected jdp_CombatPulsarCorona.CombatCoronaParams params;
    protected jdp_CombatRangeBlockerUtil blocker = null; //new CombatRangeBlockerUtil(1400, PULSAR_LENGTH);

    protected float pulsarAngle = 60f;
    protected final float pulsarRotation = -1f * (3f);

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        float amount = Global.getCombatEngine().getElapsedInLastFrame();
        this.entity = stats.getEntity();
        this.state = state;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            id = id + "_" + ship.getId();
            init();
        } else {
            return;
        }
        if (!ship.isAlive() || ship.isHulk()) return;


        pulsarAngle += pulsarRotation * amount * 1f;
        pulsarAngle = Misc.normalizeAngle(pulsarAngle);

        flare1.advance(amount);
        if (!single) flare2.advance(amount);

        flare1.setCurrAngle(pulsarAngle);
        if (!single) flare2.setCurrAngle(pulsarAngle + 180f);

        if (amount > 0 && blocker != null) {
            blocker.updateLimits(entity, params.relatedEntity, 0.5f);
            //blocker.sync();
            blocker.advance(amount, 100f, 0.5f);
        }

        render(CombatEngineLayers.BELOW_SHIPS_LAYER, Global.getCombatEngine().getViewport());
        /*
    "jitterUnderColor":[255,165,90,155],
	"jitterUnderCopies":25,
	"jitterUnderMinRange":0,
	"jitterUnderRange":7,
	"jitterUnderRangeRadiusFraction":0,

	"jitterColor":[255,165,90,55],
	"jitterCopies":2,
	"jitterMinRange":0,
	"jitterRange":5,
	"jitterRangeRadiusFraction":0,
         */
        ship.setJitterUnder(this, JITTER_UNDER_COLOR, 1f, 25, 0f, 7f);
        ship.setJitter(this, JITTER_COLOR, 1f, 2, 0f, 5f);

        fxMult = 0.2f;
        if (state == State.ACTIVE && !ship.isPhased()) {
            fxMult = 0.65f;
            applyEffect(ship, amount);
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        super.unapply(stats, id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return super.getStatusData(index, state, effectLevel);
    }

    public void init() {
        if (!inited) {
            this.params = new jdp_CombatPulsarCorona.CombatCoronaParams(PULSAR_LENGTH*2, ship.getCollisionRadius()+50f, ship, 500f, 1f, 0f);
            this.single = false;
            if (blocker == null) {
                blocker = new jdp_CombatRangeBlockerUtil(2000, PULSAR_LENGTH*2f);
            }
            name = "Erratic Lamp";
            params.name = "Erratic Lamp";
            nameTooltip = "Erratic Lamp";

            flareTexture = Global.getSettings().getSprite("terrain", "pulsar");
            starTexture = Global.getSettings().getSprite("glows", "jdp_lampstar");

            flare1 = new jdp_CombatPulsarRenderer(this);
            flare2 = new jdp_CombatPulsarRenderer(this);
            star = new jdp_CombatStarRenderer(90000000, 80, 80, 80, 80, 80, starTexture, true);
            Global.getCombatEngine().addLayeredRenderingPlugin(flare1);
            Global.getCombatEngine().addLayeredRenderingPlugin(flare2);
            Global.getCombatEngine().addLayeredRenderingPlugin(star);

            inited = true;
        }
    }

    public float getFXMult() {
        return fxMult;
    }

    public void render(CombatEngineLayers layer, ViewportAPI viewport) {
        if (blocker != null && !blocker.wasEverUpdated()) {
            blocker.updateAndSync(entity, params.relatedEntity, 1f);
        }

      //  if (isNearViewport(pulsarAngle, viewport)) {
            flare1.render(layer, viewport); //viewport.getAlphaMult()
       // }

//if (!single && isNearViewport(pulsarAngle + 180f, viewport)) {
            flare2.render(layer, viewport);
     //   }

        star.ponder(Color.white, ship.getShieldCenterEvenIfNoShield());
    }

    protected boolean isNearViewport(float angle, ViewportAPI viewport) {
        float wClose = getPulsarInnerWidth();
        float wFar = getPulsarOuterWidth();
        float distClose = getPulsarInnerRadius();
        float distFar = getPulsarOuterRadius();

        float length = distFar - distClose;
        float incr = (float) Math.ceil((distFar - distClose) / 2000f);

        for (float dist = wClose; dist < distFar; dist += incr) {
            Vector2f test = Misc.getUnitVectorAtDegreeAngle(angle);
            test.scale(dist);
            Vector2f.add(test, entity.getLocation(), test);

            float testDist = wClose + (wFar - wClose) * (dist - distClose) / length;
            testDist *= 0.5f;
            if (viewport.isNearViewport(test, testDist + 500f)) {
                return true;
            }
        }
        return false;
    }

    public void applyEffect(CombatEntityAPI entity, float amount) {
        if (entity instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) entity;

            List<ShipAPI> ships = Global.getCombatEngine().getShips();
            ships.remove(ship);

            for (ShipAPI other : ships) {
                if (other != null) {
                    if (other.isPhased()) continue;
                    float intensity = getIntensityAtPoint(other.getLocation());
                    if (intensity <= 0) return;

                    other.getMutableStats().getCRLossPerSecondPercent().modifyMult("PulsarBeam", 3f * intensity, "Lampburn");
                    other.getFluxTracker().increaseFlux(amount * 2000f * intensity, true);
                    other.getMutableStats().getEngineMalfunctionChance().modifyMult("PulsarBeam", 5f * intensity);

                    // "wind" effect - adjust velocity
                    float maxSpeed = other.getMaxSpeed();
                    float currSpeed = other.getVelocity().length();

                    float currWindBurn = intensity * maxWindBurn;
                    float maxFleetBurnIntoWind = maxSpeed - Math.abs(currWindBurn);

                    float angle = Misc.getAngleInDegreesStrict(this.entity.getLocation(), other.getLocation());
                    Vector2f windDir = Misc.getUnitVectorAtDegreeAngle(angle);
                    if (currWindBurn < 0) {
                        windDir.negate();
                    }

                    Vector2f velDir = Misc.normalise(new Vector2f(other.getVelocity()));
                    velDir.scale(currSpeed);

                    float fleetBurnAgainstWind = -1f * Vector2f.dot(windDir, velDir);

                    float accelMult = 0.5f;
                    if (fleetBurnAgainstWind > maxFleetBurnIntoWind) {
                        accelMult += 0.75f + 0.25f * (fleetBurnAgainstWind - maxFleetBurnIntoWind);
                    }

                    Vector2f vel = other.getVelocity();
                    windDir.scale(intensity * 500f);
                    other.getVelocity().set(vel.x + windDir.x, vel.y + windDir.y);
                    Color glowColor = getPulsarColorForAngle(angle);
                    int alpha = glowColor.getAlpha();
                    if (alpha < 75) {
                        glowColor = Misc.setAlpha(glowColor, 75);
                    }

                    Misc.normalise(windDir);
                }
            }

            for (MissileAPI missile : Global.getCombatEngine().getMissiles()) {
                if (missile instanceof MissileAPI) {
                    float intensity = getIntensityAtPoint(missile.getLocation()) / 2f;
                    if (intensity <= 0) return;

                    if (missile.getOwner() != ship.getOwner()) {
                        if (missile.getMissileAI() instanceof GuidedMissileAI) {
                            ((GuidedMissileAI) missile.getMissileAI()).setTarget(missile.getSource());
                        }
                        missile.setOwner(ship.getOwner());
                        missile.setSource(ship);
                    }

                    // "wind" effect - adjust velocity
                    float maxSpeed = missile.getMaxSpeed();
                    float currSpeed = missile.getVelocity().length();

                    float currWindBurn = intensity * maxWindBurn;
                    float maxFleetBurnIntoWind = maxSpeed - Math.abs(currWindBurn);

                    float angle = Misc.getAngleInDegreesStrict(this.entity.getLocation(), missile.getLocation());
                    Vector2f windDir = Misc.getUnitVectorAtDegreeAngle(angle);
                    if (currWindBurn < 0) {
                        windDir.negate();
                    }

                    Vector2f velDir = Misc.normalise(new Vector2f(missile.getVelocity()));
                    velDir.scale(currSpeed);

                    float fleetBurnAgainstWind = -1f * Vector2f.dot(windDir, velDir);

                    float accelMult = 0.5f;
                    if (fleetBurnAgainstWind > maxFleetBurnIntoWind) {
                        accelMult += 0.75f + 0.25f * (fleetBurnAgainstWind - maxFleetBurnIntoWind);
                    }

                    Vector2f vel = missile.getVelocity();
                    windDir.scale(intensity * 500f);
                    missile.getVelocity().set(vel.x + windDir.x, vel.y + windDir.y);

                    Color glowColor = getPulsarColorForAngle(angle);
                    int alpha = glowColor.getAlpha();
                    if (alpha < 75) {
                        glowColor = Misc.setAlpha(glowColor, 75);
                    }
                }
            }
        }
    }

    public float getIntensityAtPoint(Vector2f point) {
        float maxDist = params.bandWidthInEngine;
        float minDist = params.relatedEntity.getCollisionRadius();
        float dist = Misc.getDistance(point, params.relatedEntity.getLocation());

        if (dist > maxDist) return 0f;

        float intensity = 1f;
        if (minDist < maxDist) {
            intensity = 1f - (dist - minDist) / (maxDist - minDist);
            //intensity = 0.5f + intensity * 0.5f;
            if (intensity < 0) intensity = 0;
            if (intensity > 1) intensity = 1;
        }

        float angle = Misc.getAngleInDegreesStrict(params.relatedEntity.getLocation(), point);
        float diff = Misc.getAngleDiff(angle, pulsarAngle);
        if (!single) diff = Math.min(diff, Misc.getAngleDiff(angle, pulsarAngle + 180f)); //+180f
        float maxDiff = PULSAR_ARC / 2f;

        if (diff > maxDiff) intensity = 0f;
         //else if (diff > maxDiff / 2) {
            //intensity *= 0.25f + 0.75f * (1f - (diff - maxDiff * 0.5f) / (maxDiff * 0.5f));
        //}

        return intensity;
    }

    public jdp_CombatRangeBlockerUtil getPulsarBlocker() {
        return blocker;
    }

    public Vector2f getPulsarCenterLoc() {
        return params.relatedEntity.getLocation();
    }

    public Color getPulsarColorForAngle(float angle) {
        if (color == null) {
            color = Misc.setAlpha(jdp_FusionLampEntityPlugin.GLOW_COLOR, 255);//200
        }
        return color;
    }

    public float getPulsarInnerRadius() {
        return params.relatedEntity.getCollisionRadius();
    }

    public float getPulsarOuterRadius() {
        return params.middleRadius + params.bandWidthInEngine * 0.5f;
    }

    public float getPulsarInnerWidth() {
        return PULSAR_ARC / 360f * 2f * (float) Math.PI * getPulsarInnerRadius();
    }

    public float getPulsarOuterWidth() {
        float r1 = getPulsarInnerRadius();
        float r2 = getPulsarOuterRadius();
        return getPulsarInnerWidth() * r2 / r1;
    }

    public float getPulsarScrollSpeed() {
        return 25f;
    }

    public SpriteAPI getPulsarTexture() {
        return flareTexture;
    }
}
