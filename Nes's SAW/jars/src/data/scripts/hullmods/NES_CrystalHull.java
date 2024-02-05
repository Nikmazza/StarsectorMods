package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicIncompatibleHullmods;
import data.scripts.utils.NES_Util;
import org.lazywizard.lazylib.CollisionUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class NES_CrystalHull extends BaseHullMod {

    public static final float VENT_RATE_BONUS = 25f;
    public static final float CORONA_EFFECT_REDUCTION = 0.05f;
    public static final float ENERGY_DAMAGE_REDUCTION = 0.95f;

    private static final float MAX_SPARKLE_CHANCE_PER_SECOND_PER_CELL = 0.75f; //0.75f default
    private static final Color SPARK_COLOR = new Color(245, 245, 195, 200);
    private static final float SPARK_DURATION = 0.2f; //0.2f default
    private static final float SPARK_RADIUS = 4f; //4f default

    private static final float RANGE_THRESHOLD = 700f; //range cutoff limit
    private static final float RANGE_MULT = 0.25f; //range reduction multiplier above limit


    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>(4);
    static {
        BLOCKED_HULLMODS.add("solar_shielding");
        BLOCKED_HULLMODS.add("safetyoverrides");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                        ship.getVariant(),
                        tmp,
                        "NES_CrystalHull"
                );
            }
        }
    }


    public static Vector2f getCellLocation(ShipAPI ship, float x, float y) {
        float xx = x - (ship.getArmorGrid().getGrid().length / 2f);
        float yy = y - (ship.getArmorGrid().getGrid()[0].length / 2f);
        float cellSize = ship.getArmorGrid().getCellSize();
        Vector2f cellLoc = new Vector2f();
        float theta = (float) (((ship.getFacing() - 90f) / 360f) * (Math.PI * 2.0));
        cellLoc.x = (float) (xx * Math.cos(theta) - yy * Math.sin(theta)) * cellSize + ship.getLocation().x;
        cellLoc.y = (float) (xx * Math.sin(theta) + yy * Math.cos(theta)) * cellSize + ship.getLocation().y;

        return cellLoc;
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getVentRateMult().modifyPercent(id, VENT_RATE_BONUS);
        stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, CORONA_EFFECT_REDUCTION);

        stats.getEnergyDamageTakenMult().modifyMult(id, ENERGY_DAMAGE_REDUCTION);
        stats.getEnergyShieldDamageTakenMult().modifyMult(id, ENERGY_DAMAGE_REDUCTION);

        stats.getWeaponRangeThreshold().modifyFlat(id, RANGE_THRESHOLD);
        stats.getWeaponRangeMultPastThreshold().modifyMult(id, RANGE_MULT);
    }

    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) VENT_RATE_BONUS + "%";
        if (index == 1) return "" + (int) Math.round((1f - CORONA_EFFECT_REDUCTION) * 100f) + "%";
        if (index == 2) return "" + (int) Math.round((1f - ENERGY_DAMAGE_REDUCTION) * 100f) + "%";
        if (index == 3) return Misc.getRoundedValue(RANGE_THRESHOLD); //range cutoff
        if (index == 4) return Global.getSettings().getHullModSpec("solar_shielding").getDisplayName();
        if (index == 5) return Global.getSettings().getHullModSpec("safetyoverrides").getDisplayName();
        return null;
    }

    //SPARKLES! Courtesy of Dark Revenant.
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        ShieldAPI shield = ship.getShield();

        float fluxLevel = ship.getFluxTracker().getFluxLevel();

        ArmorGridAPI armorGrid = ship.getArmorGrid();
        Color color = new Color(SPARK_COLOR.getRed(), SPARK_COLOR.getGreen(), SPARK_COLOR.getBlue(), NES_Util.clamp255((int) (SPARK_COLOR.getAlpha() * (1f - fluxLevel))));
        for (int x = 0; x < armorGrid.getGrid().length; x++) {
            for (int y = 0; y < armorGrid.getGrid()[0].length; y++) {
                float armorLevel = armorGrid.getArmorValue(x, y);
                if (armorLevel <= 0f) {
                    continue;
                }

                float chance = amount * (1f - fluxLevel) * MAX_SPARKLE_CHANCE_PER_SECOND_PER_CELL * armorLevel / armorGrid.getMaxArmorInCell();
                if (Math.random() >= chance) {
                    continue;
                }

                float cellSize = armorGrid.getCellSize();
                Vector2f cellLoc = getCellLocation(ship, x, y);
                cellLoc.x += cellSize * 0.1f - cellSize * (float) Math.random();
                cellLoc.y += cellSize * 0.1f - cellSize * (float) Math.random();
                if (CollisionUtils.isPointWithinBounds(cellLoc, ship)) {
                    engine.addHitParticle(cellLoc, ship.getVelocity(), 0.5f * SPARK_RADIUS * (float) Math.random() + SPARK_RADIUS, 1f, SPARK_DURATION,
                            NES_Util.colorJitter(color, 50f));
                }
            }
        }

    }
}