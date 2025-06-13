package data.campaign.fleets;

import java.util.List;
import java.util.Random;

import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantSeededFleetManager;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RouteFleetAssignmentAI;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken.VisibilityLevel;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI.EncounterOption;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteData;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteSegment;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class BRVScavengerFleetAssignmentAI extends RouteFleetAssignmentAI {

	public BRVScavengerFleetAssignmentAI(CampaignFleetAPI fleet, RouteData route, boolean pirate) {
		super(fleet, route);
	}
	
	@Override
	protected String getTravelActionText(RouteSegment segment) {
		//if (segment.systemTo == route.getMarket().getContainingLocation()) {
		if (segment.to == route.getMarket().getPrimaryEntity()) {
			return "returning to " + route.getMarket().getName();
		}
		return "on a salvage expedition";
	}
	
	@Override
	protected String getInSystemActionText(RouteSegment segment) {
		return "exploring";
	}


	@Override
	protected void addLocalAssignment(RouteSegment segment, boolean justSpawned) {
		//boolean pickSpecificEntity = (float) Math.random() > 0.2f && segment.systemFrom instanceof StarSystemAPI;
		boolean pickSpecificEntity = (float) Math.random() > 0.2f && !segment.from.getContainingLocation().isHyperspace();
		if (pickSpecificEntity) {
			SectorEntityToken target = RemnantSeededFleetManager.pickEntityToGuard(new Random(), (StarSystemAPI) segment.from.getContainingLocation(), fleet);
			if (target != null) {
				if (justSpawned) {
					Vector2f loc = Misc.getPointAtRadius(new Vector2f(target.getLocation()), 500);
					fleet.setLocation(loc.x, loc.y);
				}
				
				float speed = Misc.getSpeedForBurnLevel(8);
				float dist = Misc.getDistance(fleet.getLocation(), target.getLocation());
				float seconds = dist / speed;
				float days = seconds / Global.getSector().getClock().getSecondsPerDay();
				days += 5f + 5f * (float) Math.random();
				fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, target, days, "investigating");
				return;
			} else {
				if (justSpawned) {
					Vector2f loc = Misc.getPointAtRadius(new Vector2f(), 8000);
					fleet.setLocation(loc.x, loc.y);
				}
				
				float days = 5f + 5f * (float) Math.random();
				fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, null, days, "exploring");
			}
		} else {
			super.addLocalAssignment(segment, justSpawned);
		}
	}
}










