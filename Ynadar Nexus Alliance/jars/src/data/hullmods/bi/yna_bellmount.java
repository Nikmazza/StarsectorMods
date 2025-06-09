package data.hullmods.bi;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class yna_bellmount extends BaseHullMod {

	public static final float COST_REDUCTION  = 10;
	public static final float D_RANGE  = 20;
	public static final float BASE_TURN  = 5;
	
        @Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, -COST_REDUCTION);
		stats.getDynamic().getMod(Stats.LARGE_MISSILE_MOD).modifyFlat(id, -COST_REDUCTION);
		stats.getDynamic().getMod(Stats.LARGE_ENERGY_MOD).modifyFlat(id, -COST_REDUCTION);
                stats.getWeaponTurnRateBonus().modifyFlat(id, BASE_TURN);
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
                ship.addListener(new WeaponRangeModifier() {
			@Override
			public float getWeaponRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
                            if (weapon.getSlot() == null) return 0f;
                            else if (weapon.getSize().equals(WeaponAPI.WeaponSize.LARGE) && !weapon.getType().equals(WeaponAPI.WeaponType.MISSILE)) {
				return D_RANGE / 100f;
                            }
                        	return 0;
			}
                	@Override
			public float getWeaponRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
                        	return 1f;
			}
                        @Override
			public float getWeaponRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
				return 0f;
			}
		});
	}
        
        @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) COST_REDUCTION + "";
		if (index == 1) return "" + (int) D_RANGE + "%";
		//if (index == 2) return "" + (int) BASE_TURN + "";
		return null;
	}

	@Override
	public boolean affectsOPCosts() {
		return true;
	}

}








