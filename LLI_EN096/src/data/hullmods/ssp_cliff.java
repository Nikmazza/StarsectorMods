package data.hullmods;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ssp_cliff extends BaseHullMod {
    Color COLOR =  new Color(255,140,140, 255);
    Color COLOR_HAS_HIGH_SCATTER_AMP =  new Color(160,213,225,255);
    protected boolean HAS_HIGH_SCATTER_AMP=false;//插件颜色
    //标准模式
    public static float Recoil_Bonus = 60f;
    public static float ProjectileSpeed_Bonus=30f;
    public static float Missile_Bonus=10f;
    //光束侠模式
    public static float BeamDamage_Percent=10f;
    public static float BeamRange_Flat=100f;
    public static float BeamForceHardFluxChance=80f;//只是给文本显示服务的变量，真概率在随机数里面
@Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
    if(ship.getVariant().hasHullMod("ssp_modetransform")){HAS_HIGH_SCATTER_AMP=true;}else{HAS_HIGH_SCATTER_AMP=false;}//插件颜色
    ship.addListener(new Cliff_EffectMod(ship, id));
}
    public static class Cliff_EffectMod implements AdvanceableListener, WeaponBaseRangeModifier, DamageDealtModifier {
        protected ShipAPI ship;
        protected String id;
        public Cliff_EffectMod(ShipAPI ship, String id) {
            this.ship = ship;
            this.id = id;
        }
        public void advance(float amount) {
            Object hullSize = ship.getHullSize();
            MutableShipStatsAPI stats = ship.getMutableStats();
            //设置效果阈值数
            float Mult = ship.getMutableStats().getDynamic().getMod("ssp_cliff_Mult").computeEffective(1f);
            float f = ship.getFluxLevel();//得到当前幅能百分比
            //根本不多余的计算公式
            float Recoil = 1f - (Recoil_Bonus*0.01f * f * Mult);
            float ProjectileSpeed =  1f + (ProjectileSpeed_Bonus*0.01f * f * Mult);
            float Missile = 1f + Missile_Bonus * 0.01f * Mult ;

            stats.getMissileAccelerationBonus().modifyMult(id, 1-0.01f*Missile_Bonus);
            stats.getMissileMaxTurnRateBonus().modifyMult(id, 1-0.01f*Missile_Bonus);
            stats.getMissileMaxSpeedBonus().modifyMult(id, 1-0.01f*Missile_Bonus);
            if (ship.getVariant().hasHullMod("ssp_modetransform"))
            {//给予光束侠模式效果
                stats.getBeamWeaponRangeBonus().modifyFlat(id,BeamRange_Flat * f * Mult );
            } else { //给予基础模式效果
                stats.getMaxRecoilMult().modifyMult(id, Recoil);
                stats.getRecoilPerShotMult().modifyMult(id, Recoil);
                stats.getRecoilDecayMult().modifyMult(id, Recoil);
                stats.getProjectileSpeedMult().modifyMult(id, ProjectileSpeed);
            }
       }
       //光束侠射程调整
        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0;
        }
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) { return 0; }
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            if (!ship.getVariant().hasHullMod("ssp_modetransform")) {return 1;}
            if (weapon.getSlot() == null || weapon.getSlot().getWeaponType() == WeaponAPI.WeaponType.BALLISTIC) { return 1;}//在实弹槽位上的武器不受到射程惩罚
            if (weapon.getSlot() == null || weapon.getSlot().getWeaponType() == WeaponAPI.WeaponType.BUILT_IN) { return 1;}//内置武器不受到射程惩罚
            if (weapon.isBeam()){return 1;}//光束武器不受到射程惩罚
            if (weapon.getType() == WeaponAPI.WeaponType.MISSILE){ return 1;}//在导弹武器不受到射程惩罚
            else return 0.05f;
        }
        public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            if (ship.getVariant().hasHullMod("ssp_modetransform")) {
                float Mult = ship.getMutableStats().getDynamic().getMod("ssp_cliff_Mult").computeEffective(1f);//协议:升级的加成
                float f = ship.getFluxLevel();//得到当前幅能百分比

                float Beam_Damage = BeamDamage_Percent * f * Mult;

                WeaponAPI weapon = null;
                if (param instanceof BeamAPI) {
                    weapon = ((BeamAPI) param).getWeapon();
                } else {
                    return null;
                }
                if (weapon == null || ship == null) return null;
                if (!shieldHit) return null;

                String id = "ssp_cliff";

                float Random = MathUtils.getRandomNumberInRange(0f, 1.25f);//基础概率，直接当成期望就行了
                if (Random/Mult <= f) {
                    damage.setForceHardFlux(true);
                } else if (Random/Mult > f) {
                    damage.setForceHardFlux(false);
                }
                damage.getModifier().modifyPercent(id, Beam_Damage);
                return id;
            }else return null;
        }
    }
    //新描述哦
    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) { return false; }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        float Mult = ship.getMutableStats().getDynamic().getMod("ssp_cliff_Mult").computeEffective(1f);
        Color h = Misc.getHighlightColor();
        Color b = Misc.getNegativeHighlightColor();
        LabelAPI label = tooltip.addPara(
                SSPI18nUtil.getHullModString("ssp_cliff_tooltip0"),
                opad,h,""+(int)Missile_Bonus+"%",Global.getSettings().getHullModSpec("ssp_modetransform").getDisplayName());
        label.setHighlight(""+(int)Missile_Bonus+"%",Global.getSettings().getHullModSpec("ssp_modetransform").getDisplayName());
        label.setHighlightColors(b,h);

        if(ship.getVariant().hasHullMod("ssp_modetransform")) {
            tooltip.addSectionHeading(SSPI18nUtil.getHullModString("ssp_beammode"), Alignment.MID, opad);
            label = tooltip.addPara(
                    SSPI18nUtil.getHullModString("ssp_cliff_beammode_tooltip"), opad, h,
                    ""+(int)BeamRange_Flat*Mult,"" +(int)BeamDamage_Percent*Mult+"%",""+(int)BeamForceHardFluxChance*Mult+"%");
            label.setHighlight(""+(int)BeamRange_Flat*Mult,"" +(int)BeamDamage_Percent*Mult+"%",""+(int)BeamForceHardFluxChance*Mult+"%");
            label.setHighlightColors(h,h,h);
        }else{
            tooltip.addSectionHeading(SSPI18nUtil.getHullModString("ssp_basemode"), Alignment.MID, opad);
            label = tooltip.addPara(
                    SSPI18nUtil.getHullModString("ssp_cliff_basemode_tooltip"), opad, h,
                    "" +(int)ProjectileSpeed_Bonus*Mult+"%","" +(int)Recoil_Bonus*Mult+"%");
            label.setHighlight("" +(int)ProjectileSpeed_Bonus*Mult+"%","" +(int)Recoil_Bonus*Mult+"%");
            label.setHighlightColors(h,h);
        }
    }
    public Color getNameColor() {
    if(HAS_HIGH_SCATTER_AMP)return COLOR_HAS_HIGH_SCATTER_AMP;
    else return COLOR;
}
    public Color getBorderColor() {
    if(HAS_HIGH_SCATTER_AMP)return COLOR_HAS_HIGH_SCATTER_AMP;
    else return COLOR;
}
    public int getDisplaySortOrder() { return 99; }
}