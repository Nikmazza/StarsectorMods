package Jaydee8652.JaydeePiracy.campaign.econ;

import com.fs.starfarer.D.M;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.DelayedActionScript;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class jdp_glassedWorld extends BaseMarketConditionPlugin {
	Integer HAZARD = 100;
	Float MAX_INDUSTRIES = 1f;
	Integer DAYS_DELAY = 10;
	Integer DAYS_DISRUPTED = 0;

	Boolean DISRUPTED = false;

	public void apply(String id) {
		super.apply(id);
		market.getHazard().modifyFlat(id, (HAZARD / 100f), condition.getName());
		market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).modifyFlat(id, MAX_INDUSTRIES);

		List<Industry> industries = market.getIndustries();
		if (industries.isEmpty()) return;

		if (DISRUPTED.equals(false)) {
			DISRUPTED = true;
			Global.getSector().addScript(new DelayedActionScript(DAYS_DELAY) {
			@Override
			public void doAction() {
				Collections.shuffle(industries);
				Industry industry = industries.get(0);

				while (!industry.isFunctional()) {
					Collections.shuffle(industries);
					industry = industries.get(0);
				}

				DAYS_DISRUPTED = (int) Math.floor(Math.random() * 16);
				if (DAYS_DISRUPTED <= 5) DAYS_DISRUPTED = 5;

				industry.setDisrupted(DAYS_DISRUPTED);
				DISRUPTED = false;


				DAYS_DELAY = (int) Math.floor(Math.random() * 46);
				if (DAYS_DELAY <= 10) DAYS_DELAY = 10;
			}
		});
		}
	}

	public void unapply(String id) {
		super.unapply(id);
		market.getHazard().unmodify(id);
		market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).unmodify(id);

	}

	protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
		super.createTooltipAfterDescription(tooltip, expanded);
		tooltip.addPara("%s hazard rating", 10f, Misc.getHighlightColor(), "+" + HAZARD + "%");
		tooltip.addPara("%s maximum number of industries", 10f, Misc.getHighlightColor(), "+1");
		tooltip.addPara("Approximately once a month an industry will %s be disrupted for a few days", 10f, Misc.getHighlightColor(), "randomly");

	}
}
