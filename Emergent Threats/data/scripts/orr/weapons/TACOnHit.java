package data.scripts.orr.weapons;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.Misc;

import org.magiclib.util.MagicRender;

public class TACOnHit implements OnHitEffectPlugin {
	
	private static float ODDS = 0.20f;
	
	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		
		MagicRender.battlespace(
			Global.getSettings().getSprite("fx", "orr_tac_hit"), //sprite
			point, 						//loc
			new Vector2f(), 			//vel
			new Vector2f(40,40),		//size
			new Vector2f(100,100), 		//growth
			proj.getFacing(), 			//angle
			0f,							//spin
			new Color(255,150,100,255),	//color
			true,						//additive
			0f,							//fadein
			0.4f,						//full
			0.2f);						//fadeout
			
		MagicRender.battlespace(
			Global.getSettings().getSprite("fx", "orr_tac_hit_ring"), //sprite
			point, 						//loc
			new Vector2f(), 			//vel
			new Vector2f(40,40),		//size
			new Vector2f(200,200), 		//growth
			proj.getFacing(), 			//angle
			0f,							//spin
			new Color(255,120,70,255),	//color
			true,						//additive
			0.2f,						//fadein
			0.2f,						//full
			0.3f);						//fadeout
		
		if (!shieldHit && ((float) Math.random() < ODDS)) {
			engine.applyDamage(target, point, proj.getDamageAmount(), DamageType.HIGH_EXPLOSIVE, 0, false, false, proj.getSource());
		}
	}
}