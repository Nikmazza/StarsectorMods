package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import java.awt.*;

public class ssp_ammofeedjet extends BaseShipSystemScript {
	protected boolean runonce=false;
	protected float Mult=1f;
	public static final float ROF_BONUS = 1.25f;
	Color COLOR = new Color(25, 18, 5,200);
	Color COLOR2 = new Color(255,180,50, 255);
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		runonce=true;
		ShipAPI ship = (ShipAPI) stats.getEntity();
		float HardFluxDissipationFraction=0f;
		float BrustDissipation=1f;
		if(ship.getVariant().hasHullMod("ssp_LongerRange")){ BrustDissipation=0.25f; Mult=2f;}
		if(ship.getVariant().hasHullMod("ssp_ShortRange")){ HardFluxDissipationFraction=1f;}
		stats.getEnergyWeaponDamageMult().modifyMult(id, ROF_BONUS * effectLevel);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, ROF_BONUS * effectLevel);
		stats.getBallisticWeaponDamageMult().modifyMult(id, ROF_BONUS * effectLevel);
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, ROF_BONUS * effectLevel);

		stats.getMaxSpeed().modifyFlat(id, 25f* Mult * effectLevel);
		stats.getAcceleration().modifyPercent(id, 100f * effectLevel);
		stats.getDeceleration().modifyPercent(id, 100f * effectLevel);
		stats.getTurnAcceleration().modifyFlat(id, 10f * effectLevel);
		stats.getTurnAcceleration().modifyPercent(id, 100f * effectLevel);
		stats.getMaxTurnRate().modifyFlat(id, 10f* effectLevel);
		stats.getMaxTurnRate().modifyPercent(id, 50f* effectLevel);

		ship.setJitterUnder(ship, COLOR2, effectLevel, 4, 5f, 8*effectLevel);

		if(state == State.OUT){
			stats.getHardFluxDissipationFraction().modifyFlat(id,HardFluxDissipationFraction);
			stats.getFluxDissipation().modifyMult(id,8f*BrustDissipation);
			if(runonce){
				float times=18;
				for(int o=0;o<times;o++){
					Vector2f Vel = new Vector2f (0,0);
					float angle=o*360/times;
					Global.getCombatEngine().addNebulaParticle(MathUtils.getPoint(ship.getLocation(),ship.getCollisionRadius()*0.75f,angle),Vel.set(VectorUtils.rotate(new Vector2f(200f, 0f), angle)),ship.getCollisionRadius()*0.5f,2f,0.2f,0.6f,0.5f,COLOR);
				}
				runonce=false;
			}
		}
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getEnergyWeaponDamageMult().unmodify(id);
		stats.getEnergyWeaponFluxCostMod().unmodify(id);
		stats.getBallisticWeaponDamageMult().unmodify(id);
		stats.getBallisticWeaponFluxCostMod().unmodify(id);
		stats.getHardFluxDissipationFraction().unmodify(id);
		stats.getFluxDissipation().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float a =25f* effectLevel*Mult;
		float b =25f* effectLevel;
		if (index == 0) {
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_ammofeedjet0") +(int)a, false);
		} else if (index == 1) {
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_ammofeedjet1")+(int)b+"%", false);
		}
			return null;
	}
	@Override
	public float getInOverride(ShipAPI ship) {
		if (ship != null && ship.getVariant().hasHullMod("ssp_LongerRange")) {
			return 0.5f;
		}
		return -1;
	}

	@Override
	public float getActiveOverride(ShipAPI ship) {
		if (ship != null && ship.getVariant().hasHullMod("ssp_LongerRange")) {
			return 7.5f;
		}
		return -1;
	}
}
