package Jaydee8652.JaydeePiracy.scripts.skills.flowerfish;

import Jaydee8652.JaydeePiracy.utils.jdp_CombatRenderer
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.ext.plus
import org.lwjgl.util.vector.Vector2f
import org.magiclib.kotlin.setAlpha
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin
import java.awt.Color

class jdp_flowerfishAbstractCausality : SCBaseSkillPlugin() {
    val afterimageInterval = IntervalUtil(0.15f, 0.15f)
    var COLOR = Color(187,36,55,255);

    companion object {
        val STATUS_KEY = Any()
    }

    override fun getAffectsString(): String {
        return "all ships with human officers";
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Ship timeflow scales with velocity and hardflux level.", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addSpacer(10f)

        tooltip.addPara("\"The rules will bend, for we shall not break.\"", 0f, Misc.getTextColor(), Misc.getHighlightColor());
        tooltip.addPara("  -Uncredited", 0f, jdp_AptitudeFlowerfish.JDP_FLOWERFISH_COLOUR, Misc.getHighlightColor());
        tooltip.addSpacer(10f);

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize, id: String) {
    }

    override fun advanceInCombat(data: SCData, ship: ShipAPI, amount: Float) {
        //Make sure the ship exists. Likely unnecessary
        super.advanceInCombat(data, ship, amount)
        if (ship == null) return
        //Has Captain
        val captain: PersonAPI = ship.captain
        if (captain == null || captain.isDefault) return
        if (ship.captain.isAICore) return

        //Modifiers
        val speed = ship.velocity.length()
        val hardflux = ((ship.hardFluxLevel / ship.maxFlux)) / 5f

        val time_bonus = (((speed + hardflux) / 150000) * (speed + hardflux))

        //Bonus
        val mod = 1 + time_bonus

        //Afterimage
        afterimageInterval.advance(Global.getCombatEngine().elapsedInLastFrame)
        if (afterimageInterval.intervalElapsed() && !Global.getCombatEngine().isPaused && ship!!.isAlive)
        {
            jdp_CombatRenderer.addAfterimage(
                ship!!,
                COLOR.setAlpha(50),
                COLOR.setAlpha(0),
                mod,
                0f,
                Vector2f().plus(ship!!.location))
        }


        //Timeflow
        ship.mutableStats.timeMult.modifyMult("jdp_flowerfishAbstractCausality", mod)

        //Bullet Time
        if (ship == Global.getCombatEngine().playerShip) {
            Global.getCombatEngine().timeMult.modifyMult("jdp_flowerfishAbstractCausality" + ship.id, 1 / mod)
            //UI
            val icon = Global.getSettings().getSpriteName("ui", "jdp_icon_abstractcausality")
            val percent = "" + Math.round(time_bonus * 100f).toInt() + "%"
            Global.getCombatEngine().maintainStatusForPlayerShip(STATUS_KEY, icon, "Abstract Causality", percent + " timeflow bonus", false)
        } else {
            Global.getCombatEngine().timeMult.unmodify("jdp_flowerfishAbstractCausality" + ship.id)
        }
    }

    override fun onActivation(data: SCData) {
    }

    override fun onDeactivation(data: SCData) {
    }
}
