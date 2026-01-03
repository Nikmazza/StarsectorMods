package Jaydee8652.JaydeePiracy.scripts.skills;

import Jaydee8652.JaydeePiracy.JaydeePiracyPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.characters.*;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.DelayedActionScript;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import org.codehaus.janino.Java;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.Condition;

public class jdp_atomicextraction {
	private static final Logger log = Global.getLogger(JaydeePiracyPlugin.class);

	public static Map<String, String> resourceDepletionMap = new HashMap<>();
	static {
		resourceDepletionMap.put(Conditions.ORE_SPARSE, null);
		resourceDepletionMap.put(Conditions.ORE_MODERATE, Conditions.ORE_SPARSE);
		resourceDepletionMap.put(Conditions.ORE_ABUNDANT, Conditions.ORE_MODERATE);
		resourceDepletionMap.put(Conditions.ORE_RICH, Conditions.ORE_ABUNDANT);
		resourceDepletionMap.put(Conditions.ORE_ULTRARICH, Conditions.ORE_RICH);

		resourceDepletionMap.put(Conditions.RARE_ORE_SPARSE, null);
		resourceDepletionMap.put(Conditions.RARE_ORE_MODERATE, Conditions.RARE_ORE_SPARSE);
		resourceDepletionMap.put(Conditions.RARE_ORE_ABUNDANT, Conditions.RARE_ORE_MODERATE);
		resourceDepletionMap.put(Conditions.RARE_ORE_RICH, Conditions.RARE_ORE_ABUNDANT);
		resourceDepletionMap.put(Conditions.RARE_ORE_ULTRARICH, Conditions.RARE_ORE_RICH);

		resourceDepletionMap.put(Conditions.VOLATILES_TRACE, null);
		resourceDepletionMap.put(Conditions.VOLATILES_DIFFUSE, Conditions.VOLATILES_TRACE);
		resourceDepletionMap.put(Conditions.VOLATILES_ABUNDANT, Conditions.VOLATILES_DIFFUSE);
		resourceDepletionMap.put(Conditions.VOLATILES_PLENTIFUL, Conditions.VOLATILES_ABUNDANT);

		resourceDepletionMap.put(Conditions.ORGANICS_TRACE, null);
		resourceDepletionMap.put(Conditions.ORGANICS_COMMON, Conditions.ORGANICS_TRACE);
		resourceDepletionMap.put(Conditions.ORGANICS_ABUNDANT, Conditions.ORGANICS_COMMON);
		resourceDepletionMap.put(Conditions.ORGANICS_PLENTIFUL, Conditions.ORGANICS_ABUNDANT);
	}


	public static class Level1 extends BaseSkillEffectDescription implements MarketSkillEffect {
		Integer DAYS_DELAY = 45;
		Boolean DEPLETING = false;

		public void apply(MarketAPI market, String id, float level) {
			if (!market.getAdmin().hasTag("jdp_atomicextraction")) market.getAdmin().addTag("jdp_atomicextraction");


			Industry mining = null;

			for (Industry ind : market.getIndustries()) {
				if (ind.getSpec().hasTag("mining")) {
					mining = ind;
				}
			}

			if ((mining != null) && (mining.isFunctional() && market.getPlanetEntity() != null && !market.getPlanetEntity().isGasGiant())) {

				mining.getDemand(Commodities.FUEL).getQuantity().modifyFlat(id, market.getSize());

				mining.getSupply(Commodities.RARE_ORE).getQuantity().modifyMult(id, 2f);
				mining.getSupply(Commodities.ORE).getQuantity().modifyMult(id, 2f);
				mining.getSupply(Commodities.ORGANICS).getQuantity().modifyMult(id, 2f);
				mining.getSupply(Commodities.VOLATILES).getQuantity().modifyMult(id, 2f);

				if (DEPLETING.equals(false)) {
					DEPLETING = true;

					Global.getSector().addScript(new DelayedActionScript(DAYS_DELAY) {
						@Override
						public void doAction() {
							if (market.getAdmin().hasTag("jdp_atomicextraction")) {
								List<MarketConditionAPI> conditions = market.getConditions();
								Collections.shuffle(conditions);

								for (MarketConditionAPI condition : market.getConditions()) {
									String resource = condition.getId();
									if (resourceDepletionMap.containsKey(resource)) market.removeCondition(resource);

									String depletedResource = resourceDepletionMap.get(resource);
									if (depletedResource != null) market.addCondition(depletedResource);
									break;
								}
								//finalMining.setDisrupted(5f);
							}
							DEPLETING = false;

							DAYS_DELAY = (int) Math.floor(Math.random() * 66);
							if (DAYS_DELAY <= 45) DAYS_DELAY = 45;
						}
					});
				}
			} else if ((mining != null) && (!mining.isFunctional())) {
				if (mining.getDemand(Commodities.FUEL) != null) {
					mining.getDemand(Commodities.FUEL).getQuantity().unmodify(id);
				} else {
					log.warn("JDP_DEBUG: Atomic Extraction encountered a null fuel demand on [" + market.getName() + " " + market.getId() + "] in ["  + market.getPrimaryEntity().getContainingLocation().getId() + " " + market.getPrimaryEntity().getContainingLocation().getId() + "]");
					log.warn("JDP_DEBUG: Admin has the following skills [" + market.getAdmin().getStats().getSkillsCopy().toString() + "]");
				}

				mining.getSupply(Commodities.RARE_ORE).getQuantity().unmodify(id);
				mining.getSupply(Commodities.ORE).getQuantity().unmodify(id);
				mining.getSupply(Commodities.ORGANICS).getQuantity().unmodify(id);
				mining.getSupply(Commodities.VOLATILES).getQuantity().unmodify(id);
			}
		}

