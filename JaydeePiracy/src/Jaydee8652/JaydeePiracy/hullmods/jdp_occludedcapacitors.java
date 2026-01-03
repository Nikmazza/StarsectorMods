package Jaydee8652.JaydeePiracy.hullmods;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.combat.threat.BaseEnergyLashActivatedSystem;
import com.fs.starfarer.api.impl.combat.threat.FragmentVolleySystemScript;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;

public class jdp_occludedcapacitors extends BaseHullMod {


	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		if (!ship.hasListenerOfClass(jdp_angleRange.class)) ship.addListener(new jdp_angleRange());
	}

	public class jdp_angleRange implements WeaponRangeModifier {
		@Override
		public float getWeaponRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}

		@Override
		public float getWeaponRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
			if (!weapon.getSlot().getWeaponType().equals(WeaponAPI.WeaponType.BALLISTIC) || !weapon.getSlot().getSlotSize().equals(WeaponAPI.WeaponSize.LARGE)) return 1;
			float weaponAngle = weapon.getCurrAngle();
			float shipFacing = ship.getFacing();
			float arcFacing = weapon.getArcFacing();

			float shortestRotation = Math.abs(MathUtils.getShortestRotation((weaponAngle - shipFacing), arcFacing));//MATH HATES HIM!
			return 2 - (shortestRotation / 50f);
		}

		@Override
		public float getWeaponRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
			return 0;
		}
	}
}
