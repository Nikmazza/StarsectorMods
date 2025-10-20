package Jaydee8652.JaydeePiracy.scripts.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class jdp_perpetualpursuit {
	public static Map<HullSize, Float> BONUS = new HashMap<HullSize, Float>();

	public static class Level1 implements ShipSkillEffect {
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
			//ShipAPI ship = (ShipAPI) stats.;
			//ship.getFleetCommander().addTag("jdp_flowerfishPerpetualPursuit");

		}

		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			//ShipAPI ship = (ShipAPI) stats.getEntity();
			//ship.getFleetCommander().removeTag("jdp_flowerfishPerpetualPursuit");
		}

		public String getEffectDescription(float level) {
			return "null";
		}

		public String getEffectPerLevelDescription() {
			return null;
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}
}