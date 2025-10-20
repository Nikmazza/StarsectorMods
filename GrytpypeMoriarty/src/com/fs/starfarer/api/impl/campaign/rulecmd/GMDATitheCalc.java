package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import data.scripts.util.GMDA_Util;
import java.util.List;
import java.util.Map;

import static com.fs.starfarer.api.impl.campaign.rulecmd.GMDAPickExtortionMethod.extortionAmount;


public class GMDATitheCalc extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        }

        CampaignFleetAPI fleet;
        if (dialog.getInteractionTarget() instanceof CampaignFleetAPI) {
            fleet = (CampaignFleetAPI) dialog.getInteractionTarget();
        } else {
            return false;
        }

        float credits = Global.getSector().getPlayerFleet().getCargo().getCredits().get();

        double tithe = (extortionAmount(credits) * 0.1);

        float powerLevel = GMDA_Util.calculatePowerLevel(fleet);
        tithe = Math.min(tithe, powerLevel * 2000.0);
        long realTithe = GMDA_Util.roundToSignificantFiguresLong(tithe, 3);

        float repImpact;
        if (realTithe <= 1250f) {
            repImpact = 0.01f;
        } else if (realTithe <= 2500f) {
            repImpact = 0.02f;
        } else if (realTithe <= 5000f) {
            repImpact = 0.03f;
        } else if (realTithe <= 10000f) {
            repImpact = 0.04f;
        } else if (realTithe <= 20000f) {
            repImpact = 0.05f;
        } else if (realTithe <= 40000f) {
            repImpact = 0.06f;
        } else if (realTithe <= 80000f) {
            repImpact = 0.07f;
        } else if (realTithe <= 160000f) {
            repImpact = 0.08f;
        } else if (realTithe <= 320000f) {
            repImpact = 0.09f;
        } else {
            repImpact = 0.10f;
        }
        float repNegImpact = repImpact;
        switch (Global.getSector().getFaction("gmda").getRelToPlayer().getLevel()) {
            default:
            case VENGEFUL:
                repImpact = repImpact * 1.5f;
                repNegImpact = repNegImpact / 1.5f;
                break;
            case HOSTILE:
                break;
            case INHOSPITABLE:
            case SUSPICIOUS:
                repImpact = Math.max(0.01f, repImpact - 0.01f);
                repNegImpact = repNegImpact + 0.01f;
                break;
            case NEUTRAL:
                repImpact = Math.max(0.01f, repImpact - 0.02f);
                repNegImpact = repNegImpact + 0.02f;
                break;
            case FAVORABLE:
                repImpact = Math.max(0.01f, repImpact - 0.03f);
                repNegImpact = repNegImpact + 0.03f;
                break;
            case WELCOMING:
                repImpact = Math.max(0.01f, repImpact - 0.04f);
                repNegImpact = repNegImpact + 0.04f;
                break;
            case FRIENDLY:
            case COOPERATIVE:
                repImpact = Math.max(0.01f, repImpact - 0.05f);
                repNegImpact = repNegImpact + 0.05f;
                break;
        }

        memoryMap.get(MemKeys.LOCAL).set("$GMDA_tithe_string", Misc.getWithDGS(realTithe), 0);
        memoryMap.get(MemKeys.LOCAL).set("$GMDA_tithe", realTithe, 0);
        memoryMap.get(MemKeys.LOCAL).set("$GMDA_repImpact", repImpact, 0);
        memoryMap.get(MemKeys.LOCAL).set("$GMDA_repNegImpact", -repNegImpact, 0);
        return true;
    }
}
