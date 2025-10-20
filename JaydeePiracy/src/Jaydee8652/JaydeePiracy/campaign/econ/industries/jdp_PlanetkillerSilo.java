package Jaydee8652.JaydeePiracy.campaign.econ.industries;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;

import Jaydee8652.JaydeePiracy.utils.jdp_Entities;
import Jaydee8652.JaydeePiracy.utils.jdp_Industries;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.AddedEntity;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.EntityLocation;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.*;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD.RaidDangerLevel;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.IconRenderMode;
import com.fs.starfarer.api.ui.LabelAPI;


public class jdp_PlanetkillerSilo extends BaseIndustry implements MarketImmigrationModifier {
	//Gimme bp! \/
	//addspecial industry_bp jdp_pksilo

	//Visual
	@Override
	public void setSpecialItem(SpecialItemData special) {
		super.setSpecialItem(special);
		if (this.special != null && this.special.getId().equals("planetkiller")) {
			if (jdp_missiletoken == null) {
				SectorEntityToken focus = market.getPlanetEntity();
				if (focus == null) focus = market.getPrimaryEntity();
				if (focus != null) {
					EntityLocation loc = new EntityLocation();
					float radius = focus.getRadius() + 120f;
					loc.orbit = Global.getFactory().createCircularOrbit(focus, (float) Math.random() * 360f,
							radius, radius / (10f + 10f * (float) Math.random()));
					AddedEntity added = BaseThemeGenerator.addNonSalvageEntity(
							market.getContainingLocation(), loc, jdp_Entities.JDP_MISSILE, getMarket().getFactionId());//Factions.NEUTRAL);
					if (added != null) {
						jdp_missiletoken = added.entity;
						market.getContainingLocation().addScript(new MissileRemover(jdp_missiletoken, market, this));
					}
				}
			}
		}
	}
	protected SectorEntityToken jdp_missiletoken;
	protected static class MissileRemover implements EveryFrameScript {
		protected SectorEntityToken jdp_missiletoken;
		protected MarketAPI market;
		protected jdp_PlanetkillerSilo industry;
		public MissileRemover(SectorEntityToken jdp_missiletoken, MarketAPI market, jdp_PlanetkillerSilo industry) {
			this.jdp_missiletoken = jdp_missiletoken;
			this.market = market;
			this.industry = industry;
		}
		public void advance(float amount) {
			Industry ind = market.getIndustry(jdp_Industries.JDP_PKSILO);
			SpecialItemData item = ind == null ? null : ind.getSpecialItem();
			if (item == null || !item.getId().equals(Items.PLANETKILLER)) {
				Misc.fadeAndExpire(jdp_missiletoken);
				industry.jdp_missiletoken = null;
				jdp_missiletoken = null;
			}
		}
		public boolean isDone() {
			return jdp_missiletoken == null;
		}
		public boolean runWhilePaused() {
			return false;
		}
	}

