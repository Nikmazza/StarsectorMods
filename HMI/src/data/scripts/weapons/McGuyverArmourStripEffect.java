package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.DisintegratorEffect;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.IntervalUtil;

public class McGuyverArmourStripEffect implements BeamEffectPlugin {

	private IntervalUtil fireInterval = new IntervalUtil(0.25f, 1.75f);
	private boolean wasZero = true;
	public static float DAMAGE = 40;
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		CombatEntityAPI target = beam.getDamageTarget();
		if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
			float dur = beam.getDamage().getDpsDuration();
			boolean hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getTo());
			// needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
			if (!wasZero) dur = 0;
			wasZero = beam.getDamage().getDpsDuration() <= 0;
			fireInterval.advance(dur);

			if (fireInterval.intervalElapsed() && !hitShield && target instanceof ShipAPI) {
				float FLUX_LEVEL = (beam.getSource().getFluxTracker().getFluxLevel())* 0.5f + 1f;
				float DAMAGE = beam.getDamage().getBaseDamage();

				if (beam.getDamageTarget() != null){
					Vector2f point = beam.getRayEndPrevFrame();
					StripArmour(beam, (ShipAPI) target, point);;
				}
			}
		}
//			Global.getSoundPlayer().playLoop("system_emp_emitter_loop", 
//											 beam.getDamageTarget(), 1.5f, beam.getBrightness() * 0.5f,
//											 beam.getTo(), new Vector2f());
	}

	public static void StripArmour(BeamAPI beam, ShipAPI target, Vector2f point) {
		CombatEngineAPI engine = Global.getCombatEngine();

		ArmorGridAPI grid = target.getArmorGrid();
		int[] cell = grid.getCellAtLocation(point);
		if (cell == null) return;

		int gridWidth = grid.getGrid().length;
		int gridHeight = grid.getGrid()[0].length;

		float damageTypeMult = DisintegratorEffect.getDamageTypeMult(beam.getSource(), target);

		float damageDealt = 0f;
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				if ((i == 2 || i == -2) && (j == 2 || j == -2)) continue; // skip corners

				int cx = cell[0] + i;
				int cy = cell[1] + j;

				if (cx < 0 || cx >= gridWidth || cy < 0 || cy >= gridHeight) continue;

				float damMult = 1/30f;
				if (i == 0 && j == 0) {
					damMult = 1/15f;
				} else if (i <= 1 && i >= -1 && j <= 1 && j >= -1) { // S hits
					damMult = 1/15f;
				} else { // T hits
					damMult = 1/30f;
				}

				float armorInCell = grid.getArmorValue(cx, cy);
				float damage = DAMAGE * damMult * damageTypeMult;
				damage = Math.min(damage, armorInCell);
				if (damage <= 0) continue;

				target.getArmorGrid().setArmorValue(cx, cy, Math.max(0, armorInCell - damage));
				damageDealt += damage;
			}
		}

		if (damageDealt > 0) {
			if (Misc.shouldShowDamageFloaty(beam.getSource(), target)) {
				engine.addFloatingDamageText(point, damageDealt, Misc.FLOATY_ARMOR_DAMAGE_COLOR, target, beam.getSource());
			}
			target.syncWithArmorGridState();
		}
	}
}
