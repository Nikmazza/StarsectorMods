package data.hullmods.ix;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import data.scripts.ix.PanopticonCorePlugin;
import data.scripts.ix.util.PanopticInterfaceUtil;

public class PanopticInterfaceCommand extends BaseHullMod {
	
	private static String COMMAND_CORE_ID = "ix_command_core";
	private static String HANDLER_MOD_ID = "ix_panoptic_command_handler";
	
	private static String SKILL_NAME_1 = "Field Modulation";
	private static String SKILL_NAME_2 = "Gunnery Implant";
	private static String SKILL_NAME_3 = "Target Analysis";
	private static String SKILL_NAME_4 = "Sword of the Fleet";	
	private static String SOTF_SKILL_ID = "ix_sword_of_the_fleet";
	
	private static float SHIELD_BONUS_UNFOLD = 100f;
	private static float ENERGY_REGEN_BONUS = 50f;
	private static float ENERGY_ROF_BONUS = 10f;
	private static String IX_HULLMOD = "ix_ninth";
	
	private static float CR_PENALTY_MAX = 30f;
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {		
		if (stats.getVariant().hasHullMod("automated")) return;
		stats.getVariant().addMod(HANDLER_MOD_ID);
		//grant Sword of the Fleet bonuses to human captain who lacks the skill
		if (hasCaptainWithoutSkill(stats)) {
			stats.getShieldUnfoldRateMult().modifyPercent(id, SHIELD_BONUS_UNFOLD);
			boolean isIX = false;
			boolean isRegenBoosted = false;
			isIX = (stats.getVariant().getHullSpec().getManufacturer().equals("IX Battlegroup") 
					|| stats.getVariant().hasHullMod(IX_HULLMOD));
			isRegenBoosted = (!stats.getEnergyAmmoRegenMult().isUnmodified() 
					&& !stats.getEnergyAmmoRegenMult().isNegative());
			if (isIX && !isRegenBoosted) stats.getEnergyAmmoRegenMult().modifyPercent(id, ENERGY_REGEN_BONUS);
			stats.getEnergyRoFMult().modifyPercent(id, ENERGY_ROF_BONUS);
		}
		//CR penalty applied before null check
		if (stats.getFleetMember() == null || stats.getFleetMember().getOwner() != 0) return;
		float crPenalty = PanopticInterfaceUtil.getReadinessPenalty(stats.getFleetMember(), hullSize);
		stats.getMaxCombatReadiness().modifyFlat(id, -crPenalty * 0.01f, "Panoptic Interface");
		
		if (stats.getFleetMember().getCaptain() == null 
				|| !stats.getFleetMember().getCaptain().isDefault()) return;
		if (PanopticInterfaceUtil.hasConflictMod(stats.getVariant())) return;
		PersonAPI p = new PanopticonCorePlugin().createPerson(COMMAND_CORE_ID, "player", null);
		stats.getFleetMember().setCaptain(p);
		removeCore();
	}
	
	private boolean hasCaptainWithoutSkill(MutableShipStatsAPI stats) {
		if (stats != null && stats.getFleetMember() != null && stats.getFleetMember().getCaptain() != null && 
				!stats.getFleetMember().getCaptain().getStats().hasSkill(SOTF_SKILL_ID)) return true;
		return false;
	}

	private boolean hasCaptainWithoutSkill(ShipAPI ship) {
		if (ship != null && ship.getFleetMember() != null && ship.getFleetMember().getCaptain() != null && 
				!ship.getFleetMember().getCaptain().getStats().hasSkill(SOTF_SKILL_ID)) return true;
		return false;
	}
	
	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return true;
	}
	
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		if (ship == null) return;
		if (ship.getVariant().hasHullMod("automated")) {
			tooltip.addPara("Interface cannot be activated on automated ship", Misc.getNegativeHighlightColor(), 10f);
			return;
		}
		boolean isAddSkill = false;
		String x = "Sword of the Fleet skill boost is ";
		String a = hasCaptainWithoutSkill(ship) ? "active" : "inactive";
		x += a;
		if (hasCaptainWithoutSkill(ship)) tooltip.addPara(x, Misc.getPositiveHighlightColor(), 10f);
		else tooltip.addPara(x, Misc.getNegativeHighlightColor(), 10f);
		
		String s = "";
		float crPenalty = 0f;
		if (PanopticInterfaceUtil.hasConflictMod(ship.getVariant())) s = "Warning: Incompatible AI system present. Interface is inactive.";
		else if (Global.getSector() != null) {
			crPenalty = PanopticInterfaceUtil.getReadinessPenalty(ship.getFleetMember(), hullSize);
			int fleetCount = PanopticInterfaceUtil.getPanopticShipCount(ship.getFleetMember());
			s = "Combat Readiness reduced by " + (int) crPenalty + "%";
			String word = fleetCount > 1 ? "ships" : "ship";
			String append = " from " + fleetCount + " interfaced " + word;
			s += append;
		}
		if (!s.isEmpty()) {
			if (crPenalty == 0f) {
				s = "CR penalty is currently negated throughout the fleet";
				tooltip.addPara(s, Misc.getPositiveHighlightColor(), 10f);
			}
			else tooltip.addPara(s, Misc.getNegativeHighlightColor(), 10f);
		}
	}
	
	private void removeCore() {
		try {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			cargo.removeCommodity(COMMAND_CORE_ID, 1f);
		}
		catch (Exception e) {}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		String s = "" + (int) PanopticInterfaceUtil.PENALTY_FRIGATE + "/"
						+ (int) PanopticInterfaceUtil.PENALTY_DESTROYER + "/"
						+ (int) PanopticInterfaceUtil.PENALTY_CRUISER + "/"
						+ (int) PanopticInterfaceUtil.PENALTY_CAPITAL + "%";
		if (index == 0) return SKILL_NAME_1;
		if (index == 1) return SKILL_NAME_2;
		if (index == 2) return SKILL_NAME_3;
		if (index == 3) return SKILL_NAME_4;
		if (index == 4) return "human captain";
		if (index == 5) return "" + (int) SHIELD_BONUS_UNFOLD + "% to raise shield speed";
		if (index == 6) return "" + (int) ENERGY_REGEN_BONUS + "% energy weapon ammo regeneration rate";
		if (index == 7) return "" + (int) ENERGY_ROF_BONUS + "% energy weapon rate of fire";
		if (index == 8) return s;
		if (index == 9) return "" + (int) PanopticInterfaceUtil.CR_PENALTY_MAX + "%";
		return null;
	}
}