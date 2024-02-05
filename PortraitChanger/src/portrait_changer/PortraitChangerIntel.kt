package portrait_changer

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CharacterDataAPI
import com.fs.starfarer.api.characters.FullName
import com.fs.starfarer.api.characters.FullName.Gender
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipLocation
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import lunalib.lunaExtensions.addLunaSpriteElement
import lunalib.lunaUI.elements.LunaElement
import lunalib.lunaUI.elements.LunaSpriteElement
import java.awt.Color

class PortraitChangerIntel : BaseIntelPlugin() {


    @Transient
    var panel: CustomPanelAPI? = null

    @Transient
    var element: TooltipMakerAPI? = null


    var isUafEnabled = Global.getSettings().modManager.isModEnabled("uaf")

    var uafSelected = true
    var gender: Gender = Gender.FEMALE

    var selectedPerson: PersonAPI = Global.getSector().playerPerson

    var lastScroller = 0f

    override fun getName(): String {
        return "Change Portrait"
    }

    override fun hasLargeDescription(): Boolean {
        return true
    }

    override fun createLargeDescription(panel: CustomPanelAPI?, width: Float, height: Float) {
        this.panel = panel
        recreatePanel()
    }

    fun recreatePanel()
    {
        if (panel == null) return
        if (element != null)
        {
            if (element!!.externalScroller != null)
            {
                element!!.removeComponent(element!!.externalScroller)
                panel!!.removeComponent(element!!.externalScroller)
            }
            panel!!.removeComponent(element)
        }

        var spacing = 0f

        element = panel!!.createUIElement(panel!!.position.width, panel!!.position.height, true)

        var selectorHeader = element!!.addSectionHeading("Select Character", Alignment.MID, 0f)

       /* var img = element!!.beginImageWithText(Global.getSector().characterData.person.portraitSprite, 96f)

        var player = Global.getSector().playerPerson
        img.addPara("Name: ${player.nameString}", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Name: ")

        element!!.addImageWithText(0f)*/

        spacing += selectorHeader.position.height
        spacing += 20f

        var people = listOf(Global.getSector().playerPerson) + Global.getSector().playerFleet.fleetData.officersCopy.map { it.person } + Global.getSector().characterData.admins.map { it.person }

        spacing = addCharacterSelector(people, spacing)
        element!!.addSpacer(5f)

        var portraitHeader = element!!.addSectionHeading("Select Portrait", Alignment.MID, 0f).position.inTL(0f, spacing)

        element!!.addSpacer(portraitHeader.height + 10f)
        spacing += portraitHeader.height + 10f

        var lastElement: LunaElement? = null
        if (isUafEnabled)
        {
            var filter1 = element!!.addLunaElement(100f, 50f).apply {
                enableTransparency = true
                borderAlpha = 0.7f

                if (uafSelected)  {
                    backgroundAlpha = 0.5f
                }

                addText("Player", Misc.getBasePlayerColor())
                centerText()

                onHoverEnter {
                    borderAlpha = 1f
                    playScrollSound()
                }
                onHoverExit {
                    borderAlpha = 0.7f
                }

                onClick {
                    uafSelected = false
                    lastScroller = element!!.externalScroller.yOffset
                    playClickSound()
                    recreatePanel()
                }
            }

            filter1.position.inTL(5f, spacing)
            element!!.addSpacer(filter1.height + 10f)
            spacing += filter1.height + 10f

            var filter2 = element!!.addLunaElement(100f, 50f).apply {
                enableTransparency = true
                borderAlpha = 0.7f

                if (!uafSelected)  {
                    backgroundAlpha = 0.5f
                }

                addText("UAF", Misc.getBasePlayerColor())
                centerText()

                onHoverEnter {
                    borderAlpha = 1f
                    playScrollSound()
                }
                onHoverExit {
                    borderAlpha = 0.7f
                }

                onClick {
                    lastScroller = element!!.externalScroller.yOffset
                    uafSelected = true
                    playClickSound()
                    recreatePanel()
                }
            }

            filter2.elementPanel.position.rightOfMid(filter1.elementPanel, 10f)
            lastElement = filter2
        }


        var genderSelector = element!!.addLunaElement(150f, 50f).apply {
            enableTransparency = true
            borderAlpha = 0.7f

            backgroundAlpha = 0.1f

            if (gender == Gender.FEMALE)
            {
                backgroundColor = Color(255, 0, 100)
            }
            else if (gender == Gender.MALE)
            {
                backgroundColor = Color(0, 100, 255)
            }

            addText(gender.name.lowercase().capitalize(), Misc.getBasePlayerColor())
            centerText()

            onHoverEnter {
                borderAlpha = 1f
                playScrollSound()
            }
            onHoverExit {
                borderAlpha = 0.7f
            }

            onClick {
                lastScroller = element!!.externalScroller.yOffset
                if (gender == Gender.FEMALE)
                {
                    gender = Gender.MALE
                }
                else if (gender == Gender.MALE)
                {
                    gender = Gender.FEMALE
                }

                playClickSound()
                recreatePanel()
            }
        }

        if (lastElement != null)
        {
            genderSelector.elementPanel.position.rightOfMid(lastElement.elementPanel, 10f)
        }
        else
        {
            genderSelector.position.inTL(5f, spacing)
            element!!.addSpacer(genderSelector.height + 10f)
            spacing += genderSelector.height + 10f
        }

        element!!.addPara("", 0f).position.inTL(10f, 240f)

        var portraits = ArrayList<String>()

        if (isUafEnabled && uafSelected)
        {
            portraits += Global.getSector().getFaction("uaf").getPortraits(gender).items
        }
        else
        {
            portraits += Global.getSector().getFaction("player").getPortraits(gender).items
        }


        var count = 1
        var currentRow = 0
        var lastSprite: LunaElement? = null
        for (portrait in portraits)
        {
            var sprite = element!!.addLunaSpriteElement(portrait, LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, 96f, 0f).apply {
                renderBorder = true
                enableTransparency = true

                borderAlpha = 0.8f
                getSprite().alphaMult = 0.8f

                onHoverEnter {
                    getSprite().alphaMult = 1f
                    playScrollSound()
                    borderAlpha = 1f

                }
                onHoverExit {
                    getSprite().alphaMult = 0.8f
                    borderAlpha = 0.8f
                }

                onClick {
                    lastScroller = element!!.externalScroller.yOffset
                    if (selectedPerson == Global.getSector().playerPerson)
                    {
                        Global.getSector().characterData.setPortraitName(portrait)
                    }
                    selectedPerson.portraitSprite = portrait
                    playClickSound()
                    recreatePanel()
                }


            }
            element!!.addTooltipToPrevious( object : TooltipCreator {
                override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
                    return false
                }

                override fun getTooltipWidth(tooltipParam: Any?): Float {
                    return 350f
                }

                override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                    tooltip!!.addImage(portrait, 128f, 0f)
                    tooltip.addSpacer(3f)
                    tooltip.addPara("Sprite: $portrait", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Sprite: ")

                }

            }, TooltipLocation.BELOW)

            element!!.addSpacer(5f)
            element!!.setParaFont(Fonts.ORBITRON_20AABOLD)
            var para = element!!.addPara("$count.", 0f)
            element!!.addSpacer(5f)

            sprite.position.setSize(96f, 96f)
            sprite.getSprite().setSize(96f, 96f)

            if (currentRow == 0)
            {
                sprite.position.inTL(6f, spacing)
                spacing += 96f + 40f
               // element!!.addSpacer(96f + 40f)
            }
            else
            {
                sprite.position.rightOfMid(lastSprite!!.elementPanel, 10f)
            }

            lastSprite = sprite
            currentRow++
            count++

            if (currentRow > 8)
            {
                currentRow = 0
            }

        }

