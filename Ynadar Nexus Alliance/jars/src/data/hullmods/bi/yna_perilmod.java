package data.hullmods.bi;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.everyframe.yna_BlockedHullmodDisplayScript;
import data.scripts.util.YNA_MD;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class yna_perilmod extends BaseHullMod {
	public static final float COST_RED  = 10;
	public static final float COST_RED_MIS  = 5;
	public static final float COST_M_RED  = 3;
	public static final float D_RANGE  = 100;
	public static final float PD_MALUS = 40;
        
	public static final float ARMOR_MULT = 2.5f;
	public static final float ARMOR_STRENGTH = 2.5f;
	public static final float FLUX_MULT = 1.5f;
        
	public static final float SENS_ADD = 60f;
	public static final float PROF_ADD = 60f;
	public static final float SIGHT_BONUS = 20f;
	public static final float TURRET_BONUS = 50f;
	public static final float EXPLOD_D = 1.5f;
	public static final float EXPLOD_R = 1.2f;
        
        
	private static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
	static
	{
		// These hullmods will automatically be removed
		// This prevents unexplained hullmod blocking
		BLOCKED_HULLMODS.add("targetingunit");
		BLOCKED_HULLMODS.add("dedicated_targeting_core");
	}
        
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, -COST_RED);
		stats.getDynamic().getMod(Stats.LARGE_ENERGY_MOD).modifyFlat(id, -COST_RED);
		stats.getDynamic().getMod(Stats.LARGE_MISSILE_MOD).modifyFlat(id, -COST_RED_MIS);
		stats.getDynamic().getMod(Stats.MEDIUM_BALLISTIC_MOD).modifyFlat(id, -COST_M_RED);
		stats.getDynamic().getMod(Stats.MEDIUM_ENERGY_MOD).modifyFlat(id, -COST_M_RED);
                
                stats.getArmorBonus().modifyMult(id, ARMOR_MULT);
                stats.getEffectiveArmorBonus().modifyMult(id, 1f / ARMOR_STRENGTH);
                stats.getFluxCapacity().modifyMult(id, FLUX_MULT);
                stats.getFluxDissipation().modifyMult(id, FLUX_MULT);
                
		stats.getBallisticWeaponRangeBonus().modifyPercent(id, D_RANGE);
		stats.getEnergyWeaponRangeBonus().modifyPercent(id, D_RANGE);
                stats.getNonBeamPDWeaponRangeBonus().modifyPercent(id, -PD_MALUS);
                stats.getBeamPDWeaponRangeBonus().modifyPercent(id, -PD_MALUS);
                
                stats.getSensorStrength().modifyFlat(id, SENS_ADD);
                stats.getSensorProfile().modifyFlat(id, PROF_ADD);
                stats.getSightRadiusMod().modifyMult(id, 1 + (SIGHT_BONUS * 0.01f));
            
                stats.getWeaponHealthBonus().modifyMult(id, (1 + (TURRET_BONUS / 100)));
                
                stats.getDynamic().getStat(Stats.EXPLOSION_DAMAGE_MULT).modifyMult(id, EXPLOD_D);
                stats.getDynamic().getStat(Stats.EXPLOSION_RADIUS_MULT).modifyMult(id, EXPLOD_R);
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		for (String tmp : BLOCKED_HULLMODS) {
			if (ship.getVariant().getHullMods().contains(tmp)) {
				ship.getVariant().removeMod(tmp);
				yna_BlockedHullmodDisplayScript.showBlocked(ship);
			}
		}
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
		
                LabelAPI bullet;
                tooltip.setBulletedListMode(" • ");
                
		tooltip.addSectionHeading(YNA_MD.base("feat"), Alignment.MID, opad);
                bullet = tooltip.addPara(YNA_MD.peril("armor"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "x" + (float) ARMOR_MULT + "", "x" + (float) ARMOR_STRENGTH + "");
		bullet.setHighlight("x" + (float) ARMOR_MULT + "", "x" + (float) ARMOR_STRENGTH + "");
		bullet.setHighlightColors(good, bad);
                bullet = tooltip.addPara(YNA_MD.peril("flux"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "x" + (float) FLUX_MULT + "", YNA_MD.peril("fluxtra"));
		bullet.setHighlight("x" + (float) FLUX_MULT + "", YNA_MD.peril("fluxtra"));
		bullet.setHighlightColors(good, gray);
                bullet = tooltip.addPara(YNA_MD.base("good_range"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) D_RANGE + "%");
                bullet = tooltip.addPara(YNA_MD.base("good_pd_range"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) (D_RANGE - PD_MALUS) + "%");
                bullet = tooltip.addPara(YNA_MD.peril("sensor"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "+" + (int) PROF_ADD + " units");
                bullet = tooltip.addPara(YNA_MD.peril("sight"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) SIGHT_BONUS + "%");
                bullet = tooltip.addPara(YNA_MD.peril("turret"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) TURRET_BONUS + "%");
                
		tooltip.addSectionHeading(YNA_MD.base("comp"), Alignment.MID, opad);
                bullet = tooltip.addPara(YNA_MD.base("comp_large"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "-10", "-5");
                bullet = tooltip.addPara(YNA_MD.base("comp_med"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "-3");
                bullet = tooltip.addPara(YNA_MD.base("comp_range_full"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), h,
                    "");
		bullet.setHighlight("" + YNA_MD.base("comp_range") + "");
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
