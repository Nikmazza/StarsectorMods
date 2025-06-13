package Jaydee8652.JaydeePiracy.scripts.skills;

import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

import java.util.HashMap;
import java.util.Map;

//Made for NEON! Go download NSP!
public class jdp_weirdslayer {
	public static Map<HullSize, Float> BONUS = new HashMap<HullSize, Float>();
	public static float DAMAGE_BONUS_PERCENT = 25f;


	public static class Level1 implements ShipSkillEffect {
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
			return "+" + bonus + "% damage dealt to existential threats.";		}

		public String getEffectPerLevelDescription() {
			return null;
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}
}