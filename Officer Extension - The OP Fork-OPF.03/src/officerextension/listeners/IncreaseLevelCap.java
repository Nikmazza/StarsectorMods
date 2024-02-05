package officerextension.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.util.Misc;
import officerextension.Settings;
import officerextension.UtilReflection;
import officerextension.ui.Button;
import officerextension.ui.OfficerUIElement;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class IncreaseLevelCap extends ActionListener {

    private final OfficerUIElement uiElement;

    public IncreaseLevelCap(OfficerUIElement uiElement) {
        this.uiElement = uiElement;
    }

    @Override
    public void trigger(Object... args) {
        PersonAPI officerPerson = uiElement.getOfficerData().getPerson();
        StringBuilder confirmSB = new StringBuilder();
        List<String> highlights = new ArrayList<>();
        List<Color> colors = new ArrayList<>();
        confirmSB.append("Are you sure? ")
                .append(officerPerson.getNameString())
                .append(" (level ")
                .append(officerPerson.getStats().getLevel())
                .append(") will have their maximum level increased by 1.");
        String bonusXPPercent = (int) (100f * Settings.INCREASE_LEVEL_CAP_BONUS_XP_FRACTION) + "%";
        int numStoryPoints = Global.getSector().getPlayerStats().getStoryPoints();
        OfficerLevelupPlugin levelUpPlugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");
        int currentMaximum = levelUpPlugin.getMaxLevel(officerPerson);
        String storyPointOrPointsPlayer = numStoryPoints == 1 ? "story point" : "story points";
        String storyPointOrPointsCost = currentMaximum == 1 ? "story point" : "story points";
        confirmSB.append("\n\n")
                .append("Increasing level cap by 1 costs ")
                .append(currentMaximum)
                .append(" ")
                .append(storyPointOrPointsCost)
                .append(" due to their current maximum level of")
                .append(" ")
                .append(currentMaximum)
                .append(" and grants ")
                .append(bonusXPPercent)
                .append(" bonus experience.\n\n")
                .append("You have ")
                .append(numStoryPoints)
                .append(" ")
                .append(storyPointOrPointsPlayer)
                .append(".");
        highlights.add(currentMaximum + " " + storyPointOrPointsCost);
        colors.add(Misc.getStoryOptionColor());
        highlights.add("" + currentMaximum);
        colors.add(Misc.getStoryOptionColor());
        highlights.add(bonusXPPercent);
        colors.add(Misc.getStoryOptionColor());
        highlights.add("" + numStoryPoints);
        colors.add(numStoryPoints >= currentMaximum ? Misc.getStoryOptionColor() : Misc.getNegativeHighlightColor());
        ConfirmIncreaseLevelCap confirmListener = new ConfirmIncreaseLevelCap(uiElement);
        UtilReflection.ConfirmDialogData data = UtilReflection.showConfirmationDialog(
                confirmSB.toString(),
                "Increase Cap",
                "Never mind",
                650f,
                230f,
                confirmListener);
        if (data == null) {
            return;
        }
        LabelAPI label = data.textLabel;
        label.setHighlight(highlights.toArray(new String[0]));
        label.setHighlightColors(colors.toArray(new Color[0]));
        Button yesButton = data.confirmButton;
        if (numStoryPoints < currentMaximum) {
            yesButton.setEnabled(false);
        }
    }
}
