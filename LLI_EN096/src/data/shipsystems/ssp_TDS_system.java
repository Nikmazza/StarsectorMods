package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ssp_TDS_system extends BaseShipSystemScript {
    protected boolean RunOnce=false;
    public static Color JITTER_COLOR = new Color(255, 255, 255,255);
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship=null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return; }
        float Mult=1f;
        if(!RunOnce){
            RunOnce=true;
            if(ship.getVariant().hasHullMod("ssp_LongerRange")){ship.getFluxTracker().increaseFlux(ship.getSystem().getFluxPerUse()*2,true);}
        }
        if(ship.getVariant().hasHullMod("ssp_ShortRange")){Mult=0f;}
        stats.getEnergyDamageTakenMult().modifyMult(id,0);
        stats.getKineticDamageTakenMult().modifyMult(id,Mult);
        stats.getHighExplosiveDamageTakenMult().modifyMult(id,Mult);
        stats.getFragmentationDamageTakenMult().modifyMult(id,Mult);
        ship.setJitterUnder(ship,JITTER_COLOR,2,4,5);
        ship.setJitterShields(false);
        if(state==State.IN){
            stats.getAcceleration().modifyMult(id, 1000);
            stats.getDeceleration().modifyMult(id, 1000);
            stats.getMaxSpeed().modifyFlat(id,200);
        }
        else if(state==State.ACTIVE){
            //ship.getVelocity().set(VectorUtils.rotate(new Vector2f(100f, 0f), VectorUtils.getFacing(ship.getVelocity())));//推力无视机动性
            //SpawnWD(ship.getLocation(),ship);//视觉特效
            stats.getAcceleration().modifyMult(id, 0);
            stats.getDeceleration().modifyMult(id, 0);
            stats.getMaxSpeed().modifyFlat(id,200);
        }
        else if(state==State.OUT){
            stats.getAcceleration().modifyMult(id, 1000);
            stats.getDeceleration().modifyMult(id, 1000);
            stats.getMaxSpeed().modifyFlat(id,0);
        }
    }
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship=null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return; }
        stats.getEnergyDamageTakenMult().unmodify(id);
        stats.getKineticDamageTakenMult().unmodify(id);
        stats.getHighExplosiveDamageTakenMult().unmodify(id);
        stats.getFragmentationDamageTakenMult().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
        //复位
//        for(WeaponAPI WS: ship.getAllWeapons()){
//            if(WS.getSlot().isDecorative()){
//                WS.setCurrAngle(WS.getSlot().getAngle()+ship.getFacing());}
//        }
        RunOnce=false;
    }
    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData(SSPI18nUtil.getShipSystemString("ssp_TDS_system"), false);
        }
        return null;
    }
    public float getRegenOverride(ShipAPI ship) {
        if(ship.getVariant().hasHullMod("ssp_LongerRange")){return 0.15f;}
        else return -1;
    }
    public int getUsesOverride(ShipAPI ship) {
        if(ship.getVariant().hasHullMod("ssp_LongerRange")){return 3;}
        else return -1;
    }
//    public void SpawnWD(Vector2f Loc, ShipAPI ship){
//        float XX=ship.getVelocity().getX();
//        float YY=ship.getVelocity().getY();
//        Vector2f ShipVel=new Vector2f (-XX,YY);
//        float ShipVelocityFacing=VectorUtils.getFacing(ShipVel);
//        //喷射特效
//        for(WeaponAPI WS: ship.getAllWeapons()){
//            if(WS.getSlot().isDecorative() && Misc.isInArc(WS.getCurrAngle(),95,-ShipVelocityFacing)){
//                WS.setCurrAngle(-ShipVelocityFacing);
//                WS.setForceFireOneFrame(true);
//            }
//        }
        //扭曲特效
//        float Arc=70;
//        WaveDistortion WD = new WaveDistortion();
//        //RippleDistortion WD = new RippleDistortion();
//        WD.setLocation(Loc);
//        WD.setSize(ship.getCollisionRadius()*2f);
//        WD.setVelocity(ship.getVelocity());
//        WD.setArc(VectorUtils.getFacing(ship.getVelocity())-Arc,VectorUtils.getFacing(ship.getVelocity())+Arc);
//        WD.setArcAttenuationWidth(2f);
//        WD.setLifetime(0.5f);
//        WD.setAutoFadeSizeTime(0.5f);
//        WD.setAutoFadeIntensityTime(0.5f);
//        DistortionShader.addDistortion(WD);
        //烟雾特效
        //Vector2f Vel = new Vector2f (0,0);
        //Global.getCombatEngine().addNebulaSmokeParticle(Loc,Vel.set(VectorUtils.rotate(new Vector2f(-10f, 0f), ShipVelocityFacing)),ship.getCollisionRadius()*1.2f,1f,0.1f,0.1f,1f, Color.GRAY);

    //}
}
