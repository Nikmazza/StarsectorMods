package data.scripts.campaign.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantOfficerGeneratorPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class II_StellaCastellum extends OrbitalStation {

    public static float DEFENSE_BONUS_CASTELLUM = 3f;

    @Override
    public void apply() {
        super.apply(false);

        int size = 9;

        modifyStabilityWithBaseMod();

        applyIncomeAndUpkeep(size);

        demand(Commodities.CREW, size);
        demand(Commodities.SUPPLIES, size);

        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(getModId(), 1f + DEFENSE_BONUS_CASTELLUM, getNameForModifier());

        matchCommanderToAICore(aiCoreId);

        if (!isFunctional()) {
            supply.clear();
            unapply();
        } else {
            applyCRToStation();
        }
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            Color h = Misc.getHighlightColor();
            float opad = 10f;

            float cr = getCR();
            tooltip.addPara("Station combat readiness: %s", opad, h, "" + Math.round(cr * 100f) + "%");

            addStabilityPostDemandSection(tooltip, hasDemand, mode);

            addGroundDefensesImpactSection(tooltip, DEFENSE_BONUS_CASTELLUM, Commodities.SUPPLIES);
        }
    }

    @Override
    protected int getBaseStabilityMod() {
        return 4;
    }

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    protected void matchCommanderToAICore(String aiCore) {
        if (stationFleet == null) {
            return;
        }

        PersonAPI commander = null;
        if (Commodities.ALPHA_CORE.equals(aiCore)) {
            if (market.getFactionId().contentEquals("interstellarimperium")) {
                ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
                commander = ip.getPerson("ii_minerva");
            } else {
                AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(Commodities.ALPHA_CORE);
                commander = plugin.createPerson(Commodities.ALPHA_CORE, Factions.REMNANTS, null);
                if (stationFleet.getFlagship() != null) {
                    RemnantOfficerGeneratorPlugin.integrateAndAdaptCoreForAIFleet(stationFleet.getFlagship());
                }
            }
        } else {
            if (stationFleet.getFlagship() != null) {
                int level = getHumanCommanderLevel();
                PersonAPI current = stationFleet.getFlagship().getCaptain();
                if (level > 0) {
                    if (current.isAICore() || current.getStats().getLevel() != level) {
                        commander = OfficerManagerEvent.createOfficer(
                                Global.getSector().getFaction(market.getFactionId()), level, true);
                    }
                } else {
                    if (stationFleet.getFlagship() == null || stationFleet.getFlagship().getCaptain() == null
                            || !stationFleet.getFlagship().getCaptain().isDefault()) {
                        commander = Global.getFactory().createPerson();
                    }
                }
            }

        }

        if (commander != null) {
            if (stationFleet.getFlagship() != null) {
                stationFleet.getFlagship().setCaptain(commander);
                stationFleet.getFlagship().setFlagship(false);
            }
        }
    }
}
