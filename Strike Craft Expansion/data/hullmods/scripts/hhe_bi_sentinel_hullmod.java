package data.hullmods.scripts;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

import java.util.HashSet;
import java.util.Set;

public class hhe_bi_sentinel_hullmod extends BaseHullMod {

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<String>(4);

	static final float HHE_SENTINEL_FIGHTER_REFIT_RATE = 15f;
	static final float HHE_SENTINEL_FIGHTER_REPLACEMENT_RATE = 30f;
	//static final float HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE = 10f;
	
	static final float HHE_SENTINEL_TURRET_SPEED_DECREASE = 25f;
	static final float HHE_SENTINEL_BOMBER_COST_PENALTY = 1.5f;
	//static final float HHE_SENTINEL_SUPPORT_COST_PENALTY = 1000f;
	
	static {
		BLOCKED_HULLMODS.add("expanded_deck_crew");
		BLOCKED_HULLMODS.add("expanded_cargo_holds");
		BLOCKED_HULLMODS.add("auxiliary_fuel_tanks");
		BLOCKED_HULLMODS.add("additional_berthing");
	}

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_DECREASE_MULT).modifyMult(id, 1f - HHE_SENTINEL_FIGHTER_REFIT_RATE / 100f);
		stats.getDynamic().getStat(Stats.REPLACEMENT_RATE_INCREASE_MULT).modifyPercent(id, HHE_SENTINEL_FIGHTER_REPLACEMENT_RATE);
		
		//stats.getWeaponTurnRateBonus().modifyMult(id, 1f - HHE_SENTINEL_TURRET_SPEED_DECREASE * 0.01f);
		//stats.getBeamWeaponTurnRateBonus().modifyMult(id, 1f - HHE_SENTINEL_TURRET_SPEED_DECREASE * 0.01f);
		
		stats.getDynamic().getMod(Stats.BOMBER_COST_MOD).modifyMult(id, HHE_SENTINEL_BOMBER_COST_PENALTY);
		//stats.getDynamic().getMod(Stats.SUPPORT_COST_MOD).modifyFlat(id, HHE_SENTINEL_SUPPORT_COST_PENALTY);
	}
	//public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		//fighter.getMutableStats().getEnergyDamageTakenMult().modifyMult(id, 1f - HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE * 0.01f);
		//fighter.getMutableStats().getFragmentationDamageTakenMult().modifyMult(id, 1f - HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE * 0.01f);
		//fighter.getMutableStats().getHighExplosiveDamageTakenMult().modifyMult(id, 1f - HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE * 0.01f);
		//fighter.getMutableStats().getKineticDamageTakenMult().modifyMult(id, 1f - HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE * 0.01f);
		
		//fighter.getMutableStats().getEnergyShieldDamageTakenMult().modifyMult(id, 1f - HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE * 0.01f);
		//fighter.getMutableStats().getFragmentationShieldDamageTakenMult().modifyMult(id, 1f - HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE * 0.01f);
		//fighter.getMutableStats().getHighExplosiveShieldDamageTakenMult().modifyMult(id, 1f - HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE * 0.01f);
		//fighter.getMutableStats().getKineticShieldDamageTakenMult().modifyMult(id, 1f - HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE * 0.01f);
	//}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Bomber ordnance point requirements are increased by 50" + "%";
		//if (index == 1) return "" + (int) HHE_SENTINEL_TURRET_SPEED_DECREASE + "%";
		if (index == 1) return "" + (int) HHE_SENTINEL_FIGHTER_REFIT_RATE + "%";
		if (index == 2) return "" + (int) HHE_SENTINEL_FIGHTER_REPLACEMENT_RATE + "%";
		//if (index == 4) return "" + (int) HHE_SENTINEL_FIGHTER_DURABILITY_INCREASE + "%";
		if (index == 3) return " \n ";
		if (index == 4) return " • Expanded Deck Crew \n   • Expanded Cargo Holds \n   • Auxiliary Fuel Tanks\n   • Additional Berthing";
		return null;
	}
	
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                ship.getVariant().removeMod(tmp);
            }
        }
    }
	
	@Override
	public boolean affectsOPCosts() {
		return true;
	}
}
