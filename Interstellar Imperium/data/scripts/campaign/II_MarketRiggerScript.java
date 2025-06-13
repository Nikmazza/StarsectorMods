package data.scripts.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.II_Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.lazywizard.lazylib.MathUtils;

/* Based on Nicke535's work with heavy modifications */
public class II_MarketRiggerScript implements EveryFrameScript {

    private static final Map<String, MarketRiggerData> RIGGER_DATA = new HashMap<>();

    static {
        Map<String, ShipReplacerData> ultraRestrictionRigger = new HashMap<>();
        ultraRestrictionRigger.put(Submarkets.SUBMARKET_OPEN, new ShipReplacerData(1f, 0, 0));
        ultraRestrictionRigger.put(Submarkets.SUBMARKET_BLACK, new ShipReplacerData(1f, 0, 0));
        ultraRestrictionRigger.put(Submarkets.GENERIC_MILITARY, new ShipReplacerData(0.5f, 0, 0));
        ultraRestrictionRigger.put("ii_ebay", new ShipReplacerData(1f, 0, 0));

        RIGGER_DATA.put("ii_olympus", new MarketRiggerData(Arrays.asList("ii_dominus", "ii_caesar", "ii_matriarch"), ultraRestrictionRigger, Arrays.asList("ii_aleria")));

        Map<String, ShipReplacerData> highRestrictionRigger = new HashMap<>();
        highRestrictionRigger.put(Submarkets.SUBMARKET_OPEN, new ShipReplacerData(0.75f, 2, 4));
        highRestrictionRigger.put(Submarkets.SUBMARKET_BLACK, new ShipReplacerData(0.5f, 1, 3));
        highRestrictionRigger.put(Submarkets.GENERIC_MILITARY, new ShipReplacerData(0.25f, 0, 0));
        highRestrictionRigger.put("ii_ebay", new ShipReplacerData(0.5f, 1, 3));

        RIGGER_DATA.put("ii_matriarch", new MarketRiggerData(Arrays.asList("ii_dominus", "ii_caesar", "ii_barrus", "ii_ardea"), highRestrictionRigger, null));

        Map<String, ShipReplacerData> restrictionRigger = new HashMap<>();
        restrictionRigger.put(Submarkets.SUBMARKET_OPEN, new ShipReplacerData(0.5f, 1, 3));
        restrictionRigger.put(Submarkets.SUBMARKET_BLACK, new ShipReplacerData(0.25f, 0, 2));
        restrictionRigger.put("ii_ebay", new ShipReplacerData(0.25f, 0, 2));

        RIGGER_DATA.put("ii_dominus", new MarketRiggerData(Arrays.asList("ii_barrus", "ii_ixon", "ii_sebastos", "ii_dictator", "ii_ardea"), restrictionRigger, null));
        RIGGER_DATA.put("ii_caesar", new MarketRiggerData(Arrays.asList("ii_barrus", "ii_ixon", "ii_sebastos", "ii_dictator", "ii_ardea"), restrictionRigger, null));
        RIGGER_DATA.put("ii_adamas", new MarketRiggerData(Arrays.asList("ii_ixon", "ii_sebastos", "ii_dictator", "ii_ardea", "ii_lynx"), restrictionRigger, null));
    }

    /* Counts in seconds */
    private final IntervalUtil shortTracker = new IntervalUtil(1f, 1.5f);

    /* Counts in days */
    private IntervalUtil longTracker = new IntervalUtil(1f, 1f);

    /* Updates once every longTracker period */
    private final List<String> marketsToManipulate = new ArrayList<>();

    private final HashMap<String, Float> retainedMembers = new LinkedHashMap<>();

    private final Random rand = new Random();

    protected Object readResolve() {
        longTracker = new IntervalUtil(1f, 1f);
        return this;
    }

