package data.skills

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin
import kotlin.collections.iterator

class EscortDuty : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all frigates and destroyers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Frigates and destroyers close to cruisers with assigned officers receive increased stats", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - The radius for this effect is approximately 1500 su wide",0f, Misc.getTextColor(), Misc.getHighlightColor(), "1000")
        tooltip.addPara("   - Ships under the effect gain 15%% Maneuverability",0f, Misc.getTextColor(), Misc.getHighlightColor(), "15%")
        tooltip.addPara("   - Ships under the effect gain 10%% max speed",0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")
        tooltip.addPara("   - Ships under the effect gain a 10%% increase towards all weapon ranges",0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {
        if (!ship!!.hasListenerOfClass(EscortdutyScript::class.java) && (ship.hullSize == ShipAPI.HullSize.FRIGATE || ship.hullSize == ShipAPI.HullSize.DESTROYER)) {
            ship.addListener(EscortdutyScript(ship))
        }
    }

}

class EscortdutyScript(var ship: ShipAPI) : AdvanceableListener {

    var interval = IntervalUtil(0.2f, 0.2f)

    var isActive = false

    override fun advance(amount: Float) {

        if (ship == Global.getCombatEngine().playerShip && isActive) {

            Global.getCombatEngine().maintainStatusForPlayerShip("csp_escort_duty", "graphics/icons/hullsys/targeting_feed.png",
                "Escort Duty", "15% Maneuverability / 10% Weapon Range / 10% Max Speed", false)
        }

        interval.advance(amount)
        if (!interval.intervalElapsed()) return

        isActive = false

        var iterator = Global.getCombatEngine().shipGrid.getCheckIterator(ship.location, 2000f, 2000f)
        for (entry in iterator) {
            var ally = entry as ShipAPI

            if (ally == ship) continue
            if (!ally.isAlive) continue
            if (ship.owner != ally.owner) continue

            if (!ally.isCruiser) continue
            if (MathUtils.getDistance(ally, ship) >= 1500) continue

            isActive = true
        }

        if (isActive) {
            ship.mutableStats.maxSpeed.modifyPercent("csp_escort_duty", 10f)
            ship.mutableStats.acceleration.modifyPercent("csp_escort_duty", 15f)
            ship.mutableStats.deceleration.modifyPercent("csp_escort_duty", 15f)
            ship.mutableStats.turnAcceleration.modifyPercent("csp_escort_duty", 15f)
            ship.mutableStats.maxTurnRate.modifyPercent("csp_escort_duty", 15f)

            ship.mutableStats.ballisticWeaponRangeBonus.modifyPercent("csp_escort_duty", 10f)
            ship.mutableStats.energyWeaponRangeBonus.modifyPercent("csp_escort_duty", 10f)
            ship.mutableStats.missileWeaponRangeBonus.modifyPercent("csp_escort_duty", 10f)

        } else {
            ship.mutableStats.maxSpeed.unmodify("csp_escort_duty")
            ship.mutableStats.acceleration.unmodify("csp_escort_duty")
            ship.mutableStats.deceleration.unmodify("csp_escort_duty")
            ship.mutableStats.turnAcceleration.unmodify("csp_escort_duty")
            ship.mutableStats.maxTurnRate.unmodify("csp_escort_duty")


            ship.mutableStats.ballisticWeaponRangeBonus.unmodify("csp_escort_duty")
            ship.mutableStats.energyWeaponRangeBonus.unmodify("csp_escort_duty")
            ship.mutableStats.missileWeaponRangeBonus.unmodify("csp_escort_duty")
        }
    }
}