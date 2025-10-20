package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GMDANormalTalk extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        }

        float credits = Global.getSector().getPlayerFleet().getCargo().getCredits().get();

        MonthlyReport report = SharedData.getData().getCurrentReport();
        report.computeTotals();
        float profit = Math.max(0f, report.getRoot().totalIncome - report.getRoot().totalUpkeep);
        float netWorth = credits + profit;
        return true;
    }
}
