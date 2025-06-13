package data.scripts.ix.conditions;

import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class IXInfrastructure extends BaseHazardCondition{

    private static int INDUSTRY_BONUS = 1;
    
    public void apply(String id) {
		super.apply(id);
		if (market.getSize() == 3) {
			market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).modifyFlat("ix_infrastructure", 1f, "Existing Infrastructure");
		}
		else market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).unmodify("ix_infrastructure");
    }
    
    @Override
    public void unapply(String id) {
		super.unapply(id);
		market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).unmodify("ix_infrastructure");
	}
    
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
		float opad = 10f;
		
		tooltip.addPara("%s limit while colony does not exceed size 3",  
				opad, Misc.getHighlightColor(), 
				"" + "+1 Industry");
    }
}