	@Override
	//Adds a tooltip if PK Device is not installed.
	public void createTooltip(Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded) {
		this.currTooltipMode = mode;
		float pad = 3.0F;
		float opad = 10.0F;
		FactionAPI faction = this.market.getFaction();
		Color color = faction.getBaseUIColor();
		Color dark = faction.getDarkUIColor();
		Color grid = faction.getGridUIColor();
		Color bright = faction.getBrightUIColor();
		Color gray = Misc.getGrayColor();
		Color highlight = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
		MarketAPI copy = this.market.clone();
		copy.setSuppressedConditions(this.market.getSuppressedConditions());
		copy.setRetainSuppressedConditionsSetWhenEmpty(true);
		this.market.setRetainSuppressedConditionsSetWhenEmpty(true);
		MarketAPI orig = this.market;
		this.market = copy;
		boolean needToAddIndustry = !this.market.hasIndustry(this.getId());
		if (needToAddIndustry) {
			this.market.getIndustries().add(this);
		}

		if (mode != IndustryTooltipMode.NORMAL) {
			this.market.clearCommodities();

			for(CommodityOnMarketAPI curr : this.market.getAllCommodities()) {
				curr.getAvailableStat().setBaseValue(100.0F);
			}
		}

		this.market.reapplyConditions();
		this.reapply();
		String type = "";
		if (this.isIndustry()) {
			type = " - Industry";
		}

		if (this.isStructure()) {
			type = " - Structure";
		}

		tooltip.addTitle(this.getCurrentName() + type, color);
		String desc = this.spec.getDesc();
		String override = this.getDescriptionOverride();
		if (override != null) {
			desc = override;
		}

		desc = Global.getSector().getRules().performTokenReplacement((String)null, desc, this.market.getPrimaryEntity(), (Map)null);
		tooltip.addPara(desc, opad);
		if (this.isIndustry() && (mode == IndustryTooltipMode.ADD_INDUSTRY || mode == IndustryTooltipMode.UPGRADE || mode == IndustryTooltipMode.DOWNGRADE)) {
			int num = Misc.getNumIndustries(this.market);
			int max = Misc.getMaxIndustries(this.market);
			if (this.isIndustry()) {
				if (mode == IndustryTooltipMode.UPGRADE) {
					for(Industry curr : this.market.getIndustries()) {
						if (this.getSpec().getId().equals(curr.getSpec().getUpgrade())) {
							if (curr.isIndustry()) {
								--num;
							}
							break;
						}
					}
				} else if (mode == IndustryTooltipMode.DOWNGRADE) {
					for(Industry curr : this.market.getIndustries()) {
						if (this.getSpec().getId().equals(curr.getSpec().getDowngrade())) {
							if (curr.isIndustry()) {
								--num;
							}
							break;
						}
					}
				}
			}

			Color var49 = Misc.getTextColor();
			if (num > max) {
				--num;
				tooltip.addPara("Maximum number of industries reached", bad, opad);
			}
		}

		this.addRightAfterDescriptionSection(tooltip, mode);
		if (this.isDisrupted()) {
			int left = (int)this.getDisruptedDays();
			if (left < 1) {
				left = 1;
			}

			String days = "days";
			if (left == 1) {
				days = "day";
			}

			tooltip.addPara("Operations disrupted! %s " + days + " until return to normal function.", opad, Misc.getNegativeHighlightColor(), highlight, new String[]{"" + left});
		}

		if ((DebugFlags.COLONY_DEBUG || this.market.isPlayerOwned()) && mode == IndustryTooltipMode.NORMAL) {
			if (this.getSpec().getUpgrade() != null && !this.isBuilding()) {
				tooltip.addPara("Click to manage or upgrade", Misc.getPositiveHighlightColor(), opad);
			} else {
				tooltip.addPara("Click to manage", Misc.getPositiveHighlightColor(), opad);
			}
		}

		if (mode == IndustryTooltipMode.QUEUED) {
			tooltip.addPara("Click to remove or adjust position in queue", Misc.getPositiveHighlightColor(), opad);
			tooltip.addPara("Currently queued for construction. Does not have any impact on the colony.", opad);
			int left = (int)this.getSpec().getBuildTime();
			if (left < 1) {
				left = 1;
			}

			String days = "days";
			if (left == 1) {
				days = "day";
			}

			tooltip.addPara("Requires %s " + days + " to build.", opad, highlight, new String[]{"" + left});
		} if (!this.isFunctional() && mode == IndustryTooltipMode.NORMAL && !this.isDisrupted() && (this.buildTime - this.buildProgress) != 1) {
			tooltip.addPara("Currently under construction and not producing anything or providing other benefits.", opad);
			int left = (int)(this.buildTime - this.buildProgress);
			if (left < 1) {
				left = 1;
			}

			String days = "days";
			if (left == 1) {
				days = "day";
			}

			tooltip.addPara("Requires %s more " + days + " to finish building.", opad, highlight, new String[]{"" + left});
		}
		//No item installed tooltip
		else if (!this.isFunctional() && mode == IndustryTooltipMode.NORMAL && !this.isDisrupted() && (this.buildTime - this.buildProgress) == 1) {
			tooltip.addPara("Not currently providing any benefits.", opad);
			String laughablethreat = "Without a physical Planetkiller Device installed it is unlikely anyone, faction or individual, will take this threat seriously.";
			tooltip.addPara("%s", opad, highlight, new String[]{laughablethreat});

		}


		if (!this.isAvailableToBuild() && (mode == IndustryTooltipMode.ADD_INDUSTRY || mode == IndustryTooltipMode.UPGRADE || mode == IndustryTooltipMode.DOWNGRADE)) {
			String reason = this.getUnavailableReason();
			if (reason != null) {
				tooltip.addPara(reason, bad, opad);
			}
		}

		boolean category = this.getSpec().hasTag("parent_item");
		if (!category) {
			int credits = (int)Global.getSector().getPlayerFleet().getCargo().getCredits().get();
			String creditsStr = Misc.getDGSCredits((float)credits);
			if (mode != IndustryTooltipMode.UPGRADE && mode != IndustryTooltipMode.ADD_INDUSTRY) {
				if (mode == IndustryTooltipMode.DOWNGRADE && this.getSpec().getUpgrade() != null) {
					float refundFraction = Global.getSettings().getFloat("industryRefundFraction");
					IndustrySpecAPI spec = Global.getSettings().getIndustrySpec(this.getSpec().getUpgrade());
					int cost = (int)(spec.getCost() * refundFraction);
					String refundStr = Misc.getDGSCredits((float)cost);
					tooltip.addPara("%s refunded for downgrade.", opad, highlight, new String[]{refundStr});
				}
			} else {
				int cost = (int)this.getBuildCost();
				String costStr = Misc.getDGSCredits((float)cost);
				int days = (int)this.getBuildTime();
				String daysStr = "days";
				if (days == 1) {
					daysStr = "day";
				}

				LabelAPI label = null;
				if (mode == IndustryTooltipMode.UPGRADE) {
					label = tooltip.addPara("%s and %s " + daysStr + " to upgrade. You have %s.", opad, highlight, new String[]{costStr, "" + days, creditsStr});
				} else {
					label = tooltip.addPara("%s and %s " + daysStr + " to build. You have %s.", opad, highlight, new String[]{costStr, "" + days, creditsStr});
				}

				label.setHighlight(new String[]{costStr, "" + days, creditsStr});
				if (credits >= cost) {
					label.setHighlightColors(new Color[]{highlight, highlight, highlight});
				} else {
					label.setHighlightColors(new Color[]{bad, highlight, highlight});
				}
			}

			this.addPostDescriptionSection(tooltip, mode);
			if (!this.getIncome().isUnmodified()) {
				int income = this.getIncome().getModifiedInt();
				tooltip.addPara("Monthly income: %s", opad, highlight, new String[]{Misc.getDGSCredits((float)income)});
				tooltip.addStatModGrid(300.0F, 65.0F, 10.0F, pad, this.getIncome(), true, new TooltipMakerAPI.StatModValueGetter() {
					public String getPercentValue(MutableStat.StatMod mod) {
						return null;
					}

					public String getMultValue(MutableStat.StatMod mod) {
						return null;
					}

					public Color getModColor(MutableStat.StatMod mod) {
						return null;
					}

					public String getFlatValue(MutableStat.StatMod mod) {
						return Misc.getWithDGS(mod.value) + "¢";
					}
				});
			}

			if (!this.getUpkeep().isUnmodified()) {
				int upkeep = this.getUpkeep().getModifiedInt();
				tooltip.addPara("Monthly upkeep: %s", opad, highlight, new String[]{Misc.getDGSCredits((float)upkeep)});
				tooltip.addStatModGrid(300.0F, 65.0F, 10.0F, pad, this.getUpkeep(), true, new TooltipMakerAPI.StatModValueGetter() {
					public String getPercentValue(MutableStat.StatMod mod) {
						return null;
					}

					public String getMultValue(MutableStat.StatMod mod) {
						return null;
					}

					public Color getModColor(MutableStat.StatMod mod) {
						return null;
					}

					public String getFlatValue(MutableStat.StatMod mod) {
						return Misc.getWithDGS(mod.value) + "¢";
					}
				});
			}

			this.addPostUpkeepSection(tooltip, mode);
			boolean hasSupply = false;

			for(MutableCommodityQuantity curr : this.supply.values()) {
				int qty = curr.getQuantity().getModifiedInt();
				if (qty > 0) {
					hasSupply = true;
					break;
				}
			}

			boolean hasDemand = false;

			for(MutableCommodityQuantity curr : this.demand.values()) {
				int qty = curr.getQuantity().getModifiedInt();
				if (qty > 0) {
					hasDemand = true;
					break;
				}
			}

			float maxIconsPerRow = 10.0F;
			if (hasSupply) {
				tooltip.addSectionHeading("Production", color, dark, Alignment.MID, opad);
				tooltip.beginIconGroup();
				tooltip.setIconSpacingMedium();
				float icons = 0.0F;

				for(MutableCommodityQuantity curr : this.supply.values()) {
					int qty = curr.getQuantity().getModifiedInt();
					if (qty > 0) {
						tooltip.addIcons(this.market.getCommodityData(curr.getCommodityId()), qty, IconRenderMode.NORMAL);
					}

					int plus = 0;
					int minus = 0;

					for(MutableStat.StatMod mod : curr.getQuantity().getFlatMods().values()) {
						if (mod.value > 0.0F) {
							plus += (int)mod.value;
						} else if (mod.desc != null && mod.desc.contains("shortage")) {
							minus += (int)Math.abs(mod.value);
						}
					}

					minus = Math.min(minus, plus);
					if (minus > 0 && mode == IndustryTooltipMode.NORMAL) {
						tooltip.addIcons(this.market.getCommodityData(curr.getCommodityId()), minus, IconRenderMode.DIM_RED);
					}

					icons += (float)(qty + Math.max(0, minus));
				}

				int rows = (int)Math.ceil((double)(icons / maxIconsPerRow));
				rows = 3;
				tooltip.addIconGroup(32.0F, rows, opad);
			}

			this.addPostSupplySection(tooltip, hasSupply, mode);
			if (hasDemand || this.hasPostDemandSection(hasDemand, mode)) {
				tooltip.addSectionHeading("Demand & effects", color, dark, Alignment.MID, opad);
			}

			if (hasDemand) {
				tooltip.beginIconGroup();
				tooltip.setIconSpacingMedium();
				float icons = 0.0F;

				for(MutableCommodityQuantity curr : this.demand.values()) {
					int qty = curr.getQuantity().getModifiedInt();
					if (qty > 0) {
						CommodityOnMarketAPI com = orig.getCommodityData(curr.getCommodityId());
						int available = com.getAvailable();
						int normal = Math.min(available, qty);
						int red = Math.max(0, qty - available);
						if (mode != IndustryTooltipMode.NORMAL) {
							normal = qty;
							red = 0;
						}

						if (normal > 0) {
							tooltip.addIcons(com, normal, IconRenderMode.NORMAL);
						}

						if (red > 0) {
							tooltip.addIcons(com, red, IconRenderMode.DIM_RED);
						}

						icons += (float)(normal + Math.max(0, red));
					}
				}

				int rows = (int)Math.ceil((double)(icons / maxIconsPerRow));
				rows = 3;
				rows = 1;
				tooltip.addIconGroup(32.0F, rows, opad);
			}

			this.addPostDemandSection(tooltip, hasDemand, mode);
			if (!needToAddIndustry) {
				this.addInstalledItemsSection(mode, tooltip, expanded);
				this.addImprovedSection(mode, tooltip, expanded);
				ListenerUtil.addToIndustryTooltip(this, mode, tooltip, this.getTooltipWidth(), expanded);
			}

			tooltip.addPara("*Shown production and demand values are already adjusted based on current market size and local conditions.", gray, opad);
		}

		if (needToAddIndustry) {
			this.unapply();
			this.market.getIndustries().remove(this);
		}

		this.market = orig;
		this.market.setRetainSuppressedConditionsSetWhenEmpty((Boolean)null);
		if (!needToAddIndustry) {
			this.reapply();
		}

	}

