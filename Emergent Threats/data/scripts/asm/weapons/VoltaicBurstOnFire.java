package data.scripts.asm.weapons;

import java.awt.Color;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.EmpArcEntityAPI;
import com.fs.starfarer.api.combat.EmpArcEntityAPI.EmpArcParams;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.threat.RoilingSwarmEffect;

import org.magiclib.util.MagicLensFlare;
import data.scripts.vice.util.DistanceUtil;

public class VoltaicBurstOnFire implements OnFireEffectPlugin {

	private static int BASE_BOLT_COUNT = 3;
	private static float BURST_RANGE = 1000f;
	private static float FRAGMENTS_PER_BURST = 20f;
	private static float DAMAGE = 500f;
	private static float EMP = 500f;
	private static Color GLOW_COLOR = new Color(255,255,150,175);
	
	public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine) {
		ShipAPI ship = weapon.getShip();
		if (ship == null) return;
		
		Vector2f weaponLoc = weapon.getLocation();
		
		MagicLensFlare.createSharpFlare(
				engine,
				ship,
				weaponLoc,
				10,		//thickness
				300,	//length
				0,		//angle
				GLOW_COLOR,	//fringe
				Color.white	//core
		);
		
		int boltCount = BASE_BOLT_COUNT;
		RoilingSwarmEffect swarm = RoilingSwarmEffect.getSwarmFor(ship);
		int swarmCount = swarm == null ? 0 : swarm.getNumActiveMembers();
		if (swarmCount > 0) boltCount += (int) (swarmCount / FRAGMENTS_PER_BURST);
		
		List<CombatEntityAPI> enemies = DistanceUtil.getAllEnemiesInRange(weaponLoc, BURST_RANGE, ship);
		float enemyCount = enemies.size();
		if (enemyCount == 0) return;
		
		EmpArcParams params = new EmpArcParams();
		params.segmentLengthMult = 2f;
		params.zigZagReductionFactor = 0.15f;
		params.fadeOutDist = 500f;
		params.minFadeOutMult = 2f;
		params.flickerRateMult = 0.7f;
		
		if (enemyCount == 1) {
			EmpArcEntityAPI arc = (EmpArcEntityAPI) engine.spawnEmpArc(
					ship, //damage source
					weaponLoc, //origin point
					ship, //point anchor
					(CombatEntityAPI) enemies.get(0), //target
					DamageType.ENERGY,
					DAMAGE, //damage
					EMP, //emp damage
					BURST_RANGE + 500f, //extra range due to ship geometry
					null, //sound
					30f, //thickness
					GLOW_COLOR, //fringe color
					Color.white, //core color
					params
					);
			arc.setCoreWidthOverride(50f);
			arc.setSingleFlickerMode(true);
		}
		
		else if (enemyCount > 1) {
			float count = boltCount;
			if (boltCount > enemyCount) count = enemyCount;
			for (int i = 0; i < count; i++) {
				EmpArcEntityAPI arc = (EmpArcEntityAPI) engine.spawnEmpArc(
						ship, //damage source
						weaponLoc, //origin point
						ship, //point anchor
						(CombatEntityAPI) enemies.get(i), //target
						DamageType.ENERGY,
						DAMAGE, //damage
						EMP, //emp damage
						BURST_RANGE + 500f, //extra range due to ship geometry
						null, //sound
						30f, //thickness
						GLOW_COLOR, //fringe color
						Color.white, //core color
						params
						);
				arc.setCoreWidthOverride(50f);
				arc.setSingleFlickerMode(true);
			}
		}
		
		if (boltCount > enemyCount) {
			float count = enemyCount;
			float leftoverCount = boltCount - enemyCount;
			if (leftoverCount < enemyCount) count = leftoverCount;
			for (int i = 0; i < count; i++) {
				EmpArcEntityAPI arc = (EmpArcEntityAPI) engine.spawnEmpArc(
						ship, //damage source
						weaponLoc, //origin point
						ship, //point anchor
						(CombatEntityAPI) enemies.get(i), //target
						DamageType.ENERGY,
						DAMAGE, //damage
						EMP, //emp damage
						BURST_RANGE + 500f, //extra range due to ship geometry
						null, //sound
						30f, //thickness
						GLOW_COLOR, //fringe color
						Color.white, //core color
						params
						);
				arc.setCoreWidthOverride(50f);
				arc.setSingleFlickerMode(true);
			}
		}
	}
}