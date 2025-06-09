package data.hullmods.bi;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.AIHints;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.combat.listeners.WeaponOPCostModifier;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.everyframe.yna_BlockedHullmodDisplayScript;
import data.scripts.util.YNA_MD;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class yna_hullspec extends BaseHullMod {
	public static final float HE_RES = 35f;
	public static final float BEAM_RES = 20f;
	public static final float BEAM_EFF = 10f;
//	public static final float BEAM_RANGE = 100f;
	public static final float THRESHOLD_BONUS = 20f;
	public static final float ARMOR_BIG = 2f;
	public static final float ARMOR_BIG_NO = 0.5f;
        
	public static final float BIG_SEE = 20f;
        
	public static final float EXPLOD = 20f;
        
	public static final float ARM_FIG = 100f; 
        public static final float ENG_HEALTH_FIG = 50f;
        public static final float WEP_HEALTH_FIG = 30f;
	public static final float REP_RATE_FIG = 33f;
        
//        public static final float BOON1_RANGE = 300f;
//        public static final float BOON2_RANGE = 200f;
        public static final float BOON3_RANGE = 100f;
        public static final float BOOP_RANGE = 100f;
	private static Map BOON1_RANGE = new HashMap();
	private static Map BOON2_RANGE = new HashMap();
	private static Map BIG_SAW = new HashMap();
	private static Map DEP_BAD = new HashMap();
	static {
		BOON1_RANGE.put(HullSize.FRIGATE, 100f);
		BOON1_RANGE.put(HullSize.DESTROYER, 150f);
		BOON1_RANGE.put(HullSize.CRUISER, 200f);
		BOON1_RANGE.put(HullSize.CAPITAL_SHIP, 200f);
                
		BOON2_RANGE.put(HullSize.FRIGATE, 100f);
		BOON2_RANGE.put(HullSize.DESTROYER, 100f);
		BOON2_RANGE.put(HullSize.CRUISER, 150f);
		BOON2_RANGE.put(HullSize.CAPITAL_SHIP, 200f);
                
		BIG_SAW.put(HullSize.FRIGATE, 10f);
		BIG_SAW.put(HullSize.DESTROYER, 20f);
		BIG_SAW.put(HullSize.CRUISER, 30f);
		BIG_SAW.put(HullSize.CAPITAL_SHIP, 50f);
                
		DEP_BAD.put(HullSize.FRIGATE, 2f);
		DEP_BAD.put(HullSize.DESTROYER, 5f);
		DEP_BAD.put(HullSize.CRUISER, 8f);
		DEP_BAD.put(HullSize.CAPITAL_SHIP, 15f);
	}
//        public static final float BOO_FIRE = 10f;
        
	public static final float FRAG_BAD = 40f;
	public static final float REC_BAD = 30f;
        
	private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
	static
	{
		// These hullmods will automatically be removed
		// This prevents unexplained hullmod blocking
		BLOCKED_HULLMODS.add("ballistic_rangefinder");
	}
        
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
            if (hullSize == HullSize.FIGHTER) {
                stats.getEffectiveArmorBonus().modifyFlat(id, ARM_FIG);
                stats.getEngineHealthBonus().modifyMult(id, 1f + (ENG_HEALTH_FIG * 0.01f));
                stats.getWeaponHealthBonus().modifyMult(id, 1f + (WEP_HEALTH_FIG * 0.01f));
		stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f - REP_RATE_FIG * 0.01f);
            }
            
		stats.getWeaponRangeThreshold().modifyMult(id, 1 + (THRESHOLD_BONUS * 0.01f));
        
		stats.getHighExplosiveDamageTakenMult().modifyMult(id, 1 - (HE_RES * 0.01f));
		stats.getFragmentationDamageTakenMult().modifyMult(id, 1 + (FRAG_BAD * 0.01f));
		stats.getBeamDamageTakenMult().modifyMult(id, 1 - (BEAM_RES * 0.01f));
		stats.getBeamWeaponFluxCostMult().modifyMult(id, 1 - (BEAM_EFF * 0.01f));
                
            if (hullSize != HullSize.FIGHTER) {
		stats.getSightRadiusMod().modifyMult(id, 1 + (BIG_SEE * 0.01f));
		stats.getSensorStrength().modifyFlat(id, (Float) BIG_SAW.get(hullSize));
                
		stats.getDynamic().getStat(Stats.EXPLOSION_DAMAGE_MULT).modifyMult(id, 1 - (EXPLOD * 0.01f));
		stats.getDynamic().getStat(Stats.EXPLOSION_RADIUS_MULT).modifyMult(id, 1 - (EXPLOD * 0.01f));
                
                if (!(stats.getVariant().hasHullMod(HullMods.CIVGRADE) && !stats.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS))) {
                    if (stats.getVariant().getHullSpec().getSuppliesPerMonth() > 0) {
			stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, (Float) DEP_BAD.get(hullSize));
                    }
                }
            }
                
		stats.addListener(new WeaponOPCostModifier() {
                        @Override
			public int getWeaponOPCost(MutableShipStatsAPI stats, WeaponSpecAPI weapon, int currCost) {
				if (weapon.getWeaponId().startsWith("yna_") && weapon.hasTag("ynadar") && 
                                        (weapon.getType() != WeaponAPI.WeaponType.MISSILE)) {
                                        if (weapon.getSize().equals(WeaponAPI.WeaponSize.SMALL)) return (currCost - (int) 3); 
                                        if (weapon.getSize().equals(WeaponAPI.WeaponSize.MEDIUM)) return (currCost - (int) 5);
                                        if (weapon.getSize().equals(WeaponAPI.WeaponSize.LARGE)) return (currCost - (int) 8);
				}
				return currCost;
			}
		});
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            
		for (String tmp : BLOCKED_HULLMODS) {
			if (ship.getVariant().getHullMods().contains(tmp)) {
				ship.getVariant().removeMod(tmp);
				yna_BlockedHullmodDisplayScript.showBlocked(ship);
			}
		}
            
		ship.addListener(new WeaponBaseRangeModifier() {
                        @Override
			public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
				return 0;
			}
                        @Override
			public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
				return 1f;
			}
                        @Override
			public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
				if (weapon.getSize() == WeaponAPI.WeaponSize.SMALL && 
                                        weapon.getType() != WeaponAPI.WeaponType.MISSILE && 
                                        (!weapon.getId().startsWith("yna_")) &&
                                        !weapon.isBeam() && 
                                        !weapon.getSpec().getAIHints().contains(AIHints.PD)) return (float) BOON1_RANGE.get(ship.getHullSize());
				if (weapon.getSize() == WeaponAPI.WeaponSize.MEDIUM && 
                                        weapon.getType() != WeaponAPI.WeaponType.MISSILE &&  
                                        (!weapon.getId().startsWith("yna_")) &&
                                        !weapon.isBeam() && 
                                        !weapon.getSpec().getAIHints().contains(AIHints.PD)) return (float) BOON2_RANGE.get(ship.getHullSize());
				if (weapon.getSize() == WeaponAPI.WeaponSize.LARGE && 
                                        weapon.getType() != WeaponAPI.WeaponType.MISSILE && 
                                        (!weapon.getId().startsWith("yna_")) &&
                                        !weapon.isBeam() && 
                                        !weapon.getSpec().getAIHints().contains(AIHints.PD)) return BOON3_RANGE;
				if ((weapon.getSize() == WeaponAPI.WeaponSize.SMALL || 
                                        weapon.getSize() == WeaponAPI.WeaponSize.MEDIUM || 
                                        weapon.getSize() == WeaponAPI.WeaponSize.LARGE) && 
                                        (!weapon.getId().startsWith("yna_")) &&
                                        (!weapon.isBeam() && 
                                        weapon.getSpec().getAIHints().contains(AIHints.PD) ||
                                        weapon.isBeam())) return BOOP_RANGE;
				/*
                                if ((weapon.getSize() == WeaponAPI.WeaponSize.SMALL || 
                                        weapon.getSize() == WeaponAPI.WeaponSize.MEDIUM || 
                                        weapon.getSize() == WeaponAPI.WeaponSize.LARGE) && 
                                        (!weapon.getId().startsWith("yna_")) &&
                                        weapon.isBeam()) return BEAM_RANGE;
                                */
				return 0f;
			}
		});
        }
        
	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}

        protected static final float LOAD_OF_BULL = 3f;
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float opad = 10f;
		float pad = 3f;
		Color h = Misc.getHighlightColor();
                Color bad = Misc.getNegativeHighlightColor();
                Color good = Misc.getPositiveHighlightColor();
                Color gray = Misc.getGrayColor();
                float boon1 = (float) BOON1_RANGE.get(hullSize);
                float boon2 = (float) BOON2_RANGE.get(hullSize);
                float seesaw = (float) BIG_SAW.get(hullSize);
                float dep_debt = (float) DEP_BAD.get(hullSize);
		
                LabelAPI bullet;
                tooltip.addPara(YNA_MD.hull("mod1_flavor"), gray, opad);
                tooltip.addPara(YNA_MD.hull("mod1_flavor2"), gray, pad);
                
                tooltip.setBulletedListMode(" • ");
                
		tooltip.addSectionHeading(YNA_MD.base("good"), Alignment.MID, opad);
                bullet = tooltip.addPara(YNA_MD.hull("good_range"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) boon1 + "", "+" + (int) boon2 + "", "+" + (int) BOON3_RANGE + "" );
                bullet.setHighlight(YNA_MD.base("ba"), "+" + (int) boon1 + "", "+" + (int) boon2 + "", "+" + (int) BOON3_RANGE + "" );
                bullet.setHighlightColors(h, good, good, good);
                bullet = tooltip.addPara(YNA_MD.hull("good_bean"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) BOOP_RANGE + "", "-" + (int) BEAM_EFF + "%" );
                /*
                bullet = tooltip.addPara(YNA_MD.hull("good_pd_rng"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) BOOP_RANGE + "" );
                bullet.setHighlight(YNA_MD.base("base"), "+" + (int) BOOP_RANGE + "");
                bullet.setHighlightColors(h, good);
                bullet = tooltip.addPara(YNA_MD.hull("good_bean"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) BEAM_RANGE + "", "-" + (int) BEAM_EFF + "%" );
                bullet = tooltip.addPara(YNA_MD.hull("good_armor"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "" + (int) ARMOR_BIG + "x", "" + (float) ARMOR_BIG_NO + "x" );
                bullet.setHighlight("" + (int) ARMOR_BIG + "x", "" + (float) ARMOR_BIG_NO + "x" );
                bullet.setHighlightColors(good, bad);
                */
                bullet = tooltip.addPara(YNA_MD.hull("good_he_rec"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                        "-" + (int) HE_RES + "%", "-" + (int) BEAM_RES + "%" );
                    bullet.setHighlight("-" + (int) HE_RES + "%", "-" + (int) BEAM_RES + "%", YNA_MD.hull("good_he_rec_g"));
                    bullet.setHighlightColors(good, good, gray);
                    tooltip.setBulletedListMode("   ");
                    tooltip.addPara("%s", 0f, Global.getSettings().getColor("standardTextColor"), gray,
                        YNA_MD.hull("good_he_rec_extra"));
                tooltip.setBulletedListMode(" • ");
                bullet = tooltip.addPara(YNA_MD.hull("good_wep_thresh"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) THRESHOLD_BONUS + "%" );
                bullet = tooltip.addPara(YNA_MD.hull("good_sensor"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) BIG_SEE + "%", "+" + (int) seesaw + "" );
                
		tooltip.addSectionHeading(YNA_MD.base("bad"), Alignment.MID, opad);
                bullet = tooltip.addPara(YNA_MD.hull("bad_frag_rec"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                        "+" + (int) FRAG_BAD + "%" );
                    tooltip.setBulletedListMode("   ");
                    tooltip.addPara("%s", 0f, Global.getSettings().getColor("standardTextColor"), gray,
                        YNA_MD.hull("bad_frag_rec_extra"));
                tooltip.setBulletedListMode(" • ");
                bullet = tooltip.addPara(YNA_MD.hull("bad_deploy"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), bad,
                    "+" + (int) dep_debt + "" );
                
		tooltip.addSectionHeading(YNA_MD.base("comp"), Alignment.MID, opad);
                bullet = tooltip.addPara(YNA_MD.hull("cost"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "-3/-5/-8");
                bullet = tooltip.addPara(YNA_MD.hull("comp"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), gray,
                    "");
		bullet.setHighlight(YNA_MD.base("hl_bal_find"));
		bullet.setHighlightColors(h);
                
            tooltip.setBulletedListMode(null);
	}
    
	@Override
	public boolean affectsOPCosts() {
		return true;
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null && ship.getHullSpec().getHullId().startsWith("yna_");
	}
}
