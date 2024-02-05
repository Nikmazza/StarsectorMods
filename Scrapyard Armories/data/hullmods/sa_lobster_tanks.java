package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;
/**
 * Original lobster hullmod script by creature
 * Modified by Mayu
 * Modified by ParadoxConduit
 */
public class sa_lobster_tanks extends BaseHullMod {
	
	private static final Logger log = Global.getLogger(sa_lobster_tanks.class);   

	private static final Set<String> BLOCKED_HULLMOD = new HashSet<String>();
	static {
		BLOCKED_HULLMOD.add("yrex_LobsterPot");
	}

        private static final Map<ShipAPI.HullSize, Integer> shipSizeEffect = new HashMap();
        static {
            shipSizeEffect.put(ShipAPI.HullSize.DEFAULT, 10);
            shipSizeEffect.put(ShipAPI.HullSize.CAPITAL_SHIP, 100);
            shipSizeEffect.put(ShipAPI.HullSize.CRUISER, 50);
            shipSizeEffect.put(ShipAPI.HullSize.DESTROYER, 30);
            shipSizeEffect.put(ShipAPI.HullSize.FRIGATE, 10);    
        }

	public static String DATA_KEY = "sa_lobster_tanks_ship_key";

	public static class LobsterPotData {
		Map<FleetMemberAPI, Integer> counter = new LinkedHashMap<FleetMemberAPI, Integer>();
		Map<FleetMemberAPI, Float> growthTracker = new LinkedHashMap<FleetMemberAPI, Float>();//Each time grown reaches 1.0, add 1 lobster.
	}

	private static final Color TEXT_COLOR = new Color(255,255,255,80);
	private static final Color LOBSTER_COLOR = new Color(145,185,245,255);
	private static final Color FOOD_COLOR = new Color(255,255,255,255);
	private static final Vector2f ZERO = new Vector2f();

	private final float foodCost = Global.getSettings().getFloat("sa_lobster_tanks_food_to_lobster_cost");
	private final float growthPerLobster = Global.getSettings().getFloat("sa_lobster_tanks_growth_rate");
	private final float spawningMonthFrom = Global.getSettings().getFloat("sa_lobster_tanks_spawning_month_from");
	private final float spawningMonthTo = Global.getSettings().getFloat("sa_lobster_tanks_spawning_month_to");
	private final int daysToUpdate = Global.getSettings().getInt("sa_lobster_tanks_days_to_update");
	private int dayCounter;
	private int eatCounter;
	private int birthCounter;

	private final boolean playSFX = Global.getSettings().getBoolean("sa_lobster_tanks_play_sfx");
	private final boolean showNotification = Global.getSettings().getBoolean("sa_lobster_tanks_show_notification");

	private CombatEngineAPI engine;

        @Override
        public void applyEffectsAfterShipCreation(final ShipAPI ship, final String id){
            for (final String rmv : BLOCKED_HULLMOD) {
                if (ship.getVariant().getHullMods().contains(rmv)) {                
                    ship.getVariant().removeMod(rmv);
                }
            }
        } 
        
        @Override
	public String getDescriptionParam(final int index, final HullSize hullSize) {
		if (index == 0)
			return "" + foodCost;
		if (index == 1)
			return shipSizeEffect.get(ShipAPI.HullSize.FRIGATE) + "/" + 
			shipSizeEffect.get(ShipAPI.HullSize.DESTROYER) + "/" + 
			shipSizeEffect.get(ShipAPI.HullSize.CRUISER) + "/" + 
			shipSizeEffect.get(ShipAPI.HullSize.CAPITAL_SHIP);
		return null;
	}
        
