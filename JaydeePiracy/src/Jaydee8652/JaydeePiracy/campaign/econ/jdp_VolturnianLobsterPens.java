package Jaydee8652.JaydeePiracy.campaign.econ;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class jdp_VolturnianLobsterPens extends BaseMarketConditionPlugin {
	public void apply(String id) {
		super.apply(id);

		String commodityId = Commodities.LOBSTER;
		Integer mod = 1;
		Integer base = 0;

		Industry industry = market.getIndustry(Industries.AQUACULTURE);

		if (industry.isFunctional()) {
			industry.supply(id + "_0", commodityId, base, BaseIndustry.BASE_VALUE_TEXT);
			industry.supply(id + "_1", commodityId, mod, Misc.ucFirst(condition.getName().toLowerCase()));
		} else {
			industry.getSupply(commodityId).getQuantity().unmodifyFlat(id + "_0");
			industry.getSupply(commodityId).getQuantity().unmodifyFlat(id + "_1");
		}
	}

	//This is the change, the real lobsters are a persistent invasive species and will never go away.
	//(IE this variant of the condition can be unapplied)
	public void unapply(String id) {
		super.unapply(id);
		String commodityId = Commodities.LOBSTER;
		Industry industry = market.getIndustry(Industries.AQUACULTURE);

		industry.getSupply(commodityId).getQuantity().unmodifyFlat(id + "_0");
		industry.getSupply(commodityId).getQuantity().unmodifyFlat(id + "_1");
	}

	protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
		super.createTooltipAfterDescription(tooltip, expanded);
		tooltip.addPara("%s volturnian lobster production (Aquaculture)",
				10f, Misc.getHighlightColor(),
				"+1");
	}
}
