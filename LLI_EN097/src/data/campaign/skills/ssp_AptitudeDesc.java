package data.campaign.skills;

import com.fs.starfarer.api.characters.DescriptionSkillEffect;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ssp_AptitudeDesc {
    public static class Level1 implements DescriptionSkillEffect {
        public String getString() {
            //return "The maximum level of all skills governed by this aptitude is limited to the level of the aptitude.";
            return BaseIntelPlugin.BULLET + "此系列技能是由你在英仙座星区的冒险中邂逅的同伴提供的强力支援技能\n"
                    +BaseIntelPlugin.BULLET + "这些技能无法通过常规方式获取和移除，需要完成特定的条件获取这些技能\n";
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
