package data.skills;

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.HullMods
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class RaptorpackDoctrine : SCBaseSkillPlugin() {

    var DAMAGE_TO_LARGER_BONUS = 10f
    var DAMAGE_TO_LARGER_BONUS_DEST = 10f
    var PEAK_TIME_BONUS = 15f
    var PEAK_TIME_BONUS_DEST = 15f
    var DP_REDUCTION: Float = 0.15f
    var DP_REDUCTION_MAX: Float = 8f
    var RAPTORPACK_DOCTRINE_DP_REDUCTION_ID: String = "Raptorpack_doctrine_dp_reduction"

    override fun getAffectsString(): String {
        return "All Cruisers and Destroyers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Cruisers and Destroyers deal increased damage against larger targets", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - 10%% increased damage against larger hullsizes for cruisers", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")
        tooltip.addPara("   - 10%% increased damage against larger hullsizes for destroyers", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "10%")
        tooltip.addPara("Increased peak operating time for cruisers and destroyers", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("   - 15%% increased operating time for cruisers", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "15%")
        tooltip.addPara("   - 15%% increased operating time for destroyers", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "15%")
        tooltip.addPara("Deployment point cost reduced by 15%% or 8, whichever is less", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        var member = stats!!.fleetMember ?: return
        if (hullSize == ShipAPI.HullSize.CRUISER) {
            val baseCost = stats.suppliesToRecover.baseValue
            val reduction = Math.min(DP_REDUCTION_MAX, baseCost * DP_REDUCTION)

            stats.damageToCruisers.modifyPercent(id, DAMAGE_TO_LARGER_BONUS)
            stats.damageToCapital.modifyPercent(id, DAMAGE_TO_LARGER_BONUS)

            stats.peakCRDuration.modifyPercent(id, PEAK_TIME_BONUS)
            if (stats.fleetMember == null || stats.fleetMember.variant == null || !stats.fleetMember.variant.hasHullMod(HullMods.NEURAL_INTERFACE) && !stats.fleetMember.variant.hasHullMod(HullMods.NEURAL_INTEGRATOR)) {
                stats.dynamic.getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(RAPTORPACK_DOCTRINE_DP_REDUCTION_ID, -reduction)
            }
        }
        if (hullSize == ShipAPI.HullSize.DESTROYER) {
            val baseCost = stats.suppliesToRecover.baseValue
            val reduction = Math.min(DP_REDUCTION_MAX, baseCost * DP_REDUCTION)

            stats.damageToCruisers.modifyPercent(id, DAMAGE_TO_LARGER_BONUS_DEST)
            stats.damageToCapital.modifyPercent(id, DAMAGE_TO_LARGER_BONUS_DEST)

            stats.peakCRDuration.modifyPercent(id, PEAK_TIME_BONUS_DEST)
            if (stats.fleetMember == null || stats.fleetMember.variant == null || !stats.fleetMember.variant.hasHullMod(HullMods.NEURAL_INTERFACE) && !stats.fleetMember.variant.hasHullMod(HullMods.NEURAL_INTEGRATOR)) {
                stats.dynamic.getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyFlat(RAPTORPACK_DOCTRINE_DP_REDUCTION_ID, -reduction)
            }
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

}