package data.scripts.ix.weapons;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class LaceratorOnArmorHitEffect implements BeamEffectPlugin {

	private IntervalUtil fireInterval = new IntervalUtil(0.20f, 0.20f);

	private boolean wasZero = true;
	private static float HP_LOSS_BASE = 40; // x5 per second
	private static float HP_RATIO_THRESHOLD = 0.5f; // if generator is present then HP drain is minimum 100/s
	private static String REACTOR_MOD = "ix_dawnstar_lacerator";
	
	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		ShipAPI thisShip = beam.getSource();	

		CombatEntityAPI target = beam.getDamageTarget();
		if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
			float dur = beam.getDamage().getDpsDuration();
			// needed because when the ship is in fast-time, dpsDuration will not be reset every frame as it should be
			fireInterval.advance(dur);
			if (fireInterval.intervalElapsed()) {
				ShipAPI ship = (ShipAPI) target;
				boolean hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getRayEndPrevFrame());
				if (!hitShield && ship.isAlive()) {
					Vector2f point = beam.getRayEndPrevFrame();
					boolean hasMod = thisShip.getVariant().hasHullMod(REACTOR_MOD);
					float lostHP = HP_LOSS_BASE * calculateHullLossMult(ship, point, hasMod);
					float newHP = target.getHitpoints() - lostHP;
					if (newHP > 1) target.setHitpoints(newHP);
					else engine.applyDamage(target, point, 10000f, beam.getDamage().getType(), 0f, true, false, beam.getSource());
					engine.addFloatingDamageText(point, lostHP, Misc.FLOATY_HULL_DAMAGE_COLOR, target, beam.getSource());
				}
			}
		}
	}
	
	private float calculateHullLossMult(ShipAPI ship, Vector2f point, boolean hasMod) {
		ArmorGridAPI grid = ship.getArmorGrid();
		int[] cell = grid.getCellAtLocation(point);
		if (cell == null) return 1f;
		float armorPercentRemaining = ship.getArmorGrid().getArmorFraction(cell[0], cell[1]);
		float ratio = 1f - armorPercentRemaining;
		if (hasMod && ratio < HP_RATIO_THRESHOLD) ratio = HP_RATIO_THRESHOLD; 
		return ratio;
	}
}