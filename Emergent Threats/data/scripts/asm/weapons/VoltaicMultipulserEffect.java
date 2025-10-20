package data.scripts.asm.weapons;

import java.awt.Color;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EmpArcEntityAPI;
import com.fs.starfarer.api.combat.EmpArcEntityAPI.EmpArcParams;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import data.scripts.vice.util.DistanceUtil;

public class VoltaicMultipulserEffect implements OnFireEffectPlugin, OnHitEffectPlugin {

	private static Color GLOW_COLOR = new Color(255,255,150,175);
	private static float CHAIN_RANGE = 800f;
	private static String SYS_ID = "asm_voltaic_burst";
	
	public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine) {
		ShipAPI ship = proj.getSource();
		
		Vector2f projLoc = MathUtils.getPointOnCircumference(proj.getLocation(), 12f, proj.getWeapon().getCurrAngle());
		
        engine.addHitParticle(
				projLoc,
				ship.getVelocity(),
				40.0f, //size
				1.0f, //brightness
				0.3f, //duration
				GLOW_COLOR);
		
		engine.addHitParticle(
				projLoc,
				ship.getVelocity(),
				25.0f, //size
				1.0f, //brightness
				0.15f, //duration
				Color.white);
	}
	
	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		
		if (!(target instanceof ShipAPI) || ((ShipAPI) target).isHulk()) return;
		
		float dam = proj.getDamageAmount();
		float emp = proj.getEmpAmount();
		ShipAPI ship = proj.getSource();
		ShipAPI t = (ShipAPI) target;
		Vector2f projLoc = MathUtils.getPointOnCircumference(proj.getWeapon().getLocation(), 12f, proj.getWeapon().getCurrAngle());
		
		EmpArcParams params = new EmpArcParams();
		params.segmentLengthMult = 4f;
		params.zigZagReductionFactor = 0.15f;
		params.fadeOutDist = 500f;
		params.minFadeOutMult = 1f;
		params.flickerRateMult = 0.7f;
		EmpArcEntityAPI arc = null;
		
		arc = (EmpArcEntityAPI)engine.spawnEmpArc(
				ship, //damage source
				projLoc, //origin point
				ship, //point anchor
				t, //target
				DamageType.ENERGY,
				dam, //damage
				emp, //emp damage
				3000f, //extra range due to ship geometry
				null, //sound
				50f, //thickness
				GLOW_COLOR, //fringe color
				Color.white, //core color
				params
				);
		arc.setCoreWidthOverride(20f);
		arc.setSingleFlickerMode(true);
		
		//String sysId = ship.getVariant().getHullSpec().getShipDefenseId();
		//if (!sysId.equals(SYS_ID) || t.getOwner() == proj.getSource().getOwner()) return;
		if (!ship.getVariant().hasHullMod("fragment_swarm")) return;
		ShipAPI next = DistanceUtil.getNearestFriend(t, CHAIN_RANGE, true); //nearest friend of enemy target
		
		if (next != null) {
			params.minFadeOutMult = 2f;
			
			EmpArcEntityAPI arc2 = (EmpArcEntityAPI) engine.spawnEmpArc(
				ship, //damage source
				point, //origin point
				ship, //point anchor
				next, //target
				DamageType.ENERGY,
				dam, //damage
				emp, //emp damage
				CHAIN_RANGE + 500f, //extra range due to ship geometry
				null, //sound
				50f, //thickness
				GLOW_COLOR, //fringe color
				Color.white, //core color
				params
				);
			arc2.setCoreWidthOverride(20f);
			arc2.setSingleFlickerMode(true);
		}
	}
}