package data.weapon;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class ssp_protractedwarfare implements OnFireEffectPlugin, DamageDealtModifier {
    protected Map<CombatEntityAPI, Float> HaHaHaHaHashmap = new HashMap();
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ShipAPI ship_this = weapon.getShip();
        if (!ship_this.hasListenerOfClass(ssp_protractedwarfare.class)) {
            ship_this.addListener(this);
        }
    }
    public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
        float HitTimes = 0f;
        if(param instanceof DamagingProjectileAPI && target instanceof ShipAPI){
            DamagingProjectileAPI proj = (DamagingProjectileAPI) param;
            if(proj.getWeapon()!=null && proj.getWeapon().getSpec()!=null){
                if(proj.getWeapon().getSpec().getWeaponId().equals("ssp_blaster")||proj.getWeapon().getSpec().getWeaponId().equals("ssp_BigFackingGPD")){
                    //从这里才算正式开始
                    if(HaHaHaHaHashmap.get(target) != null && target instanceof ShipAPI){
                        HitTimes=HaHaHaHaHashmap.get(target);
                        HitTimes++;
                        HaHaHaHaHashmap.put((ShipAPI) target,HitTimes);
                        String id = "ssp_protractedwarfare";
                        damage.getModifier().modifyMult(id,1+HitTimes*0.01f);
                        return id;
                    }
                    else if(HaHaHaHaHashmap.get(target) == null && target instanceof ShipAPI){
                        HaHaHaHaHashmap.put((ShipAPI) target,HitTimes);
                    }
                }
            }
        }
        return null;
    }
    }
