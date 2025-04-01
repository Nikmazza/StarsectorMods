package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicIncompatibleHullmods;

import java.util.HashMap;
import java.util.Map;

public class ssp_T_shortrange extends BaseHullMod {
    private static Map mag = new HashMap();
    static {
        mag.put(ShipAPI.HullSize.FRIGATE, 1f);
        mag.put(ShipAPI.HullSize.DESTROYER, 2f);
        mag.put(ShipAPI.HullSize.CRUISER, 3f);
        mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 4f);
    }
    public static final float MaxSpeedandADTM_Mult = 30f;
    public static final float DamageBonus = 0.25f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if(stats.getVariant()!=null && this.spec!=null && Misc.isSpecialMod(stats.getVariant(),this.spec)){
            stats.getVariant().removePermaMod(this.spec.getId());
        }
        stats.getMaxSpeed().modifyFlat(id,stats.getMaxSpeed().getBaseValue()*MaxSpeedandADTM_Mult*0.01f);
        stats.getAcceleration().modifyPercent(id, MaxSpeedandADTM_Mult);
        stats.getDeceleration().modifyPercent(id, MaxSpeedandADTM_Mult);
        stats.getTurnAcceleration().modifyPercent(id, MaxSpeedandADTM_Mult);
        stats.getMaxTurnRate().modifyPercent(id, MaxSpeedandADTM_Mult);
        stats.getEnergyWeaponRangeBonus().modifyFlat(id,-75f * (Float) mag.get(hullSize));
        stats.getBallisticWeaponRangeBonus().modifyFlat(id,-75f * (Float) mag.get(hullSize));
    }
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new ssp_T_shortrange_listener(ship,id));
        if (ship.getVariant().getHullMods().contains("unstable_injector")) {
            //if someone tries to install sussy hullmodsus, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "unstable_injector",
                    "ssp_T_shortrange"//插件id
            );
        }
    }
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff")
                && !ship.getVariant().hasHullMod("unstable_injector")
                && !ship.getVariant().hasHullMod("ssp_T_beam")
                && !ship.getVariant().hasHullMod("ssp_T_longrange");
    }
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("ssp_cliff")) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }
    public static class ssp_T_shortrange_listener implements DamageDealtModifier {
        protected ShipAPI ship;
        protected String id;
        public ssp_T_shortrange_listener(ShipAPI ship, String id){
            this.ship = ship;
            this.id = id;
        }
        public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit){
            if(target instanceof ShipAPI && !shieldHit) {
                float Target_Facing = target.getFacing();
                float HitPoint_Facing= VectorUtils.getAngleStrict(target.getLocation(),point);
                float HullHitAngle= Math.abs(HitPoint_Facing-Target_Facing);
                if(HullHitAngle>180){HullHitAngle=360-HullHitAngle;}
                String id = "ssp_T_shortrange_listener_effectmod";
                damage.getModifier().modifyMult(id, 1+DamageBonus*(HullHitAngle/180));
                return id;
            }else if(target instanceof ShipAPI && shieldHit) {
                float Shield_Facing = target.getShield().getFacing();
                float ShieldHit_Facing= VectorUtils.getAngleStrict(target.getLocation(),point);
                float ShieldHitAngle= Math.abs(ShieldHit_Facing-Shield_Facing);
                if(ShieldHitAngle>180){ShieldHitAngle=360-ShieldHitAngle;}//一个0~180的值，数值越大击中位置与护盾朝向夹角越大
                String id = "ssp_T_shortrange_listener_effectmod";
                damage.getModifier().modifyMult(id, 1+DamageBonus*ShieldHitAngle/(target.getShield().getArc()*0.5f));
                return id;
            }else return null;
        }
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0){
            return "75/150/225/300";
        }else if(index ==1){
            return "30%";
        }else if(index ==2){
            return "25%";
        }else if(index ==3){
            return "25%";
        }else if(index ==4){
            return Global.getSettings().getHullModSpec("unstable_injector").getDisplayName();
        }
        return null;
    }
}
