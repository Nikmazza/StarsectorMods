package data.scripts.vice.listeners;

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

import data.scripts.vice.util.RemoveSpecialFightersUtil;

//reduces Banteng (Standard/TT/SD), deletes items that should not drop and improperly fit special fighters
public class PruneBantengMarketListener extends BaseCampaignEventListener {
	
	private static String BANTENG = "vice_banteng";
	private static int MAX_SHIP_COUNT = 4;
	
	private static List<String> CORE_LIST = new ArrayList<String>();
	static {
		CORE_LIST.add("xo_synthesis_core");
		CORE_LIST.add("ix_broken_core");
		CORE_LIST.add("ix_command_core");
	}
	
	private static List<String> WING_LIST = new ArrayList<String>();
	static {
		WING_LIST.add("vice_kite_atk_wing");
		WING_LIST.add("vice_kite_bmr_wing");
		WING_LIST.add("vice_kite_lg_wing");
		WING_LIST.add("vice_kite_dem_wing");
		WING_LIST.add("vice_disruptor_drone_wing");
	}
	
	public PruneBantengMarketListener() {
		super(true);
	}	
	
	public static void pruneMarket(MarketAPI market) {
		if (market == null || market.getSubmarketsCopy() == null) return;
		List<SubmarketAPI> submarkets = market.getSubmarketsCopy();
		for (SubmarketAPI s : submarkets) {
			int shipCountBant = 0;
			if (s.getSpecId().equals(Submarkets.SUBMARKET_STORAGE)) return;
			if (s.getSpecId().equals("ix_honor_guard_market")) return;
			List<FleetMemberAPI> shipsToDelete = new ArrayList<FleetMemberAPI>();
			List<FleetMemberAPI> ships = s.getCargo().getMothballedShips().getMembersListCopy();
			for (FleetMemberAPI ship : ships) {
				//prune extra haulers
				if (ship.getHullSpec().getHullId().startsWith(BANTENG)) {
					if (shipCountBant < MAX_SHIP_COUNT) shipCountBant++;
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

	@Override
	public void reportPlayerOpenedMarket(MarketAPI market) {
		pruneMarket(market);
		if (Global.getSector().getPlayerMemoryWithoutUpdate().is("$vice_faith_fury_aborted", true)) {
			Global.getSector().getMemoryWithoutUpdate().set("$vice_faith_fury", true);
			Global.getSector().getMemoryWithoutUpdate().set("$vice_faith_fury_failed", true);
		}
	}
	
	//clear special items that drop due to scrapping or selling ships
	@Override
	public void reportPlayerClosedMarket(MarketAPI market) {
		deleteLPCs(WING_LIST);
		deleteCores(CORE_LIST);
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
	
	private void deleteCores(List<String> coreList) {
		try {
			CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
			for (String core : coreList) {
				float quantity = cargo.getCommodityQuantity(core);
				if (quantity >= 0f) cargo.removeCommodity(core, quantity); 
			}
		}
		catch (Exception e) {}
	}
}