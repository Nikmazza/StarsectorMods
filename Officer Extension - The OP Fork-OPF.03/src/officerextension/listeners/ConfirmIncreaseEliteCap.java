package officerextension.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import officerextension.Settings;
import officerextension.ui.OfficerUIElement;

public class ConfirmIncreaseEliteCap extends DialogDismissedListener {

    private final OfficerUIElement uiElement;
    private final OfficerDataAPI officerData;

    public ConfirmIncreaseEliteCap(OfficerUIElement uiElement) {
        this.uiElement = uiElement;
        this.officerData = uiElement.getOfficerData();
    }

    @Override
    public void trigger(Object... args) {
        // The second argument is 0 if confirmed, 1 if canceled
        int option = (int) args[1];
        if (option == 1) {
            return;
        }
        // Have to do the check again here, since the player can press space bar to confirm despite
        // the confirm button being disabled
        OfficerLevelupPlugin levelUpPlugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");
        int currentMaximum = levelUpPlugin.getMaxEliteSkills(officerData.getPerson());
        int newMaximum = currentMaximum + 1;
        if (Global.getSector().getPlayerStats().getStoryPoints() < currentMaximum) {
            return;
        }
        if (currentMaximum > 0) {
            Global.getSoundPlayer().playUISound("ui_char_spent_story_point_leadership", 1f, 1f);
            Global.getSector().getPlayerStats().spendStoryPoints(
                    currentMaximum,
                    true,
                    null,
                    true,
                    Settings.INCREASE_ELITE_CAP_BONUS_XP_FRACTION,
                    "Increased Officer Elite Skill Cap: " + officerData.getPerson().getNameString());
        }

        officerData.getPerson().getMemoryWithoutUpdate().set(Settings.OFFICER_ELITE_CAP, newMaximum);
        uiElement.recreate();

    }
}
