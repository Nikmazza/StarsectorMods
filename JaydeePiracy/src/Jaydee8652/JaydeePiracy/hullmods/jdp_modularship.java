package Jaydee8652.JaydeePiracy.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicIncompatibleHullmods;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class jdp_modularship extends BaseHullMod {

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//Does nothing but prevent installing a makeshift shield, mostly a backup incase other
		//preventative measures break

		ShipAPI ship = (ShipAPI) stats.getEntity();

		//Incompatibilities
		if(stats.getVariant().getHullMods().contains("frontshield")){
			//if someone tries to install frontshield (makeshift shields), remove it
			MagicIncompatibleHullmods.removeHullmodWithWarning(
					stats.getVariant(),
					"frontshield",
					"jdp_modularship"
			);
		}
	}
}
