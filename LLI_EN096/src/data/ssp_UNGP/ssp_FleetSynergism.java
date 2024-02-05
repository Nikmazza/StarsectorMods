package data.ssp_UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatInitTag;
import ungp.api.rules.tags.UNGP_PlayerShipSkillTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings.Difficulty;

import java.util.*;

public class ssp_FleetSynergism extends UNGP_BaseRuleEffect implements UNGP_CombatInitTag, UNGP_PlayerShipSkillTag {
    private float max;
    private Map<String, Integer> fleet_member;

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
    public void init(CombatEngineAPI engine) {
    }
    public void apply(FleetDataAPI fleetData, FleetMemberAPI member, MutableShipStatsAPI stats, ShipAPI.HullSize hullSize) {
        if(stats==null){return;}
        if(DPBuff(stats)){
            float num=0f;
            for (FleetMemberAPI n:Global.getSector().getPlayerFleet().getMembersWithFightersCopy()){
                if(n==null) break;
                if(n.getHullSpec().getBaseHullId().equals(stats.getFleetMember().getHullSpec().getBaseHullId())){
                    num+=2;
                }
            }
            float baseCost = stats.getSuppliesToRecover().getBaseValue();
            stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(this.buffID, -(int)(baseCost*0.01f*Math.min(num,max)));
            stats.getSuppliesToRecover().modifyFlat(this.buffID, -(int)(baseCost*0.01f*Math.min(num,max)));
            stats.getSuppliesPerMonth().modifyFlat(this.buffID, -(int)(stats.getSuppliesPerMonth().getBaseValue()*0.01f*Math.min(num,max)));
        }
    }
    public static boolean DPBuff(MutableShipStatsAPI stats) {
        String cc=null;
        Map<String,Integer> fleet_memberMap = new HashMap<String,Integer>();
        HashSet<String> fleet_memberSet = new HashSet<>();
        for (FleetMemberAPI c:Global.getSector().getPlayerFleet().getMembersWithFightersCopy()){
            fleet_memberSet.add(c.getHullSpec().getBaseHullId());
            if(!fleet_memberMap.containsKey(c.getHullSpec().getBaseHullId())){
                fleet_memberMap.put(c.getHullSpec().getBaseHullId(),0);
            }else if(fleet_memberMap.containsKey(c.getHullSpec().getBaseHullId())){
                fleet_memberMap.put(c.getHullSpec().getBaseHullId(),fleet_memberMap.get(c.getHullSpec().getBaseHullId())+1);
            }
        }
        for (String w:fleet_memberSet){
            float count_w=0;
            if(fleet_memberMap.get(w)>count_w){
                count_w=fleet_memberMap.get(w);
                cc=w;
            }
        }
        if(cc!=null){
            if (stats.getEntity() instanceof ShipAPI) {
                ShipAPI ship = (ShipAPI) stats.getEntity();
                return ship.getHullSpec().getBaseHullId().equals(cc);
            } else {
                FleetMemberAPI member = stats.getFleetMember();
                if (member == null) return true;
                return member.getHullSpec().getBaseHullId().equals(cc);
            }
        }
        else return false;
    }
    public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize) {
        stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).unmodify(this.buffID);
    }
}
