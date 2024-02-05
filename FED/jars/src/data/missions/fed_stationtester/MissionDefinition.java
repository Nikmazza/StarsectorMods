package data.missions.fed_stationtester;

//All credit goes to DR for this mission script.

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class MissionDefinition implements MissionDefinitionPlugin {

    public static final List<String> PLAYER_STATIONS = new ArrayList<>();
    public static final List<String> ENEMY_FACTIONS = new ArrayList<>();
    public static final List<String> PLANETS = new ArrayList<>();

    private static final Random rand = new Random();
    private static int size = 30;
    private static boolean first = true;

    private static String playerStation;
    private static int playerStationIndex;
    private static String enemyFaction;
    private static int enemyFactionIndex;
    private static int enemyQuality;
    private static boolean balanceFleets;
    private static boolean boostTime;
    private static boolean autoshit;

    private static long bestEnemyFleetSeed = 0L;
    private static float bestDistance = Float.MAX_VALUE;

    static {
        PLAYER_STATIONS.add("fed_station_1_standard");
        PLAYER_STATIONS.add("fed_station_2_standard");
        PLAYER_STATIONS.add("fed_station_3_standard");
        PLAYER_STATIONS.add("station1_Standard");
        PLAYER_STATIONS.add("station2_Standard");
        PLAYER_STATIONS.add("station3_Standard");
        PLAYER_STATIONS.add("station1_midline_Standard");
        PLAYER_STATIONS.add("station2_midline_Standard");
        PLAYER_STATIONS.add("station3_midline_Standard");
        PLAYER_STATIONS.add("station1_hightech_Standard");
        PLAYER_STATIONS.add("station2_hightech_Standard");
        PLAYER_STATIONS.add("station3_hightech_Standard");
        PLAYER_STATIONS.add("remnant_station2_Damaged");
        PLAYER_STATIONS.add("remnant_station2_Standard");

        ENEMY_FACTIONS.add("everything");
        ENEMY_FACTIONS.add(Factions.HEGEMONY);
        ENEMY_FACTIONS.add(Factions.DIKTAT);
        ENEMY_FACTIONS.add(Factions.INDEPENDENT);
        ENEMY_FACTIONS.add(Factions.LIONS_GUARD);
        ENEMY_FACTIONS.add(Factions.LUDDIC_CHURCH);
        ENEMY_FACTIONS.add(Factions.LUDDIC_PATH);
        ENEMY_FACTIONS.add(Factions.PERSEAN);
        ENEMY_FACTIONS.add(Factions.PIRATES);
        ENEMY_FACTIONS.add(Factions.SCAVENGERS);
        ENEMY_FACTIONS.add(Factions.TRITACHYON);
        ENEMY_FACTIONS.add(Factions.DERELICT);
        ENEMY_FACTIONS.add(Factions.REMNANTS);
        //ENEMY_FACTIONS.add("interstellarimperium");
        //ENEMY_FACTIONS.add("ii_imperial_guard");
	ENEMY_FACTIONS.add("star_federation");
    }

    private void init() {
        for (PlanetSpecAPI spec : Global.getSettings().getAllPlanetSpecs()) {
            PLANETS.add(spec.getPlanetType());
        }
    }

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        boolean refreshEnemy = false;

        if (first || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            init();
            first = false;
            playerStation = PLAYER_STATIONS.get(0);
            playerStationIndex = PLAYER_STATIONS.indexOf(playerStation);
            enemyFaction = ENEMY_FACTIONS.get(0);
            enemyFactionIndex = ENEMY_FACTIONS.indexOf(enemyFaction);
            size = 30;
            enemyQuality = 125;
            balanceFleets = true;
            boostTime = true;
            autoshit = false;
            refreshEnemy = true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            refreshEnemy = true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            size = Math.max(size - 1, 1);
            refreshEnemy = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            size = Math.min(size + 1, 250);
            refreshEnemy = true;
        }

        float width = 18000f;
        float height = 18000f;

        api.initFleet(FleetSide.PLAYER, "A", FleetGoal.ATTACK, true, size / 8);
        api.initFleet(FleetSide.ENEMY, "B", FleetGoal.ATTACK, true, size / 8);

        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            playerStationIndex = (playerStationIndex + 1) % PLAYER_STATIONS.size();
            playerStation = PLAYER_STATIONS.get(playerStationIndex);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
            enemyFactionIndex = (enemyFactionIndex + 1) % ENEMY_FACTIONS.size();
            enemyFaction = ENEMY_FACTIONS.get(enemyFactionIndex);
            refreshEnemy = true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            enemyQuality = Math.max(enemyQuality - 5, -50);
            refreshEnemy = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            enemyQuality = Math.min(enemyQuality + 5, 150);
            refreshEnemy = true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
            balanceFleets = !balanceFleets;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
            boostTime = !boostTime;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_GRAVE)) {
            autoshit = !autoshit;
            refreshEnemy = true;
        }

        float enemySize = size * 5f;

        api.setFleetTagline(FleetSide.PLAYER, playerStation);
        api.setFleetTagline(FleetSide.ENEMY, enemyFaction + " (" + Math.round(enemySize) + " points) (Q " + enemyQuality + "%)");

        int reps = 1;
        if (refreshEnemy) {
            bestDistance = Float.MAX_VALUE;
            if (balanceFleets) {
                reps = 1000;
            }
        }

        FleetMemberAPI playerStationMember = Global.getFactory().createFleetMember(FleetMemberType.SHIP, playerStation);

        if (playerStationMember == null) {
            return;
        }

        playerStationMember.getRepairTracker().setCR(1f);

        CampaignFleetAPI bestEnemyFleet = null;
        for (int i = 0; i < reps; i++) {
            CampaignFleetAPI enemyFleet;
            long enemyFleetSeed;
            if ((i == 0) || refreshEnemy) {
                MarketAPI market = Global.getFactory().createMarket("fake", "fake", 5);
                market.getStability().modifyFlat("fake", 10000);
                market.setFactionId(enemyFaction);
                SectorEntityToken token = Global.getSector().getHyperspace().createToken(0, 0);
                market.setPrimaryEntity(token);
                market.getStats().getDynamic().getMod(Stats.FLEET_QUALITY_MOD).modifyFlat("fake", FleetFactoryV3.BASE_QUALITY_WHEN_NO_MARKET);
                market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat("fake", 1f);
                FleetParamsV3 params = new FleetParamsV3(market,
                        new Vector2f(0, 0),
                        enemyFaction,
                        enemyQuality / 100f, // qualityOverride
                        "missionFleet",
                        enemySize, // combatPts
                        0f, // freighterPts
                        0f, // tankerPts
                        0f, // transportPts
                        0f, // linerPts
                        0f, // utilityPts
                        0f); // qualityMod
                params.withOfficers = false;
                params.ignoreMarketFleetSizeMult = true;
                params.forceAllowPhaseShipsEtc = true;
                params.modeOverride = ShipPickMode.PRIORITY_THEN_ALL;

                if (refreshEnemy) {
                    enemyFleetSeed = rand.nextLong();
                } else {
                    enemyFleetSeed = bestEnemyFleetSeed;
                }
                params.random = new Random(enemyFleetSeed);

                enemyFleet = FleetFactoryV3.createFleet(params);
                if (!refreshEnemy) {
                    bestEnemyFleet = enemyFleet;
                }
            } else {
                enemyFleet = bestEnemyFleet;
                enemyFleetSeed = bestEnemyFleetSeed;
            }

            if (enemyFleet == null) {
                continue;
            }

            float targetFP = enemySize;

            float enemyFP = 0f;
            for (FleetMemberAPI member : enemyFleet.getFleetData().getMembersInPriorityOrder()) {
                enemyFP += member.getFleetPointCost();
            }

            float distance = Math.abs(enemyFP - targetFP);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestEnemyFleetSeed = enemyFleetSeed;
                bestEnemyFleet = enemyFleet;
            }

            if (Math.round(distance) <= 0) {
                break;
            }
        }

        if (bestEnemyFleet == null) {
            return;
        }

        api.addBriefingItem("Incorrectness: " + Math.round(bestDistance));

        if (boostTime) {
            api.addBriefingItem("Time acceleration applied!");
        }

        if (autoshit) {
            api.addBriefingItem("Crappy autofit enabled!");
        }

        api.addFleetMember(FleetSide.PLAYER, playerStationMember);

        if (refreshEnemy) {
            DefaultFleetInflaterParams p = new DefaultFleetInflaterParams();
            p.quality = enemyQuality / 100f;
            p.seed = MathUtils.getRandom().nextLong();
            p.mode = ShipPickMode.PRIORITY_THEN_ALL;
            p.allWeapons = !autoshit;

            DefaultFleetInflater inflater = new DefaultFleetInflater(p);
            inflater.inflate(bestEnemyFleet);
        }

        for (FleetMemberAPI member : bestEnemyFleet.getFleetData().getMembersInPriorityOrder()) {
            api.addFleetMember(FleetSide.ENEMY, member);
        }

        api.initMap(-width / 2f, width / 2f, -height / 2f, height / 2f);

        int numNebula = 15;
        boolean inNebula = Math.random() > 0.5;
        if (inNebula) {
            numNebula = 100;
        }

        for (int i = 0; i < numNebula; i++) {
            float x = (float) Math.random() * width - width / 2;
            float y = (float) Math.random() * height - height / 2;
            float radius = 100f + (float) Math.random() * 400f;
            if (inNebula) {
                radius += 100f + 500f * (float) Math.random();
            }
            api.addNebula(x, y, radius);
        }

        float numAsteroidsWithinRange = Math.max(0f, MathUtils.getRandomNumberInRange(-20f, 20f));
        int numAsteroids = Math.min(400, (int) ((numAsteroidsWithinRange + 1f) * 20f));

        api.addAsteroidField(0, 0, (float) Math.random() * 360f, width, 20f, 70f, numAsteroids);

        int numRings = 0;
        if (Math.random() > 0.75) {
            numRings++;
        }
        if (Math.random() > 0.75) {
            numRings++;
        }
        if (numRings > 0) {
            int numRingAsteroids = (int) (numRings * 300 + (numRings * 600f) * (float) Math.random());
            if (numRingAsteroids > 1500) {
                numRingAsteroids = 1500;
            }
            api.addRingAsteroids(0, 0, (float) Math.random() * 360f, width, 100f, 200f, numRingAsteroids);
        }

        String planet = PLANETS.get((int) (Math.random() * PLANETS.size()));
        float radius = 25f + (float) Math.random() * (float) Math.random() * 500f;

        api.addPlanet(0, 0, radius, planet, 0f, true);
        if (planet.contentEquals("wormholeUnder")) {
            api.addPlanet(0, 0, radius, "wormholeA", 0f, true);
            api.addPlanet(0, 0, radius, "wormholeB", 0f, true);
            api.addPlanet(0, 0, radius, "wormholeC", 0f, true);
        }

        api.getContext().aiRetreatAllowed = false;
        api.getContext().enemyDeployAll = true;
        api.getContext().fightToTheLast = true;
        api.getContext().setStandoffRange(6000f);

        if (boostTime) {
            api.addPlugin(new BaseEveryFrameCombatPlugin() {
                @Override
                public void init(CombatEngineAPI engine) {
                    engine.getContext().aiRetreatAllowed = false;
                    engine.getContext().enemyDeployAll = true;
                    engine.getContext().fightToTheLast = true;
                }

                @Override
                public void advance(float amount, List<InputEventAPI> events) {
                    if (Global.getCombatEngine().isPaused()) {
                        return;
                    }

                    float trueFrameTime = Global.getCombatEngine().getElapsedInLastFrame();
                    float trueFPS = 1 / trueFrameTime;
                    float newTimeMult = Math.max(1f, trueFPS / 30f);
                    Global.getCombatEngine().getTimeMult().modifyMult("ii_tester", newTimeMult);
                }
            });
        }
    }
}
