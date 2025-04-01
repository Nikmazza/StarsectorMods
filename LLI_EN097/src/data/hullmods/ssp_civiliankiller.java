package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import org.lwjgl.util.vector.Vector2f;

public class ssp_civiliankiller extends BaseHullMod {
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDamageToCapital().modifyMult(id,0.75f);
        stats.getDamageToCruisers().modifyMult(id,1.05f);
        stats.getDamageToDestroyers().modifyMult(id,1.10f);
        stats.getDamageToFrigates().modifyMult(id,1.15f);
    }
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new ssp_civiliankillerEffect(ship, id));
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "-25%";
        if (index == 1) return "+5%";
        if (index == 2) return "+10%";
        if (index == 3) return "+15%";
        if (index == 4) return ""+ Global.getSettings().getHullModSpec(HullMods.CIVGRADE).getDisplayName();
        if (index == 5) return "+50%";
        if (index == 6) return ""+ Global.getSettings().getHullModSpec(HullMods.MILITARIZED_SUBSYSTEMS).getDisplayName();
        return null;
    }
    public static class ssp_civiliankillerEffect implements DamageDealtModifier {
        protected ShipAPI ship;
        protected String id;
        public ssp_civiliankillerEffect(ShipAPI ship, String id) {
            this.ship = ship;
            this.id = id;
        }
        public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            if(target instanceof ShipAPI && ((ShipAPI) target).getVariant().hasHullMod(HullMods.CIVGRADE)){
                    damage.getModifier().modifyMult(id, 1.5f);
                    return id;
            }
            else return null;
        }
    }
}
