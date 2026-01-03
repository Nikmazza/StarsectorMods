package data.hullmods.tw;

import java.util.List;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

import org.lazywizard.lazylib.MathUtils;
import data.scripts.ix.util.NameListUtil;

public class AuroraASMHandler extends BaseHullMod {
	
	private static String TW_AURORA_VAR_ID = "aurora_tw_encounter";
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		boolean apply = true;
		if (stats.getFleetMember() != null && Global.getSector().getPlayerFleet() != null) {
			List<FleetMemberAPI> fleetList = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
			for (FleetMemberAPI member : fleetList) {
				if (member.getVariant().getHullVariantId() == stats.getVariant().getHullVariantId()) {
					apply = false;
				}
			}
		}
		if (apply) {
			//sensor shadow cannot be salvaged but regular hostile version can be
			if (Global.getSector().getMemoryWithoutUpdate().is("$twc_midir_defected", true)) {
				stats.getVariant().addTag(Tags.UNRECOVERABLE);
			}
			stats.getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(id, -30f);
		}
	}
	
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);
		if (ship == null || !Global.getSector().getMemoryWithoutUpdate().is("$twc_midir_defected", true)) return;
		if (ship.getFleetMember().getOwner() == 1) {
			ship.getFleetMember().setShipName(NameListUtil.SENSOR_SHADOW);
			//FleetMemberAPI clone = Global.getFactory().createFleetMember(FleetMemberType.SHIP, TW_AURORA_VAR_ID);
			Vector2f loc = MathUtils.getPoint(ship.getLocation(), 4000f, ship.getFacing());
			ShipAPI clone = Global.getCombatEngine().getFleetManager(0).spawnShipOrWing(TW_AURORA_VAR_ID, loc, ship.getFacing(), 0f, null);
			clone.getVariant().removeMod("tw_aurora_asm_handler");
			clone.getFleetMember().setShipName(NameListUtil.TWC_MIDIR);
			clone.getFleetMember().getRepairTracker().setCR(0.75f);
			clone.setRetreating(true, true);
			Global.getSector().getPlayerFleet().getFleetData().addFleetMember(clone.getFleetMember());
			
			Global.getCombatEngine().applyDamage(ship, ship.getLocation(), 100000f, DamageType.ENERGY, 0f, true, false, ship);
			Global.getCombatEngine().removeEntity(ship);
			ship.getVariant().removeMod("tw_aurora_asm_handler");
		}
	}
}