package Jaydee8652.JaydeePiracy.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;

import static com.fs.starfarer.api.impl.campaign.econ.impl.PlanetaryShield.*;
import static com.fs.starfarer.api.impl.campaign.ids.Industries.PLANETARYSHIELD;

public class jdp_logisticsComplex extends BaseMarketConditionPlugin {
	public static float ACCESS_BONUS = 30f;


	public void apply(String id) {
		super.apply(id);

		Industry spaceport = null;
		Industry shield = null;

		SpecialItemData special = null;

		//Technically even uncolonised planets have spaceports as far as I understand, so without this check it could
		//look for special items on markets that don't exist, which is bad for hopefully obvious reasons.
		if (market.getId().equals("fake_Colonize")) return;

		for (Industry ind : market.getIndustries()) {
			if (ind.getSpec().hasTag(Industries.TAG_SPACEPORT)) {
				spaceport = ind;
				special = ind.getSpecialItem();
			} else if (ind.getSpec().getId() == Industries.PLANETARYSHIELD) {
				shield = ind;
			}
		}

		if ((spaceport != null) && (spaceport.isFunctional()) && (special != null)) {
			if (special.getId().equals(Items.FULLERENE_SPOOL)) market.getAccessibilityMod().modifyFlat(id, ACCESS_BONUS/100f, condition.getName());
		} else if ((spaceport != null) && (!spaceport.isFunctional()) && (special == null)) {
			market.getAccessibilityMod().unmodify(id);
		}

		if ((shield != null) && (shield.isFunctional())) {
			float bonus = DEFENSE_BONUS;
			if (shield.isImproved()) bonus = bonus + IMPROVE_DEFENSE_BONUS;
			if (shield.getAICoreId().equals(Commodities.ALPHA_CORE)) bonus = bonus + ALPHA_CORE_BONUS;

			market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(getModId(), 1f + bonus, condition.getName());
		} else if ((shield != null) && (!shield.isFunctional())) {
			market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodify(getModId());
		}
	}

	public void unapply(String id) {
		super.unapply(id);
		Industry spaceport = null;
		Industry shield = null;

		for (Industry ind : market.getIndustries()) {
			if (ind.getSpec().hasTag(Industries.TAG_SPACEPORT)) {
				spaceport = ind;
			} else if (ind.getSpec().getId().equals(Industries.PLANETARYSHIELD)) {
				shield = ind;
			}
		}

		if (spaceport != null) {
			market.getAccessibilityMod().unmodify(id);
		}
		if (shield != null) {
			market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodify(getModId());
		}
	}

	protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
		super.createTooltipAfterDescription(tooltip, expanded);
		tooltip.addPara("Accessibility bonus from Fullerene Spool is %s", 10f, Misc.getHighlightColor(), "doubled");
		tooltip.addPara("Ground Defense bonus from Planetary Shield is %s", 10f, Misc.getHighlightColor(), "doubled");

	}
}