		public void unapply(MarketAPI market, String id) {
			Industry mining = null;

			for (Industry ind : market.getIndustries()) {
				if (ind.getSpec().hasTag("mining")) {
					mining = ind;
				}
			}

			if ((mining != null)) {
				if (mining.getDemand(Commodities.FUEL) != null) {
					mining.getDemand(Commodities.FUEL).getQuantity().unmodify(id);
				} else {
					log.warn("JDP_DEBUG: Atomic Extraction encountered a null fuel demand on [" + market.getName() + " " + market.getId() + "] in ["  + market.getPrimaryEntity().getContainingLocation().getId() + " " + market.getPrimaryEntity().getContainingLocation().getId() + "]");
					log.warn("JDP_DEBUG: Admin has the following skills [" + market.getAdmin().getStats().getSkillsCopy().toString() + "]");
				}

				mining.getSupply(Commodities.RARE_ORE).getQuantity().unmodify(id);
				mining.getSupply(Commodities.ORE).getQuantity().unmodify(id);
				mining.getSupply(Commodities.ORGANICS).getQuantity().unmodify(id);
				mining.getSupply(Commodities.VOLATILES).getQuantity().unmodify(id);
			}
		}

		@Override
		public void createCustomDescription(
				MutableCharacterStatsAPI stats,
				SkillSpecAPI skill,
				TooltipMakerAPI info,
				float width) {
			float pad = 3f;
			float opad = 10f;
			Color h = Misc.getHighlightColor();
			Color bad = Misc.getNegativeHighlightColor();
			Color good = Misc.getPositiveHighlightColor();
			Color neutral = Misc.getGrayColor();


			float HEIGHT = 50f;
			float PAD = 10f;

			//info.addPara("This administrator is specialised in lucrative Eridani-Utopia Terraforming geoengineering practices.", 0f, neutral);

			info.addPara("This administrator is experienced in the use of controlled subsurface Antimatter-Fusion detonations for mining.",
					3f, Misc.getHighlightColor(), "This administrator is experienced in the use of controlled subsurface Antimatter-Fusion detonations for mining.");
			info.addPara("*This skill does not apply on gas giants or standalone stations", 3f, neutral, "*This skill does not apply on gas giants or standalone stations");
			info.addSpacer(10f);

			info.addPara("Produced units of ore, rare ore, organics and volatiles is %s (Mining)",
					3f, Misc.getHighlightColor(),
					"doubled");
			info.addPara("%s fuel demand per size level (Mining)",
					3f, Misc.getHighlightColor(),
					"+1");
			info.addSpacer(10f);

			info.addPara("Approximately once every two months, the size of a random mining deposit will permanently decrease", 3f, bad, "permanently decrease");
			info.addPara("*Given enough time this will fully remove the deposit", 3f, neutral, "*Given enough time this will fully remove the deposit");
		}

		@Override
		public boolean hasCustomDescription() {
			return true;
		}

		public String getEffectPerLevelDescription() {
			return null;
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.GOVERNED_OUTPOST;
		}
	}
}