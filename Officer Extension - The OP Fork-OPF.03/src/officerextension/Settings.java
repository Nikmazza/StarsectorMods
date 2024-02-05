package officerextension;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Settings {

    public static float SUSPENDED_SALARY_FRACTION;
    public static int DEMOTE_OFFICER_SP_COST;
    public static float DEMOTE_BONUS_XP_FRACTION;
    public static String OFFICER_LEVEL_CAP = "$officerextension_Officer_Level_Cap";
    public static String OFFICER_ELITE_CAP = "$officerextension_Officer_Elite_Cap";
    public static float INCREASE_LEVEL_CAP_BONUS_XP_FRACTION;
    public static float INCREASE_ELITE_CAP_BONUS_XP_FRACTION;
    public static final List<String> OFFICER_SKILLS = new ArrayList<>();
    public static final List<String> LEARNABLE_OFFICER_SKILLS = new ArrayList<>();
    public static float FORGET_ELITE_BONUS_XP_FRACTION;
    public static float SUSPEND_OFFICER_COST_MULTIPLIER;
    public static boolean SHOW_COMMANDER_SKILLS;
    public static boolean SPLIT_COMMANDER_SKILLS;
    public final static Set<String> PERSISTENT_OFFICER_TAGS = new TreeSet<>();
    /** Unused -- only used pre 0.4.0 */
    public static final String SUSPENDED_OFFICERS_DATA_KEY = "officerextension_SuspendedOfficers";

    public static final String OFFICER_IS_SUSPENDED_KEY = "$officerextension_IsSuspended";
    public static final String SUSPENDED_OFFICERS_NODE = "node_id_suspended_officers";
    public static final String OFFICER_TAGS_DATA_KEY = "$officerextension_OfficerTags";

    //Loads all officer skills obtainable via level up into a list
    public static void loadOfficerSkills(){
        List<String> allSkills = Global.getSettings().getSkillIds();
        SkillSpecAPI skill;
        for(String id : allSkills){
            skill = Global.getSettings().getSkillSpec(id);
            if(skill.isCombatOfficerSkill() && !skill.isAptitudeEffect()){
                OFFICER_SKILLS.add(id);
                if (!skill.hasTag("npc_only")
                        && !skill.hasTag("player_only")
                        && !skill.hasTag("deprecated")){
                LEARNABLE_OFFICER_SKILLS.add(id);
                }
            }
        }
    }
    public static void load() {
        loadOfficerSkills();
        try {
            JSONObject json = Global.getSettings().loadJSON("officerextension_settings.json");
            SUSPENDED_SALARY_FRACTION = (float) json.getDouble("suspendedOfficerMonthlySalaryFraction");
            DEMOTE_OFFICER_SP_COST = json.getInt("demoteOfficerSPCost");
            DEMOTE_BONUS_XP_FRACTION = (float) json.getDouble("demoteOfficerBonusXPFraction");
            INCREASE_LEVEL_CAP_BONUS_XP_FRACTION = (float) json.getDouble("increaseLevelCapBonusXPFraction");
            INCREASE_ELITE_CAP_BONUS_XP_FRACTION = (float) json.getDouble("increaseEliteCapBonusXPFraction");
            FORGET_ELITE_BONUS_XP_FRACTION = (float) json.getDouble("forgetEliteSkillBonusXPFraction");
            SUSPEND_OFFICER_COST_MULTIPLIER = (float) json.getDouble("suspendOfficerCostMultiplier");
            SHOW_COMMANDER_SKILLS = json.getBoolean("shouldShowFleetCommanderSkills");
            SPLIT_COMMANDER_SKILLS = json.getBoolean("shouldSplitFleetCommanderSkills");
            PERSISTENT_OFFICER_TAGS.clear();
            JSONArray persistentTags = json.getJSONArray("officerFilterPersistentTags");
            for (int i = 0 ; i < persistentTags.length(); i++) {
                PERSISTENT_OFFICER_TAGS.add(persistentTags.getString(i));
            }
        }
        catch (Exception e) {
            Global.getLogger(Settings.class).error("Failure to load \"Officer Extension/officerextension_settings.json\"", e);
        }
    }
}
