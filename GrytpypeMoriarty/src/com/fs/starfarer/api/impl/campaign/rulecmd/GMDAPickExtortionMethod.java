package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.scripts.util.GMDA_Util;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


public class GMDAPickExtortionMethod extends BaseCommandPlugin {

    public static Logger log = Global.getLogger(GMDAPickExtortionMethod.class);

    public static double extortionAmount(float credits) {
        double GMDAtithe = 0.0;
        double prevBracket = 0.0;
        double bracket = 20000.0;
        double incrementalDiv = 2.0;
        double tithePerBracket = 10000.0; // Each bracket increases the tithe by 10000 credits
        int limit = 1000;
        do {
            GMDAtithe += Math.min(bracket, credits - prevBracket) / incrementalDiv;
            prevBracket = bracket;
            incrementalDiv += 1.0;
            bracket += incrementalDiv * tithePerBracket;
            limit--;
        } while (credits > bracket && limit > 0);
        return GMDAtithe;
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

        float netWorth = GMDAPickContributionMethod.playerNetWorth(fleet);
        float targetExtortion = (float) GMDAPickExtortionMethod.extortionAmount(netWorth);

        float powerLevel = GMDA_Util.calculatePowerLevel(fleet);

        float lowerThreshold = Math.min(200000f, powerLevel * 1500f);
        float upperThreshold = powerLevel * 3500f;
        if (credits > lowerThreshold) {
            float weight = (float) Math.sqrt(Math.min(credits, upperThreshold) / targetExtortion);
            extortionMethods.add("GMDAtithe", weight);
            log.info("GMDAtithe extortion method at weight " + weight);
        }
		
        if (extortionMethods.isEmpty()) {
            memoryMap.get(MemKeys.LOCAL).set("$GMDA_extortionMethod", "none", 0);
            return false;
        }

        memoryMap.get(MemKeys.LOCAL).set("$GMDA_extortionMethod", extortionMethods.pick(), 7);
        memoryMap.get(MemKeys.LOCAL).set("$GMDA_netWorthString", Misc.getDGSCredits(
                GMDA_Util.roundToSignificantFiguresLong(netWorth, 4)), 0);
        return true;
    }
}
