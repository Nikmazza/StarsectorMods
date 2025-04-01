package data.ssp_UNGP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseOneTimeFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.ht.HyperspaceTopographyEventIntel;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;
import ungp.scripts.campaign.specialist.rules.UNGP_RulesManager;

public class ssp_HyperspaceTopography extends UNGP_BaseRuleEffect {
    public ssp_HyperspaceTopography() {
    }
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return 0.0F;
    }

    public void applyGlobalStats() {
        if (!Global.getSector().getPersistentData().containsKey(this.buffID)) {
            HyperspaceTopographyEventIntel.addFactorCreateIfNecessary(new ssp_HyperspaceTopographyFactor(700),null);
            Global.getSector().getPersistentData().put(this.buffID, true);
        }

    }
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return index == 0 ? "700" : null;
    }
    public class ssp_HyperspaceTopographyFactor extends BaseOneTimeFactor {
        public ssp_HyperspaceTopographyFactor(int points) {
            super(points);
        }
        @Override
        public String getDesc(BaseEventIntel intel) {
            return UNGP_RulesManager.URule.getByID("ssp_HyperspaceTopography").getExtra1();
        }
    }
}

