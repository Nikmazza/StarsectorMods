package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.VectorUtils;
import org.magiclib.util.MagicIncompatibleHullmods;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ssp_ShortRange extends BaseHullMod {
    private static Map mag = new HashMap();
    static {
        mag.put(ShipAPI.HullSize.FRIGATE, 1f);
        mag.put(ShipAPI.HullSize.DESTROYER, 2f);
        mag.put(ShipAPI.HullSize.CRUISER, 3f);
        mag.put(ShipAPI.HullSize.CAPITAL_SHIP, 4f);
    }
    public static final float MaxSpeedandADTM_Mult = 30f;
    public static final float DamageBonus = 0.25f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if(stats.getVariant()!=null && this.spec!=null && Misc.isSpecialMod(stats.getVariant(),this.spec)){
            stats.getVariant().removePermaMod(this.spec.getId());
        }
        stats.getMaxSpeed().modifyPercent(id, MaxSpeedandADTM_Mult);
        stats.getAcceleration().modifyPercent(id, MaxSpeedandADTM_Mult);
        stats.getDeceleration().modifyPercent(id, MaxSpeedandADTM_Mult);
        stats.getTurnAcceleration().modifyPercent(id, MaxSpeedandADTM_Mult);
        stats.getMaxTurnRate().modifyPercent(id, MaxSpeedandADTM_Mult);
        stats.getZeroFluxSpeedBoost().modifyFlat(id,stats.getMaxSpeed().getBaseValue()*-MaxSpeedandADTM_Mult*0.01f);
        stats.getEnergyWeaponRangeBonus().modifyFlat(id,-75f * (Float) mag.get(hullSize));
        stats.getBallisticWeaponRangeBonus().modifyFlat(id,-75f * (Float) mag.get(hullSize));
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new ssp_ShortRange_listener(ship,id));
        if (ship.getVariant().getHullMods().contains("unstable_injector")) {
            //if someone tries to install sussy hullmodsus, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(),
                    "unstable_injector",
                    "ssp_ShortRange"//插件id
            );
        }
    }
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("ssp_cliff")
                &&!ship.getVariant().hasHullMod("unstable_injector")
                &&!ship.getVariant().hasHullMod("ssp_LowerThreshold")
                &&!ship.getVariant().hasHullMod("ssp_LongerRange");
    }
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("ssp_cliff")) {
            return SSPI18nUtil.getHullModString("LLI_ONLY");
        }
        return super.getUnapplicableReason(ship);
    }
    //新描述哦
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_ShortRange_tooltip0"),
                opad, h, "30%",Global.getSettings().getHullModSpec("unstable_injector").getDisplayName());
        label.setHighlight("30%",Global.getSettings().getHullModSpec("unstable_injector").getDisplayName());
        label.setHighlightColors(h,h);
        tooltip.addSectionHeading(SSPI18nUtil.getHullModString("ssp_ShortRange_SectionHeading1"), Alignment.MID, opad);
        label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ShortRange_tooltip1"), opad, h,
                "75/150/225/300","25%","25%");
        label.setHighlight("75/150/225/300","25%","25%");
        label.setHighlightColors(b,h,h);
        //战术系统描述
        if(ship!=null && ship.getSystem() != null) {
            switch (ship.getSystem().getId()) {
                case "ssp_ammofeedjet":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ammofeedjet_S")
                            , opad, h,
                            "100%");
                    label.setHighlight("100%");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_lanina_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_lanina_system_S")
                            , opad, h,
                            "1");
                    label.setHighlight("1");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_ManeuveringJets":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ManeuveringJets_S")
                            , opad, h,
                            "");
                    label.setHighlight("");
                    label.setHighlightColors();
                    break;
                case "ssp_ThunderCharge":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_ThunderCharge_S")
                            , opad, h,
                            "20%");
                    label.setHighlight("20%");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_microburn":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_microburn_S")
                            , opad, h,
                            "1", "20%");
                    label.setHighlight("1", "20%");
                    label.setHighlightColors(Color.green, Color.green);
                    break;
                case "ssp_PhaseTransferField":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_PhaseTransferField_S")
                            , opad, h,
                            "20%");
                    label.setHighlight("20%");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_AdvanceVenting":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_AdvanceVenting_S")
                            , opad, h,
                            "200");
                    label.setHighlight("200");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_HitMeYouWeekMissile":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_HitMeYouWeekMissile_S")
                            , opad, h,
                            "500");
                    label.setHighlight("500");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_missleswarm":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_missleswarm_S")
                            , opad, h,
                            "");
                    label.setHighlight("");
                    label.setHighlightColors(h);
                    break;
                case "ssp_AmmoFeed":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_AmmoFeed_S")
                            , opad, h,
                            "4s");
                    label.setHighlight("4s");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_HighEnergyFocus":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() +SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_HighEnergyFocus_S")
                            , opad, h,
                            "1s");
                    label.setHighlight("1s");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_zhurong_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_zhurong_system_S")
                            , opad, h,
                            "6");
                    label.setHighlight("6");
                    label.setHighlightColors(Color.green);
                    break;
                case "ssp_TDS_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_TDS_system_S")
                            , opad, h,
                            "");
                    label.setHighlight("");
                    label.setHighlightColors();
                    break;
                case "ssp_gonggong_system":
                    tooltip.addSectionHeading(ship.getSystem().getDisplayName() + SSPI18nUtil.getHullModString("ssp_Supreme"), Alignment.MID, opad);
                    label = tooltip.addPara(SSPI18nUtil.getHullModString("ssp_gonggong_system_S")
                            , opad, h,
                            "6");
                    label.setHighlight("6");
                    label.setHighlightColors(Color.green);
                    break;
            }
        }
    }
    public static class ssp_ShortRange_listener implements DamageDealtModifier{
        protected ShipAPI ship;
        protected String id;
        public ssp_ShortRange_listener(ShipAPI ship, String id){
            this.ship = ship;
            this.id = id;
        }
        public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit){
            if(target instanceof ShipAPI && !shieldHit) {
                float Target_Facing = target.getFacing();
                float HitPoint_Facing= VectorUtils.getAngleStrict(target.getLocation(),point);
                float HullHitAngle= Math.abs(HitPoint_Facing-Target_Facing);
                if(HullHitAngle>180){HullHitAngle=360-HullHitAngle;}
                String id = "ssp_ShortRange_listener_effectmod";
                damage.getModifier().modifyMult(id, 1+DamageBonus*(HullHitAngle/180));
                //Global.getCombatEngine().addFloatingDamageText(point, HullHitAngle, Color.CYAN, target,ship);
                //Global.getCombatEngine().addFloatingDamageText(ship.getLocation(), HitPoint_Facing, Color.magenta, target,ship);
                //Global.getCombatEngine().addFloatingDamageText(target.getLocation(), Target_Facing, Color.green, target,ship);
                return id;
            }else if(target instanceof ShipAPI && shieldHit) {
                float Shield_Facing = target.getShield().getFacing();
                float ShieldHit_Facing= VectorUtils.getAngleStrict(target.getLocation(),point);
                float ShieldHitAngle= Math.abs(ShieldHit_Facing-Shield_Facing);
                if(ShieldHitAngle>180){ShieldHitAngle=360-ShieldHitAngle;}//一个0~180的值，数值越大击中位置与护盾朝向夹角越大
                String id = "ssp_ShortRange_listener_effectmod";
                damage.getModifier().modifyMult(id, 1+DamageBonus*ShieldHitAngle/(target.getShield().getArc()*0.5f));
                //Global.getCombatEngine().addFloatingDamageText(point, ShieldHitAngle/(target.getShield().getArc()*0.5f)*100f, Color.blue, target,ship);
                //Global.getCombatEngine().addFloatingDamageText(ship.getLocation(), ShieldHit_Facing, Color.pink, target,ship);
                //Global.getCombatEngine().addFloatingDamageText(target.getLocation(), target.getShield().getArc(), Color.lightGray, target,ship);
                return id;
            }else return null;
        }
    }
}
