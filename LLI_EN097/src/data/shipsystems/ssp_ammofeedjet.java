package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.SSPI18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import java.awt.*;
import java.util.Random;

public class ssp_ammofeedjet extends BaseShipSystemScript {
	protected boolean runonce=false;
	protected boolean runonce0=false;
	protected float number=0f;
	protected float Mult=1f;
	float times=30;
	public static final float ROF_BONUS = 0.5f;
	Color COLOR = new Color(100, 75, 20,100);
	Color COLOR2 = new Color(255,180,50, 255);
	Color COLOR3 = new Color(110, 85, 90, 60);
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		runonce=true;
		ShipAPI ship = (ShipAPI) stats.getEntity();
		float HardFluxDissipationFraction=0f;
		float BrustDissipation=1f;
		if(ship.getVariant().hasHullMod("ssp_SYS_p")){ BrustDissipation=0.25f; Mult=2f;}
		if(ship.getVariant().hasHullMod("ssp_ShortRange")){ HardFluxDissipationFraction=1f;}
		stats.getEnergyWeaponDamageMult().modifyMult(id, 1+ROF_BONUS * effectLevel);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1+ROF_BONUS * effectLevel);
		stats.getBallisticWeaponDamageMult().modifyMult(id, 1+ROF_BONUS * effectLevel);
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1+ROF_BONUS * effectLevel);

		stats.getMaxSpeed().modifyFlat(id, 25f* Mult * effectLevel);
		stats.getAcceleration().modifyPercent(id, 100f * effectLevel);
		stats.getDeceleration().modifyPercent(id, 100f * effectLevel);
		stats.getTurnAcceleration().modifyFlat(id, 10f * effectLevel);
		stats.getTurnAcceleration().modifyPercent(id, 100f * effectLevel);
		stats.getMaxTurnRate().modifyFlat(id, 10f* effectLevel);
		stats.getMaxTurnRate().modifyPercent(id, 50f* effectLevel);

		ship.setJitterShields(false);
		ship.setJitterUnder(ship, COLOR2, 1, 4, 3f, 4* effectLevel);

		if(state == State.OUT){
			stats.getHardFluxDissipationFraction().modifyFlat(id,HardFluxDissipationFraction);
			stats.getFluxDissipation().modifyMult(id,8f*BrustDissipation);
			//让AI也会速v小技巧
			if(effectLevel<1
					&& !runonce0 &&(
						ship!=Global.getCombatEngine().getPlayerShip()||
						(ship==Global.getCombatEngine().getPlayerShip()&&Global.getCombatEngine().getCombatUI().isAutopilotOn()))){
				if(ship.getCaptain()!=null && ship.getCaptain().getStats().getLevel()*0.125f>=Math.random()){
					ship.giveCommand(ShipCommand.VENT_FLUX,0,0);
				}
				runonce0=true;
			}
			if(1-effectLevel>number/times) {
				Vector2f Vel = new Vector2f(0, 0);
				float angle = MathUtils.getRandomNumberInRange(0, 360);
				Global.getCombatEngine().addNebulaSmoothParticle(MathUtils.getPoint(ship.getLocation(), ship.getCollisionRadius() * 0.75f, angle), Vel.set(VectorUtils.rotate(new Vector2f(200f, 0f), angle)), ship.getCollisionRadius() * 0.5f, 0.5f, 0.2f, 0.6f, 0.5f, COLOR);
				Global.getCombatEngine().addNebulaParticle(MathUtils.getPoint(ship.getLocation(), ship.getCollisionRadius() * 0.55f, angle), Vel.set(VectorUtils.rotate(new Vector2f(170f, 0f), angle)), ship.getCollisionRadius() * 0.3f, 0.5f, 0.1f, 0.4f, 0.8f, COLOR3);
				number++;
			}
		}
	}

	public void unapply(MutableShipStatsAPI stats, String id) {
		number=0f;
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
		float a =50f* effectLevel*Mult;
		float b =50f* effectLevel;
		if (index == 0) {
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_ammofeedjet0") +(int)a, false);
		} else if (index == 1) {
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_ammofeedjet1")+(int)b+"%", false);
		}
			return null;
	}
	@Override
	public float getInOverride(ShipAPI ship) {
		if (ship != null && ship.getVariant().hasHullMod("ssp_SYS_p")) {
			return 0.5f;
		}
		return -1;
	}

	@Override
	public float getActiveOverride(ShipAPI ship) {
		if (ship != null && ship.getVariant().hasHullMod("ssp_SYS_p")) {
			return 7.5f;
		}
		return -1;
	}
}
