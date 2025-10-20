package data.hullmods.tw;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

//dummy hullmod, stats changes handled by TrinityRetrofit
public class EnhancedControlNode extends BaseHullMod {
	
	private static String RADIANT_MOD = "ix_converted_hull";
	private static String NIMBUS_NAME = "Nimbus (TW)";
	
	@Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
    }
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

	}
	
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		if (ship.getVariant().hasHullMod(RADIANT_MOD)) {
			String s1 = "This ship can use the Activate Starquake Drones hullmod to equip two %s drones in place of standard Nimbus drones, while also disabling the rest of its hangars.";
			String s2 = "Starquake (TW)";
			tooltip.addPara(s1, 10f, Misc.getHighlightColor(), s2);
		}
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return NIMBUS_NAME;
		return null;
	}
}