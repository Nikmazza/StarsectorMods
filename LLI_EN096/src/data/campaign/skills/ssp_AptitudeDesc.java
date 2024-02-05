package data.campaign.skills;

import com.fs.starfarer.api.characters.DescriptionSkillEffect;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ssp_AptitudeDesc {
    public static class Level1 implements DescriptionSkillEffect {
        public String getString() {
            //return "The maximum level of all skills governed by this aptitude is limited to the level of the aptitude.";
            return BaseIntelPlugin.BULLET + "These are powerful skills provided by acquaintances you've met in your exploration of the Sector.\n"
                    +BaseIntelPlugin.BULLET + "These skills cannot be added or removed like normal skills, with specific requirements to obtain them.\n";
            // pick one to unlock next tier
            // can wrap around
            // spend " + Misc.STORY + " points to make elite
        }
        public Color[] getHighlightColors() {
            Color h = Misc.getHighlightColor();
            Color s = Misc.getStoryOptionColor();
            return new Color[] {};
        }
        public String[] getHighlights() {
            return new String[] {};
        }
        public Color getTextColor() {
            return Misc.getTextColor();
            //return null;
        }
    }
}
