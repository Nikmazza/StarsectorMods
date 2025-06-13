package data.skills

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import second_in_command.SCData
import second_in_command.SCUtils.addAndCheckTag
import second_in_command.specs.SCBaseSkillPlugin

class VeteranStandard : SCBaseSkillPlugin() {

    companion object {
        fun reapplyDmods(variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, stats: MutableShipStatsAPI) {
            if (variant.hasTag("sc_applied_dmods_this_frame")) return
            variant.addTag("sc_applied_dmods_this_frame")

            var dmodSpecs = Global.getSettings().allHullModSpecs.filter { it.hasTag(Tags.HULLMOD_DMOD) }
            var hmods = variant.permaMods
            for (hmod in hmods) {
                if (dmodSpecs.map { it.id }.contains(hmod)) {

                    var spec = Global.getSettings().getHullModSpec(hmod) ?: continue
                    var plugin = spec.effect
                    plugin.applyEffectsBeforeShipCreation(hullSize, stats, hmod)
                }
            }

            variant.removeTag("sc_applied_dmods_this_frame")
        }
    }

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("Reduces most negative effects of d-mods by 25%%*", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+3%% combat readiness for every s-mod on the ship (max 15%%)", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

        tooltip.addSpacer(10f)

        tooltip.addPara("*This effect stacks multiplicatively with others of the same kind", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "multiplicatively")
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {

        var smods = variant.sMods.count()
        var bonus = 0.03f * smods
        bonus = bonus.coerceIn(0f, 0.15f)

        stats!!.maxCombatReadiness.modifyFlat(id, bonus, "Veteran Standard")

        if (data.isNPC && !variant.addAndCheckTag("sc_pristine_condition")) {
            stats.fleetMember.repairTracker.cr += bonus
            stats.fleetMember.repairTracker.cr = MathUtils.clamp(stats.fleetMember.repairTracker.cr, 0f, 1f)
        }
        stats.dynamic.getStat(Stats.DMOD_EFFECT_MULT).modifyMult(id, 0.75f)
        reapplyDmods(variant, hullSize, stats)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {



    }

    override fun callEffectsFromSeparateSkill(stats: MutableShipStatsAPI?, hullSize: ShipAPI.HullSize?, id: String?) {
          stats!!.dynamic.getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(id, 0.8f)
    }

    override fun getNPCSpawnWeight(fleet: CampaignFleetAPI): Float {
        if (fleet.fleetData.membersListCopy.any { it.variant.sMods.isNotEmpty() }) return super.getNPCSpawnWeight(fleet)
        return 0f
    }

}