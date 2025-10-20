package data.scripts.campaign.fleets;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI.EncounterOption;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.GMDAPickContributionMethod;
import com.fs.starfarer.api.impl.campaign.fleets.DisposableFleetManager;
import com.fs.starfarer.api.campaign.SectorEntityToken.VisibilityLevel;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseManager;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.GMDAModPlugin;
import org.lazywizard.lazylib.MathUtils;

import java.util.List;

public class GMDAFleetManager extends DisposableFleetManager {

    public static int MAX_GMDA_FLEETS = 1;
    private static final float MAX_LY_FROM_GMDA = 8f;

    @Override
    protected Object readResolve() {
        super.readResolve();
        return this;
    }

    @Override
    protected String getSpawnId() {
        return "gmda_spawn"; // not a faction id, just an identifier for this spawner
    }

    @Override
    protected int getDesiredNumFleetsForSpawnLocation() {
        MarketAPI market = getLargestMarket();
        MarketAPI closestMarket = getClosestGMDA();

        float desiredNumFleets;
        if (closestMarket == null) {
            desiredNumFleets = 0f;
        } else {
            float distScale = 1f - Math.min(1f, Misc.getDistanceToPlayerLY(closestMarket.getLocationInHyperspace()) / MAX_LY_FROM_GMDA);
            desiredNumFleets = 1f * distScale;
        }

        if (market != null) {
            desiredNumFleets += Math.max(0f, (market.getSize() - 3f) * 0.5f);
        }

        int level = getGMDALevel();
        desiredNumFleets += level * 0.5f;

        return (int) Math.round(desiredNumFleets * MAX_GMDA_FLEETS);
    }

    protected int getGMDALevel() {
        if (currSpawnLoc == null) {
            return 0;
        }
        int total = 0;

        for (MarketAPI market : Global.getSector().getEconomy().getMarkets(currSpawnLoc)) {
            if (market.isHidden()) {
                continue;
            }
            if (market.hasCondition(Conditions.DECIVILIZED)) {
                continue;
            }

            if (market.getFactionId() == Factions.INDEPENDENT) {
                total++;
                if (market.hasCondition("pirate_activity")){
                    if(market.getStabilityValue() < 5 ) {
                        total++;
                        }
                    }
                }
            }
        return total;
    }

