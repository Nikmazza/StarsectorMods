package data.scripts.asm.weapons;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EmpArcEntityAPI;
import com.fs.starfarer.api.combat.EmpArcEntityAPI.EmpArcParams;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class VoltaicFlechetteOnHit implements OnHitEffectPlugin {

	public static Color color = new Color(200,200,150,255);
	private static String SYS_ID = "asm_voltaic_burst";
	
	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		boolean isImproved = projectile.getSource().getVariant().hasHullMod("fragment_swarm");
		//isImproved = projectile.getSource().getVariant().getHullSpec().getShipDefenseId().equals(SYS_ID);
		float odds = 0.2f;
		if ((!isImproved && !shieldHit && (float) Math.random() < odds) || (isImproved && (float) Math.random() < odds)) {
			if (isImproved && shieldHit) {
				ShipAPI t = (ShipAPI) target;
				float pierceChance = t.getHardFluxLevel() - 0.1f;
				pierceChance *= t.getMutableStats().getDynamic().getValue(Stats.SHIELD_PIERCED_MULT);
				if ((float) Math.random() < pierceChance) return;
			}
			
			float dam = 50f;
			float emp = 200f;
			EmpArcParams params = new EmpArcParams();
			params.segmentLengthMult = 2f;
			params.zigZagReductionFactor = 0.15f;
			params.fadeOutDist = 500f;
			params.minFadeOutMult = 2f;
			params.flickerRateMult = 0.7f;
			EmpArcEntityAPI arc = null;
			
			if (!isImproved) arc = (EmpArcEntityAPI) engine.spawnEmpArc(projectile.getSource(), point, target, target,
							   DamageType.ENERGY, 
							   dam,
							   emp, // emp 
							   100000f, // max range 
							   "tachyon_lance_emp_impact",
							   20f, // thickness
							   color,
							   Color.white,
							   params
							   );
			
			else arc = (EmpArcEntityAPI) engine.spawnEmpArcPierceShields(projectile.getSource(), point, target, target,
							   DamageType.ENERGY, 
							   dam,
							   emp, // emp 
							   100000f, // max range 
							   "tachyon_lance_emp_impact",
							   20f, // thickness
							   color,
							   Color.white,
							   params
							   );
			
			arc.setCoreWidthOverride(30f);
			arc.setSingleFlickerMode(true);
		}
	}
}