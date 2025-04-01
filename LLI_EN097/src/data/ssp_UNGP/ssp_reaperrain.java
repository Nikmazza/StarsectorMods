package data.ssp_UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ssp_reaperrain extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private static Map mag = new HashMap();
    static {
        mag.put(ShipAPI.HullSize.FRIGATE, 1f);
        mag.put(ShipAPI.HullSize.DESTROYER, 2f);
        mag.put(ShipAPI.HullSize.CRUISER, 4f);
        mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 8f);
    }
    private float ssp_reperrain_timer;
    private float timer=0;
    private int num=10;
    public ssp_reaperrain(){}
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        this.ssp_reperrain_timer = this.getValueByDifficulty(0, difficulty);
    }
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if(index == 0){
            return difficulty.getLinearValue(1f, 2f);
        }else{
            return super.getValueByDifficulty(index, difficulty);
        }
    }
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if(index==0)return ""+(int)(120/(this.getValueByDifficulty(0, difficulty)+1));
        else if(index==1)return ""+num;
        else if(index==2)return Global.getSettings().getWeaponSpec("ssp_reaper").getWeaponName();
        return null;
    }
    public void advanceInCombat(CombatEngineAPI engine, float amount) {
        timer+=amount;
        WeightedRandomPicker<ShipAPI> TgtPicker = new WeightedRandomPicker();
        if(timer>=120/(ssp_reperrain_timer+1)){
            //先决定方向
            int left=-1;
            if(Math.random()<=0.5){left=1;}
            if(left<0){
                Global.getCombatEngine().getCombatUI().addMessage(0, Color.RED,this.rule.getExtra1());
            }else{
                Global.getCombatEngine().getCombatUI().addMessage(0,Color.RED,this.rule.getExtra2());
            }
            //确定目标，设置权重
            for(ShipAPI s:Global.getCombatEngine().getShips()){
                if(s.getOwner()==1)continue;
                if(s.getHullSize()== ShipAPI.HullSize.FIGHTER)continue;
                TgtPicker.add(s, (Float) mag.get(s.getHullSize()));
            }
            //发射
            for(int i=0;i<num;i++){
                ShipAPI targrt=TgtPicker.pick();
                if(targrt==null)return;
                float locY=targrt.getLocation().getY()+MathUtils.getRandomNumberInRange(-targrt.getCollisionRadius()*2,targrt.getCollisionRadius()*2);
                Vector2f spawnloc =new Vector2f((Global.getCombatEngine().getMapWidth()+MathUtils.getRandomNumberInRange(0,400))/2*left,locY);
                CombatEntityAPI proj = Global.getCombatEngine().spawnProjectile(null, null, "ssp_reaper",spawnloc,90+90*left,null);
                proj.setOwner(1);
            }
            timer=0;
        }
    }
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) { }
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {}
}
