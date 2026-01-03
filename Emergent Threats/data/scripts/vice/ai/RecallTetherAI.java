package data.scripts.vice.ai;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.Misc;

import data.scripts.vice.util.DistanceUtil;

public class RecallTetherAI implements ShipSystemAIScript {
    
	private CombatEngineAPI engine;
	private ShipAPI ship;
	private WeaponAPI weapon;
	private static String WEAPON = "vice_plasma_fighter";
	
	@Override
	public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
		this.engine = engine;
		this.ship = ship;
		for (WeaponAPI w : ship.getAllWeapons()) {
			if (w.getId().equals(WEAPON)) this.weapon = w;
		}
	}
	
	@Override
	public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
		if (engine != Global.getCombatEngine()) engine = Global.getCombatEngine();
		if (engine.isPaused() || ship.getShipAI() == null) return;
		if (weapon == null) return;
		if (weapon.getAmmo() == 0 && ship.getSystem().getState() == SystemState.IDLE) ship.useSystem();
		return;
	}
}