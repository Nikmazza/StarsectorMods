//by Nia, modified by Nes
//v2, now with 100% less mem leaks!!!
package data.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.combat.listeners.DamageListener;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static data.scripts.utils.NES_Util.txt;

public class NES_VolatileShells extends BaseHullMod {

    private static final float CRIT_CHANCE = 0.2f; //20% cooler
    private static final float CRIT_MULT = 4f; //QUAD DAMAGE

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        String id = ship.getId();
        Data data;
        Map<String, Object> customCombatData = Global.getCombatEngine().getCustomData();
        if (customCombatData.get("nes_crit" + id) instanceof NES_VolatileShells.Data)
            data = (NES_VolatileShells.Data) customCombatData.get("nes_crit" + id);
        else {
            data = new NES_VolatileShells.Data();
            customCombatData.put("nes_crit" + id, data);
        }

        if (!ship.isHulk() && !ship.isPiece()) {

            //only crit when shipsystem is active
            float activecrit = CRIT_CHANCE * ship.getSystem().getEffectLevel();

            for (DamagingProjectileAPI proj : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 500f)) {
                if (ship == proj.getSource() && !data.toCrit.contains(proj) && !data.hasHit.contains(proj)) {
                    if (Math.random() < activecrit) {
                        data.toCrit.add(proj);
                        proj.setDamageAmount(proj.getDamageAmount()*CRIT_MULT);
                    } else {
                        data.hasHit.add(proj);
                    }
                }
            }

            for (DamagingProjectileAPI proj : data.toCrit) {
                if (proj.didDamage() && !data.hasHit.contains(proj)) {
                    data.hasHit.add(proj);
                    if (proj.getDamageTarget() instanceof ShipAPI) {
                        //show this message when critical shot lands

                        //to prevent text spam on low damage strikes, vulcans etc (crit damage still applies)
                        //number is statcard weapon damage
                        if (proj.getDamageAmount() >= 40 * CRIT_MULT) {
                            Global.getCombatEngine().addFloatingText(proj.getLocation(), txt("hullmod_volatileshells"), 25f, Color.red, proj, 1f, 0f);
                        }
                        Global.getCombatEngine().addHitParticle(proj.getLocation(), proj.getVelocity(), 100f, 1f, 0.05f, Color.white);
                    }
                }
            }

            final List<DamagingProjectileAPI> toRemove = new ArrayList<>();
            for (DamagingProjectileAPI proj : data.hasHit) {
                if (!Global.getCombatEngine().isEntityInPlay(proj)) {
                    data.toCrit.remove(proj);
                    toRemove.add(proj);
                }
            }

            for (DamagingProjectileAPI proj : toRemove) {
                data.hasHit.remove(proj);
            }
            toRemove.clear();
        }
    }

    private static class Data {
        final List<DamagingProjectileAPI> toCrit = new ArrayList<>();
        final List<DamagingProjectileAPI> hasHit = new ArrayList<>();
    }
}