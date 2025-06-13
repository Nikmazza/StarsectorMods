package Jaydee8652.JaydeePiracy.scripts.skills
import com.fs.starfarer.api.characters.DescriptionSkillEffect
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.util.Misc
import java.awt.Color

object jdp_AptitudeUnique {//Most of this doesn't matter, because the Aptitude itself is NPC only
    class Level0 : DescriptionSkillEffect {
        override fun getTextColor(): Color {
            return Misc.getTextColor()
        }

        override fun getString(): String {
            return BaseIntelPlugin.BULLET + "Unique skills here"
        }

        override fun getHighlights(): Array<String> {
            return arrayOf ("" + Misc.STORY + " point")
        }

        override fun getHighlightColors(): Array<Color> {
            return arrayOf(Misc.getStoryOptionColor())
        }

    }
}