        @Override
	public void advanceInCampaign(final FleetMemberAPI member, final float amount) {
		if (member.getFleetData() == null)
			return;
		if (member.getFleetData().getFleet() == null)
			return;
		if (!member.getFleetData().getFleet().isPlayerFleet())
			return;

		if (engine != Global.getCombatEngine()) {
			this.engine = Global.getCombatEngine();
		}

		final CampaignFleetAPI fleet = member.getFleetData().getFleet();

		if (hasDoneToday(member)) {
			return;
		}
		// On next day
		
		float lobsters = fleet.getCargo().getCommodityQuantity(Commodities.LOBSTER);
		float food = fleet.getCargo().getCommodityQuantity(Commodities.FOOD);
		
		float maxLobstersThatCanBreed = ((Integer)shipSizeEffect.get(member.getVariant().getHullSize())).intValue() * (100/growthPerLobster);
		float maxLobstersThatNeedToEat = lobsters;
		float maxLobstersThatCanBeFed = food / foodCost;
		
		float lobstersToEat = Math.min(maxLobstersThatCanBreed, Math.min(maxLobstersThatNeedToEat, maxLobstersThatCanBeFed));
		float totalGrowth = lobstersToEat * growthPerLobster;
		
		boolean isSpawningSeason = false;
		if(Global.getSector().getClock().getMonth() >= spawningMonthFrom && Global.getSector().getClock().getMonth() <= spawningMonthTo) isSpawningSeason = true;
		
		if(isSpawningSeason) totalGrowth *= 10;
		
		int newLobsters = this.addGrowth(member, totalGrowth);
		
		newLobsters = (int) Math.min(((Integer)shipSizeEffect.get(member.getVariant().getHullSize())).intValue(), newLobsters);

		fleet.getCargo().removeCommodity(Commodities.FOOD, lobstersToEat * foodCost);
		fleet.getCargo().addCommodity(Commodities.LOBSTER, newLobsters);
		
		dayCounter += 1;
		birthCounter += newLobsters;
		eatCounter += lobstersToEat * foodCost;
		
		if (dayCounter >= daysToUpdate){
			
		
			if(eatCounter > 0) {
				if (playSFX)
					Global.getSoundPlayer().playSound("ui_cargo_food_drop", 1f, 1f, fleet.getLocation(), ZERO);
		
				if (showNotification)			
					Global.getSector().getCampaignUI().addMessage("Lobsters have eaten " + (int) (eatCounter) + " food.", FOOD_COLOR);
			}
			
			if(birthCounter > 0) {
				if (playSFX)
					Global.getSoundPlayer().playSound("ui_cargo_luxury", 1f, 1f, fleet.getLocation(), ZERO);

				if (showNotification)			
					Global.getSector().getCampaignUI().addMessage((int) (birthCounter) + " new lobsters have been born!", LOBSTER_COLOR);
					
				fleet.addFloatingText("Lobsters have multiplied!", Misc.setAlpha(TEXT_COLOR, 255), 0.5f);
			}
			
			dayCounter = 0;
			eatCounter = 0;
			birthCounter = 0;
		}		
	}

	private boolean hasDoneToday(final FleetMemberAPI member) {
		if(engine == null) {
			return true;
		}
		if(engine.getCustomData() == null) {
			return true;
		}

		LobsterPotData data = (LobsterPotData) engine.getCustomData().get(DATA_KEY);
		if (data == null) {
			data = new LobsterPotData();
			engine.getCustomData().put(DATA_KEY, data);
		}

		if (!data.counter.containsKey(member)) {
			data.counter.put(member, Global.getSector().getClock().getDay());
		}

		if (data.counter.get(member) == Integer.valueOf(Global.getSector().getClock().getDay())) {
			return true;
		}
		data.counter.put(member, Global.getSector().getClock().getDay());

		return false;
	}
	
	private int addGrowth(final FleetMemberAPI member, final float growthAmount) {

		LobsterPotData data = (LobsterPotData) engine.getCustomData().get(DATA_KEY);
		if (data == null) {
			data = new LobsterPotData();
			engine.getCustomData().put(DATA_KEY, data);
		}

		if (!data.growthTracker.containsKey(member)) {
			data.growthTracker.put(member, 0f);
		}
		
		float currentGrowth = Float.valueOf((Float)data.growthTracker.get(member));
		int numberOfNewLobsters = 0;
		
		currentGrowth += growthAmount;

		//New lobster is grown every 100 growth, times the number of days it takes to update
		numberOfNewLobsters = (int) currentGrowth / 100;
		
		//Leave the remaining growth to the counter;
		currentGrowth = currentGrowth % 100;
		
		data.growthTracker.put(member, currentGrowth);

		return numberOfNewLobsters;
	}

	@Override
	public boolean isApplicableToShip(final ShipAPI ship) {
                return ship != null && (!ship.getVariant().getHullMods().contains("yrex_LobsterPot"));
	}
        
    @Override
	public String getUnapplicableReason(final ShipAPI ship) {
                if (ship == null || ship.getVariant() == null)
                    return "Unable to locate ship!";
                if (ship.getVariant().hasHullMod("yrex_LobsterPot")) {
                    return "Incompatible with Volturnian Aquarium";
                }
                return null;
	}

    @Override
    public void addPostDescriptionSection(final TooltipMakerAPI tooltip, final ShipAPI.HullSize hullSize, final ShipAPI ship, final float width, final boolean isForModSpec) {
        final Color green = new Color(55,245,65,255);
        final float pad = 10f;
        tooltip.addSectionHeading("Details", Alignment.MID, pad);
        tooltip.addPara("- Consumes %s food(s) to produce %s Volturnian Lobster per day."
                +       "\nProduction report will be given every %s days.", pad, green,
                new String[] {
                    Misc.getRoundedValue(10.0f) + "",
                    Misc.getRoundedValue(1.0f) + "",
                    Misc.getRoundedValue(7.0f) + ""
                });
    }

}