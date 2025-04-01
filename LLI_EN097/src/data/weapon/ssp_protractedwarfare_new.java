package data.weapon;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import org.lwjgl.util.vector.Vector2f;

public class ssp_protractedwarfare_new implements OnHitEffectPlugin{
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
                      Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(target instanceof ShipAPI ){
            if(((ShipAPI) target).getHullSize() != ShipAPI.HullSize.FIGHTER){
                if (!((ShipAPI) target).hasListenerOfClass(ssp_protractedwarfare_new_EffectMod.class)) {
                    ((ShipAPI) target).addListener(new ssp_protractedwarfare_new_EffectMod());
                }
            }
        }
    }
    public static class ssp_protractedwarfare_new_EffectMod implements DamageTakenModifier, AdvanceableListener {
        private float TotalDamageTaken=0;
        public ssp_protractedwarfare_new_EffectMod() {
        }
        @Override
        public void advance(float amount) {
        }
        @Override
        public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            if(param instanceof DamagingProjectileAPI && target instanceof ShipAPI){
                DamagingProjectileAPI proj = (DamagingProjectileAPI) param;
                if(proj.getWeapon()!=null && proj.getWeapon().getSpec()!=null){
                    if(proj.getProjectileSpecId().equals("ssp_blaster_shot")
                            ||proj.getProjectileSpecId().equals("ssp_BigFackingGPD_shot")){
                        //从这里才算正式开始
                        TotalDamageTaken+=1;
                        String id = "ssp_protractedwarfare_new_EffectMod";
                        damage.getModifier().modifyMult(id, 1f+TotalDamageTaken/100);
                    }
                }
            }
            return null;
        }
    }
}
