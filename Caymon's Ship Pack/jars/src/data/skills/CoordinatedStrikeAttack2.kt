package data.skills

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class CoordinatedStrikeAttack2 : SCBaseSkillPlugin() {
    override fun getAffectsString(): String {
        return "all ships with fighter bays"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("+5%% damage dealt by fighters", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+1 flux dissipation per ordnance point spend on fighters", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        {
    }

    fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }
    fun applyEffectsToFighterSpawnedByShip(data: SCData, fighter: ShipAPI?, ship: ShipAPI?, id: String?) {
        var stats = fighter!!.mutableStats

        stats.ballisticWeaponDamageMult.modifyPercent(id, 5f)
        stats.energyWeaponDamageMult.modifyPercent(id, 5f)
        stats.missileWeaponDamageMult.modifyPercent(id, 5f)

    }
}
}