    @Override
    public void advance(float amount) {
        SectorAPI sector = Global.getSector();
        if (sector == null) {
            return;
        }

        float longAmount = Misc.getDays(amount);
        if (sector.isPaused()) {
            longAmount = 0f;
        }

        longTracker.advance(longAmount);
        shortTracker.advance(amount);

        if (longTracker.intervalElapsed()) {
            FactionAPI faction = sector.getFaction("interstellarimperium");

            marketsToManipulate.clear();
            for (MarketAPI market : sector.getEconomy().getMarketsCopy()) {
                if (!market.isHidden() && (market.getFaction() == faction)) {
                    marketsToManipulate.add(market.getId());
                }
            }

            Set<Map.Entry<String, Float>> retainedSet = retainedMembers.entrySet();
            Iterator<Map.Entry<String, Float>> iter = retainedSet.iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Float> retained = iter.next();
                float curr = retained.getValue();
                curr -= 1f;
                if (curr <= 0f) {
                    iter.remove();
                } else {
                    retained.setValue(curr);
                }
            }
        }

        if (shortTracker.intervalElapsed()) {
            for (String marketID : marketsToManipulate) {
                MarketAPI market = sector.getEconomy().getMarket(marketID);
                if (market == null) {
                    continue;
                }

                for (SubmarketAPI submarket : market.getSubmarketsCopy()) {
                    String submarketID = submarket.getSpecId();
                    CargoAPI cargo = submarket.getCargo();
                    List<FleetMemberAPI> toDelete = new ArrayList<>();
                    for (FleetMemberAPI member : cargo.getMothballedShips().getMembersInPriorityOrder()) {
                        if (retainedMembers.containsKey(member.getId())) {
                            continue;
                        }

                        String hullID = II_Util.getNonDHullId(member.getHullSpec());
                        MarketRiggerData riggerData = RIGGER_DATA.get(hullID);
                        if (riggerData != null) {
                            ShipReplacerData replacerData = riggerData.replacementData.get(submarketID);
                            if (replacerData == null) {
                                replacerData = new ShipReplacerData(0f, 0, 0);
                            }

                            boolean alwaysReplace = false;
                            if (riggerData.bannedMarkets != null) {
                                if (riggerData.bannedMarkets.contains(marketID)) {
                                    alwaysReplace = true;
                                }
                            }

                            if (alwaysReplace || ((float) Math.random() < replacerData.replacementChance)) {
                                List<String> variantList = riggerData.replacementHulls;
                                String variantID = variantList.get(MathUtils.getRandomNumberInRange(0, variantList.size() - 1));
                                variantID += "_Hull";

                                addShip(variantID, DModManager.getNumDMods(member.getVariant()), cargo);

                                toDelete.add(member);
                            } else {
                                int DMods = MathUtils.getRandomNumberInRange(replacerData.minDMods, replacerData.maxDMods);
                                if (DMods > 0) {
                                    DModManager.setDHull(member.getVariant());
                                    DModManager.addDMods(member, true, DMods, rand);
                                }
                                float time = 30f;
                                if (submarket instanceof BaseSubmarketPlugin base) {
                                    time = base.getMinSWUpdateInterval();
                                }
                                retainedMembers.put(member.getId(), time);
                            }
                        }
                    }

                    for (FleetMemberAPI member : toDelete) {
                        cargo.getMothballedShips().removeFleetMember(member);
                    }
                }
            }
        }
    }

    private FleetMemberAPI addShip(String variantId, int dMods, CargoAPI cargo) {
        FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, variantId);

        if (dMods > 0) {
            DModManager.setDHull(member.getVariant());
            DModManager.addDMods(member, true, dMods, rand);
        }

        member.getRepairTracker().setMothballed(true);
        member.getRepairTracker().setCR(0.5f);
        cargo.getMothballedShips().addFleetMember(member);
        return member;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    /* Run when paused to even further limit the small grace-periods the player might find the wrong ships in */
    @Override
    public boolean runWhilePaused() {
        return true;
    }

    static class MarketRiggerData {

        final List<String> replacementHulls;
        final Map<String, ShipReplacerData> replacementData;
        final List<String> bannedMarkets;

        MarketRiggerData(List<String> replacementHulls, Map<String, ShipReplacerData> replacementData, List<String> bannedMarkets) {
            this.replacementHulls = replacementHulls;
            this.replacementData = replacementData;
            this.bannedMarkets = bannedMarkets;
        }
    }

    static class ShipReplacerData {

        final float replacementChance;
        final int minDMods;
        final int maxDMods;

        ShipReplacerData(float replacementChance, int minDMods, int maxDMods) {
            this.replacementChance = replacementChance;
            this.minDMods = minDMods;
            this.maxDMods = maxDMods;
        }
    }
}
