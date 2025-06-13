package data.scripts.vice.ai;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;

import data.scripts.vice.util.DistanceUtil;

//attempt to use system when at least 2 Terminator DEMs are present and enemy is within 2000 su
public class DEMStrikeAI implements ShipSystemAIScript {
    
	private CombatEngineAPI engine;
    private ShipAPI ship;
    private ShipSystemAPI system;
	private static String DEM_WING_ID = "vice_terminator_dem_wing";
	private static float RANGE = 2000f;
	
    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
    }

	private static boolean isInRange(ShipAPI ship, ShipAPI target, float range) {
		if (ship == null || target == null) return false;
		return DistanceUtil.getDistance(ship, target) <= range;
	}
	
    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        if (engine != Global.getCombatEngine()) this.engine = Global.getCombatEngine();
        if (engine.isPaused() || ship.getShipAI() == null) return;
		if (isInRange(ship, target, RANGE)) {
			int currentFighters = 0;
			for (FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()) {
				if (bay.getWing() == null || !bay.getWing().getWingId().equals(DEM_WING_ID)) continue;
				currentFighters += bay.getWing().getWingMembers().size();
			}
			if (currentFighters > 0) ship.useSystem();
        }
    }
}