	@Override
	//Only functional with PK Device installed.
	public boolean isFunctional() {
		return super.isFunctional() && this.special != null && this.special.getId().equals("planetkiller");
	}

	@Override
	//Only available with bp (addspecial industry_bp jdp_pksilo)
	public boolean isAvailableToBuild() {
		if (!Global.getSector().getPlayerFaction().knowsIndustry(getId())) {
			return false;
		}
		//return market.getPlanetEntity() != null;
		return true;
	}
	public boolean showWhenUnavailable() {
		return Global.getSector().getPlayerFaction().knowsIndustry(getId());
	}

	//Modidiers
	@Override
	protected int getBaseStabilityMod() {
		return 3;
	}
	public static float IMPROVE_DEFENSE_BONUS = 0.5f;
	public static float MAX_BONUS_WHEN_UNMET_DEMAND = 0.25f;

	//Apply
	public void apply() {
		super.apply(true);

		//Modify Local Stability
		modifyStabilityWithBaseMod();

		//Demand and Production (Not really that important to the industry)
		int size = market.getSize();

		demand(Commodities.SUPPLIES, 1 + size);
		demand(Commodities.HAND_WEAPONS,  size - 1);
		demand(Commodities.MARINES, size);
		demand(Commodities.FUEL, 7);
		demand(Commodities.METALS,  5);
		demand(Commodities.RARE_METALS, 3);

        supply(Commodities.CREW, size);

		Pair<String, Integer> deficit = getMaxDeficit(Commodities.HAND_WEAPONS);
		applyDeficitToProduction(1, deficit, Commodities.MARINES);

		MemoryAPI memory = market.getMemoryWithoutUpdate();


		if (!isFunctional()) {
			supply.clear();
			unapply();
		}

		//Nonlocal effects.
		if (this.special != null && this.isFunctional()) {

			//Tag for dialogue
			Global.getSector().getMemoryWithoutUpdate().set("$jdp_has_interfector", true);
			Global.getSector().getMemoryWithoutUpdate().set("$jdp_upsetAlviss", true);


			Iterator var1 = Misc.getFactionMarkets(this.market.getFactionId()).iterator();
			//In theory unnecessary, just making sure
			while (true) {
				MarketAPI marketAPI;
				do {
					if (!var1.hasNext()) {
						return;
					}

					marketAPI = (MarketAPI) var1.next();
				} while (!marketAPI.getFactionId().equals(this.market.getFactionId()));
					if (this.market.hasCondition("jdp_pkinspirealpha") == false) {
						marketAPI.addCondition("jdp_pkinspire");
					}
			}
		}
	}

