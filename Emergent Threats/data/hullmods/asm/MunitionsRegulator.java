package data.hullmods.asm;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.IntervalUtil;

public class MunitionsRegulator extends BaseHullMod {
	
	public static String MR_DATA_KEY = "asm_subjugator_reload_key";
	private static float RELOAD_BONUS = 100f;
	private static float RELOAD_TIME = 20f;
	private static float FLUX_PENALTY = 100f;
	private static String CONFLICT_MOD = "magazines";
	
	public static class PeriodicMissileReloadData {
		IntervalUtil interval = new IntervalUtil(RELOAD_TIME, RELOAD_TIME);
	}
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getBallisticAmmoRegenMult().modifyPercent(id, RELOAD_BONUS);
		stats.getEnergyAmmoRegenMult().modifyPercent(id, RELOAD_BONUS);
		ShipVariantAPI var = stats.getVariant();
		var.removeMod(CONFLICT_MOD);
		
		int neutronCount = 0;
		if (var.getWeaponSpec("WS 007") != null && isNeutron(var.getWeaponSpec("WS 007"))) neutronCount++;
		if (var.getWeaponSpec("WS 008") != null && isNeutron(var.getWeaponSpec("WS 008"))) neutronCount++;
		if (var.getWeaponSpec("WS 009") != null && isNeutron(var.getWeaponSpec("WS 009"))) neutronCount++;
		if (var.getWeaponSpec("WS 010") != null && isNeutron(var.getWeaponSpec("WS 010"))) neutronCount++;
		if (var.getWeaponSpec("WS 011") != null && isNeutron(var.getWeaponSpec("WS 011"))) neutronCount++;
		if (var.getWeaponSpec("WS 012") != null && isNeutron(var.getWeaponSpec("WS 012"))) neutronCount++;
		
		float flux = FLUX_PENALTY * neutronCount;
		stats.getFluxDissipation().modifyFlat(id, -flux);
	}
	
	private boolean isNeutron(WeaponSpecAPI spec) {
		return spec.getWeaponId().equals("neutron_torpedo");
	}
	
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);

		if (!ship.isAlive()) return;
		
		CombatEngineAPI engine = Global.getCombatEngine();
		
		String key = MR_DATA_KEY + "_" + ship.getId();
		PeriodicMissileReloadData data = (PeriodicMissileReloadData) engine.getCustomData().get(key);
		if (data == null) {
			data = new PeriodicMissileReloadData();
			engine.getCustomData().put(key, data);
		}
		
		data.interval.advance(amount);
		if (data.interval.intervalElapsed()) {
			for (WeaponAPI w : ship.getAllWeapons()) {
				if (!w.getSpec().getWeaponId().equals("neutron_torpedo")) continue;
				if (w.getAmmo() < w.getMaxAmmo()) {
					int ammo = w.getAmmo() + 1;
					w.setAmmo(ammo);
				}
			}
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) RELOAD_BONUS + "%";
		if (index == 1) return "" + 1;
		if (index == 2) return "" + (int) RELOAD_TIME;
		if (index == 3) return "" + (int) FLUX_PENALTY;
		return null;
	}
}