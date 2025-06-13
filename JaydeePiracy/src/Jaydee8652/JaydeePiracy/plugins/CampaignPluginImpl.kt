package Jaydee8652.JaydeePiracy.plugins

import Jaydee8652.JaydeePiracy.plugins.OmegaCoreOfficerPluginImpl
import com.fs.starfarer.api.PluginPick
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin
import com.fs.starfarer.api.campaign.BaseCampaignPlugin
import com.fs.starfarer.api.campaign.CampaignPlugin
import Jaydee8652.JaydeePiracy.utils.JaydeePiracyIDs.JDP_CORE_OMEGA


class CampaignPluginImpl: BaseCampaignPlugin() {//Loaded on game in the mod plugin, applies officer stats to my copy of the Omega Core

    override fun pickAICoreOfficerPlugin(commodityId: String): PluginPick<AICoreOfficerPlugin>? {
        return when (commodityId) {
            JDP_CORE_OMEGA -> PluginPick<AICoreOfficerPlugin>(OmegaCoreOfficerPluginImpl(), CampaignPlugin.PickPriority.MOD_SET)
            else -> null
        }
    }
}