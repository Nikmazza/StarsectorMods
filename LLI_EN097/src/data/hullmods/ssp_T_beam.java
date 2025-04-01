package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import data.SSPI18nUtil;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicIncompatibleHullmods;

public class ssp_T_beam extends BaseHullMod {
    public static float BeamDamage_Percent=10f;
    public static float BeamRange_Flat=200f;
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new ssp_T_beam_listener(ship, id));
        if (ship.getVariant().getHullMods().contains("advancedoptics")) {
            //if someone tries to install sussy hullmodsus, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "advancedoptics",
                    "ssp_T_beam"
            );
        }else if(ship.getVariant().getHullMods().contains("high_scatter_amp")) {
            //if someone tries to install sussy hullmodsus, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "high_scatter_amp",
                    "ssp_T_beam"
            );
        }
    }
    public static class ssp_T_beam_listener implements AdvanceableListener, WeaponBaseRangeModifier, DamageDealtModifier {
        protected ShipAPI ship;
        protected String id;
        public ssp_T_beam_listener(ShipAPI ship, String id) {
            this.ship = ship;
            this.id = id;
        }
        public void advance(float amount) {
            MutableShipStatsAPI stats = ship.getMutableStats();
            stats.getBeamWeaponRangeBonus().modifyFlat(id,BeamRange_Flat * ship.getFluxLevel() );
        }
        //光束侠射程调整
        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) { return 0; }
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) { return 0; }
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            if (weapon.getSlot().getWeaponType() == WeaponAPI.WeaponType.BALLISTIC) { return 1;}//在实弹槽位上的武器不受到射程惩罚
            if (weapon.getSlot().getWeaponType() == WeaponAPI.WeaponType.BUILT_IN) { return 1;}//内置武器不受到射程惩罚
            if (weapon.isBeam()){return 1;}//光束武器不受到射程惩罚
            if (weapon.getType() == WeaponAPI.WeaponType.MISSILE){ return 1;}//在导弹武器不受到射程惩罚
            else return 0.05f;
        }
        public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            WeaponAPI weapon = null;
            if (param instanceof BeamAPI) {
                weapon = ((BeamAPI) param).getWeapon();
            } else {
                return null;
            }
            if (weapon == null || ship == null) return null;
            if (!shieldHit) return null;

            String id = "ssp_T_beam";

            if (Math.random() <= ship.getFluxLevel()) {
                damage.setForceHardFlux(true);
            } else if (Math.random() > ship.getFluxLevel()) {
                damage.setForceHardFlux(false);
            }
            damage.getModifier().modifyPercent(id, BeamDamage_Percent * ship.getFluxLevel());
            return id;
        }
    }
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff")
                && !ship.getVariant().hasHullMod("advancedoptics")
                && !ship.getVariant().hasHullMod("high_scatter_amp")
                && !ship.getVariant().hasHullMod("ssp_T_longrange")
                && !ship.getVariant().hasHullMod("ssp_T_shortrange");
    }
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("ssp_cliff")) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }
    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "200";
        }else if(index ==1){
            return "10%";
        }else if(index ==2){
            return "100%";
        }else if(index ==3){
            return Global.getSettings().getHullModSpec("advancedoptics").getDisplayName();
        }else if(index ==4){
            return Global.getSettings().getHullModSpec("high_scatter_amp").getDisplayName();
        }
        return null;
    }
}
