package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.magiclib.util.MagicIncompatibleHullmods;

import java.awt.*;


public class ssp_shieldmod extends BaseHullMod {
    public static final float SHIELD_BONUS_TURN = 10f;
    public static final float SHIELD_BONUS_UNFOLD = 25f;

    public static class ssp_shieldmodEffect implements AdvanceableListener {
        protected ShipAPI ship;
        protected String id;
        protected float activetime =4f;//最多持续时间
        protected float activetime1 =0f;//持续时间计时器
        protected float colddown =20f;//冷却时间
        protected float sinceactive =colddown+1f;//冷却时间计时器
        boolean allowactive=false;
        boolean allowactive1=false;

        public ssp_shieldmodEffect(ShipAPI ship, String id) {
            this.ship = ship;
            this.id = id;
        }
        public void advance(float amount) {
            MutableShipStatsAPI stats = ship.getMutableStats();
            if(ship.getShield() == null){return;}
            //计时器
            sinceactive += amount;//冷却时间计时器
            activetime1 += amount;//持续时间计时器
            float arc1 = ship.getShield().getArc();//盾最大角度
            float arc2 = ship.getShield().getActiveArc();//盾当前角度
            //护盾完全展开允许激活，开关1
            //条件：开启护盾；护盾当前角度等于最大角度；距离上一次触发已经大于了冷却时间；开关2并非为真；
            if (ship.getShield().isOn() && arc1 == arc2 && sinceactive>colddown && !allowactive1 ){  //&& activetime1 < activetime
                allowactive=true;//设置允许开启，打开开关1
                activetime1=0f;//持续时间计时器归零
            }
            //关闭护盾行为激活效果.开关2
            if (ship.getShield().isOff() && sinceactive>colddown && allowactive) {
                allowactive1 = true;
            }
            //开关1和2都开启，给予效果
            if (activetime1 < activetime && allowactive1) {
                stats.getMaxSpeed().modifyFlat(id, 50f);
                stats.getAcceleration().modifyPercent(id, 150f);
                stats.getDeceleration().modifyPercent(id, 150f);
                stats.getTurnAcceleration().modifyPercent(id, 150f);
                stats.getMaxTurnRate().modifyPercent(id, 150f);

            } else if (activetime1 > activetime && allowactive1) {//超出持续时间
                stats.getMaxSpeed().unmodify(id);
                stats.getMaxTurnRate().unmodify(id);
                stats.getTurnAcceleration().unmodify(id);
                stats.getAcceleration().unmodify(id);
                stats.getDeceleration().unmodify(id);
                sinceactive = 0f;//重置冷却时间计时器
                allowactive = false;//关闭开关1
                allowactive1 = false;//关闭开关2
            }
            float shield_Arc=(arc2/arc1);
            //开关盾狂魔
            if(ship.getShield().isOn() ){
               stats.getShieldDamageTakenMult().modifyMult(id,(0.90f-((1-shield_Arc)*0.20f)));
            }else{
                stats.getShieldDamageTakenMult().unmodify(id);
            }
            //可视化
            float activeing=Math.round((activetime-activetime1));
            float activeing_string=activetime-activetime1;
            float coldingdown=Math.round((colddown-sinceactive));
            float coldingdown_string=colddown-sinceactive;
            if (Global.getCombatEngine().getPlayerShip() == ship) {

                if(allowactive && !allowactive1) {
                    Global.getCombatEngine().maintainStatusForPlayerShip("act", "graphics/icons/hullsys/emp_emitter.png", SSPI18nUtil.getHullModString("ssp_shieldmod_title"),
                            String.format(SSPI18nUtil.getHullModString("ssp_shieldmod_allowactive"), "."), false);
                }
                else if (activeing_string>0 && activetime1 < activetime && allowactive1){
                    Global.getCombatEngine().maintainStatusForPlayerShip("act", "graphics/icons/hullsys/emp_emitter.png", SSPI18nUtil.getHullModString("ssp_shieldmod_title"),
                            String.format(SSPI18nUtil.getHullModString("ssp_shieldmod_activeing"), activeing+ "s"), false);
                }
                else if (coldingdown_string>0 && coldingdown_string<colddown && !allowactive1){
                    Global.getCombatEngine().maintainStatusForPlayerShip("act", "graphics/icons/hullsys/emp_emitter.png", SSPI18nUtil.getHullModString("ssp_shieldmod_title"),
                            String.format(SSPI18nUtil.getHullModString("ssp_shieldmod_coldingdown"), coldingdown + "s"), true);
                }
            }

        }
        }
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldTurnRateMult().modifyMult(id, 1+(SHIELD_BONUS_TURN*0.01f));
        stats.getShieldUnfoldRateMult().modifyMult(id, 1-(SHIELD_BONUS_UNFOLD*0.01f));
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getShield().getType() != ShieldAPI.ShieldType.NONE){ship.addListener(new ssp_shieldmodEffect(ship, id));}
        if (ship.getVariant().getHullMods().contains("hardenedshieldemitter")) {
            //if someone tries to install sussy hullmodsus, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "hardenedshieldemitter",
                    "ssp_shieldmod"
            );
        }else if (ship.getVariant().getHullMods().contains("shield_shunt")) {
            //if someone tries to install sussy hullmodsus, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "shield_shunt",
                    "ssp_shieldmod"
            );
        }
        }

    //新描述哦
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_shieldmod_tooltip0"),
                opad, h, "" + (int)SHIELD_BONUS_UNFOLD+"%","" + (int)SHIELD_BONUS_TURN+"%",Global.getSettings().getHullModSpec("hardenedshieldemitter").getDisplayName(),Global.getSettings().getHullModSpec("shield_shunt").getDisplayName());
        label.setHighlight("" + (int)SHIELD_BONUS_UNFOLD+"%","" + (int)SHIELD_BONUS_TURN+"%",Global.getSettings().getHullModSpec("hardenedshieldemitter").getDisplayName(),Global.getSettings().getHullModSpec("shield_shunt").getDisplayName());
        label.setHighlightColors(b,h,h,h);
        tooltip.addSectionHeading(SSPI18nUtil.getHullModString("ssp_shieldmod_SectionHeading1"), Alignment.MID, opad);
        label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_shieldmod_tooltip1"), opad, h,
                "30"+"%","10"+"%");
        label.setHighlight("30"+"%","10"+"%");
        label.setHighlightColors(h,h,h);
        tooltip.addSectionHeading(SSPI18nUtil.getHullModString("ssp_shieldmod_SectionHeading2"), Alignment.MID, opad);
        label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_shieldmod_tooltip2"), opad, h,
                "4","20");
        label.setHighlight("4","20");
        label.setHighlightColors(h,h);

    }


}
