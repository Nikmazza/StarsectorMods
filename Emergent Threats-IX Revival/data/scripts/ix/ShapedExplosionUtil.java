package data.scripts.ix;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual.NEParams;
import com.fs.starfarer.api.util.Misc;

//for creating Dragonfire muzzle smoke and Rift Torpedo explosion visuals
public class ShapedExplosionUtil {

	//for Soliton Burst Emitter (large) and Orthrus (small), has different preset values compared to ET core mod
	public static void spawnShapedExplosion(Vector2f loc, float angle, float shipSpeed, Color pc, boolean isSmall) {
		if (Global.getCombatEngine().getViewport().isNearViewport(loc, 800f)) {
			
			int numParticles = 200;
			float minSize = 20;
			float maxSize = 30;
			
			float minDur = 0.6f;
			float maxDur = 1.0f;
			
			float arc = 120f;
			float scatter = 100f;
			float minVel = 20f + shipSpeed;
			float maxVel = 60f + shipSpeed;
			
			float endSizeMin = 1f;
			float endSizeMax = 2f;
			
			if (isSmall) {
			numParticles = 100;
			minSize = 15;
			maxSize = 20;
			
			arc = 30;
			scatter = 50f;
			minDur = 0.3f;
			maxDur = 0.6f;
			}
			
			Vector2f spawnPoint = new Vector2f(loc);
			for (int i = 0; i < numParticles; i++) {
				float angleOffset = (float) Math.random();
				if (angleOffset > 0.2f) {
					angleOffset *= angleOffset;
				}
				float speedMult = 1f - angleOffset;
				speedMult = 0.5f + speedMult * 0.5f;
				angleOffset *= Math.signum((float) Math.random() - 0.5f);
				angleOffset *= arc/2f;
				float theta = (float) Math.toRadians(angle + angleOffset);
				float r = (float) (Math.random() * Math.random() * scatter);
				float x = (float)Math.cos(theta) * r;
				float y = (float)Math.sin(theta) * r;
				Vector2f pLoc = new Vector2f(spawnPoint.x + x, spawnPoint.y + y);
				
				float speed = minVel + (maxVel - minVel) * (float) Math.random();
				speed *= speedMult;
				
				Vector2f pVel = Misc.getUnitVectorAtDegreeAngle((float) Math.toDegrees(theta));
				pVel.scale(speed);
				
				float pSize = minSize + (maxSize - minSize) * (float) Math.random();
				float pDur = minDur + (maxDur - minDur) * (float) Math.random();
				float endSize = endSizeMin + (endSizeMax - endSizeMin) * (float) Math.random();
				Global.getCombatEngine().addNebulaParticle(pLoc, pVel, pSize, endSize, 0.1f, 0.5f, pDur, pc);
			}
		}
	}
	
	public static NEParams createStandardRiftParams(Color borderColor, Color underColor, float radius) {
		NEParams p = new NEParams();
		p.hitGlowSizeMult = 0.75f;
		p.spawnHitGlowAt = 0f;
		p.noiseMag = 1f;
		p.fadeIn = 0.1f;
		p.underglow = underColor;
		p.withHitGlow = true;
		p.radius = radius;
		p.color = borderColor;
		return p;
	}
	
	public static void spawnStandardRift(Vector2f hitLocation, NEParams p) {
		CombatEngineAPI engine = Global.getCombatEngine();
		CombatEntityAPI prev = null;
		for (int i = 0; i < 2; i++) {
			//NEParams p = params.clone();
			p.radius *= 0.75f + 0.5f * (float) Math.random();
			p.withHitGlow = prev == null;
			Vector2f loc = Misc.getPointAtRadius(hitLocation, p.radius * 0.4f);
			CombatEntityAPI e = engine.addLayeredRenderingPlugin(new NegativeExplosionVisual(p));
			e.getLocation().set(loc);
			
			if (prev != null) {
				float dist = Misc.getDistance(prev.getLocation(), loc);
				Vector2f vel = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(loc, prev.getLocation()));
				vel.scale(dist / (p.fadeIn + p.fadeOut) * 0.7f);
				e.getVelocity().set(vel);
			}
			prev = e;
		}
	}
}
