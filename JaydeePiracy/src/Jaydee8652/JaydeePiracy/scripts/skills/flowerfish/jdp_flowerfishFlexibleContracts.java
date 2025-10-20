package Jaydee8652.JaydeePiracy.scripts.skills.flowerfish;


import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.econ.Market;
import com.fs.starfarer.rpg.Person;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;
import org.magiclib.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class jdp_flowerfishFlexibleContracts extends SCBaseSkillPlugin{

    @Override
    public String getAffectsString() {
        return "fleet";
    }

    @Override
    public void addTooltip(SCData data, TooltipMakerAPI tooltip) {
        tooltip.addPara("You are more likely to find officers and mercenary officers on markets", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltip.addSpacer(10f);

        tooltip.addPara("\"Welcome to the Flowerfish Mercenary Network, don't forget to smile.\"", 0f, Misc.getTextColor(), Misc.getHighlightColor());
        tooltip.addPara("  -Flowerfish \"Local Resource\" Initiative", 0f, jdp_AptitudeFlowerfish.JDP_FLOWERFISH_COLOUR, Misc.getHighlightColor());
        tooltip.addSpacer(10f);
    }

    @Override
    public void advance(SCData data, Float amount) {}

    @Override
    public void onActivation(SCData data) {
        if (data.isPlayer() && !Global.getSector().hasScript(jdp_flowerfishFlexibleContractsScript.class)) {
            Global.getSector().addScript(new jdp_flowerfishFlexibleContractsScript());
        }
    }

    @Override
    public void onDeactivation(SCData data) {
        if (data.isPlayer()) {
            jdp_flowerfishFlexibleContractsScript script = null;
            for (EveryFrameScript s : Global.getSector().getScripts()) {
                if (s instanceof jdp_flowerfishFlexibleContractsScript) {
                    script = (jdp_flowerfishFlexibleContractsScript) s;
                    break;
                }
            }
            if (script != null) {
                Global.getSector().removeScript(script);

                for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
                    market.getStats().getDynamic().getMod(Stats.OFFICER_PROB_MOD).unmodify("jdp_flowerfishFlexibleContracts");
                    market.getStats().getDynamic().getMod(Stats.OFFICER_ADDITIONAL_PROB_MULT_MOD).unmodify("jdp_flowerfishFlexibleContracts");
                    market.getStats().getDynamic().getMod(Stats.OFFICER_IS_MERC_PROB_MOD).unmodify("jdp_flowerfishFlexibleContracts");

                }
            }
        }
    }
    public static class jdp_flowerfishFlexibleContractsScript implements EveryFrameScript {

        private IntervalUtil interval = new IntervalUtil(2f, 3f);

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean runWhilePaused() {
            return true;
        }

        @Override
        public void advance(float amount) {
            interval.advance(amount);

            if (interval.intervalElapsed()) {
                List<MarketAPI> markets = Global.getSector().getEconomy().getMarketsCopy();
                for (MarketAPI market : markets) {
                    market.getStats().getDynamic().getMod(Stats.OFFICER_PROB_MOD).modifyFlat("jdp_flowerfishFlexibleContracts", 0.1f);
                    market.getStats().getDynamic().getMod(Stats.OFFICER_ADDITIONAL_PROB_MULT_MOD).modifyFlat("jdp_flowerfishFlexibleContracts", 0.1f);
                    market.getStats().getDynamic().getMod(Stats.OFFICER_IS_MERC_PROB_MOD).modifyFlat("jdp_flowerfishFlexibleContracts", 0.1f);

                    market.getStats().getDynamic().getMod(Stats.OFFICER_PROB_MOD).modifyMult("jdp_flowerfishFlexibleContracts", 1.5f);
                    market.getStats().getDynamic().getMod(Stats.OFFICER_ADDITIONAL_PROB_MULT_MOD).modifyMult("jdp_flowerfishFlexibleContracts", 1.2f);
                    market.getStats().getDynamic().getMod(Stats.OFFICER_IS_MERC_PROB_MOD).modifyFlat("jdp_flowerfishFlexibleContracts", 1.2f);
                }
            }
        }
    }
}