        panel!!.addUIElement(element)
        element!!.externalScroller.yOffset = lastScroller

    }

    fun addCharacterSelector(characters: List<PersonAPI>, spacing: Float) : Float
    {
        var space = spacing
        var count = 1
        var currentRow = 0
        var lastSprite: LunaElement? = null
        for (character in characters) {


            var type = "Player"
            if (Global.getSector().playerFleet.fleetData.officersCopy.map { it.person }.contains(character)) type = "Officer"
            if (Global.getSector().characterData.admins.map { it.person }.contains(character)) type = "Admin"

            var color = Misc.getDarkPlayerColor()
            if (Global.getSector().playerFleet.fleetData.officersCopy.map { it.person }.contains(character)) color = Misc.getBrightPlayerColor().darker().darker()
            if (Global.getSector().characterData.admins.map { it.person }.contains(character)) color = Misc.getHighlightColor().darker().darker()

            var sprite = element!!.addLunaSpriteElement(character.portraitSprite, LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, 64f,  0f).apply {
                renderBorder = true
                enableTransparency = true

                borderColor = color
                borderAlpha = 0.8f
                getSprite().alphaMult = 0.4f

                if (character == selectedPerson) {
                    getSprite().alphaMult = 1f
                } else {
                    getSprite().alphaMult = 0.4f
                }

                onHoverEnter {
                    playScrollSound()
                    borderAlpha = 1f

                }
                onHoverExit {
                    borderAlpha = 0.8f
                }

                onClick {
                    lastScroller = element!!.externalScroller.yOffset
                    selectedPerson = character
                    playClickSound()
                    recreatePanel()
                }
            }

            element!!.addTooltipToPrevious( object : TooltipCreator {
                override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
                    return false
                }

                override fun getTooltipWidth(tooltipParam: Any?): Float {
                    return 350f
                }

                override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                    tooltip!!.addSectionHeading("$type",  Misc.getTextColor(), color, Alignment.MID, 0f)
                    tooltip!!.addImage(character.portraitSprite, 128f, 0f)
                    tooltip.addSpacer(3f)
                    tooltip.addPara("Name: ${character.name.fullName}", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Name: ")
                    tooltip.addPara("Sprite: ${character.portraitSprite}", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Sprite:")

                }

            }, TooltipLocation.BELOW)

            element!!.addSpacer(5f)
            //element!!.setParaFont(Fonts.ORBITRON_20AABOLD)
            var para = element!!.addPara("${character.name.fullName}.", 0f)
            element!!.addSpacer(5f)

            sprite.position.setSize(64f, 64f)
            sprite.getSprite().setSize(64f, 64f)

            if (currentRow == 0) {

                sprite.position.inTL(6f, space)
                space += 64f + 40f
             //   element!!.addSpacer(64f + 40f)
            } else {
                sprite.position.rightOfMid(lastSprite!!.elementPanel, 75f)
            }

            lastSprite = sprite
            currentRow++
            count++

            if (currentRow > 6) {
                currentRow = 0
            }
        }

        return space
    }

    override fun getIcon(): String {
        var path = "graphics/icons/intel/portrait_changer.png"
        Global.getSettings().loadTexture(path)
        return path
    }

    override fun getIntelTags(map: SectorMapAPI?): MutableSet<String> {
        return mutableSetOf("Personal")
    }

}