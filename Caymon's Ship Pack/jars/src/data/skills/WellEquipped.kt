package data.skills

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class WellEquipped : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("-15%% monthly supply consumption for ship maintenance", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+15%% cargo and fuel capacity", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("-15%% fuel usage", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats!!.suppliesPerMonth.modifyMult(id, 0.85f)
        stats.fuelUseMod.modifyMult(id, 0.85f)

        stats.cargoMod.modifyPercent(id, 15f)
        stats.fuelMod.modifyPercent(id, 15f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

}