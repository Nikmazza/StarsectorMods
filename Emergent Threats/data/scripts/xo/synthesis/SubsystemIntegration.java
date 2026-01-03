package data.scripts.xo.synthesis;

import java.util.Collection;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CharacterDataAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

import data.scripts.vice.util.RemnantSubsystemsUtil;

public class SubsystemIntegration extends SCBaseSkillPlugin {
    
	private static float CR_BOOST = 0.05f;
	private RemnantSubsystemsUtil util = new RemnantSubsystemsUtil();
	
	@Override
    public String getAffectsString() {
        return "all ships in the fleet";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
        tooltip.addPara("All ships can use AI Adaptive Subsystems, ships that can already do so gain 5%% to combat readiness", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
		tooltip.addSpacer(10f);
		tooltip.addPara("Acquire the AI Subsystem Integration hullmod", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "AI Subsystem Integration");
    }

    @Override
    public void applyEffectsBeforeShipCreation(SCData data, MutableShipStatsAPI stats, ShipVariantAPI variant, ShipAPI.HullSize hullSize, String id) {
        if (util.isApplicableWithoutSynthesis(variant)) stats.getMaxCombatReadiness().modifyFlat(id, CR_BOOST, "Synthesis executive officer");
		
    }

	@Override
    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) {
		
    }
	
	@Override
	public void onActivation(SCData data) {
		if (data.isNPC()) data.getFleet().getMemoryWithoutUpdate().set("$xo_synthesis_fleet", true);
		if (data.isPlayer() && !Global.getSector().getMemoryWithoutUpdate().is("$gave_SI_hullmods", true)) {
			CharacterDataAPI player = Global.getSector().getCharacterData();
			player.addHullMod("vice_ai_subsystem_integration");
			Global.getSector().getMemoryWithoutUpdate().set("$gave_SI_hullmods", true);
		}
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_synthesis_is_active", true);
	}
	
	@Override
	public void onDeactivation(SCData data) {
		if (data.isPlayer()) Global.getSector().getMemoryWithoutUpdate().set("$xo_synthesis_is_active", false);
		
		//delete invalid adaptive hullmods after Synthesis officer is removed
		if (Global.getSector() == null || Global.getSector().getPlayerFleet() == null) return;
		CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
		List<FleetMemberAPI> fleetList = fleet.getMembersWithFightersCopy();
		List<MarketAPI> marketList = Global.getSector().getEconomy().getMarketsCopy();
		for (MarketAPI market : marketList) {
			if (market.getSubmarket(Submarkets.SUBMARKET_STORAGE) != null) {
				CargoAPI storage = market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
				List<FleetMemberAPI> storageList = storage.getMothballedShips().getMembersListCopy();
				if (!storageList.isEmpty()) {
					for (FleetMemberAPI ship : storageList) fleetList.add(ship);
				}
			}
		}
		
		for (FleetMemberAPI member : fleetList) {
			String modToDelete = null;
			Collection<String> modList = member.getVariant().getNonBuiltInHullmods();
			for (String mod : modList) {
				if (mod.startsWith("vice_adaptive") && !util.isApplicable(member.getVariant())) {
					if (mod.equals("vice_adaptive_entropy_projector_abyssal")) continue;
					modToDelete = mod;
				}
			}
			if (modToDelete != null) member.getVariant().removeMod(modToDelete);
		}
		// **/
	}
}
