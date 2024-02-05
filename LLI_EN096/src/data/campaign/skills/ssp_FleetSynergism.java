package data.campaign.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.ArrayList;


public class ssp_FleetSynergism {
    public static float DP_REDUCTION = 2f;
    public static class Level1 implements ShipSkillEffect {
        public static boolean DPBuff(MutableShipStatsAPI stats) {
            ArrayList<String> cc=new ArrayList();
            for (FleetMemberAPI c:Global.getSector().getPlayerFleet().getMembersWithFightersCopy()){
                if(c.getCaptain().getStats().hasSkill("ssp_FleetSynergism")){
                    cc.add(c.getHullSpec().getHullId());
                }
            }
            if(cc!=null && cc.size()>0){
                if (stats.getEntity() instanceof ShipAPI) {
                    ShipAPI ship = (ShipAPI) stats.getEntity();
                    return ship.getHullSpec().getHullId().equals(cc.get(0));
                } else {
                    FleetMemberAPI member = stats.getFleetMember();
                    if (member == null) return true;
                    return member.getHullSpec().getHullId().equals(cc.get(0));
                }
            }
            else return false;
        }
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
            if (DPBuff(stats)) {
                float num=0f;
                for (FleetMemberAPI n:Global.getSector().getPlayerFleet().getMembersWithFightersCopy()){
                    if(n.getHullSpec().getHullId().equals(stats.getFleetMember().getHullId())){
                        num+=1;
                    }
                }
                float baseCost = stats.getSuppliesToRecover().getBaseValue();
                stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat("ssp_FleetSynergism_DP_REDUCTION_ID", -baseCost*DP_REDUCTION*0.01f*num);
            }
        }

        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).unmodifyFlat("ssp_FleetSynergism_DP_REDUCTION_ID");
        }

        public String getEffectDescription(float level) {
            return "For each ship in the fleet of the same type as the ship piloted by an officer (including the player) with this skill, the deployment points of those ships will be reduced by 2%.\nExample:If the officer commands a Hammerhead-class destroyer and there are 4 other Hammerheads in the fleet, all Hammerheads will have their DP reduced by 10% (5 x 2%).";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
//    public static class Level1 extends BaseSkillEffectDescription implements AfterShipCreationSkillEffect {
//        public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
//            ship.addListener(new ssp_FleetSynergism_listener(ship));
//        }
//        public void unapplyEffectsAfterShipCreation(ShipAPI ship, String id) {
//            ship.removeListenerOfClass(ssp_FleetSynergism_listener.class);
//        }
//        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) { }
//        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) { }
//        public String getEffectDescription(float level) {
//            return "舰队中每拥有一艘与该技能持有者所指挥舰船相同的我方舰船，都可以降低这些舰船2%的部署点";
//        }
//        public String getEffectPerLevelDescription() {
//            return null;
//        }
//        public ScopeDescription getScopeDescription() {
//            return ScopeDescription.PILOTED_SHIP;
//        }
//    }
//    public static class ssp_FleetSynergism_listener implements AdvanceableListener {
//        protected ShipAPI ship;
//        protected int num=0;
//        public ssp_FleetSynergism_listener(ShipAPI ship) {
//            this.ship = ship;
//        }
//        public void advance(float amount) {
//            String SHIP_ID=ship.getHullSpec().getHullId();
//            for(ShipAPI Fleet:Global.getCombatEngine().getShips()){
//                if(Fleet==null){break;}
//                if(Fleet.getOwner()==ship.getOwner() && Fleet.getHullSpec().getHullId().equals(SHIP_ID)){
//                    num+=1;
//                }
//            }
//            for(ShipAPI Fleet:Global.getCombatEngine().getShips()){
//                if(Fleet==null){break;}
//                if(Fleet.getOwner()==ship.getOwner() && Fleet.getHullSpec().getHullId().equals(SHIP_ID)){
//                    float reduction=
//                            //Fleet.getMutableStats().getSuppliesToRecover().getBaseValue()*
//                            //num*
//                                    5f;
//                    Fleet.getMutableStats().getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat("ssp_FleetSynergism_DP_REDUCTION_ID", -(int)reduction);
//                }
//            }
//        }
//    }
}
