package data.scripts.vice;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.AICoreAdminPlugin;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.CampaignPlugin.PickPriority;

import data.scripts.vice.SynthesisCorePlugin;
import data.scripts.vice.RelicCorePlugin;

public class XOCampaignPlugin extends BaseCampaignPlugin {

	private static String SYNTHESIS_CORE_ID = "xo_synthesis_core";
	private static String DELTA_RELIC_ID = "asm_relic_delta";
	private static String GAMMA_RELIC_ID = "asm_relic_gamma";
	private static String BETA_RELIC_ID = "asm_relic_beta";
	
	public PluginPick<AICoreOfficerPlugin> pickAICoreOfficerPlugin(String commodityId) {
		if (SYNTHESIS_CORE_ID.equals(commodityId)) {
			return new PluginPick<AICoreOfficerPlugin>(new SynthesisCorePlugin(), PickPriority.MOD_SET);
		}
		else if (DELTA_RELIC_ID.equals(commodityId) 
					|| GAMMA_RELIC_ID.equals(commodityId)
					|| BETA_RELIC_ID.equals(commodityId)) {
			return new PluginPick<AICoreOfficerPlugin>(new RelicCorePlugin(), PickPriority.MOD_SET);
		}
		else return null;
	}
}