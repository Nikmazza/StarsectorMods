package Jaydee8652.JaydeePiracy.campaign.econ;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.campaign.FactionAPI;

public class jdp_PKInspire extends BaseMarketConditionPlugin implements MarketImmigrationModifier {

	public static final float STAB_BONUS = 1f;
	public static final float ACCESS_BONUS = 0.1f;


	@Override
	public void apply(String id) {
		super.apply(id);
		market.getStability().modifyFlat (id, STAB_BONUS, "Show of Force");
		market.getAccessibilityMod().modifyFlat(id, ACCESS_BONUS, "Show of Force");
	}

	@Override
	public void unapply(String id) {
		super.unapply(id);
		market.getStability().unmodify(id);
		market.getAccessibilityMod().unmodifyFlat(id);
		market.removeTransientImmigrationModifier(this);
	}

	@Override
	public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
		incoming.add(Factions.POOR, 10f);
		incoming.getWeight().modifyFlat(getModId(), getImmigrationBonus(), Misc.ucFirst(condition.getName().toLowerCase()));
	}

	protected float getImmigrationBonus() {
		return market.getSize();
	}

	protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
		super.createTooltipAfterDescription(tooltip, expanded);

		tooltip.addPara("%s stability",
				10f, Misc.getHighlightColor(),
				"+" + (int)STAB_BONUS);

		tooltip.addPara("%s accessibility",
				10f, Misc.getHighlightColor(),
				"+" + (int) Math.round(ACCESS_BONUS * 100f) + "%");

		tooltip.addPara("%s population growth (based on colony size)",
				10f, Misc.getHighlightColor(),
				"+" + (int) getImmigrationBonus());
	}
}