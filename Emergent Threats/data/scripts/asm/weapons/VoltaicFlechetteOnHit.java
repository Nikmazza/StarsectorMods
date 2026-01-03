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

import org.magiclib.util.MagicLensFlare;

public class VoltaicFlechetteOnHit implements OnHitEffectPlugin {

	private static Color CORE_COLOR = new Color(255,255,255,175);
	private static Color FRINGE_COLOR = new Color(200,200,150,175);
	
	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldHit, 
					  ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		
		MagicLensFlare.createSharpFlare(
        engine,
        proj.getSource(),
        point,
		5,				//thickness
        400,			//length
        0,				//angle
        FRINGE_COLOR, 	//fringe
        CORE_COLOR		//core
        );
		
		float dam = 0;
		float emp = proj.getEmpAmount();
		float basePierceChance = 1f; //always pierces if enemy flux is high enough
		EmpArcParams params = new EmpArcParams();
		params.segmentLengthMult = 2f;
		params.zigZagReductionFactor = 0.15f;
		params.fadeOutDist = 500f;
		params.minFadeOutMult = 2f;
		params.flickerRateMult = 0.7f;
		EmpArcEntityAPI arc = null;
		
		boolean isFragmentAugmented = proj.getSource().getVariant().hasHullMod("fragment_swarm");
		boolean isGoodHit = false;
		boolean isPierceHit = false;
		if (!shieldHit && isFragmentAugmented) isGoodHit = true;
		else if (shieldHit && isFragmentAugmented) {
			ShipAPI t = (ShipAPI) target;
			float pierceChance = t.getHardFluxLevel() - 0.1f;
			pierceChance *= t.getMutableStats().getDynamic().getValue(Stats.SHIELD_PIERCED_MULT);
			if ((float) Math.random() < pierceChance * basePierceChance) {
				isGoodHit = true;
				isPierceHit = true;
			}
		}
		if (!isGoodHit) return;
		else if (isPierceHit) arc = (EmpArcEntityAPI) engine.spawnEmpArcPierceShields(proj.getSource(), point, target, target,
						   DamageType.ENERGY, 
						   dam,
						   emp, // emp 
						   100000f, // max range 
						   null,
						   10f, // thickness
						   CORE_COLOR,
						   FRINGE_COLOR,
						   params
						   );
		else arc = (EmpArcEntityAPI) engine.spawnEmpArc(proj.getSource(), point, target, target,
						   DamageType.ENERGY, 
						   dam,
						   emp, // emp 
						   100000f, // max range 
						   null,
						   10f, // thickness
						   CORE_COLOR,
						   FRINGE_COLOR,
						   params
						   );
		arc.setCoreWidthOverride(30f);
		arc.setSingleFlickerMode(true);
	}
}