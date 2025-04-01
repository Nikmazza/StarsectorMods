package data.ssp_UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatInitTag;
import ungp.api.rules.tags.UNGP_PlayerShipSkillTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings.Difficulty;

import java.util.*;

public class ssp_FleetSynergism extends UNGP_BaseRuleEffect implements UNGP_CombatInitTag, UNGP_PlayerShipSkillTag {
    private float max;
    public ssp_FleetSynergism() {
    }
    public void updateDifficultyCache(Difficulty difficulty) {
        this.max = this.getValueByDifficulty(1, difficulty);
    }
    public float getValueByDifficulty(int index, Difficulty difficulty) {
        if(index == 1){
            return difficulty.getLinearValue(8, 8);
        }else{
            return super.getValueByDifficulty(index, difficulty);
        }
    }
    public String getDescriptionParams(int index, Difficulty difficulty) {
        if(index==0)return "2%";
        else if(index==1)return this.getPercentString(this.getValueByDifficulty(index, difficulty));
        return null;
    }
    public boolean addIntelTips(TooltipMakerAPI imageTooltip) {
        if(DPBuff()==null)return false;
        imageTooltip.addPara(this.rule.getExtra1(), 0.0F);
        imageTooltip.addPara(Global.getSettings().getHullSpec(DPBuff()).getNameWithDesignationWithDashClass(), Misc.getHighlightColor(), 5.0F);
        return true;
    }
    public void init(CombatEngineAPI engine) {
    }
    public void apply(FleetDataAPI fleetData, FleetMemberAPI member, MutableShipStatsAPI stats, ShipAPI.HullSize hullSize) {
        if(member==null || stats==null){return;}
        if(member.getHullSpec().getBaseHullId().equals(DPBuff())){//当前舰船id等于可buff舰船id
            float num=0f;
            for (FleetMemberAPI n:Global.getSector().getPlayerFleet().getMembersWithFightersCopy()){
                if(n==null) break;
                if(stats.getFleetMember() == null) continue;
                if(n.getHullSpec().getBaseHullId().equals(stats.getFleetMember().getHullSpec().getBaseHullId())){
                    num+=2;
                }
            }
            float baseCost = stats.getSuppliesToRecover().getBaseValue();
            stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(this.buffID, -(int)(baseCost*0.01f*Math.min(num,max)));
            stats.getSuppliesToRecover().modifyFlat(this.buffID, -(int)(baseCost*0.01f*Math.min(num,max)));
            stats.getSuppliesPerMonth().modifyFlat(this.buffID, -(int)(stats.getSuppliesPerMonth().getBaseValue()*0.01f*Math.min(num,max)));
        }else{
            stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).unmodify(this.buffID);
            stats.getSuppliesToRecover().unmodify(this.buffID);
            stats.getSuppliesPerMonth().unmodify(this.buffID);
        }
    }
    public static String DPBuff() {
        String cc=null;//可buff舰船id
        Map<String,Integer> fleet_memberMap = new HashMap<>();//id和重复数量的map
        HashSet<String> fleet_memberSet = new HashSet<>();//用于比对的不重复id的set
        if (Global.getSector().getPlayerFleet() == null) return "";
        for (FleetMemberAPI c:Global.getSector().getPlayerFleet().getMembersWithFightersCopy()){//对于所有舰船（包括战机？）
            if(c==null) break;
            if(c.getHullSpec().getHullSize() == ShipAPI.HullSize.FIGHTER) continue;//跳过战机
            fleet_memberSet.add(c.getHullSpec().getBaseHullId());//向集合中放舰队中所有舰船的id合集（不重复
            if(!fleet_memberMap.containsKey(c.getHullSpec().getBaseHullId())){//map里面塞id，和重复数量
                fleet_memberMap.put(c.getHullSpec().getBaseHullId(),0);//放id，第一次塞所以重复数量=0
            }else if(fleet_memberMap.containsKey(c.getHullSpec().getBaseHullId())){//如果map里面存在了id，那么更新map
                fleet_memberMap.put(c.getHullSpec().getBaseHullId(),fleet_memberMap.get(c.getHullSpec().getBaseHullId())+1);//放一个新的进去但是重复数量+1
            }
        }
        float count_w=0;//一个用于比较的数值
        for (String w:fleet_memberSet){//对set里面的id而言
            if(fleet_memberMap.get(w)>count_w){//如果当前id在map里面对应的id的value大于这个比较的数值
                count_w=fleet_memberMap.get(w);//比较数值=当前id在map中的value
                cc=w;//用于决定buff船体id的变量=当前id
            }
        }
        return cc;
    }
    public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize) {
        stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).unmodify(this.buffID);
        stats.getSuppliesToRecover().unmodify(this.buffID);
        stats.getSuppliesPerMonth().unmodify(this.buffID);
    }
}
