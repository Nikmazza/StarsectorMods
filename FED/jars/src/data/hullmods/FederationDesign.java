package data.hullmods;

import java.util.HashMap;
import java.util.Map;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class FederationDesign extends BaseHullMod {

    private CombatEngineAPI engine;

    private static final Map SHIELD_MIN_RADIUS_HMAP = new HashMap();
    private static final Map MIN_SHIELD_ARC_PERCENT = new HashMap();
    private static final Map AUX_SHIELDS_HMAP = new HashMap();
    private static final Map FLEETSHIELDS_HMAP = new HashMap();

    private static final float FED_ENERGY_RANGE_BONUS = 100f;
    private static final float FED_ZEROFLUX_BOOST = 15f;
    private static final float FED_ZEROFLUX_LEVEL = 0.01f;

    private static final float FED_ABSOLUTE_MIN_SHIELD_ARC = 60f;
    private static final float FED_SHIELD_MAX_ARC = 360f;

    private static final float FED_FIRERATE_PENALTY_PERCENT = 30f;
    private static final float FED_FIRERATE_BOOST_PERCENT = 40f;

    // Subtract from 1 to get the real value
    private static final float FED_BASE_FLUX_ROF_MAX = 0.25f;
    private static final float FED_RFC_FLUX_ROF_MAX = 0.50f;
    private static final float FED_SO_FLUX_ROF_MAX = 0.75f;

    private static String fedShieldIcon = "graphics/icons/hullsys/fortress_shield.png";
    private static String fedEngineIcon = "graphics/icons/hullsys/maneuvering_jets.png";
    private static String fedWeaponIcon = "graphics/icons/hullsys/ammo_feeder.png";

    private static String fed_design_shield1 = Global.getSettings().getString("star_federation", "fed_design_shield1");
    private static String fed_design_shield2 = Global.getSettings().getString("star_federation", "fed_design_shield2");
    private static String fed_design_shield3 = Global.getSettings().getString("star_federation", "fed_design_shield3");

    private static String fed_design_engine1 = Global.getSettings().getString("star_federation", "fed_design_engine1");
    private static String fed_design_engine2 = Global.getSettings().getString("star_federation", "fed_design_engine2");
    private static String fed_design_engine3 = Global.getSettings().getString("star_federation", "fed_design_engine3");
    private static String fed_design_engine4 = Global.getSettings().getString("star_federation", "fed_design_engine4");

    private static String fed_design_weapon1 = Global.getSettings().getString("star_federation", "fed_design_weapon1");
    private static String fed_design_weapon2 = Global.getSettings().getString("star_federation", "fed_design_weapon2");
    private static String fed_design_weapon3 = Global.getSettings().getString("star_federation", "fed_design_weapon3");
    private static String fed_design_weapon4 = Global.getSettings().getString("star_federation", "fed_design_weapon4");
    private static String fed_design_weapon5 = Global.getSettings().getString("star_federation", "fed_design_weapon5");

    private static String ballistic = Global.getSettings().getString("star_federation", "fed_design_ballistic");
    private static String energy = Global.getSettings().getString("star_federation", "fed_design_energy");

    protected static final float PARA_PAD = 15f;
    protected static final float SECTION_PAD = 10f;
    protected static final float INTERNAL_PAD = 4f;
    protected static final float INTERNAL_PARA_PAD = 4f;
    protected static final float BULLET_PAD = 3f;

    static {
        AUX_SHIELDS_HMAP.put("fed_boss_shieldemitter", 1f);
        AUX_SHIELDS_HMAP.put("fed_superkestrel_emitter", 1f);
        AUX_SHIELDS_HMAP.put("fed_flagship_sideshield", 1f);
        AUX_SHIELDS_HMAP.put("fed_raptor_modshield", 1f);
        AUX_SHIELDS_HMAP.put("fed_legion_emitter", 1f);
    }

    static {
        FLEETSHIELDS_HMAP.put("fed_cargocap_shieldemitter", 1f);
    }

    static {
        SHIELD_MIN_RADIUS_HMAP.put(HullSize.FIGHTER, 0f);
        SHIELD_MIN_RADIUS_HMAP.put(HullSize.FRIGATE, 0.50f);
        SHIELD_MIN_RADIUS_HMAP.put(HullSize.DESTROYER, 0.40f);
        SHIELD_MIN_RADIUS_HMAP.put(HullSize.CRUISER, 0.20f);
        SHIELD_MIN_RADIUS_HMAP.put(HullSize.CAPITAL_SHIP, 0.35f);
    }

    static {
        MIN_SHIELD_ARC_PERCENT.put(HullSize.FIGHTER, 0);
        MIN_SHIELD_ARC_PERCENT.put(HullSize.FRIGATE, 45);
        MIN_SHIELD_ARC_PERCENT.put(HullSize.DESTROYER, 50);
        MIN_SHIELD_ARC_PERCENT.put(HullSize.CRUISER, 55);
        MIN_SHIELD_ARC_PERCENT.put(HullSize.CAPITAL_SHIP, 60);
    }

    private static final Map SHIELD_GROWTH_SPEED_HMAP = new HashMap();

    static {
        SHIELD_GROWTH_SPEED_HMAP.put(HullSize.FIGHTER, 3f);
        SHIELD_GROWTH_SPEED_HMAP.put(HullSize.FRIGATE, 0.75f);
        SHIELD_GROWTH_SPEED_HMAP.put(HullSize.DESTROYER, 1f);
        SHIELD_GROWTH_SPEED_HMAP.put(HullSize.CRUISER, 1.25f);
        SHIELD_GROWTH_SPEED_HMAP.put(HullSize.CAPITAL_SHIP, 2.00f);
    }

    private static final Map SHIELD_RADIUS_GROWTH_SPEED_HMAP = new HashMap();

    static {
        SHIELD_RADIUS_GROWTH_SPEED_HMAP.put(HullSize.FIGHTER, 1f);
        SHIELD_RADIUS_GROWTH_SPEED_HMAP.put(HullSize.FRIGATE, 1.5f);
        SHIELD_RADIUS_GROWTH_SPEED_HMAP.put(HullSize.DESTROYER, 2f);
        SHIELD_RADIUS_GROWTH_SPEED_HMAP.put(HullSize.CRUISER, 2.5f);
        SHIELD_RADIUS_GROWTH_SPEED_HMAP.put(HullSize.CAPITAL_SHIP, 3.00f);
    }

    //MagicIncompatibleWarning MagicIncompatibleHullmods;
    //Don't really know how to use this, forgot string.csv syntax to make it right
    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        //All done programmatically
        return null;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {

        float HEIGHT = 50f;
        float PAD = 10f;
        Color YELLOW = new Color(241, 199, 0);

        tooltip.addSectionHeading("Lightweight Shield Emitter",
                Global.getSettings().getColor("tooltipTitleAndLightHighlightColor"), Global.getSettings().getDesignTypeColor("Star Federation").darker(), Alignment.TMID, PARA_PAD);

        TooltipMakerAPI fedShield = tooltip.beginImageWithText(fedShieldIcon, 64f);
        fedShield.addPara(fed_design_shield1, 0f, Misc.getNegativeHighlightColor(),
                "" + MIN_SHIELD_ARC_PERCENT.get(HullSize.FRIGATE) + "%",
                "" + MIN_SHIELD_ARC_PERCENT.get(HullSize.DESTROYER) + "%",
                "" + MIN_SHIELD_ARC_PERCENT.get(HullSize.CRUISER) + "%",
                "" + MIN_SHIELD_ARC_PERCENT.get(HullSize.CAPITAL_SHIP) + "%");
        fedShield.addPara(fed_design_shield2, 0f, YELLOW,
                "" + Math.round((Float) FED_ABSOLUTE_MIN_SHIELD_ARC));
        fedShield.addPara(fed_design_shield3, 0f, YELLOW,
                "Stabilized Shields", "half");
        tooltip.addImageWithText(SECTION_PAD);

        tooltip.addSectionHeading("Warp Core",
                Global.getSettings().getColor("tooltipTitleAndLightHighlightColor"), Global.getSettings().getDesignTypeColor("Star Federation").darker(), Alignment.TMID, PARA_PAD);
        TooltipMakerAPI fedEngine = tooltip.beginImageWithText(fedEngineIcon, 64f);
        fedEngine.addPara(fed_design_engine1, 0f, Misc.getPositiveHighlightColor(),
                "" + Math.round((Float) FED_ZEROFLUX_BOOST));
        //fedEngine.addPara(fed_design_engine2, 0f, Misc.getPositiveHighlightColor(),
                //"" + Math.round((Float) (FED_ZEROFLUX_LEVEL * 100)) + "%");
        fedEngine.addPara(fed_design_engine3, 0f, Misc.getPositiveHighlightColor(),
                "rapid entry/exit");
        fedEngine.addPara(fed_design_engine4, 0f, Misc.getNegativeHighlightColor(),
                "shatter the hull");
        tooltip.addImageWithText(SECTION_PAD);

        tooltip.addSectionHeading("Gunnery Load Balancer",
                Global.getSettings().getColor("tooltipTitleAndLightHighlightColor"), Global.getSettings().getDesignTypeColor("Star Federation").darker(), Alignment.TMID, PARA_PAD);
        TooltipMakerAPI fedWeapon = tooltip.beginImageWithText(fedWeaponIcon, 64f);
        fedWeapon.addPara(fed_design_weapon1, 0f, Misc.getNegativeHighlightColor(),
                "" + Math.round((Float) ((100f - FED_FIRERATE_PENALTY_PERCENT))) + "% at 0% flux");
        fedWeapon.addPara(fed_design_weapon2, 0f, Misc.getPositiveHighlightColor(),
                "" + Math.round((Float) ((100f - FED_FIRERATE_PENALTY_PERCENT + FED_FIRERATE_BOOST_PERCENT))) + "% at " + Math.round((Float) ((1f - FED_BASE_FLUX_ROF_MAX) * 100)) + "% flux");
        fedWeapon.addPara(fed_design_weapon3, 0f, Misc.getPositiveHighlightColor(),
                "" + Math.round((Float) ((1f - FED_RFC_FLUX_ROF_MAX) * 100)) + "%");
        fedWeapon.addPara(fed_design_weapon4, 0f, Misc.getPositiveHighlightColor(),
                "" + Math.round((Float) ((1f - FED_SO_FLUX_ROF_MAX) * 100)) + "%");
        fedWeapon.addPara(fed_design_weapon5, 0f, YELLOW,
                "100%");
        tooltip.addImageWithText(SECTION_PAD);

        //LabelAPI bullet;
        //tooltip.setBulletedListMode("    • ");
        //bullet = tooltip.addPara("string_id_here", BULLET_PAD, Global.getSettings().getColor("standardTextColor"), Misc.getPositiveHighlightColor(), "+ stuff"  + "%");
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        stats.getZeroFluxSpeedBoost().modifyFlat(id, FED_ZEROFLUX_BOOST);
        //stats.getZeroFluxMinimumFluxLevel().modifyFlat(id, FED_ZEROFLUX_LEVEL);
        //stats.getEnergyWeaponRangeBonus().modifyFlat(id, FED_ENERGY_RANGE_BONUS);
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        engine = Global.getCombatEngine();

        if (engine == null) {
            return;
        }
        if (engine.isPaused()) {
            return;
        }
        engine.getCustomData().put(ship.getFleetMemberId() + "_usedTravelDrive", true);
        engine.getCustomData().put(ship.getFleetMemberId() + "_usedEscapeDriveDrive", true);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        //method below to save space - turns on hidden engines for zeroflux boost.
        fedEngineBoostVisuals(ship);

        Boolean validPlayerShip = false;
        if (ship == Global.getCombatEngine().getPlayerShip()) {
            validPlayerShip = true;
        }

//        if (ship.getOwner() == 0) {
//            if (Math.random() >= 0.95d) {
//                //engine.addFloatingText(ship.getLocation(), "Location Y: " + ship.getLocation().y, 15f, Color.red, ship, 1f, 0.5f);
//                //engine.addFloatingText(ship.getLocation(), "Ship Facing: " + ship.getFacing(), 15f, Color.red, ship, 1f, 0.5f);
//            }
//            //if (!engine.getContext().getPlayerGoal().equals(FleetGoal.ESCAPE)) {
//            if (engine.getCustomData().containsKey(ship.getFleetMemberId() + "_usedEscapeDrive")) {
//                //engine.addFloatingText(ship.getLocation(), "VALID: " + ((engine.getMapHeight() * 0.4f) + ship.getLocation().y)/600f, 25f, Color.red, ship, 1f, 0.5f);
//                if (engine.getCustomData().get(ship.getFleetMemberId() + "_usedEscapeDrive").equals(true)) {
//                        
//                    if ((ship.isDirectRetreat()
//                            || (validPlayerShip && ship.getEngineController().isAccelerating()))
//                            && ship.getFacing() >= 260f && ship.getFacing() <= 280f) {
//                        ship.getTravelDrive().getSpecAPI().setIn(3f);
//                        //ship.getVelocity().scale(0.95f);
//                        ship.turnOnTravelDrive((  (engine.getMapHeight() * 0.45f) + ship.getLocation().y) / 600f);
//                        engine.getCustomData().remove(ship.getFleetMemberId() + "_usedEscapeDrive");
//                        engine.getCustomData().put(ship.getFleetMemberId() + "_usedEscapeDrive", false);
//                    }
//                    //ship.getTravelDrive().getSpecAPI().setIn(0f);
//                    
//                }
//            } else {
//                engine.getCustomData().put(ship.getFleetMemberId() + "_usedEscapeDrive", true);
//            }
//            //  }
//        }

        //base fire rate scale maxes out at 80% flux
        float maxBoost = FED_BASE_FLUX_ROF_MAX;
        //RFC makes fire rate max out at 50% flux
        if (ship.getVariant().hasHullMod("fluxbreakers")) {
            maxBoost = FED_RFC_FLUX_ROF_MAX;
        }
        //SO makes fire rate max out at 25% flux
        if (ship.getVariant().hasHullMod("safetyoverrides")) {
            maxBoost = FED_SO_FLUX_ROF_MAX;
        }
        float fluxLevel = ship.getFluxLevel() + maxBoost;
        if (fluxLevel > 1f) {
            fluxLevel = 1f;
        }

        // Weapons work better at high flux, for power can be diverted away from the weakened shield/engines.
        ship.getMutableStats().getBallisticWeaponFluxCostMod().modifyPercent(ship.getId() + "_feddesign", FED_FIRERATE_PENALTY_PERCENT - FED_FIRERATE_BOOST_PERCENT * fluxLevel);
        ship.getMutableStats().getEnergyWeaponFluxCostMod().modifyPercent(ship.getId() + "_feddesign", FED_FIRERATE_PENALTY_PERCENT - FED_FIRERATE_BOOST_PERCENT * fluxLevel);
        ship.getMutableStats().getBallisticWeaponFluxCostMod().modifyPercent(ship.getId() + "_feddesign", FED_FIRERATE_PENALTY_PERCENT - FED_FIRERATE_BOOST_PERCENT * fluxLevel);
        ship.getMutableStats().getBallisticRoFMult().modifyPercent(ship.getId() + "_feddesign", -FED_FIRERATE_PENALTY_PERCENT + FED_FIRERATE_BOOST_PERCENT * fluxLevel);
        ship.getMutableStats().getEnergyRoFMult().modifyPercent(ship.getId() + "_feddesign", -FED_FIRERATE_PENALTY_PERCENT + FED_FIRERATE_BOOST_PERCENT * fluxLevel);
        ship.getMutableStats().getBallisticAmmoRegenMult().modifyPercent(ship.getId() + "_feddesign", -FED_FIRERATE_PENALTY_PERCENT + FED_FIRERATE_BOOST_PERCENT * fluxLevel);
        ship.getMutableStats().getEnergyAmmoRegenMult().modifyPercent(ship.getId() + "_feddesign", -FED_FIRERATE_PENALTY_PERCENT + FED_FIRERATE_BOOST_PERCENT * fluxLevel);

        //if(engine.getPlayerShip() == ship){
        //    engine.maintainStatusForPlayerShip(null, fedWeaponIcon, "LOAD BALANCER", "Rate of Fire: " + Math.round((Float)ship.getMutableStats().getEnergyRoFMult()) * 100 + "%", false);
        //}
        //Shield arc will decrease at high flux
        //Also responsible for 
        //Make sure we aren't running something if the game is paused or a ship is dead.   
        if (ship.getShield() == null || Global.getCombatEngine().isPaused() || ship.isHulk()) {
            return;
        }

        if ((ship.getShield().getType().equals(com.fs.starfarer.api.combat.ShieldAPI.ShieldType.FRONT) || ship.getShield().getType().equals(com.fs.starfarer.api.combat.ShieldAPI.ShieldType.OMNI))) {

            //API stuff for readability of shield changing code.
            ShieldAPI shield = ship.getShield();
            FluxTrackerAPI flux = ship.getFluxTracker();
            ShipHullSpecAPI hull = ship.getHullSpec();

            float hardFluxRatio = flux.getHardFlux() / flux.getMaxFlux();
            float currentRadius = shield.getRadius();
            float defaultRadius = hull.getShieldSpec().getRadius();
            float radiusTarget;
            float arcTarget;
            float minFSRadius;
            float tmpDistance;

            //Cosmetic stuff to make shield less visually strong at higher flux
            int shieldInnerOpacity;
            int shieldRingOpacity;

            shieldInnerOpacity = (int) (hull.getShieldSpec().getInnerColor().getAlpha() * ((1 - 0.5 * ((flux.getHardFlux() / flux.getMaxFlux())))));
            shieldInnerOpacity = safeColor(shieldInnerOpacity);
            shieldRingOpacity = (int) (hull.getShieldSpec().getRingColor().getAlpha() * ((1 - 0.5 * ((flux.getHardFlux() / flux.getMaxFlux())))));
            shieldInnerOpacity = safeColor(shieldRingOpacity);

            if (shield.isOff() && !ship.isDefenseDisabled()) {
                //When the shield is off and not overloading, turn shield target to zero as so you can cosmetically unfold it again.
                radiusTarget = hull.getShieldSpec().getRadius() * 0.9f;

                //exclude auxiliary shield modules
                if (AUX_SHIELDS_HMAP.containsKey(hull.getHullId()) || FLEETSHIELDS_HMAP.containsKey(hull.getHullId())) {
                    radiusTarget = hull.getShieldSpec().getRadius() * 0.1f;
                }

                //Logic to actually shrink the shield with target 0f. Yay pretty shield deployment.
                shield.setRadius(currentRadius + (((radiusTarget - currentRadius) * 0.04f)));

            } else if (!flux.isOverloadedOrVenting()) {

                //Establish a target radius, mathy stuff for making it only effect 110% of the total shield size for cosmetics only.
                radiusTarget = ((defaultRadius * 1.1f) + 0.1f * ((defaultRadius * 1.1f) * (1 - hardFluxRatio)));

                if (AUX_SHIELDS_HMAP.containsKey(hull.getHullId())) { //THIS IS A SECONDARY SHIELD

                    int altShieldInnerColor = (int) (hull.getShieldSpec().getInnerColor().getAlpha() * (1 - (0.5 * ((flux.getCurrFlux() / flux.getMaxFlux())))));
                    altShieldInnerColor = safeColor(altShieldInnerColor);
                    int altShieldRingColor = (int) (hull.getShieldSpec().getRingColor().getAlpha() * (1 - (0.5 * ((flux.getCurrFlux() / flux.getMaxFlux())))));
                    altShieldRingColor = safeColor(altShieldRingColor);

                    shield.setActiveArc(shield.getArc());
                    shield.setRingColor(new Color(hull.getShieldSpec().getRingColor().getRed(), hull.getShieldSpec().getRingColor().getGreen(), hull.getShieldSpec().getRingColor().getBlue(), altShieldInnerColor));
                    shield.setInnerColor(new Color(hull.getShieldSpec().getInnerColor().getRed(), hull.getShieldSpec().getInnerColor().getGreen(), hull.getShieldSpec().getInnerColor().getBlue(), altShieldInnerColor));
                    shield.setRadius(currentRadius + (((radiusTarget - currentRadius) * (0.01f * (float) SHIELD_RADIUS_GROWTH_SPEED_HMAP.get(hull.getHullSize())))));

                } else if (FLEETSHIELDS_HMAP.containsKey(hull.getHullId())) { // THIS IS A FLEETSHIELD

                    //Fleetshields must shrink if they are too close.
                    radiusTarget = (defaultRadius * 0.6f) + ((defaultRadius * 0.5f * (1f - hardFluxRatio)));
                    List<ShipAPI> ships = CombatUtils.getShipsWithinRange(ship.getLocation(), ship.getShieldRadiusEvenIfNoShield());
                    minFSRadius = radiusTarget;
                    for (ShipAPI nearShip : ships) {

                        if (nearShip.getHullSpec().getBaseHullId().equals(ship.getHullSpec().getBaseHullId()) && !nearShip.equals(ship) && nearShip.isAlive() && !nearShip.getFluxTracker().isOverloaded()) {
                            tmpDistance = MathUtils.getDistance(ship.getShieldCenterEvenIfNoShield(), nearShip.getShieldCenterEvenIfNoShield()) - nearShip.getShieldRadiusEvenIfNoShield();
                            if (tmpDistance < minFSRadius) {
                                minFSRadius = tmpDistance;
                            }
                        }

                    }
                    if (minFSRadius <= radiusTarget && minFSRadius >= 400f) {
                        radiusTarget = minFSRadius;
                    } else if (ship.getParentStation().getFluxTracker().isOverloadedOrVenting()) {
                        radiusTarget = 0f;
                    } else {
                        radiusTarget = 500f;
                    }

                    //Fleetshield gets less visible as it nears high flux
                    int altShieldInnerColor = (int) (hull.getShieldSpec().getInnerColor().getAlpha() * (1 - (0.7 * ((flux.getCurrFlux() / flux.getMaxFlux())))));
                    altShieldInnerColor = safeColor(altShieldInnerColor);
                    int altShieldRingColor = (int) (hull.getShieldSpec().getRingColor().getAlpha() * (1 - (0.7 * ((flux.getCurrFlux() / flux.getMaxFlux())))));
                    altShieldRingColor = safeColor(altShieldRingColor);

                    shield.setRingColor(new Color(shield.getRingColor().getRed(), shield.getRingColor().getGreen(), shield.getRingColor().getBlue(), altShieldRingColor));
                    shield.setInnerColor(new Color(shield.getInnerColor().getRed(), shield.getInnerColor().getGreen(), shield.getInnerColor().getBlue(), altShieldInnerColor));

                    //Always 360, always radius-effected by flux level.
                    shield.setActiveArc(shield.getArc());
                    shield.setRadius(currentRadius + (((radiusTarget - currentRadius) * (0.0035f * (float) SHIELD_RADIUS_GROWTH_SPEED_HMAP.get(hull.getHullSize())))));

                } else { //THIS IS A NORMAL SHIELD ON A NORMAL SHIP

                    //Accouting for vanilla hullmod bonuses, we accomodate for changed shield arc (or level at which the shield starts to shrink)
                    //Start with the default shield arc
                    arcTarget = hull.getShieldSpec().getArc();

                    if (ship.getVariant().getHullMods().contains("extendedshieldemitter")) {
                        arcTarget = arcTarget + 60f;
                    }

                    if (ship.getVariant().getHullMods().contains("frontemitter")) {
                        arcTarget = arcTarget * 2f;
                    }

                    if (ship.getVariant().getHullMods().contains("adaptiveshields")) {
                        arcTarget = arcTarget * .75f;
                    }

                    if (ship.getVariant().getHullMods().contains("stabilizedshieldemitter")) {
                        arcTarget = arcTarget * (float) (1f - 1f * (hardFluxRatio) + ((float) SHIELD_MIN_RADIUS_HMAP.get(hull.getHullSize()) * 0.5f) * (hardFluxRatio));
                    } else {
                        arcTarget = arcTarget * (float) (1f - 1f * (hardFluxRatio) + ((float) SHIELD_MIN_RADIUS_HMAP.get(hull.getHullSize())) * (hardFluxRatio));
                    }

                    if (arcTarget > FED_SHIELD_MAX_ARC) {
                        arcTarget = FED_SHIELD_MAX_ARC;
                    }

                    if (arcTarget < FED_ABSOLUTE_MIN_SHIELD_ARC) {
                        arcTarget = FED_ABSOLUTE_MIN_SHIELD_ARC;
                    }
                    shield.setArc(arcTarget);

                    shield.setRingColor(new Color(shield.getRingColor().getRed(), shield.getRingColor().getGreen(), shield.getRingColor().getBlue(), shieldRingOpacity));
                    shield.setInnerColor(new Color(shield.getRingColor().getRed(), shield.getInnerColor().getGreen(), shield.getInnerColor().getBlue(), shieldInnerOpacity));

                    //Final logic, approaches the limit of the target radius but never reaches it via difference and a multiplier to make it slow. Tsk. At least it works!
                    shield.setRadius(currentRadius + (((radiusTarget - currentRadius) * (0.01f * (float) SHIELD_RADIUS_GROWTH_SPEED_HMAP.get(hull.getHullSize())))));
                }
            }
        }
    }

    public void fedEngineBoostVisuals(ShipAPI ship) {
        // Variables for making sure extra engines count as flame-outable
        
        float enginesFracTempOffline = 0;

        CombatEngineAPI thisEngine = Global.getCombatEngine();
        //Cycle through all ship engines
        for (ShipEngineControllerAPI.ShipEngineAPI engine : ship.getEngineController().getShipEngines()) {
            //thisEngine.addFloatingText(ship.getLocation(), "Elaped time: " + Global.getCombatEngine().getTotalElapsedTime(false) , 10f, Color.ORANGE, ship, 1f, 2f);

            //Don't care about permanent/unrelated engines
            if (engine.isPermanentlyDisabled() || engine.isSystemActivated()) {
                continue;
            } else {
                // "FEDERATION_VENT" are the ones that turn on when zeroflux is activated
                // There is no other way to do this from a hullmod, it seems?
                if (engine.getStyleId().equalsIgnoreCase("FEDERATION_VENT")) {

                    if (ship.getSystem().isOn() || ship.isEngineBoostActive() || ship.getTravelDrive().isOn()) {
                        //Fade in new flame color from (0,0,0,0)
                        if (!ship.getVariant().hasHullMod("safteyoverrides")) {
                            ship.getEngineController().fadeToOtherColor(engine.getEngineSlot(), new Color(255, 125, 25, 255), new Color(60, 60, 60, 70), 1f, 1f);
                        }
                        engine.getEngineSlot().setContrailColor(new Color(60, 60, 60, 70));
                        engine.getEngineSlot().setContrailDuration(3);
                        engine.getEngineSlot().setContrailSpeedMultAngVel(0.5f);
                        engine.getEngineSlot().setContrailWidthMultiplier(0.5f);
                        engine.getEngineSlot().setGlowAlternateColor(new Color(80, 80, 80, 80));
                        engine.getEngineSlot().setGlowSizeMult(1f);
                        engine.getEngineSlot().setContrailWidth(20f);
                        ship.getMutableStats().getAcceleration().modifyPercent(ship.getId() + "_ceb", 30);

                        //Counting if the engine is able to contriubute to a flameout ratio
                        //If all engines add up to more than half, the ship flames out
                        //Only adds if zeroflux is on and is VENT
                        if (engine.isDisabled()) {
                            if (Math.random() > 0.99) {
                                //thisEngine.addFloatingText(new Vector2f(engine.getLocation().x, engine.getLocation().y + (float) (10 * Math.random())), "" + engine.getContribution(), 8f, Color.WHITE, ship, 0f, 0.5f);
                            }

                            enginesFracTempOffline += engine.getContribution();
                        }
                        // Else statement to hide nonactive engine nozzels
                        // Leaves an annoying white additive glow despite everthing here
                    } else {
                        //engine.setHitpoints(99999);
                        engine.getEngineSlot().setContrailColor(new Color(0, 0, 0, 0));
                        engine.getEngineSlot().setGlowSizeMult(0f);
                        engine.getEngineSlot().setContrailDuration(0);
                        engine.getEngineSlot().setContrailSpeedMultAngVel(0f);
                        engine.getEngineSlot().setContrailWidthMultiplier(0f);
                        engine.getEngineSlot().setGlowAlternateColor(new Color(0, 0, 0, 0));
                        ship.getMutableStats().getAcceleration().unmodifyPercent(ship.getId() + "_ceb");
                    }
                }

                // Count regular engines, this will flameout all engines (including vents)
                // when zeroflux boost is off and more than half "normal" engines are disabled.
                if (engine.getStyleId().equalsIgnoreCase("FEDERATION_ENGINE") && !ship.isEngineBoostActive()) {
                    if (engine.isDisabled()) {
                        enginesFracTempOffline += engine.getContribution();
                    }
                }
            }
        }
        //Final logic after all the adding is done. 
        if (!ship.getEngineController().isFlamedOut() && enginesFracTempOffline > ship.getEngineController().getFlameoutFraction()) {
            //engine.addFloatingText(ship.getLocation(), "Engine flameout!", 25f, Color.ORANGE, ship, 1f, 2f);
            ship.getEngineController().forceFlameout();
        }
    }

    private int safeColor(int num) {
        if (num > 255) {
            num = 255;
        }
        if (num < 25) {
            num = 25;
        }
        return num;
    }

}
