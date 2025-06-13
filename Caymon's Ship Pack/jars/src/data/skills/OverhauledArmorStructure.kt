package data.skills

import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShieldAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import second_in_command.SCData
import second_in_command.specs.SCBaseSkillPlugin

class OverhauledArmorStructure : SCBaseSkillPlugin() {

    override fun getAffectsString(): String {
        return "all ships in the fleet"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {

        tooltip.addPara("+10%% armor and hitpoints, doubled for shieldless ships", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())

    }

    override fun applyEffectsBeforeShipCreation(data: SCData, stats: MutableShipStatsAPI, variant: ShipVariantAPI, hullSize: ShipAPI.HullSize?, id: String?) {
        stats.armorBonus.modifyPercent(id, 10f)
        stats.hullBonus.modifyPercent(id, 10f)
        if (stats.getVariant().getHullSpec().getShieldType() == ShieldAPI.ShieldType.NONE || stats.getVariant().getHullSpec().getShieldType() == ShieldAPI.ShieldType.PHASE){
            stats.armorBonus.modifyPercent(id, 20f)
            stats.hullBonus.modifyPercent(id, 20f)
        }
    }

    override fun applyEffectsAfterShipCreation(data: SCData, ship: ShipAPI, variant: ShipVariantAPI, id: String?) {

    }

}