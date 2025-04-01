package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ssp_CLIFF_deliver extends ssp_CLIFF_basehullmod {
    public IntervalUtil interval = new IntervalUtil(0.5f,0.5f);//最小,最大
    private static Map mag = new HashMap();
    static {
        mag.put(ShipAPI.HullSize.FRIGATE, 800f);
        mag.put(ShipAPI.HullSize.DESTROYER, 1200f);
        mag.put(ShipAPI.HullSize.CRUISER, 1600f);
        mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 2000f);
    }
    private static Map meg = new HashMap();//英语笑话：a/e不分
    static {
        meg.put(ShipAPI.HullSize.FRIGATE, 40f);
        meg.put(ShipAPI.HullSize.DESTROYER, 80f);
        meg.put(ShipAPI.HullSize.CRUISER, 160f);
        meg.put(ShipAPI.HullSize.CAPITAL_SHIP, 240f);
    }
    public static class ssp_CLIFF_deliver_customdata{
        Map<ShipAPI, CombatEntityAPI> Spritemap = new HashMap();
        Map<ShipAPI, ShipAPI> Spritemap_Arrow = new HashMap();
        public ssp_CLIFF_deliver_customdata(){}
    }
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) { }
    public void advanceInCombat(ShipAPI ship, float amount) {
        ShipAPI target=null;
        interval.advance(amount);
        ssp_CLIFF_deliver_customdata CustomData = (ssp_CLIFF_deliver_customdata) Global.getCombatEngine().getCustomData().get("ssp_CLIFF_deliver");
        if(CustomData==null){
            CustomData = new ssp_CLIFF_deliver_customdata();
            Global.getCombatEngine().getCustomData().put("ssp_CLIFF_deliver",CustomData);
        }
        //每0.5s进行目标更新
        if(interval.intervalElapsed()){
            float minScore = Float.MAX_VALUE;
            ShipAPI best = null;
            for(ShipAPI t:CombatUtils.getShipsWithinRange(ship.getLocation(), (Float) mag.get(ship.getHullSize()))){
                if(t==ship){continue;}
                if(!t.isAlive()){continue;}
                if(t.getFluxTracker().isOverloadedOrVenting()){continue;}
                if(t.getOwner() != ship.getOwner()){continue;}
                if(t.isFighter()){continue;}
                float hppercent=t.getHitpoints()/t.getMaxHitpoints();
                if(hppercent<minScore && hppercent<ship.getHitpoints()/ship.getMaxHitpoints()){
                    minScore = hppercent;
                    best = t;
                }
            }
            target = best;
            CustomData.Spritemap_Arrow.put(ship,target);
            //0.5s一次检测，生成回血实体
            if(CustomData.Spritemap_Arrow.get(ship)!=null){
                if(CustomData.Spritemap_Arrow.get(ship).getHitpoints()/CustomData.Spritemap_Arrow.get(ship).getMaxHitpoints()<ship.getHitpoints()/ship.getMaxHitpoints()){
                    if((CustomData.Spritemap_Arrow.get(ship).getHitpoints()+(Float)meg.get(ship.getHullSize())*3)/CustomData.Spritemap_Arrow.get(ship).getMaxHitpoints()<=ship.getHitpoints()/ship.getMaxHitpoints()){
                        if(!ship.getFluxTracker().isOverloadedOrVenting()){
                            Vector2f loc = MathUtils.getRandomPointOnCircumference(ship.getLocation(),ship.getCollisionRadius() * 0.75f);
                            float facing = VectorUtils.getAngle(ship.getLocation(), loc);
                            createLifeDeliverMissile(loc, facing, 10f, ship , CustomData.Spritemap_Arrow.get(ship) , (Float)meg.get(ship.getHullSize()));
                            ship.setHitpoints(ship.getHitpoints()-(Float)meg.get(ship.getHullSize())*(2-ship.getMutableStats().getDynamic().getMod("ssp_CLIFF_morehp").computeEffective(1f)));
                        }
                    }
                }
            }
        }
        //递送范围光环
        if(!CustomData.Spritemap.containsKey(ship)){
            CombatEntityAPI e = Global.getCombatEngine().addLayeredRenderingPlugin(new ssp_CLIFF_deliver_render(ship,target));
            CustomData.Spritemap.put(ship,e);
        }
    }
    //生成回血实体
    public static void createLifeDeliverMissile(Vector2f loc, float facing, float speed, ShipAPI from , ShipAPI to, float toHeal){
        //生成导弹，拖尾在magicTrail里面自己改吧
        //机动性在proj文件里面
        MissileAPI shot = (MissileAPI) Global.getCombatEngine().spawnProjectile(
                from,null,"ssp_lifesteal",loc,facing,Misc.ZERO);
        //导弹ai特性，要翻转一下导弹的所有者似乎才能正常制导
        if(shot.getOwner()==0){shot.setOwner(1);}else if(shot.getOwner()==1){shot.setOwner(0);}
        GuidedMissileAI ai = (GuidedMissileAI) shot.getMissileAI();
        shot.getVelocity().scale(speed);
        ai.setTarget(to);
        //继续生成回血实体特效
        Global.getCombatEngine().addLayeredRenderingPlugin(new ssp_CreateEntity.ProcessEntityMoving(shot,toHeal, to));
    }
    public static class ssp_CLIFF_deliver_render extends BaseCombatLayeredRenderingPlugin {
        protected ShipAPI ship;
        protected ShipAPI target;
        public ssp_CLIFF_deliver_render(ShipAPI ship,ShipAPI target) {
            this.ship = ship;
            this.target = target;
        }
        @Override
        public float getRenderRadius() {return 5000; }
        @Override
        public boolean isExpired() {
            return ship.isHulk()||!Global.getCombatEngine().isEntityInPlay(ship);
        }
        @Override
        public void render(CombatEngineLayers layer, ViewportAPI viewport) {
            //画圈
            float size=(Float) mag.get(ship.getHullSize());
            SpriteAPI sprite = Global.getSettings().getSprite("fx", "ssp_verybigO");
            sprite.setColor(new Color(15, 40, 110,255));
            sprite.setWidth(size*2f);
            sprite.setHeight(size*2f);
            sprite.setAdditiveBlend();
            if(ship==Global.getCombatEngine().getPlayerShip() || Misc.getDistance(ship.getLocation(),Global.getCombatEngine().getPlayerShip().getMouseTarget())<ship.getCollisionRadius()*1.5f){
                sprite.setAlphaMult(0.6f);
            }else {
                sprite.setAlphaMult(0.1f);
            }
            sprite.renderAtCenter(ship.getLocation().x, ship.getLocation().y);
            //连线
            ssp_CLIFF_deliver_customdata CustomData = (ssp_CLIFF_deliver_customdata) Global.getCombatEngine().getCustomData().get("ssp_CLIFF_deliver");
            if(CustomData==null)return;
            target=CustomData.Spritemap_Arrow.get(ship);
            //Global.getLogger(this.getClass()).info(target);
            if(target!=null){
                SpriteAPI arrow=Global.getSettings().getSprite("fx", "ssp_CLIFF_deliverRender");
                arrow.setColor(new Color(15, 40, 110,255));
                arrow.setAngle(VectorUtils.getAngle(ship.getLocation(),target.getLocation()));
                arrow.setWidth(Misc.getDistance(ship.getLocation(),target.getLocation()));
                arrow.setHeight((Float)meg.get(ship.getHullSize())*0.3f);
                arrow.setAdditiveBlend();
                arrow.setAlphaMult(1f);
                arrow.renderAtCenter(MathUtils.getMidpoint(ship.getLocation(),target.getLocation()).x, MathUtils.getMidpoint(ship.getLocation(),target.getLocation()).y);
            }
        }
    }
    //描述
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if(ship==null)return;
        float opad = 10f;
        float Mult = 2-ship.getMutableStats().getDynamic().getMod("ssp_CLIFF_morehp").computeEffective(1f);
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_CLIFF_deliver_tooltip0"),
                opad, h,
                "800/1200/1600/2000","2",
                ""+(int)((Float)meg.get(ShipAPI.HullSize.FRIGATE)*Mult)+"/"+(int)((Float)meg.get(ShipAPI.HullSize.DESTROYER)*Mult)+"/"+(int)((Float)meg.get(ShipAPI.HullSize.CRUISER)*Mult)+"/"+(int)((Float)meg.get(ShipAPI.HullSize.CAPITAL_SHIP)*Mult),
                "40/80/160/240",SSPI18nUtil.getHullModString("ssp_hpmessager"),SSPI18nUtil.getHullModString("ssp_hpmessager"),"90%");
        label.setHighlight(
                "800/1200/1600/2000","2",
                ""+(int)((Float)meg.get(ShipAPI.HullSize.FRIGATE)*Mult)+"/"+(int)((Float)meg.get(ShipAPI.HullSize.DESTROYER)*Mult)+"/"+(int)((Float)meg.get(ShipAPI.HullSize.CRUISER)*Mult)+"/"+(int)((Float)meg.get(ShipAPI.HullSize.CAPITAL_SHIP)*Mult),
                "40/80/160/240",SSPI18nUtil.getHullModString("ssp_hpmessager"),SSPI18nUtil.getHullModString("ssp_hpmessager"),"90%");
        label.setHighlightColors(h,h,h,h,h,h,h,h);
        tooltip.addPara(SSPI18nUtil.getHullModString("ssp_CLIFF_basehullmod"), Color.GRAY, opad);
    }
}
