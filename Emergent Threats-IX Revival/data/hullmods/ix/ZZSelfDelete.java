package data.hullmods.ix;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;

//for deleting depreciated hullmods from saves for future compatibility
public class ZZSelfDelete extends BaseHullMod {
		
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id)	{
		ShipVariantAPI variant = stats.getVariant();
		variant.getSMods().remove(id);
		variant.getPermaMods().remove(id);
		variant.getHullMods().remove(id);
	}
}