	//Unapply
	private void clearEffects() {
		Iterator var1 = Misc.getFactionMarkets(this.market.getFactionId()).iterator();
		//In theory unnecessary, just making sure
		while(true) {
			MarketAPI marketAPI;
			do {
				if (!var1.hasNext()) {
					return;
				}

				marketAPI = (MarketAPI)var1.next();
			} while(!marketAPI.getFactionId().equals(this.market.getFactionId()));

			marketAPI.removeCondition("jdp_pkinspire");
		}
	}
	@Override
	public void unapply() {
		super.unapply();

		//Tag for dialogue
		Global.getSector().getMemoryWithoutUpdate().set("$jdp_has_interfector", false);
		Global.getSector().getMemoryWithoutUpdate().set("$jdp_upsetAlviss", false);


		//Non-local effects
		this.clearEffects();

		MemoryAPI memory = market.getMemoryWithoutUpdate();

		//Local Bonus
		unmodifyStabilityWithBaseMod();
		market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(getModId());
	}

	@Override
	protected Pair<String, Integer> getStabilityAffectingDeficit() {
		return getMaxDeficit(Commodities.SUPPLIES, Commodities.FUEL, Commodities.HAND_WEAPONS);
	}

	//I'll make it legal...
	public boolean isDemandLegal(CommodityOnMarketAPI com) {
		return true;
	}
	public boolean isSupplyLegal(CommodityOnMarketAPI com) {
		return true;
	}

	protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
		return mode != IndustryTooltipMode.NORMAL || isFunctional();
	}

	//Raid danger
	@Override
	public RaidDangerLevel adjustCommodityDangerLevel(String commodityId, RaidDangerLevel level) {
		return level.next();
	}
	@Override
	public RaidDangerLevel adjustItemDangerLevel(String itemId, String data, RaidDangerLevel level) {
		return level.next();
	}

	@Override
	protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
		if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
			addStabilityPostDemandSection(tooltip, hasDemand, mode);
			Color h = Misc.getHighlightColor();
			float opad = 10f;

			float bonus = getImmigrationBonus();
			tooltip.addPara("Pragmatic individuals are currently seeking immigration to secure peace of mind, increasing population growth: %s", opad, h, "+" + Math.round(bonus));
			String terracide = "The instant and assured threat of Terracide presented by this installation will serve as potent leverage in our negotiations with other factions, likely forcing them to the table when they would otherwise refuse.";
			tooltip.addPara("%s", opad, h, new String[]{terracide});
		}
	}

	//Immigration Bonus
	public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
		if (isFunctional()) {
			incoming.add(Factions.NEUTRAL, getImmigrationBonus());
			incoming.getWeight().modifyFlat(getModId(), getImmigrationBonus(), getNameForModifier());
		}
	}

	//Apply Alpha Non-local
	@Override
	protected void applyAlphaCoreModifiers() {
		Iterator var1 = Misc.getFactionMarkets(this.market.getFactionId()).iterator();
		//In theory unnecessary, just making sure
		while (true) {
			MarketAPI marketAPI;
			do {
				if (!var1.hasNext()) {
					return;
				}

				marketAPI = (MarketAPI) var1.next();
			} while (!marketAPI.getFactionId().equals(this.market.getFactionId()));

			marketAPI.removeCondition("jdp_pkinspire");
			marketAPI.addCondition("jdp_pkinspirealpha");
		}
	}

	//Unapply Alpha Non-local
	@Override
	protected void applyNoAICoreModifiers() {
		Iterator var1 = Misc.getFactionMarkets(this.market.getFactionId()).iterator();
		//In theory unnecessary, just making sure
		while (true) {
			MarketAPI marketAPI;
			do {
				if (!var1.hasNext()) {
					return;
				}

				marketAPI = (MarketAPI) var1.next();
			} while (!marketAPI.getFactionId().equals(this.market.getFactionId()));
			marketAPI.removeCondition("jdp_pkinspirealpha");
		}
	}

	//Apply Alpha Local
	@Override
	protected void applyAlphaCoreSupplyAndDemandModifiers() {
		demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha core");
	}

	//Alpha Tooltip
	protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
		float opad = 10f;
		Color highlight = Misc.getHighlightColor();

		String pre = "Alpha-level AI core currently assigned. ";
		if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
			pre = "Alpha-level AI core. ";
		}

		if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
			CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
			TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
			text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
							"Manages and spreads propaganda, improving non-local effects.", 0f, highlight,
					"" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION);
			tooltip.addImageWithText(opad);
			return;
		}

		tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
						"Manages and spreads propaganda, improving non-local effects.", opad, highlight,
				"" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION);

	}

	//Improvement
	@Override
	public boolean canImprove() {
		return true;
	}
	protected void applyImproveModifiers() {
		if (isImproved()) {
			market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult("ground_defenses_improve",
					1f + IMPROVE_DEFENSE_BONUS,
					getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
		} else {
			market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult("ground_defenses_improve");
		}
	}

	public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
		float opad = 10f;
		Color highlight = Misc.getHighlightColor();

		float a = IMPROVE_DEFENSE_BONUS;
		String str = Strings.X + (1f + a) + "";

		if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
			info.addPara("Ground defenses increased by %s.", 0f, highlight, str);
		} else {
			info.addPara("Increases ground defenses by %s.", 0f, highlight, str);
		}

		info.addSpacer(opad);
		super.addImproveDesc(info, mode);
	}

	protected float getImmigrationBonus() {
		return getSizeMult() * 3f;
	}

}

