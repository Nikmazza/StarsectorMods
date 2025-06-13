package data.scripts.vice.weapons;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import org.magiclib.util.MagicRender;

public class WarpTorpedoOnHit implements OnHitEffectPlugin {
	
	private static Color EXPLOSION_COLOR = new Color(70,200,255,200);
	private static float EXTRA_DAMAGE = 400f;
	
	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		
		float angle = proj.getFacing() + 90f;
		Vector2f loc = proj.getLocation();
		//Vector2f velocity = proj.getVelocity();
		
		MagicRender.battlespace(
			Global.getSettings().getSprite("fx", "warp_torp_blast"), //sprite
			loc, 						//loc
			new Vector2f(), 			//vel
			new Vector2f(100,100),		//size
			new Vector2f(800,800), 		//growth
			angle, 						//angle
			0f,							//spin
			EXPLOSION_COLOR,			//color
			true,						//additive
			0f,							//fadein
			0.2f,						//full
			0.3f);						//fadeout
		
		engine.applyDamage(target, point, EXTRA_DAMAGE, DamageType.ENERGY, 0, false, false, proj.getSource());
		engine.addFloatingDamageText(point, EXTRA_DAMAGE, Color.magenta, target, proj.getSource());
		Global.getSoundPlayer().playSound("vice_warp_torpedo_hit", 1f, 1f, point, new Vector2f());
	}
}