package data.scripts.vice.ai;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.util.IntervalUtil;

//use when ship flux is over 70%
public class FluxShuntAI implements ShipSystemAIScript {
    
	private CombatEngineAPI engine;
    private ShipAPI ship;
    private ShipSystemAPI system;
	private static float FLUX_THRESHOLD = 0.70f;
	
    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {  
        if (engine != Global.getCombatEngine()) this.engine = Global.getCombatEngine();
        if (engine.isPaused() || ship.getShipAI() == null || ship.getFluxLevel() < FLUX_THRESHOLD) return;
		ship.useSystem();
    }
}