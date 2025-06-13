package data.scripts.vice.skills;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;

public class AIFleetCommander {
	
	private static float DP_REDUCTION_SYNTHESIS = 1f;
	private static float DP_REDUCTION_FRIGATE = 1f;
	private static float DP_REDUCTION_DESTROYER = 2f;
	private static float DP_REDUCTION_CRUISER = 3f;
	private static float DP_REDUCTION_CAPITAL = 4f;	
	
	private static float getDeployReduction(HullSize hullSize) {
		float deployReduction = 0f;
		MemoryAPI mem = Global.getSector().getMemoryWithoutUpdate();
		if (hullSize.equals(HullSize.FRIGATE)) deployReduction = DP_REDUCTION_FRIGATE;
		else if (hullSize.equals(HullSize.DESTROYER)) deployReduction = DP_REDUCTION_DESTROYER;
		else if (hullSize.equals(HullSize.CRUISER)) deployReduction = DP_REDUCTION_CRUISER;
		else if (hullSize.equals(HullSize.CAPITAL_SHIP)) deployReduction = DP_REDUCTION_CAPITAL;
		if (mem.is("$xo_synthesis_is_active", true)) deployReduction += DP_REDUCTION_SYNTHESIS;
		return deployReduction;
	}
	
	public static class Level1 implements ShipSkillEffect {
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
			if (Misc.isAutomated(stats)) {
				float deployReduction = getDeployReduction(hullSize);
				stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, -deployReduction);
				if (stats.getDynamic().getStat(Stats.DEPLOYMENT_POINTS_MOD).getModifiedValue() < 0f) {
					stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyMult(id + "_zero", 0f);
				}
				
			}
		}
		
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).unmodify(id);
			stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).unmodify(id + "_zero");
		}
		
		public String getEffectDescription(float level) {
			return null; //string is in 1a/b due to "type":"ALL_SHIPS_IN_FLEET" preventing string from showing 
		}
		
		public String getEffectPerLevelDescription() {
			return null;
		}
		
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}
	
	public static class Level1a implements ShipSkillEffect {
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {}
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {}
		public String getEffectPerLevelDescription() {return null;}
		public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
		
		public String getEffectDescription(float level) {
			return "Reduce deployment point cost of all automated ships by 1/2/3/4, based on hull size";
		}
	}
	
	public static class Level1b implements ShipSkillEffect {
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {}
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {}
		public String getEffectPerLevelDescription() {return null;}
		public ScopeDescription getScopeDescription() {return ScopeDescription.NONE;}
		
		public String getEffectDescription(float level) {
			String s = "Lower deployment point cost also reduces the use of automated ship points";
			if (Global.getSettings().getModManager().isModEnabled("second_in_command")) {
				s = "Reduce cost by an additional 1 when advised by a Synthesis aptitude executive officer";
			}
			return s;
		}
	}
}