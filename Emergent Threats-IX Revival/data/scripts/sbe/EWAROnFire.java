package data.scripts.sbe;

import java.awt.Color;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

import data.scripts.ix.util.DistanceUtil;

public class EWAROnFire implements BeamEffectPlugin {

	private boolean appliedRift = false;
	private boolean appliedFlash = false;
	private static Color glow = new Color(100,100,255,255);
	private static IntervalUtil tracker = new IntervalUtil(0.7f, 0.7f);
	private static float emitterRange = 1200f; //slightly longer than default 1000 range due to ship sizes
	private static float emitterRangeFrig = 1000f;
	private static float damage = 500f;
	private static float damageFrig = 400f;
	private static float empRadius = 400f;
	private static float empRadiusFrig = 300f;
	private static String FRIG_HULLMOD = "tw_point_defense_small";
	//sbe_ix_rift
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		if (appliedRift) return;
		tracker.advance(amount);
		
		//on fire lightning prong effect, removed for now
		//if (!appliedFlash) { appliedFlash = true; }
		
		//pulse spawns 150 su away from nearest hostile missile/ship, then blasts enemies within empRadius 500 su
		//if closest target is within 300 su, main pulse originates from EWAR prongs instead of extending
		if (tracker.intervalElapsed() && !appliedRift) {
			ShipAPI ship = beam.getSource();
			boolean isFrig = ship.getVariant().hasHullMod(FRIG_HULLMOD);
			float range = isFrig ? emitterRangeFrig : emitterRange;
			CombatEntityAPI e = DistanceUtil.getNearestEnemyMissileOrShip(ship, range);
			if (e == null) appliedRift = true;
			else {
				float angle = ship.getFacing();
				float angleTo = VectorUtils.getAngle(e.getLocation(), ship.getLocation());
				Vector2f origin = MathUtils.getPointOnCircumference(beam.getFrom(), 20f, angle);
				Vector2f burstLoc = null;
				if (DistanceUtil.getDistance(e.getLocation(), origin) < 300f) burstLoc = origin;
				else burstLoc = MathUtils.getPointOnCircumference(e.getLocation(), 150f, angleTo);
				
				for (int i = 0; i < 2; i++) {
					engine.spawnEmpArcVisual(
							origin, beam.getSource(), burstLoc, null,
							20f, //thickness
							glow, //fringe color
							Color.white //core color
							);
				}
				float blastRadius = isFrig ? empRadiusFrig : empRadius;
				List <CombatEntityAPI> enemies = DistanceUtil.getAllEnemiesNearPoint(burstLoc, empRadius, ship);
				float empDamage = isFrig ? damageFrig : damage;
				for (CombatEntityAPI e : enemies) {
					engine.spawnEmpArc(
							ship, //damage source
							burstLoc, //from
							null, //from anchor
							e, //target
							DamageType.ENERGY, //damagetype
							empDamage, //damage
							empDamage, //emp damage
							100000f, //max range
							"ewar_ix_fire", //sound id
							20f, //thickness
							glow, //fringe color
							Color.white //core color
							);							
				}
				appliedRift = true;
			}
		}
	}
}