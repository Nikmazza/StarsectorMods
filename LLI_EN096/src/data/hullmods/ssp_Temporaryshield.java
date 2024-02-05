package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.magiclib.util.MagicIncompatibleHullmods;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;

public class ssp_Temporaryshield extends BaseHullMod {
    //public static final Color JITTER_COLOR = new Color(85,85,185,50);
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldUnfoldRateMult().modifyMult(id,100f);
        stats.getShieldArcBonus().modifyFlat(id,360f);
        //stats.getShieldDamageTakenMult().modifyMult(id,0.5f);
        //stats.getArmorBonus().modifyMult(id,1.15f);
    }

//    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
//        ship.addListener(new ssp_TemporaryshieldEffect(ship, id));
//        if (ship.getVariant().getHullMods().contains("shield_shunt")) {
//            //if someone tries to install sussy hullmodsus, remove it
//            MagicIncompatibleHullmods.removeHullmodWithWarning(
//                    ship.getVariant(),
//                    "shield_shunt",
//                    "ssp_Temporaryshield"
//            );
//        }else if (ship.getVariant().getHullMods().contains("advancedshieldemitter")) {
//            //if someone tries to install sussy hullmodsus, remove it
//            MagicIncompatibleHullmods.removeHullmodWithWarning(
//                    ship.getVariant(),
//                    "advancedshieldemitter",
//                    "ssp_Temporaryshield"
//            );
//        }else if (ship.getVariant().getHullMods().contains("frontemitter")) {
//            //if someone tries to install sussy hullmodsus, remove it
//            MagicIncompatibleHullmods.removeHullmodWithWarning(
//                    ship.getVariant(),
//                    "frontemitter",
//                    "ssp_Temporaryshield"
//            );
//        }else if (ship.getVariant().getHullMods().contains("adaptiveshields")) {
//            //if someone tries to install sussy hullmodsus, remove it
//            MagicIncompatibleHullmods.removeHullmodWithWarning(
//                    ship.getVariant(),
//                    "adaptiveshields",
//                    "ssp_Temporaryshield"
//            );
//        }else if (ship.getVariant().getHullMods().contains("frontshield")) {
//            //if someone tries to install sussy hullmodsus, remove it
//            MagicIncompatibleHullmods.removeHullmodWithWarning(
//                    ship.getVariant(),
//                    "frontshield",
//                    "ssp_Temporaryshield"
//            );
//        }
//    }
//    @Override
//    public boolean isApplicableToShip(ShipAPI ship) {
//        return ship.getVariant().hasHullMod("ssp_cliff")
//                && !ship.getVariant().hasHullMod("shield_shunt")
//                && !ship.getVariant().hasHullMod("advancedshieldemitter")
//                && !ship.getVariant().hasHullMod("frontemitter")
//                && !ship.getVariant().hasHullMod("adaptiveshields")
//                && !ship.getVariant().hasHullMod("frontshield");
//    }
//    public String getUnapplicableReason(ShipAPI ship) {
//        if (!ship.getVariant().hasHullMod("ssp_cliff")) {
//            return "只能安装在楼兰工业的舰船上";
//        }
//        return null;
//    }
//    //新描述哦
//    @Override
//    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
//    @Override
//    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
//        float pad = 3f;
//        float opad = 10f;
//        Color h = Misc.getHighlightColor();
//        LabelAPI label = tooltip.addPara("将护盾改为临时护盾，极大的增加护盾展开速度，并且将护盾角度提升到最大。\n" +
//                "增加{%s}装甲，护盾受到伤害减少{%s}，与大部分护盾插件不兼容。", opad, h,"15"+"%","50"+"%");
//        label.setHighlight("15"+"%","50"+"%");
//        label.setHighlightColors(h,h);
//        tooltip.addSectionHeading("临时护盾", Alignment.MID, opad);
//        label = tooltip.addPara("护盾只能临时性展开{%s}，冷却时间{%s}。当护盾开启时：\n" +
//                        "·无法关闭护盾和主动耗散\n" +
//                        "·略微增加自身时间流速，效果逐渐降低"
//                        , opad, h,
//                "3s","6s");
//        label.setHighlight("3s","6s");
//        label.setHighlightColors(h,h);
//    }
//
//    public static class ssp_TemporaryshieldEffect implements AdvanceableListener {
//        protected ShipAPI ship;
//        protected String id;
//        protected float cooldingdown=6f;//冷却时间
//        protected float ToggleOnTime=3f;//激活时间
//        protected float sinceToggleOn=3f;
//        protected float sincecolddown =0f+cooldingdown;
//        boolean Toggle=false;
//        public ssp_TemporaryshieldEffect(ShipAPI ship, String id) {
//            this.ship = ship;
//            this.id = id;
//        }
//        public void advance(float amount) {
//            MutableShipStatsAPI stats = ship.getMutableStats();
//            //CombatEngineAPI Engine= Global.getCombatEngine();
//            sincecolddown += amount;
//            sinceToggleOn += amount;
//            if(ship.getShield() == null){return;}
//            float arc1 = ship.getShield().getArc();//盾最大角度
//            float arc2 = ship.getShield().getActiveArc();//盾当前角度
//
//            if (ship.getShield().isOn() && !Toggle && arc1 == arc2) {
//                Toggle = true;
//                sinceToggleOn = 0f;
//            }
//            float Level=ToggleOnTime-sinceToggleOn;
//            Level = MathUtils.clamp(Level, 0f, ToggleOnTime);
//            //子弹时间
//            if (Global.getCombatEngine().getPlayerShip() == ship) {
//                Global.getCombatEngine().getTimeMult().modifyMult(id, 1/(1+Level));
//                } else {
//                    Global.getCombatEngine().getTimeMult().unmodify(id);
//            }
//            if (Toggle && sinceToggleOn < ToggleOnTime) {
//                stats.getTimeMult().modifyMult(id,1f+Level);
//                ship.getShield().toggleOn();
//                ship.blockCommandForOneFrame(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK);
//                ship.blockCommandForOneFrame(ShipCommand.VENT_FLUX);
//                ship.setJitterUnder(this, JITTER_COLOR, Level, 4, 0.5f, 2f);
//            } else if (Toggle && sinceToggleOn > ToggleOnTime) {
//                stats.getTimeMult().unmodify(id);
//                ship.getShield().toggleOff();
//                Toggle = false;
//                sincecolddown = 0f;
//            }
//            if (sincecolddown < cooldingdown) {
//                ship.blockCommandForOneFrame(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK);
//            }
//
//
//            //可视化
//            if (Global.getCombatEngine().getPlayerShip() == ship) {
//                if(sincecolddown > cooldingdown && !Toggle) {
//                    Global.getCombatEngine().maintainStatusForPlayerShip("Tem", "graphics/icons/hullsys/fortress_shield.png", "临时护盾",
//                            String.format(SSPI18nUtil.getHullModString("ssp_Temporaryshield_allowon"), ""), false);
//                }else if(sincecolddown < cooldingdown ) {
//                    Global.getCombatEngine().maintainStatusForPlayerShip("Tem", "graphics/icons/hullsys/fortress_shield.png", "临时护盾",
//                            String.format(SSPI18nUtil.getHullModString("ssp_Temporaryshield_off"), (int)cooldingdown-(int)sincecolddown), true);
//                }
//            }
//        }
//    }
}
