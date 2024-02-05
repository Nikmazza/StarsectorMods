package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

public class ssp_PhaseTransferField extends BaseShipSystemScript {
    protected boolean RunOnce=false;
    public static float EMPExplosion_Range=600f;
    public static Color COLOR = new Color(165,0,190,255);
    public static class ShipData {
        public ShipAPI ship;
        public float Stage;
        public BaseCombatLayeredRenderingPlugin RenderPlugin;
        public ShipData(ShipAPI ship) {
            this.ship = ship;
        }
    }
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship=null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return; }
        //加listener
        if(!ship.hasListenerOfClass(ssp_PhaseTransferField_EffectMod.class)){
        ship.addListener(new ssp_PhaseTransferField_EffectMod(ship,id));
        }
        stats.getHullDamageTakenMult().modifyMult(id,0.5f);
        stats.getArmorDamageTakenMult().modifyMult(id,0.5f);
        stats.getEmpDamageTakenMult().modifyMult(id,0.00f);
        if(state==State.IN){
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 0);
        for(WeaponAPI weapon:ship.getAllWeapons()){ if (weapon.isDisabled()) {weapon.repair();} }
        }
        //加特效
        final String ShipDataKey = ship.getId()+"ssp_PhaseTransferField_ShipData";
        Object ShipDataObj = Global.getCombatEngine().getCustomData().get(ShipDataKey);

        if (state == State.IN && ShipDataObj == null ) {
            Global.getCombatEngine().getCustomData().put(ShipDataKey, new ssp_PhaseTransferField.ShipData(ship));
        } else if (state == State.IDLE && ShipDataObj != null) {
            Global.getCombatEngine().getCustomData().remove(ShipDataKey);
            ShipDataObj = null;
        }
        if (ShipDataObj == null) return;
        final ssp_PhaseTransferField.ShipData ShipData = (ssp_PhaseTransferField.ShipData) ShipDataObj;
        ShipData.Stage = effectLevel ;
        //if (ShipData.RenderPlugin == null) {
            ShipData.RenderPlugin = new BaseCombatLayeredRenderingPlugin() {
                public void render(CombatEngineLayers layer, ViewportAPI viewport) {
                        SpriteAPI sprite = Global.getSettings().getSprite("fx", "ssp_HitMeYouWeekMissile");
                        sprite.setColor(new Color(215, 55, 255, 120));
                        sprite.setWidth(EMPExplosion_Range*2);
                        sprite.setHeight(EMPExplosion_Range*2);
                        sprite.setAdditiveBlend();
                        sprite.setAlphaMult(ShipData.Stage);
                        sprite.renderAtCenter(ShipData.ship.getLocation().x, ShipData.ship.getLocation().y);

                }
                @Override
                public float getRenderRadius() {return 5000; }
               @Override
               public boolean isExpired() { return ShipData.ship.getSystem().isCoolingDown(); }
            };
        if(!RunOnce){
            RunOnce=true;
            Global.getCombatEngine().addLayeredRenderingPlugin(ShipData.RenderPlugin);
        }

        //}
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship=null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return; }
        RunOnce=false;
        stats.getHullDamageTakenMult().unmodify(id);
        stats.getArmorDamageTakenMult().unmodify(id);
        stats.getCombatEngineRepairTimeMult().unmodify(id);
        if(ship.hasListenerOfClass(ssp_PhaseTransferField_EffectMod.class)){
            ship.removeListenerOfClass(ssp_PhaseTransferField_EffectMod.class);
            Ending_EMPExplosion(ship);
        }
    }

    public static class ssp_PhaseTransferField_EffectMod implements DamageTakenModifier, AdvanceableListener {
        protected ShipAPI ship;
        protected String id;
        private float TotalDamageTaken=0f;
        public ssp_PhaseTransferField_EffectMod(ShipAPI ship, String id) {
            this.ship = ship;
            this.id = id;
        }
        @Override
        public void advance(float amount) {
            float BONUS=1f;
            if (ship.getVariant().hasHullMod("ssp_LongerRange")) {BONUS=0.5f;}
            if (ship.getVariant().hasHullMod("ssp_ShortRange")) {BONUS=1.20f;}
            //可视化
            if (Global.getCombatEngine().getPlayerShip() == ship) {
                Global.getCombatEngine().maintainStatusForPlayerShip("ssp_PhaseTransferField_EffectMod", "graphics/icons/hullsys/damper_field.png", SSPI18nUtil.getShipSystemString("ssp_PhaseTransferField_title"),
                        String.format(SSPI18nUtil.getShipSystemString("ssp_PhaseTransferField"),(int)(ship.getHullSpec().getHitpoints()*0.5f*BONUS-TotalDamageTaken) ), false);
            }
        }
        @Override
        public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
        float BONUS=1f;
        if (ship.getVariant().hasHullMod("ssp_LongerRange")) {BONUS=0.5f;}
        if (ship.getVariant().hasHullMod("ssp_ShortRange")) {BONUS=1.20f;}
        if(!shieldHit){
            TotalDamageTaken += damage.getDamage();
        }
        String id = "ssp_PhaseTransferField_EffectMod";
        damage.getModifier().modifyMult(id, 0f);
        if(TotalDamageTaken < ship.getHullSpec().getHitpoints()*0.5f*BONUS){
            return id;
        }else{
            ship.getFluxTracker().beginOverloadWithTotalBaseDuration(0.5f);
            return null;
        }
        }
    }
    public static void Ending_EMPExplosion(ShipAPI ship){
        float MULT=1f;
        if (ship.getVariant().hasHullMod("ssp_LongerRange")) {MULT=4f;}
        List<ShipAPI> TargetInRange= AIUtils.getNearbyEnemies(ship, EMPExplosion_Range);
        for (ShipAPI Target : TargetInRange) {
            if (Target == null) return;
            if (!Target.isHulk() && Target.getCollisionClass() == CollisionClass.SHIP || Target.getCollisionClass() == CollisionClass.FIGHTER) {
                Global.getCombatEngine().spawnEmpArc(ship, MathUtils.getRandomPointInCircle(ship.getLocation(),ship.getCollisionRadius()), null, Target, DamageType.ENERGY, 400*(1+ship.getFluxLevel())*MULT, 1000*(1+ship.getFluxLevel())*MULT, 100000, null, 10, COLOR, COLOR);
            }
        }
        for (int i=0;i<6;i++) {
            Global.getCombatEngine().spawnEmpArcVisual(MathUtils.getRandomPointInCircle(ship.getLocation(),ship.getCollisionRadius()*0.8f),null,MathUtils.getRandomPointInCircle(ship.getLocation(),500+ship.getCollisionRadius()),null,1,COLOR,COLOR);
        }
        for (int i=0;i<80;i++) {
            Vector2f Vel = new Vector2f (0,0);
            float Random=MathUtils.getRandomNumberInRange(1f,8f);
            Global.getCombatEngine().addHitParticle(ship.getLocation(),Vel.set(VectorUtils.rotate(new Vector2f(100f*Random, 0f), MathUtils.getRandomNumberInRange(0,360))),10f,1f,MathUtils.getRandomNumberInRange(1f, 2f),COLOR);
        }
    }



}
