package data.shipsystems.scripts;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class yna_HardenerFieldStats extends BaseShipSystemScript {

	private static final Map mag = new HashMap();
	private static final Map mag2 = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0.5f);
		mag.put(HullSize.FRIGATE, 0.5f);
		mag.put(HullSize.DESTROYER, 0.5f);
		mag.put(HullSize.CRUISER, 0.5f);
		mag.put(HullSize.CAPITAL_SHIP, 0.5f);
                
		mag2.put(HullSize.FIGHTER, 500f);
		mag2.put(HullSize.FRIGATE, 500f);
		mag2.put(HullSize.DESTROYER, 650f);
		mag2.put(HullSize.CRUISER, 800f);
		mag2.put(HullSize.CAPITAL_SHIP, 1000f);
	}
	private static final float FLUX_DIS = 200f;
        
	private static final float WEP_RED = 67f;
        
	protected Object STATUSKEY1 = new Object();
	protected Object STATUSKEY2 = new Object();
	protected Object STATUSKEY3 = new Object();
	
	//public static final float INCOMING_DAMAGE_MULT = 0.25f;
	//public static final float INCOMING_DAMAGE_CAPITAL = 0.5f;
	
        @Override
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		effectLevel = 1f;
		
		ShipAPI ship = null;
		boolean player = false;
		float mult = (Float) mag.get(HullSize.CRUISER);
		float mult2 = (Float) mag2.get(ShipAPI.HullSize.CRUISER);
		float mult3 = (Float) WEP_RED / 100f;
		float mult4 = (Float) FLUX_DIS / 100f;
		if (stats.getVariant() != null) {
			mult = (Float) mag.get(stats.getVariant().getHullSize());
			mult2 = (Float) mag2.get(stats.getVariant().getHullSize());
		}
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}
                
		if (state == State.IN) {
                    stats.getHullDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
                    stats.getArmorDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
                    stats.getEmpDamageTakenMult().modifyMult(id, 1f - (1f - mult) * effectLevel);
                    stats.getEffectiveArmorBonus().modifyFlat(id, mult2 * effectLevel);
                    stats.getFluxDissipation().modifyFlat(id, 1f + (mult4 * effectLevel));
                }
                if (state == State.ACTIVE) {
                    stats.getEnergyRoFMult().modifyMult(id, 1f - mult3 * effectLevel);
                    stats.getBallisticRoFMult().modifyMult(id, 1f - mult3 * effectLevel);
                    stats.getMissileRoFMult().modifyMult(id, 1f - mult3 * effectLevel);
                    stats.getBeamWeaponDamageMult().modifyMult(id, 1f - mult3 * effectLevel);
                    stats.getBeamWeaponFluxCostMult().modifyMult(id, 1f - mult3 * effectLevel);
                }
		if (state == State.OUT) {
                    stats.getHullDamageTakenMult().unmodify(id);
                    stats.getArmorDamageTakenMult().unmodify(id);
                    stats.getEmpDamageTakenMult().unmodify(id);
                    stats.getEffectiveArmorBonus().unmodify(id);
                    stats.getFluxDissipation().unmodify(id);
                    stats.getEnergyRoFMult().unmodify(id);
                    stats.getBallisticRoFMult().unmodify(id);
                    stats.getMissileRoFMult().unmodify(id);
                    stats.getBeamWeaponDamageMult().unmodify(id);
                    stats.getBeamWeaponFluxCostMult().unmodify(id);
                }
                
		if (player) {
			ShipSystemAPI system = yna_getHardener(ship);
			if (system != null) {
				float percent = (1f - mult) * effectLevel * 100;
				float percent2 = mult4 * effectLevel * 100;
				Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
					system.getSpecAPI().getIconSpriteName(), system.getDisplayName(),
					(int) Math.round(percent) + "% less damage taken", false);
				Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
					system.getSpecAPI().getIconSpriteName(), system.getDisplayName(),
					"+" + (int) Math.round(mult2 * effectLevel) + " static Armor", false);
				Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY3,
					system.getSpecAPI().getIconSpriteName(), system.getDisplayName(),
					"+" + (int) Math.round(percent2) + "% Flux dissipation", false);
			}
		}
	}
	
	public static ShipSystemAPI yna_getHardener(ShipAPI ship) {
		ShipSystemAPI system = ship.getPhaseCloak();
		if (system != null && system.getId().equals("yna_hardener")) return system;
		if (system != null && system.getSpecAPI() != null && system.getSpecAPI().hasTag(Tags.SYSTEM_USES_DAMPER_FIELD_AI)) return system;
		return ship.getSystem();
	}
	
        @Override
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getEmpDamageTakenMult().unmodify(id);
		stats.getEffectiveArmorBonus().unmodify(id);
		stats.getFluxDissipation().unmodify(id);
		stats.getEnergyRoFMult().unmodify(id);
		stats.getBallisticRoFMult().unmodify(id);
		stats.getMissileRoFMult().unmodify(id);
		stats.getBeamWeaponDamageMult().unmodify(id);
		stats.getBeamWeaponFluxCostMult().unmodify(id);
	}
	
}
