package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.WeaponGroupSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.hullmods.ssp_CLIFF_deliver;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class ssp_superdestoryer_system extends BaseShipSystemScript {
    public static float SYSTEM_RANGE = 900f;
    protected boolean runonce=false;
    protected float block_teleport=0f;
    protected float echo_createHP=0f;
    protected ShipAPI echo;
    protected Map<String, Float> wpn_angle_map = new HashMap();
    protected Map<String, Boolean> wpn_fireing_map = new HashMap();
    public IntervalUtil interval = new IntervalUtil(0.05f,0.05f);//最小,最大
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship=null;
        if(stats.getEntity()!=null){
            ship= (ShipAPI) stats.getEntity();
        }
        if(ship==null)return;
        if(!runonce){
            ShipVariantAPI v=stats.getVariant();
            for(WeaponGroupSpec wgs : v.getWeaponGroups()){
                if(wgs==null){break;}
                wgs.setAutofireOnByDefault(false);
            }
            echo = Global.getCombatEngine().createFXDrone(v);
            echo.setLayer(CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);
            echo.setOwner(ship.getOriginalOwner());
            echo.getMutableStats().getHullDamageTakenMult().modifyMult(id, 0f); // so it's non-targetable
            echo.getMutableStats().getBallisticWeaponFluxCostMod().modifyMult(id, 0f);
            echo.getMutableStats().getEnergyWeaponFluxCostMod().modifyMult(id, 0f);
            echo.getMutableStats().getMissileWeaponFluxCostMod().modifyMult(id, 0f);
            echo.getMutableStats().getTurnAcceleration().modifyMult(id,10000);
            //同步一些属性。但我觉得没必要
            //echo.getMutableStats().getBallisticRoFMult().modifyMult(id,ship.getMutableStats().getBallisticRoFMult().getModifiedValue());
            //echo.getMutableStats().getEnergyRoFMult().modifyMult(id,ship.getMutableStats().getEnergyRoFMult().getModifiedValue());
            //echo.getMutableStats().getMissileRoFMult().modifyMult(id,ship.getMutableStats().getMissileRoFMult().getModifiedValue());
            //echo.getMutableStats().getBallisticAmmoRegenMult().modifyMult(id,ship.getMutableStats().getBallisticAmmoRegenMult().getModifiedValue());
            //echo.getMutableStats().getEnergyAmmoRegenMult().modifyMult(id,ship.getMutableStats().getEnergyAmmoRegenMult().getModifiedValue());
            //echo.getMutableStats().getMissileAmmoRegenMult().modifyMult(id,ship.getMutableStats().getMissileAmmoRegenMult().getModifiedValue());

            echo.setDrone(true);
            echo.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DRONE_MOTHERSHIP, 100000f, ship);
            echo.setCollisionClass(CollisionClass.FIGHTER);
            Global.getCombatEngine().addEntity(echo);
            echo.getLocation().set(LocationSet(ship));
            if(ship.getShipTarget()!=null){
                echo.setFacing(Misc.getAngleInDegrees(echo.getLocation(),ship.getShipTarget().getLocation()));
            }else{
                echo.setFacing(ship.getFacing());
            }
            echo.setMaxHitpoints(ship.getMaxHitpoints());
            echo.setHitpoints(ship.getHitpoints());
            echo_createHP=ship.getHitpoints();
            runonce=true;
            for(int r=0;r<5;r++){
                Global.getCombatEngine().spawnEmpArcVisual(ship.getLocation(),null,echo.getLocation(),null,5,new Color(160,50, 240),new Color(0,0,0));
            }
        }
        //同步
        interval.advance(Global.getCombatEngine().getElapsedInLastFrame());
        block_teleport+=Global.getCombatEngine().getElapsedInLastFrame();
        echo.setAlphaMult(0.5f*effectLevel);
        //根据鼠标位置调整转向
        if(ship.getMouseTarget()!=null){
            float Targrt_Angle=VectorUtils.getAngle(echo.getLocation(),ship.getMouseTarget());
            float cuurent_Angle=echo.getFacing();

//            if(Targrt_Angle-cuurent_Angle>0){
//                if(Targrt_Angle-cuurent_Angle>=180){
//                    echo.giveCommand(ShipCommand.TURN_RIGHT,0,0);
//                }
//                else if(Targrt_Angle-cuurent_Angle<180){
//                    echo.giveCommand(ShipCommand.TURN_LEFT,0,0);
//                }
//            } else if(Targrt_Angle-cuurent_Angle<0){
//                if(Targrt_Angle-cuurent_Angle>=-180){
//                    echo.giveCommand(ShipCommand.TURN_RIGHT,0,0);
//                }
//                else if(Targrt_Angle-cuurent_Angle<-180){
//                    echo.giveCommand(ShipCommand.TURN_LEFT,0,0);
//                }
//            }
            if(Math.abs(Targrt_Angle-cuurent_Angle)<Global.getCombatEngine().getElapsedInLastFrame() * ship.getMutableStats().getMaxTurnRate().modified) {
                echo.setFacing(Targrt_Angle);
            }else{
                if (Targrt_Angle - cuurent_Angle > 0) {
                    if (Targrt_Angle - cuurent_Angle >= 180) {
                        echo.setFacing(echo.getFacing() - Global.getCombatEngine().getElapsedInLastFrame() * ship.getMutableStats().getMaxTurnRate().modified);
                    } else if (Targrt_Angle - cuurent_Angle < 180) {
                        echo.setFacing(echo.getFacing() + Global.getCombatEngine().getElapsedInLastFrame() * ship.getMutableStats().getMaxTurnRate().modified);
                    }
                } else if (Targrt_Angle - cuurent_Angle < 0) {
                    if (Targrt_Angle - cuurent_Angle >= -180) {
                        echo.setFacing(echo.getFacing() - Global.getCombatEngine().getElapsedInLastFrame() * ship.getMutableStats().getMaxTurnRate().modified);
                    } else if (Targrt_Angle - cuurent_Angle < -180) {
                        echo.setFacing(echo.getFacing() + Global.getCombatEngine().getElapsedInLastFrame() * ship.getMutableStats().getMaxTurnRate().modified);
                    }
                }
            }
            //Global.getLogger(this.getClass()).info(Targrt_Angle+"    "+cuurent_Angle);
        }
        for(WeaponAPI w_s:ship.getAllWeapons()){
            wpn_angle_map.put(w_s.getSlot().getId(),w_s.getCurrAngle()-ship.getFacing());
            if(w_s.isFiring()){
                wpn_fireing_map.put(w_s.getSlot().getId(),true);
            }else {
                wpn_fireing_map.put(w_s.getSlot().getId(),false);
            }
        }
        for (WeaponAPI w_e:echo.getAllWeapons()){
            //朝向同步
            if(wpn_angle_map.get(w_e.getSlot().getId())!=null){
                w_e.setCurrAngle(wpn_angle_map.get(w_e.getSlot().getId())+echo.getFacing());
            }
            //开火同步
            if(wpn_fireing_map.get(w_e.getSlot().getId())!=null){
                if(wpn_fireing_map.get(w_e.getSlot().getId())==true){
                    w_e.setForceFireOneFrame(true);
                }
            }
        }
    }
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship=null;
        if(stats.getEntity()!=null){
            ship= (ShipAPI) stats.getEntity();
        }
        if(ship==null || echo==null)return;
        //必要的判断后，开始干活！
        if(echo.getHitpoints()>echo_createHP){
            Vector2f loc = MathUtils.getRandomPointOnCircumference(echo.getLocation(),echo.getCollisionRadius() * 0.75f);
            float facing = VectorUtils.getAngle(loc,ship.getLocation());
            ssp_CLIFF_deliver.createLifeDeliverMissile(loc,facing,10f,echo,ship,echo.getHitpoints()-echo_createHP);
        }
        //当提前结束技能时
        if(block_teleport<=8.2){
            for(int v=0;v<5;v++){
                Global.getCombatEngine().spawnEmpArcVisual(ship.getLocation(),null,echo.getLocation(),null,5,new Color(160,50, 240),new Color(0,0,0));
            }
            ship.getLocation().set(echo.getLocation());
        }else{
            ship.getSystem().setCooldownRemaining(ship.getSystem().getCooldown()*0.25f);//返还大量冷却时间
        }
        //移除echo并还原参数
        Global.getCombatEngine().removeEntity(echo);
        wpn_angle_map.clear();
        wpn_fireing_map.clear();
        runonce=false;
        block_teleport=0f;
        echo_createHP=0f;
        //提供一个技能位置显示
        if(Global.getCombatEngine().getPlayerShip()==ship){
            Global.getCombatEngine().addLayeredRenderingPlugin(new ssp_superdestoryer_system_Render(ship));
        }
    }

    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (system.isOutOfAmmo()) return null;
        if (system.getState() != ShipSystemAPI.SystemState.IDLE) return null;
        if (isUsable(system, ship)) return "ready";
        else return "out of range";
    }
    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        return true;
    }
    public static class ssp_superdestoryer_system_Render extends BaseCombatLayeredRenderingPlugin {
        protected ShipAPI ship;
        public ssp_superdestoryer_system_Render(ShipAPI ship){
            this.ship=ship;
        }
        @Override
        public float getRenderRadius() {return 5000; }
        @Override
        public boolean isExpired() {
            return ship.isHulk() || ship.getSystem().isOn() || Global.getCombatEngine().getPlayerShip()!=ship;
        }
        @Override
        public void render(CombatEngineLayers layer, ViewportAPI viewport) {
            //SpriteAPI sprite = Global.getSettings().getSprite("fx","ssp_superdestoryer_systemRender");
            SpriteAPI sprite = ship.getSpriteAPI();
            sprite.setAlphaMult(0.2f);
            sprite.renderAtCenter(LocationSet(ship).x,LocationSet(ship).y);
        }
    }
    public static Vector2f LocationSet(ShipAPI ship){
        if(Misc.getDistance(ship.getLocation(),ship.getMouseTarget())<=SYSTEM_RANGE){
            return ship.getMouseTarget();
        }else{
            return MathUtils.getPoint(ship.getLocation(),SYSTEM_RANGE,Misc.getAngleInDegrees(ship.getLocation(),ship.getMouseTarget()));
        }
    }
}