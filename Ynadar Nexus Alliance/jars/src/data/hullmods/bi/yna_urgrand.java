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

public class yna_urgrand extends BaseHullMod {
	public static final float COST_RED  = 10;
	public static final float COST_RED_MIS  = 5;
	public static final float D_RANGE  = 70;
	public static final float PD_MALUS = 10;
        
        
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
                
		stats.getBallisticWeaponRangeBonus().modifyPercent(id, D_RANGE);
		stats.getEnergyWeaponRangeBonus().modifyPercent(id, D_RANGE);
                stats.getNonBeamPDWeaponRangeBonus().modifyPercent(id, -PD_MALUS);
                stats.getBeamPDWeaponRangeBonus().modifyPercent(id, -PD_MALUS);
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
                
		tooltip.addSectionHeading(YNA_MD.base("good"), Alignment.MID, opad);
                bullet = tooltip.addPara(YNA_MD.base("good_range"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) D_RANGE + "%");
                bullet = tooltip.addPara(YNA_MD.base("good_pd_range"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "+" + (int) (D_RANGE - PD_MALUS) + "%");
                
		tooltip.addSectionHeading(YNA_MD.base("comp"), Alignment.MID, opad);
                bullet = tooltip.addPara(YNA_MD.base("comp_large"), LOAD_OF_BULL, Global.getSettings().getColor("standardTextColor"), good,
                    "-10", "-5");
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
