package data.ssp_UNGP;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.util.HashSet;

public class ssp_CRDecay extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float CRD;
    public ssp_CRDecay() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        this.CRD = this.getValueByDifficulty(0, difficulty);
    }
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if(index == 0){
            return difficulty.getLinearValue(3f, 3f);
        }else{
            return super.getValueByDifficulty(index, difficulty);
        }
    }
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if(index==0)return this.getPercentString(this.getValueByDifficulty(index, difficulty));
        return null;
    }

    public void advanceInCombat(CombatEngineAPI engine, float amount) { }
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) { }
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
        if(ship.getHullSize() != ShipAPI.HullSize.FIGHTER){
            HashSet<String> weaponid = new HashSet<String>();
            for(WeaponAPI w:ship.getAllWeapons()){
                if(w!=null){
                    weaponid.add(w.getId());
                }
            }
            if (engine.getPlayerShip() == ship && (ship.getAllWeapons().size()-weaponid.size())>0) {
                engine.maintainStatusForPlayerShip(this.buffID, this.rule.getSpritePath(), this.rule.getName(), this.rule.getExtra1() + (ship.getAllWeapons().size()-weaponid.size())*CRD+ "%", true);
            }
            ship.getMutableStats().getMissileWeaponDamageMult().modifyMult(this.buffID,1-((ship.getAllWeapons().size()-weaponid.size())*CRD*0.01f));
            ship.getMutableStats().getBallisticWeaponDamageMult().modifyMult(this.buffID,1-((ship.getAllWeapons().size()-weaponid.size())*CRD*0.01f));
            ship.getMutableStats().getEnergyWeaponDamageMult().modifyMult(this.buffID,1-((ship.getAllWeapons().size()-weaponid.size())*CRD*0.01f));
        }
    }
}
