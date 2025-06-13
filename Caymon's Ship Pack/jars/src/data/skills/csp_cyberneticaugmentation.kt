package data.skills

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.skills.*
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class csp_cyberneticaugmentation : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships without officers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("Grants non-elite Gunnery Implants, Target Analysis, Point Defense", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("Gives all ships 50%% more ammo regeneration for weapons that use ammunition", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI?, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        var member = stats!!.fleetMember ?: return
        if (member.captain == null || member.captain.isDefault) {

            //Apply Skill Effects
            PointDefense.Level1().apply(stats, hullSize, id, 1f)
            PointDefense.Level2().apply(stats, hullSize, id, 1f)

            TargetAnalysis.Level2().apply(stats, hullSize, id, 1f)
            TargetAnalysis.Level3().apply(stats, hullSize, id, 1f)

            GunneryImplants.Level1().apply(stats, hullSize, id, 1f)
            GunneryImplants.Level2().apply(stats, hullSize, id, 1f)
            GunneryImplants.Level3().apply(stats, hullSize, id, 1f)


        }
        stats.ballisticAmmoRegenMult.modifyPercent(id, 50f)
        stats.energyAmmoRegenMult.modifyPercent(id, 50f)
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI?, variant: ShipVariantAPI, id: String?) {

    }

}