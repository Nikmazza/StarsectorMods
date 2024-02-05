//by Nia, modified by Nes
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

import static data.scripts.utils.NES_Util.txt;

public class NES_VolatileShells extends BaseHullMod {

    private static final float CRIT_CHANCE = 0.2f; //20% cooler
    private static final float CRIT_MULT = 4f; //QUAD DAMAGE

    private final List<DamagingProjectileAPI> toCrit = new ArrayList<DamagingProjectileAPI>();
    private final List<DamagingProjectileAPI> hasHit = new ArrayList<DamagingProjectileAPI>();
    private final List<DamagingProjectileAPI> toRemove = new ArrayList<DamagingProjectileAPI>();

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        if (!ship.isHulk() && !ship.isPiece()) {

            //only crit when shipsystem is active
            float activecrit = CRIT_CHANCE * ship.getSystem().getEffectLevel();

            for (DamagingProjectileAPI proj : CombatUtils.getProjectilesWithinRange(ship.getLocation(), 500f)) {
                if (ship == proj.getSource() && !toCrit.contains(proj) && !hasHit.contains(proj)) {
                    if (Math.random() < activecrit) {
                        toCrit.add(proj);
                        proj.setDamageAmount(proj.getDamageAmount()*CRIT_MULT);
                    } else {
                        hasHit.add(proj);
                    }
                }
            }

            for (DamagingProjectileAPI proj : toCrit) {
                if (proj.didDamage() && !hasHit.contains(proj)) {
                    hasHit.add(proj);
                    if (proj.getDamageTarget() instanceof ShipAPI) {
                        //show this message when critical shot lands

                        //to prevent text spam on low damage strikes, vulcans etc
                        //number is statcard weapon damage
                        if (proj.getDamageAmount() >= 40 * CRIT_MULT) {
                            Global.getCombatEngine().addFloatingText(proj.getLocation(), txt("hullmod_volatileshells"), 25f, Color.red, proj, 1f, 0f);
                        }
                        Global.getCombatEngine().addHitParticle(proj.getLocation(), proj.getVelocity(), 100f, 1f, 0.05f, Color.white);
                    }
                }
            }


            for (DamagingProjectileAPI proj : hasHit) {
                if (!Global.getCombatEngine().isEntityInPlay(proj)) {
                    toCrit.remove(proj);
                    toRemove.add(proj);
                }
            }

            for (DamagingProjectileAPI proj : toRemove) {
                hasHit.remove(proj);
            }
            toRemove.clear();
        }
    }
}
