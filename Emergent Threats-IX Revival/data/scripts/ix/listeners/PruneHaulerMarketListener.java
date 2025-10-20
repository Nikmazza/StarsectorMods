package data.scripts.ix.listeners;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;

import data.scripts.ix.util.RemoveSpecialFightersUtil;

//reduces Banteng (IX/TW) and Buffalo (IX/TW) to no more than 4 per market
//adds Panopticon Interface checker to IX warships so players won't randomly spawn interfaces if they buy IX hulls then split off the ships into their own fleets or put them into task groups
public class PruneHaulerMarketListener extends BaseCampaignEventListener {
	
	private static String BANTENG_IX = "banteng_ix";
	private static String BANTENG_TW = "banteng_tw";
	private static String BUFFALO_IX = "buffalo_ix";
	private static String BUFFALO_TW = "buffalo_tw";
	private static int MAX_SHIP_COUNT = 4;
	
	private static String IX_MOD_ID = "ix_ninth";
	private static String CHECKER_ID = "ix_panoptic_checker";	
	
	private static List<String> WING_LIST = new ArrayList<String>();
	static {
		WING_LIST.add("nimbus_tw_wing_c");
		WING_LIST.add("nimbus_tw_wing_r");
		WING_LIST.add("nimbus_tw_wing_s");
		WING_LIST.add("nimbus_tw_wing_m");
		WING_LIST.add("nimbus_tw_wing_l");
		WING_LIST.add("starquake_tw_wing");
	}
	
	public PruneHaulerMarketListener() {
		super(true);
	}	
	
	public static void pruneMarket(MarketAPI market) {
		if (market == null || market.getSubmarketsCopy() == null) return;
		List<SubmarketAPI> submarkets = market.getSubmarketsCopy();
		for (SubmarketAPI s : submarkets) {
			int shipCountBant = 0;
			int shipCountBuff = 0;
			if (s.getSpecId().equals(Submarkets.SUBMARKET_STORAGE)) return;
			if (s.getSpecId().equals("ix_honor_guard_market")) return;
			List<FleetMemberAPI> shipsToDelete = new ArrayList<FleetMemberAPI>();
			List<FleetMemberAPI> ships = s.getCargo().getMothballedShips().getMembersListCopy();
			for (FleetMemberAPI ship : ships) {
				//add panoptic interface checker
				if (ship.getVariant().hasHullMod(IX_MOD_ID)) ship.getVariant().addPermaMod(CHECKER_ID);
				//prune extra haulers
				if (ship.getHullSpec().getHullId().equals(BANTENG_IX)
							|| ship.getHullSpec().getHullId().equals(BANTENG_TW)) {
					if (shipCountBant < MAX_SHIP_COUNT) shipCountBant++;
					else shipsToDelete.add(ship);
				}
				else if (ship.getHullSpec().getHullId().equals(BUFFALO_IX)
							|| ship.getHullSpec().getHullId().equals(BUFFALO_TW)) {
					if (shipCountBuff < MAX_SHIP_COUNT) shipCountBuff++;
					else shipsToDelete.add(ship);
				}
			}
			if (!shipsToDelete.isEmpty()) {
				for (FleetMemberAPI ship : shipsToDelete) {
					s.getCargo().getMothballedShips().removeFleetMember(ship);
				}
				s.getPlugin().updateCargoPrePlayerInteraction();
			}
		}
	}
	
	private void deleteLPCs(List<String> wingList) {
		try {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			for (CargoStackAPI s : cargo.getStacksCopy()) {
				for (String wing : wingList) {
					if (s.isFighterWingStack() && s.getFighterWingSpecIfWing().getId().equals(wing)) cargo.removeStack(s);
				}
			}
		}
		catch (Exception e) {}
		
		//removes banned wings on ships, exempts special carriers which has built-in mod call other util method
		RemoveSpecialFightersUtil.deleteSpecialLPCs(WING_LIST); 
	}

	@Override
	public void reportPlayerOpenedMarket(MarketAPI market) {
		pruneMarket(market);
	}
	
	//clear special items that drop due to scrapping or selling ships
	@Override
	public void reportPlayerClosedMarket(MarketAPI market) {
		deleteLPCs(WING_LIST);
	}
}