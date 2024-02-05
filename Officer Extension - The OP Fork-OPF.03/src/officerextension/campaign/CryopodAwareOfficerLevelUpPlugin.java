package officerextension.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.OfficerLevelupPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import officerextension.Settings;

public class CryopodAwareOfficerLevelUpPlugin extends OfficerLevelupPluginImpl {
    @Override
    public int getMaxLevel(PersonAPI person) {
        if (person != null && person.getMemoryWithoutUpdate().getBoolean(MemFlags.EXCEPTIONAL_SLEEPER_POD_OFFICER)) {
            if (person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_LEVEL_CAP) <= Global.getSettings().getInt("exceptionalSleeperPodsOfficerLevel")) {
                return Global.getSettings().getInt("exceptionalSleeperPodsOfficerLevel");
            }
            if (person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_LEVEL_CAP) > Global.getSettings().getInt("exceptionalSleeperPodsOfficerLevel")) {
                return person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_LEVEL_CAP);
            }

        }
        if (person != null && person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_LEVEL_CAP) <= super.getMaxLevel(person)) {
            return super.getMaxLevel(person);
        }
        if (person != null && person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_LEVEL_CAP) > super.getMaxLevel(person)) {
            return person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_LEVEL_CAP);
        }
        return super.getMaxLevel(person);
    }

    @Override
    public int getMaxEliteSkills(PersonAPI person) {
        if (person != null && person.getMemoryWithoutUpdate().getBoolean(MemFlags.EXCEPTIONAL_SLEEPER_POD_OFFICER)) {
            if (person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_ELITE_CAP) <= Global.getSettings().getInt("exceptionalSleeperPodsOfficerEliteSkills")) {
                return Global.getSettings().getInt("exceptionalSleeperPodsOfficerEliteSkills");
            }
            if (person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_ELITE_CAP) > Global.getSettings().getInt("exceptionalSleeperPodsOfficerEliteSkills")) {
                return person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_ELITE_CAP);
            }
        }
        if (person != null && person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_ELITE_CAP) <= super.getMaxEliteSkills(person)) {
            return super.getMaxEliteSkills(person);
        }
        if (person != null && person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_ELITE_CAP) > super.getMaxEliteSkills(person)) {
            return person.getMemoryWithoutUpdate().getInt(Settings.OFFICER_ELITE_CAP);
        }
        return super.getMaxEliteSkills(person);
    }
}
