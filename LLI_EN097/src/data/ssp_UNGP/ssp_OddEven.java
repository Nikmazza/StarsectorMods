package data.ssp_UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class ssp_OddEven extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float OddEven;
    public ssp_OddEven() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        this.OddEven = this.getValueByDifficulty(0, difficulty);
    }
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if(index == 0){
            return difficulty.getLinearValue(0.10f, 0.10f);
        }else{
            return super.getValueByDifficulty(index, difficulty);
        }
    }
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if(index==0)return "x"+(1-this.getValueByDifficulty(0, difficulty));
        if(index==1)return "x"+(1+this.getValueByDifficulty(0, difficulty));
        return null;
    }

    public void advanceInCombat(CombatEngineAPI engine, float amount) { }
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) { }
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
        if(Global.getSector().getClock().getMonth()%2==0){//偶数月
            ship.getMutableStats().getHullDamageTakenMult().modifyMult(this.buffID, 1+OddEven);
            ship.getMutableStats().getArmorDamageTakenMult().modifyMult(this.buffID, 1+OddEven);
            ship.getMutableStats().getShieldDamageTakenMult().modifyMult(this.buffID, 1+OddEven);
            ship.getMutableStats().getEmpDamageTakenMult().modifyMult(this.buffID, 1+OddEven);
            ship.getMutableStats().getMissileWeaponDamageMult().unmodify(this.buffID);
            ship.getMutableStats().getBallisticWeaponDamageMult().unmodify(this.buffID);
            ship.getMutableStats().getEnergyWeaponDamageMult().unmodify(this.buffID);
        }else{//奇数月
            ship.getMutableStats().getHullDamageTakenMult().unmodify(this.buffID);
            ship.getMutableStats().getArmorDamageTakenMult().unmodify(this.buffID);
            ship.getMutableStats().getShieldDamageTakenMult().unmodify(this.buffID);
            ship.getMutableStats().getEmpDamageTakenMult().unmodify(this.buffID);
            ship.getMutableStats().getMissileWeaponDamageMult().modifyMult(this.buffID, 1-OddEven);
            ship.getMutableStats().getBallisticWeaponDamageMult().modifyMult(this.buffID, 1-OddEven);
            ship.getMutableStats().getEnergyWeaponDamageMult().modifyMult(this.buffID, 1-OddEven);
        }
    }
}
