package data.shipsystems.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import org.lwjgl.util.vector.Vector2f;

public class BurnDriveBadAI implements ShipSystemAIScript {
	protected ShipAPI ship;
	protected CombatEngineAPI engine;
	protected ShipwideAIFlags flags;
	protected ShipSystemAPI system;
	public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
		this.ship = ship;
		this.flags = flags;
		this.engine = engine;
		this.system = system;
	}
	public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
		BurnDriveBadStats script = (BurnDriveBadStats)system.getScript();
		if(script.isUsable(system,ship)) {
			ship.giveCommand(ShipCommand.USE_SYSTEM, null, 0);
		}
	}
}
