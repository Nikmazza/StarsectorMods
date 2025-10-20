package data.hullmods.ix;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.util.IntervalUtil;

//handles hullmod switching and system selection
public class AquilonMissileMicroforge extends BaseHullMod {
	
	public static String MR_DATA_KEY = "aquilon_ix_reload_data_key";
	private static float RELOAD_TIME = 40f;
	private static float RELOAD_PERCENT = 50f;
	private static String CONFLICT_MOD_1 = "missleracks";
	private static String CONFLICT_MOD_2 = "missile_autoloader";
	private static String TW_MOD = "tw_trinity_retrofit";
	
	private static String AQUILON_FL = "ix_aquilon_fl";
	private static String AQUILON_FLH = "ix_aquilon_fl_handler";
	private static String AQUILON_FL_SYSTEM = "ix_flare_launcher";
	private static String AQUILON_SR = "ix_aquilon_sr";
	private static String AQUILON_SRH = "ix_aquilon_sr_handler";
	private static String AQUILON_SR_SYSTEM = "ix_starfall_rockets";
	private static String AQUILON_SS = "ix_aquilon_ss";
	private static String AQUILON_SSH = "ix_aquilon_ss_handler";
	private static String AQUILON_SS_SYSTEM = "ix_stormwall_salvo";
	private static String AQUILON_SC = "ix_aquilon_sc";
	private static String AQUILON_SCH = "ix_aquilon_sc_handler";
	private static String AQUILON_SC_SYSTEM = "ix_spatial_charges";	
	
	public static class AquilonMissileReloadData {
		IntervalUtil interval = new IntervalUtil(RELOAD_TIME, RELOAD_TIME);
	}
	
	//also handles ship system swapping
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		ShipVariantAPI variant = stats.getVariant();
		//if ship has no system, give spacial charges by default
		if (!variant.hasHullMod(AQUILON_FL) && !variant.hasHullMod(AQUILON_FLH)
				&& !variant.hasHullMod(AQUILON_SR) && !variant.hasHullMod(AQUILON_SRH)
				&& !variant.hasHullMod(AQUILON_SS) && !variant.hasHullMod(AQUILON_SSH)
				&& !variant.hasHullMod(AQUILON_SC) && !variant.hasHullMod(AQUILON_SCH)) {
			variant.addMod(AQUILON_SC);
			variant.addMod(AQUILON_SCH);
		}
		//if TW ship has Starfall Rockets, replace with Stormwall Salvo
		if (variant.hasHullMod(TW_MOD) && variant.hasHullMod(AQUILON_SR)) {
			variant.getHullMods().remove(AQUILON_SRH);
			variant.getHullMods().remove(AQUILON_SR);
			variant.addMod(AQUILON_SS);
			variant.addMod(AQUILON_SSH);
		}
		//if IX ship has Stormwall Salvo, replace with Starfall Rockets
		else if (!variant.hasHullMod(TW_MOD) && variant.hasHullMod(AQUILON_SS)) {
			variant.getHullMods().remove(AQUILON_SSH);
			variant.getHullMods().remove(AQUILON_SS);
			variant.addMod(AQUILON_SR);
			variant.addMod(AQUILON_SRH);
		}
		
		//FL -> SR(IX)/SS(TW) -> SC
		if (variant.hasHullMod(AQUILON_SCH) && !variant.hasHullMod(AQUILON_SC)) {
			variant.getHullMods().remove(AQUILON_SCH);
			variant.addMod(AQUILON_FLH);
			variant.addMod(AQUILON_FL);
		}
		
		//TW gets Stormwall Salvo, IX get Starfall Rocket
		if (variant.hasHullMod(AQUILON_FLH) && !variant.hasHullMod(AQUILON_FL)) {
			variant.getHullMods().remove(AQUILON_FLH);
			if (variant.hasHullMod(TW_MOD)) {
				variant.addMod(AQUILON_SSH);
				variant.addMod(AQUILON_SS);
			}
			else {
				variant.addMod(AQUILON_SRH);
				variant.addMod(AQUILON_SR);
			}
		}
		
		//Stormwall Salvo leads to Spacial Charges
		if (variant.hasHullMod(AQUILON_SSH) && !variant.hasHullMod(AQUILON_SS)) {
			variant.getHullMods().remove(AQUILON_SSH);
			variant.addMod(AQUILON_SCH);
			variant.addMod(AQUILON_SC);
		}
		//Starfall Rockets leads to Spacial Charges
		if (variant.hasHullMod(AQUILON_SRH) && !variant.hasHullMod(AQUILON_SR)) {
			variant.getHullMods().remove(AQUILON_SRH);
			variant.addMod(AQUILON_SCH);
			variant.addMod(AQUILON_SC);
		}
		
		if (variant.hasHullMod(AQUILON_FL)) variant.getHullSpec().setShipSystemId(AQUILON_FL_SYSTEM);
		else if (variant.hasHullMod(AQUILON_SR)) variant.getHullSpec().setShipSystemId(AQUILON_SR_SYSTEM);
		else if (variant.hasHullMod(AQUILON_SS)) variant.getHullSpec().setShipSystemId(AQUILON_SS_SYSTEM);
		else if (variant.hasHullMod(AQUILON_SC)) variant.getHullSpec().setShipSystemId(AQUILON_SC_SYSTEM);
		
		variant.removeMod(CONFLICT_MOD_1);
		variant.removeMod(CONFLICT_MOD_2);
	}
	
	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);

		if (!ship.isAlive()) return;
		
		CombatEngineAPI engine = Global.getCombatEngine();
		
		String key = MR_DATA_KEY + "_" + ship.getId();
		AquilonMissileReloadData data = (AquilonMissileReloadData) engine.getCustomData().get(key);
		if (data == null) {
			data = new AquilonMissileReloadData();
			engine.getCustomData().put(key, data);
		}
		
		data.interval.advance(amount);
		if (data.interval.intervalElapsed()) {
			for (WeaponAPI w : ship.getAllWeapons()) {
				if (w.getSize() != WeaponSize.MEDIUM || w.getType() != WeaponType.MISSILE) continue;
				if (w.usesAmmo() && w.getAmmo() < w.getMaxAmmo()) {
					int extraAmmo = 1;
					if (w.getMaxAmmo() * RELOAD_PERCENT * 0.01f > 1f) {
						extraAmmo = (int) (w.getMaxAmmo() * RELOAD_PERCENT * 0.01f);
					}
					int ammo = w.getAmmo() + extraAmmo;
					if (w.getMaxAmmo() == 1) w.setAmmo(1);
					else if (ammo <= w.getMaxAmmo()) w.setAmmo(ammo);
					else w.setAmmo(w.getMaxAmmo());
				}
			}
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) RELOAD_PERCENT + "%";
		if (index == 1) return "" + (int) RELOAD_TIME;
		if (index == 2) return "" + 1;
		return null;
	}
}