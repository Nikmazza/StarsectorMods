package data.scripts.ix.ai;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.IntervalUtil;

//use when ship flux is over 80%
public class ShieldVentAI implements ShipSystemAIScript {
    
	private CombatEngineAPI engine;
    private ShipAPI ship;
    private ShipSystemAPI system;
	private static float FLUX_THRESHOLD = 0.80f;
	private static float ANGLE_GOOD = 360f;
	private static float ANGLE_BAD = 160f;
	
    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {  
        if (engine != Global.getCombatEngine()) this.engine = Global.getCombatEngine();
        if (engine.isPaused() || ship.getShipAI() == null) return;
		if (ship.getSystem().isCoolingDown()) {
			ship.getShield().toggleOff();
			ship.getShield().setArc(0f);
		}
		else if (ship.getShield().getArc() == 0f) {
			float angle = ship.getVariant().hasHullMod("ix_limited_undershield") ? ANGLE_BAD : ANGLE_GOOD;
			ship.getShield().setArc(angle);
		}
		if (ship.getFluxLevel() > FLUX_THRESHOLD) ship.useSystem();
    }
}