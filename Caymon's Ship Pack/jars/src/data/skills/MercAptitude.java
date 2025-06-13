package data.skills;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import second_in_command.SCData;
import second_in_command.specs.SCAptitudeSection;
import second_in_command.specs.SCBaseAptitudePlugin;

public class MercAptitude extends SCBaseAptitudePlugin {

    //The ID of the skill that is always active
    @Override
    public String getOriginSkillId() {
        return "sc_veteranmods";
    }

    //Determines which skills are added and how they are sectioned off in the UI
    @Override
    public void createSections() {

        //Parameters:
        // - canChooseMultiple: Can only one skill be selected from this section?
        // - requiredPreviousSkills: How many skills in previous sections are required for accessing this one?
        // - soundId what skill is played when clicking on skills from this section?
        SCAptitudeSection section1 = new SCAptitudeSection(true, 0, "leadership1");
        section1.addSkill("sc_coordinatedstrikeattack");
        section1.addSkill("sc_overhauledarmorstructure");
        section1.addSkill("sc_escortduty");
        section1.addSkill("sc_mechcraft");
        section1.addSkill("sc_wellequipped");
        section1.addSkill("sc_veteranstandard");
        addSection(section1); // Finalize the section by adding it.

        SCAptitudeSection section2 = new SCAptitudeSection(true, 2, "leadership2");
        section2.addSkill("sc_raptorpackdoctrine");
        section2.addSkill("sc_divideandconquer");
        addSection(section2);

        SCAptitudeSection section3 = new SCAptitudeSection(false, 4, "leadership3");
        section3.addSkill("sc_cyberneticaugmentation");
        section3.addSkill("sc_strengthofthepackisthewolf");
        addSection(section3);

    }

    //Chance for the aptitude to appear on NPC fleets.
    //Lower value is rarer, higher is more common.
    //Should be around 1 on average.
    @Override
    public Float getNPCFleetSpawnWeight(SCData data, CampaignFleetAPI fleet) {
        return 0.5f;
    }
}
