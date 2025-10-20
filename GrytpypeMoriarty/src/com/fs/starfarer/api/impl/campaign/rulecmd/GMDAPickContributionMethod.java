package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.scripts.util.GMDA_Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;

/**
 * CabalPickContributionMethod
 */
public class GMDAPickContributionMethod extends BaseCommandPlugin {

    public static Logger log = Global.getLogger(GMDAPickContributionMethod.class);

    public static boolean playerHasAbilityToPayContribution(CampaignFleetAPI fleet) {
        List<String> extortionMethodsGMDA = new ArrayList<>(2);

        float powerLevel = GMDA_Util.calculatePowerLevel(fleet);

        float credits = Global.getSector().getPlayerFleet().getCargo().getCredits().get();
        float lowerThreshold = Math.min(100000f, powerLevel * 600f);
        if (credits > lowerThreshold) {
            extortionMethodsGMDA.add("GMDAtithe");
        }
        return !extortionMethodsGMDA.isEmpty();
    }

    public static float playerNetWorth(CampaignFleetAPI fleet) {
        float credits = Global.getSector().getPlayerFleet().getCargo().getCredits().get();

        MonthlyReport report = SharedData.getData().getCurrentReport();
        report.computeTotals();
        float profit = Math.max(0f, report.getRoot().totalIncome - report.getRoot().totalUpkeep);

        return credits + profit;
    }

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params,
            Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        }

        CampaignFleetAPI fleet;
        if (dialog.getInteractionTarget() instanceof CampaignFleetAPI) {
            fleet = (CampaignFleetAPI) dialog.getInteractionTarget();
        } else {
            return false;
        }

        WeightedRandomPicker<String> extortionMethods = new WeightedRandomPicker<>();
        float credits = Global.getSector().getPlayerFleet().getCargo().getCredits().get();

        MonthlyReport report = SharedData.getData().getCurrentReport();
        report.computeTotals();
        float profit = Math.max(0f, report.getRoot().totalIncome - report.getRoot().totalUpkeep);

        float netWorth = playerNetWorth(fleet);
        float targetExtortion = (float) GMDAPickExtortionMethod.extortionAmount(netWorth);

        float powerLevel = GMDA_Util.calculatePowerLevel(fleet);

        log.info("Seen credit value of " + credits);
        log.info("Seen monthly profit of " + profit);
        log.info("Evaluated net worth at " + netWorth);
        log.info("Targeting extortion value at " + targetExtortion);
        log.info("Evaluated player power level of " + powerLevel);

        float lowerThreshold = Math.min(100000f, powerLevel * 600f);
        float upperThreshold = powerLevel * 3500f;
        if (credits > lowerThreshold) {
            float weight = (float) Math.sqrt(Math.min(credits, upperThreshold) / targetExtortion) * 1.5f;
            extortionMethods.add("GMDAtithe", weight);
            log.info("Tithe extortion method at weight " + weight);
        }


        if (extortionMethods.isEmpty()) {
            memoryMap.get(MemKeys.LOCAL).set("$GMDA_extortionMethod", "GMDAnone", 0);
            return false;
        }

        memoryMap.get(MemKeys.LOCAL).set("$GMDA_extortionMethod", extortionMethods.pick(), 7);
        memoryMap.get(MemKeys.LOCAL).set("$GMDA_netWorthString", Misc.getDGSCredits(
                GMDA_Util.roundToSignificantFiguresLong(netWorth, 4)), 0);
        return true;
    }
}
