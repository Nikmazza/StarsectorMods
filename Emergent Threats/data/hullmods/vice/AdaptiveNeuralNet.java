package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.util.RemnantSubsystemsUtil;

public class AdaptiveNeuralNet extends BaseHullMod {

	private static float CR_BONUS = 4f;
	private static float AI_BONUS = 2f;
	private float TOTAL_BONUS = 0f; 	//description text only, bonus is set dynamically in method.
	private static String THIS_MOD = "vice_adaptive_neural_net";
	private boolean isHumanCaptain = false; //for description text only
	
	//Utility variables
	private RemnantSubsystemsUtil util = new RemnantSubsystemsUtil();
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		float bonus = CR_BONUS;
		String coreId = null;
		
		//else checks for RAT interactions where human can captain AI ships through Automation XO or skill
		if (stats.getFleetMember() != null && stats.getFleetMember().getCaptain() != null 
				&& !stats.getFleetMember().getCaptain().isAICore()
				&& !stats.getFleetMember().getCaptain().isDefault()) isHumanCaptain = true;
		else if (stats.getFleetMember() != null && stats.getFleetMember().getCaptain() != null 
				&& (stats.getFleetMember().getCaptain().getStats().hasSkill("rat_augmented") 
					|| stats.getFleetMember().getCaptain().isPlayer())) isHumanCaptain = true;
		
		if (stats.getFleetMember() != null) coreId = stats.getFleetMember().getCaptain().getAICoreId();
		
		if ((Commodities.GAMMA_CORE).equals(coreId)) bonus = CR_BONUS + AI_BONUS * 1f;
		else if ((Commodities.BETA_CORE).equals(coreId)) bonus = CR_BONUS + AI_BONUS * 2f;
		else if ((Commodities.ALPHA_CORE).equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		else if ((Commodities.OMEGA_CORE).equals(coreId)) bonus = CR_BONUS + AI_BONUS * 4f;
		
		else if (("asm_relic_gamma").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 1f;
		else if (("asm_relic_beta").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 2f;
		
		else if (("tahlan_daemoncore").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 2f;
		else if (("tahlan_archdaemoncore").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		
		else if (("sotf_sierracore_officer").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		else if (("sotf_projectsiren_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		
		else if (("sotf_ichip_nightingale").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 2f;
		else if (("sotf_ichip_barrow_d").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 1f;
		else if (("sotf_ichip_barrow").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		else if (("sotf_ichip_seraph").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		
		else if (("sotf_ichip_sliver").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 1f;
		else if (("sotf_ichip_sliver1").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 1f;
		else if (("sotf_ichip_sliver2").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 1f;
		else if (("sotf_ichip_echo").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 2f;
		else if (("sotf_ichip_echo1").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 2f;
		else if (("sotf_ichip_annex").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;

		else if (("rat_chronos_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		else if (("rat_cosmos_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		else if (("rat_seraph_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		else if (("rat_neuro_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		else if (("rat_exo_processor").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		else if (("rat_primordial_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 4f;

		else if (("zea_dusk_boss_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 4f;
		else if (("zea_dawn_boss_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 4f;
		else if (("zea_elysia_boss_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 4f;
		else if (("zea_dormant_dusk_boss_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 4f;
		
		else if (("volantian_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 2f;
		
		else if (("ix_panopticon_core").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 2f;
		else if (("ix_panopticon_instance").equals(coreId)) bonus = CR_BONUS + AI_BONUS * 3f;
		
		boolean isModule = false;
		if (util.isModuleCheck(stats)) {
			bonus = CR_BONUS + AI_BONUS * 0f;
			isModule = true;
		}
		
		//generic bonus based on level for cores not in list
		if (!isModule && coreId != null && bonus == CR_BONUS) {
			int coreLevel = stats.getFleetMember().getCaptain().getStats().getLevel();
			if (coreLevel <= 1) bonus = CR_BONUS;
			else if (coreLevel <= 4) bonus = CR_BONUS + AI_BONUS;
			else if (coreLevel <= 6) bonus = CR_BONUS + AI_BONUS * 2f;
			else if (coreLevel <= 8) bonus = CR_BONUS + AI_BONUS * 3f;
			else if (coreLevel >= 9) bonus = CR_BONUS + AI_BONUS * 4f;
		}
		
		TOTAL_BONUS = bonus;
		
		stats.getMaxCombatReadiness().modifyFlat(id, bonus * 0.01f, "Adaptive neural net");
		
		util.applyShipwideHullMod(stats.getVariant(), id, true);
		util.addModuleHandler(stats);
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		//self clear if invalid player hull, but do not clear on modules since they can be added by handler
		if (!isApplicableToShip(ship) && ship.getOwner() == 0 && !util.isModuleCheck(ship)) {
			ship.getVariant().getHullMods().remove(id);
		}
	}
	
	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return true;
	}
	
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		if (isForModSpec || ship == null) return;
		if (!ship.getVariant().hasHullMod(THIS_MOD)) return; //do not show extra text if not installed on hull.
		boolean isMMI = Global.getSector().getMemoryWithoutUpdate().is("$xo_mmi_is_active", true);
		float bonus = isMMI && isHumanCaptain ? 10f : TOTAL_BONUS;
		String standardString = isMMI && isHumanCaptain ? "This ship is receiving a Synthesis CR bonus of %s." : "This ship is receiving a CR bonus of %s.";
		String s = (util.isModuleCheck(ship)) ? "Ship module bonus is limited to %s." : standardString;
		tooltip.addPara(s, 10f, Misc.getHighlightColor(), "" + (int) bonus + "%");
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		if (util.hasDriveField(ship)) return false;
		if (util.isModuleCheck(ship)) return false;
		return (util.isApplicable(ship) && util.isOnlyRemnantMod(ship));
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (util.hasDriveField(ship)) return util.getIncompatibleCauseString("drivefield");
		if (ship.getVariant().hasHullMod("vice_shipwide_integration") && (ship.getVariant().hasHullMod(THIS_MOD))) return null;
		if (util.isModuleCheck(ship)) return util.getIncompatibleCauseString("hub");
		if (!util.isApplicable(ship)) return util.getIncompatibleCauseString("manufacturer");
		if (!util.isOnlyRemnantMod(ship)) return util.getIncompatibleCauseString("modcount");
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) CR_BONUS + "%";
		if (index == 1) return "" + (int) AI_BONUS + "%";
		return null;
	}
}