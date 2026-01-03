package Jaydee8652.JaydeePiracy.scripts.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.rpg.Person;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.subsystems.MagicSubsystemsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class jdp_uneasymovements {
	public static Map<HullSize, Float> BONUS = new HashMap<HullSize, Float>();
	public static float DAMAGE_BONUS_PERCENT = 25f;


	public static class Level1 implements ShipSkillEffect {
		public void apply(MutableShipStatsAPI stats, HullSize hullSize, String id, float level) {
			/*ShipAPI ship = (ShipAPI) stats.getEntity();
			List<FleetMemberAPI> fleet = ship.getFleetCommander().getFleet().getFleetData().getMembersListCopy();
			for (FleetMemberAPI fleetMember : fleet) {
				PersonAPI captain = fleetMember.getCaptain();
				if (captain == null || captain.isDefault()) return;
				//MagicSubsystemsManager.addSubsystemToShip(fleetMember, new jdp_flowerfishUneasyMovements.jdp_maerulanphaseskimmer(fleetMember));
			}*/
		}

		public void unapply(MutableShipStatsAPI stats, HullSize hullSize, String id) {
			/*ShipAPI ship = (ShipAPI) stats.getEntity();
			List<FleetMemberAPI> fleet = ship.getFleetCommander().getFleet().getFleetData().getMembersListCopy();
			for (FleetMemberAPI fleetMember : fleet) {
				PersonAPI captain = fleetMember.getCaptain();
				if (captain == null || captain.isDefault()) return;
			//	MagicSubsystemsManager.removeSubsystemFromShip(fleetMember, jdp_flowerfishUneasyMovements.jdp_maerulanphaseskimmer(fleetMember));
			}*/
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