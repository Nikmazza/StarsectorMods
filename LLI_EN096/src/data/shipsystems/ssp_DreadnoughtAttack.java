package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.SSPI18nUtil;

public class ssp_DreadnoughtAttack extends BaseShipSystemScript {
    private float BONUS = 0.33f;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship=null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return; }

        float Mult = 1f;

        if (ship.getVariant().hasHullMod("ssp_LongerRange")) {Mult=2f;BONUS=0.5f;}
        if (ship.getVariant().hasHullMod("ssp_ShortRange")) {BONUS=0.20f;}
        stats.getHullDamageTakenMult().modifyMult(id,BONUS);
        stats.getArmorDamageTakenMult().modifyMult(id,BONUS);
        stats.getEmpDamageTakenMult().modifyMult(id,BONUS);
        stats.getTimeMult().modifyMult(id,Mult);
        if(state == State.OUT) {
            stats.getCombatEngineRepairTimeMult().modifyMult(id, 0);
                for(WeaponAPI weapon:ship.getAllWeapons()){
                    if (weapon.isDisabled()) {weapon.repair();}
                }

        }

    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship=null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return; }
        stats.getArmorDamageTakenMult().unmodify(id);
        stats.getHullDamageTakenMult().unmodify(id);
        stats.getEmpDamageTakenMult().unmodify(id);
        stats.getCombatEngineRepairTimeMult().unmodify(id);
        stats.getBallisticAmmoRegenMult().unmodify(id);
        stats.getEnergyAmmoRegenMult().unmodify(id);
        stats.getMissileAmmoRegenMult().unmodify(id);
        stats.getTimeMult().unmodify(id);
    }
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(SSPI18nUtil.getShipSystemString("ssp_DreadnoughtAttack") +(int)(100-BONUS*100)+"%", false);
        }
        return null;
    }


}
