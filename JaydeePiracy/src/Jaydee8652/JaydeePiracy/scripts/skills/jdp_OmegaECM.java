package Jaydee8652.JaydeePiracy.scripts.skills;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import static Jaydee8652.JaydeePiracy.scripts.skills.jdp_omegakin.DAMAGE_BONUS_PERCENT;

public class jdp_OmegaECM {//Just a clone of Omega ECM with a human-readable description
	
	public static Map<HullSize, Float> BONUS = new HashMap<ShipAPI.HullSize, Float>();
	static {
		BONUS.put(HullSize.FRIGATE, 5f);
		BONUS.put(HullSize.DESTROYER, 10f);
		BONUS.put(HullSize.CRUISER, 15f);
		BONUS.put(HullSize.CAPITAL_SHIP, 30f);
	}

	public static class Level1 implements ShipSkillEffect {
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
			Float bonus = BONUS.get(hullSize);
			if (bonus != null) {
				stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, bonus);
			}
		}
		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).unmodify(id);
		}
		public String getEffectDescription(float level) {
			int min = (int)Math.round(BONUS.get(HullSize.FRIGATE));
			int lowmid = (int)Math.round(BONUS.get(HullSize.DESTROYER));
			int highmid = (int)Math.round(BONUS.get(HullSize.CRUISER));
			int max = (int)Math.round(BONUS.get(HullSize.CAPITAL_SHIP));
			return "Increases ECM rating of the piloted ship by " + min + "/" + lowmid + "/" + highmid + "/" + max + "% depending on ship size";
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
			ShipAPI ship = (ShipAPI) stats.getEntity();
			if (ship != null && !ship.hasListenerOfClass(jdp_omegakinListener.class)) {
				ship.addListener(new jdp_omegakinListener());
			}
		}

		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			ShipAPI ship = (ShipAPI) stats.getEntity();
			if (ship != null && ship.hasListenerOfClass(jdp_omegakinListener.class)) {
				ship.removeListenerOfClass(jdp_omegakinListener.class);
			}
		}

		public String getEffectDescription(float level) {
			int bonus = (int) DAMAGE_BONUS_PERCENT;
			return "+" + bonus + "% damage dealt by omega-type weaponry";		}

		public String getEffectPerLevelDescription() {
			return null;
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}

	//More damage to dwellers? Might be a bit much.
	/*public static class Level3 implements ShipSkillEffect {
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
			ShipAPI ship = (ShipAPI) stats.getEntity();
			if (ship != null && !ship.hasListenerOfClass(jdp_weirdslayerListener.class)) {
				ship.addListener(new jdp_weirdslayerListener());
			}
		}

		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			ShipAPI ship = (ShipAPI) stats.getEntity();
			if (ship != null && ship.hasListenerOfClass(jdp_weirdslayerListener.class)) {
				ship.removeListenerOfClass(jdp_weirdslayerListener.class);
			}
		}

		public String getEffectDescription(float level) {
			int bonus = (int) DAMAGE_BONUS_PERCENT;
			return "+" + bonus + "% damage dealt to *singers*";		}

		public String getEffectPerLevelDescription() {
			return null;
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}*/
}
