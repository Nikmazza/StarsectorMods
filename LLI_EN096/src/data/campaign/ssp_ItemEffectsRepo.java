package data.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.*;
import com.fs.starfarer.api.impl.campaign.econ.ResourceDepositsCondition;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseInstallableItemEffect;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;


import java.util.*;
import java.util.List;

public class ssp_ItemEffectsRepo {
	public static List<String> ssp_MANTLE_BORE_COMMODITIES = new ArrayList<String>();
	static {
		ssp_MANTLE_BORE_COMMODITIES.add(Commodities.ORE);
		ssp_MANTLE_BORE_COMMODITIES.add(Commodities.RARE_ORE);
		ssp_MANTLE_BORE_COMMODITIES.add(Commodities.ORGANICS);
	}

	public static void add_item_effects(){
		//地幔钻机(D)
		ItemEffectsRepo.ITEM_EFFECTS.put("ssp_mantle_bore", new BaseInstallableItemEffect("ssp_mantle_bore") {
			protected Set<String> getAffectedCommodities(Industry industry) {
				MarketAPI market = industry.getMarket();

				Set<String> result = new LinkedHashSet<String>();
				for (MarketConditionAPI mc : market.getConditions()) {
					String cid = mc.getId();
					String commodity = ResourceDepositsCondition.COMMODITY.get(cid);
					for (String curr : ssp_MANTLE_BORE_COMMODITIES) {
						if (curr.equals(commodity)) {
							result.add(curr);
						}
					}
				}
				return result;
			}

			public void apply(Industry industry) {
				if (industry instanceof BaseIndustry) {
					BaseIndustry b = (BaseIndustry) industry;
					Set<String> list = getAffectedCommodities(industry);

					if (!list.isEmpty()) {
						for (String curr : list) {
							b.supply(spec.getId(), curr, 1, Misc.ucFirst(spec.getName().toLowerCase()));
						}
					}
				}
			}

			public void unapply(Industry industry) {
				if (industry instanceof BaseIndustry) {
					BaseIndustry b = (BaseIndustry) industry;
					for (String curr : ssp_MANTLE_BORE_COMMODITIES) {
						b.supply(spec.getId(), curr, 0, null);
					}
				}
			}

			protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
												  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
				List<String> commodities = new ArrayList<String>();
				for (String curr : ssp_MANTLE_BORE_COMMODITIES) {
					CommoditySpecAPI c = Global.getSettings().getCommoditySpec(curr);
					commodities.add(c.getName().toLowerCase());
				}
				text.addPara(pre + Misc.getAndJoined(commodities) + SSPI18nUtil.getString("industry","ssp_mantle_bore"),
						pad, Misc.getHighlightColor(),
						"" + 1);
			}

			@Override
			public String[] getSimpleReqs(Industry industry) {
				return new String[]{ItemEffectsRepo.NOT_A_GAS_GIANT, ItemEffectsRepo.NOT_HABITABLE};
			}
		});
		//迷你寒冰圣地
		ItemEffectsRepo.ITEM_EFFECTS.put("ssp_cryosanctum", new BaseInstallableItemEffect("ssp_cryosanctum") {
			public void apply(Industry industry) {
				if (industry instanceof BaseIndustry) {
					BaseIndustry b = (BaseIndustry) industry;

					if(b.getId().equals("megaport") || b.getId().equals("spaceport")){
					b.supply(spec.getId(),Commodities.ORGANS,2, Misc.ucFirst(spec.getName().toLowerCase()));
					b.supply(spec.getId(),Commodities.CREW,2,Misc.ucFirst(spec.getName().toLowerCase()));
					}else if(b.getId().equals("patrolhq") ||b.getId().equals("militarybase")||b.getId().equals("highcommand")){
						b.supply(spec.getId(),Commodities.ORGANS,1, Misc.ucFirst(spec.getName().toLowerCase()));
						b.supply(spec.getId(),Commodities.MARINES,1,Misc.ucFirst(spec.getName().toLowerCase()));
					}
				}
			}

			public void unapply(Industry industry) {
				if (industry instanceof BaseIndustry) {
					BaseIndustry b = (BaseIndustry) industry;
					b.supply(spec.getId(),Commodities.ORGANS, 0, null);
					b.supply(spec.getId(),Commodities.CREW,0,null);
					b.supply(spec.getId(),Commodities.MARINES,0,null);
				}
			}

			protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
												  InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {
				List<String> ind_0 = new ArrayList<String>();
				ind_0.add(Global.getSettings().getIndustrySpec("patrolhq").getName());
				ind_0.add(Global.getSettings().getIndustrySpec("militarybase").getName());
				ind_0.add(Global.getSettings().getIndustrySpec("highcommand").getName());
				List<String> commodities_0 = new ArrayList<String>();
				commodities_0.add(Global.getSettings().getCommoditySpec("organs").getName());
				commodities_0.add(Global.getSettings().getCommoditySpec("marines").getName());
				List<String> ind_1 = new ArrayList<String>();
				ind_1.add(Global.getSettings().getIndustrySpec("spaceport").getName());
				ind_1.add(Global.getSettings().getIndustrySpec("megaport").getName());
				List<String> commodities_1 = new ArrayList<String>();
				commodities_1.add(Global.getSettings().getCommoditySpec("organs").getName());
				commodities_1.add(Global.getSettings().getCommoditySpec("crew").getName());

				text.addPara(pre + SSPI18nUtil.getString("industry","ssp_cryosanctum"),
						pad, Misc.getHighlightColor(),
						Misc.getAndJoined(ind_0),""+1,Misc.getAndJoined(commodities_0),Misc.getAndJoined(ind_1),""+2,Misc.getAndJoined(commodities_1));
			}

		});
	}
}




