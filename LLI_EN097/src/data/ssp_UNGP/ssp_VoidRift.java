package data.ssp_UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.SSP_NegativeExplosionVisual;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.WaveDistortion;
import org.lwjgl.util.vector.Vector2f;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.awt.*;

public class ssp_VoidRift extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float ssp_VoidRift_timer;
    private float timer=60;
    public ssp_VoidRift(){}
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        this.ssp_VoidRift_timer = this.getValueByDifficulty(0, difficulty);
    }
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if(index == 0){
            return difficulty.getLinearValue(1f, 2f);
        }else{
            return super.getValueByDifficulty(index, difficulty);
        }
    }
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if(index==0)return ""+(int)(90/(this.getValueByDifficulty(0, difficulty)/4+1));
//        else if(index==1)return "1";
//        else if(index==2)return "120";
//        else if(index==3)return "1";
        return null;
    }
    public void advanceInCombat(CombatEngineAPI engine, float amount) {
        timer+=amount;
        boolean stop=false;
        if(engine.getTotalElapsedTime(false)<=1) {
            CombatFleetManagerAPI fleetManager = engine.getFleetManager(1);
            for(ShipAPI s:engine.getShips()){
                if(s.getHullSpec().getHullId().equals("ssp_voidrift")){
                    stop=true;
                }
                if(Global.getCombatEngine().isSimulation()){
                    stop=true;
                }
            }
            if (engine.isEnemyInFullRetreat() || stop) {
                return;
            }
            //Global.getCombatEngine().getCombatUI().addMessage(0, Color.RED, "spawned");
            boolean wasSuppressed = fleetManager.isSuppressDeploymentMessages();
            fleetManager.setSuppressDeploymentMessages(true);
            // float y= MathUtils.getRandomNumberInRange(-Global.getCombatEngine().getMapHeight()/2*0.8f,Global.getCombatEngine().getMapHeight()/2*0.8f);
            // float x= MathUtils.getRandomNumberInRange(-Global.getCombatEngine().getMapWidth()/2*0.8f,Global.getCombatEngine().getMapWidth()/2*0.8f);
            Vector2f point = new Vector2f(0, Global.getCombatEngine().getMapWidth() / 2 * 0.6f);
            SpawnWD(point);
            engine.getFleetManager(1).spawnShipOrWing("ssp_voidrift_variant", point, 0);
            fleetManager.setSuppressDeploymentMessages(wasSuppressed);
        }

        if(timer>=90/(ssp_VoidRift_timer/4+1)){
            ShipAPI ship=null;
            for(ShipAPI s:engine.getShips()){
                if(s.getHullSpec().getHullId().equals("ssp_voidrift")){
                    ship=s;
                }
            }
            if (ship != null) {
                CombatFleetManagerAPI fleetManager = engine.getFleetManager(1);
                if (engine.isEnemyInFullRetreat()) {
                    return;
                }
                boolean wasSuppressed = fleetManager.isSuppressDeploymentMessages();
                fleetManager.setSuppressDeploymentMessages(true);
                SSP_NegativeExplosionVisual.SSP_NEParams p = createStandardRiftParams(new Color(100,100,255,255), 100f);
                spawnStandardRift(ship.getLocation(), p);
                ShipAPI s=Global.getCombatEngine().getFleetManager(1).spawnShipOrWing(GetShipOrWing(), ship.getLocation(), (float)(360*Math.random()));
                s.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING,114514f);
                fleetManager.setSuppressDeploymentMessages(wasSuppressed);
            }

            timer=0;
        }
    }


    public String GetShipOrWing(){
        WeightedRandomPicker<String> VariantPicker = new WeightedRandomPicker();
        VariantPicker.add("defender_PD", 18.5F);
        VariantPicker.add("warden_Defense", 18.5F);
        VariantPicker.add("picket_Assault", 18.5F);
        VariantPicker.add("sentry_FS", 18.5F);
        VariantPicker.add("bastillon_Standard", 10.0F);
        VariantPicker.add("berserker_Assault", 10.0F);
        VariantPicker.add("rampart_Standard", 5.0F);
        VariantPicker.add("guardian_Standard", 1.0F);
        return VariantPicker.pick();
    }
    //定义裂隙的其他属性，此处的颜色均为实际效果颜色
    public static SSP_NegativeExplosionVisual.SSP_NEParams createStandardRiftParams(Color borderColor, float radius) {
        SSP_NegativeExplosionVisual.SSP_NEParams p = new SSP_NegativeExplosionVisual.SSP_NEParams();
        p.hitGlowSizeMult = 0.75f;
        p.spawnHitGlowAt = 0f;
        p.noiseMag = 1f;
        p.fadeIn = 0.1f;
        p.underglow = new Color(100, 0, 25, 100);
        p.withHitGlow = true;
        p.radius = radius;
        p.color = borderColor;
        return p;
    }
    //使用定义过的裂隙的属性，生成裂隙
    public static void spawnStandardRift(Vector2f loc, SSP_NegativeExplosionVisual.SSP_NEParams params) {
        CombatEngineAPI engine = Global.getCombatEngine();

        CombatEntityAPI prev = null;
        for (int i = 0; i < 2; i++) {
            SSP_NegativeExplosionVisual.SSP_NEParams p = params.clone();
            p.radius *= 0.75f + 0.5f * (float) Math.random();
            p.withHitGlow = prev == null;
            loc = Misc.getPointAtRadius(loc, p.radius * 0.4f);
            CombatEntityAPI e = engine.addLayeredRenderingPlugin(new SSP_NegativeExplosionVisual(p));
            e.getLocation().set(loc);
            if (prev != null) {
                float dist = Misc.getDistance(prev.getLocation(), loc);
                Vector2f vel = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(loc, prev.getLocation()));
                vel.scale(dist / (p.fadeIn + p.fadeOut) * 0.7f);
                e.getVelocity().set(vel);
            }
            prev = e;
        }

    }

    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) { }
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {}
    public void SpawnWD(Vector2f Loc){
        //扭曲特效
        WaveDistortion WD = new WaveDistortion();
        WD.setLocation(Loc);
        WD.setSize(200f);
        WD.setArc(0,360);
        WD.setArcAttenuationWidth(2f);
        WD.setLifetime(0.5f);
        WD.setAutoFadeSizeTime(0.5f);
        WD.setAutoFadeIntensityTime(0.5f);
        DistortionShader.addDistortion(WD);
    }


}
