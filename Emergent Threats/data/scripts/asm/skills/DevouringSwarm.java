package data.scripts.asm.skills;

import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class DevouringSwarm {
	
	public static float SIZE_SIZE_BOOST = 60f;
	public static float RESPAWN_RATE_BOOST = 30f;
	public static String FRAGCO_MOD_ID = "fragment_coordinator";
	public static String SECFAB_MOD_ID = "secondary_fabricator";
	
	public static class Level1 implements ShipSkillEffect {
		
		public static float CR_CHANGE = 30f;
		
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
			
			if (stats.getVariant().hasHullMod("asm_threat_compromised") 
						|| stats.getVariant().hasHullMod("threat_hullmod")){
				stats.getMaxCombatReadiness().modifyFlat(id, CR_CHANGE * 0.01f, "Fragment synchronization");
			}
			else stats.getMaxCombatReadiness().modifyFlat(id, -CR_CHANGE * 0.01f, "Fragment decoherence");
		}
		
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			stats.getMaxCombatReadiness().unmodify(id);
		}
		
		public String getEffectDescription(float level) {
			return "+30% CR when commanding Threat Infected hull, -30% CR for all other hulls.";
		}
		
		public String getEffectPerLevelDescription() {
			return null;
		}
		
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}
	
	public static class Level2 implements ShipSkillEffect {
		
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
			
			if (!stats.getVariant().hasHullMod(FRAGCO_MOD_ID)){
				stats.getDynamic().getMod(Stats.FRAGMENT_SWARM_SIZE_MOD).modifyPercent(id, SIZE_SIZE_BOOST);
			}
			if (!stats.getVariant().hasHullMod(SECFAB_MOD_ID)){
				stats.getDynamic().getStat(Stats.FRAGMENT_SWARM_RESPAWN_RATE_MULT).modifyPercent(id, RESPAWN_RATE_BOOST);
			}
		}
		
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			stats.getDynamic().getMod(Stats.FRAGMENT_SWARM_SIZE_MOD).unmodify(id);
			stats.getDynamic().getStat(Stats.FRAGMENT_SWARM_RESPAWN_RATE_MULT).unmodify(id);
		}
		
		public String getEffectDescription(float level) {
			return "Gain the Fragment Coordinator and Secondary Fabricator bonuses even when the hullmods are not equipped.";
		}
		
		public String getEffectPerLevelDescription() {
			return null;
		}
		
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}
	
	public static class Level3 implements ShipSkillEffect {
		
		public static float SIZE_SMOD_BOOST = 40f;
		public static float RESPAWN_SMOD_BOOST = 20f;
		
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
			boolean fragCoIsBuiltIn = false;
			boolean secFabIsBuiltIn = false;
			
			for (String mod : stats.getVariant().getSMods()) {
				if (FRAGCO_MOD_ID.equals(mod)) fragCoIsBuiltIn = true;
				if (SECFAB_MOD_ID.equals(mod)) secFabIsBuiltIn = true;
			}
			
			if (stats.getVariant().hasHullMod("fragment_coordinator") && !fragCoIsBuiltIn){
				stats.getDynamic().getMod(Stats.FRAGMENT_SWARM_SIZE_MOD).modifyPercent(id, SIZE_SMOD_BOOST);
			}
			if (stats.getVariant().hasHullMod("secondary_fabricator") && !secFabIsBuiltIn){
				stats.getDynamic().getStat(Stats.FRAGMENT_SWARM_RESPAWN_RATE_MULT).modifyPercent(id, RESPAWN_SMOD_BOOST);
			}
		}
		
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			stats.getDynamic().getMod(Stats.FRAGMENT_SWARM_SIZE_MOD).unmodify(id);
			stats.getDynamic().getStat(Stats.FRAGMENT_SWARM_RESPAWN_RATE_MULT).unmodify(id);
		}
		
		public String getEffectDescription(float level) {
			return "Gain the effects of Coordinator and Fabricator S-mods when the hullmods are equipped but not built-in.";
		}
		
		public String getEffectPerLevelDescription() {
			return null;
		}
		
		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}
}