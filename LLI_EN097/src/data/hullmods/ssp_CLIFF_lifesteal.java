package data.hullmods;

import com.fs.starfarer.C;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.ProjectileSpawnType;
import com.fs.starfarer.api.loading.ProjectileSpecAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.CombatViewport;
import com.fs.starfarer.combat.entities.BaseEntity;
import com.fs.starfarer.combat.entities.ship.G;
import com.fs.starfarer.combat.o0OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO.B;
import com.fs.starfarer.combat.o0OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO.OOoO;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.plugins.MagicAutoTrails;
import org.magiclib.plugins.MagicTrailPlugin;
import org.magiclib.util.MagicTrailObject;
import org.magiclib.util.MagicTrailTracker;

import java.awt.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ssp_CLIFF_lifesteal extends ssp_CLIFF_basehullmod {
    private static Map mag = new HashMap();
    static {
        mag.put(ShipAPI.HullSize.FRIGATE, 800f);
        mag.put(ShipAPI.HullSize.DESTROYER, 1200f);
        mag.put(ShipAPI.HullSize.CRUISER, 1600f);
        mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 2000f);
    }
    public IntervalUtil interval = new IntervalUtil(0.5f,0.5f);//最小,最大
    public class ssp_CLIFF_lifesteal_customdata{
        Map<ShipAPI, CombatEntityAPI> Spritemap = new HashMap();
        public ssp_CLIFF_lifesteal_customdata(){}
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if(ship == null){return;}
        if(!ship.isAlive()){return;}
        interval.advance(amount);
        ssp_CLIFF_lifesteal_customdata CustomData = (ssp_CLIFF_lifesteal_customdata) Global.getCombatEngine().getCustomData().get("ssp_CLIFF_lifesteal");
        if(CustomData==null){
            CustomData = new ssp_CLIFF_lifesteal_customdata();
            Global.getCombatEngine().getCustomData().put("ssp_CLIFF_lifesteal",CustomData);
        }
        if(!CustomData.Spritemap.containsKey(ship) && ship==Global.getCombatEngine().getPlayerShip()){
            CombatEntityAPI e = Global.getCombatEngine().addLayeredRenderingPlugin(new ssp_CLIFF_lifesteal_render(ship));
            CustomData.Spritemap.put(ship,e);
        }
        if(interval.intervalElapsed()){
            //吸血光环
            for(ShipAPI s: CombatUtils.getShipsWithinRange(ship.getLocation(),(Float) mag.get(ship.getHullSize()))){
                if(!s.hasListenerOfClass(ssp_CLIFF_lifesteal_listener.class) && s.isAlive() && s!=ship && !s.isHulk()){
                    if(s.getOwner()*ship.getOwner()==0 && s.getHullSize() != ShipAPI.HullSize.FIGHTER){
                        s.addListener(new ssp_CLIFF_lifesteal_listener());
                    }
                }
            }
        }
    }
    public static class ssp_CLIFF_lifesteal_render extends BaseCombatLayeredRenderingPlugin {
        protected ShipAPI ship;
        public ssp_CLIFF_lifesteal_render(ShipAPI ship) {
            this.ship = ship;
        }
        @Override
        public float getRenderRadius() {return 5000; }
        @Override
        public boolean isExpired() {
            return ship.isHulk()||ship!=Global.getCombatEngine().getPlayerShip()||!Global.getCombatEngine().isEntityInPlay(ship);
        }
        @Override
        public void render(CombatEngineLayers layer, ViewportAPI viewport) {
            float size=(Float) mag.get(ship.getHullSize());
            SpriteAPI sprite = Global.getSettings().getSprite("fx", "ssp_verybigO");
            sprite.setColor(new Color(155,15,15,255));
            sprite.setWidth(size*2f);
            sprite.setHeight(size*2f);
            sprite.setAdditiveBlend();
            sprite.setAlphaMult(0.2f);
            sprite.renderAtCenter(ship.getLocation().x, ship.getLocation().y);
        }
    }
    public static class ssp_CLIFF_lifesteal_listener implements DamageListener {
        private HashMap<ShipAPI, Float> map= new HashMap<>();

        public static final float TRIGGER_THRESHOLD = 250f;//每积攒250伤害就触发一次回血导弹
        public static final float STEAL_PERCENT = 0.20f;//结构偷取百分比

        public ssp_CLIFF_lifesteal_listener(){ }
        //汇报伤害
        public void reportDamageApplied(Object source, CombatEntityAPI target, ApplyDamageResultAPI result) {
            if(target instanceof ShipAPI && !((ShipAPI)target).isAlive()){
                ((ShipAPI)target).removeListenerOfClass(ssp_CLIFF_lifesteal_listener.class);
            }
            if(target instanceof ShipAPI && ((ShipAPI)target).isHulk()){
                ((ShipAPI)target).removeListenerOfClass(ssp_CLIFF_lifesteal_listener.class);
            }

            if(target instanceof ShipAPI && source instanceof ShipAPI){
                ShipAPI SOURCE=(ShipAPI) source;//我受不了了
                ShipAPI TARGET=(ShipAPI) target;//我受不了了
                if(SOURCE.getVariant().hasHullMod("ssp_CLIFF_lifesteal")
                        && Misc.getDistance(SOURCE.getLocation(),target.getLocation())<=(Float) mag.get(SOURCE.getHullSize())){
                    //只管攒够伤害就生成一次回血导弹，至于回血是否溢出在导弹里面写
                    float hullDamage = result.getDamageToHull();
                    if(hullDamage > 0f) accumulateDamage(TARGET, SOURCE, hullDamage);
                }
            }
        }
        //计算造成伤害数值，大于阈值则触发
        public void accumulateDamage(ShipAPI target, ShipAPI source, float damage){
            float now = 0f;
            if(map.containsKey(target)){
                now = map.get(target);
            }
            now += damage;
            while (now > TRIGGER_THRESHOLD){
                now -= TRIGGER_THRESHOLD;
                Vector2f loc = MathUtils.getRandomPointOnCircumference(target.getLocation(),target.getCollisionRadius() * 0.75f);
                float facing = VectorUtils.getAngle(target.getLocation(), loc);
                createLifeStealMissile(
                        loc,
                        facing,
                        10f, target , source,
                        TRIGGER_THRESHOLD * STEAL_PERCENT);
            }
            map.put(target,now);
        }
    }
    //生成回血实体
    public static void createLifeStealMissile(Vector2f loc, float facing, float speed, ShipAPI from , ShipAPI to, float toHeal){
        //生成导弹，拖尾在magicTrail里面自己改吧
        //机动性在proj文件里面
        MissileAPI shot = (MissileAPI) Global.getCombatEngine().spawnProjectile(
                from,null,"ssp_lifesteal",loc,facing, Misc.ZERO);
        //打友军的时候，翻转导弹所属
        if(from.getOwner()==to.getOwner()){if(shot.getOwner()==0){shot.setOwner(1);}else if(shot.getOwner()==1){shot.setOwner(0);}}
        GuidedMissileAI ai = (GuidedMissileAI) shot.getMissileAI();
        shot.getVelocity().scale(speed);
        ai.setTarget(to);
        //继续生成回血实体特效
        Global.getCombatEngine().addLayeredRenderingPlugin(new ssp_CreateEntity.ProcessEntityMoving(shot,toHeal, to));
    }
    //描述
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if(ship==null)return;
        float opad = 10f;
        float mult = ship.getMutableStats().getDynamic().getMod("ssp_CLIFF_morehp").computeEffective(1f);
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_CLIFF_lifesteal_tooltip0"),
                opad, h, "800/1200/1600/2000","250","50",SSPI18nUtil.getHullModString("ssp_hpmessager"),SSPI18nUtil.getHullModString("ssp_hpmessager"),"90%");
        label.setHighlight("800/1200/1600/2000","250","50",SSPI18nUtil.getHullModString("ssp_hpmessager"),SSPI18nUtil.getHullModString("ssp_hpmessager"),"90%");
        label.setHighlightColors(h,h,h,h,h,h);
        tooltip.addPara(SSPI18nUtil.getHullModString("ssp_CLIFF_basehullmod"), Color.GRAY, opad);
    }
}
