package mmm.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.missions.cb.*;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.unkrig.commons.nullanalysis.NotNull;
import mmm.MyPair;
import mmm.Utils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.magiclib.util.MagicSettings;

import java.awt.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MmmMilitaryCustomBounty extends MilitaryCustomBounty {
    private static final String MOD_ID = Utils.MOD_ID;
    private static final Logger log = Global.getLogger(MmmMilitaryCustomBounty.class);
    static {
        if (MagicSettings.getBoolean(MOD_ID, "MmmDebug")) {
            log.setLevel(Level.ALL);
        }
    }

    // Strings and constants
    public static final String DIFFICULTY_KEY = "$mmm_mcb_difficulty";
    public static final String MULTIPLIERS_KEY_PREFIX = "$mmm_mcb_multipliers_";
//    public static final String SKIP_UPDATE_DIFFICULTY = "mmm_mcb_skip_update_difficulty";
    // Multiplies omega ships's fleet points with this value.
    public static final float OMEGA_MULTIPLIER = 3f;

    // Settings
    public static int MAX_DIFFICULTY;
    // Limits the multiplier used to scale up/down fleets due to difficulty of bounty faction.
    public static float MAX_DIFFICULTY_MULTIPLIER = 1.75f;
    public static float MIN_DIFFICULTY_MULTIPLIER = 0.5f;

    // To improve ship variety, we lump all military ships together by size.
    public enum MyShipRole {
        CIVILIAN,
        FREIGHTER,
        TANKER,
        LINER,
        MILITARY
    }

    static public class CustomData {
        public DifficultyChoice choice;
        public float multiplier;
        public int difficulty;
        // Scaled difficulty not subject to vanilla max. Vanilla difficulty is stored in CustomBountyData.difficulty
        public int scaledDifficulty;
        // Scaled difficulty subject to vanilla max.
        public int vanillaDifficulty;
        public String id;
        public Float multiplierUpdate = null;

        CustomData(DifficultyChoice choice, float multiplier, int difficulty, int scaledDifficulty,
                   int vanillaDifficulty, String id) {
            this.choice = choice;
            this.multiplier = multiplier;
            this.difficulty = difficulty;
            this.scaledDifficulty = scaledDifficulty;
            this.vanillaDifficulty = vanillaDifficulty;
            this.id = id;
        }

        @Override
        public String toString() {
            return MessageFormat.format("choice={0}, multiplier=({1}, {2}), difficulty=({3}, {4}s, {5}v)",
                    choice, multiplier, multiplierUpdate, difficulty, scaledDifficulty, vanillaDifficulty);
        }
    }

    public static Object getCustomMapKey() {
        return MmmMilitaryCustomBounty.class;
    }

    public static class RoleToVariants {
        // (Ship Role, Hull Size) -> Variants; never null.
        @NotNull Map<MyPair<MyShipRole, ShipAPI.HullSize>, Set<String>> mappings ;
        @NotNull Set<String> allVariants;

        RoleToVariants(@NotNull Map<MyPair<MyShipRole, ShipAPI.HullSize>, Set<String>> mappings) {
            this.mappings = mappings;
            this.allVariants = mappings.values().stream().flatMap(Set::stream).collect(Collectors.toUnmodifiableSet());
        }
    }

    // Returns all ship variants for this faction categorized by their role and size.
    public static @NotNull RoleToVariants getRoleToVariants(FactionAPI faction) {
        Set<String> allVariantIds = new HashSet<>();
        for (String role : Utils.allShipRoles()) {
            allVariantIds.addAll(faction.getVariantsForRole(role));
        }

        Map<MyPair<MyShipRole, ShipAPI.HullSize>, Set<String>> roleToVariantIds = new HashMap<>();
        for (String variantId : allVariantIds) {
            ShipVariantAPI variant = Global.getSettings().getVariant(variantId);
            if (variant == null) continue;  // Sanity check

            MyPair<MyShipRole, ShipAPI.HullSize> key = new MyPair<>(getMyShipRole(variant), variant.getHullSize());
            roleToVariantIds.computeIfAbsent(key, k -> new HashSet<>()).add(variantId);
        }

        return new RoleToVariants(roleToVariantIds);
    }

    private static MyShipRole getMyShipRole(ShipVariantAPI variant) {
        MyShipRole role;
        if (!variant.isCivilian()) {
            role = MyShipRole.MILITARY;
        } else if (variant.isFreighter()) {
            role = MyShipRole.FREIGHTER;
        } else if (variant.isTanker()) {
            role = MyShipRole.TANKER;
        } else if (variant.isLiner()) {
            role = MyShipRole.LINER;
        } else {
            role = MyShipRole.CIVILIAN;
        }
        return role;
    }

    // Helper for getReplacementVariants; returns null if empty
    public static Set<String> lookupVariants(RoleToVariants roleToVariants, String variantId) {
        // Lookup the ship roles for this variant so we can lookup the mapping.
        ShipVariantAPI variant = Global.getSettings().getVariant(variantId);
        if (variant == null) return null;  // Sanity check

        Set<String> variantIds = roleToVariants.mappings.get(new MyPair<>(getMyShipRole(variant), variant.getHullSize()));
        if (variantIds != null && !variantIds.isEmpty()) return variantIds;
        return null;
    }

    // Returns the set of replacement variants for the provided variant. A variant is eligible if it has the same
    // role, size, and faction as the input variant. Never returns null or empty.
    public static Set<String> getReplacementVariants(RoleToVariants roleToVariants, Collection<RoleToVariants> all,
                                                     String factionId, String variantId,
            ShipAPI.HullSize size) {
        Set<String> variantIds = lookupVariants(roleToVariants, variantId);

        // If no variants can be found, try it with all other factions and pick the smallest set.
        if (variantIds == null) {
            log.debug(MessageFormat.format(
                    "replacements not found for faction={0}, variant={1}, size={2}; looking in other factions",
                    factionId, variantId, size));

            List<Set<String>> allVariantIds = new ArrayList<>();
            for (RoleToVariants curr : all) {
                Set<String> variants = lookupVariants(curr, variantId);
                if (variants != null && !variants.isEmpty()) {
                    allVariantIds.add(variants);
                }
            }

            variantIds = allVariantIds.stream().min((o1, o2) -> o1.size() - o2.size())
                    .orElse(null);
        }

        // If still no variants is found, fallback by returning the original variant.
        return variantIds != null ? variantIds : Collections.singleton(variantId);
    }

    protected String getFactionId(CustomBountyCreator creator, CampaignFleetAPI fleet) {
        if (creator instanceof CBDeserter) {
            return getPerson().getFaction().getId();
        } else if (creator instanceof CBDerelict) {
            return Factions.DERELICT;
        } else if (creator instanceof CBMerc) {
            return Factions.MERCENARY;
        } else if (creator instanceof CBPather) {
            return Factions.LUDDIC_PATH;
        } else if (creator instanceof CBPirate) {
            return Factions.PIRATES;
        } else if (creator instanceof CBRemnantPlus || creator instanceof CBRemnant) {
            return Factions.REMNANTS;
        } else {
            return fleet.getFaction() != null ? fleet.getFaction().getId() : null;
        }
    }

    // Replaces pickDifficulty to return difficulty greater than 10.
    protected Map<DifficultyChoice, Integer> myPickDifficulties(Random random) {
        AggregateBountyData d = getAggregateData();
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (memory == null || d == null) return null;  // Sanity check

        int total = d.completedDifficulty.stream().mapToInt(Integer::intValue).sum();
        int vanillaDifficulty = d.completedDifficulty.isEmpty() ? 0 :
                total / Math.max(d.completedDifficulty.size(), NUM_TO_TRACK_FOR_DIFFICULTY);

        Integer stored = (Integer) memory.get(DIFFICULTY_KEY);

        int baseDifficulty = Math.min(MAX_DIFFICULTY,
                Math.max(0, Math.max(vanillaDifficulty, stored == null ? 0 : stored)));
        int difficultyMax;
        if (baseDifficulty > 10) {
            difficultyMax = Math.min(MAX_DIFFICULTY, (int) (baseDifficulty * 1.3f));
        } else {
            difficultyMax = Math.min(MAX_DIFFICULTY, Math.max(4, baseDifficulty + 3));
        }
        int difficultyMin = Math.max(0, Math.min(baseDifficulty - 3, (int) (baseDifficulty / 2f)));

        int totalDifficulties = difficultyMax - difficultyMin + 1;

//        MyPair<Integer, Integer> lowRange;
//        MyPair<Integer, Integer> normalRange;
//        MyPair<Integer, Integer> highRange;
        List<MyPair<Integer, Integer>> ranges = new ArrayList<>();
        if (totalDifficulties <= 9) {
            // If there are 9 difficulty levels or less, divide them evenly into 3 buckets, prefer normal to low to high.
            int size = totalDifficulties / 3;
            int[] sizes;
            if (totalDifficulties % 3 == 1) {
                sizes = new int[]{size, size + 1, size};
            } else if (totalDifficulties % 3 == 2) {
                sizes = new int[]{size + 1, size + 1, size};
            } else {
                sizes = new int[]{size, size, size};
            }

            int rangeStart = difficultyMin;
            for (int s : sizes) {
                ranges.add(new MyPair<>(rangeStart, rangeStart + s - 1));
                rangeStart += s;
            }
        } else {
            // Otherwise assign low/normal/high to 3 difficulties each, with normal centered around base.
            int normalHigh = Math.min(difficultyMax - 3, baseDifficulty + 1);
            ranges.add(new MyPair<>(difficultyMin, difficultyMin + 2));
            ranges.add(new MyPair<>(normalHigh - 2, normalHigh));
            ranges.add(new MyPair<>(difficultyMax - 2, difficultyMax));
        }
        assert ranges.get(0).one == difficultyMin && ranges.get(2).two == difficultyMax &&
               ranges.get(0).two < ranges.get(1).one && ranges.get(1).two < ranges.get(2).one
               : MessageFormat.format("min={0}, max={1}, ranges={2}", difficultyMin, difficultyMax, ranges);

        int[] difficulties = new int[3];
        for (int i = 0; i < 3; ++i) {
            difficulties[i] = random.nextInt(ranges.get(i).one, ranges.get(i).two + 1);
        }
        Map<DifficultyChoice, Integer> result = new LinkedHashMap<>();
        result.put(DifficultyChoice.LOW, difficulties[0]);
        result.put(DifficultyChoice.NORMAL, difficulties[1]);
        result.put(DifficultyChoice.HIGH, difficulties[2]);

        log.debug(MessageFormat.format("myPickDifficulty={0}, base={1}, min={2}, max={3}, ranges={4}",
                result, baseDifficulty, difficultyMin, difficultyMax, ranges));
        return result;
    }

    // Replaces pickCreator to increase difficulty maximum and change threshold/probToSkip computations and returns
    // a list sorted by priority instead of a single creator.
    // In addition, alter getFrequency result so that CBRemnantPlus is repeatable.
    public List<Pair<CustomData, CustomBountyCreator>> myGetCreators(int difficulty, DifficultyChoice choice) {
        WeightedRandomPicker<Pair<CustomData, CustomBountyCreator>> picker = new WeightedRandomPicker<>(genRandom);

        // 0.5 minimum quality if player faction; scale quality from 0.5 to 1 instead of 0 to 1
        float quality = getQuality();
        if (getPerson().getFaction().isPlayerFaction()) {
            quality = 0.5f + quality / 2;
        }

        // Note that in vanilla: frequency, threshold (getThresholdNotHigh)
        // pirates:  10, 5
        // path:      5, 5
        // deserter: 10, 8
        // mercenary: 5,
        // station:   5,
        // derelict:  5,
        // remnant:   5,
        // remnant+: 10,
        // remnantS:  5,

        // Work around for a bug in SOTF
        Collection<CustomBountyCreator> uniqueCreators = getCreators().stream().collect(Collectors.toMap(
                CustomBountyCreator::getId, Function.identity(),
                (cr0, cr1) -> cr0, TreeMap::new)).values();
        for (CustomBountyCreator curr : uniqueCreators) {
            float multiplier;
            float frequency = -1f;

            if (curr instanceof CBRemnantPlus) {
                multiplier = CBStats.REMNANT_PLUS_MULT;
                // Makes up for the fact that now it takes much higher difficulty for this mission to show up.
                frequency = 10f;
            } else if (curr instanceof CBEnemyStation) {
                multiplier = CBStats.ENEMY_STATION_MULT;
                frequency = 3f;
            } else if (curr instanceof CBRemnantStation) {
                multiplier = CBStats.REMNANT_STATION_MULT;
                frequency = 3f;
            } else if (curr instanceof CBPirate) {
                multiplier = CBStats.PIRATE_MULT;
                frequency = 5f;
            } else if (curr instanceof CBPather) {
                multiplier = CBStats.PATHER_MULT;
                frequency = 5f;
            } else if (curr instanceof CBDeserter) {
                multiplier = CBStats.DESERTER_MULT;
                frequency = 7.5f;  // Because you can only get it from the same faction.
            } else if (curr instanceof CBDerelict) {
                multiplier = CBStats.DERELICT_MULT;
                frequency = 5f;
            } else if (curr instanceof CBMerc) {
                multiplier = CBStats.MERC_MULT;
                frequency = 5f;
            } else if (curr instanceof CBRemnant) {
                multiplier = CBStats.REMNANT_MULT;
                frequency = 5f;
            } else {
                // If this is not a recognized vanilla class, then use the multiplier reverse engineered from baseRewards.
                final String KEY = getMultiplierKey(curr.getId());
                if (Global.getSector().getPersistentData().get(KEY) instanceof MyPair<?, ?> existing &&
                        existing.one instanceof Float estimated) {
                    multiplier = estimated;
                } else {
                    multiplier = 1f;
                }
            }

            // Multiplier can scale difficulty up (pirates/path) or down (remnant, mercenary etc).
            multiplier = Math.max(MIN_DIFFICULTY_MULTIPLIER, Math.min(multiplier, MAX_DIFFICULTY_MULTIPLIER));
            int scaledDifficulty = Math.round(difficulty / multiplier);
            if (curr.getMinDifficulty() > scaledDifficulty) {
                continue;
            }

            // Obey max difficulty only if it's not vanilla max, which does not exist in vanilla.
            if (curr.getMaxDifficulty() < CustomBountyCreator.MAX_DIFFICULTY) {
                if (curr.getMaxDifficulty() < scaledDifficulty) {
                    continue;
                }
            }

            int vanillaDifficulty = Math.min(Math.max(curr.getMinDifficulty(), scaledDifficulty),
                    curr.getMaxDifficulty());

            // Don't increase difficulty (past vanilla) the first time you take a mission for this creator
            int actualDifficulty = difficulty;
            if (curr instanceof BaseCustomBountyCreator cr) {
                if (cr.getNumCompletedGlobal() <= 0) {
                    scaledDifficulty = Math.min(vanillaDifficulty, scaledDifficulty);
                    actualDifficulty = Math.round(scaledDifficulty * multiplier);
                }
            }

            float vanillaFrequency = curr.getFrequency(this, vanillaDifficulty);
            if (vanillaFrequency <= 0f) {
                // Make remnant plus recurring instead of one-off.
                frequency = curr instanceof CBRemnantPlus ? 3f : 0f;
            } else if (curr instanceof CBPirate || curr instanceof CBPather) {
                // Reduce Pirate/Path frequency at high difficulty to make up for the threshold computation.
                if (scaledDifficulty >= 5) {
                    frequency = 3f;
                }
            } else if (frequency <= -1f) {
                // This isn't possible in vanilla but other mods might add more creators.
                frequency = vanillaFrequency;
            }

            CustomData customData = new CustomData(
                    choice, multiplier, actualDifficulty, scaledDifficulty, vanillaDifficulty, curr.getId());

            float probToSkip = (1.1f - quality) * (float) curr.getMinDifficulty() / CustomBountyCreator.MAX_DIFFICULTY;
            // Take the square of probToSkip to reduce its effect.
            float weight = probToSkip >= 1f ? 0f : frequency * (1f - probToSkip * probToSkip);
            picker.add(new Pair<>(customData, curr), weight);

//            log.debug(MessageFormat.format(
//                    "myPickCreator; class={0}, weight={1}, frequency=({2}, {3}v), quality={4}, probToSkip={5}, {6}",
//                    curr.getClass().getSimpleName(), weight, frequency, vanillaFrequency, quality, probToSkip, customData));
        }

        List<Pair<CustomData, CustomBountyCreator>> creators = new ArrayList<>();
        while (!picker.isEmpty()) {
            creators.add(picker.pickAndRemove());
        }
        return creators;
    }

    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        if (Factions.PIRATES.equals(createdAt.getFaction().getId())) {
            return false;
        }
        this.isBarEvent = barEvent;
        if (barEvent) {
            // Since findOrCreateGiver doesn't call findOrCreateGiver with option to delete quest giver on abort, this
            // work around is needed to ensure that it doesn't change the PRNG needed for MBM.
            Random saved = genRandom;
            genRandom = new Random(saved.nextInt() + createdAt.getId().hashCode());
            createBarGiver(createdAt);
            genRandom = saved;
        }

        PersonAPI person = getPerson();
        if (person == null || genRandom == null) return false;

        String id = getMissionId();
        if (!setPersonMissionRef(person, "$" + id + "_ref")) {
            return false;
        }

        setStartingStage(Stage.BOUNTY);
        setSuccessStage(Stage.COMPLETED);
        setFailureStage(Stage.FAILED);
        addNoPenaltyFailureStages(Stage.FAILED_NO_PENALTY);

        connectWithMemoryFlag(Stage.BOUNTY, Stage.COMPLETED, person, "$" + id + "_completed");
        connectWithMemoryFlag(Stage.BOUNTY, Stage.FAILED, person, "$" + id + "_failed");

        addTag(Tags.INTEL_BOUNTY);

        // Faction ID -> RoleToVariants
        Map<String, RoleToVariants> allRoleToVariants = null;

        dataLow = dataNormal = dataHigh = null;
        Map<DifficultyChoice, Integer> difficulties = myPickDifficulties(genRandom);
        if (difficulties == null) return false;  // Sanity check
        for (Map.Entry<DifficultyChoice, Integer> entry : difficulties.entrySet()) {
            DifficultyChoice choice = entry.getKey();
            List<Pair<CustomData, CustomBountyCreator>> creators = myGetCreators(entry.getValue(), choice);
            if (creators == null || creators.isEmpty()) return false;  // Sanity check

            CustomData customData = null;
            creator = null;
            data = null;
            for (Pair<CustomData, CustomBountyCreator> curr : creators) {
                customData = curr.one;
                creator = curr.two;
                // Create the bounty with vanilla difficulty and attempt to adjust the fleet after.
                data = creator.createBounty(createdAt, this, customData.vanillaDifficulty, Stage.BOUNTY);
                if (data == null || data.fleet == null) {
                    log.error( "Failed to create bounty for creator=" + creator.getId());
                    continue;
                }
                data.customMap.put(getCustomMapKey(), customData);
                break;
            }

            if (creator == null || data == null || data.fleet == null) return false;

            int referenceReward = CBStats.getBaseBounty(customData.vanillaDifficulty, 1f, null);
            customData.multiplierUpdate = (float) data.baseReward / referenceReward;

            CampaignFleetAPI fleet = data.fleet;
            // Do not inflate the fleet here, as we want to wait until the new fleet members has been added to ensure
            // that the smod/dmods are added.

            if (choice == DifficultyChoice.LOW) {
                creatorLow = creator;
                dataLow = data;
            } else if (choice == DifficultyChoice.NORMAL) {
                creatorNormal = creator;
                dataNormal = data;
            } else {
                creatorHigh = creator;
                dataHigh = data;
            }

            String factionId = getFactionId(creator, fleet);
            log.debug(MessageFormat.format(
                    "creator={0}, faction={1}, {2}, isStation={3}, strength={4}",
                    creator.getId(), factionId, customData, fleet.isStationMode(), fleet.getEffectiveStrength()));

            if (factionId == null) continue;  // Sanity check

            // If our desired difficulty is greater than what vanilla supports, try to modify the fleet.
            if (customData.scaledDifficulty > customData.vanillaDifficulty) {
                // If we cannot modify the fleet, then use vanilla difficulty max as difficulty
                if (fleet.isStationMode() || data.system == null || data.system.getStar() == null) {
                    customData.difficulty = customData.vanillaDifficulty;
                    continue;
                }

                if (allRoleToVariants == null) {
                    allRoleToVariants = new HashMap<>();
                    for (FactionAPI faction : Global.getSector().getAllFactions()) {
                        RoleToVariants current = getRoleToVariants(faction);
                        if (!current.mappings.isEmpty()) {
                            allRoleToVariants.put(faction.getId(), current);
                        }
                    }
                }
                RoleToVariants roleToVariants = allRoleToVariants.get(factionId);
                RoleToVariants omegaRoleToVariants = allRoleToVariants.get(Factions.OMEGA);

                // Sanity check
                if (roleToVariants == null || omegaRoleToVariants == null) continue;

                List<FleetMemberAPI> omegaMembers = new ArrayList<>();
                FleetDataAPI fleetData = fleet.getFleetData();
                List<Set<String>> replacementSets = new ArrayList<>();
                float fp = 0f;

                for (FleetMemberAPI member : fleet.getMembersWithFightersCopy()) {
                    if (member.isFighterWing()) continue;

                    ShipVariantAPI variant = member.getVariant();
                    if (variant == null || variant.getHullVariantId() == null || variant.getHullSize() == null) {
                        continue;  // Sanity check
                    }

                    ShipAPI.HullSize size = variant.getHullSize();
                    String variantId = variant.getOriginalVariant() != null ?
                            variant.getOriginalVariant() : variant.getHullVariantId();
                    if (variantId == null) continue;  // sanity check

                    float currentFp = member.getFleetPointCost();

                    // Special handling for omega, since they have show up in RemnantPlus.
                    Set<String> replacements;
                    if (omegaRoleToVariants.allVariants.contains(variantId)) {
                        omegaMembers.add(member);
                        currentFp *= OMEGA_MULTIPLIER;
                        replacements = getReplacementVariants(omegaRoleToVariants, allRoleToVariants.values(),
                                Factions.OMEGA, variantId, size);
                    } else {
                        replacements = getReplacementVariants(roleToVariants, allRoleToVariants.values(),
                                factionId, variantId, size);
                    }
                    fp += currentFp;


//                    log.debug(MessageFormat.format(
//                            "getReplacementVariants: factionId={0}, variantId={1}, size={2}, fp={3}, replacements={4}",
//                            factionId, variantId, size, currentFp, replacements));
                    if (!replacements.isEmpty()) {
                        replacementSets.add(replacements);
                    }
                }

                float targetFp = fp * customData.scaledDifficulty / customData.vanillaDifficulty;
                float actualFp = fp;

                // Convert replacementSets to replacementPickers and shuffle them.
                Collections.shuffle(replacementSets, genRandom);
                List<WeightedRandomPicker<String>> replacementPickers = new ArrayList<>();
                for (Set<String> replacements : replacementSets) {
                    WeightedRandomPicker<String> picker = new WeightedRandomPicker<>(genRandom);
                    picker.addAll(replacements);
                    replacementPickers.add(picker);
                }

                int added = 0;
                while (actualFp < targetFp) {
                    for (WeightedRandomPicker<String> picker : replacementPickers) {
                        if (actualFp >= targetFp) break;
                        ++added;

                        // Randomly pick a replacement from one of the possibilities.
                        String variantId = picker.pick();
                        FleetMemberAPI addedMember = fleetData.addFleetMember(variantId);
//                        ShipVariantAPI addedVariant = addedMember.getVariant();
                        float replacementFp = addedMember.getFleetPointCost();

                        if (omegaRoleToVariants.allVariants.contains(variantId)) {
                            omegaMembers.add(addedMember);
                            replacementFp *= OMEGA_MULTIPLIER;
                        }

                        actualFp += replacementFp;
//                        log.debug(MessageFormat.format("Added variant={0}, fp={1}, name={2}",
//                                variantId, replacementFp, addedVariant.getFullDesignationWithHullName()));
                    }
                }

                // Now we resort the ships and ensure that the flagship is up front; also ensure omega is ahead.
                fleetData.sort();
                FleetMemberAPI flagship = fleet.getFlagship();
                List<FleetMemberAPI> order = OrbitalMissionBase.sortedFleet(fleetData.getMembersListCopy());

                Collections.reverse(omegaMembers);
                for (FleetMemberAPI omegaMember : omegaMembers) {
                    if (order.remove(omegaMember)) {
                        order.add(0, omegaMember);
                    }
                }
                if (order.remove(flagship)) {
                    order.add(0, flagship);
                }
                fleetData.sortToMatchOrder(order);
//                fleetData.setFlagship(flagship);
//                fleetData.setSyncNeeded();
                fleet.inflateIfNeeded();

                // Adjust rewards and difficulty based on actual fleet points added
                float ratio = actualFp / fp;
                int oldReward = data.baseReward;
                data.baseReward = OrbitalMissionBase.myGetRoundNumber(oldReward * ratio);
                customData.scaledDifficulty = Math.round(customData.vanillaDifficulty * ratio);
                customData.difficulty = Math.round(customData.scaledDifficulty * customData.multiplier);

                log.debug(MessageFormat.format(
                        "added {0} ships; class={1}, fp={2}, targetFp={3}, actualFp={4}, strength={5}, {6}, " +
                                "oldReward={7}, baseReward={8}",
                        added, creator.getId(), fp, targetFp, actualFp, fleet.getEffectiveStrength(), customData,
                        oldReward, data.baseReward));
            }
        }

        // Should not be needed but sanity check
        return dataLow != null && dataLow.fleet != null && dataNormal != null && dataNormal.fleet != null &&
                dataHigh != null && dataHigh.fleet != null;
    }

    @Override
    public void accept(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        // If we're dealing with a non-vanilla class, then assuming that they also call CBStats.getBaseBounty,
        // we can reverse engineer the multiplier from data.baseReward.
        for (CustomBountyCreator.CustomBountyData current : Arrays.asList(dataLow, dataNormal, dataHigh)) {
            if (current == null) continue;  // Sanity check

            if (current.customMap.get(getCustomMapKey()) instanceof CustomData customData &&
                    customData.multiplierUpdate != null) {
                Map<String, Object> persistent = Global.getSector().getPersistentData();
                final String KEY = getMultiplierKey(customData.id);
                MyPair<Float, Integer> newPair;
                if (persistent.get(KEY) instanceof MyPair<?, ?> existing &&
                        existing.one instanceof Float multiplier &&
                        existing.two instanceof Integer total) {
                    float newMultiplier = multiplier * total / (total + 1) + customData.multiplierUpdate / (total + 1);
                    newPair = new MyPair<>(newMultiplier, total + 1);
                } else {
                    newPair = new MyPair<>(customData.multiplierUpdate, 1);
                }
                persistent.put(KEY, newPair);
//                log.debug(MessageFormat.format("getPersistentData: {0}, {1}", KEY, newPair));
            }

        }

        super.accept(dialog, memoryMap);
    }

    @Override
    protected void endSuccessImpl(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.endSuccessImpl(dialog, memoryMap);
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (memory != null && data.customMap.get(getCustomMapKey()) instanceof CustomData customData) {
            int completedDifficulty = customData.difficulty;
            Integer d = (Integer) memory.get(DIFFICULTY_KEY);
            // Only difficulty if high, and only decrease if low.
            if (d == null || completedDifficulty > d && customData.choice == DifficultyChoice.HIGH ||
                    completedDifficulty < d && customData.choice == DifficultyChoice.LOW) {
                log.debug("difficulty set to " + completedDifficulty);
                memory.set(DIFFICULTY_KEY, completedDifficulty);
            }
        }
        // Allow contact from player faction at vanilla probability
        if (isBarEvent && getPerson().getFaction().isPlayerFaction()) {
            OrbitalMissionBase.addPotentialContact(this, dialog, 1f, 1f);
        }
    }

    @Override
    protected void endFailureImpl(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.endFailureImpl(dialog, memoryMap);
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (memory != null && data.customMap.get(getCustomMapKey()) instanceof CustomData customData) {
            int target = Math.max(0, Math.round(customData.difficulty / 1.3f));
            Integer d = (Integer) memory.get(DIFFICULTY_KEY);
            if (d == null || target < d) {
                memory.set(DIFFICULTY_KEY, target);
            }
        }
    }

    // Same as BaseCustomBountyCreator.addIntelAssessment, except that it displays more lines of ships
    protected void addIntelAssessment(TextPanelAPI text) {
        final float opad = 10f;
        final int maxRows = 5;
        final int cols = 7;
        final float iconSize = 440f / cols;
        final Color h = Misc.getHighlightColor();

        // How many rows of ships to display? Display 70% of the ships but only complete rows, and at least 1 row, no more
        // than 5 total rows.
        List<FleetMemberAPI> members = showData.fleet.getFleetData().getMembersListCopy().stream().filter(
                m -> !m.isFighterWing()).toList();

        int rows = Math.max(1, Math.min(maxRows,
                Math.min(members.size() / cols, (int) Math.ceil(members.size() * .7f / cols))));
        int size = Math.min(rows * cols, members.size());
        List<FleetMemberAPI> displayMembers = members.subList(0, size);
        if (!members.isEmpty()) {
            TooltipMakerAPI info = text.beginTooltip();
            info.setParaSmallInsignia();
            info.addPara(Misc.ucFirst(getPerson().getHeOrShe()) + " taps a data pad, and " +
                    "an intel assessment shows up on your tripad.", 0f);
            info.addShipList(cols, rows, iconSize, h, displayMembers, opad);

            int diff = members.size() - size;
            if (diff > 1) {
                info.addPara("The assessment notes that the fleet may contain upwards of %s other ships" +
                        " of lesser significance.", opad, h, String.valueOf(diff));
            } else if (diff > 0) {
                info.addPara("The assessment notes the fleet may contain several other ships" +
                        " of lesser significance.", opad);
            } else {
                info.addPara("It appears to contain complete information about the scope of the assignment.", opad);
            }
            text.addTooltip();
        }
    }

    @Override
    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params,
                                 Map<String, MemoryAPI> memoryMap) {

        if (showData != null && "showBountyAssessment".equals(action)) {
            if (showData.customMap.get(getCustomMapKey()) instanceof CustomData customData) {
//                if (customData.scaledDifficulty > customData.vanillaDifficulty) {
                    addIntelAssessment(dialog.getTextPanel());
                    return true;
//                }
            }
        }
        return super.callAction(action, ruleId, dialog, params, memoryMap);
    }


    public static String getMultiplierKey(String id) {
        return MULTIPLIERS_KEY_PREFIX + id;
    }
}