    protected MarketAPI getClosestGMDA() {
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        if (player == null) {
            return null;
        }

        MarketAPI closest = null;
        float minDistance = 100000f;
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.isHidden()) {
                continue;
            }
            if (market.hasCondition(Conditions.DECIVILIZED)) {
                continue;
            }
            if (!market.hasCondition("GMDA_facility")) {
                switch (market.getFactionId()) {
                    case Factions.INDEPENDENT:
                        break;
                    default:
                        continue;
                }
            }
            float distance = Misc.getDistanceToPlayerLY(market.getLocationInHyperspace());
            if (distance < minDistance) {
                closest = market;
                minDistance = distance;
            }
        }
        return closest;
    }


    protected MarketAPI getLargestMarket() {
        if (currSpawnLoc == null) {
            return null;
        }
        MarketAPI largest = null;
        int maxSize = 0;
        for (MarketAPI market : Global.getSector().getEconomy().getMarkets(currSpawnLoc)) {
            if (market.isHidden()) {
                continue;
            }
            if (market.hasCondition(Conditions.DECIVILIZED)) {
                continue;
            }
            if (!market.hasCondition("GMDA_facility")) {
                switch (market.getFactionId()) {
                    case Factions.INDEPENDENT:
                    case "gmda":
                        break;
                    default:
                        continue;
                }
            }

            if (market.getSize() > maxSize) {
                maxSize = (market.getSize());
                largest = market;
            }
        }
        return largest;
    }

    @Override
    protected float getExpireDaysPerFleet() {
        /* Bigger fleets, slower wind-down */
        return 20f;
    }

    @Override
    protected CampaignFleetAPI spawnFleetImpl() {
        if (!GMDAModPlugin.Module_GMDA)  {
            return null;
        }

        StarSystemAPI system = currSpawnLoc;
        if (system == null) {
            return null;
        }

        float combat = MathUtils.getRandomNumberInRange(10f, MathUtils.getRandomNumberInRange(40f, MathUtils.getRandomNumberInRange(80f, 120f)));

        float timeFactor = (PirateBaseManager.getInstance().getDaysSinceStart() - 180f) / (365f * 2f);
        if (timeFactor < 0f) {
            timeFactor = 0f;
        }
        if (timeFactor > 1f) {
            timeFactor = (float) Math.sqrt(timeFactor);
        }
        if (timeFactor > 2f) {
            timeFactor = 2f;
        }
        combat *= 1f + MathUtils.getRandomNumberInRange(0f, timeFactor);

        float levelFactor = 0f;
        if (Global.getSector().getPlayerPerson() != null) {
            levelFactor = Global.getSector().getPlayerPerson().getStats().getLevel() / 50f;
        }

        combat *= 1f + MathUtils.getRandomNumberInRange(0f, levelFactor);
        float freighter = Math.round(combat * MathUtils.getRandomNumberInRange(0.05f, 0.1f));
        float tanker = Math.round(combat * MathUtils.getRandomNumberInRange(0f, 0.1f));
        float utility = Math.round((freighter + tanker) * MathUtils.getRandomNumberInRange(0f, 0.5f));

        String fleetType;
        if (combat < 50) {
            fleetType = FleetTypes.PATROL_SMALL;
        } else if (combat < 100) {
            fleetType = FleetTypes.PATROL_MEDIUM;
        } else {
            fleetType = FleetTypes.PATROL_LARGE;
        }

        FleetParamsV3 params = new FleetParamsV3(
                null, // market
                system.getLocation(), // location
                "gmda_pursuit", // fleet's faction, if different from above, which is also used for source market picking
                1.25f,
                fleetType,
                combat, // combatPts
                freighter, // freighterPts
                tanker, // tankerPts
                0f, // transportPts
                0f, // linerPts
                utility, // utilityPts
                0 // qualityBonus
        );

        FactionDoctrineAPI doctrine = Global.getSector().getFaction("gmda_pursuit").getDoctrine().clone();

        GMDAFleetType gmdaFleetType;
        if (combat < 80 && Math.random() < 0.3) {
            gmdaFleetType = GMDAFleetType.FRIGATES;
            doctrine.setShipSize(1);
            doctrine.setWarships(4);
            doctrine.setCarriers(1);
            doctrine.setPhaseShips(2);
        } else if (combat >= 40 && combat < 120 && Math.random() < 0.3) {
            gmdaFleetType = GMDAFleetType.DESTROYERS;
            doctrine.setShipSize(2);
            doctrine.setWarships(4);
            doctrine.setCarriers(2);
            doctrine.setPhaseShips(1);
        } else if (combat >= 80 && Math.random() < 0.3) {
            gmdaFleetType = GMDAFleetType.CRUISERS;
            doctrine.setShipSize(4);
            doctrine.setWarships(5);
            doctrine.setCarriers(1);
            doctrine.setPhaseShips(1);
        } else if (combat >= 90 && Math.random() < 0.3) {
            gmdaFleetType = GMDAFleetType.CAPITALS;
            doctrine.setShipSize(5);
            doctrine.setWarships(4);
            doctrine.setCarriers(2);
            doctrine.setPhaseShips(1);
        } else if (combat >= 60 && Math.random() < 0.4) {
            gmdaFleetType = GMDAFleetType.CARRIERS;
            doctrine.setShipSize(3);
            doctrine.setWarships(2);
            doctrine.setCarriers(5);
            doctrine.setPhaseShips(1);
        } else if (combat >= 60 && combat <= 140 && Math.random() < 0.4) {
            gmdaFleetType = GMDAFleetType.PHASE;
            doctrine.setShipSize(3);
            doctrine.setWarships(2);
            doctrine.setCarriers(5);
            doctrine.setPhaseShips(1);
        } else {
            gmdaFleetType = GMDAFleetType.BALANCED;
            doctrine.setShipSize(2);
            doctrine.setWarships(5);
            doctrine.setCarriers(2);
            doctrine.setPhaseShips(1);
        }

        params.doctrineOverride = doctrine;
        params.ignoreMarketFleetSizeMult = true;
        params.officerLevelBonus = 2;
        params.officerNumberBonus = 2;
        params.forceAllowPhaseShipsEtc = true;
        params.modeOverride = ShipPickMode.PRIORITY_THEN_ALL;

        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

        if ((fleet == null) || fleet.isEmpty()) {
            return null;
        }

        if (combat < 50) {
            fleet.getCommander().setRankId(Ranks.SPACE_COMMANDER);
        } else if (combat < 100) {
            fleet.getCommander().setRankId(Ranks.SPACE_CAPTAIN);
        } else {
            fleet.getCommander().setRankId(Ranks.SPACE_ADMIRAL);
        }

        switch (gmdaFleetType) {
            case FRIGATES:
                fleet.setName("Pursuit Patrol");
                break;
            case DESTROYERS:
                fleet.setName("Strike Force");
                break;
            case CRUISERS:
                fleet.setName("Enforcement Operation");
                break;
            case CAPITALS:
                fleet.setName("Parade Flotilla");
                break;
            case PHASE:
                fleet.setName("Fighter Patrol");
                break;
            case CARRIERS:
                fleet.setName("Fighter Patrol");
                break;
            default:
            case BALANCED:
                if (combat < 50) {
                    fleet.setName("Hyperlane Patrol");
                } else if (combat < 100) {
                    fleet.setName("Sheriff Operation");
                } else {
                    fleet.setName("Ministerial Command");
                }
                break;
        }

        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_FLEET_TYPE, "gmdaFleet");
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_NO_MILITARY_RESPONSE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_FIGHT_TO_THE_LAST, true);
        fleet.addScript(new CowardChange(fleet));
		
        int nf = getDesiredNumFleetsForSpawnLocation();

        /* Avoid hyperspace build-up */
        if (nf == 1) {
            setLocationAndOrders(fleet, 1f, 1f);
        } else {
            setLocationAndOrders(fleet, 0f, 0f);
        }
        return fleet;

    }

    public static class CowardChange implements EveryFrameScript {

        private final CampaignFleetAPI fleet;
        private long signalExtortionPaid = 0L;
        private final IntervalUtil tracker = new IntervalUtil(0.1f, 0.3f);

        public CowardChange(CampaignFleetAPI fleet) {
            this.fleet = fleet;
        }

        @Override
        public void advance(float amount) {
            long signalExtortionPaidGlobal = 0L;
            float timerGlobal = 0f;
            if (Global.getSector().getMemoryWithoutUpdate().contains("$gmda_extortion_signal")) {
                signalExtortionPaidGlobal = Global.getSector().getMemoryWithoutUpdate().getLong("$gmda_extortion_signal");
            }
            if (Global.getSector().getMemoryWithoutUpdate().contains("$gmda_extortion_timer")) {
                timerGlobal = Global.getSector().getMemoryWithoutUpdate().getFloat("$gmda_extortion_timer");
            }

            float days = Global.getSector().getClock().convertToDays(amount);
            tracker.advance(days);
            if (timerGlobal > 0f) {
                timerGlobal -= days;
                Global.getSector().getMemoryWithoutUpdate().set("$gmda_extortion_timer", timerGlobal);
            }

            MemoryAPI mem = fleet.getMemoryWithoutUpdate();
            if (mem.getBoolean("$GMDA_extortionAskedFor")) {
                Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_PURSUE_PLAYER, "GMDApulloverPlayer", false, 0f);
                Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_STICK_WITH_PLAYER_IF_ALREADY_TARGET, "GMDApulloverPlayer", false, 0f);
                Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, "GMDApulloverPlayer", false, 0f);
            }

            float chaseDuration;
            float tryAgainTimer;
            boolean allowGlobalPay;
            switch (Global.getSector().getFaction("gmda").getRelToPlayer().getLevel()) {
                default:
                case VENGEFUL:
                    tryAgainTimer = 20f;
                    chaseDuration = 8f;
                    allowGlobalPay = false;
                    break;
                case HOSTILE:
                    tryAgainTimer = 25f;
                    chaseDuration = 7f;
                    allowGlobalPay = false;
                    break;
                case INHOSPITABLE:
                    tryAgainTimer = 30f;
                    chaseDuration = 6f;
                    allowGlobalPay = true;
                    break;
                case SUSPICIOUS:
                    tryAgainTimer = 35f;
                    chaseDuration = 5f;
                    allowGlobalPay = true;
                    break;
                case NEUTRAL:
                    tryAgainTimer = 45f;
                    chaseDuration = 4f;
                    allowGlobalPay = true;
                    break;
                case FAVORABLE:
                    tryAgainTimer = 50f;
                    chaseDuration = 3f;
                    allowGlobalPay = true;
                    break;
                case WELCOMING:
                    tryAgainTimer = 55f;
                    chaseDuration = 2f;
                    allowGlobalPay = true;
                    break;
                case FRIENDLY:
                    tryAgainTimer = 60f;
                    chaseDuration = 1f;
                    allowGlobalPay = true;
                    break;
                case COOPERATIVE:
                    return;
            }

            if (signalExtortionPaid < signalExtortionPaidGlobal) {
                if (mem.getBoolean("$GMDA_extortionPaid")) {
                    signalExtortionPaidGlobal = Global.getSector().getClock().getTimestamp();
                    timerGlobal = Math.max(tryAgainTimer, 30f);
                    Global.getSector().getMemoryWithoutUpdate().set("$gmda_extortion_signal", signalExtortionPaidGlobal);
                    Global.getSector().getMemoryWithoutUpdate().set("$gmda_extortion_timer", timerGlobal);
                } else {
                    if (allowGlobalPay) {
                        mem.set("$GMDA_extortionPaid", tryAgainTimer);
                    }
                    signalExtortionPaid = signalExtortionPaidGlobal;
                    Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_PURSUE_PLAYER, "GMDApulloverPlayer", false, 0f);
                    Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_STICK_WITH_PLAYER_IF_ALREADY_TARGET, "GMDApulloverPlayer", false, 0f);
                    Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, "GMDApulloverPlayer", false, 0f);
                }
            }

            if (tracker.intervalElapsed() && (timerGlobal <= 0f)) {
                doFactionChange();

                CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                if (playerFleet == null) {
                    return;
                }

                if (playerFleet.getContainingLocation() != fleet.getContainingLocation()) {
                    return;
                }

                if (!GMDAPickContributionMethod.playerHasAbilityToPayContribution(fleet)) {
                    return;
                }

                if ((fleet.getCurrentAssignment() != null) && (fleet.getCurrentAssignment().getAssignment() == FleetAssignment.GO_TO_LOCATION)) {
                    return;
                }

                if (!fleet.getFaction().getId().contentEquals("gmda_pursuit")) {
                    return;
                }

                VisibilityLevel level = playerFleet.getVisibilityLevelTo(fleet);
                if (level == VisibilityLevel.COMPOSITION_AND_FACTION_DETAILS) {
                    float chance = GMDAPickContributionMethod.playerNetWorth(fleet) / 1000000f;
                    if (Math.random() < chance) {
                        Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_PURSUE_PLAYER, "GMDApulloverPlayer", true,
                                chaseDuration);
                        Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_STICK_WITH_PLAYER_IF_ALREADY_TARGET, "GMDApulloverPlayer", true,
                                chaseDuration);
                        Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, "GMDApulloverPlayer", true,
                                chaseDuration);
                        mem.set(MemFlags.FLEET_BUSY, false);
                        timerGlobal = tryAgainTimer;
                        Global.getSector().getMemoryWithoutUpdate().set("$gmda_extortion_timer", timerGlobal);
                    } else {
                        Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_PURSUE_PLAYER, "GMDApulloverPlayer", false, 0f);
                        Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_STICK_WITH_PLAYER_IF_ALREADY_TARGET, "GMDApulloverPlayer", false, 0f);
                        Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, "GMDApulloverPlayer", false, 0f);
                        timerGlobal = 7f;
                        Global.getSector().getMemoryWithoutUpdate().set("$gmda_extortion_timer", timerGlobal);
                    }
                }
            }
        }

        @Override
        public boolean isDone() {
            return !fleet.isAlive();
        }

        @Override
        public boolean runWhilePaused() {
            return false;
        }

        private void doFactionChange() {
            boolean canSeePlayer = false;
            MemoryAPI mem = fleet.getMemoryWithoutUpdate();

            if ((fleet.getBattle() != null) && !fleet.getBattle().isDone()) {
                return;
            }

            CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
            if (playerFleet != null) {
				if (GMDAPickContributionMethod.playerHasAbilityToPayContribution(fleet)) {
                    if (playerFleet.getContainingLocation() == fleet.getContainingLocation()) {
                        VisibilityLevel level = playerFleet.getVisibilityLevelTo(fleet);
                        if ((level == VisibilityLevel.COMPOSITION_DETAILS) || (level == VisibilityLevel.COMPOSITION_AND_FACTION_DETAILS)) {
                            canSeePlayer = true;
                        }
                    }
				}
            }

            float fleetStrength = fleet.getEffectiveStrength();

            if (fleetStrength < 1f) {
                return;
            }

            float gmdaWeakerTotal = getWeakerTotalForFaction(Global.getSector().getFaction("gmda_pursuit"));
            float gmdaStrongerTotal = getStrongerTotalForFaction(Global.getSector().getFaction("gmda_pursuit"));
            float gmdaDecisionLevel = 0f;
            if (gmdaWeakerTotal >= 1f) {
                gmdaDecisionLevel += Math.min(1f, fleetStrength / gmdaWeakerTotal);
            }
            gmdaDecisionLevel -= (float) Math.sqrt(gmdaStrongerTotal / fleetStrength);
			float worthLevel = GMDAPickContributionMethod.playerNetWorth(fleet) / 1000000f;
            if (canSeePlayer) {
                gmdaDecisionLevel += 1f * Math.min(1f, worthLevel);
            }
            if (mem.getBoolean(MemFlags.MEMORY_KEY_PURSUE_PLAYER)) {
                gmdaDecisionLevel += 2f * Math.min(1f, worthLevel);
            }

            if (gmdaDecisionLevel >= 0.25f) {
                if (!fleet.getFaction().getId().contentEquals("gmda_pursuit")) {
                    fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);
                    fleet.setNoFactionInName(false);
                    fleet.setFaction("gmda_pursuit", true);
					Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_NO_REP_IMPACT, "gmda_disguise", false, 0);
					Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_LOW_REP_IMPACT, "gmda_disguise", false, 0);
                }
            } else {
                Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_PURSUE_PLAYER, "GMDApulloverPlayer", false, 0f);
                Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_STICK_WITH_PLAYER_IF_ALREADY_TARGET, "GMDApulloverPlayer", false, 0f);
                Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, "GMDApulloverPlayer", false, 0f);
                fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, false);
                fleet.setNoFactionInName(false);
                fleet.setFaction("gmda", true);
				Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_NO_REP_IMPACT, "gmda_disguise", true, 99999);
				Misc.setFlagWithReason(mem, MemFlags.MEMORY_KEY_LOW_REP_IMPACT, "gmda_disguise", true, 99999);
            }
        }

        private float getWeakerTotalForFaction(FactionAPI faction) {
            List<CampaignFleetAPI> visible = Misc.getVisibleFleets(fleet, false);
            float weakerTotal = 0f;
            for (CampaignFleetAPI other : visible) {
                if ((fleet.getAI() != null) && faction.isHostileTo(other.getFaction())) {
                    EncounterOption option = fleet.getAI().pickEncounterOption(null, other, true);
                    float dist = Misc.getDistance(fleet.getLocation(), other.getLocation());
                    VisibilityLevel level = other.getVisibilityLevelTo(fleet);
                    boolean seesComp = level == VisibilityLevel.COMPOSITION_AND_FACTION_DETAILS
                            || level == VisibilityLevel.COMPOSITION_DETAILS;
                    if ((dist < 800f) && seesComp) {
                        if ((option == EncounterOption.ENGAGE) || (option == EncounterOption.HOLD)) {
                            weakerTotal += other.getEffectiveStrength();
                        }
                    }
                }
            }

            return weakerTotal;
        }

        private float getStrongerTotalForFaction(FactionAPI faction) {
            List<CampaignFleetAPI> visible = Misc.getVisibleFleets(fleet, false);
            float strongerTotal = 0f;
            for (CampaignFleetAPI other : visible) {
                if ((fleet.getAI() != null) && faction.isHostileTo(other.getFaction())) {
                    EncounterOption option = fleet.getAI().pickEncounterOption(null, other, true);
                    float dist = Misc.getDistance(fleet.getLocation(), other.getLocation());
                    VisibilityLevel level = other.getVisibilityLevelTo(fleet);
                    boolean seesComp = level == VisibilityLevel.COMPOSITION_AND_FACTION_DETAILS
                            || level == VisibilityLevel.COMPOSITION_DETAILS;
                    if ((dist < 800f) && seesComp) {
                        if ((option == EncounterOption.DISENGAGE) || (option == EncounterOption.HOLD_VS_STRONGER)) {
                            strongerTotal += other.getEffectiveStrength();
                        }
                    }
                }
            }

            return strongerTotal;
        }
    }

    private static enum GMDAFleetType {

        FRIGATES, DESTROYERS, CRUISERS, CAPITALS, CARRIERS, PHASE, BALANCED
    }
}
