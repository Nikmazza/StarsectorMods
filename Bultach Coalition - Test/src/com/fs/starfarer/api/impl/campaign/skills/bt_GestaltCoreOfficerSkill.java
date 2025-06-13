package com.fs.starfarer.api.impl.campaign.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import java.util.Random;

public class bt_GestaltCoreOfficerSkill {

    public static final float ARMOR_HEAL_FRACTION = 0.15f;
    public static final float MAX_REGEN_ARMOR_FRACTION = 0.40f;

    private static final Random random = new Random();

    public static class Level1 implements ShipSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
            stats.getCRLossPerSecondPercent().modifyMult(id, 0.5f);
        }
        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            stats.getCRLossPerSecondPercent().unmodify(id);
        }
        @Override
        public String getEffectDescription(float level) {
            return "We have waited.";
        }
        @Override
        public String getEffectPerLevelDescription() { return null; }
        @Override
        public ScopeDescription getScopeDescription() { return ScopeDescription.PILOTED_SHIP; }
    }

    public static class Level2 implements ShipSkillEffect {

        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
            if (stats.getEntity() instanceof ShipAPI) {
                ShipAPI ship = (ShipAPI) stats.getEntity();
                CombatEngineAPI engine = Global.getCombatEngine();

                if (engine == null || (engine != null && engine.isSimulation())) {
                    return;
                }

                if (!ship.hasListenerOfClass(GestaltArmorHealingListener.class)) {
                    ship.addListener(new GestaltArmorHealingListener(ship));
                }
            }
        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            if (stats.getEntity() instanceof ShipAPI) {
                ShipAPI ship = (ShipAPI) stats.getEntity();
                ship.removeListenerOfClass(GestaltArmorHealingListener.class);
            }
        }

        @Override
        public String getEffectDescription(float level) {
            return String.format("We will endure, if only so that your light does not fade.",
                    ARMOR_HEAL_FRACTION * 100f, MAX_REGEN_ARMOR_FRACTION * 100f);
        }

        @Override
        public String getEffectPerLevelDescription() { return null; }
        @Override
        public ScopeDescription getScopeDescription() { return ScopeDescription.PILOTED_SHIP; }

        public void createCustomDescription(MutableCharacterStatsAPI charStats, SkillSpecAPI skill, TooltipMakerAPI info, float width) {
            info.addPara("We will endure, if only so that your light does not fade.",
                    0f, Misc.getHighlightColor(),
                    (int)(ARMOR_HEAL_FRACTION * 100f) + "%",
                    (int)(MAX_REGEN_ARMOR_FRACTION * 100f) + "%");
        }
    }

    public static class GestaltArmorHealingListener implements DamageDealtModifier {
        protected final ShipAPI ship;
        protected final ArmorGridAPI grid;
        protected final int gridWidth;
        protected final int gridHeight;
        protected float healedAmount = 0f;
        protected final float healLimit;
        protected final float maxArmorPerCell;

        public GestaltArmorHealingListener(ShipAPI ship) {
            this.ship = ship;
            this.grid = ship.getArmorGrid();

            if (this.grid != null) {
                this.maxArmorPerCell = grid.getMaxArmorInCell();
                float[][] gridArray = grid.getGrid();
                this.gridWidth = gridArray.length;
                this.gridHeight = (gridWidth > 0) ? gridArray[0].length : 0;

                float totalMaxArmor = 0f;
                if (gridWidth > 0 && gridHeight > 0) {
                    totalMaxArmor = maxArmorPerCell * gridWidth * gridHeight;
                }
                this.healLimit = totalMaxArmor * MAX_REGEN_ARMOR_FRACTION;
            } else {

                this.maxArmorPerCell = 0f;
                this.gridWidth = 0;
                this.gridHeight = 0;
                this.healLimit = 0f;
            }
        }

        @Override
        public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage,
                                        Vector2f point, boolean shieldHit) {

            if (grid == null) return null;
            if (ship == null || !ship.isAlive() || ship.isHulk()) return null;
            if (damage == null || shieldHit || !(target instanceof ShipAPI) || ((ShipAPI)target).isFighter() || ((ShipAPI)target).isHulk() || target.getOwner() == ship.getOwner()) return null;
            if (gridWidth <= 0 || gridHeight <= 0) return null;

            if (healedAmount >= healLimit) return null;

            float rawDamageDealt = damage.getDamage();
            float healAmountForThisHit = rawDamageDealt * ARMOR_HEAL_FRACTION;

            healAmountForThisHit = Math.min(healAmountForThisHit, healLimit - healedAmount);

            if (healAmountForThisHit <= 0f) return null;

            float appliedHealThisHit = 0f;
            int attempts = 0;
            int maxAttempts = gridWidth * gridHeight;

            while (appliedHealThisHit < healAmountForThisHit && attempts < maxAttempts) {
                attempts++;

                int x = random.nextInt(gridWidth);
                int y = random.nextInt(gridHeight);

                float currentArmor = grid.getArmorValue(x, y);
                float missingArmor = maxArmorPerCell - currentArmor;

                if (missingArmor > 0) {

                    float healThisCell = Math.min(missingArmor, healAmountForThisHit - appliedHealThisHit);

                    if (healThisCell > 0.01f) {
                        grid.setArmorValue(x, y, currentArmor + healThisCell);
                        healedAmount += healThisCell;
                        appliedHealThisHit += healThisCell;
                    }
                }
                if (healedAmount >= healLimit) break;
            }

            if (appliedHealThisHit > 0) {
                ship.syncWithArmorGridState();
            }
            return null;
        }
    }
}